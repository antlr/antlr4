package org.antlr.v4.codegen.src;

import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.SourceGenTriggers;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.parse.GrammarASTAdaptor;
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

	public SrcOp code;

	public RuleFunction(OutputModelFactory factory, Rule r) {
		super(factory);
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

		factory.currentRule.push(this);
		GrammarASTAdaptor adaptor = new GrammarASTAdaptor(r.ast.token.getInputStream());
		GrammarAST blk = (GrammarAST)r.ast.getFirstChildWithType(ANTLRParser.BLOCK);
		CommonTreeNodeStream nodes = new CommonTreeNodeStream(adaptor,blk);
		SourceGenTriggers genTriggers = new SourceGenTriggers(nodes, factory);
		try {
			code = genTriggers.block(null,null); // GEN Instr OBJECTS
		}
		catch (Exception e){
			e.printStackTrace(System.err);
		}
		factory.currentRule.pop();
	}

	@Override
	public List<String> getChildren() {
		final List<String> sup = super.getChildren();
		return new ArrayList<String>() {{ if ( sup!=null ) addAll(sup); add("code"); }};
	}
}
