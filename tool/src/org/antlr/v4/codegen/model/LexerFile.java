package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.GrammarAST;

import java.util.*;

public class LexerFile extends OutputModelObject {
	public String fileName;
	public Lexer lexer;
	public Map<String, Action> namedActions;

	public LexerFile(OutputModelFactory factory, String fileName) {
		super(factory);
		this.fileName = fileName;
		factory.file = this;
		namedActions = new HashMap<String, Action>();
		for (String name : factory.gen.g.namedActions.keySet()) {
			GrammarAST ast = factory.gen.g.namedActions.get(name);
			namedActions.put(name, new Action(factory, ast));
		}
		lexer = new Lexer(factory, this);
	}
}