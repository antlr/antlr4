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

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.model.decl.RuleContextDecl;
import org.antlr.v4.codegen.model.decl.RuleContextListDecl;
import org.antlr.v4.codegen.model.decl.StructDecl;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.tool.LeftRecursiveRule;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.GrammarAST;

public class LeftRecursiveRuleFunction extends RuleFunction {
	public LeftRecursiveRuleFunction(OutputModelFactory factory, LeftRecursiveRule r) {
		super(factory, r);

		CodeGenerator gen = factory.getGenerator();
		// Since we delete x=lr, we have to manually add decls for all labels
		// on left-recur refs to proper structs
		for (Pair<GrammarAST,String> pair : r.leftRecursiveRuleRefLabels) {
			GrammarAST idAST = pair.a;
			String altLabel = pair.b;
			String label = idAST.getText();
			GrammarAST rrefAST = (GrammarAST)idAST.getParent().getChild(1);
			if ( rrefAST.getType() == ANTLRParser.RULE_REF ) {
				Rule targetRule = factory.getGrammar().getRule(rrefAST.getText());
				String ctxName = gen.getTarget().getRuleFunctionContextStructName(targetRule);
				RuleContextDecl d;
				if (idAST.getParent().getType() == ANTLRParser.ASSIGN) {
					d = new RuleContextDecl(factory, label, ctxName);
				}
				else {
					d = new RuleContextListDecl(factory, label, ctxName);
				}

				StructDecl struct = ruleCtx;
				if ( altLabelCtxs!=null ) {
					StructDecl s = altLabelCtxs.get(altLabel);
					if ( s!=null ) struct = s; // if alt label, use subctx
				}
				struct.addDecl(d); // stick in overall rule's ctx
			}
		}
	}
}
