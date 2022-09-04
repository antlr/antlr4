package org.antlr.v4.gui;

import org.antlr.runtime.RecognitionException;
import org.antlr.v4.Tool;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.DecisionInfo;
import org.antlr.v4.runtime.atn.ParseInfo;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.tool.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Interpret a lexer/parser, optionally printing tree string and dumping profile info
 *
 *  $ java org.antlr.v4.runtime.misc.Intrepreter [X.g4|XParser.g4 XLexer.g4] startRuleName inputFileName
 *        [-tree]
 *        [-gui]
 *        [-trace]
 *        [-encoding encoding]
 *        [-tokens]
 *        [-profile filename.csv]
 */
public class Interpreter {
	public static final String[] profilerColumnNames = {
			"Rule","Invocations", "Time (ms)", "Total k", "Max k", "Ambiguities", "DFA cache miss"
	};

	protected static class IgnoreTokenVocabGrammar extends Grammar {
		public IgnoreTokenVocabGrammar(String fileName,
									   String grammarText,
									   Grammar tokenVocabSource,
									   ANTLRToolListener listener)
				throws RecognitionException
		{
			super(fileName, grammarText, tokenVocabSource, listener);
		}

		@Override
		public void importTokensFromTokensFile() {
			// don't try to import tokens files; must give me both grammars if split
		}
	}

	protected String grammarFileName;
	protected String parserGrammarFileName;
	protected String lexerGrammarFileName;
	protected String startRuleName;
	protected boolean printTree = false;
	protected boolean gui = false;
	protected boolean trace = false;
	protected String encoding = null;
	protected boolean showTokens = false;
	protected String profileFileName = null;
	protected String inputFileName;

	public Interpreter(String[] args) throws Exception {
		if ( args.length < 2 ) {
			System.err.println("java org.antlr.v4.guIntrepreter [X.g4|XParser.g4 XLexer.g4] startRuleName\n" +
					"  [-tokens] [-tree] [-gui] [-encoding encodingname]\n" +
					"  [-trace] [-profile filename.csv] [input-filename(s)]");
			System.err.println("Omitting input-filename makes rig read from stdin.");
			return;
		}
		int i=0;
		grammarFileName = args[i];
		i++;
		if ( args[i].endsWith(".g4") ) {
			parserGrammarFileName = grammarFileName;
			lexerGrammarFileName = args[i];
			i++;
			grammarFileName = null;

			if ( parserGrammarFileName.toLowerCase().endsWith("lexer.g4") ) { // swap
				String save = parserGrammarFileName;
				parserGrammarFileName = lexerGrammarFileName;
				lexerGrammarFileName = save;
			}
		}
		startRuleName = args[i];
		i++;
		while ( i<args.length ) {
			String arg = args[i];
			i++;
			if ( arg.charAt(0)!='-' ) { // input file name
				inputFileName = arg;
			}
			else if ( arg.equals("-tree") ) {
				printTree = true;
			}
			else if ( arg.equals("-gui") ) {
				gui = true;
			}
			else if ( arg.equals("-tokens") ) {
				showTokens = true;
			}
			else if ( arg.equals("-trace") ) {
				trace = true;
			}
			else if ( arg.equals("-profile") ) {
				if ( i>=args.length ) {
					System.err.println("missing CSV filename on -profile (ignoring -profile)");
					return;
				}
				if ( args[i].startsWith("-") ) { // filename can't start with '-' since likely an arg
					System.err.println("missing CSV filename on -profile (ignoring -profile)");
					return;
				}
				profileFileName = args[i];
				if ( !profileFileName.endsWith(".csv") ) {
					System.err.println("warning: missing '.csv' suffix on -profile filename: "+profileFileName);
				}
				i++;
			}
			else if ( arg.equals("-encoding") ) {
				if ( i>=args.length ) {
					System.err.println("missing encoding on -encoding");
					return;
				}
				encoding = args[i];
				i++;
			}
		}
	}

