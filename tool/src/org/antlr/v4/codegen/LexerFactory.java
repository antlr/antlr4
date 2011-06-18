package org.antlr.v4.codegen;

import org.antlr.v4.codegen.model.*;
import org.antlr.v4.tool.*;
import org.stringtemplate.v4.ST;

import java.util.*;

/** */
public class LexerFactory extends OutputModelFactory {
	public LexerFactory(CodeGenerator gen) {
		super(gen);
	}

	@Override
	public OutputModelObject buildOutputModel() {
		return new LexerFile(this, gen.getRecognizerFileName());
	}

	public ST build() {
		LexerGrammar lg = (LexerGrammar)gen.g;
		ST fileST = gen.templates.getInstanceOf("LexerFile");
		ST lexerST = gen.templates.getInstanceOf("Lexer");
		lexerST.add("lexerName", gen.g.getRecognizerName());
		lexerST.add("modes", lg.modes.keySet());
		fileST.add("fileName", gen.getRecognizerFileName());
		fileST.add("lexer", lexerST);

		SerializedATN atn = new SerializedATN(this, lg.atn);

		for (String modeName : lg.modes.keySet()) { // for each mode

//			injectDFAs(lg, lexerST, modeName);
//			LexerCompiler comp = new LexerCompiler(lg);
//			CompiledATN atn = comp.compileMode(modeName);
//			injectPDAs(atn, lexerST, modeName);
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

//	void injectDFAs(LexerGrammar lg, ST lexerST, String modeName) {
//		System.out.println("inject dfa for "+modeName);
//		DFA dfa = lg.modeToDFA.get(modeName);
//		ST dfaST = gen.templates.getInstanceOf("DFA");
//		dfaST.add("name", modeName);
//		CompiledDFA obj = new CompiledDFA(dfa);
//		dfaST.add("model", obj);
////		ST actionST = gen.templates.getInstanceOf("actionMethod");
////		actionST.add("name", modeName);
////		actionST.add("actions", obj.actions);
////		lexerST.add("actions", actionST);
//		lexerST.add("dfas", dfaST);
//	}

	/*
	void injectPDAs(CompiledATN atn, ST lexerST, String modeName) {
		ST pdaST = gen.templates.getInstanceOf("ATN");
		for (Rule r : atn.ruleActions.keySet()) {
			Set<Token> actionTokens = atn.ruleActions.keySet(r);
			ST actionST = gen.templates.getInstanceOf("actionMethod");
			actionST.add("name", r.name);
			for (Token t : actionTokens) {
				actionST.add("actions", Misc.strip(t.getText(),1));
				actionST.add("ruleIndex", r.index);
			}
			pdaST.add("actions", actionST);
			lexerST.add("actions", actionST);
		}
		for (Rule r : atn.ruleSempreds.keySet()) {
			Set<Token> sempredTokens = atn.ruleSempreds.keySet(r);
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
		pdaST.add("model", atn);
		lexerST.add("atns", pdaST);
	}
	*/

	// lexers don't do anything with rules etc...

	@Override
	public List<SrcOp> ruleRef(GrammarAST ID, GrammarAST label, GrammarAST args) {
		return null;
	}

	@Override
	public List<SrcOp> tokenRef(GrammarAST ID, GrammarAST label, GrammarAST args) {
		return null;
	}

	@Override
	public List<SrcOp> stringRef(GrammarAST ID, GrammarAST label) {
		return null;
	}

	@Override
	public void defineBitSet(BitSetDecl b) {
	}
}
