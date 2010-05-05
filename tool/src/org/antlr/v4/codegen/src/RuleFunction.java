package org.antlr.v4.codegen.src;

import org.antlr.v4.tool.Attribute;
import org.antlr.v4.tool.GrammarAST;
import org.antlr.v4.tool.Rule;

import java.util.ArrayList;
import java.util.List;

/** */
public class RuleFunction {
	public String name;
	public List<String> modifiers;
    public List<Attribute> args;
    public List<Attribute> retvals;
	public CodeBlock code;

	public RuleFunction(Rule r) {
		this.name = r.name;
		if ( r.modifiers!=null && r.modifiers.size()>0 ) {
			this.modifiers = new ArrayList<String>();
			for (GrammarAST t : r.modifiers) modifiers.add(t.getText());
		}
	}
}
