package org.antlr.v4.codegen;

import org.antlr.v4.runtime.pda.PDA;
import org.antlr.v4.tool.LexerGrammar;
import org.stringtemplate.v4.ST;

/** */
public class LexerFactory {
	public CodeGenerator gen;
	public LexerFactory(CodeGenerator gen) {
		this.gen = gen;
	}

	public ST build() {
		LexerGrammar lg = (LexerGrammar)gen.g;
		ST fileST = gen.templates.getInstanceOf("LexerFile");
		ST lexerST = gen.templates.getInstanceOf("Lexer");
		lexerST.add("lexerName", gen.g.getRecognizerName());
		lexerST.add("modes", lg.modes.keySet());
		fileST.add("fileName", gen.getRecognizerFileName());
		fileST.add("lexer", lexerST);
		for (String modeName : lg.modes.keySet()) { // for each mode
			PDA pda = NFABytecodeGenerator.getBytecode(lg, modeName);
			ST pdaST = gen.templates.getInstanceOf("PDA");
			pdaST.add("name", modeName);
			pdaST.add("model", pda);
			lexerST.add("pdas", pdaST);
		}
		return fileST;
	}
}
