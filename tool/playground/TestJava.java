import org.antlr.runtime.debug.BlankDebugEventListener;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionMode;

import java.io.File;

/** Parse a java file or directory of java files using the generated parser
 *  ANTLR builds from java.g4
 */
class TestJava {
	public static long lexerTime = 0;
	public static boolean profile = false;
	public static JavaLexer lexer;
	public static JavaParser parser = null;
	public static boolean showTree = false;
	public static boolean printTree = false;
	public static boolean SLL = false;
	public static boolean diag = false;

	public static void main(String[] args) {
        doAll(args);
//        doAll(args);
//        doAll(args);
    }

    static void doAll(String[] args) {
        try {
            lexerTime = 0;
            long start = System.currentTimeMillis();
            if (args.length > 0 ) {
                // for each directory/file specified on the command line
                for(int i=0; i< args.length;i++) {
					if ( args[i].equals("-tree") ) showTree = true;
					else if ( args[i].equals("-ptree") ) printTree = true;
					else if ( args[i].equals("-SLL") ) SLL = true;
					else if ( args[i].equals("-diag") ) diag = true;
                    doFile(new File(args[i])); // parse it
                }
            }
            else {
                System.err.println("Usage: java Main <directory or file name>");
            }
            long stop = System.currentTimeMillis();
            System.out.println("Lexer total time " + lexerTime + "ms.");
            System.out.println("Total time " + (stop - start) + "ms.");

            System.out.println("finished parsing OK");
            System.out.println(LexerATNSimulator.ATN_failover+" lexer failovers");
            System.out.println(LexerATNSimulator.match_calls+" lexer match calls");
            System.out.println(ParserATNSimulator.ATN_failover+" parser failovers");
            System.out.println(ParserATNSimulator.predict_calls +" parser predict calls");
            System.out.println(ParserATNSimulator.retry_with_context +" retry_with_context after SLL conflict");
            System.out.println(ParserATNSimulator.retry_with_context_indicates_no_conflict +" retry sees no conflict");
            if ( profile ) {
                System.out.println("num decisions "+profiler.numDecisions);
            }
        }
        catch(Exception e) {
            System.err.println("exception: "+e);
            e.printStackTrace(System.err);   // so we can get stack trace
        }
    }


    // This method decides what action to take based on the type of
	//   file we are looking at
	public static void doFile(File f)
							  throws Exception {
		// If this is a directory, walk each file/dir in that directory
		if (f.isDirectory()) {
			String files[] = f.list();
			for(int i=0; i < files.length; i++)
				doFile(new File(f, files[i]));
		}

		// otherwise, if this is a java file, parse it!
		else if ( ((f.getName().length()>5) &&
				f.getName().substring(f.getName().length()-5).equals(".java"))
			|| f.getName().equals("input") )
		{
			System.err.println("parsing "+f.getAbsolutePath());
			parseFile(f.getAbsolutePath());
		}
	}

	static class CountDecisions extends BlankDebugEventListener {
		public int numDecisions = 0;
		public void enterDecision(int decisionNumber) {
			numDecisions++;
		}
	}
	static CountDecisions profiler = new CountDecisions();

	// Here's where we do the real work...
	public static void parseFile(String f)
								 throws Exception {
		try {
			// Create a scanner that reads from the input stream passed to us
			if ( lexer==null ) {
				lexer = new JavaLexer(null);
			}
			lexer.setInputStream(new ANTLRFileStream(f));

			CommonTokenStream tokens = new CommonTokenStream(lexer);
			long start = System.currentTimeMillis();
			tokens.fill();
//			System.out.println(tokens.getTokens());
			long stop = System.currentTimeMillis();
			lexerTime += stop-start;
//			for (Object t : tokens.getTokens()) {
//				System.out.println(t);
//			}

			if ( true ) {
				// Create a parser that reads from the scanner
				if ( parser==null ) {
					parser = new JavaParser(tokens);
//                    parser.getInterpreter().setContextSensitive(false);
//                    parser.setErrorHandler(new BailErrorStrategy<Token>());
//					parser.getInterpreter().setContextSensitive(true);
				}
				parser.setTokenStream(tokens);

				if ( diag ) parser.addErrorListener(new DiagnosticErrorListener());
				if ( SLL ) parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
				// start parsing at the compilationUnit rule
				ParserRuleContext<Token> tree = parser.compilationUnit();
				if ( showTree ) tree.inspect(parser);
				if ( printTree ) System.out.println(tree.toStringTree(parser));

				//System.err.println("finished "+f);
//                System.out.println("cache size = "+DefaultErrorStrategy.cache.size());
			}
		}
		catch (Exception e) {
			System.err.println("parser exception: "+e);
			e.printStackTrace();   // so we can get stack trace
		}
	}

}

