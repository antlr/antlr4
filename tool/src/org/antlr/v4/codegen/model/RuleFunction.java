/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.model.decl.*;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.misc.OrderedHashSet;
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
	@ModelElement public StructDecl ruleCtx;	//@ModelElement public DynamicScopeStruct scope;
	@ModelElement public Map<String, Action> namedActions;
	@ModelElement public Action finallyAction;
	@ModelElement public List<SrcOp> postamble;

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

		startState = factory.getGrammar().atn.ruleToStartState[r.index];
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
