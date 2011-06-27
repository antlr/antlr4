package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.CoreOutputModelFactory;
import org.antlr.v4.codegen.model.decl.*;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.atn.RuleTransition;
import org.antlr.v4.tool.*;

import java.util.*;

/** */
public class InvokeRule extends RuleElement implements LabeledOp {
	public String name;
	public List<Decl> labels = new ArrayList<Decl>();
	public String argExprs;
	public String ctxName;

	public InvokeRule(CoreOutputModelFactory factory, GrammarAST ast, GrammarAST labelAST) {
		super(factory, ast);
		if ( ast.atnState!=null ) {
			RuleTransition ruleTrans = (RuleTransition)ast.atnState.transition(0);
			stateNumber = ast.atnState.stateNumber;
		}

		this.name = ast.getText();
		Rule r = factory.g.getRule(name);
		ctxName = factory.gen.target.getRuleFunctionContextStructName(r);

		if ( labelAST!=null ) {
			// for x=r, define <rule-context-type> x and list_x
			String label = labelAST.getText();
			RuleContextDecl d = new RuleContextDecl(factory,label,ctxName);
			labels.add(d);
			factory.currentRule.peek().addContextDecl(d);
			if ( labelAST.parent.getType() == ANTLRParser.PLUS_ASSIGN  ) {
				String listLabel = factory.gen.target.getListLabel(label);
				RuleContextListDecl l = new RuleContextListDecl(factory, listLabel, d);
				factory.currentRule.peek().addContextDecl(l);
			}
		}
		if ( ast.getChildCount()>0 ) {
			argExprs = ast.getChild(0).getText();
		}

		// If action refs rule as rulename not label, we need to define implicit label
		if ( factory.currentAlt.ruleRefsInActions.containsKey(ast.getText()) ) {
			String label = factory.gen.target.getImplicitRuleLabel(ast.getText());
			RuleContextDecl d = new RuleContextDecl(factory,label,ctxName);
			labels.add(d);
			factory.currentRule.peek().addContextDecl(d);
		}

//		LinearApproximator approx = new LinearApproximator(factory.g, ATN.INVALID_DECISION_NUMBER);
//		RuleTransition call = (RuleTransition)ast.atnState.transition(0);
//		IntervalSet fset = approx.FIRST(call.followState);
//		System.out.println("follow rule ref "+name+"="+fset);
//		follow = factory.createFollowBitSet(ast, fset);
//		factory.defineBitSet(follow);
	}

	public List<Decl> getLabels() {
		return labels;
	}
}
