package org.antlr.v4;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.TreeWizard;
import org.antlr.v4.analysis.AnalysisPipeline;
import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.LexerNFAFactory;
import org.antlr.v4.automata.NFAFactory;
import org.antlr.v4.automata.ParserNFAFactory;
import org.antlr.v4.codegen.CodeGenPipeline;
import org.antlr.v4.parse.ANTLRLexer;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.parse.GrammarASTAdaptor;
import org.antlr.v4.parse.ToolANTLRParser;
import org.antlr.v4.semantics.SemanticPipeline;
import org.antlr.v4.tool.*;

import java.io.*;
import java.util.*;

public class Tool {
    public final Properties antlrSettings = new Properties();
    public String VERSION = "!Unknown version!";
    //public static final String VERSION = "${project.version}";
    public static final String UNINITIALIZED_DIR = "<unset-dir>";

	public ErrorManager errMgr = new ErrorManager(this);
	
	List<ANTLRToolListener> listeners =
		Collections.synchronizedList(new ArrayList<ANTLRToolListener>());
	/** Track separately so if someone adds a listener, it's the only one
	 *  instead of it and the default stderr listener.
	 */
	DefaultToolListener defaultListener = new DefaultToolListener(this);

    Map<String, Grammar> grammars = new HashMap<String, Grammar>();

	// GRAMMARS
    public List<String> grammarFileNames = new ArrayList<String>();
	
	// COMMAND-LINE OPTIONS

    public boolean generate_NFA_dot = false;
    public boolean generate_DFA_dot = false;
    public String outputDirectory = ".";
    public boolean haveOutputDir = false;
    public String inputDirectory = null;
    public String parentGrammarDirectory;
    public String grammarOutputDirectory;
    public boolean haveInputDir = false;
    public String libDirectory = ".";
    public boolean debug = false;
    public boolean trace = false;
    public boolean profile = false;
    public boolean report = false;
    public boolean printGrammar = false;
    public boolean depend = false;
    public boolean forceAllFilesToOutputDir = false;
    public boolean forceRelativeOutput = false;
    public boolean deleteTempLexer = true;
	public boolean minimizeDFA = true;
    public boolean verbose = false;
	
    /** Don't process grammar file if generated files are newer than grammar */
    /**
     * Indicate whether the tool should analyze the dependencies of the provided grammar
     * file list and ensure that the grammars with dependencies are built
     * after any of the other gramamrs in the list that they are dependent on. Setting
     * this option also has the side effect that any grammars that are includes for other
     * grammars in the list are excluded from individual analysis, which allows the caller
     * to invoke the tool via org.antlr.tool -make *.g and not worry about the inclusion
     * of grammars that are just includes for other grammars or what order the grammars
     * appear on the command line.
     *
     * This option was coded to make life easier for tool integration (such as Maven) but
     * may also be useful at the command line.
     *
     * @param make
     */
    public boolean make = false;
    public boolean showBanner = true;

    /** Exit after showing version or whatever */ 
    public static boolean exitNow = false;

    // The internal options are for my use on the command line during dev
    public static boolean internalOption_PrintGrammarTree = false;
    public static boolean internalOption_PrintDFA = false;
    public static boolean internalOption_ShowNFAConfigsInDFA = false;
    public static boolean internalOption_watchNFAConversion = false;
    public static boolean internalOption_saveTempLexer = false;

    public static void main(String[] args) {
        Tool antlr = new Tool(args);

        if (!exitNow) {
            antlr.processGrammarsOnCommandLine();
            if (antlr.errMgr.getNumErrors() > 0) {
                antlr.exit(1);
            }
            antlr.exit(0);
        }
    }

    public Tool() { }

    public Tool(String[] args) {
		this();
        processArgs(args);
    }

    public void exit(int e) { System.exit(e); }

    public void panic() { throw new Error("ANTLR panic"); }
    
