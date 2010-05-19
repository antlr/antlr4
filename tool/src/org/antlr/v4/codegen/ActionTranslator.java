package org.antlr.v4.codegen;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;
import org.antlr.v4.codegen.src.RuleFunction;
import org.antlr.v4.codegen.src.actions.*;
import org.antlr.v4.parse.ActionSplitter;
import org.antlr.v4.parse.ActionSplitterListener;
import org.antlr.v4.tool.ActionAST;
import org.antlr.v4.tool.Attribute;

import java.util.ArrayList;
import java.util.List;

/** */
public class ActionTranslator implements ActionSplitterListener {
	ActionAST node;
	RuleFunction rf;
	List<ActionChunk> chunks = new ArrayList<ActionChunk>();

	public ActionTranslator(ActionAST node) {
		this.node = node;
	}

	public static List<ActionChunk> translateAction(RuleFunction rf, Token tokenWithinAction, ActionAST node) {
		ActionTranslator translator = new ActionTranslator(node);
		translator.rf = rf;
		System.out.println("translate "+tokenWithinAction);
		ANTLRStringStream in = new ANTLRStringStream(tokenWithinAction.getText());
		in.setLine(tokenWithinAction.getLine());
		in.setCharPositionInLine(tokenWithinAction.getCharPositionInLine());
		ActionSplitter trigger = new ActionSplitter(in, translator);
		// forces eval, triggers listener methods
		trigger.getActionTokens();
		return translator.chunks;
	}

	public void attr(String expr, Token x) {
		System.out.println("attr "+x);
		Attribute a = node.resolver.resolveToAttribute(x.getText(), node);
		switch ( a.dict.type ) {
			case ARG: chunks.add(new ArgRef(x.getText())); break;
			case RET: chunks.add(new RetValueRef(x.getText())); break;
//			case PREDEFINED_RULE: chunks.add(new RetValueRef(x.getText())); break;
//			case PREDEFINED_LEXER_RULE: chunks.add(new RetValueRef(x.getText())); break;
//			case PREDEFINED_TREE_RULE: chunks.add(new RetValueRef(x.getText())); break;
//			case GLOBAL_SCOPE: chunks.add(new RetValueRef(x.getText())); break;
//			case RULE_SCOPE: chunks.add(new RetValueRef(x.getText())); break;
//			case TOKEN: chunks.add(new RetValueRef(x.getText())); break;
		}
	}

	public void setQualifiedAttr(String expr, Token x, Token y, Token rhs) {
	}

	public void qualifiedAttr(String expr, Token x, Token y) {
	}

	public void setAttr(String expr, Token x, Token rhs) {
		System.out.println("setAttr "+x+" "+rhs);
		List<ActionChunk> exprchunks = translateAction(rf,rhs,node);
		chunks.add(new SetAttr(x.getText(), exprchunks));
	}

	public void setDynamicScopeAttr(String expr, Token x, Token y, Token rhs) {
	}

	public void dynamicScopeAttr(String expr, Token x, Token y) {
	}

	public void setDynamicNegativeIndexedScopeAttr(String expr, Token x, Token y, Token index, Token rhs) {
	}

	public void dynamicNegativeIndexedScopeAttr(String expr, Token x, Token y, Token index) {
	}

	public void setDynamicAbsoluteIndexedScopeAttr(String expr, Token x, Token y, Token index, Token rhs) {
	}

	public void dynamicAbsoluteIndexedScopeAttr(String expr, Token x, Token y, Token index) {
	}

	public void templateInstance(String expr) {
	}

	public void indirectTemplateInstance(String expr) {
	}

	public void setExprAttribute(String expr) {
	}

	public void setAttribute(String expr) {
	}

	public void templateExpr(String expr) {
	}

	public void unknownSyntax(Token t) {
	}

	public void text(String text) {
		chunks.add(new ActionText(text));
	}
}
