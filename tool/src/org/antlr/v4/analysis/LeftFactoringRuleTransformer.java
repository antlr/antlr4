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
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.AltAST;
import org.antlr.v4.tool.ast.BlockAST;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.GrammarRootAST;
import org.antlr.v4.tool.ast.RuleAST;
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
	private static final Logger LOGGER = Logger.getLogger(LeftFactoringRuleTransformer.class.getName());

	public GrammarRootAST _ast;
	public Map<String, Rule> _rules;
	public Grammar _g;
	public Tool _tool;

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
				Object leftFactoredRules = r.namedActions.get("leftfactor");
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

					if (!translateLeftFactoredDecision(block, rules)) {
						// couldn't translate the decision
						continue;
					}

					translatedBlocks.add(block);
				}
			}
		}
	}

	protected boolean translateLeftFactoredDecision(GrammarAST block, String[] rules) {
		if (block.getParent() instanceof RuleAST) {
			// not yet supported
			return false;
		}

		List<GrammarAST> alternatives = block.getAllChildrenWithType(ANTLRParser.ALT);
		List<GrammarAST> translatedAlternatives = new ArrayList<GrammarAST>();
		IntervalSet translatedIntervals = new IntervalSet();
		for (GrammarAST alternative : alternatives) {
			GrammarAST translatedAlt = translateLeftFactoredAlternative(alternative, rules);
			if (translatedAlt != null) {
				translatedIntervals.add(alternative.getChildIndex());
			}

			translatedAlternatives.add(translatedAlt);
		}

		if (translatedIntervals.isNil()) {
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
		GrammarASTAdaptor adaptor = new GrammarASTAdaptor();
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

		return true;
	}

	protected GrammarAST translateLeftFactoredAlternative(GrammarAST alternative, String[] rules) {
		if (alternative.getChild(0).getType() == ANTLRParser.EPSILON) {
			return null;
		}

		GrammarAST translatedElement = translateLeftFactoredElement((GrammarAST)alternative.getChild(0), rules);
		if (translatedElement == null) {
			return null;
		}

		alternative.replaceChildren(0, 0, translatedElement);
		return alternative;
	}

	protected GrammarAST translateLeftFactoredElement(GrammarAST element, String[] rules) {
		if (rules.length == 0) {
			return null;
		}

		GrammarASTAdaptor adaptor = new GrammarASTAdaptor();

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

			GrammarAST translatedChildElement = translateLeftFactoredElement((GrammarAST)element.getChild(1), rules);
			if (translatedChildElement == null) {
				return null;
			}

			if (!translatedChildElement.isNil() || translatedChildElement.getChildCount() != 2) {
				throw new UnsupportedOperationException("not yet implemented");
			}

			GrammarAST root = adaptor.nil();
			Object factoredElement = adaptor.deleteChild(translatedChildElement, 0);
			adaptor.addChild(root, factoredElement);
			adaptor.addChild(root, element);
			adaptor.replaceChildren(element, 1, 1, translatedChildElement);
			return root;
		}

		case ANTLRParser.RULE_REF:
			if (rules.length != 1) {
				throw new UnsupportedOperationException("not yet implemented");
			}

			if (rules[0].equals(element.getToken().getText())) {
				// this element is already left factored
				return element;
			}

			// not yet supported
			return null;

		case ANTLRParser.BLOCK:
			// not yet supported
			return null;

		case ANTLRParser.POSITIVE_CLOSURE:
		{
			/* a+
			 *
			 * =>
			 *
			 * factoredElement a_factored a*
			 */

			GrammarAST originalChildElement = (GrammarAST)element.getChild(0);
			GrammarAST translatedElement = translateLeftFactoredElement(originalChildElement.dupTree(), rules);
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
			adaptor.addChild(root, factoredElement);
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
}
