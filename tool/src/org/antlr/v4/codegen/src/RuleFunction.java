package org.antlr.v4.codegen.src;

import org.antlr.v4.misc.Utils;
import org.antlr.v4.tool.Attribute;
import org.antlr.v4.tool.GrammarAST;
import org.antlr.v4.tool.Rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** */
public class RuleFunction extends OutputModelObject {
	public String name;
	public List<String> modifiers;
    public Collection<Attribute> args;
	public Collection<Attribute> retvals;
	public Collection<Attribute> ruleScopeDecls;
	public List<String> globalScopesUsed;
	public Collection<String> ruleLabels;
	public Collection<String> tokenLabels;
	public List<String> elementsReferencedInRewrite;
	public List<String> exceptions;
	public String finallyAction;

	public CodeBlock code;

	public RuleFunction(Rule r) {
		this.name = r.name;
		if ( r.modifiers!=null && r.modifiers.size()>0 ) {
			this.modifiers = new ArrayList<String>();
			for (GrammarAST t : r.modifiers) modifiers.add(t.getText());
		}
		modifiers = Utils.nodesToStrings(r.modifiers);

		if ( r.args!=null ) args = r.args.attributes.values();
		if ( r.retvals!=null ) retvals = r.retvals.attributes.values();
		if ( r.scope!=null ) ruleScopeDecls = r.scope.attributes.values();
		ruleLabels = r.getLabelNames();
		tokenLabels = r.getTokenRefs();
		exceptions = Utils.nodesToStrings(r.exceptionActions);
		if ( r.finallyAction!=null ) finallyAction = r.finallyAction.getText();
	}

	@Override
	public List<String> getChildren() {
		return new ArrayList<String>() {{ add("code"); }};
	}
}
