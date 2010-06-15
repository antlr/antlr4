package org.antlr.v4.codegen;

import org.antlr.runtime.Token;
import org.antlr.v4.automata.DFA;
import org.antlr.v4.tool.LexerGrammar;
import org.antlr.v4.tool.Rule;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.misc.Misc;

import java.util.LinkedHashMap;
import java.util.Set;

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
			injectDFAs(lg, lexerST, modeName);
			//injectPDAs(lg, lexerST, modeName);
		}

		LinkedHashMap<String,Integer> tokens = new LinkedHashMap<String,Integer>();
		for (String t : gen.g.tokenNameToTypeMap.keySet()) {
			Integer ttype = gen.g.tokenNameToTypeMap.get(t);
			if ( ttype>0 ) tokens.put(t, ttype);
		}
		lexerST.add("tokens", tokens);
		lexerST.add("namedActions", gen.g.namedActions);

		return fileST;
	}

	void injectDFAs(LexerGrammar lg, ST lexerST, String modeName) {
		System.out.println("inject dfa for "+modeName);
		DFA dfa = lg.modeToDFA.get(modeName);
		ST dfaST = gen.templates.getInstanceOf("DFA");
		dfaST.add("name", modeName);
		CompiledDFA obj = new CompiledDFA(dfa);
		dfaST.add("model", obj);
//		ST actionST = gen.templates.getInstanceOf("actionMethod");
//		actionST.add("name", modeName);
//		actionST.add("actions", obj.actions);
//		lexerST.add("actions", actionST);
		lexerST.add("dfas", dfaST);
	}

	void injectPDAs(LexerGrammar lg, ST lexerST, String modeName) {
		LexerCompiler comp = new LexerCompiler(lg);
		CompiledPDA pda = comp.compileMode(modeName);
		ST pdaST = gen.templates.getInstanceOf("PDA");
		for (Rule r : pda.ruleActions.keySet()) {
			Set<Token> actionTokens = pda.ruleActions.keySet(r);
			ST actionST = gen.templates.getInstanceOf("actionMethod");
			actionST.add("name", r.name);
			for (Token t : actionTokens) {
				actionST.add("actions", Misc.strip(t.getText(),1));
				actionST.add("ruleIndex", r.index);
			}
			pdaST.add("actions", actionST);
			lexerST.add("actions", actionST);
		}
		for (Rule r : pda.ruleSempreds.keySet()) {
			Set<Token> sempredTokens = pda.ruleSempreds.keySet(r);
			ST sempredST = gen.templates.getInstanceOf("sempredMethod");
			sempredST.add("name", r.name);
			sempredST.add("ruleIndex", r.index);
			for (Token t : sempredTokens) {
				sempredST.add("preds", t.getText());
			}
			pdaST.add("sempreds", sempredST);
			lexerST.add("sempreds", sempredST);
		}
		pdaST.add("name", modeName);
		pdaST.add("model", pda);
		lexerST.add("pdas", pdaST);
	}
}
