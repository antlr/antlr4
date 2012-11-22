/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *      derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.antlr.v4.analysis;

import org.antlr.v4.Tool;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.parse.GrammarASTAdaptor;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATNSimulator;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.tool.Alternative;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.AltAST;
import org.antlr.v4.tool.ast.BlockAST;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.GrammarRootAST;
import org.antlr.v4.tool.ast.PlusBlockAST;
import org.antlr.v4.tool.ast.RuleAST;
import org.antlr.v4.tool.ast.RuleRefAST;
import org.antlr.v4.tool.ast.StarBlockAST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sam Harwell
 */
public class LeftFactoringRuleTransformer {
	public static final String LEFTFACTOR = "leftfactor";
	public static final String SUPPRESS_ACCESSOR = "suppressAccessor";

	private static final Logger LOGGER = Logger.getLogger(LeftFactoringRuleTransformer.class.getName());

	public GrammarRootAST _ast;
	public Map<String, Rule> _rules;
	public Grammar _g;
	public Tool _tool;

	private final GrammarASTAdaptor adaptor = new GrammarASTAdaptor();

	public LeftFactoringRuleTransformer(@NotNull GrammarRootAST ast, @NotNull Map<String, Rule> rules, @NotNull Grammar g) {
		this._ast = ast;
		this._rules = rules;
		this._g = g;
		this._tool = g.tool;
	}

	public void translateLeftFactoredRules() {
		// translate all rules marked for auto left factoring
		for (Rule r : _rules.values()) {
			if (Grammar.isTokenName(r.name)) {
				continue;
			}

			Object leftFactoredRules = r.namedActions.get(LEFTFACTOR);
			if (leftFactoredRules == null) {
				continue;
			}

			if (!(leftFactoredRules instanceof ActionAST)) {
				continue;
			}

			String leftFactoredRuleAction = leftFactoredRules.toString();
			leftFactoredRuleAction = leftFactoredRuleAction.substring(1, leftFactoredRuleAction.length() - 1);
			String[] rules = leftFactoredRuleAction.split(",\\s*");
			if (rules.length == 0) {
				continue;
			}

			LOGGER.log(Level.FINE, "Left factoring {0} out of alts in grammar rule {1}", new Object[] { Arrays.toString(rules), r.name });

			Set<GrammarAST> translatedBlocks = new HashSet<GrammarAST>();
			List<GrammarAST> blocks = r.ast.getNodesWithType(ANTLRParser.BLOCK);
			blockLoop:
			for (GrammarAST block : blocks) {
				for (GrammarAST current = (GrammarAST)block.getParent(); current != null; current = (GrammarAST)current.getAncestor(ANTLRParser.BLOCK)) {
					if (translatedBlocks.contains(current)) {
						// an enclosing decision was already factored
						continue blockLoop;
					}
				}

				if (rules.length != 1) {
					throw new UnsupportedOperationException("Chained left factoring is not yet implemented.");
				}

				if (!translateLeftFactoredDecision(block, rules[0], false, DecisionFactorMode.COMBINED_FACTOR, true)) {
					// couldn't translate the decision
					continue;
				}

				translatedBlocks.add(block);
			}
		}
	}

	protected boolean expandOptionalQuantifiersForBlock(GrammarAST block, boolean variant) {
		List<GrammarAST> children = new ArrayList<GrammarAST>();
		for (int i = 0; i < block.getChildCount(); i++) {
			GrammarAST child = (GrammarAST)block.getChild(i);
			if (child.getType() != ANTLRParser.ALT) {
				children.add(child);
				continue;
			}

			GrammarAST expandedAlt = expandOptionalQuantifiersForAlt(child);
			if (expandedAlt == null) {
				return false;
			}

			children.add(expandedAlt);
		}

		GrammarAST newChildren = adaptor.nil();
		newChildren.addChildren(children);
		block.replaceChildren(0, block.getChildCount() - 1, newChildren);
		block.freshenParentAndChildIndexesDeeply();

		if (!variant && block.getParent() instanceof RuleAST) {
			RuleAST ruleAST = (RuleAST)block.getParent();
			String ruleName = ruleAST.getChild(0).getText();
			Rule r = _rules.get(ruleName);
			List<GrammarAST> blockAlts = block.getAllChildrenWithType(ANTLRParser.ALT);
			r.numberOfAlts = blockAlts.size();
			r.alt = new Alternative[blockAlts.size() + 1];
			for (int i = 0; i < blockAlts.size(); i++) {
				r.alt[i + 1] = new Alternative(r, i + 1);
				r.alt[i + 1].ast = (AltAST)blockAlts.get(i);
			}
		}

		return true;
	}

