package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.OutputModelFactory;

import java.util.ArrayList;
import java.util.List;

/** */
public class ParserFile extends OutputModelObject {
	public String fileName;
	public Parser parser;
	public List<DFADecl> dfaDecls = new ArrayList<DFADecl>();
	public List<BitSetDecl> bitSetDecls = new ArrayList<BitSetDecl>();
	public String TokenLabelType;
	public String ASTLabelType;

	public ParserFile(OutputModelFactory factory, String fileName) {
		super(factory);
		this.fileName = fileName;
		factory.file = this;
		TokenLabelType = factory.gen.g.getOption("TokenLabelType");
		ASTLabelType = factory.gen.g.getOption("ASTLabelType");
		parser = new Parser(factory, this);
	}

	public void defineBitSet(BitSetDecl b) {
		bitSetDecls.add(b);
	}

	@Override
	public List<String> getChildren() {
		final List<String> sup = super.getChildren();
		return new ArrayList<String>() {{
			if ( sup!=null ) addAll(sup);
			add("parser");
			add("dfaDecls");
			add("bitSetDecls");
		}};
	}
}
