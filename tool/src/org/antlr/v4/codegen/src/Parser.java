package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.Rule;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** */
public class Parser extends OutputModelObject {
	public String name;
	public Map<String,Integer> tokens;
	public List<RuleFunction> funcs = new ArrayList<RuleFunction>();
	ParserFile file;

	public Parser(OutputModelFactory factory, ParserFile file) {
		this.factory = factory;
		this.file = file; // who contains us?
		name = factory.g.getRecognizerName();
		tokens = new LinkedHashMap<String,Integer>();
		for (String t : factory.g.tokenNameToTypeMap.keySet()) {
			Integer ttype = factory.g.tokenNameToTypeMap.get(t);
			if ( ttype>0 ) tokens.put(t, ttype);
		}
		for (Rule r : factory.g.rules.values()) funcs.add( new RuleFunction(factory, r) );
	}

	@Override
	public List<String> getChildren() {
		final List<String> sup = super.getChildren();
		return new ArrayList<String>() {{ if ( sup!=null ) addAll(sup); add("funcs"); }};
	}
}