	protected GrammarAST expandOptionalQuantifiersForAlt(GrammarAST alt) {
		if (alt.getChildCount() == 0) {
			return null;
		}

		if (alt.getChild(0).getType() == ANTLRParser.OPTIONAL) {
			GrammarAST root = adaptor.nil();

			GrammarAST alt2 = alt.dupTree();
			alt2.deleteChild(0);
			if (alt2.getChildCount() == 0) {
				adaptor.addChild(alt2, adaptor.create(ANTLRParser.EPSILON, "EPSILON"));
			}

			alt.setChild(0, alt.getChild(0).getChild(0));
			if (alt.getChild(0).getType() == ANTLRParser.BLOCK && alt.getChild(0).getChildCount() == 1 && alt.getChild(0).getChild(0).getType() == ANTLRParser.ALT) {
				GrammarAST list = adaptor.nil();
				for (Object tree : ((GrammarAST)alt.getChild(0).getChild(0)).getChildren()) {
					adaptor.addChild(list, tree);
				}

				adaptor.replaceChildren(alt, 0, 0, list);
			}

			adaptor.addChild(root, alt);
			adaptor.addChild(root, alt2);
			return root;
		}
		else if (alt.getChild(0).getType() == ANTLRParser.CLOSURE) {
			GrammarAST root = adaptor.nil();

			GrammarAST alt2 = alt.dupTree();
			alt2.deleteChild(0);
			if (alt2.getChildCount() == 0) {
				adaptor.addChild(alt2, adaptor.create(ANTLRParser.EPSILON, "EPSILON"));
			}

			PlusBlockAST plusBlockAST = new PlusBlockAST(ANTLRParser.POSITIVE_CLOSURE, adaptor.createToken(ANTLRParser.POSITIVE_CLOSURE, "+"), null);
			for (int i = 0; i < alt.getChild(0).getChildCount(); i++) {
				plusBlockAST.addChild(alt.getChild(0).getChild(i));
			}

			alt.setChild(0, plusBlockAST);

			adaptor.addChild(root, alt);
			adaptor.addChild(root, alt2);
			return root;
		}

		return alt;
	}

