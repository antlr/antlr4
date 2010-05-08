package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.CodeGenerator;

import java.util.ArrayList;
import java.util.List;

/** */
public class ParserFile extends OutputModelObject {
	public String fileName;
	public Parser parser;
	
	public ParserFile(CodeGenerator gen, Parser p, String fileName) {
		this.gen = gen;
		parser = p;
		this.fileName = fileName; 
	}

	@Override
	public List<String> getChildren() {
		return new ArrayList<String>() {{ add("parser"); }};
	}
}
