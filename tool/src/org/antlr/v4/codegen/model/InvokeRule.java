/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.ActionTranslator;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.codegen.ParserFactory;
import org.antlr.v4.codegen.model.chunk.ActionChunk;
import org.antlr.v4.codegen.model.decl.Decl;
import org.antlr.v4.codegen.model.decl.RuleContextDecl;
import org.antlr.v4.codegen.model.decl.RuleContextListDecl;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.atn.RuleTransition;
import org.antlr.v4.runtime.misc.OrderedHashSet;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.GrammarAST;

import java.util.List;

/** */
public class InvokeRule extends RuleElement implements LabeledOp {
	public String name;
	public OrderedHashSet<Decl> labels = new OrderedHashSet<Decl>(); // TODO: should need just 1
	public String ctxName;

	@ModelElement public List<ActionChunk> argExprsChunks;

	public InvokeRule(ParserFactory factory, GrammarAST ast, GrammarAST labelAST) {
		super(factory, ast);
		if ( ast.atnState!=null ) {
			RuleTransition ruleTrans = (RuleTransition)ast.atnState.transition(0);
			stateNumber = ast.atnState.stateNumber;
		}

		this.name = ast.getText();
		CodeGenerator gen = factory.getGenerator();
		Rule r = factory.getGrammar().getRule(name);
		ctxName = gen.getTarget().getRuleFunctionContextStructName(r);

		// TODO: move to factory
		RuleFunction rf = factory.getCurrentRuleFunction();
		if ( labelAST!=null ) {
			// for x=r, define <rule-context-type> x and list_x
			String label = labelAST.getText();
			if ( labelAST.parent.getType() == ANTLRParser.PLUS_ASSIGN  ) {
				factory.defineImplicitLabel(ast, this);
				String listLabel = gen.getTarget().getListLabel(label);
				RuleContextListDecl l = new RuleContextListDecl(factory, listLabel, ctxName);
				rf.addContextDecl(ast.getAltLabel(), l);
			}
			else {
				RuleContextDecl d = new RuleContextDecl(factory,label,ctxName);
				labels.add(d);
				rf.addContextDecl(ast.getAltLabel(), d);
			}
		}

		ActionAST arg = (ActionAST)ast.getFirstChildWithType(ANTLRParser.ARG_ACTION);
		if ( arg != null ) {
			argExprsChunks = ActionTranslator.translateAction(factory, rf, arg.token, arg);
		}

		// If action refs rule as rulename not label, we need to define implicit label
		if ( factory.getCurrentOuterMostAlt().ruleRefsInActions.containsKey(ast.getText()) ) {
			String label = gen.getTarget().getImplicitRuleLabel(ast.getText());
			RuleContextDecl d = new RuleContextDecl(factory,label,ctxName);
			labels.add(d);
			rf.addContextDecl(ast.getAltLabel(), d);
		}
	}

	@Override
	public List<Decl> getLabels() {
		return labels.elements();
	}
}
