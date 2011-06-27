package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.*;
import org.antlr.v4.codegen.model.actions.ActionChunk;
import org.antlr.v4.tool.*;

import java.util.List;

/** */
public class Action extends RuleElement {
	@ModelElement public List<ActionChunk> chunks;

	public Action(OutputModelFactory factory, GrammarAST ast) {
		super(factory,ast);
		RuleFunction rf = factory.getCurrentRule();
		chunks = ActionTranslator.translateAction(factory, rf, ast.token, (ActionAST)ast);
		//System.out.println("actions="+chunks);
	}
}
