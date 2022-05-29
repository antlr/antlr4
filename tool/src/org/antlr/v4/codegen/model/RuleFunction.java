/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.model.decl.AltLabelStructDecl;
import org.antlr.v4.codegen.model.decl.AttributeDecl;
import org.antlr.v4.codegen.model.decl.ContextRuleGetterDecl;
import org.antlr.v4.codegen.model.decl.ContextRuleListGetterDecl;
import org.antlr.v4.codegen.model.decl.ContextRuleListIndexedGetterDecl;
import org.antlr.v4.codegen.model.decl.ContextTokenGetterDecl;
import org.antlr.v4.codegen.model.decl.ContextTokenListGetterDecl;
import org.antlr.v4.codegen.model.decl.ContextTokenListIndexedGetterDecl;
import org.antlr.v4.codegen.model.decl.Decl;
import org.antlr.v4.codegen.model.decl.StructDecl;
import org.antlr.v4.misc.FrequencySet;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.parse.GrammarASTAdaptor;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.OrderedHashSet;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.tool.Attribute;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.AltAST;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.PredAST;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.antlr.v4.parse.ANTLRParser.RULE_REF;
import static org.antlr.v4.parse.ANTLRParser.STRING_LITERAL;
import static org.antlr.v4.parse.ANTLRParser.TOKEN_REF;

/** */
public class RuleFunction extends OutputModelObject {
	public final String name;
	public final String escapedName;
	public final List<String> modifiers;
	public String ctxType;
	public final Collection<String> ruleLabels;
	public final Collection<String> tokenLabels;
	public final ATNState startState;
	public final int index;
	public final Rule rule;
	public final AltLabelStructDecl[] altToContext;
	public boolean hasLookaheadBlock;

	@ModelElement public List<SrcOp> code;
	@ModelElement public OrderedHashSet<Decl> locals; // TODO: move into ctx?
	@ModelElement public Collection<AttributeDecl> args = null;
	@ModelElement public StructDecl ruleCtx;
	@ModelElement public Map<String,AltLabelStructDecl> altLabelCtxs;
	@ModelElement public Map<String,Action> namedActions;
	@ModelElement public Action finallyAction;
	@ModelElement public List<ExceptionClause> exceptions;
	@ModelElement public List<SrcOp> postamble;

	public RuleFunction(OutputModelFactory factory, Rule r) {
		super(factory);
		this.name = r.name;
		this.escapedName = factory.getGenerator().getTarget().escapeIfNeeded(r.name);
		this.rule = r;
		modifiers = Utils.nodesToStrings(r.modifiers);

		index = r.index;

		ruleCtx = new StructDecl(factory, r);
		altToContext = new AltLabelStructDecl[r.getOriginalNumberOfAlts()+1];
		addContextGetters(factory, r);

		if ( r.args!=null ) {
			Collection<Attribute> decls = r.args.attributes.values();
			if ( decls.size()>0 ) {
				args = new ArrayList<AttributeDecl>();
				ruleCtx.addDecls(decls);
				for (Attribute a : decls) {
					args.add(new AttributeDecl(factory, a));
				}
				ruleCtx.ctorAttrs = args;
			}
		}
		if ( r.retvals!=null ) {
			ruleCtx.addDecls(r.retvals.attributes.values());
		}
		if ( r.locals!=null ) {
			ruleCtx.addDecls(r.locals.attributes.values());
		}

		ruleLabels = r.getElementLabelNames();
		tokenLabels = r.getTokenRefs();
		if ( r.exceptions!=null ) {
			exceptions = new ArrayList<ExceptionClause>();
			for (GrammarAST e : r.exceptions) {
				ActionAST catchArg = (ActionAST)e.getChild(0);
				ActionAST catchAction = (ActionAST)e.getChild(1);
				exceptions.add(new ExceptionClause(factory, catchArg, catchAction));
			}
		}

		startState = factory.getGrammar().atn.ruleToStartState[r.index];
	}

	public void addContextGetters(OutputModelFactory factory, Rule r) {
		// Add ctx labels for elements in alts with no -> label
		List<AltAST> altsNoLabels = r.getUnlabeledAltASTs();
		if ( altsNoLabels!=null ) {
			Set<Decl> decls = getDeclsForAllElements(altsNoLabels);
			// we know to put in rule ctx, so do it directly
			for (Decl d : decls) ruleCtx.addDecl(d);
		}

		// make structs for -> labeled alts, define ctx labels for elements
		altLabelCtxs = new HashMap<String,AltLabelStructDecl>();
		Map<String, List<Pair<Integer, AltAST>>> labels = r.getAltLabels();
		if ( labels!=null ) {
			for (Map.Entry<String, List<Pair<Integer, AltAST>>> entry : labels.entrySet()) {
				String label = entry.getKey();
				List<AltAST> alts = new ArrayList<AltAST>();
				for (Pair<Integer, AltAST> pair : entry.getValue()) {
					alts.add(pair.b);
				}

				Set<Decl> decls = getDeclsForAllElements(alts);
				for (Pair<Integer, AltAST> pair : entry.getValue()) {
					Integer altNum = pair.a;
					altToContext[altNum] = new AltLabelStructDecl(factory, r, altNum, label);
					if (!altLabelCtxs.containsKey(label)) {
						altLabelCtxs.put(label, altToContext[altNum]);
					}

					// we know which ctx to put in, so do it directly
					for (Decl d : decls) {
						altToContext[altNum].addDecl(d);
					}
				}
			}
		}
	}

