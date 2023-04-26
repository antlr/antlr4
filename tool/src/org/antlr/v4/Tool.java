/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.v4.analysis.AnalysisPipeline;
import org.antlr.v4.automata.ATNFactory;
import org.antlr.v4.automata.LexerATNFactory;
import org.antlr.v4.automata.ParserATNFactory;
import org.antlr.v4.codegen.CodeGenPipeline;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.misc.Graph;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.parse.GrammarASTAdaptor;
import org.antlr.v4.parse.GrammarTreeVisitor;
import org.antlr.v4.parse.ToolANTLRLexer;
import org.antlr.v4.parse.ToolANTLRParser;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.misc.LogManager;
import org.antlr.v4.runtime.misc.IntegerList;
import org.antlr.v4.runtime.atn.ATNSerializer;
import org.antlr.v4.semantics.SemanticPipeline;
import org.antlr.v4.tool.ANTLRMessage;
import org.antlr.v4.tool.ANTLRToolListener;
import org.antlr.v4.tool.BuildDependencyGenerator;
import org.antlr.v4.tool.DOTGenerator;
import org.antlr.v4.tool.DefaultToolListener;
import org.antlr.v4.tool.ErrorManager;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.GrammarTransformPipeline;
import org.antlr.v4.tool.LexerGrammar;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.GrammarASTErrorNode;
import org.antlr.v4.tool.ast.GrammarRootAST;
import org.antlr.v4.tool.ast.RuleAST;
import org.antlr.v4.tool.ast.TerminalAST;
import org.stringtemplate.v4.STGroup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Tool {
	public static final String VERSION;
	static {
		// Assigned in a static{} block to prevent the field from becoming a
		// compile-time constant
		VERSION = RuntimeMetaData.VERSION;
	}

	public static final String GRAMMAR_EXTENSION = ".g4";
	public static final String LEGACY_GRAMMAR_EXTENSION = ".g";

	public static final List<String> ALL_GRAMMAR_EXTENSIONS =
		Collections.unmodifiableList(Arrays.asList(GRAMMAR_EXTENSION, LEGACY_GRAMMAR_EXTENSION));

	public static enum OptionArgType { NONE, STRING } // NONE implies boolean
	public static class Option {
		String fieldName;
		String name;
		OptionArgType argType;
		String description;

		public Option(String fieldName, String name, String description) {
			this(fieldName, name, OptionArgType.NONE, description);
		}

		public Option(String fieldName, String name, OptionArgType argType, String description) {
			this.fieldName = fieldName;
			this.name = name;
			this.argType = argType;
			this.description = description;
		}
	}

	// fields set by option manager

	public File inputDirectory; // used by mvn plugin but not set by tool itself.
	public String outputDirectory;
	public String libDirectory;
	public boolean generate_ATN_dot = false;
	public String grammarEncoding = null; // use default locale's encoding
	public String msgFormat = "antlr";
	public boolean launch_ST_inspector = false;
	public boolean ST_inspector_wait_for_close = false;
    public boolean force_atn = false;
    public boolean log = false;
	public boolean gen_listener = true;
	public boolean gen_visitor = false;
	public boolean gen_dependencies = false;
	public String genPackage = null;
	public Map<String, String> grammarOptions = null;
	public boolean warnings_are_errors = false;
	public boolean longMessages = false;
	public boolean exact_output_dir = false;

    public final static Option[] optionDefs = {
		new Option("outputDirectory",             "-o", OptionArgType.STRING, "specify output directory where all output is generated"),
		new Option("libDirectory",                "-lib", OptionArgType.STRING, "specify location of grammars, tokens files"),
		new Option("generate_ATN_dot",            "-atn", "generate rule augmented transition network diagrams"),
		new Option("grammarEncoding",             "-encoding", OptionArgType.STRING, "specify grammar file encoding; e.g., euc-jp"),
		new Option("msgFormat",                   "-message-format", OptionArgType.STRING, "specify output style for messages in antlr, gnu, vs2005"),
		new Option("longMessages",                "-long-messages", "show exception details when available for errors and warnings"),
		new Option("gen_listener",                "-listener", "generate parse tree listener (default)"),
		new Option("gen_listener",                "-no-listener", "don't generate parse tree listener"),
		new Option("gen_visitor",                 "-visitor", "generate parse tree visitor"),
		new Option("gen_visitor",                 "-no-visitor", "don't generate parse tree visitor (default)"),
		new Option("genPackage",                  "-package", OptionArgType.STRING, "specify a package/namespace for the generated code"),
		new Option("gen_dependencies",            "-depend", "generate file dependencies"),
		new Option("",                            "-D<option>=value", "set/override a grammar-level option"),
		new Option("warnings_are_errors",         "-Werror", "treat warnings as errors"),
		new Option("launch_ST_inspector",         "-XdbgST", "launch StringTemplate visualizer on generated code"),
		new Option("ST_inspector_wait_for_close", "-XdbgSTWait", "wait for STViz to close before continuing"),
		new Option("force_atn",                   "-Xforce-atn", "use the ATN simulator for all predictions"),
		new Option("log",                         "-Xlog", "dump lots of logging info to antlr-timestamp.log"),
	    new Option("exact_output_dir",            "-Xexact-output-dir", "all output goes into -o dir regardless of paths/package"),
	};

	// helper vars for option management
	protected boolean haveOutputDir = false;
	protected boolean return_dont_exit = false;


	public final String[] args;

	protected List<String> grammarFiles = new ArrayList<String>();

	public ErrorManager errMgr;
    public LogManager logMgr = new LogManager();

	List<ANTLRToolListener> listeners = new CopyOnWriteArrayList<ANTLRToolListener>();

	/** Track separately so if someone adds a listener, it's the only one
	 *  instead of it and the default stderr listener.
	 */
	DefaultToolListener defaultListener = new DefaultToolListener(this);

	public static void main(String[] args) {
        Tool antlr = new Tool(args);
        if ( args.length == 0 ) { antlr.help(); antlr.exit(0); }

        try {
            antlr.processGrammarsOnCommandLine();
        }
        finally {
            if ( antlr.log ) {
                try {
                    String logname = antlr.logMgr.save();
                    System.out.println("wrote "+logname);
                }
                catch (IOException ioe) {
                    antlr.errMgr.toolError(ErrorType.INTERNAL_ERROR, ioe);
                }
            }
        }
		if ( antlr.return_dont_exit ) return;

		if (antlr.errMgr.getNumErrors() > 0) {
			antlr.exit(1);
		}
		antlr.exit(0);
	}

	public Tool() { this(null); }

	public Tool(String[] args) {
		this.args = args;
		errMgr = new ErrorManager(this);
		// We have to use the default message format until we have
		// parsed the -message-format command line option.
		errMgr.setFormat("antlr");
		handleArgs();
		errMgr.setFormat(msgFormat);
	}

	protected void handleArgs() {
		int i=0;
		while ( args!=null && i<args.length ) {
			String arg = args[i];
			i++;
			if ( arg.startsWith("-D") ) { // -Dlanguage=Java syntax
				handleOptionSetArg(arg);
				continue;
			}
			if ( arg.charAt(0)!='-' ) { // file name
				if ( !grammarFiles.contains(arg) ) grammarFiles.add(arg);
				continue;
			}
			boolean found = false;
			for (Option o : optionDefs) {
				if ( arg.equals(o.name) ) {
					found = true;
					String argValue = null;
					if ( o.argType==OptionArgType.STRING ) {
						argValue = args[i];
						i++;
					}
					// use reflection to set field
					Class<? extends Tool> c = this.getClass();
					try {
						Field f = c.getField(o.fieldName);
						if ( argValue==null ) {
							if ( arg.startsWith("-no-") ) f.setBoolean(this, false);
							else f.setBoolean(this, true);
						}
						else f.set(this, argValue);
					}
					catch (Exception e) {
						errMgr.toolError(ErrorType.INTERNAL_ERROR, "can't access field "+o.fieldName);
					}
				}
			}
			if ( !found ) {
				errMgr.toolError(ErrorType.INVALID_CMDLINE_ARG, arg);
			}
		}
		if ( outputDirectory!=null ) {
			if (outputDirectory.endsWith("/") ||
				outputDirectory.endsWith("\\")) {
				outputDirectory =
					outputDirectory.substring(0, outputDirectory.length() - 1);
			}
			File outDir = new File(outputDirectory);
			haveOutputDir = true;
			if (outDir.exists() && !outDir.isDirectory()) {
				errMgr.toolError(ErrorType.OUTPUT_DIR_IS_FILE, outputDirectory);
				outputDirectory = ".";
			}
		}
		else {
			outputDirectory = ".";
		}
		if ( libDirectory!=null ) {
			if (libDirectory.endsWith("/") ||
				libDirectory.endsWith("\\")) {
				libDirectory = libDirectory.substring(0, libDirectory.length() - 1);
			}
			File outDir = new File(libDirectory);
			if (!outDir.exists()) {
				errMgr.toolError(ErrorType.DIR_NOT_FOUND, libDirectory);
				libDirectory = ".";
			}
		}
		else {
			libDirectory = ".";
		}
		if ( launch_ST_inspector ) {
			STGroup.trackCreationEvents = true;
			return_dont_exit = true;
		}
	}

	protected void handleOptionSetArg(String arg) {
		int eq = arg.indexOf('=');
		if ( eq>0 && arg.length()>3 ) {
			String option = arg.substring("-D".length(), eq);
			String value = arg.substring(eq+1);
			if ( value.length()==0 ) {
				errMgr.toolError(ErrorType.BAD_OPTION_SET_SYNTAX, arg);
				return;
			}
			if ( Grammar.parserOptions.contains(option) ||
				 Grammar.lexerOptions.contains(option) )
			{
				if ( grammarOptions==null ) grammarOptions = new HashMap<String, String>();
				grammarOptions.put(option, value);
			}
			else {
				errMgr.grammarError(ErrorType.ILLEGAL_OPTION,
									null,
									null,
									option);
			}
		}
		else {
			errMgr.toolError(ErrorType.BAD_OPTION_SET_SYNTAX, arg);
		}
	}

	public void processGrammarsOnCommandLine() {
		List<GrammarRootAST> sortedGrammars = sortGrammarByTokenVocab(grammarFiles);

		for (GrammarRootAST t : sortedGrammars) {
			final Grammar g = createGrammar(t);
			g.fileName = t.fileName;
			if ( gen_dependencies ) {
				BuildDependencyGenerator dep =
					new BuildDependencyGenerator(this, g);
				/*
					List outputFiles = dep.getGeneratedFileList();
					List dependents = dep.getDependenciesFileList();
					System.out.println("output: "+outputFiles);
					System.out.println("dependents: "+dependents);
					 */
				System.out.println(dep.getDependencies().render());

			}
			else if (errMgr.getNumErrors() == 0) {
				process(g, true);
			}
		}
	}

	/** To process a grammar, we load all of its imported grammars into
		subordinate grammar objects. Then we merge the imported rules
		into the root grammar. If a root grammar is a combined grammar,
		we have to extract the implicit lexer. Once all this is done, we
		process the lexer first, if present, and then the parser grammar
	 */
	public void process(Grammar g, boolean gencode) {
		g.loadImportedGrammars();

		GrammarTransformPipeline transform = new GrammarTransformPipeline(g, this);
		transform.process();

		LexerGrammar lexerg;
		GrammarRootAST lexerAST;
		if ( g.ast!=null && g.ast.grammarType== ANTLRParser.COMBINED &&
			 !g.ast.hasErrors )
		{
			lexerAST = transform.extractImplicitLexer(g); // alters g.ast
			if ( lexerAST!=null ) {
				if (grammarOptions != null) {
					lexerAST.cmdLineOptions = grammarOptions;
				}

				lexerg = new LexerGrammar(this, lexerAST);
				lexerg.fileName = g.fileName;
				lexerg.originalGrammar = g;
				g.implicitLexer = lexerg;
				lexerg.implicitLexerOwner = g;
				processNonCombinedGrammar(lexerg, gencode);
//				System.out.println("lexer tokens="+lexerg.tokenNameToTypeMap);
//				System.out.println("lexer strings="+lexerg.stringLiteralToTypeMap);
			}
		}
		if ( g.implicitLexer!=null ) g.importVocab(g.implicitLexer);
//		System.out.println("tokens="+g.tokenNameToTypeMap);
//		System.out.println("strings="+g.stringLiteralToTypeMap);
		processNonCombinedGrammar(g, gencode);
	}

	public void processNonCombinedGrammar(Grammar g, boolean gencode) {
		if ( g.ast==null || g.ast.hasErrors ) return;

		boolean ruleFail = checkForRuleIssues(g);
		if ( ruleFail ) return;

		int prevErrors = errMgr.getNumErrors();
		// MAKE SURE GRAMMAR IS SEMANTICALLY CORRECT (FILL IN GRAMMAR OBJECT)
		SemanticPipeline sem = new SemanticPipeline(g);
		sem.process();

		if ( errMgr.getNumErrors()>prevErrors ) return;

		CodeGenerator codeGenerator = CodeGenerator.create(g);
		if (codeGenerator == null) {
			return;
		}

		// BUILD ATN FROM AST
		ATNFactory factory;
		if ( g.isLexer() ) factory = new LexerATNFactory((LexerGrammar)g, codeGenerator);
		else factory = new ParserATNFactory(g);
		g.atn = factory.createATN();

		if ( generate_ATN_dot ) generateATNs(g);

		if (gencode && g.tool.getNumErrors()==0 ) {
			String interpFile = generateInterpreterData(g);
			try (Writer fw = getOutputFileWriter(g, g.name + ".interp")) {
				fw.write(interpFile);
			}
			catch (IOException ioe) {
				errMgr.toolError(ErrorType.CANNOT_WRITE_FILE, ioe);
			}
		}

		// PERFORM GRAMMAR ANALYSIS ON ATN: BUILD DECISION DFAs
		AnalysisPipeline anal = new AnalysisPipeline(g);
		anal.process();

		//if ( generate_DFA_dot ) generateDFAs(g);

		if ( g.tool.getNumErrors()>prevErrors ) return;

		// GENERATE CODE
		if ( gencode ) {
			CodeGenPipeline gen = new CodeGenPipeline(g, codeGenerator);
			gen.process();
		}
	}

	/**
	 * Important enough to avoid multiple definitions that we do very early,
	 * right after AST construction. Also check for undefined rules in
	 * parser/lexer to avoid exceptions later. Return true if we find multiple
	 * definitions of the same rule or a reference to an undefined rule or
	 * parser rule ref in lexer rule.
	 */
	public boolean checkForRuleIssues(final Grammar g) {
		// check for redefined rules
		GrammarAST RULES = (GrammarAST)g.ast.getFirstChildWithType(ANTLRParser.RULES);
		List<GrammarAST> rules = new ArrayList<GrammarAST>(RULES.getAllChildrenWithType(ANTLRParser.RULE));
		for (GrammarAST mode : g.ast.getAllChildrenWithType(ANTLRParser.MODE)) {
			rules.addAll(mode.getAllChildrenWithType(ANTLRParser.RULE));
		}

		boolean redefinition = false;
		final Map<String, RuleAST> ruleToAST = new HashMap<String, RuleAST>();
		for (GrammarAST r : rules) {
			RuleAST ruleAST = (RuleAST)r;
			GrammarAST ID = (GrammarAST)ruleAST.getChild(0);
			String ruleName = ID.getText();
			RuleAST prev = ruleToAST.get(ruleName);
			if ( prev !=null ) {
				GrammarAST prevChild = (GrammarAST)prev.getChild(0);
				g.tool.errMgr.grammarError(ErrorType.RULE_REDEFINITION,
										   g.fileName,
										   ID.getToken(),
										   ruleName,
										   prevChild.getToken().getLine());
				redefinition = true;
				continue;
			}
			ruleToAST.put(ruleName, ruleAST);
		}

		// check for undefined rules
		class UndefChecker extends GrammarTreeVisitor {
			public boolean badref = false;
			@Override
			public void tokenRef(TerminalAST ref) {
				if ("EOF".equals(ref.getText())) {
					// this is a special predefined reference
					return;
				}

				if ( g.isLexer() ) ruleRef(ref, null);
			}

			@Override
			public void ruleRef(GrammarAST ref, ActionAST arg) {
				RuleAST ruleAST = ruleToAST.get(ref.getText());
				String fileName = ref.getToken().getInputStream().getSourceName();
				if (Character.isUpperCase(currentRuleName.charAt(0)) &&
					Character.isLowerCase(ref.getText().charAt(0)))
				{
					badref = true;
					errMgr.grammarError(ErrorType.PARSER_RULE_REF_IN_LEXER_RULE,
										fileName, ref.getToken(), ref.getText(), currentRuleName);
				}
				else if ( ruleAST==null ) {
					badref = true;
					errMgr.grammarError(ErrorType.UNDEFINED_RULE_REF,
										fileName, ref.token, ref.getText());
				}
			}
			@Override
			public ErrorManager getErrorManager() { return errMgr; }
		}

		UndefChecker chk = new UndefChecker();
		chk.visitGrammar(g.ast);

		return redefinition || chk.badref;
	}

	public List<GrammarRootAST> sortGrammarByTokenVocab(List<String> fileNames) {
//		System.out.println(fileNames);
		Graph<String> g = new Graph<String>();
		List<GrammarRootAST> roots = new ArrayList<GrammarRootAST>();
		for (String fileName : fileNames) {
			GrammarAST t = parseGrammar(fileName);
			if ( t==null || t instanceof GrammarASTErrorNode) continue; // came back as error node
			if ( ((GrammarRootAST)t).hasErrors ) continue;
			GrammarRootAST root = (GrammarRootAST)t;
			roots.add(root);
			root.fileName = fileName;
			String grammarName = root.getChild(0).getText();

			GrammarAST tokenVocabNode = findOptionValueAST(root, "tokenVocab");
			// Make grammars depend on any tokenVocab options
			if ( tokenVocabNode!=null ) {
				String vocabName = tokenVocabNode.getText();
				// Strip quote characters if any
				int len = vocabName.length();
				int firstChar = vocabName.charAt(0);
				int lastChar = vocabName.charAt(len - 1);
				if (len >= 2 && firstChar == '\'' && lastChar == '\'') {
					vocabName = vocabName.substring(1, len-1);
				}
				// If the name contains a path delimited by forward slashes,
				// use only the part after the last slash as the name
				int lastSlash = vocabName.lastIndexOf('/');
				if (lastSlash >= 0) {
					vocabName = vocabName.substring(lastSlash + 1);
				}
				g.addEdge(grammarName, vocabName);
			}
			// add cycle to graph so we always process a grammar if no error
			// even if no dependency
			g.addEdge(grammarName, grammarName);
		}

		List<String> sortedGrammarNames = g.sort();
//		System.out.println("sortedGrammarNames="+sortedGrammarNames);

		List<GrammarRootAST> sortedRoots = new ArrayList<GrammarRootAST>();
		for (String grammarName : sortedGrammarNames) {
			for (GrammarRootAST root : roots) {
				if ( root.getGrammarName().equals(grammarName) ) {
					sortedRoots.add(root);
					break;
				}
			}
		}

		return sortedRoots;
	}

	/** Manually get option node from tree; return null if no defined. */
	public static GrammarAST findOptionValueAST(GrammarRootAST root, String option) {
		GrammarAST options = (GrammarAST)root.getFirstChildWithType(ANTLRParser.OPTIONS);
		if ( options!=null && options.getChildCount() > 0 ) {
			for (Object o : options.getChildren()) {
				GrammarAST c = (GrammarAST)o;
				if ( c.getType() == ANTLRParser.ASSIGN &&
					 c.getChild(0).getText().equals(option) )
				{
					return (GrammarAST)c.getChild(1);
				}
			}
		}
		return null;
	}


	/** Given the raw AST of a grammar, create a grammar object
		associated with the AST. Once we have the grammar object, ensure
		that all nodes in tree referred to this grammar. Later, we will
		use it for error handling and generally knowing from where a rule
		comes from.
	 */
	public Grammar createGrammar(GrammarRootAST ast) {
		final Grammar g;
		if ( ast.grammarType==ANTLRParser.LEXER ) g = new LexerGrammar(this, ast);
		else g = new Grammar(this, ast);

		// ensure each node has pointer to surrounding grammar
		GrammarTransformPipeline.setGrammarPtr(g, ast);
		return g;
	}

	public GrammarRootAST parseGrammar(String fileName) {
		try {
			File file = new File(fileName);
			if (!file.isAbsolute()) {
				file = new File(inputDirectory, fileName);
			}

			ANTLRFileStream in = new ANTLRFileStream(file.getAbsolutePath(), grammarEncoding);
			GrammarRootAST t = parse(fileName, in);
			return t;
		}
		catch (IOException ioe) {
			errMgr.toolError(ErrorType.CANNOT_OPEN_FILE, ioe, fileName);
		}
		return null;
	}

	/** Convenience method to load and process an ANTLR grammar. Useful
	 *  when creating interpreters.  If you need to access to the lexer
	 *  grammar created while processing a combined grammar, use
	 *  getImplicitLexer() on returned grammar.
	 */
	public Grammar loadGrammar(String fileName) {
		GrammarRootAST grammarRootAST = parseGrammar(fileName);
		final Grammar g = createGrammar(grammarRootAST);
		g.fileName = fileName;
		process(g, false);
		return g;
	}

	private final Map<String, Grammar> importedGrammars = new HashMap<String, Grammar>();

	/**
	 * Try current dir then dir of g then lib dir
	 * @param g
	 * @param nameNode The node associated with the imported grammar name.
	 */
	public Grammar loadImportedGrammar(Grammar g, GrammarAST nameNode) throws IOException {
		String name = nameNode.getText();
		Grammar imported = importedGrammars.get(name);
		if (imported == null) {
			g.tool.log("grammar", "load " + name + " from " + g.fileName);
			File importedFile = null;
			for (String extension : ALL_GRAMMAR_EXTENSIONS) {
				importedFile = getImportedGrammarFile(g, name + extension);
				if (importedFile != null) {
					break;
				}
			}

			if ( importedFile==null ) {
				errMgr.grammarError(ErrorType.CANNOT_FIND_IMPORTED_GRAMMAR, g.fileName, nameNode.getToken(), name);
				return null;
			}

			String absolutePath = importedFile.getAbsolutePath();
			ANTLRFileStream in = new ANTLRFileStream(absolutePath, grammarEncoding);
			GrammarRootAST root = parse(g.fileName, in);
			if (root == null) {
				return null;
			}

			imported = createGrammar(root);
			imported.fileName = absolutePath;
			importedGrammars.put(root.getGrammarName(), imported);
		}

		return imported;
	}

	public GrammarRootAST parseGrammarFromString(String grammar) {
		return parse("<string>", new ANTLRStringStream(grammar));
	}

	public GrammarRootAST parse(String fileName, CharStream in) {
		try {
			GrammarASTAdaptor adaptor = new GrammarASTAdaptor(in);
			ToolANTLRLexer lexer = new ToolANTLRLexer(in, this);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			lexer.tokens = tokens;
			ToolANTLRParser p = new ToolANTLRParser(tokens, this);
			p.setTreeAdaptor(adaptor);
			ParserRuleReturnScope r = p.grammarSpec();
			GrammarAST root = (GrammarAST) r.getTree();
			if (root instanceof GrammarRootAST) {
				((GrammarRootAST) root).hasErrors = lexer.getNumberOfSyntaxErrors() > 0 || p.getNumberOfSyntaxErrors() > 0;
				assert ((GrammarRootAST) root).tokenStream == tokens;
				if (grammarOptions != null) {
					((GrammarRootAST) root).cmdLineOptions = grammarOptions;
				}
				return ((GrammarRootAST) root);
			}
			return null;
		}
		catch (RecognitionException re) {
			// TODO: do we gen errors now?
			ErrorManager.internalError("can't generate this message at moment; antlr recovers");
		}
		return null;
	}

	public void generateATNs(Grammar g) {
		DOTGenerator dotGenerator = new DOTGenerator(g);
		List<Grammar> grammars = new ArrayList<Grammar>();
		grammars.add(g);
		List<Grammar> imported = g.getAllImportedGrammars();
		if ( imported!=null ) grammars.addAll(imported);
		for (Grammar ig : grammars) {
			for (Rule r : ig.rules.values()) {
				try {
					String dot = dotGenerator.getDOT(g.atn.ruleToStartState[r.index], g.isLexer());
					if (dot != null) {
						writeDOTFile(g, r, dot);
					}
				}
                catch (IOException ioe) {
					errMgr.toolError(ErrorType.CANNOT_WRITE_FILE, ioe);
				}
			}
		}
	}

	public static String generateInterpreterData(Grammar g) {
		StringBuilder content = new StringBuilder();

		content.append("token literal names:\n");
		String[] names = g.getTokenLiteralNames();
		for (String name : names) {
			content.append(name + "\n");
		}
		content.append("\n");

		content.append("token symbolic names:\n");
		names = g.getTokenSymbolicNames();
		for (String name : names) {
			content.append(name + "\n");
		}
		content.append("\n");

		content.append("rule names:\n");
		names = g.getRuleNames();
		for (String name : names) {
			content.append(name + "\n");
		}
		content.append("\n");

		if ( g.isLexer() ) {
			content.append("channel names:\n");
			content.append("DEFAULT_TOKEN_CHANNEL\n");
			content.append("HIDDEN\n");
			for (String channel : g.channelValueToNameList) {
				content.append(channel + "\n");
			}
			content.append("\n");

			content.append("mode names:\n");
			for (String mode : ((LexerGrammar)g).modes.keySet()) {
				content.append(mode + "\n");
			}
		}
		content.append("\n");

		IntegerList serializedATN = ATNSerializer.getSerialized(g.atn);
		// Uncomment if you'd like to write out histogram info on the numbers of
		// each integer value:
		//Utils.writeSerializedATNIntegerHistogram(g.name+"-histo.csv", serializedATN);

		content.append("atn:\n");
		content.append(serializedATN.toString());

		return content.toString();
	}

	/** This method is used by all code generators to create new output
	 *  files. If the outputDir set by -o is not present it will be created.
	 *  The final filename is sensitive to the output directory and
	 *  the directory where the grammar file was found.  If -o is /tmp
	 *  and the original grammar file was foo/t.g4 then output files
	 *  go in /tmp/foo.
	 *
	 *  The output dir -o spec takes precedence if it's absolute.
	 *  E.g., if the grammar file dir is absolute the output dir is given
	 *  precedence. "-o /tmp /usr/lib/t.g4" results in "/tmp/T.java" as
	 *  output (assuming t.g4 holds T.java).
	 *
	 *  If no -o is specified, then just write to the directory where the
	 *  grammar file was found.
	 *
	 *  If outputDirectory==null then write a String.
	 */
	public Writer getOutputFileWriter(Grammar g, String fileName) throws IOException {
		if (outputDirectory == null) {
			return new StringWriter();
		}
		// output directory is a function of where the grammar file lives
		// for subdir/T.g4, you get subdir here.  Well, depends on -o etc...
		File outputDir = getOutputDirectory(g.fileName);
		File outputFile = new File(outputDir, fileName);

		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(outputFile);
		OutputStreamWriter osw;
		if ( grammarEncoding!=null ) {
			osw = new OutputStreamWriter(fos, grammarEncoding);
		}
		else {
			osw = new OutputStreamWriter(fos);
		}
		return new BufferedWriter(osw);
	}

	public File getImportedGrammarFile(Grammar g, String fileName) {
		File importedFile = new File(inputDirectory, fileName);
		if ( !importedFile.exists() ) {
			File gfile = new File(g.fileName);
			String parentDir = gfile.getParent();
			importedFile = new File(parentDir, fileName);
			if ( !importedFile.exists() ) { // try in lib dir
				importedFile = new File(libDirectory, fileName);
				if ( !importedFile.exists() ) {
					return null;
				}
			}
		}
		return importedFile;
	}

	/**
	 * Return the location where ANTLR will generate output files for a given
	 * file. This is a base directory and output files will be relative to
	 * here in some cases such as when -o option is used and input files are
	 * given relative to the input directory.
	 *
	 * @param fileNameWithPath path to input source
	 */
	public File getOutputDirectory(String fileNameWithPath) {
		if ( exact_output_dir ) {
			return new_getOutputDirectory(fileNameWithPath);
		}

		File outputDir;
		String fileDirectory;

		// Some files are given to us without a PATH but should should
		// still be written to the output directory in the relative path of
		// the output directory. The file directory is either the set of sub directories
		// or just or the relative path recorded for the parent grammar. This means
		// that when we write the tokens files, or the .java files for imported grammars
		// taht we will write them in the correct place.
		if ((fileNameWithPath == null) || (fileNameWithPath.lastIndexOf(File.separatorChar) == -1)) {
			// No path is included in the file name, so make the file
			// directory the same as the parent grammar (which might sitll be just ""
			// but when it is not, we will write the file in the correct place.
			fileDirectory = ".";

		}
		else {
			fileDirectory = fileNameWithPath.substring(0, fileNameWithPath.lastIndexOf(File.separatorChar));
		}
		if ( haveOutputDir ) {
			// -o /tmp /var/lib/t.g4 => /tmp/T.java
			// -o subdir/output /usr/lib/t.g4 => subdir/output/T.java
			// -o . /usr/lib/t.g4 => ./T.java
			if (fileDirectory != null &&
				(new File(fileDirectory).isAbsolute() ||
					fileDirectory.startsWith("~"))) { // isAbsolute doesn't count this :(
				// somebody set the dir, it takes precendence; write new file there
				outputDir = new File(outputDirectory);
			}
			else {
				// -o /tmp subdir/t.g4 => /tmp/subdir/T.java
				if (fileDirectory != null) {
					outputDir = new File(outputDirectory, fileDirectory);
				}
				else {
					outputDir = new File(outputDirectory);
				}
			}
		}
		else {
			// they didn't specify a -o dir so just write to location
			// where grammar is, absolute or relative, this will only happen
			// with command line invocation as build tools will always
			// supply an output directory.
			outputDir = new File(fileDirectory);
		}
		return outputDir;
	}

	/** @since 4.7.1 in response to -Xexact-output-dir */
	public File new_getOutputDirectory(String fileNameWithPath) {
		File outputDir;
		String fileDirectory;

		if (fileNameWithPath.lastIndexOf(File.separatorChar) == -1) {
			// No path is included in the file name, so make the file
			// directory the same as the parent grammar (which might still be just ""
			// but when it is not, we will write the file in the correct place.
			fileDirectory = ".";
		}
		else {
			fileDirectory = fileNameWithPath.substring(0, fileNameWithPath.lastIndexOf(File.separatorChar));
		}
		if ( haveOutputDir ) {
			// -o /tmp /var/lib/t.g4 => /tmp/T.java
			// -o subdir/output /usr/lib/t.g4 => subdir/output/T.java
			// -o . /usr/lib/t.g4 => ./T.java
			// -o /tmp subdir/t.g4 => /tmp/T.java
			outputDir = new File(outputDirectory);
		}
		else {
			// they didn't specify a -o dir so just write to location
			// where grammar is, absolute or relative, this will only happen
			// with command line invocation as build tools will always
			// supply an output directory.
			outputDir = new File(fileDirectory);
		}
		return outputDir;
	}

	protected void writeDOTFile(Grammar g, Rule r, String dot) throws IOException {
		writeDOTFile(g, r.g.name + "." + r.name, dot);
	}

	protected void writeDOTFile(Grammar g, String name, String dot) throws IOException {
		Writer fw = getOutputFileWriter(g, name + ".dot");
		try {
			fw.write(dot);
		}
		finally {
			fw.close();
		}
	}

	public void help() {
		info("ANTLR Parser Generator  Version " + Tool.VERSION);
		for (Option o : optionDefs) {
			String name = o.name + (o.argType!=OptionArgType.NONE? " ___" : "");
			String s = String.format(" %-19s %s", name, o.description);
			info(s);
		}
	}

    public void log(String component, String msg) { logMgr.log(component, msg); }
    public void log(String msg) { log(null, msg); }

	public int getNumErrors() { return errMgr.getNumErrors(); }

	public void addListener(ANTLRToolListener tl) {
		if ( tl!=null ) listeners.add(tl);
	}
	public void removeListener(ANTLRToolListener tl) { listeners.remove(tl); }
	public void removeListeners() { listeners.clear(); }
	public List<ANTLRToolListener> getListeners() { return listeners; }

	public void info(String msg) {
		if ( listeners.isEmpty() ) {
			defaultListener.info(msg);
			return;
		}
		for (ANTLRToolListener l : listeners) l.info(msg);
	}
	public void error(ANTLRMessage msg) {
		if ( listeners.isEmpty() ) {
			defaultListener.error(msg);
			return;
		}
		for (ANTLRToolListener l : listeners) l.error(msg);
	}
	public void warning(ANTLRMessage msg) {
		if ( listeners.isEmpty() ) {
			defaultListener.warning(msg);
		}
		else {
			for (ANTLRToolListener l : listeners) l.warning(msg);
		}

		if (warnings_are_errors) {
			errMgr.emit(ErrorType.WARNING_TREATED_AS_ERROR, new ANTLRMessage(ErrorType.WARNING_TREATED_AS_ERROR));
		}
	}

	public void version() {
		info("ANTLR Parser Generator  Version " + VERSION);
	}

	public void exit(int e) { System.exit(e); }

	public void panic() { throw new Error("ANTLR panic"); }

}
