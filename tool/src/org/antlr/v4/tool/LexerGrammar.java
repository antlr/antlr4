package org.antlr.v4.tool;

import org.antlr.misc.MultiMap;
import org.antlr.runtime.RecognitionException;
import org.antlr.v4.Tool;
import org.antlr.v4.automata.DFA;

import java.util.HashMap;
import java.util.Map;

/** */
public class LexerGrammar extends Grammar {
	public static final String DEFAULT_MODE_NAME = "DEFAULT_MODE";

	public MultiMap<String, Rule> modes = new MultiMap<String, Rule>();

	public Map<String, DFA> modeToDFA = new HashMap<String, DFA>();	

	public LexerGrammar(Tool tool, GrammarRootAST ast) {
		super(tool, ast);
	}

	public LexerGrammar(String grammarText) throws RecognitionException {
		super(grammarText);
	}

	public LexerGrammar(String grammarText, ANTLRToolListener listener) throws RecognitionException {
		super(grammarText, listener);
	}

	public LexerGrammar(String fileName, String grammarText, ANTLRToolListener listener) throws RecognitionException {
		super(fileName, grammarText, listener);
	}

	@Override
	public void defineRule(Rule r) {
		super.defineRule(r);
		modes.map(r.mode, r);
	}
}
