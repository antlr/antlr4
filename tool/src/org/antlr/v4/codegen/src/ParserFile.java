package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.GrammarAST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** */
public class ParserFile extends OutputModelObject {
	public String fileName;
	public Parser parser;
	public List<DFADecl> dfaDecls = new ArrayList<DFADecl>();
	public List<BitSetDecl> bitSetDecls = new ArrayList<BitSetDecl>();
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

	public void defineBitSet(BitSetDecl b) {
		bitSetDecls.add(b);
	}

//	@Override
//	public List<String> getChildren() {
//		final List<String> sup = super.getChildren();
//		return new ArrayList<String>() {{
//			if ( sup!=null ) addAll(sup);
//			add("parser");
//			add("dfaDecls");
//			add("namedActions");
//			add("bitSetDecls");
//		}};
//	}
}