	protected boolean translateLeftFactoredDecision(GrammarAST block, String factoredRule, boolean variant, DecisionFactorMode mode, boolean includeFactoredElement) {
		if (mode == DecisionFactorMode.PARTIAL_UNFACTORED && includeFactoredElement) {
			throw new IllegalArgumentException("Cannot include the factored element in unfactored alternatives.");
		}
		else if (mode == DecisionFactorMode.COMBINED_FACTOR && !includeFactoredElement) {
			throw new IllegalArgumentException("Cannot return a combined answer without the factored element.");
		}

		if (!expandOptionalQuantifiersForBlock(block, variant)) {
			return false;
		}

		List<GrammarAST> alternatives = block.getAllChildrenWithType(ANTLRParser.ALT);
		GrammarAST[] factoredAlternatives = new GrammarAST[alternatives.size()];
		GrammarAST[] unfactoredAlternatives = new GrammarAST[alternatives.size()];
		IntervalSet factoredIntervals = new IntervalSet();
		IntervalSet unfactoredIntervals = new IntervalSet();
		for (int i = 0; i < alternatives.size(); i++) {
			GrammarAST alternative = alternatives.get(i);
			if (mode.includeUnfactoredAlts()) {
				GrammarAST unfactoredAlt = translateLeftFactoredAlternative(alternative.dupTree(), factoredRule, variant, DecisionFactorMode.PARTIAL_UNFACTORED, false);
				unfactoredAlternatives[i] = unfactoredAlt;
				if (unfactoredAlt != null) {
					unfactoredIntervals.add(i);
				}
			}

			if (mode.includeFactoredAlts()) {
				GrammarAST factoredAlt = translateLeftFactoredAlternative(alternative, factoredRule, variant, mode == DecisionFactorMode.COMBINED_FACTOR ? DecisionFactorMode.PARTIAL_FACTORED : DecisionFactorMode.FULL_FACTOR, includeFactoredElement);
				factoredAlternatives[i] = factoredAlt;
				if (factoredAlt != null) {
					factoredIntervals.add(alternative.getChildIndex());
				}
			}
		}

		if (factoredIntervals.isNil() && !mode.includeUnfactoredAlts()) {
			return false;
		} else if (unfactoredIntervals.isNil() && !mode.includeFactoredAlts()) {
			return false;
		}

		if (unfactoredIntervals.isNil() && factoredIntervals.size() == alternatives.size() && mode.includeFactoredAlts() && !includeFactoredElement) {
			for (int i = 0; i < factoredAlternatives.length; i++) {
				GrammarAST translatedAlt = factoredAlternatives[i];
				if (translatedAlt.getChildCount() == 0) {
					adaptor.addChild(translatedAlt, adaptor.create(ANTLRParser.EPSILON, "EPSILON"));
				}

				adaptor.setChild(block, i, translatedAlt);
			}

			return true;
		}
		else if (factoredIntervals.isNil() && unfactoredIntervals.size() == alternatives.size() && mode.includeUnfactoredAlts()) {
			for (int i = 0; i < unfactoredAlternatives.length; i++) {
				GrammarAST translatedAlt = unfactoredAlternatives[i];
				if (translatedAlt.getChildCount() == 0) {
					adaptor.addChild(translatedAlt, adaptor.create(ANTLRParser.EPSILON, "EPSILON"));
				}

				adaptor.setChild(block, i, translatedAlt);
			}

			return true;
		}

		if (mode == DecisionFactorMode.FULL_FACTOR) {
			return false;
		}

		/* for a, b, c being arbitrary `element` trees, this block performs
		 * this transformation:
		 *
		 * factoredElement a
		 * | factoredElement b
		 * | factoredElement c
		 * | ...
		 *
		 * ==>
		 *
		 * factoredElement (a | b | c | ...)
		 */
		GrammarAST newChildren = adaptor.nil();
		for (int i = 0; i < alternatives.size(); i++) {
			if (mode.includeFactoredAlts() && factoredIntervals.contains(i)) {
				boolean combineWithPrevious = i > 0 && factoredIntervals.contains(i - 1) && (!mode.includeUnfactoredAlts() || !unfactoredIntervals.contains(i - 1));
				if (combineWithPrevious) {
					GrammarAST translatedAlt = factoredAlternatives[i];
					if (translatedAlt.getChildCount() == 0) {
						adaptor.addChild(translatedAlt, adaptor.create(ANTLRParser.EPSILON, "EPSILON"));
					}

					GrammarAST previous = (GrammarAST)newChildren.getChild(newChildren.getChildCount() - 1);
					if (LOGGER.isLoggable(Level.FINE)) {
						LOGGER.log(Level.FINE, previous.toStringTree());
						LOGGER.log(Level.FINE, translatedAlt.toStringTree());
					}
					if (previous.getChildCount() == 1 || previous.getChild(1).getType() != ANTLRParser.BLOCK) {
						GrammarAST newBlock = new BlockAST(adaptor.createToken(ANTLRParser.BLOCK, "BLOCK"));
						GrammarAST newAlt = new AltAST(adaptor.createToken(ANTLRParser.ALT, "ALT"));
						adaptor.addChild(newBlock, newAlt);
						while (previous.getChildCount() > 1) {
							adaptor.addChild(newAlt, previous.deleteChild(1));
						}

						if (newAlt.getChildCount() == 0) {
							adaptor.addChild(newAlt, adaptor.create(ANTLRParser.EPSILON, "EPSILON"));
						}

						adaptor.addChild(previous, newBlock);
					}

					if (translatedAlt.getChildCount() == 1 || translatedAlt.getChild(1).getType() != ANTLRParser.BLOCK) {
						GrammarAST newBlock = new BlockAST(adaptor.createToken(ANTLRParser.BLOCK, "BLOCK"));
						GrammarAST newAlt = new AltAST(adaptor.createToken(ANTLRParser.ALT, "ALT"));
						adaptor.addChild(newBlock, newAlt);
						while (translatedAlt.getChildCount() > 1) {
							adaptor.addChild(newAlt, translatedAlt.deleteChild(1));
						}

						if (newAlt.getChildCount() == 0) {
							adaptor.addChild(newAlt, adaptor.create(ANTLRParser.EPSILON, "EPSILON"));
						}

						adaptor.addChild(translatedAlt, newBlock);
					}

					GrammarAST combinedBlock = (GrammarAST)previous.getChild(1);
					adaptor.addChild(combinedBlock, translatedAlt.getChild(1).getChild(0));

					if (LOGGER.isLoggable(Level.FINE)) {
						LOGGER.log(Level.FINE, previous.toStringTree());
					}
				}
				else {
					GrammarAST translatedAlt = factoredAlternatives[i];
					if (translatedAlt.getChildCount() == 0) {
						adaptor.addChild(translatedAlt, adaptor.create(ANTLRParser.EPSILON, "EPSILON"));
					}

					adaptor.addChild(newChildren, translatedAlt);
				}
			}

			if (mode.includeUnfactoredAlts() && unfactoredIntervals.contains(i)) {
				GrammarAST translatedAlt = unfactoredAlternatives[i];
				if (translatedAlt.getChildCount() == 0) {
					adaptor.addChild(translatedAlt, adaptor.create(ANTLRParser.EPSILON, "EPSILON"));
				}

				adaptor.addChild(newChildren, translatedAlt);
			}
		}

		adaptor.replaceChildren(block, 0, block.getChildCount() - 1, newChildren);

		if (!variant && block.getParent() instanceof RuleAST) {
			RuleAST ruleAST = (RuleAST)block.getParent();
			String ruleName = ruleAST.getChild(0).getText();
			Rule r = _rules.get(ruleName);
			List<GrammarAST> blockAlts = block.getAllChildrenWithType(ANTLRParser.ALT);
			r.numberOfAlts = blockAlts.size();
			r.alt = new Alternative[blockAlts.size() + 1];
			for (int i = 0; i < blockAlts.size(); i++) {
				r.alt[i + 1] = new Alternative(r, i + 1);
				r.alt[i + 1].ast = (AltAST)blockAlts.get(i);
			}
		}

		return true;
	}