	public void fillNamedActions(OutputModelFactory factory, Rule r) {
		if ( r.finallyAction!=null ) {
			finallyAction = new Action(factory, r.finallyAction);
		}

		namedActions = new HashMap<String, Action>();
		for (String name : r.namedActions.keySet()) {
			ActionAST ast = r.namedActions.get(name);
			namedActions.put(name, new Action(factory, ast));
		}
	}

	/** for all alts, find which ref X or r needs List
	   Must see across alts. If any alt needs X or r as list, then
	   define as list.
	 */
	public Set<Decl> getDeclsForAllElements(List<AltAST> altASTs) {
		Set<String> needsList = new HashSet<String>();
		Set<String> nonOptional = new HashSet<String>();
		List<GrammarAST> allRefs = new ArrayList<GrammarAST>();
		boolean firstAlt = true;
		IntervalSet reftypes = new IntervalSet(RULE_REF, TOKEN_REF, STRING_LITERAL);
		for (AltAST ast : altASTs) {
			List<GrammarAST> refs = getRuleTokens(ast.getNodesWithType(reftypes));
			allRefs.addAll(refs);
			Pair<FrequencySet<String>, FrequencySet<String>> minAndAltFreq = getElementFrequenciesForAlt(ast);
			FrequencySet<String> minFreq = minAndAltFreq.a;
			FrequencySet<String> altFreq = minAndAltFreq.b;
			for (GrammarAST t : refs) {
				String refLabelName = getName(t);

				if (refLabelName != null) {
					if (altFreq.count(refLabelName) > 1) {
						needsList.add(refLabelName);
					}

					if (firstAlt && minFreq.count(refLabelName) != 0) {
						nonOptional.add(refLabelName);
					}
				}
			}

			for (String ref : nonOptional.toArray(new String[0])) {
				if (minFreq.count(ref) == 0) {
					nonOptional.remove(ref);
				}
			}

			firstAlt = false;
		}
		Set<Decl> decls = new LinkedHashSet<Decl>();
		for (GrammarAST t : allRefs) {
			String refLabelName = getName(t);

			if (refLabelName == null) {
				continue;
			}

			List<Decl> d = getDeclForAltElement(t,
												refLabelName,
												needsList.contains(refLabelName),
												!nonOptional.contains(refLabelName));
			decls.addAll(d);
		}
		return decls;
	}

	private List<GrammarAST> getRuleTokens(List<GrammarAST> refs) {
		List<GrammarAST> result = new ArrayList<>(refs.size());
		for (GrammarAST ref : refs) {
			CommonTree r = ref;

			boolean ignore = false;
			while (r != null) {
				// Ignore string literals in predicates
				if (r instanceof PredAST) {
					ignore = true;
					break;
				}
				r = r.parent;
			}

			if (!ignore) {
				result.add(ref);
			}
		}

		return result;
	}

	private String getName(GrammarAST token) {
		String tokenText = token.getText();
		String tokenName = token.getType() != STRING_LITERAL ? tokenText : token.g.getTokenName(tokenText);
		return tokenName == null || tokenName.startsWith("T__") ? null : tokenName; // Do not include tokens with auto generated names
	}

	/** Given list of X and r refs in alt, compute how many of each there are */
	protected Pair<FrequencySet<String>, FrequencySet<String>> getElementFrequenciesForAlt(AltAST ast) {
		try {
			ElementFrequenciesVisitor visitor = new ElementFrequenciesVisitor(new CommonTreeNodeStream(new GrammarASTAdaptor(), ast));
			visitor.outerAlternative();
			if (visitor.frequencies.size() != 1) {
				factory.getGrammar().tool.errMgr.toolError(ErrorType.INTERNAL_ERROR);
				return new Pair<>(new FrequencySet<String>(), new FrequencySet<String>());
			}

			return new Pair<>(visitor.getMinFrequencies(), visitor.frequencies.peek());
		}
		catch (RecognitionException ex) {
			factory.getGrammar().tool.errMgr.toolError(ErrorType.INTERNAL_ERROR, ex);
			return new Pair<>(new FrequencySet<String>(), new FrequencySet<String>());
		}
	}

	public List<Decl> getDeclForAltElement(GrammarAST t, String refLabelName, boolean needList, boolean optional) {
		List<Decl> decls = new ArrayList<Decl>();
		if ( t.getType()==RULE_REF ) {
			Rule rref = factory.getGrammar().getRule(t.getText());
			String ctxName = factory.getGenerator().getTarget()
							 .getRuleFunctionContextStructName(rref);
			if ( needList) {
				if(factory.getGenerator().getTarget().supportsOverloadedMethods())
					decls.add( new ContextRuleListGetterDecl(factory, refLabelName, ctxName) );
				decls.add( new ContextRuleListIndexedGetterDecl(factory, refLabelName, ctxName) );
			}
			else {
				decls.add( new ContextRuleGetterDecl(factory, refLabelName, ctxName, optional) );
			}
		}
		else {
			if ( needList ) {
				if(factory.getGenerator().getTarget().supportsOverloadedMethods())
					decls.add( new ContextTokenListGetterDecl(factory, refLabelName) );
				decls.add( new ContextTokenListIndexedGetterDecl(factory, refLabelName) );
			}
			else {
				decls.add( new ContextTokenGetterDecl(factory, refLabelName, optional) );
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
