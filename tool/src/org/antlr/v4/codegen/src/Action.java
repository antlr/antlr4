package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.ActionTranslator;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.src.actions.ActionChunk;
import org.antlr.v4.tool.ActionAST;
import org.antlr.v4.tool.GrammarAST;

import java.util.ArrayList;
import java.util.List;

/** */
public class Action extends SrcOp {
	public List<ActionChunk> chunks;
	public Action(OutputModelFactory factory, GrammarAST ast) {
		super(factory,ast);
		RuleFunction rf = factory.currentRule.peek();
		chunks = ActionTranslator.translateAction(factory,rf, ast.token, (ActionAST)ast);
		System.out.println("actions="+chunks);
	}

	@Override
	public List<String> getChildren() {
		final List<String> sup = super.getChildren();
		return new ArrayList<String>() {{
			if ( sup!=null ) addAll(sup);
			add("chunks");
		}};
	}

}
