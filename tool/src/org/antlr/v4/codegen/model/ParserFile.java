package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.GrammarAST;

import java.util.*;

/** */
public class ParserFile extends OutputModelObject {
	public String fileName;
	public Parser parser;
//	public List<DFADecl> dfaDecls = new ArrayList<DFADecl>();
//	public OrderedHashSet<BitSetDecl> bitSetDecls = new OrderedHashSet<BitSetDecl>();
	public String TokenLabelType;
	public String ASTLabelType;
	public Map<String, Action> namedActions;

	public ParserFile(OutputModelFactory factory, String fileName) {
		super(factory);
		this.fileName = fileName;
		factory.file = this;
		TokenLabelType = factory.gen.g.getOption("TokenLabelType");
		ASTLabelType = factory.gen.g.getOption("ASTLabelType");
		namedActions = new HashMap<String, Action>();
		for (String name : factory.gen.g.namedActions.keySet()) {
			GrammarAST ast = factory.gen.g.namedActions.get(name);
			namedActions.put(name, new Action(factory, ast));
		}
		parser = new Parser(factory, this);
	}

//	public void defineBitSet(BitSetDecl b) {
//		bitSetDecls.add(b);
//	}
}
