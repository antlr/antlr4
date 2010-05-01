package org.antlr.v4.codegen;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;
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
			//ANTLRStringStream input = new ANTLRStringStream("32");
			ANTLRStringStream input = new ANTLRStringStream("/*x*/!ab");
			//ANTLRStringStream input = new ANTLRStringStream("abc32ab");
			int ttype = 0;
			while ( ttype!= Token.EOF ) {
				ttype = nfa.execThompson(input); System.out.println("ttype="+ttype);
			}
		}
	}
}
