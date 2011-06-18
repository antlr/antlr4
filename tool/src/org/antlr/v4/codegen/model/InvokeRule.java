package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.*;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.atn.RuleTransition;
import org.antlr.v4.tool.*;

import java.util.*;

/** */
public class InvokeRule extends RuleElement implements LabeledOp {
	public String name;
	public List<String> labels = new ArrayList<String>();
	public String argExprs;
	public BitSetDecl follow;
	public String ctxName;

	public InvokeRule(OutputModelFactory factory, GrammarAST ast, GrammarAST labelAST) {
		super(factory, ast);
		if ( ast.atnState!=null ) {
			RuleTransition ruleTrans = (RuleTransition)ast.atnState.transition(0);
			stateNumber = ast.atnState.stateNumber;
		}

		this.name = ast.getText();
		Rule r = factory.g.getRule(name);
		ctxName = factory.gen.target.getRuleFunctionContextStructName(r);

		if ( labelAST!=null ) {
			String label = labelAST.getText();
			labels.add(label);
			RuleContextDecl d = new RuleContextDecl(factory,label,ctxName);
			factory.currentRule.peek().addDecl(d);
			if ( labelAST.parent.getType() == ANTLRParser.PLUS_ASSIGN  ) {
//				TokenListDecl l = new TokenListDecl(factory.getListLabel(label));
//				factory.currentRule.peek().addDecl(l);
			}
		}
		if ( ast.getChildCount()>0 ) {
			argExprs = ast.getChild(0).getText();
		}

		// If action refs rule as rulename not label, we need to define implicit label
		if ( factory.currentAlt.ruleRefsInActions.containsKey(ast.getText()) ) {
			String label = factory.gen.target.getImplicitRuleLabel(ast.getText());
			labels.add(label);
			RuleContextDecl d = new RuleContextDecl(factory,label,ctxName);
			factory.currentRule.peek().addDecl(d);
		}

//		LinearApproximator approx = new LinearApproximator(factory.g, ATN.INVALID_DECISION_NUMBER);
//		RuleTransition call = (RuleTransition)ast.atnState.transition(0);
//		IntervalSet fset = approx.FIRST(call.followState);
//		System.out.println("follow rule ref "+name+"="+fset);
//		follow = factory.createFollowBitSet(ast, fset);
//		factory.defineBitSet(follow);
	}

	public List<String> getLabels() {
		return labels;
	}
}
