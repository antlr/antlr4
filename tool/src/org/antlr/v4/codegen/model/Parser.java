package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.*;

import java.util.*;

/** */
public class Parser extends OutputModelObject {
	public String name;
	public Map<String,Integer> tokens;
	public String[] tokenNames;
	public Set<String> ruleNames;
	public ParserFile file;

	@ModelElement public List<RuleFunction> funcs = new ArrayList<RuleFunction>();
	@ModelElement public SerializedATN atn;
	@ModelElement public LinkedHashMap<Integer, ForcedAction> actions;
	@ModelElement public LinkedHashMap<Integer, Action> sempreds;

	public Parser(OutputModelFactory factory, ParserFile file) {
		this.factory = factory;
		this.file = file; // who contains us?
		Grammar g = factory.getGrammar();
		name = g.getRecognizerName();
		tokens = new LinkedHashMap<String,Integer>();
		//tokens.putAll( g.tokenNameToTypeMap );
		for (String t : g.tokenNameToTypeMap.keySet()) {
			Integer ttype = g.tokenNameToTypeMap.get(t);
			if ( ttype>0 ) tokens.put(t, ttype);
		}
//		int numTokens = g.getTokenTypes().size();
//		for (int t=Token.MIN_TOKEN_TYPE; t<numTokens; t++) {
//			String lit = g.typeToStringLiteralList.get(t);
//			if ( lit!=null ) tokens.put(lit, t);
//			tokens.put(g.typeToTokenList.get(t), t);
//		}
		tokenNames = g.getTokenDisplayNames();
		ruleNames = g.rules.keySet();
		atn = new SerializedATN(factory, g.atn);

		sempreds = new LinkedHashMap<Integer, Action>();
		for (PredAST p : g.sempreds.keySet()) {
			sempreds.put(g.sempreds.get(p), new Action(factory, p));
		}
		actions = new LinkedHashMap<Integer, ForcedAction>();
		for (ActionAST a : g.actions.keySet()) {
			actions.put(g.actions.get(a), new ForcedAction(factory, a));
		}
	}
}