	protected GrammarAST translateLeftFactoredAlternative(GrammarAST alternative, String factoredRule, boolean variant, DecisionFactorMode mode, boolean includeFactoredElement) {
		if (mode == DecisionFactorMode.PARTIAL_UNFACTORED && includeFactoredElement) {
			throw new IllegalArgumentException("Cannot include the factored element in unfactored alternatives.");
		}
		else if (mode == DecisionFactorMode.COMBINED_FACTOR && !includeFactoredElement) {
			throw new IllegalArgumentException("Cannot return a combined answer without the factored element.");
		}

		assert alternative.getChildCount() > 0;

		if (alternative.getChild(0).getType() == ANTLRParser.EPSILON) {
			if (mode == DecisionFactorMode.PARTIAL_UNFACTORED) {
				return alternative;
			}

			return null;
		}

		GrammarAST translatedElement = translateLeftFactoredElement((GrammarAST)alternative.getChild(0), factoredRule, variant, mode, includeFactoredElement);
		if (translatedElement == null) {
			return null;
		}

		alternative.replaceChildren(0, 0, translatedElement);
		if (alternative.getChildCount() == 0) {
			adaptor.addChild(alternative, adaptor.create(ANTLRParser.EPSILON, "EPSILON"));
		}

		assert alternative.getChildCount() > 0;
		return alternative;
	}

