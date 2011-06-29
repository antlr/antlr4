/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4;

import org.antlr.runtime.*;
import org.antlr.v4.analysis.AnalysisPipeline;
import org.antlr.v4.automata.*;
import org.antlr.v4.codegen.CodeGenPipeline;
import org.antlr.v4.parse.*;
import org.antlr.v4.semantics.SemanticPipeline;
import org.antlr.v4.tool.*;
import org.stringtemplate.v4.STGroup;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

public class Tool {
	public String VERSION = "4.0-"+new Date();

	public static enum OptionArgType { NONE, STRING }
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

	public String outputDirectory;
	public String libDirectory;
	public boolean report = false;
	public boolean printGrammar = false;
	public boolean debug = false;
	public boolean profile = false;
	public boolean trace = false;
	public boolean generate_ATN_dot = false;
	public String msgFormat = "antlr";
	public boolean saveLexer = false;
	public boolean launch_ST_inspector = false;

	public static Option[] optionDefs = {
	new Option("outputDirectory",	"-o", OptionArgType.STRING, "specify output directory where all output is generated"),
	new Option("libDirectory",		"-lib", OptionArgType.STRING, "specify location of .token files"),
	new Option("report",			"-report", "print out a report about the grammar(s) processed"),
	new Option("printGrammar",		"-print", "print out the grammar without actions"),
	new Option("debug",				"-debug", "generate a parser that emits debugging events"),
	new Option("profile",			"-profile", "generate a parser that computes profiling information"),
	new Option("trace",				"-trace", "generate a recognizer that traces rule entry/exit"),
	new Option("generate_ATN_dot",	"-atn", "generate rule augmented transition networks"),
	new Option("msgFormat",			"-message-format", OptionArgType.STRING, "specify output style for messages"),
	new Option("saveLexer",			"-Xsavelexer", "save temp lexer file created for combined grammars"),
	new Option("launch_ST_inspector", "-XdbgST", "launch StringTemplate visualizer on generated code"),
	};

	// helper vars for option management
	protected boolean haveOutputDir = false;
	protected boolean return_dont_exit = false;

    // The internal options are for my use on the command line during dev
    public static boolean internalOption_PrintGrammarTree = false;
    public static boolean internalOption_ShowATNConfigsInDFA = false;


	public final String[] args;

	protected List<String> grammarFiles = new ArrayList<String>();

	public ErrorManager errMgr = new ErrorManager(this);

	List<ANTLRToolListener> listeners =
	Collections.synchronizedList(new ArrayList<ANTLRToolListener>());

	/** Track separately so if someone adds a listener, it's the only one
	 *  instead of it and the default stderr listener.
	 */
	DefaultToolListener defaultListener = new DefaultToolListener(this);

	public static void main(String[] args) {
		Tool antlr = new Tool(args);
		if ( args.length == 0 ) { antlr.help(); antlr.exit(0); }

		antlr.processGrammarsOnCommandLine();

		if ( antlr.return_dont_exit ) return;

		if (antlr.errMgr.getNumErrors() > 0) {
			antlr.exit(1);
		}
		antlr.exit(0);
	}

	public Tool() { this(null); }

	public Tool(String[] args) {
		this.args = args;
		handleArgs();
	}

