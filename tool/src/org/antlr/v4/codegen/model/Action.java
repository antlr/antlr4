/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
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