	protected GrammarAST translateLeftFactoredElement(GrammarAST element, String factoredRule, boolean variant, DecisionFactorMode mode, boolean includeFactoredElement) {
		if (mode == DecisionFactorMode.PARTIAL_UNFACTORED && includeFactoredElement) {
			throw new IllegalArgumentException("Cannot include the factored element in unfactored alternatives.");
		}

		if (mode == DecisionFactorMode.COMBINED_FACTOR) {
			throw new UnsupportedOperationException("Cannot return a combined answer.");
		}

		assert !mode.includeFactoredAlts() || !mode.includeUnfactoredAlts();

		switch (element.getType()) {
		case ANTLRParser.ASSIGN:
		case ANTLRParser.PLUS_ASSIGN:
		{
			/* label=a
			 *
			 * ==>
			 *
			 * factoredElement label=a_factored
			 */

			GrammarAST translatedChildElement = translateLeftFactoredElement((GrammarAST)element.getChild(1), factoredRule, variant, mode, includeFactoredElement);
			if (translatedChildElement == null) {
				return null;
			}

			RuleAST ruleAST = (RuleAST)element.getAncestor(ANTLRParser.RULE);
			LOGGER.log(Level.WARNING, "Could not left factor ''{0}'' out of decision in rule ''{1}'': labeled rule references are not yet supported.",
				new Object[] { factoredRule, ruleAST.getChild(0).getText() });
			return null;
			//if (!translatedChildElement.isNil()) {
			//	GrammarAST root = adaptor.nil();
			//	Object factoredElement = translatedChildElement;
			//	if (outerRule) {
			//		adaptor.addChild(root, factoredElement);
			//	}
			//
			//	String action = String.format("_localctx.%s = (ContextType)_localctx.getParent().getChild(_localctx.getParent().getChildCount() - 1);", element.getChild(0).getText());
			//	adaptor.addChild(root, new ActionAST(adaptor.createToken(ANTLRParser.ACTION, action)));
			//	return root;
			//}
			//else {
			//	GrammarAST root = adaptor.nil();
			//	Object factoredElement = adaptor.deleteChild(translatedChildElement, 0);
			//	if (outerRule) {
			//		adaptor.addChild(root, factoredElement);
			//	}
			//
			//	adaptor.addChild(root, element);
			//	adaptor.replaceChildren(element, 1, 1, translatedChildElement);
			//	return root;
			//}
		}

		case ANTLRParser.RULE_REF:
		{
			if (factoredRule.equals(element.getToken().getText())) {
				if (!mode.includeFactoredAlts()) {
					return null;
				}

				if (includeFactoredElement) {
					// this element is already left factored
					return element;
				}

				GrammarAST root = adaptor.nil();
				root.addChild(adaptor.create(Token.EPSILON, "EPSILON"));
				root.deleteChild(0);
				return root;
			}

			Rule targetRule = _rules.get(element.getToken().getText());
			if (targetRule == null) {
				return null;
			}

			RuleVariants ruleVariants = createLeftFactoredRuleVariant(targetRule, factoredRule);
			switch (ruleVariants) {
			case NONE:
				if (!mode.includeUnfactoredAlts()) {
					return null;
				}

				// just call the original rule (leave the element unchanged)
				return element;

			case FULLY_FACTORED:
				if (!mode.includeFactoredAlts()) {
					return null;
				}

				break;

			case PARTIALLY_FACTORED:
				break;

			default:
				throw new IllegalStateException();
			}

			String marker = mode.includeFactoredAlts() ? ATNSimulator.RULE_LF_VARIANT_MARKER : ATNSimulator.RULE_NOLF_VARIANT_MARKER;
			element.setText(element.getText() + marker + factoredRule);

			GrammarAST root = adaptor.nil();

			if (includeFactoredElement) {
				assert mode.includeFactoredAlts();
				RuleRefAST factoredRuleRef = new RuleRefAST(adaptor.createToken(ANTLRParser.RULE_REF, factoredRule));
				factoredRuleRef.setOption(SUPPRESS_ACCESSOR, adaptor.create(ANTLRParser.ID, "true"));

				if (_rules.get(factoredRule).args != null && _rules.get(factoredRule).args.size() > 0) {
					adaptor.addChild(factoredRuleRef, new ActionAST(adaptor.createToken(ANTLRParser.ARG_ACTION, "0")));
				}

				adaptor.addChild(root, factoredRuleRef);
			}

			adaptor.addChild(root, element);

			return root;
		}

		case ANTLRParser.BLOCK:
		{
			GrammarAST cloned = element.dupTree();
			if (!translateLeftFactoredDecision(cloned, factoredRule, variant, mode, includeFactoredElement)) {
				return null;
			}

			if (cloned.getChildCount() != 1) {
				return null;
			}

			GrammarAST root = adaptor.nil();
			for (int i = 0; i < cloned.getChild(0).getChildCount(); i++) {
				adaptor.addChild(root, cloned.getChild(0).getChild(i));
			}

			return root;
		}

		case ANTLRParser.POSITIVE_CLOSURE:
		{
			/* a+
			 *
			 * =>
			 *
			 * factoredElement a_factored a*
			 */

			GrammarAST originalChildElement = (GrammarAST)element.getChild(0);
			GrammarAST translatedElement = translateLeftFactoredElement(originalChildElement.dupTree(), factoredRule, variant, mode, includeFactoredElement);
			if (translatedElement == null) {
				return null;
			}

			GrammarAST closure = new StarBlockAST(ANTLRParser.CLOSURE, adaptor.createToken(ANTLRParser.CLOSURE, "CLOSURE"), null);
			adaptor.addChild(closure, originalChildElement);

			GrammarAST root = adaptor.nil();
			if (mode.includeFactoredAlts()) {
				if (includeFactoredElement) {
					Object factoredElement = adaptor.deleteChild(translatedElement, 0);
					adaptor.addChild(root, factoredElement);
				}
			}
			adaptor.addChild(root, translatedElement);
			adaptor.addChild(root, closure);
			return root;
		}

		case ANTLRParser.CLOSURE:
		case ANTLRParser.OPTIONAL:
			// not yet supported
			if (mode.includeUnfactoredAlts()) {
				return element;
			}

			return null;

		case ANTLRParser.DOT:
			// ref to imported grammar, not yet supported
			if (mode.includeUnfactoredAlts()) {
				return element;
			}

			return null;

		case ANTLRParser.ACTION:
		case ANTLRParser.SEMPRED:
			if (mode.includeUnfactoredAlts()) {
				return element;
			}

			return null;

		case ANTLRParser.WILDCARD:
		case ANTLRParser.STRING_LITERAL:
		case ANTLRParser.TOKEN_REF:
		case ANTLRParser.NOT:
			// terminals
			if (mode.includeUnfactoredAlts()) {
				return element;
			}

			return null;

		case ANTLRParser.EPSILON:
			// empty tree
			if (mode.includeUnfactoredAlts()) {
				return element;
			}

			return null;

		default:
			// unknown
			return null;
		}
	}