	protected void handleArgs() {
		int i=0;
		while ( args!=null && i<args.length ) {
			String arg = args[i];
			i++;
			if ( arg.charAt(0)!='-' ) { // file name
				grammarFiles.add(arg);
				continue;
			}
			for (Option o : optionDefs) {
				if ( arg.equals(o.name) ) {
					String value = null;
					if ( o.argType==OptionArgType.STRING ) {
						value = args[i];
						i++;
					}
					// use reflection to set field
					Class c = this.getClass();
					try {
						Field f = c.getField(o.fieldName);
						if ( value==null ) f.setBoolean(this, true);
						else f.set(this, value);
					}
					catch (Exception e) {
						errMgr.toolError(ErrorType.INTERNAL_ERROR, "can't access field "+o.fieldName);
					}
				}
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
				libDirectory = ".";
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

	public void processGrammarsOnCommandLine() {
		for (String fileName : grammarFiles) {
			GrammarAST t = load(fileName);
			if ( t instanceof GrammarASTErrorNode ) return; // came back as error node
			if ( ((GrammarRootAST)t).hasErrors ) return;

			GrammarRootAST ast = (GrammarRootAST)t;
			Grammar g = createGrammar(ast);
			g.fileName = fileName;
			process(g);
		}
	}

	public void process(Grammar g) {
		GrammarRootAST lexerAST = null;
		if ( g.ast!=null && g.ast.grammarType== ANTLRParser.COMBINED &&
		!g.ast.hasErrors )
		{
			lexerAST = extractImplicitLexer(g); // alters ast
		}
		processNonCombinedGrammar(g);
		if ( g.ast!=null && g.ast.grammarType== ANTLRParser.COMBINED &&
		!g.ast.hasErrors )
		{
			if ( lexerAST!=null ) {
				LexerGrammar lexerg = new LexerGrammar(this, lexerAST);
				lexerg.fileName = g.fileName;
				g.implicitLexer = lexerg;
				lexerg.implicitLexerOwner = g;
				lexerg.importVocab(g);
				processNonCombinedGrammar(lexerg);
				g.importVocab(lexerg);
			}
		}
	}

	public void processNonCombinedGrammar(Grammar g) {
		g.loadImportedGrammars();
		if ( g.ast!=null && internalOption_PrintGrammarTree ) System.out.println(g.ast.toStringTree());
		//g.ast.inspect();

		// MAKE SURE GRAMMAR IS SEMANTICALLY CORRECT (FILL IN GRAMMAR OBJECT)
		SemanticPipeline sem = new SemanticPipeline(g);
		sem.process();

		if ( errMgr.getNumErrors()>0 ) return;

		if ( g.getImportedGrammars()!=null ) { // process imported grammars (if any)
			for (Grammar imp : g.getImportedGrammars()) {
				processNonCombinedGrammar(imp);
			}
		}

		// BUILD ATN FROM AST
		ATNFactory factory;
		if ( g.isLexer() ) factory = new LexerATNFactory((LexerGrammar)g);
		else factory = new ParserATNFactory(g);
		g.atn = factory.createATN();

		if ( generate_ATN_dot ) generateATNs(g);

		// PERFORM GRAMMAR ANALYSIS ON ATN: BUILD DECISION DFAs
		AnalysisPipeline anal = new AnalysisPipeline(g);
		anal.process();

		//if ( generate_DFA_dot ) generateDFAs(g);

		if ( g.tool.getNumErrors()>0 ) return;

		// GENERATE CODE
		CodeGenPipeline gen = new CodeGenPipeline(g);
		gen.process();
	}

	public Grammar createGrammar(GrammarRootAST ast) {
		if ( ast.grammarType==ANTLRParser.LEXER ) return new LexerGrammar(this, ast);
		else return new Grammar(this, ast);
	}

	public GrammarAST load(String fileName) {
		ANTLRFileStream in = null;
		try {
			in = new ANTLRFileStream(fileName);
		}
		catch (IOException ioe) {
			errMgr.toolError(ErrorType.CANNOT_OPEN_FILE, ioe, fileName);
		}
		return load(in);
	}

	public GrammarAST loadFromString(String grammar) {
		return load(new ANTLRStringStream(grammar));
	}

	public GrammarAST load(CharStream in) {
		try {
			ANTLRLexer lexer = new ANTLRLexer(in);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			ToolANTLRParser p = new ToolANTLRParser(tokens, this);
			p.setTreeAdaptor(new GrammarASTAdaptor(in));
			ParserRuleReturnScope r = p.grammarSpec();
			GrammarAST root = (GrammarAST) r.getTree();
			if ( root instanceof GrammarRootAST ) {
				((GrammarRootAST)root).hasErrors = p.getNumberOfSyntaxErrors()>0;
			}
			return root;
		}
		catch (RecognitionException re) {
			// TODO: do we gen errors now?
			errMgr.internalError("can't generate this message at moment; antlr recovers");
		}
		return null;
	}

	/** Build lexer grammar from combined grammar that looks like:
	 *
	 *  (COMBINED_GRAMMAR A
	 *      (tokens { X (= Y 'y'))
	 *      (OPTIONS (= x 'y'))
	 *      (scope Blort { int x; })
	 *      (@ members {foo})
	 *      (@ lexer header {package jj;})
	 *      (RULES (RULE .+)))
	 *
	 *  Move rules and actions to new tree, don't dup. Split AST apart.
	 *  We'll have this Grammar share token symbols later; don't generate
	 *  tokenVocab or tokens{} section.
	 *
	 *  Side-effects: it removes children from GRAMMAR & RULES nodes
	 *                in combined AST. Careful: nodes are shared between
	 *                trees after this call.
	 */
	public GrammarRootAST extractImplicitLexer(Grammar combinedGrammar) {
		GrammarRootAST combinedAST = combinedGrammar.ast;
		//System.out.println("before="+combinedAST.toStringTree());
		GrammarASTAdaptor adaptor = new GrammarASTAdaptor(combinedAST.token.getInputStream());
		List<org.antlr.v4.tool.GrammarAST> elements = combinedAST.getChildren();

		// MAKE A GRAMMAR ROOT and ID
		String lexerName = combinedAST.getChild(0).getText()+"Lexer";
		GrammarRootAST lexerAST =
		new GrammarRootAST(new CommonToken(ANTLRParser.GRAMMAR,"LEXER_GRAMMAR"));
		lexerAST.grammarType = ANTLRParser.LEXER;
		lexerAST.token.setInputStream(combinedAST.token.getInputStream());
		lexerAST.addChild((org.antlr.v4.tool.GrammarAST)adaptor.create(ANTLRParser.ID, lexerName));

		// MOVE OPTIONS
		org.antlr.v4.tool.GrammarAST optionsRoot =
		(org.antlr.v4.tool.GrammarAST)combinedAST.getFirstChildWithType(ANTLRParser.OPTIONS);
		if ( optionsRoot!=null ) {
			org.antlr.v4.tool.GrammarAST lexerOptionsRoot = (org.antlr.v4.tool.GrammarAST)adaptor.dupNode(optionsRoot);
			lexerAST.addChild(lexerOptionsRoot);
			List<org.antlr.v4.tool.GrammarAST> options = optionsRoot.getChildren();
			for (org.antlr.v4.tool.GrammarAST o : options) {
				String optionName = o.getChild(0).getText();
				if ( !Grammar.doNotCopyOptionsToLexer.contains(optionName) ) {
					lexerOptionsRoot.addChild(o);
				}
			}
		}

		// MOVE lexer:: actions
		List<org.antlr.v4.tool.GrammarAST> actionsWeMoved = new ArrayList<org.antlr.v4.tool.GrammarAST>();
		for (org.antlr.v4.tool.GrammarAST e : elements) {
			if ( e.getType()==ANTLRParser.AT ) {
				if ( e.getChild(0).getText().equals("lexer") ) {
					lexerAST.addChild(e);
					actionsWeMoved.add(e);
				}
			}
		}
		elements.removeAll(actionsWeMoved);
		org.antlr.v4.tool.GrammarAST combinedRulesRoot =
		(org.antlr.v4.tool.GrammarAST)combinedAST.getFirstChildWithType(ANTLRParser.RULES);
		if ( combinedRulesRoot==null ) return lexerAST;

		// MOVE lexer rules

		org.antlr.v4.tool.GrammarAST lexerRulesRoot =
		(org.antlr.v4.tool.GrammarAST)adaptor.create(ANTLRParser.RULES, "RULES");
		lexerAST.addChild(lexerRulesRoot);
		List<org.antlr.v4.tool.GrammarAST> rulesWeMoved = new ArrayList<org.antlr.v4.tool.GrammarAST>();
		List<GrammarASTWithOptions> rules = combinedRulesRoot.getChildren();
		for (GrammarASTWithOptions r : rules) {
			String ruleName = r.getChild(0).getText();
			if ( Character.isUpperCase(ruleName.charAt(0)) ) {
				lexerRulesRoot.addChild(r);
				rulesWeMoved.add(r);
			}
		}
		int nLexicalRules = rulesWeMoved.size();
		rules.removeAll(rulesWeMoved);

		// Will track 'if' from IF : 'if' ; rules to avoid defining new token for 'if'
		Map<String,String> litAliases =
		Grammar.getStringLiteralAliasesFromLexerRules(lexerAST);

		if ( nLexicalRules==0 && (litAliases==null||litAliases.size()==0) &&
		combinedGrammar.stringLiteralToTypeMap.size()==0 )
		{
			// no rules, tokens{}, or 'literals' in grammar
			return null;
		}

		// add strings from combined grammar (and imported grammars) into to lexer
		for (String lit : combinedGrammar.stringLiteralToTypeMap.keySet()) {
			if ( litAliases!=null && litAliases.containsKey(lit) ) continue; // already has rule
			// create for each literal: (RULE <uniquename> (BLOCK (ALT <lit>))
			String rname = combinedGrammar.getStringLiteralLexerRuleName(lit);
			// can't use wizard; need special node types
			org.antlr.v4.tool.GrammarAST litRule = new RuleAST(ANTLRParser.RULE);
			BlockAST blk = new BlockAST(ANTLRParser.BLOCK);
			AltAST alt = new AltAST(ANTLRParser.ALT);
			TerminalAST slit = new TerminalAST(new org.antlr.runtime.CommonToken(ANTLRParser.STRING_LITERAL, lit));
			alt.addChild(slit);
			blk.addChild(alt);
			CommonToken idToken = new CommonToken(ANTLRParser.ID, rname);
			litRule.addChild(new TerminalAST(idToken));
			litRule.addChild(blk);
			lexerRulesRoot.addChild(litRule);

//			(GrammarAST)
//				wiz.create("(RULE ID["+rname+"] (BLOCK (ALT STRING_LITERAL["+lit+"])))");
		}

		System.out.println("after ="+combinedAST.toStringTree());
		System.out.println("lexer ="+lexerAST.toStringTree());
		return lexerAST;
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
					String dot = dotGenerator.getDOT(g.atn.ruleToStartState[r.index]);
					if (dot != null) {
						writeDOTFile(g, r, dot);
					}
				} catch (IOException ioe) {
					errMgr.toolError(ErrorType.CANNOT_WRITE_FILE, ioe);
				}
			}
		}
	}

