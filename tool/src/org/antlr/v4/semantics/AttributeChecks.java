package org.antlr.v4.semantics;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;
import org.antlr.v4.parse.ActionSplitter;
import org.antlr.v4.parse.ActionSplitterListener;
import org.antlr.v4.tool.*;

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
            for (ActionAST a : r.exceptionActions) {
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
		System.out.println(node.chunks);
    }

    // LISTENER METHODS
    
    public void setQualifiedAttr(String expr, Token x, Token y, Token rhs) {
		qualifiedAttr(expr, x, y);
        new AttributeChecks(g, r, alt, node, rhs).examineAction();
    }

	// $x.y
	public void qualifiedAttr(String expr, Token x, Token y) {
		if ( node.resolver.resolveToAttribute(x.getText(), y.getText(), node)==null ) {
			Rule rref = isolatedRuleRef(x.getText());
			if ( rref!=null ) {
				if ( rref!=null && rref.args!=null && rref.args.get(y.getText())!=null ) {
					g.tool.errMgr.grammarError(ErrorType.INVALID_RULE_PARAMETER_REF,
											  g.fileName, y, y.getText(), expr);
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

	public void setAttr(String expr, Token x, Token rhs) {
		if ( node.resolver.resolveToAttribute(x.getText(), node)==null ) {
            errMgr.grammarError(ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE,
                                      g.fileName, x, x.getText(), expr);
        }
        new AttributeChecks(g, r, alt, node, rhs).examineAction();
    }

    public void attr(String expr, Token x) {
		if ( node.resolver.resolveToAttribute(x.getText(), node)==null ) {
			if ( node.resolver.resolveToDynamicScope(x.getText(), node)!=null ) {
				return; // $S for scope S is ok
			}
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

    public void setDynamicScopeAttr(String expr, Token x, Token y, Token rhs) {
		//System.out.println("SET "+x+" :: "+y);
		dynamicScopeAttr(expr, x, y);
		new AttributeChecks(g, r, alt, node, rhs).examineAction();
	}

    public void dynamicScopeAttr(String expr, Token x, Token y) {
		//System.out.println(x+" :: "+y);
		AttributeDict s = node.resolver.resolveToDynamicScope(x.getText(), node);
		if ( s==null ) {
			errMgr.grammarError(ErrorType.UNKNOWN_DYNAMIC_SCOPE,
									  g.fileName, x, x.getText(), expr);
			return;
		}
		Attribute a = s.get(y.getText());
		if ( a==null ) {
			errMgr.grammarError(ErrorType.UNKNOWN_DYNAMIC_SCOPE_ATTRIBUTE,
									  g.fileName, y, x.getText(), y.getText(), expr);
		}
	}

	public void setDynamicNegativeIndexedScopeAttr(String expr, Token x, Token y,
												   Token index, Token rhs) {
		setDynamicScopeAttr(expr, x, y, rhs);
		new AttributeChecks(g, r, alt, node, index).examineAction();
	}

	public void dynamicNegativeIndexedScopeAttr(String expr, Token x, Token y,
												Token index) {
		dynamicScopeAttr(expr, x, y);
		new AttributeChecks(g, r, alt, node, index).examineAction();
	}

	public void setDynamicAbsoluteIndexedScopeAttr(String expr, Token x, Token y,
												   Token index, Token rhs) {
		setDynamicScopeAttr(expr, x, y, rhs);
		new AttributeChecks(g, r, alt, node, index).examineAction();
	}

    public void dynamicAbsoluteIndexedScopeAttr(String expr, Token x, Token y,
												Token index) {
		dynamicScopeAttr(expr, x, y);
		new AttributeChecks(g, r, alt, node, index).examineAction();
	}

    public void unknownSyntax(Token t) {
		errMgr.grammarError(ErrorType.INVALID_TEMPLATE_ACTION,
								  g.fileName, t, t.getText());
    }

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
			labels = r.getLabelDefs().get(x);
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
