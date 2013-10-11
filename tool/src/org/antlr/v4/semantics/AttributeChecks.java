/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
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

package org.antlr.v4.semantics;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;
import org.antlr.v4.parse.ActionSplitter;
import org.antlr.v4.parse.ActionSplitterListener;
import org.antlr.v4.tool.Alternative;
import org.antlr.v4.tool.ErrorManager;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LabelElementPair;
import org.antlr.v4.tool.LabelType;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.GrammarAST;

import java.util.List;

/** Trigger checks for various kinds of attribute expressions.
 *  no side-effects.
 */
public class AttributeChecks implements ActionSplitterListener {
    public Grammar g;
    public Rule r;          // null if action outside of rule
    public Alternative alt; // null if action outside of alt; could be in rule
    public ActionAST node;
	public Token actionToken; // token within action
	public ErrorManager errMgr;

    public AttributeChecks(Grammar g, Rule r, Alternative alt, ActionAST node, Token actionToken) {
        this.g = g;
        this.r = r;
        this.alt = alt;
        this.node = node;
        this.actionToken = actionToken;
		this.errMgr = g.tool.errMgr;
    }

    public static void checkAllAttributeExpressions(Grammar g) {
        for (ActionAST act : g.namedActions.values()) {
            AttributeChecks checker = new AttributeChecks(g, null, null, act, act.token);
            checker.examineAction();
        }

        for (Rule r : g.rules.values()) {
            for (ActionAST a : r.namedActions.values()) {
                AttributeChecks checker = new AttributeChecks(g, r, null, a, a.token);
                checker.examineAction();
            }
            for (int i=1; i<=r.numberOfAlts; i++) {
                Alternative alt = r.alt[i];
                for (ActionAST a : alt.actions) {
                    AttributeChecks checker =
                        new AttributeChecks(g, r, alt, a, a.token);
                    checker.examineAction();
                }
            }
            for (GrammarAST e : r.exceptions) {
				ActionAST a = (ActionAST)e.getChild(1);
                AttributeChecks checker = new AttributeChecks(g, r, null, a, a.token);
                checker.examineAction();
			}
			if ( r.finallyAction!=null ) {
				AttributeChecks checker =
					new AttributeChecks(g, r, null, r.finallyAction, r.finallyAction.token);
				checker.examineAction();
			}
        }
    }

    public void examineAction() {
		//System.out.println("examine "+actionToken);
        ANTLRStringStream in = new ANTLRStringStream(actionToken.getText());
        in.setLine(actionToken.getLine());
        in.setCharPositionInLine(actionToken.getCharPositionInLine());
        ActionSplitter splitter = new ActionSplitter(in, this);
		// forces eval, triggers listener methods
        node.chunks = splitter.getActionTokens();
    }

    // LISTENER METHODS

	// $x.y
	@Override
	public void qualifiedAttr(String expr, Token x, Token y) {
		if ( g.isLexer() ) {
			errMgr.grammarError(ErrorType.ATTRIBUTE_IN_LEXER_ACTION,
								g.fileName, x, x.getText()+"."+y.getText(), expr);
			return;
		}
		if ( node.resolver.resolveToAttribute(x.getText(), node)!=null ) {
			// must be a member access to a predefined attribute like $ctx.foo
			attr(expr, x);
			return;
		}

		if ( node.resolver.resolveToAttribute(x.getText(), y.getText(), node)==null ) {
			Rule rref = isolatedRuleRef(x.getText());
			if ( rref!=null ) {
				if ( rref.args!=null && rref.args.get(y.getText())!=null ) {
					g.tool.errMgr.grammarError(ErrorType.INVALID_RULE_PARAMETER_REF,
											  g.fileName, y, y.getText(), rref.name, expr);
				}
				else {
					errMgr.grammarError(ErrorType.UNKNOWN_RULE_ATTRIBUTE,
											  g.fileName, y, y.getText(), rref.name, expr);
				}
			}
			else if ( !node.resolver.resolvesToAttributeDict(x.getText(), node) ) {
				errMgr.grammarError(ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE,
										  g.fileName, x, x.getText(), expr);
			}
			else {
				errMgr.grammarError(ErrorType.UNKNOWN_ATTRIBUTE_IN_SCOPE,
										  g.fileName, y, y.getText(), expr);
			}
		}
	}

