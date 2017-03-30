/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;
import org.antlr.v4.codegen.model.RuleFunction;
import org.antlr.v4.codegen.model.chunk.ActionChunk;
import org.antlr.v4.codegen.model.chunk.ActionText;
import org.antlr.v4.codegen.model.chunk.ArgRef;
import org.antlr.v4.codegen.model.chunk.LabelRef;
import org.antlr.v4.codegen.model.chunk.ListLabelRef;
import org.antlr.v4.codegen.model.chunk.LocalRef;
import org.antlr.v4.codegen.model.chunk.NonLocalAttrRef;
import org.antlr.v4.codegen.model.chunk.QRetValueRef;
import org.antlr.v4.codegen.model.chunk.RetValueRef;
import org.antlr.v4.codegen.model.chunk.RulePropertyRef;
import org.antlr.v4.codegen.model.chunk.RulePropertyRef_ctx;
import org.antlr.v4.codegen.model.chunk.RulePropertyRef_parser;
import org.antlr.v4.codegen.model.chunk.RulePropertyRef_start;
import org.antlr.v4.codegen.model.chunk.RulePropertyRef_stop;
import org.antlr.v4.codegen.model.chunk.RulePropertyRef_text;
import org.antlr.v4.codegen.model.chunk.SetAttr;
import org.antlr.v4.codegen.model.chunk.SetNonLocalAttr;
import org.antlr.v4.codegen.model.chunk.ThisRulePropertyRef_ctx;
import org.antlr.v4.codegen.model.chunk.ThisRulePropertyRef_parser;
import org.antlr.v4.codegen.model.chunk.ThisRulePropertyRef_start;
import org.antlr.v4.codegen.model.chunk.ThisRulePropertyRef_stop;
import org.antlr.v4.codegen.model.chunk.ThisRulePropertyRef_text;
import org.antlr.v4.codegen.model.chunk.TokenPropertyRef;
import org.antlr.v4.codegen.model.chunk.TokenPropertyRef_channel;
import org.antlr.v4.codegen.model.chunk.TokenPropertyRef_index;
import org.antlr.v4.codegen.model.chunk.TokenPropertyRef_int;
import org.antlr.v4.codegen.model.chunk.TokenPropertyRef_line;
import org.antlr.v4.codegen.model.chunk.TokenPropertyRef_pos;
import org.antlr.v4.codegen.model.chunk.TokenPropertyRef_text;
import org.antlr.v4.codegen.model.chunk.TokenPropertyRef_type;
import org.antlr.v4.codegen.model.chunk.TokenRef;
import org.antlr.v4.codegen.model.decl.StructDecl;
import org.antlr.v4.parse.ActionSplitter;
import org.antlr.v4.parse.ActionSplitterListener;
import org.antlr.v4.tool.Attribute;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.ActionAST;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** */
public class ActionTranslator implements ActionSplitterListener {
	public static final Map<String, Class<? extends RulePropertyRef>> thisRulePropToModelMap =
		new HashMap<String, Class<? extends RulePropertyRef>>();
	static {
		thisRulePropToModelMap.put("start", ThisRulePropertyRef_start.class);
		thisRulePropToModelMap.put("stop",  ThisRulePropertyRef_stop.class);
		thisRulePropToModelMap.put("text",  ThisRulePropertyRef_text.class);
		thisRulePropToModelMap.put("ctx",   ThisRulePropertyRef_ctx.class);
		thisRulePropToModelMap.put("parser",  ThisRulePropertyRef_parser.class);
	}

	public static final Map<String, Class<? extends RulePropertyRef>> rulePropToModelMap =
		new HashMap<String, Class<? extends RulePropertyRef>>();
	static {
		rulePropToModelMap.put("start", RulePropertyRef_start.class);
		rulePropToModelMap.put("stop",  RulePropertyRef_stop.class);
		rulePropToModelMap.put("text",  RulePropertyRef_text.class);
		rulePropToModelMap.put("ctx",   RulePropertyRef_ctx.class);
		rulePropToModelMap.put("parser",  RulePropertyRef_parser.class);
	}

