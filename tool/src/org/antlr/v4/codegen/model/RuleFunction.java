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
import org.antlr.v4.codegen.model.decl.AltLabelStructDecl;
import org.antlr.v4.codegen.model.decl.ContextRuleGetterDecl;
import org.antlr.v4.codegen.model.decl.ContextRuleListGetterDecl;
import org.antlr.v4.codegen.model.decl.ContextRuleListIndexedGetterDecl;
import org.antlr.v4.codegen.model.decl.ContextTokenGetterDecl;
import org.antlr.v4.codegen.model.decl.ContextTokenListGetterDecl;
import org.antlr.v4.codegen.model.decl.ContextTokenListIndexedGetterDecl;
import org.antlr.v4.codegen.model.decl.Decl;
import org.antlr.v4.codegen.model.decl.StructDecl;
import org.antlr.v4.misc.FrequencySet;
import org.antlr.v4.misc.Triple;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.OrderedHashSet;
import org.antlr.v4.tool.Attribute;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.AltAST;
import org.antlr.v4.tool.ast.GrammarAST;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.antlr.v4.parse.ANTLRParser.CLOSURE;
import static org.antlr.v4.parse.ANTLRParser.POSITIVE_CLOSURE;
import static org.antlr.v4.parse.ANTLRParser.RULE_REF;
import static org.antlr.v4.parse.ANTLRParser.TOKEN_REF;

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
		List<AltAST> altsNoLabels = r.getUnlabeledAltASTs();
		if ( altsNoLabels!=null ) {
			Set<Decl> decls = getDeclsForAllElements(altsNoLabels);
			// we know to put in rule ctx, so do it directly
			for (Decl d : decls) ruleCtx.addDecl(d);
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
				Set<Decl> decls = getDeclsForAltElements(altAST);
				// we know which ctx to put in, so do it directly
				for (Decl d : decls) altToContext[altNum].addDecl(d);
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

		startState = factory.getGrammar().atn.ruleToStartState[r.index];
	}

	public void fillNamedActions(OutputModelFactory factory, Rule r) {
		namedActions = new HashMap<String, Action>();
		for (String name : r.namedActions.keySet()) {
			GrammarAST ast = r.namedActions.get(name);
			namedActions.put(name, new Action(factory, ast));
		}
	}

	/** for all alts, find which ref X or r needs List
	   Must see across alts.  If any alt needs X or r as list, then
	   define as list.
	 */
	public Set<Decl> getDeclsForAllElements(List<AltAST> altASTs) {
		Set<String> needsList = new HashSet<String>();
		List<GrammarAST> allRefs = new ArrayList<GrammarAST>();
		for (AltAST ast : altASTs) {
			IntervalSet reftypes = new IntervalSet(RULE_REF, TOKEN_REF);
			List<GrammarAST> refs = ast.getNodesWithType(reftypes);
			allRefs.addAll(refs);
			FrequencySet<String> altFreq = getElementFrequenciesForAlt(ast);
			for (GrammarAST t : refs) {
				String refLabelName = t.getText();
				if ( altFreq.count(t.getText())>1 ) needsList.add(refLabelName);
			}
		}
		Set<Decl> decls = new HashSet<Decl>();
		for (GrammarAST t : allRefs) {
			String refLabelName = t.getText();
			List<Decl> d = getDeclForAltElement(t,
												refLabelName,
												needsList.contains(refLabelName));
			decls.addAll(d);
		}
		return decls;
	}

	/** Given list of X and r refs in alt, compute how many of each there are */
	protected FrequencySet<String> getElementFrequenciesForAlt(AltAST ast) {
		IntervalSet reftypes = new IntervalSet(RULE_REF, TOKEN_REF);
		List<GrammarAST> refs = ast.getNodesWithType(reftypes);
		FrequencySet<String> altFreq = new FrequencySet<String>();
		for (GrammarAST t : refs) {
			String refLabelName = t.getText();
			altFreq.add(refLabelName);
		}
		return altFreq;
	}

	/** Get list of decls for token/rule refs.
	 *  Single ref X becomes X() getter
	 *  Multiple refs to X becomes List X() method, X(int i) method.
	 *  Ref X in a loop then we get List X(), X(int i)
	 *
	 *  Does not gen labels for literals like '+', 'begin', ';', ...
 	 */
	public Set<Decl> getDeclsForAltElements(AltAST altAST) {
		IntervalSet reftypes = new IntervalSet(RULE_REF,
											   TOKEN_REF);
		List<GrammarAST> refs = altAST.getNodesWithType(reftypes);
		Set<Decl> decls = new HashSet<Decl>();
		FrequencySet<String> freq = new FrequencySet<String>();
		for (GrammarAST t : refs) freq.add(t.getText());
		for (GrammarAST t : refs) {
			String refLabelName = t.getText();
			boolean inLoop = t.hasAncestor(CLOSURE) || t.hasAncestor(POSITIVE_CLOSURE);
			boolean multipleRefs = freq.count(refLabelName)>1;
			boolean needList = inLoop || multipleRefs;
//			System.out.println(altAST.toStringTree()+" "+t+" inLoop? "+inLoop);
			List<Decl> d = getDeclForAltElement(t, refLabelName, needList);
			decls.addAll(d);
		}
		return decls;
	}

	public List<Decl> getDeclForAltElement(GrammarAST t, String refLabelName, boolean needList) {
		List<Decl> decls = new ArrayList<Decl>();
		if ( t.getType()==RULE_REF ) {
			Rule rref = factory.getGrammar().getRule(t.getText());
			String ctxName = factory.getGenerator().target
							 .getRuleFunctionContextStructName(rref);
			if ( needList ) {
				decls.add( new ContextRuleListGetterDecl(factory, refLabelName, ctxName) );
				decls.add( new ContextRuleListIndexedGetterDecl(factory, refLabelName, ctxName) );
			}
			else {
				decls.add( new ContextRuleGetterDecl(factory, refLabelName, ctxName) );
			}
		}
		else {
			if ( needList ) {
				decls.add( new ContextTokenListGetterDecl(factory, refLabelName) );
				decls.add( new ContextTokenListIndexedGetterDecl(factory, refLabelName) );
			}
			else {
				decls.add( new ContextTokenGetterDecl(factory, refLabelName) );
			}
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
//			System.out.println(d.name+" lives in alt "+alt.alt.altNum);
			AltLabelStructDecl altCtx = altLabelCtxs.get(altLabel);
			if ( altCtx!=null ) { // we have an alt ctx
//				System.out.println("ctx is "+ altCtx.name);
				altCtx.addDecl(d);
				return;
			}
		}
		ruleCtx.addDecl(d); // stick in overall rule's ctx
	}
}
