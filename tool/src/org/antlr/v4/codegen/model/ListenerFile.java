package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.ActionAST;

import java.util.ArrayList;
import java.util.List;

/** A model object representing a parse tree listener file.
 *  These are the rules specific events triggered by a parse tree visitor.
 */
public class ListenerFile extends OutputFile {
	public String grammarName;
	public String parserName;
	public List<String> listenerNames = new ArrayList<String>();
//	public List<String> ruleNames = new ArrayList<String>();

	@ModelElement public Action header;

	public ListenerFile(OutputModelFactory factory, String fileName) {
		super(factory, fileName);
		Grammar g = factory.getGrammar();
		parserName = g.getRecognizerName();
		grammarName = g.name;
		for (Rule r : g.rules.values()) {
			List<String> labels = r.getAltLabels();
			if ( labels==null ) {
				listenerNames.add(r.name);
			}
			else { // alt(s) with label(s)
				for (String label : labels) {
					listenerNames.add(label);
				}
			}
		}
		ActionAST ast = g.namedActions.get("header");
		if ( ast!=null ) header = new Action(factory, ast);
	}
}