	/** This method is used by all code generators to create new output
	 *  files. If the outputDir set by -o is not present it will be created.
	 *  The final filename is sensitive to the output directory and
	 *  the directory where the grammar file was found.  If -o is /tmp
	 *  and the original grammar file was foo/t.g then output files
	 *  go in /tmp/foo.
	 *
	 *  The output dir -o spec takes precedence if it's absolute.
	 *  E.g., if the grammar file dir is absolute the output dir is given
	 *  precendence. "-o /tmp /usr/lib/t.g" results in "/tmp/T.java" as
	 *  output (assuming t.g holds T.java).
	 *
	 *  If no -o is specified, then just write to the directory where the
	 *  grammar file was found.
	 *
	 *  If outputDirectory==null then write a String.
	 */
	public Writer getOutputFile(Grammar g, String fileName) throws IOException {
		if (outputDirectory == null) {
			return new StringWriter();
		}
		// output directory is a function of where the grammar file lives
		// for subdir/T.g, you get subdir here.  Well, depends on -o etc...
		// But, if this is a .tokens file, then we force the output to
		// be the base output directory (or current directory if there is not a -o)
		//
		File outputDir;
		if ( fileName.endsWith(".tokens") ) {// CodeGenerator.VOCAB_FILE_EXTENSION)) {
			outputDir = new File(outputDirectory);
		}
		else {
			outputDir = getOutputDirectory(g.fileName);
		}
		File outputFile = new File(outputDir, fileName);

		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		FileWriter fw = new FileWriter(outputFile);
		return new BufferedWriter(fw);
	}

