/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.codegen;

import org.antlr.runtime.*;
import org.antlr.v4.codegen.model.RuleFunction;
import org.antlr.v4.codegen.model.chunk.*;
import org.antlr.v4.codegen.model.decl.StructDecl;
import org.antlr.v4.parse.*;
import org.antlr.v4.tool.*;
import org.antlr.v4.tool.ast.ActionAST;

import java.lang.reflect.Constructor;
import java.util.*;

/** */
public class ActionTranslator implements ActionSplitterListener {
	public static final Map<String, Class> thisRulePropToModelMap = new HashMap<String, Class>() {{
		put("start", ThisRulePropertyRef_start.class);
		put("stop",  ThisRulePropertyRef_stop.class);
		put("text",  ThisRulePropertyRef_text.class);
        put("ctx",   ThisRulePropertyRef_ctx.class);
	}};

	public static final Map<String, Class> rulePropToModelMap = new HashMap<String, Class>() {{
		put("start", RulePropertyRef_start.class);
		put("stop",  RulePropertyRef_stop.class);
		put("text",  RulePropertyRef_text.class);
        put("ctx",   RulePropertyRef_ctx.class);
	}};

	public static final Map<String, Class> tokenPropToModelMap = new HashMap<String, Class>() {{
		put("text",  TokenPropertyRef_text.class);
		put("type",  TokenPropertyRef_type.class);
		put("line",  TokenPropertyRef_line.class);
		put("index", TokenPropertyRef_index.class);
		put("pos",   TokenPropertyRef_pos.class);
		put("channel", TokenPropertyRef_channel.class);
		put("int",   TokenPropertyRef_int.class);
	}};

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
		if ( action.charAt(0)=='{' ) {
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
		System.out.println("label="+altLabel);
		if ( rf!=null ) translator.nodeContext = rf.ruleCtx;
		if ( altLabel!=null ) translator.nodeContext = rf.altLabelCtxs.get(altLabel);
		ANTLRStringStream in = new ANTLRStringStream(action);
		in.setLine(tokenWithinAction.getLine());
		in.setCharPositionInLine(tokenWithinAction.getCharPositionInLine());
		ActionSplitter trigger = new ActionSplitter(in, translator);
		// forces eval, triggers listener methods
		trigger.getActionTokens();
		return translator.chunks;
	}

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

	/** $x.y = expr; */
	public void setQualifiedAttr(String expr, Token x, Token y, Token rhs) {
		gen.g.tool.log("action-translator", "setQAttr "+x+"."+y+"="+rhs);
		// x has to be current rule; just set y attr
		List<ActionChunk> rhsChunks = translateActionChunk(factory,rf,rhs.getText(),node);
		chunks.add(new SetAttr(nodeContext,y.getText(), rhsChunks));
	}

	public void qualifiedAttr(String expr, Token x, Token y) {
		gen.g.tool.log("action-translator", "qattr "+x+"."+y);
		Attribute a = node.resolver.resolveToAttribute(x.getText(), y.getText(), node);
		switch ( a.dict.type ) {
			case ARG: chunks.add(new ArgRef(nodeContext,y.getText())); break; // has to be current rule
			case RET:
				if ( factory.getCurrentRuleFunction()!=null &&
					 factory.getCurrentRuleFunction().name.equals(x.getText()) )
				{
					chunks.add(new RetValueRef(rf.ruleCtx, y.getText())); break;
				}
				else {
					chunks.add(new QRetValueRef(nodeContext, getRuleLabel(x.getText()), y.getText())); break;
				}
			case PREDEFINED_RULE:
				if ( factory.getCurrentRuleFunction()!=null &&
					 factory.getCurrentRuleFunction().name.equals(x.getText()) )
				{
					chunks.add(getRulePropertyRef(y));
				}
				else {
					chunks.add(getRulePropertyRef(x, y));
				}
				break;
			case TOKEN:
				chunks.add(getTokenPropertyRef(x, y));
				break;
		}
	}

	public void setAttr(String expr, Token x, Token rhs) {
		gen.g.tool.log("action-translator", "setAttr "+x+" "+rhs);
		List<ActionChunk> rhsChunks = translateActionChunk(factory,rf,rhs.getText(),node);
		SetAttr s = new SetAttr(nodeContext, x.getText(), rhsChunks);
		if ( factory.getGrammar().isLexer() ) s = new LexerSetAttr(nodeContext, x.getText(), rhsChunks);
		chunks.add(s);
	}

	public void nonLocalAttr(String expr, Token x, Token y) {
		gen.g.tool.log("action-translator", "nonLocalAttr "+x+"::"+y);
		Rule r = factory.getGrammar().getRule(x.getText());
		chunks.add(new NonLocalAttrRef(nodeContext, x.getText(), y.getText(), r.index));
	}

	public void setNonLocalAttr(String expr, Token x, Token y, Token rhs) {
		gen.g.tool.log("action-translator", "setNonLocalAttr "+x+"::"+y+"="+rhs);
		Rule r = factory.getGrammar().getRule(x.getText());
		List<ActionChunk> rhsChunks = translateActionChunk(factory,rf,rhs.getText(),node);
		SetNonLocalAttr s = new SetNonLocalAttr(nodeContext, x.getText(), y.getText(), r.index, rhsChunks);
		chunks.add(s);
	}

	public void unknownSyntax(Token t) {
	}

	public void text(String text) {
		chunks.add(new ActionText(nodeContext,text));
	}

	TokenPropertyRef getTokenPropertyRef(Token x, Token y) {
		try {
			Class c = tokenPropToModelMap.get(y.getText());
			Constructor ctor = c.getConstructor(new Class[] {StructDecl.class, String.class});
			TokenPropertyRef ref =
				(TokenPropertyRef)ctor.newInstance(nodeContext, getTokenLabel(x.getText()));
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
			Class c = thisRulePropToModelMap.get(prop.getText());
			Constructor ctor = c.getConstructor(new Class[] {StructDecl.class, String.class});
			RulePropertyRef ref =
				(RulePropertyRef)ctor.newInstance(nodeContext, getRuleLabel(prop.getText()));
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
			Class c = rulePropToModelMap.get(prop.getText());
			Constructor ctor = c.getConstructor(new Class[] {StructDecl.class, String.class});
			RulePropertyRef ref =
				(RulePropertyRef)ctor.newInstance(nodeContext, getRuleLabel(x.getText()));
			return ref;
		}
		catch (Exception e) {
			g.tool.errMgr.toolError(ErrorType.INTERNAL_ERROR, e, prop.getText());
		}
		return null;
	}

	public String getTokenLabel(String x) {
		if ( node.resolver.resolvesToLabel(x, node) ) return x;
		return factory.getGenerator().target.getImplicitTokenLabel(x);
	}

	public String getRuleLabel(String x) {
		if ( node.resolver.resolvesToLabel(x, node) ) return x;
		return factory.getGenerator().target.getImplicitRuleLabel(x);
	}

}
