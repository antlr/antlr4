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
import org.antlr.v4.runtime.misc.Interval;
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
			if (!Character.isUpperCase(r.name.charAt(0))) {
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
						throw new UnsupportedOperationException("not yet implemented.");
					}

					if (!translateLeftFactoredDecision(block, rules[0], true)) {
						// couldn't translate the decision
						continue;
					}

					translatedBlocks.add(block);
				}
			}
		}
	}

	protected boolean expandOptionalQuantifiersForBlock(GrammarAST block) {
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

		return alt;
	}

	protected boolean translateLeftFactoredDecision(GrammarAST block, String factoredRule, boolean outerRule) {
		if (!expandOptionalQuantifiersForBlock(block)) {
			return false;
		}

		List<GrammarAST> alternatives = block.getAllChildrenWithType(ANTLRParser.ALT);
		List<GrammarAST> translatedAlternatives = new ArrayList<GrammarAST>();
		IntervalSet translatedIntervals = new IntervalSet();
		for (GrammarAST alternative : alternatives) {
			GrammarAST translatedAlt = translateLeftFactoredAlternative(alternative, factoredRule, outerRule);
			if (translatedAlt != null) {
				translatedIntervals.add(alternative.getChildIndex());
			}

			translatedAlternatives.add(translatedAlt);
		}

		if (translatedIntervals.isNil()) {
			return false;
		}

		if (!outerRule) {
			if (translatedIntervals.size() != alternatives.size()) {
				return false;
			}

			for (GrammarAST translatedAlt : translatedAlternatives) {
				if (translatedAlt.getChildCount() == 0) {
					adaptor.addChild(translatedAlt, adaptor.create(ANTLRParser.EPSILON, "EPSILON"));
				}
			}

			return true;
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
		for (int i = translatedIntervals.getIntervals().size() - 1; i >= 0; i--) {
			Interval interval = translatedIntervals.getIntervals().get(i);
			GrammarAST factoredElement = (GrammarAST)translatedAlternatives.get(interval.a).getChild(0);
			for (int j = interval.a; j <= interval.b; j++) {
				translatedAlternatives.get(j).deleteChild(0);
			}

			GrammarAST block1 = new AltAST(adaptor.createToken(ANTLRParser.ALT, "ALT"));
			block1.addChild(factoredElement);

			GrammarAST block2 = new BlockAST(adaptor.createToken(ANTLRParser.BLOCK, "BLOCK"));
			block1.addChild(block2);
			for (int j = interval.a; j <= interval.b; j++) {
				GrammarAST translatedAlt = translatedAlternatives.get(j);
				if (adaptor.getChildCount(translatedAlt) == 0) {
					adaptor.addChild(translatedAlt, adaptor.create(ANTLRParser.EPSILON, "EPSILON"));
				}

				adaptor.addChild(block2, translatedAlt);
			}

			GrammarAST root = adaptor.nil();
			root.addChild(block1);
			block.replaceChildren(interval.a, interval.b, root);
		}

		if (outerRule && block.getParent() instanceof RuleAST) {
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

	protected GrammarAST translateLeftFactoredAlternative(GrammarAST alternative, String factoredRule, boolean outerRule) {
		if (alternative.getChild(0).getType() == ANTLRParser.EPSILON) {
			return null;
		}

		GrammarAST translatedElement = translateLeftFactoredElement((GrammarAST)alternative.getChild(0), factoredRule, outerRule);
		if (translatedElement == null) {
			return null;
		}

		alternative.replaceChildren(0, 0, translatedElement);
		return alternative;
	}

	protected GrammarAST translateLeftFactoredElement(GrammarAST element, String factoredRule, boolean outerRule) {
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

			GrammarAST translatedChildElement = translateLeftFactoredElement((GrammarAST)element.getChild(1), factoredRule, outerRule);
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
				if (outerRule) {
					// this element is already left factored
					return element;
				}

				GrammarAST root = adaptor.nil();
				root.addChild(adaptor.create(Token.EPSILON, "dummy"));
				root.deleteChild(0);
				return root;
			}

			Rule targetRule = _rules.get(element.getToken().getText());
			if (targetRule == null) {
				return null;
			}

			if (!createLeftFactoredRuleVariant(targetRule, factoredRule)) {
				return null;
			}

			RuleRefAST ruleRefAST = (RuleRefAST)element;
			element.setText(element.getText() + ATNSimulator.RULE_VARIANT_MARKER + factoredRule);
//			ruleRefAST.addLeftFactoredElement(factoredRule);

			GrammarAST root = adaptor.nil();

			if (outerRule) {
				RuleRefAST factoredRuleRef = new RuleRefAST(adaptor.createToken(ANTLRParser.RULE_REF, factoredRule));
				if (outerRule) {
					factoredRuleRef.setOption(SUPPRESS_ACCESSOR, adaptor.create(ANTLRParser.ID, "true"));
				}

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
			if (!translateLeftFactoredDecision(cloned, factoredRule, outerRule)) {
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
			GrammarAST translatedElement = translateLeftFactoredElement(originalChildElement.dupTree(), factoredRule, outerRule);
			if (translatedElement == null) {
				return null;
			}

			if (!translatedElement.isNil() || translatedElement.getChildCount() != 2) {
				throw new UnsupportedOperationException("not yet implemented");
			}

			GrammarAST closure = new StarBlockAST(ANTLRParser.CLOSURE, adaptor.createToken(ANTLRParser.CLOSURE, "CLOSURE"), null);
			adaptor.addChild(closure, originalChildElement);

			GrammarAST root = adaptor.nil();
			Object factoredElement = adaptor.deleteChild(translatedElement, 0);
			if (outerRule) {
				adaptor.addChild(root, factoredElement);
			}
			adaptor.addChild(root, translatedElement);
			adaptor.addChild(root, closure);
			return root;
		}

		case ANTLRParser.CLOSURE:
		case ANTLRParser.OPTIONAL:
			// not yet supported
			return null;

		case ANTLRParser.DOT:
			// ref to imported grammar, not yet supported
			return null;

		case ANTLRParser.ACTION:
		case ANTLRParser.SEMPRED:
			return null;

		case ANTLRParser.WILDCARD:
		case ANTLRParser.STRING_LITERAL:
		case ANTLRParser.TOKEN_REF:
		case ANTLRParser.NOT:
			// terminals
			return null;

		case ANTLRParser.EPSILON:
			// empty tree
			return null;

		default:
			// unknown
			return null;
		}
	}

	protected boolean createLeftFactoredRuleVariant(Rule rule, String factoredElement) {
		RuleAST ast = (RuleAST)rule.ast.dupTree();
		BlockAST block = (BlockAST)ast.getFirstChildWithType(ANTLRParser.BLOCK);
		if (!translateLeftFactoredDecision(block, factoredElement, false)) {
			return false;
		}

		String variantName = ast.getChild(0).getText() + ATNSimulator.RULE_VARIANT_MARKER + factoredElement;
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

		return true;
	}
}
