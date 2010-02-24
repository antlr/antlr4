package org.antlr.v4;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.TreeWizard;
import org.antlr.v4.parse.ANTLRLexer;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.parse.GrammarASTAdaptor;
import org.antlr.v4.semantics.SemanticPipeline;
import org.antlr.v4.tool.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Tool {
    public final Properties antlrSettings = new Properties();
    public String VERSION = "!Unknown version!";
    //public static final String VERSION = "${project.version}";
    public static final String UNINITIALIZED_DIR = "<unset-dir>";
    private List<String> grammarFileNames = new ArrayList<String>();
    private boolean generate_NFA_dot = false;
    private boolean generate_DFA_dot = false;
    private String outputDirectory = ".";
    private boolean haveOutputDir = false;
    private String inputDirectory = null;
    private String parentGrammarDirectory;
    private String grammarOutputDirectory;
    private boolean haveInputDir = false;
    private String libDirectory = ".";
    private boolean debug = false;
    private boolean trace = false;
    private boolean profile = false;
    private boolean report = false;
    private boolean printGrammar = false;
    private boolean depend = false;
    private boolean forceAllFilesToOutputDir = false;
    private boolean forceRelativeOutput = false;
    protected boolean deleteTempLexer = true;
    private boolean verbose = false;
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
     private boolean make = false;
    private boolean showBanner = true;

    /** Exit after showing version or whatever */ 
    private static boolean exitNow = false;

    // The internal options are for my use on the command line during dev
    public static boolean internalOption_PrintGrammarTree = false;
    public static boolean internalOption_PrintDFA = false;
    public static boolean internalOption_ShowNFAConfigsInDFA = false;
    public static boolean internalOption_watchNFAConversion = false;
    public static boolean internalOption_saveTempLexer = false;

    protected Map<String, Grammar> grammars = new HashMap<String, Grammar>();
    
    public static void main(String[] args) {
        Tool antlr = new Tool(args);

        if (!exitNow) {
            antlr.processGrammarsOnCommandLine();
            if (ErrorManager.getNumErrors() > 0) {
                antlr.exit(1);
            }
            antlr.exit(0);
        }
    }

    public Tool() {
    }

    public Tool(String[] args) {
        processArgs(args);
    }

    public void exit(int e) {
        System.exit(e);
    }

    public void panic() {
        throw new Error("ANTLR panic");
    }
    
    public void processArgs(String[] args) {
        if (verbose) {
            ErrorManager.info("ANTLR Parser Generator  Version " + VERSION);
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
                        ErrorManager.toolError(ErrorType.OUTPUT_DIR_IS_FILE, outputDirectory);
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
                        ErrorManager.toolError(ErrorType.DIR_NOT_FOUND, libDirectory);
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
            ErrorManager.toolError(ErrorType.CANNOT_OPEN_FILE, fileName, ioe);
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
            ANTLRParser p = new ANTLRParser(tokens);
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
            ErrorManager.internalError("can't generate this message at moment; antlr recovers");
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
        Grammar g = new Grammar(this, ast);
        g.fileName = grammarFileNames.get(0);
        process(g);
		if ( ast!=null && ast.grammarType==ANTLRParser.COMBINED && !ast.hasErrors ) {
			lexerAST = extractImplicitLexer(g); // alters ast
            Grammar lexerg = new Grammar(this, lexerAST);
            lexerg.fileName = grammarFileNames.get(0);
            g.implicitLexer = lexerg;
            lexerg.implicitLexerOwner = g;
            process(lexerg);
        }
    }

    protected void process(Grammar g) {
        grammars.put(g.name, g);
        g.loadImportedGrammars();
        if ( g.ast!=null && internalOption_PrintGrammarTree ) System.out.println(g.ast.toStringTree());
        //g.ast.inspect();

		// MAKE SURE GRAMMAR IS SEMANTICALLY CORRECT (FILL IN GRAMMAR OBJECT)
        SemanticPipeline sem = new SemanticPipeline();
        sem.process(g);
		if ( g.getImportedGrammars()!=null ) { // process imported grammars (if any)
			for (Grammar imp : g.getImportedGrammars()) {
				process(imp);
			}
		}

		// BUILD NFA FROM AST

		// PERFORM GRAMMAR ANALYSIS ON NFA: BUILD DECISION DFAs

		// GENERATE CODE
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
        rules.removeAll(rulesWeMoved);

		// Will track 'if' from IF : 'if' ; rules to avoid defining new token for 'if'
		Map<String,String> litAliases =
			Grammar.getStringLiteralAliasesFromLexerRules(lexerAST);

		// add strings from combined grammar (and imported grammars) into to lexer
		for (String lit : combinedGrammar.stringLiteralToTypeMap.keySet()) {
			if ( litAliases.containsKey(lit) ) continue; // already has rule
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

    private static void version() {
        ErrorManager.info("ANTLR Parser Generator  Version " + new Tool().VERSION);
    }

    private static void help() {
        ErrorManager.info("ANTLR Parser Generator  Version " + new Tool().VERSION);
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

    private static void Xhelp() {
        ErrorManager.info("ANTLR Parser Generator  Version " + new Tool().VERSION);
        System.err.println("  -Xgrtree                print the grammar AST");
        System.err.println("  -Xdfa                   print DFA as text ");
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

    /**
     * Returns the number of errors that the analysis/processing threw up.
     * @return Error count
     */
    public int getNumErrors() {
        return ErrorManager.getNumErrors();
    }

    /**
     * Set the message format to one of ANTLR, gnu, vs2005
     *
     * @param format
     */
    public void setMessageFormat(String format) {
        //ErrorManager.setFormat(format);
    }

    /**
     * Set the location (base directory) where output files should be produced
     * by the ANTLR tool.
     * @param outputDirectory
     */
    public void setOutputDirectory(String outputDirectory) {
        haveOutputDir = true;
        outputDirectory = outputDirectory;
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