    public void processArgs(String[] args) {
        if (verbose) {
			info("ANTLR Parser Generator  Version " + VERSION);
            showBanner = false;
        }

        if (args == null || args.length == 0) {
            help();
            return;
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-o") || args[i].equals("-fo")) {
                if (i + 1 >= args.length) {
                    System.err.println("missing output directory with -fo/-o option; ignoring");
                }
                else {
                    if (args[i].equals("-fo")) { // force output into dir
                        forceAllFilesToOutputDir = true;
                    }
                    i++;
                    outputDirectory = args[i];
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
            }
            else if (args[i].equals("-lib")) {
                if (i + 1 >= args.length) {
                    System.err.println("missing library directory with -lib option; ignoring");
                }
                else {
                    i++;
                    libDirectory = args[i];
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
            }
            else if (args[i].equals("-nfa")) {
                generate_NFA_dot = true;
            }
            else if (args[i].equals("-dfa")) {
                generate_DFA_dot = true;
            }
            else if (args[i].equals("-debug")) {
                debug = true;
            }
            else if (args[i].equals("-trace")) {
                trace = true;
            }
            else if (args[i].equals("-report")) {
                report = true;
            }
            else if (args[i].equals("-profile")) {
                profile = true;
            }
            else if (args[i].equals("-print")) {
                printGrammar = true;
            }
            else if (args[i].equals("-depend")) {
                depend = true;
            }
            else if (args[i].equals("-verbose")) {
                verbose = true;
            }
            else if (args[i].equals("-version")) {
                version();
                exitNow = true;
            }
            else if (args[i].equals("-make")) {
                make = true;
            }
            else if (args[i].equals("-message-format")) {
                if (i + 1 >= args.length) {
                    System.err.println("missing output format with -message-format option; using default");
                }
                else {
                    i++;
                    //ErrorManager.setFormat(args[i]);
                }
            }
            else if (args[i].equals("-Xgrtree")) {
                internalOption_PrintGrammarTree = true; // print grammar tree
            }
			else if (args[i].equals("-Xdfa")) {
				internalOption_PrintDFA = true;
			}
			else if (args[i].equals("-Xnominimizedfa")) {
				minimizeDFA = false;
			}
            else if (args[i].equals("-Xnoprune")) {
                //DFAOptimizer.PRUNE_EBNF_EXIT_BRANCHES = false;
            }
            else if (args[i].equals("-Xnocollapse")) {
                //DFAOptimizer.COLLAPSE_ALL_PARALLEL_EDGES = false;
            }
            else if (args[i].equals("-Xdbgconversion")) {
                //NFAToDFAConverter.debug = true;
            }
            else if (args[i].equals("-Xmultithreaded")) {
                //NFAToDFAConverter.SINGLE_THREADED_NFA_CONVERSION = false;
            }
            else if (args[i].equals("-Xnomergestopstates")) {
                //DFAOptimizer.MERGE_STOP_STATES = false;
            }
            else if (args[i].equals("-Xdfaverbose")) {
                internalOption_ShowNFAConfigsInDFA = true;
            }
            else if (args[i].equals("-Xsavelexer")) {
                internalOption_saveTempLexer = true;
            }
            else if (args[i].equals("-Xwatchconversion")) {
                internalOption_watchNFAConversion = true;
            }
            else if (args[i].equals("-XdbgST")) {
                //CodeGenerator.EMIT_TEMPLATE_DELIMITERS = true;
            }
            else if (args[i].equals("-Xmaxinlinedfastates")) {
                if (i + 1 >= args.length) {
                    System.err.println("missing max inline dfa states -Xmaxinlinedfastates option; ignoring");
                }
                else {
                    i++;
                   // CodeGenerator.MAX_ACYCLIC_DFA_STATES_INLINE = Integer.parseInt(args[i]);
                }
            }
            else if (args[i].equals("-Xmaxswitchcaselabels")) {
                if (i + 1 >= args.length) {
                    System.err.println("missing max switch case labels -Xmaxswitchcaselabels option; ignoring");
                }
                else {
                    i++;
                   // CodeGenerator.MAX_SWITCH_CASE_LABELS = Integer.parseInt(args[i]);
                }
            }
            else if (args[i].equals("-Xminswitchalts")) {
                if (i + 1 >= args.length) {
                    System.err.println("missing min switch alternatives -Xminswitchalts option; ignoring");
                }
                else {
                    i++;
                   // CodeGenerator.MIN_SWITCH_ALTS = Integer.parseInt(args[i]);
                }
            }
            else if (args[i].equals("-Xm")) {
                if (i + 1 >= args.length) {
                    System.err.println("missing max recursion with -Xm option; ignoring");
                }
                else {
                    i++;
                    //NFAContext.MAX_SAME_RULE_INVOCATIONS_PER_NFA_CONFIG_STACK = Integer.parseInt(args[i]);
                }
            }
            else if (args[i].equals("-Xmaxdfaedges")) {
                if (i + 1 >= args.length) {
                    System.err.println("missing max number of edges with -Xmaxdfaedges option; ignoring");
                }
                else {
                    i++;
                   // DFA.MAX_STATE_TRANSITIONS_FOR_TABLE = Integer.parseInt(args[i]);
                }
            }
            else if (args[i].equals("-Xconversiontimeout")) {
                if (i + 1 >= args.length) {
                    System.err.println("missing max time in ms -Xconversiontimeout option; ignoring");
                }
                else {
                    i++;
                    //DFA.MAX_TIME_PER_DFA_CREATION = Integer.parseInt(args[i]);
                }
            }
            else if (args[i].equals("-Xnfastates")) {
                //DecisionProbe.verbose = true;
            }
            else if (args[i].equals("-X")) {
                Xhelp();
            }
            else {
                if (args[i].charAt(0) != '-') {
                    // Must be the grammar file
                    addGrammarFile(args[i]);
                }
            }
        }
    }

    public GrammarAST load(String fileName) {
        ANTLRFileStream in = null;
        try {
            in = new ANTLRFileStream(fileName);
        }
        catch (IOException ioe) {
            errMgr.toolError(ErrorType.CANNOT_OPEN_FILE, fileName, ioe);
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

    public void processGrammarsOnCommandLine() {
		// TODO: process all files
        GrammarAST t = load(grammarFileNames.get(0));
        GrammarRootAST lexerAST = null;
		if ( t instanceof GrammarASTErrorNode ) return; // came back as error node
		if ( ((GrammarRootAST)t).hasErrors ) return;

		GrammarRootAST ast = (GrammarRootAST)t;
        Grammar g = createGrammar(ast);
        g.fileName = grammarFileNames.get(0);
		process(g);
		if ( ast!=null && ast.grammarType==ANTLRParser.COMBINED && !ast.hasErrors ) {
			lexerAST = extractImplicitLexer(g); // alters ast
			if ( lexerAST!=null ) {
				LexerGrammar lexerg = new LexerGrammar(this, lexerAST);
				lexerg.fileName = grammarFileNames.get(0);
				g.implicitLexer = lexerg;
				lexerg.implicitLexerOwner = g;
				process(lexerg);
			}
        }
    }

	public Grammar createGrammar(GrammarRootAST ast) {
		if ( ast.grammarType==ANTLRParser.LEXER ) return new LexerGrammar(this, ast);
		else return new Grammar(this, ast);
	}	

    public void process(Grammar g) {
        grammars.put(g.name, g);
        g.loadImportedGrammars();
        if ( g.ast!=null && internalOption_PrintGrammarTree ) System.out.println(g.ast.toStringTree());
        //g.ast.inspect();

		// MAKE SURE GRAMMAR IS SEMANTICALLY CORRECT (FILL IN GRAMMAR OBJECT)
        SemanticPipeline sem = new SemanticPipeline(g);
        sem.process();

		if ( errMgr.getNumErrors()>0 ) return;
		
		if ( g.getImportedGrammars()!=null ) { // process imported grammars (if any)
			for (Grammar imp : g.getImportedGrammars()) {
				process(imp);
			}
		}

		// BUILD NFA FROM AST
		NFAFactory factory = new ParserNFAFactory(g);
		if ( g.isLexer() ) factory = new LexerNFAFactory((LexerGrammar)g);
		g.nfa = factory.createNFA();
		
		if ( generate_NFA_dot ) generateNFAs(g);

		// PERFORM GRAMMAR ANALYSIS ON NFA: BUILD DECISION DFAs
		AnalysisPipeline anal = new AnalysisPipeline(g);
		anal.process();
		
		if ( generate_DFA_dot ) generateDFAs(g);

		if ( g.tool.getNumErrors()>0 ) return;

		// GENERATE CODE
		CodeGenPipeline gen = new CodeGenPipeline(g);
		gen.process();
    }

    // TODO: Move to ast manipulation class?

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
        List<GrammarAST> elements = combinedAST.getChildren();

        // MAKE A GRAMMAR ROOT and ID
        String lexerName = combinedAST.getChild(0).getText()+"Lexer";
        GrammarRootAST lexerAST =
            new GrammarRootAST(new CommonToken(ANTLRParser.GRAMMAR,"LEXER_GRAMMAR"));
        lexerAST.token.setInputStream(combinedAST.token.getInputStream());
        lexerAST.addChild((GrammarAST)adaptor.create(ANTLRParser.ID, lexerName));

        // MOVE OPTIONS
        GrammarAST optionsRoot =
            (GrammarAST)combinedAST.getFirstChildWithType(ANTLRParser.OPTIONS);
        if ( optionsRoot!=null ) {
            GrammarAST lexerOptionsRoot = (GrammarAST)adaptor.dupNode(optionsRoot);
            lexerAST.addChild(lexerOptionsRoot);
            List<GrammarAST> options = optionsRoot.getChildren();
            for (GrammarAST o : options) {
                String optionName = o.getChild(0).getText();
                if ( !Grammar.doNotCopyOptionsToLexer.contains(optionName) ) {
                    lexerOptionsRoot.addChild(o);
                }
            }
        }

        // MOVE lexer:: actions
        List<GrammarAST> actionsWeMoved = new ArrayList<GrammarAST>();
        for (GrammarAST e : elements) {
            if ( e.getType()==ANTLRParser.AT ) {
                if ( e.getChild(0).getText().equals("lexer") ) {
                    lexerAST.addChild(e);
                    actionsWeMoved.add(e);
                }
            }
        }
        elements.removeAll(actionsWeMoved);
        GrammarAST combinedRulesRoot =
            (GrammarAST)combinedAST.getFirstChildWithType(ANTLRParser.RULES);
        if ( combinedRulesRoot==null ) return lexerAST;

		TreeWizard wiz = new TreeWizard(adaptor,ANTLRParser.tokenNames);

        // MOVE lexer rules

        GrammarAST lexerRulesRoot =
            (GrammarAST)adaptor.create(ANTLRParser.RULES, "RULES");
        lexerAST.addChild(lexerRulesRoot);
        List<GrammarAST> rulesWeMoved = new ArrayList<GrammarAST>();
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
			//TreeWizard wiz = new TreeWizard(adaptor,ANTLRParser.tokenNames);
			String rname = combinedGrammar.getStringLiteralLexerRuleName(lit);
			GrammarAST litRule = (GrammarAST)
				wiz.create("(RULE ID["+rname+"] (BLOCK (ALT STRING_LITERAL["+lit+"])))");
			lexerRulesRoot.addChild(litRule);
		}

        //System.out.println("after ="+combinedAST.toStringTree());
        System.out.println("lexer ="+lexerAST.toStringTree());
        return lexerAST;
    }

	public void generateNFAs(Grammar g) {
		DOTGenerator dotGenerator = new DOTGenerator(g);
		List<Grammar> grammars = new ArrayList<Grammar>();
		grammars.add(g);
		List<Grammar> imported = g.getAllImportedGrammars();
		if ( imported!=null ) grammars.addAll(imported);
		for (Grammar ig : grammars) {
			for (Rule r : ig.rules.values()) {
				try {
					String dot = dotGenerator.getDOT(g.nfa.ruleToStartState.get(r));
					if (dot != null) {
						writeDOTFile(g, r, dot);
					}
				} catch (IOException ioe) {
					errMgr.toolError(ErrorType.CANNOT_WRITE_FILE, ioe);
				}
			}
		}
	}

	public void generateDFAs(Grammar g) {
		for (DFA dfa : g.decisionDFAs.values()) {
			generateDFA(g, dfa);
		}
	}

	public void generateDFA(Grammar g, DFA dfa) {
		DOTGenerator dotGenerator = new DOTGenerator(g);
		String dot = dotGenerator.getDOT(dfa.startState);
		String dec = "dec-";
		//if ( dfa.minimized ) dec += "min-";
		String dotFileName = g.name + "." + dec + dfa.decision;
		if (g.implicitLexer!=null) {
			dotFileName = g.name +
						  Grammar.getGrammarTypeToFileNameSuffix(g.getType()) +
						  "." + dec + dfa.decision;
		}
		try {
			writeDOTFile(g, dotFileName, dot);
		}
		catch (IOException ioe) {
			errMgr.toolError(ErrorType.CANNOT_WRITE_FILE, dotFileName, ioe);
		}
	}

	protected void writeDOTFile(Grammar g, Rule r, String dot) throws IOException {
		writeDOTFile(g, r.g.name + "." + r.name, dot);
	}

	protected void writeDOTFile(Grammar g, String name, String dot) throws IOException {
		Writer fw = getOutputFile(g, name + ".dot");
		fw.write(dot);
		fw.close();
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
			if (haveOutputDir) {
				outputDir = new File(outputDirectory);
			}
			else {
				outputDir = new File(".");
			}
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
	 * Return the Path to the directory in which ANTLR will search for ancillary
	 * files such as .tokens vocab files and imported grammar files.
	 *
	 * @return the lib Directory
	 */
	public String getLibraryDirectory() {
		return libDirectory;
	}

	/**
	 * Return the Path to the base output directory, where ANTLR
	 * will generate all the output files for the current language target as
	 * well as any ancillary files such as .tokens vocab files.
	 *
	 * @return the output Directory
	 */
	public String getOutputDirectory() {
		return outputDirectory;
	}
	
	/**
	 * Return the location where ANTLR will generate output files for a given file. This is a
	 * base directory and output files will be relative to here in some cases
	 * such as when -o option is used and input files are given relative
	 * to the input directory.
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
		//
		if (fileNameWithPath.lastIndexOf(File.separatorChar) == -1) {

			// No path is included in the file name, so make the file
			// directory the same as the parent grammar (which might sitll be just ""
			// but when it is not, we will write the file in the correct place.
			//
			fileDirectory = grammarOutputDirectory;

		}
		else {
			fileDirectory = fileNameWithPath.substring(0, fileNameWithPath.lastIndexOf(File.separatorChar));
		}
		if ( fileDirectory == null ) {
			fileDirectory = ".";
		}
		if (haveOutputDir) {
			// -o /tmp /var/lib/t.g => /tmp/T.java
			// -o subdir/output /usr/lib/t.g => subdir/output/T.java
			// -o . /usr/lib/t.g => ./T.java
			if ((fileDirectory != null && !forceRelativeOutput) &&
				(new File(fileDirectory).isAbsolute() ||
				 fileDirectory.startsWith("~")) || // isAbsolute doesn't count this :(
				forceAllFilesToOutputDir) {
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
			//
			outputDir = new File(fileDirectory);
		}
		return outputDir;
	}

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
	public void error(Message msg) {
		if ( listeners.size()==0 ) {
			defaultListener.error(msg);
			return;
		}
		for (ANTLRToolListener l : listeners) l.error(msg);
	}
	public void warning(Message msg) {
		if ( listeners.size()==0 ) {
			defaultListener.warning(msg);
			return;
		}
		for (ANTLRToolListener l : listeners) l.warning(msg);
	}

	
    public void version() {
        info("ANTLR Parser Generator  Version " + new Tool().VERSION);
    }

    public void help() {
        info("ANTLR Parser Generator  Version " + new Tool().VERSION);
        System.err.println("usage: java org.antlr.Tool [args] file.g [file2.g file3.g ...]");
        System.err.println("  -o outputDir          specify output directory where all output is generated");
        System.err.println("  -fo outputDir         same as -o but force even files with relative paths to dir");
        System.err.println("  -lib dir              specify location of token files");
        System.err.println("  -depend               generate file dependencies");
        System.err.println("  -report               print out a report about the grammar(s) processed");
        System.err.println("  -print                print out the grammar without actions");
        System.err.println("  -debug                generate a parser that emits debugging events");
        System.err.println("  -profile              generate a parser that computes profiling information");
        System.err.println("  -nfa                  generate an NFA for each rule");
        System.err.println("  -dfa                  generate a DFA for each decision point");
        System.err.println("  -message-format name  specify output style for messages");
        System.err.println("  -verbose              generate ANTLR version and other information");
        System.err.println("  -make                 only build if generated files older than grammar");
        System.err.println("  -version              print the version of ANTLR and exit.");
        System.err.println("  -X                    display extended argument list");
    }

    public void Xhelp() {
        info("ANTLR Parser Generator  Version " + new Tool().VERSION);
        System.err.println("  -Xgrtree                print the grammar AST");
		System.err.println("  -Xdfa                   print DFA as text");
		System.err.println("  -Xnominimizedfa         don't minimize decision DFA");
        System.err.println("  -Xnoprune               test lookahead against EBNF block exit branches");
        System.err.println("  -Xnocollapse            collapse incident edges into DFA states");
        System.err.println("  -Xdbgconversion         dump lots of info during NFA conversion");
        System.err.println("  -Xmultithreaded         run the analysis in 2 threads");
        System.err.println("  -Xnomergestopstates     do not merge stop states");
        System.err.println("  -Xdfaverbose            generate DFA states in DOT with NFA configs");
        System.err.println("  -Xwatchconversion       print a message for each NFA before converting");
        System.err.println("  -XdbgST                 put tags at start/stop of all templates in output");
        System.err.println("  -Xnfastates             for nondeterminisms, list NFA states for each path");
        System.err.println("  -Xsavelexer             save temp lexer file created for combined grammars");
        /*
        System.err.println("  -Xm m                   max number of rule invocations during conversion           [" + NFAContext.MAX_SAME_RULE_INVOCATIONS_PER_NFA_CONFIG_STACK + "]");
        System.err.println("  -Xmaxdfaedges m         max \"comfortable\" number of edges for single DFA state     [" + DFA.MAX_STATE_TRANSITIONS_FOR_TABLE + "]");
        System.err.println("  -Xconversiontimeout t   set NFA conversion timeout (ms) for each decision          [" + DFA.MAX_TIME_PER_DFA_CREATION + "]");
        System.err.println("  -Xmaxinlinedfastates m  max DFA states before table used rather than inlining      [" + CodeGenerator.MADSI_DEFAULT +"]");
        System.err.println("  -Xmaxswitchcaselabels m don't generate switch() statements for dfas bigger  than m [" + CodeGenerator.MSCL_DEFAULT +"]");
        System.err.println("  -Xminswitchalts m       don't generate switch() statements for dfas smaller than m [" + CodeGenerator.MSA_DEFAULT + "]");
         */
    }

    public void addGrammarFile(String grammarFileName) {
        if (!grammarFileNames.contains(grammarFileName)) {
            grammarFileNames.add(grammarFileName);
        }
    }

    /**
     * Provide the current setting of the conversion timeout on DFA creation.
     *
     * @return DFA creation timeout value in milliseconds
     */
    public int getConversionTimeout() {
        //return DFA.MAX_TIME_PER_DFA_CREATION;
        return 0;
    }

    /**
     * Returns the current setting of the message format descriptor
     * @return Current message format
     */
    public String getMessageFormat() {
        //return ErrorManager.getMessageFormat().toString();
        return null;
    }

    public int getNumErrors() { return errMgr.getNumErrors(); }

    /**Set the message format to one of ANTLR, gnu, vs2005 */
    public void setMessageFormat(String format) {
        errMgr.setFormat(format);
    }

    /** Set the location (base directory) where output files should be produced
     *  by the ANTLR tool.
     */
    public void setOutputDirectory(String outputDirectory) {
        haveOutputDir = true;
        this.outputDirectory = outputDirectory;
    }

    /**
     * Set the base location of input files. Normally (when the tool is
     * invoked from the command line), the inputDirectory is not set, but
     * for build tools such as Maven, we need to be able to locate the input
     * files relative to the base, as the working directory could be anywhere and
     * changing workig directories is not a valid concept for JVMs because of threading and
     * so on. Setting the directory just means that the getFileDirectory() method will
     * try to open files relative to this input directory.
     *
     * @param inputDirectory Input source base directory
     */
    public void setInputDirectory(String inputDirectory) {
        inputDirectory = inputDirectory;
        haveInputDir = true;
    }

}
