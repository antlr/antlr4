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
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.model.chunk.ActionChunk;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.GrammarAST;

import java.util.List;

/** */
public class SemPred extends Action {
	public String msg;       // user-specified string in fail grammar option
	public String predicate; // the predicate string with { }? stripped

	/** user-specified action in fail grammar option */
	@ModelElement public List<ActionChunk> failChunks;

	public SemPred(OutputModelFactory factory, ActionAST ast) {
		super(factory,ast);
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
																		  true);
		}
	}
}
