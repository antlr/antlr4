/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.*;
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

	public InvokeRule(OutputModelFactory factory, GrammarAST ast, GrammarAST labelAST) {
		super(factory, ast);
		if ( ast.atnState!=null ) {
			RuleTransition ruleTrans = (RuleTransition)ast.atnState.transition(0);
			stateNumber = ast.atnState.stateNumber;
		}

		this.name = ast.getText();
		CodeGenerator gen = factory.getGenerator();
		Rule r = factory.getGrammar().getRule(name);
		ctxName = gen.target.getRuleFunctionContextStructName(r);

		if ( labelAST!=null ) {
			// for x=r, define <rule-context-type> x and list_x
			String label = labelAST.getText();
			RuleContextDecl d = new RuleContextDecl(factory,label,ctxName);
			labels.add(d);
			factory.getCurrentRuleFunction().addContextDecl(d);
			if ( labelAST.parent.getType() == ANTLRParser.PLUS_ASSIGN  ) {
				String listLabel = gen.target.getListLabel(label);
				RuleContextListDecl l = new RuleContextListDecl(factory, listLabel, d);
				factory.getCurrentRuleFunction().addContextDecl(l);
			}
		}
		if ( ast.getChildCount()>0 ) {
			argExprs = ast.getChild(0).getText();
		}

		// If action refs rule as rulename not label, we need to define implicit label
		if ( factory.getCurrentAlt().ruleRefsInActions.containsKey(ast.getText()) ) {
			String label = gen.target.getImplicitRuleLabel(ast.getText());
			RuleContextDecl d = new RuleContextDecl(factory,label,ctxName);
			labels.add(d);
			factory.getCurrentRuleFunction().addContextDecl(d);
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
