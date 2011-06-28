package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.model.decl.*;
import org.antlr.v4.misc.*;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.tool.*;

import java.util.*;

/** */
public class RuleFunction extends OutputModelObject {
	public String name;
	public List<String> modifiers;
	public String ctxType;
	public List<String> globalScopesUsed;
	public Collection<String> ruleLabels;
	public Collection<String> tokenLabels;
	public List<String> elementsReferencedInRewrite;
	public List<String> exceptions;
	public ATNState startState;
	public int index;
	public Collection<Attribute> args = null;

	@ModelElement public List<SrcOp> code;
	@ModelElement public OrderedHashSet<Decl> locals; // TODO: move into ctx?
	@ModelElement public StructDecl ruleCtx;
	//@ModelElement public DynamicScopeStruct scope;
	@ModelElement public Map<String, Action> namedActions;
	@ModelElement public Action finallyAction;

	public RuleFunction(OutputModelFactory factory) {
		super(factory);
	}

	public RuleFunction(OutputModelFactory factory, Rule r) {
		super(factory);
		this.name = r.name;
		if ( r.modifiers!=null && r.modifiers.size()>0 ) {
			this.modifiers = new ArrayList<String>();
			for (GrammarAST t : r.modifiers) modifiers.add(t.getText());
		}
		modifiers = Utils.nodesToStrings(r.modifiers);

		index = r.index;

		// might need struct; build but drop later if no elements
		ruleCtx = new StructDecl(factory);

		if ( r.args!=null ) {
			ruleCtx.addDecls(r.args.attributes.values());
			args = r.args.attributes.values();
			ruleCtx.ctorAttrs = args;
		}
		if ( r.retvals!=null ) {
			ruleCtx.addDecls(r.retvals.attributes.values());
		}
		if ( r.scope!=null ) {
			ruleCtx.addDecls(r.scope.attributes.values());
		}

		globalScopesUsed = Utils.apply(r.useScopes, "getText");

		ruleLabels = r.getLabelNames();
		tokenLabels = r.getTokenRefs();
		exceptions = Utils.nodesToStrings(r.exceptionActions);
		if ( r.finallyAction!=null ) finallyAction = new Action(factory, r.finallyAction);

		namedActions = new HashMap<String, Action>();
		for (String name : r.namedActions.keySet()) {
			GrammarAST ast = r.namedActions.get(name);
			namedActions.put(name, new Action(factory, ast));
		}

		if ( factory.getGrammar().hasASTOption() ) {
			addLocalDecl(new RootDecl(factory, 0));
			addLocalDecl(new KidsListDecl(factory, 0));
		}

		startState = factory.getGrammar().atn.ruleToStartState.get(r.index);
	}

	/** Add local var decl */
	public void addLocalDecl(Decl d) {
		if ( locals ==null ) locals = new OrderedHashSet<Decl>();
		locals.add(d);
		d.isLocal = true;
	}

	/** Add decl to struct ctx */
	public void addContextDecl(Decl d) {
		ruleCtx.addDecl(d);
	}
}
