package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.Triple;
import org.antlr.v4.tool.*;
import org.antlr.v4.tool.ast.*;

import java.util.*;

/** A model object representing a parse tree listener file.
 *  These are the rules specific events triggered by a parse tree visitor.
 */
public class ListenerFile extends OutputFile {
	public String grammarName;
	public String parserName;
	public Set<String> listenerNames = new HashSet<String>();
//	public List<String> ruleNames = new ArrayList<String>();

	@ModelElement public Action header;

	public ListenerFile(OutputModelFactory factory, String fileName) {
		super(factory, fileName);
		Grammar g = factory.getGrammar();
		parserName = g.getRecognizerName();
		grammarName = g.name;
		for (Rule r : g.rules.values()) {
			List<Triple<Integer,AltAST,String>> labels = r.getAltLabels();
			listenerNames.add(r.name);
			if ( labels!=null ) {
				for (Triple<Integer,AltAST,String> pair : labels) {
					listenerNames.add(pair.c);
				}
			}
		}
		ActionAST ast = g.namedActions.get("header");
		if ( ast!=null ) header = new Action(factory, ast);
	}
}
