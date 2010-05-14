package org.antlr.v4.codegen.src;

import org.antlr.v4.analysis.LinearApproximator;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.tool.GrammarAST;

/** */
public class InvokeRule extends SrcOp implements LabeledOp {
	public String name;
	public String label;
	public String args;
	public BitSetDecl follow;

	public InvokeRule(OutputModelFactory factory, GrammarAST ast, GrammarAST labelAST) {
		this.factory = factory;
		this.ast = ast;
		this.name = ast.getText();
		if ( labelAST!=null ) {
			label = labelAST.getText();
//			TokenDecl d = new TokenDecl(label);
//			factory.currentRule.peek().addDecl(d);			
			if ( labelAST.parent.getType() == ANTLRParser.PLUS_ASSIGN  ) {
//				TokenListDecl l = new TokenListDecl(factory.getListLabel(label));
//				factory.currentRule.peek().addDecl(l);
			}			
		}
		if ( ast.getChildCount()>0 ) {
			args = ast.getChild(0).getText();
			// split and translate argAction

		}
		// compute follow
		LinearApproximator approx = new LinearApproximator(factory.g, -1);
		IntervalSet fset = approx.LOOK(ast.nfaState.transition(0).target);
		System.out.println("follow="+follow);
		follow = factory.createFollowBitSet(ast, fset);
		factory.defineBitSet(follow);
	}

	public String getLabel() {
		return label;
	}
}
