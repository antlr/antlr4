package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.CodeGenerator;

import java.util.ArrayList;
import java.util.List;

/** */
public class ParserFile extends OutputModelObject {
	public String fileName;
	public Parser parser;
	public List<DFADef> dfaDefs = new ArrayList<DFADef>();
	public List<BitSetDef> bitSetDefs = new ArrayList<BitSetDef>();
	
	public ParserFile(CodeGenerator gen, String fileName) {
		this.gen = gen;
		this.fileName = fileName; 
	}

	@Override
	public List<String> getChildren() {
		return new ArrayList<String>() {{
			add("parser");
			add("dfaDefs");
			add("bitSetDefs");
		}};
	}
}