	@Override
	public void setAttr(String expr, Token x, Token rhs) {
		if ( g.isLexer() ) {
			errMgr.grammarError(ErrorType.ATTRIBUTE_IN_LEXER_ACTION,
								g.fileName, x, x.getText(), expr);
			return;
		}
		if ( node.resolver.resolveToAttribute(x.getText(), node)==null ) {
			ErrorType errorType = ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE;
			if ( node.resolver.resolvesToListLabel(x.getText(), node) ) {
				// $ids for ids+=ID etc...
				errorType = ErrorType.ASSIGNMENT_TO_LIST_LABEL;
			}

			errMgr.grammarError(errorType,
								g.fileName, x, x.getText(), expr);
		}
		new AttributeChecks(g, r, alt, node, rhs).examineAction();
	}

	@Override
    public void attr(String expr, Token x) {
		if ( g.isLexer() ) {
			errMgr.grammarError(ErrorType.ATTRIBUTE_IN_LEXER_ACTION,
								g.fileName, x, x.getText(), expr);
			return;
		}
		if ( node.resolver.resolveToAttribute(x.getText(), node)==null ) {
			if ( node.resolver.resolvesToToken(x.getText(), node) ) {
				return; // $ID for token ref or label of token
			}
			if ( node.resolver.resolvesToListLabel(x.getText(), node) ) {
				return; // $ids for ids+=ID etc...
			}
			if ( isolatedRuleRef(x.getText())!=null ) {
				errMgr.grammarError(ErrorType.ISOLATED_RULE_REF,
									g.fileName, x, x.getText(), expr);
				return;
			}
			errMgr.grammarError(ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE,
								g.fileName, x, x.getText(), expr);
		}
	}

	@Override
	public void nonLocalAttr(String expr, Token x, Token y) {
		Rule r = g.getRule(x.getText());
		if ( r==null ) {
			errMgr.grammarError(ErrorType.UNDEFINED_RULE_IN_NONLOCAL_REF,
								g.fileName, x, x.getText(), y.getText(), expr);
		}
		else if ( r.resolveToAttribute(y.getText(), null)==null ) {
			errMgr.grammarError(ErrorType.UNKNOWN_RULE_ATTRIBUTE,
								g.fileName, y, y.getText(), x.getText(), expr);

		}
	}

	@Override
	public void setNonLocalAttr(String expr, Token x, Token y, Token rhs) {
		Rule r = g.getRule(x.getText());
		if ( r==null ) {
			errMgr.grammarError(ErrorType.UNDEFINED_RULE_IN_NONLOCAL_REF,
								g.fileName, x, x.getText(), y.getText(), expr);
		}
		else if ( r.resolveToAttribute(y.getText(), null)==null ) {
			errMgr.grammarError(ErrorType.UNKNOWN_RULE_ATTRIBUTE,
								g.fileName, y, y.getText(), x.getText(), expr);

		}
	}

	@Override
	public void text(String text) { }

	// don't care
	public void templateInstance(String expr) {   }
	public void indirectTemplateInstance(String expr) {   }
	public void setExprAttribute(String expr) {   }
	public void setSTAttribute(String expr) {  }
	public void templateExpr(String expr) {  }

	// SUPPORT

	public Rule isolatedRuleRef(String x) {
		if ( node.resolver instanceof Grammar ) return null;

		if ( x.equals(r.name) ) return r;
		List<LabelElementPair> labels = null;
		if ( node.resolver instanceof Rule ) {
			labels = r.getElementLabelDefs().get(x);
		}
		else if ( node.resolver instanceof Alternative ) {
			labels = ((Alternative)node.resolver).labelDefs.get(x);
		}
		if ( labels!=null ) {  // it's a label ref. is it a rule label?
			LabelElementPair anyLabelDef = labels.get(0);
			if ( anyLabelDef.type==LabelType.RULE_LABEL ) {
				return g.getRule(anyLabelDef.element.getText());
			}
		}
		if ( node.resolver instanceof Alternative ) {
			if ( ((Alternative)node.resolver).ruleRefs.get(x)!=null ) {
				return g.getRule(x);
			}
		}
        return null;
    }

}
