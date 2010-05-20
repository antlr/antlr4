package org.antlr.v4.codegen.src;

import org.antlr.v4.analysis.LinearApproximator;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.RuleContextDecl;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.tool.GrammarAST;
import org.antlr.v4.tool.Rule;

import java.util.ArrayList;
import java.util.List;

/** */
public class InvokeRule extends SrcOp implements LabeledOp {
	public String name;
	public List<String> labels = new ArrayList<String>();
	public String argExprs;
	public BitSetDecl follow;
	public String ctxName;

	public InvokeRule(OutputModelFactory factory, GrammarAST ast, GrammarAST labelAST) {
		super(factory, ast);
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

		// If action refs as rulename not label, we need to define implicit label
		if ( factory.currentAlt.ruleRefsInActions.containsKey(ast.getText()) ) {
			String label = factory.gen.target.getImplicitRuleLabel(ast.getText());
			labels.add(label);
			RuleContextDecl d = new RuleContextDecl(factory,label,ctxName);
			factory.currentRule.peek().addDecl(d);
		}

		// compute follow
		LinearApproximator approx = new LinearApproximator(factory.g, -1);
		IntervalSet fset = approx.LOOK(ast.nfaState.transition(0).target);
		System.out.println("follow="+follow);
		follow = factory.createFollowBitSet(ast, fset);
		factory.defineBitSet(follow);
	}

	public List<String> getLabels() {
		return labels;
	}
}
