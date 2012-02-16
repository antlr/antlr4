package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.Triple;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.AltAST;

import java.util.List;

public class ListenerDispatchMethod extends OutputModelObject {
	public String listenerName = "Rule";
	public boolean isEnter;

	public ListenerDispatchMethod(OutputModelFactory factory, Rule r, boolean isEnter) {
		super(factory);
		this.isEnter = isEnter;
		List<Triple<Integer,AltAST,String>> label = r.getAltLabels();
		if ( label!=null ) listenerName = label.get(0).c;
	}
}
