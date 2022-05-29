package org.antlr.v4.gui;

import org.antlr.runtime.RecognitionException;
import org.antlr.v4.Tool;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.DecisionInfo;
import org.antlr.v4.runtime.atn.ParseInfo;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.tool.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Run a lexer/parser combo, optionally printing tree string or generating
 *  postscript file. Optionally taking input file.
 *
 *  $ java org.antlr.v4.runtime.misc.TestRig X.g4 startRuleName inputFileName
 *        [-tree]
 *        [-tokens]
 *        [-profile]
 *
 *  $ java org.antlr.v4.runtime.misc.TestRig XParser.g4 XLexer.g4 startRuleName inputFileName
 *        [-tree]
 *        [-tokens]
 *        [-profile]
 */
public class Intrepreter {
	public static final String[] profilerColumnNames = {
			"Rule","Invocations", "Time", "Total k", "Max k", "Ambiguities", "DFA cache miss"
	};

	protected String grammarFileName;
	protected String parserGrammarFileName;
	protected String lexerGrammarFileName;
	protected String startRuleName;
	protected boolean printTree = false;
	protected boolean gui = false;
	protected String encoding = null;
	protected boolean showTokens = false;
	protected boolean profile = false;
	protected String inputFileName;

	public Intrepreter(String[] args) throws Exception {
		if ( args.length < 2 ) {
			System.err.println("java org.antlr.v4.gui.Intrepreter GrammarFileName startRuleName\n" +
					"  [-tokens] [-tree] [-gui] [-ps file.ps] [-encoding encodingname]\n" +
					"  [-trace] [-diagnostics] [-SLL]\n"+
					"  [input-filename(s)]");
			System.err.println("Use startRuleName='tokens' if GrammarName is a lexer grammar.");
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
			else if ( arg.equals("-profile") ) {
				profile = true;
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

	protected ParseInfo interp(Grammar g, LexerGrammar lg) throws RecognitionException, IOException {
		Charset charset = ( encoding == null ? Charset.defaultCharset () : Charset.forName(encoding) );
		CharStream charStream = CharStreams.fromPath(Paths.get(inputFileName), charset);
		LexerInterpreter lexEngine = (lg!=null) ?
				lg.createLexerInterpreter(charStream) :
				g.createLexerInterpreter(charStream);

		CommonTokenStream tokens = new CommonTokenStream(lexEngine);
		GrammarParserInterpreter parser = g.createGrammarParserInterpreter(tokens);
		parser.setProfile(true);

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
		if ( profile ) {
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
		for (int i = 0; i < profilerColumnNames.length; i++) {
			if ( i>0 ) System.out.print(",");
			System.out.print(profilerColumnNames[i]);
		}
		System.out.println();
		for (String[] row : table) {
			for (int i = 0; i < profilerColumnNames.length; i++) {
				if ( i>0 ) System.out.print(",");
				System.out.print(row[i]);
			}
			System.out.println();
		}
	}

	protected static Object getValue(DecisionInfo decisionInfo,
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
		Intrepreter I = new Intrepreter(args);
		DefaultToolListener listener = new DefaultToolListener(new Tool());

		if (I.grammarFileName != null) {

			String grammarContent = Files.readString(Path.of(I.grammarFileName));
			Grammar g = new Grammar(I.grammarFileName, grammarContent, null, listener);
			ParseInfo parseInfo = I.interp(g, null);
		}
		else {
			String lexerGrammarContent = Files.readString(Path.of(I.lexerGrammarFileName));
			LexerGrammar lg = new LexerGrammar(lexerGrammarContent, listener);
			String parserGrammarContent = Files.readString(Path.of(I.parserGrammarFileName));
			Grammar g = new Grammar(I.parserGrammarFileName, parserGrammarContent, lg, listener);
			ParseInfo parseInfo = I.interp(g, lg);
		}
//		System.out.println(Arrays.toString(info));
	}
}
