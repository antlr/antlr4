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
	public List<RuleFunction> funcs = new ArrayList<RuleFunction>();
	public List<DynamicScopeStruct> scopes = new ArrayList<DynamicScopeStruct>();
	public ParserFile file;
	public SerializedATN atn;
	public LinkedHashMap<Integer, ForcedAction> actions;
	public LinkedHashMap<Integer, Action> sempreds;

	public Parser(OutputModelFactory factory, ParserFile file) {
		this.factory = factory;
		this.file = file; // who contains us?
		name = factory.g.getRecognizerName();
		tokens = new LinkedHashMap<String,Integer>();
		//tokens.putAll( factory.g.tokenNameToTypeMap );
		for (String t : factory.g.tokenNameToTypeMap.keySet()) {
			Integer ttype = factory.g.tokenNameToTypeMap.get(t);
			if ( ttype>0 ) tokens.put(t, ttype);
		}
//		int numTokens = factory.g.getTokenTypes().size();
//		for (int t=Token.MIN_TOKEN_TYPE; t<numTokens; t++) {
//			String lit = factory.g.typeToStringLiteralList.get(t);
//			if ( lit!=null ) tokens.put(lit, t);
//			tokens.put(factory.g.typeToTokenList.get(t), t);
//		}
		tokenNames = factory.g.getTokenDisplayNames();
		ruleNames = factory.g.rules.keySet();
		for (AttributeDict d : factory.g.scopes.values()) {
			scopes.add( new DynamicScopeStruct(factory, d.name, d.attributes.values()) );
		}
		for (Rule r : factory.g.rules.values()) {
			//if ( r.isStartRule ) funcs.add( new StartRuleFunction(factory, r) );
			funcs.add( new RuleFunction(factory, r) );
		}
		atn = new SerializedATN(factory, factory.g.atn);

		sempreds = new LinkedHashMap<Integer, Action>();
		for (PredAST p : factory.g.sempreds.keySet()) {
			sempreds.put(factory.g.sempreds.get(p), new Action(factory, p));
		}
		actions = new LinkedHashMap<Integer, ForcedAction>();
		for (ActionAST a : factory.g.actions.keySet()) {
			actions.put(factory.g.actions.get(a), new ForcedAction(factory, a));
		}
	}

//	@Override
//	public List<String> getChildren() {
//		final List<String> sup = super.getChildren();
//		return new ArrayList<String>() {{ if ( sup!=null ) addAll(sup); add("funcs"); add("scopes"); }};
//	}
}
