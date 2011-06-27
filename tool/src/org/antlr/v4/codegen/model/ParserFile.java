package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.*;

import java.util.*;

/** */
public class ParserFile extends OutputModelObject {
	public String fileName;
	public String TokenLabelType;
	public String ASTLabelType;

	@ModelElement public Parser parser;
	@ModelElement public Map<String, Action> namedActions;

	public ParserFile(OutputModelFactory factory, String fileName) {
		super(factory);
		this.fileName = fileName;
		Grammar g = factory.getGrammar();
		TokenLabelType = g.getOption("TokenLabelType");
		ASTLabelType = g.getOption("ASTLabelType");
		namedActions = new HashMap<String, Action>();
		for (String name : g.namedActions.keySet()) {
			GrammarAST ast = g.namedActions.get(name);
			namedActions.put(name, new Action(factory, ast));
		}
	}
}
