package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.*;

import java.util.*;

public class Lexer extends OutputModelObject {
	public String name;
	public Map<String,Integer> tokens;
	public LexerFile file;
	public String[] tokenNames;
	public Set<String> ruleNames;
	public Collection<String> modes;

	@ModelElement public SerializedATN atn;
	@ModelElement public LinkedHashMap<Integer, Action> actions;
	@ModelElement public LinkedHashMap<Integer, Action> sempreds;

	public Lexer(OutputModelFactory factory, LexerFile file) {
		this.factory = factory;
		this.file = file; // who contains us?
		name = factory.g.getRecognizerName();
		tokens = new LinkedHashMap<String,Integer>();
		LexerGrammar lg = (LexerGrammar)factory.g;
		atn = new SerializedATN(factory, lg.atn);
		modes = lg.modes.keySet();

		for (String t : factory.g.tokenNameToTypeMap.keySet()) {
			Integer ttype = factory.g.tokenNameToTypeMap.get(t);
			if ( ttype>0 ) tokens.put(t, ttype);
		}

		tokenNames = factory.g.getTokenDisplayNames();
		ruleNames = factory.g.rules.keySet();

		sempreds = new LinkedHashMap<Integer, Action>();
		for (PredAST p : factory.g.sempreds.keySet()) {
			sempreds.put(factory.g.sempreds.get(p), new Action(factory, p));
		}
		actions = new LinkedHashMap<Integer, Action>();
		for (ActionAST a : factory.g.actions.keySet()) {
			actions.put(factory.g.actions.get(a), new Action(factory, a));
		}
	}

}
