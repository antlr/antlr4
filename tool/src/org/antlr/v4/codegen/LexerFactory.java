package org.antlr.v4.codegen;

import org.antlr.v4.codegen.model.*;

/** */
public class LexerFactory extends CoreOutputModelFactory {
	public LexerFactory(CodeGenerator gen) { super(gen); }

	public OutputModelObject buildOutputModel() {
		return new LexerFile(this, gen.getRecognizerFileName());
	}

	/*
	public ST build() {
		LexerGrammar lg = (LexerGrammar)gen.g;
		ST fileST = gen.templates.getInstanceOf("LexerFile");
		ST lexerST = gen.templates.getInstanceOf("Lexer");
		lexerST.add("lexerName", gen.g.getRecognizerName());
		lexerST.add("modes", lg.modes.keySet());
		fileST.add("fileName", gen.getRecognizerFileName());
		fileST.add("lexer", lexerST);

		LinkedHashMap<String,Integer> tokens = new LinkedHashMap<String,Integer>();
		for (String t : gen.g.tokenNameToTypeMap.keySet()) {
			Integer ttype = gen.g.tokenNameToTypeMap.get(t);
			if ( ttype>0 ) tokens.put(t, ttype);
		}
		lexerST.add("tokens", tokens);
		lexerST.add("namedActions", gen.g.namedActions);

		return fileST;
	}
*/
}
