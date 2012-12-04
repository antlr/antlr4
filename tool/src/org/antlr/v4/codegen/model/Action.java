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

import org.antlr.runtime.CommonToken;
import org.antlr.v4.codegen.ActionTranslator;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.model.chunk.ActionChunk;
import org.antlr.v4.codegen.model.chunk.ActionTemplate;
import org.antlr.v4.codegen.model.chunk.ActionText;
import org.antlr.v4.codegen.model.decl.StructDecl;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.tool.ast.ActionAST;
import org.stringtemplate.v4.ST;

import java.util.ArrayList;
import java.util.List;

/** */
public class Action extends RuleElement {
	@ModelElement public List<ActionChunk> chunks;

	public Action(OutputModelFactory factory, ActionAST ast) {
		super(factory,ast);
		RuleFunction rf = factory.getCurrentRuleFunction();
		if (ast != null) {
			chunks = ActionTranslator.translateAction(factory, rf, ast.token, ast);
		}
		else {
			chunks = new ArrayList<ActionChunk>();
		}
		//System.out.println("actions="+chunks);
	}

	public Action(OutputModelFactory factory, StructDecl ctx, String action) {
		super(factory,null);
		ActionAST ast = new ActionAST(new CommonToken(ANTLRParser.ACTION, action));
		RuleFunction rf = factory.getCurrentRuleFunction();
		if ( rf!=null ) { // we can translate
			ast.resolver = rf.rule;
			chunks = ActionTranslator.translateActionChunk(factory, rf, action, ast);
		}
		else {
			chunks = new ArrayList<ActionChunk>();
			chunks.add(new ActionText(ctx, action));
		}
	}

	public Action(OutputModelFactory factory, StructDecl ctx, ST actionST) {
		super(factory, null);
		chunks = new ArrayList<ActionChunk>();
		chunks.add(new ActionTemplate(ctx, actionST));
	}

}
