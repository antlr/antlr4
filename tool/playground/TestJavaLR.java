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

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.ParserATNSimulator;

import java.io.File;

class TestJavaLR {
	public static long lexerTime = 0;
	public static boolean profile = false;
	public static JavaLRLexer lexer;
	public static JavaLRParser parser = null;
	public static boolean showTree = false;
	public static boolean printTree = false;
	public static boolean SLL = false;
	public static boolean diag = false;

	public static void main(String[] args) {
		doAll(args);
//        doAll(args);
	}

	public static void doAll(String[] args) {
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
			System.out.println(ParserATNSimulator.retry_with_context_predicts_same_as_alt +" retry predicts same alt as resolving conflict");
			System.out.println(ParserATNSimulator.retry_with_context_from_dfa +" retry from DFA");
		}
		catch(Exception e) {
			System.err.println("exception: "+e);
			e.printStackTrace(System.err);   // so we can get stack trace
		}
	}


	// This method decides what action to take based on the type of
	//   file we are looking at
	public static void doFile(File f) throws Exception {
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
			System.err.println(f.getAbsolutePath());
			parseFile(f.getAbsolutePath());
		}
	}

	// Here's where we do the real work...
	public static void parseFile(String f)
								 throws Exception {
		try {
			// Create a scanner that reads from the input stream passed to us
			if ( lexer==null ) {
				lexer = new JavaLRLexer(null);
			}
			lexer.setInputStream(new ANTLRFileStream(f));

			CommonTokenStream tokens = new CommonTokenStream(lexer);
			long start = System.currentTimeMillis();
			tokens.fill(); // load all and check time
//			System.out.println(tokens.getTokens());
			long stop = System.currentTimeMillis();
			lexerTime += stop-start;

			if ( true ) {
				// Create a parser that reads from the scanner
				if ( parser==null ) {
					parser = new JavaLRParser(null);
//                    parser.setErrorHandler(new BailErrorStrategy<Token>());
//					parser.getInterpreter().setContextSensitive(true);
				}

				parser.setTokenStream(tokens);
				if ( diag ) parser.addErrorListener(new DiagnosticErrorListener());
				if ( SLL ) parser.getInterpreter().SLL = true;
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

