/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.ActionTranslator;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.model.chunk.ActionChunk;
import org.antlr.v4.runtime.atn.AbstractPredicateTransition;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.GrammarAST;

import java.util.List;

/** */
public class SemPred extends Action {
	/**
	 * The user-specified terminal option {@code fail}, if it was used and the
	 * value is a string literal. For example:
	 *
	 * <p>
	 * {@code {pred}?<fail='message'>}</p>
	 */
	public String msg;
	/**
	 * The predicate string with <code>{</code> and <code>}?</code> stripped from the ends.
	 */
	public String predicate;

	/**
	 * The translated chunks of the user-specified terminal option {@code fail},
	 * if it was used and the value is an action. For example:
	 *
	 * <p>
	 * {@code {pred}?<fail={"Java literal"}>}</p>
	 */
	@ModelElement public List<ActionChunk> failChunks;

	public SemPred(OutputModelFactory factory, ActionAST ast) {
		super(factory,ast);

		assert ast.atnState != null
			&& ast.atnState.getNumberOfTransitions() == 1
			&& ast.atnState.transition(0) instanceof AbstractPredicateTransition;

		GrammarAST failNode = ast.getOptionAST("fail");
		CodeGenerator gen = factory.getGenerator();
		predicate = ast.getText();
		if (predicate.startsWith("{") && predicate.endsWith("}?")) {
			predicate = predicate.substring(1, predicate.length() - 2);
		}
		predicate = gen.getTarget().getTargetStringLiteralFromString(predicate);

		if ( failNode==null ) return;

		if ( failNode instanceof ActionAST ) {
			ActionAST failActionNode = (ActionAST)failNode;
			RuleFunction rf = factory.getCurrentRuleFunction();
			failChunks = ActionTranslator.translateAction(factory, rf,
														  failActionNode.token,
														  failActionNode);
		}
		else {
			msg = gen.getTarget().getTargetStringLiteralFromANTLRStringLiteral(gen,
																		  failNode.getText(),
																		  true,
																		  true);
		}
	}
}