	protected RuleVariants createLeftFactoredRuleVariant(Rule rule, String factoredElement) {
		RuleAST ast = (RuleAST)rule.ast.dupTree();
		BlockAST block = (BlockAST)ast.getFirstChildWithType(ANTLRParser.BLOCK);

		RuleAST unfactoredAst = null;
		BlockAST unfactoredBlock = null;

		if (translateLeftFactoredDecision(block, factoredElement, true, DecisionFactorMode.FULL_FACTOR, false)) {
			// all alternatives factored
		} else {
			ast = (RuleAST)rule.ast.dupTree();
			block = (BlockAST)ast.getFirstChildWithType(ANTLRParser.BLOCK);
			if (!translateLeftFactoredDecision(block, factoredElement, true, DecisionFactorMode.PARTIAL_FACTORED, false)) {
				// no left factored alts
				return RuleVariants.NONE;
			}

			unfactoredAst = (RuleAST)rule.ast.dupTree();
			unfactoredBlock = (BlockAST)unfactoredAst.getFirstChildWithType(ANTLRParser.BLOCK);
			if (!translateLeftFactoredDecision(unfactoredBlock, factoredElement, true, DecisionFactorMode.PARTIAL_UNFACTORED, false)) {
				throw new IllegalStateException("expected unfactored alts for partial factorization");
			}
		}

		/*
		 * factored elements
		 */
		{
			String variantName = ast.getChild(0).getText() + ATNSimulator.RULE_LF_VARIANT_MARKER + factoredElement;
			((GrammarAST)ast.getChild(0)).token = adaptor.createToken(ast.getChild(0).getType(), variantName);
			GrammarAST ruleParent = (GrammarAST)rule.ast.getParent();
			ruleParent.insertChild(rule.ast.getChildIndex() + 1, ast);
			ruleParent.freshenParentAndChildIndexes(rule.ast.getChildIndex());

			List<GrammarAST> alts = block.getAllChildrenWithType(ANTLRParser.ALT);
			Rule variant = new Rule(_g, ast.getChild(0).getText(), ast, alts.size());
			_g.defineRule(variant);
			for (int i = 0; i < alts.size(); i++) {
				variant.alt[i + 1].ast = (AltAST)alts.get(i);
			}
		}

		/*
		 * unfactored elements
		 */
		if (unfactoredAst != null) {
			String variantName = unfactoredAst.getChild(0).getText() + ATNSimulator.RULE_NOLF_VARIANT_MARKER + factoredElement;
			((GrammarAST)unfactoredAst.getChild(0)).token = adaptor.createToken(unfactoredAst.getChild(0).getType(), variantName);
			GrammarAST ruleParent = (GrammarAST)rule.ast.getParent();
			ruleParent.insertChild(rule.ast.getChildIndex() + 1, unfactoredAst);
			ruleParent.freshenParentAndChildIndexes(rule.ast.getChildIndex());

			List<GrammarAST> alts = unfactoredBlock.getAllChildrenWithType(ANTLRParser.ALT);
			Rule variant = new Rule(_g, unfactoredAst.getChild(0).getText(), unfactoredAst, alts.size());
			_g.defineRule(variant);
			for (int i = 0; i < alts.size(); i++) {
				variant.alt[i + 1].ast = (AltAST)alts.get(i);
			}
		}

		/*
		 * result
		 */
		return unfactoredAst == null ? RuleVariants.FULLY_FACTORED : RuleVariants.PARTIALLY_FACTORED;
	}

