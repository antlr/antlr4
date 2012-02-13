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
import org.antlr.v4.misc.*;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.tool.*;
import org.antlr.v4.tool.ast.*;

import java.util.*;

import static org.antlr.v4.parse.ANTLRParser.*;

/** */
public class RuleFunction extends OutputModelObject {
	public String name;
	public List<String> modifiers;
	public String ctxType;
	public Collection<String> ruleLabels;
	public Collection<String> tokenLabels;
	public List<String> exceptions;
	public ATNState startState;
	public int index;
	public Collection<Attribute> args = null;
	public Rule rule;
	public AltLabelStructDecl[] altToContext;

	@ModelElement public List<SrcOp> code;
	@ModelElement public OrderedHashSet<Decl> locals; // TODO: move into ctx?
	@ModelElement public StructDecl ruleCtx;
	@ModelElement public Map<String,AltLabelStructDecl> altLabelCtxs;
	@ModelElement public Map<String,Action> namedActions;
	@ModelElement public Action finallyAction;
	@ModelElement public List<SrcOp> postamble;

	public RuleFunction(OutputModelFactory factory, Rule r) {
		super(factory);
		this.name = r.name;
		this.rule = r;
		if ( r.modifiers!=null && r.modifiers.size()>0 ) {
			this.modifiers = new ArrayList<String>();
			for (GrammarAST t : r.modifiers) modifiers.add(t.getText());
		}
		modifiers = Utils.nodesToStrings(r.modifiers);

		index = r.index;

		ruleCtx = new StructDecl(factory, r);
		altToContext = new AltLabelStructDecl[r.getOriginalNumberOfAlts()+1];

		// Add ctx labels for elements in alts with no -> label
		if ( !factory.getGrammar().tool.no_auto_element_labels ) {
			List<Alternative> altsNoLabels = r.getUnlabeledAlts();
			if ( altsNoLabels!=null ) {
				for (Alternative alt : altsNoLabels) {
					List<Decl> decls = getLabelsForAltElements(alt.ast);
					// we know to put in rule ctx, so do it directly
					for (Decl d : decls) ruleCtx.addDecl(d);
				}
			}
		}

		// make structs for -> labeled alts, define ctx labels for elements
		altLabelCtxs = new HashMap<String,AltLabelStructDecl>();
		List<Triple<Integer,AltAST,String>> labels = r.getAltLabels();
		if ( labels!=null ) {
			for (Triple<Integer,AltAST,String> pair : labels) {
				Integer altNum = pair.a;
				AltAST altAST = pair.b;
				String label = pair.c;
				altToContext[altNum] = new AltLabelStructDecl(factory, r, altNum, label);
				altLabelCtxs.put(label, altToContext[altNum]);
				if ( !factory.getGrammar().tool.no_auto_element_labels ) {
					List<Decl> decls = getLabelsForAltElements(altAST);
					// we know which ctx to put in, so do it directly
					for (Decl d : decls) altToContext[altNum].addDecl(d);
				}
			}
		}

		if ( r.args!=null ) {
			ruleCtx.addDecls(r.args.attributes.values());
			args = r.args.attributes.values();
			ruleCtx.ctorAttrs = args;
		}
		if ( r.retvals!=null ) {
			ruleCtx.addDecls(r.retvals.attributes.values());
		}
		if ( r.locals!=null ) {
			ruleCtx.addDecls(r.locals.attributes.values());
		}

		ruleLabels = r.getElementLabelNames();
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

	/** Get list of decls for token/rule refs.
	 *  Single ref X becomes label X
	 *  Multiple refs to X become X1, X2, ...
	 *  Ref X in a loop then is part of List
	 *
	 *  Does not gen labels for literals like '+', 'begin', ';', ...
 	 */
	public List<Decl> getLabelsForAltElements(AltAST altAST) {
		List<Decl> decls = new ArrayList<Decl>();
		IntervalSet reftypes = new IntervalSet(RULE_REF,
											   TOKEN_REF);
		List<GrammarAST> refs = altAST.getNodesWithType(reftypes);
		System.out.println(refs);
		FrequencySet<String> freq = new FrequencySet<String>();
		for (GrammarAST t : refs) {
			freq.add(t.getText());
		}
		// track which ref for X we are at so we can gen X1 if necessary
		FrequencySet<String> counter = new FrequencySet<String>();
		for (GrammarAST t : refs) {
			boolean inLoop = t.hasAncestor(CLOSURE) || t.hasAncestor(POSITIVE_CLOSURE);
//			System.out.println(altAST.toStringTree()+" "+t+" inLoop? "+inLoop);
			Decl d;
			String refLabelName = t.getText();
			if ( !inLoop && freq.count(refLabelName)>1 ) {
				counter.add(refLabelName);
				refLabelName = refLabelName+counter.count(refLabelName);
			}
			if ( t.getType()==RULE_REF ) {
				Rule rref = factory.getGrammar().getRule(t.getText());
				String ctxName = factory.getGenerator().target
								 .getRuleFunctionContextStructName(rref);
				if ( inLoop ) d = new RuleContextListDecl(factory, refLabelName, ctxName);
				else d = new RuleContextDecl(factory, refLabelName, ctxName);
			}
			else {
				if ( inLoop ) d = new TokenListDecl(factory, refLabelName);
				else d = new TokenDecl(factory, refLabelName);
			}
			decls.add(d);
		}
		return decls;
	}

	/** Add local var decl */
	public void addLocalDecl(Decl d) {
		if ( locals ==null ) locals = new OrderedHashSet<Decl>();
		locals.add(d);
		d.isLocal = true;
	}

	/** Add decl to struct ctx for rule or alt if labeled */
	public void addContextDecl(String altLabel, Decl d) {
		CodeBlockForOuterMostAlt alt = d.getOuterMostAltCodeBlock();
		// if we found code blk and might be alt label, try to add to that label ctx
		if ( alt!=null && altLabelCtxs!=null ) {
			System.out.println(d.name+" lives in alt "+alt.alt.altNum);
			AltLabelStructDecl altCtx = altLabelCtxs.get(altLabel);
			if ( altCtx!=null ) { // we have an alt ctx
				System.out.println("ctx is "+ altCtx.name);
				altCtx.addDecl(d);
				return;
			}
		}
		ruleCtx.addDecl(d); // stick in overall rule's ctx
	}
}
