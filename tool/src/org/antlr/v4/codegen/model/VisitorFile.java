package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.runtime.misc.Triple;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.AltAST;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VisitorFile extends OutputFile {
	public String grammarName;
	public String parserName;
	public Set<String> visitorNames = new HashSet<String>();

	@ModelElement public Action header;

	public VisitorFile(OutputModelFactory factory, String fileName) {
		super(factory, fileName);
		Grammar g = factory.getGrammar();
		parserName = g.getRecognizerName();
		grammarName = g.name;
		for (Rule r : g.rules.values()) {
			List<Triple<Integer,AltAST,String>> labels = r.getAltLabels();
			if ( labels!=null ) {
				for (Triple<Integer,AltAST,String> pair : labels) {
					visitorNames.add(pair.c);
				}
			}
			else {
				// if labels, must label all. no need for generic rule visitor then
				visitorNames.add(r.name);
			}
		}
		ActionAST ast = g.namedActions.get("header");
		if ( ast!=null ) header = new Action(factory, ast);
	}
}
