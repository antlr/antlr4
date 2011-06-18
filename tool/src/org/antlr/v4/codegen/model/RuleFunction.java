package org.antlr.v4.codegen.model;

import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.v4.codegen.*;
import org.antlr.v4.misc.*;
import org.antlr.v4.parse.*;
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

	@ModelElement public SrcOp code;
	@ModelElement public OrderedHashSet<Decl> decls;
	@ModelElement public StructDecl context;
	@ModelElement public DynamicScopeStruct scope;
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

		ctxType = factory.gen.target.getRuleFunctionContextStructName(r);

		List<Attribute> argsAndReturnValues = new ArrayList<Attribute>();
		List<Attribute> ctorAttrs = new ArrayList<Attribute>();

		index = r.index;

		if ( r.args!=null ) {
			argsAndReturnValues.addAll(r.args.attributes.values());
			args = r.args.attributes.values();
			ctorAttrs.addAll(args);
		}
		if ( r.retvals!=null ) {
			argsAndReturnValues.addAll(r.retvals.attributes.values());
		}
		if ( r.scope!=null ) {
			scope = new DynamicScopeStruct(factory, factory.gen.target.getRuleDynamicScopeStructName(r.name),
										   r.scope.attributes.values());
		}

		globalScopesUsed = Utils.apply(r.useScopes, "getText");

		if ( argsAndReturnValues.size()>0 ) {
			context = new StructDecl(factory, factory.gen.target.getRuleFunctionContextStructName(r),
									 argsAndReturnValues);
			context.ctorAttrs = ctorAttrs;
		}

		ruleLabels = r.getLabelNames();
		tokenLabels = r.getTokenRefs();
		exceptions = Utils.nodesToStrings(r.exceptionActions);
		if ( r.finallyAction!=null ) finallyAction = new Action(factory, r.finallyAction);

		namedActions = new HashMap<String, Action>();
		for (String name : r.namedActions.keySet()) {
			GrammarAST ast = r.namedActions.get(name);
			namedActions.put(name, new Action(factory, ast));
		}

		startState = factory.g.atn.ruleToStartState.get(r);

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

	public void addDecl(Decl d) {
		if ( decls==null ) decls = new OrderedHashSet<Decl>();
		decls.add(d);
	}

//	@Override
//	public List<String> getChildren() {
//		final List<String> sup = super.getChildren();
//		return new ArrayList<String>() {{
//			if ( sup!=null ) addAll(sup);
//			add("context"); add("scope"); add("decls"); add("code");
//			add("finallyAction"); add("namedActions");
//		}};
//	}
}
