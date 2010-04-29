package org.antlr.v4.codegen;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.v4.runtime.nfa.NFA;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;

public class CodeGenPipeline {
	Grammar g;
	public CodeGenPipeline(Grammar g) {
		this.g = g;
	}
	public void process() {
		if ( g.isLexer() ) processLexer();
	}

	void processLexer() {
		LexerGrammar lg = (LexerGrammar)g;
		for (String modeName : lg.modes.keySet()) { // for each mode
			NFA nfa = NFABytecodeGenerator.getBytecode(lg, modeName);
			ANTLRStringStream input = new ANTLRStringStream("32ab");
			int ttype = nfa.exec(input); System.out.println("ttype="+ttype);
			ttype = nfa.exec(input); System.out.println("ttype="+ttype);
		}
	}
}