	protected enum DecisionFactorMode {

		/**
		 * Alternatives are factored where possible; results are combined, and
		 * both factored and unfactored alternatives are included in the result.
		 */
		COMBINED_FACTOR(true, true),
		/**
		 * Factors all alternatives of the decision. The factoring fails if the
		 * decision contains one or more alternatives which cannot be factored.
		 */
		FULL_FACTOR(true, false),
		/**
		 * Attempts to factor all alternatives of the decision. Alternatives
		 * which could not be factored are not included in the result.
		 */
		PARTIAL_FACTORED(true, false),
		/**
		 * Attempts to factor all alternatives of the decision, and returns the
		 * remaining unfactored alternatives. Alternatives which could be
		 * factored are not included in the result.
		 */
		PARTIAL_UNFACTORED(false, true),
		;

		private final boolean includeFactoredAlts;
		private final boolean includeUnfactoredAlts;

		private DecisionFactorMode(boolean includeFactoredAlts, boolean includeUnfactoredAlts) {
			this.includeFactoredAlts = includeFactoredAlts;
			this.includeUnfactoredAlts = includeUnfactoredAlts;
		}

		public boolean includeFactoredAlts() {
			return includeFactoredAlts;
		}

		public boolean includeUnfactoredAlts() {
			return includeUnfactoredAlts;
		}
	}

	protected enum RuleVariants {
		NONE,
		PARTIALLY_FACTORED,
		FULLY_FACTORED,
	}
}