	public static final Map<String, Class<? extends TokenPropertyRef>> tokenPropToModelMap =
		new HashMap<String, Class<? extends TokenPropertyRef>>();
	static {
		tokenPropToModelMap.put("text",  TokenPropertyRef_text.class);
		tokenPropToModelMap.put("type",  TokenPropertyRef_type.class);
		tokenPropToModelMap.put("line",  TokenPropertyRef_line.class);
		tokenPropToModelMap.put("index", TokenPropertyRef_index.class);
		tokenPropToModelMap.put("pos",   TokenPropertyRef_pos.class);
		tokenPropToModelMap.put("channel", TokenPropertyRef_channel.class);
		tokenPropToModelMap.put("int",   TokenPropertyRef_int.class);
	}

	CodeGenerator gen;
	ActionAST node;
	RuleFunction rf;
	List<ActionChunk> chunks = new ArrayList<ActionChunk>();
	OutputModelFactory factory;
	StructDecl nodeContext;

	public ActionTranslator(OutputModelFactory factory, ActionAST node) {
		this.factory = factory;
		this.node = node;
		this.gen = factory.getGenerator();
	}

	public static String toString(List<ActionChunk> chunks) {
		StringBuilder buf = new StringBuilder();
		for (ActionChunk c : chunks) buf.append(c.toString());
		return buf.toString();
	}

	public static List<ActionChunk> translateAction(OutputModelFactory factory,
													RuleFunction rf,
													Token tokenWithinAction,
													ActionAST node)
	{
		String action = tokenWithinAction.getText();
		if ( action!=null && action.length()>0 && action.charAt(0)=='{' ) {
			int firstCurly = action.indexOf('{');
			int lastCurly = action.lastIndexOf('}');
			if ( firstCurly>=0 && lastCurly>=0 ) {
				action = action.substring(firstCurly+1, lastCurly); // trim {...}
			}
		}
		return translateActionChunk(factory, rf, action, node);
	}

	public static List<ActionChunk> translateActionChunk(OutputModelFactory factory,
														 RuleFunction rf,
														 String action,
														 ActionAST node)
	{
		Token tokenWithinAction = node.token;
		ActionTranslator translator = new ActionTranslator(factory, node);
		translator.rf = rf;
        factory.getGrammar().tool.log("action-translator", "translate " + action);
		String altLabel = node.getAltLabel();
		if ( rf!=null ) {
		    translator.nodeContext = rf.ruleCtx;
	        if ( altLabel!=null ) translator.nodeContext = rf.altLabelCtxs.get(altLabel);
		}
		ANTLRStringStream in = new ANTLRStringStream(action);
		in.setLine(tokenWithinAction.getLine());
		in.setCharPositionInLine(tokenWithinAction.getCharPositionInLine());
		ActionSplitter trigger = new ActionSplitter(in, translator);
		// forces eval, triggers listener methods
		trigger.getActionTokens();
		return translator.chunks;
	}

	@Override
	public void attr(String expr, Token x) {
		gen.g.tool.log("action-translator", "attr "+x);
		Attribute a = node.resolver.resolveToAttribute(x.getText(), node);
		if ( a!=null ) {
			switch ( a.dict.type ) {
				case ARG: chunks.add(new ArgRef(nodeContext,x.getText())); break;
				case RET: chunks.add(new RetValueRef(rf.ruleCtx, x.getText())); break;
				case LOCAL: chunks.add(new LocalRef(nodeContext,x.getText())); break;
				case PREDEFINED_RULE: chunks.add(getRulePropertyRef(x));	break;
			}
		}
		if ( node.resolver.resolvesToToken(x.getText(), node) ) {
			chunks.add(new TokenRef(nodeContext,getTokenLabel(x.getText()))); // $label
			return;
		}
		if ( node.resolver.resolvesToLabel(x.getText(), node) ) {
			chunks.add(new LabelRef(nodeContext,getTokenLabel(x.getText()))); // $x for x=ID etc...
			return;
		}
		if ( node.resolver.resolvesToListLabel(x.getText(), node) ) {
			chunks.add(new ListLabelRef(nodeContext,x.getText())); // $ids for ids+=ID etc...
			return;
		}
		Rule r = factory.getGrammar().getRule(x.getText());
		if ( r!=null ) {
			chunks.add(new LabelRef(nodeContext,getRuleLabel(x.getText()))); // $r for r rule ref
		}
	}