	/**
	 * Return the location where ANTLR will generate output files for a given
	 * file. This is a base directory and output files will be relative to
	 * here in some cases such as when -o option is used and input files are
	 * given relative to the input directory.
	 *
	 * @param fileNameWithPath path to input source
	 * @return
	 */
	public File getOutputDirectory(String fileNameWithPath) {
		File outputDir = new File(outputDirectory);
		String fileDirectory;

		// Some files are given to us without a PATH but should should
		// still be written to the output directory in the relative path of
		// the output directory. The file directory is either the set of sub directories
		// or just or the relative path recorded for the parent grammar. This means
		// that when we write the tokens files, or the .java files for imported grammars
		// taht we will write them in the correct place.
		if (fileNameWithPath.lastIndexOf(File.separatorChar) == -1) {
			// No path is included in the file name, so make the file
			// directory the same as the parent grammar (which might sitll be just ""
			// but when it is not, we will write the file in the correct place.
			fileDirectory = ".";

		}
		else {
			fileDirectory = fileNameWithPath.substring(0, fileNameWithPath.lastIndexOf(File.separatorChar));
		}
		if ( haveOutputDir ) {
			// -o /tmp /var/lib/t.g => /tmp/T.java
			// -o subdir/output /usr/lib/t.g => subdir/output/T.java
			// -o . /usr/lib/t.g => ./T.java
			if (fileDirectory != null &&
				(new File(fileDirectory).isAbsolute() ||
				 fileDirectory.startsWith("~"))) { // isAbsolute doesn't count this :(
				// somebody set the dir, it takes precendence; write new file there
				outputDir = new File(outputDirectory);
			}
			else {
				// -o /tmp subdir/t.g => /tmp/subdir/t.g
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

	protected void writeDOTFile(Grammar g, Rule r, String dot) throws IOException {
		writeDOTFile(g, r.g.name + "." + r.name, dot);
	}

	protected void writeDOTFile(Grammar g, String name, String dot) throws IOException {
		Writer fw = getOutputFile(g, name + ".dot");
		fw.write(dot);
		fw.close();
	}

	public void help() {
		info("ANTLR Parser Generator  Version " + new Tool().VERSION);
		for (Option o : optionDefs) {
			String name = o.name + (o.argType!=OptionArgType.NONE? " ___" : "");
			String s = String.format(" %-19s %s", name, o.description);
			info(s);
		}
	}

	public int getNumErrors() { return errMgr.getNumErrors(); }

	public void addListener(ANTLRToolListener tl) {
		if ( tl!=null ) listeners.add(tl);
	}
	public void removeListener(ANTLRToolListener tl) { listeners.remove(tl); }
	public void removeListeners() { listeners.clear(); }
	public List<ANTLRToolListener> getListeners() { return listeners; }

	public void info(String msg) {
		if ( listeners.size()==0 ) {
			defaultListener.info(msg);
			return;
		}
		for (ANTLRToolListener l : listeners) l.info(msg);
	}
	public void error(ANTLRMessage msg) {
		if ( listeners.size()==0 ) {
			defaultListener.error(msg);
			return;
		}
		for (ANTLRToolListener l : listeners) l.error(msg);
	}
	public void warning(ANTLRMessage msg) {
		if ( listeners.size()==0 ) {
			defaultListener.warning(msg);
			return;
		}
		for (ANTLRToolListener l : listeners) l.warning(msg);
	}

	public void version() {
		info("ANTLR Parser Generator  Version " + new Tool().VERSION);
	}

	public void exit(int e) { System.exit(e); }

	public void panic() { throw new Error("ANTLR panic"); }

}
