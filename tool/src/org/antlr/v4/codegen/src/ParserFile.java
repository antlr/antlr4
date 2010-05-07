package org.antlr.v4.codegen.src;

import java.util.ArrayList;
import java.util.List;

/** */
public class ParserFile extends OutputModelObject {
	public String fileName;
	public Parser parser;
	public ParserFile(Parser p, String fileName) { parser = p; this.fileName = fileName; }

	@Override
	public List<String> getChildren() {
		return new ArrayList<String>() {{ add("parser"); }};
	}
}
