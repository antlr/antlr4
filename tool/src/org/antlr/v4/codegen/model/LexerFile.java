package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.*;

import java.util.*;

public class LexerFile extends OutputModelObject {
	public String fileName;

	@ModelElement public Lexer lexer;
	@ModelElement public Map<String, Action> namedActions;

	public LexerFile(OutputModelFactory factory, String fileName) {
		super(factory);
		this.fileName = fileName;
		namedActions = new HashMap<String, Action>();
		Grammar g = factory.getGrammar();
		for (String name : g.namedActions.keySet()) {
			GrammarAST ast = g.namedActions.get(name);
			namedActions.put(name, new Action(factory, ast));
		}
	}
}