	@Override
	public void qualifiedAttr(String expr, Token x, Token y) {
		gen.g.tool.log("action-translator", "qattr "+x+"."+y);
		if ( node.resolver.resolveToAttribute(x.getText(), node)!=null ) {
			// must be a member access to a predefined attribute like $ctx.foo
			attr(expr, x);
			chunks.add(new ActionText(nodeContext, "."+y.getText()));
			return;
		}
		Attribute a = node.resolver.resolveToAttribute(x.getText(), y.getText(), node);
		if ( a==null ) {
			// Added in response to https://github.com/antlr/antlr4/issues/1211
			gen.g.tool.errMgr.grammarError(ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE,
			                               gen.g.fileName, x,
			                               x.getText(),
			                               "rule");
			return;
		}
		switch ( a.dict.type ) {
			case ARG: chunks.add(new ArgRef(nodeContext,y.getText())); break; // has to be current rule
			case RET:
				chunks.add(new QRetValueRef(nodeContext, getRuleLabel(x.getText()), y.getText()));
				break;
			case PREDEFINED_RULE:
				chunks.add(getRulePropertyRef(x, y));
				break;
			case TOKEN:
				chunks.add(getTokenPropertyRef(x, y));
				break;
		}
	}

	@Override
	public void setAttr(String expr, Token x, Token rhs) {
		gen.g.tool.log("action-translator", "setAttr "+x+" "+rhs);
		List<ActionChunk> rhsChunks = translateActionChunk(factory,rf,rhs.getText(),node);
		SetAttr s = new SetAttr(nodeContext, x.getText(), rhsChunks);
		chunks.add(s);
	}

	@Override
	public void nonLocalAttr(String expr, Token x, Token y) {
		gen.g.tool.log("action-translator", "nonLocalAttr "+x+"::"+y);
		Rule r = factory.getGrammar().getRule(x.getText());
		chunks.add(new NonLocalAttrRef(nodeContext, x.getText(), y.getText(), r.index));
	}

	@Override
	public void setNonLocalAttr(String expr, Token x, Token y, Token rhs) {
		gen.g.tool.log("action-translator", "setNonLocalAttr "+x+"::"+y+"="+rhs);
		Rule r = factory.getGrammar().getRule(x.getText());
		List<ActionChunk> rhsChunks = translateActionChunk(factory,rf,rhs.getText(),node);
		SetNonLocalAttr s = new SetNonLocalAttr(nodeContext, x.getText(), y.getText(), r.index, rhsChunks);
		chunks.add(s);
	}

	@Override
	public void text(String text) {
		chunks.add(new ActionText(nodeContext,text));
	}

	TokenPropertyRef getTokenPropertyRef(Token x, Token y) {
		try {
			Class<? extends TokenPropertyRef> c = tokenPropToModelMap.get(y.getText());
			Constructor<? extends TokenPropertyRef> ctor = c.getConstructor(StructDecl.class, String.class);
			TokenPropertyRef ref =
				ctor.newInstance(nodeContext, getTokenLabel(x.getText()));
			return ref;
		}
		catch (Exception e) {
			factory.getGrammar().tool.errMgr.toolError(ErrorType.INTERNAL_ERROR, e);
		}
		return null;
	}

	// $text
	RulePropertyRef getRulePropertyRef(Token prop) {
		try {
			Class<? extends RulePropertyRef> c = thisRulePropToModelMap.get(prop.getText());
			Constructor<? extends RulePropertyRef> ctor = c.getConstructor(StructDecl.class, String.class);
			RulePropertyRef ref =
				ctor.newInstance(nodeContext, getRuleLabel(prop.getText()));
			return ref;
		}
		catch (Exception e) {
			factory.getGrammar().tool.errMgr.toolError(ErrorType.INTERNAL_ERROR, e);
		}
		return null;
	}

	RulePropertyRef getRulePropertyRef(Token x, Token prop) {
		Grammar g = factory.getGrammar();
		try {
			Class<? extends RulePropertyRef> c = rulePropToModelMap.get(prop.getText());
			Constructor<? extends RulePropertyRef> ctor = c.getConstructor(StructDecl.class, String.class);
			RulePropertyRef ref =
				ctor.newInstance(nodeContext, getRuleLabel(x.getText()));
			return ref;
		}
		catch (Exception e) {
			g.tool.errMgr.toolError(ErrorType.INTERNAL_ERROR, e, prop.getText());
		}
		return null;
	}

	public String getTokenLabel(String x) {
		if ( node.resolver.resolvesToLabel(x, node) ) return x;
		return factory.getGenerator().getTarget().getImplicitTokenLabel(x);
	}

	public String getRuleLabel(String x) {
		if ( node.resolver.resolvesToLabel(x, node) ) return x;
		return factory.getGenerator().getTarget().getImplicitRuleLabel(x);
	}

}
