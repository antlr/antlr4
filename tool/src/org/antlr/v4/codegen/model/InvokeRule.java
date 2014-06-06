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
