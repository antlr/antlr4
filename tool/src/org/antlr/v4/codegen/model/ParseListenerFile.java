package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.runtime.misc.Tuple3;
import org.antlr.v4.tool.*;
import org.antlr.v4.tool.ast.*;

import java.util.*;

public class ParseListenerFile extends OutputFile {
	public String grammarName;
	public String parserName;
	public Set<String> listenerEnterNames = new HashSet<String>();
	public Set<String> listenerExitNames = new HashSet<String>();

	@ModelElement public Action header;

	public ParseListenerFile(OutputModelFactory factory, String fileName) {
		super(factory, fileName);
		Grammar g = factory.getGrammar();
		parserName = g.getRecognizerName();
		grammarName = g.name;
		for (Rule r : g.rules.values()) {
			List<Tuple3<Integer,AltAST,String>> labels = r.getAltLabels();
			// EXIT RULES
			if ( labels!=null ) {
				// add exit rules for alt labels
				for (Tuple3<Integer,AltAST,String> pair : labels) {
					listenerExitNames.add(pair.getItem3());
					if ( !(r instanceof LeftRecursiveRule) ) {
						listenerEnterNames.add(pair.getItem3());
					}
				}
			}
			else {
				// add exit rule if no labels
				listenerExitNames.add(r.name);
				if ( !(r instanceof LeftRecursiveRule) ) {
					listenerEnterNames.add(r.name);
				}
			}
		}
		ActionAST ast = g.namedActions.get("header");
		if ( ast!=null ) header = new Action(factory, ast);
	}
}