	protected ParseInfo interp() throws RecognitionException, IOException {
		if ( grammarFileName==null && (parserGrammarFileName==null && lexerGrammarFileName==null) ) {
			return null;
		}
		Grammar g;
		LexerGrammar lg = null;
		DefaultToolListener listener = new DefaultToolListener(new Tool());
		if (grammarFileName != null) {
			String grammarContent = Files.readString(Path.of(grammarFileName));
			g = new IgnoreTokenVocabGrammar(grammarFileName, grammarContent, null, listener);
		}
		else {
			String lexerGrammarContent = Files.readString(Path.of(lexerGrammarFileName));
			lg = new LexerGrammar(lexerGrammarContent, listener);
			String parserGrammarContent = Files.readString(Path.of(parserGrammarFileName));
			g = new IgnoreTokenVocabGrammar(parserGrammarFileName, parserGrammarContent, lg, listener);
		}

		Charset charset = ( encoding == null ? Charset.defaultCharset () : Charset.forName(encoding) );
		CharStream charStream = null;
		if ( inputFileName==null ) {
			charStream = CharStreams.fromStream(System.in, charset);
		}
		else {
			try {
				charStream = CharStreams.fromPath(Paths.get(inputFileName), charset);
			}
			catch (NoSuchFileException nsfe) {
				System.err.println("Can't find input file "+inputFileName);
				System.exit(1);
			}
		}

		LexerInterpreter lexEngine = (lg!=null) ?
				lg.createLexerInterpreter(charStream) :
				g.createLexerInterpreter(charStream);

		CommonTokenStream tokens = new CommonTokenStream(lexEngine);

		tokens.fill();

		if ( showTokens ) {
			for (Token tok : tokens.getTokens()) {
				if ( tok instanceof CommonToken ) {
					System.out.println(((CommonToken)tok).toString(lexEngine));
				}
				else {
					System.out.println(tok.toString());
				}
			}
		}

		GrammarParserInterpreter parser = g.createGrammarParserInterpreter(tokens);
		if ( profileFileName!=null ) {
			parser.setProfile(true);
		}
		parser.setTrace(trace);

		Rule r = g.rules.get(startRuleName);
		if (r == null) {
			System.err.println("No such start rule: "+startRuleName);
			return null;
		}
		ParseTree t = parser.parse(r.index);
		ParseInfo parseInfo = parser.getParseInfo();

		if ( printTree ) {
			System.out.println(t.toStringTree(parser));
		}
		if ( gui ) {
			Trees.inspect(t, parser);
		}
		if ( profileFileName!=null ) {
			dumpProfilerCSV(parser, parseInfo);
		}

		return parseInfo;
	}

	private void dumpProfilerCSV(GrammarParserInterpreter parser, ParseInfo parseInfo) {
		String[] ruleNamesByDecision = new String[parser.getATN().decisionToState.size()];
		for(int i = 0; i < ruleNamesByDecision .length; i++) {
			ruleNamesByDecision [i] = parser.getRuleNames()[parser.getATN().getDecisionState(i).ruleIndex];
		}

		DecisionInfo[] decisionInfo = parseInfo.getDecisionInfo();
		String[][] table = new String[decisionInfo.length][profilerColumnNames.length];

		for (int decision = 0; decision < decisionInfo.length; decision++) {
			for (int col = 0; col < profilerColumnNames.length; col++) {
				Object colVal = getValue(decisionInfo[decision], ruleNamesByDecision, decision, col);
				table[decision][col] = colVal.toString();
			}
		}

		try {
			FileWriter fileWriter = new FileWriter(profileFileName);
			PrintWriter pw = new PrintWriter(fileWriter);

			for (int i = 0; i < profilerColumnNames.length; i++) {
				if (i > 0) pw.print(",");
				pw.print(profilerColumnNames[i]);
			}
			pw.println();
			for (String[] row : table) {
				for (int i = 0; i < profilerColumnNames.length; i++) {
					if (i > 0) pw.print(",");
					pw.print(row[i]);
				}
				pw.println();
			}
			pw.close();
		}
		catch (IOException ioe) {
			System.err.println("Error writing profile info to "+profileFileName+": "+ioe.getMessage());
		}
	}

	public static Object getValue(DecisionInfo decisionInfo,
								  String[] ruleNamesByDecision,
								  int decision,
								  int col)
	{
		switch (col) { // laborious but more efficient than reflection
			case 0:
				return  String.format("%s:%d",ruleNamesByDecision[decision],decision);
			case 1:
				return decisionInfo.invocations;
			case 2:
				return decisionInfo.timeInPrediction/(1000.0 * 1000.0);
			case 3:
				return decisionInfo.LL_TotalLook+decisionInfo.SLL_TotalLook;
			case 4:
				return Math.max(decisionInfo.LL_MaxLook, decisionInfo.SLL_MaxLook);
			case 5:
				return decisionInfo.ambiguities.size();
			case 6:
				return decisionInfo.SLL_ATNTransitions+
						decisionInfo.LL_ATNTransitions;
		}
		return "n/a";
	}

	public static void main(String[] args) throws Exception {
		Interpreter I = new Interpreter(args);
		I.interp();
	}
}
