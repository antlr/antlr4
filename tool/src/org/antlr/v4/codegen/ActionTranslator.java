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

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;
import org.antlr.v4.codegen.model.RuleFunction;
import org.antlr.v4.codegen.model.actions.*;
import org.antlr.v4.parse.ActionSplitter;
import org.antlr.v4.parse.ActionSplitterListener;
import org.antlr.v4.tool.*;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** */
public class ActionTranslator implements ActionSplitterListener {
	public static final Map<String, Class> thisRulePropToModelMap = new HashMap<String, Class>() {{
		put("start", ThisRulePropertyRef_start.class);
		put("stop", ThisRulePropertyRef_stop.class);
		put("tree", ThisRulePropertyRef_tree.class);
		put("text", ThisRulePropertyRef_text.class);
		put("st", ThisRulePropertyRef_st.class);
	}};

	public static final Map<String, Class> rulePropToModelMap = new HashMap<String, Class>() {{
		put("start", RulePropertyRef_start.class);
		put("stop", RulePropertyRef_stop.class);
		put("tree", RulePropertyRef_tree.class);
		put("text", RulePropertyRef_text.class);
		put("st", RulePropertyRef_st.class);
	}};

	public static final Map<String, Class> treeRulePropToModelMap = rulePropToModelMap;

	public static final Map<String, Class> tokenPropToModelMap = new HashMap<String, Class>() {{
		put("text", TokenPropertyRef_text.class);
		put("type", TokenPropertyRef_type.class);
		put("line", TokenPropertyRef_line.class);
		put("index", TokenPropertyRef_index.class);
		put("pos", TokenPropertyRef_pos.class);
		put("channel", TokenPropertyRef_channel.class);
		put("tree", TokenPropertyRef_tree.class);
		put("int", TokenPropertyRef_int.class);
	}};

	CodeGenerator gen;
	ActionAST node;
	RuleFunction rf;
	List<ActionChunk> chunks = new ArrayList<ActionChunk>();
	OutputModelFactory factory;

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
//		else if ( action.charAt(0)=='"' ) {
//			int firstQuote = action.indexOf('"');
//			int lastQuote = action.lastIndexOf('"');
//			if ( firstQuote>=0 && lastQuote>=0 ) {
//				action = action.substring(firstQuote+1, lastQuote); // trim "..."
//			}
//		}
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
		System.out.println("translate " + action);
		ANTLRStringStream in = new ANTLRStringStream(action);
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
		if ( a!=null ) {
			switch ( a.dict.type ) {
				case ARG: chunks.add(new ArgRef(x.getText())); break;
				case RET: chunks.add(new RetValueRef(x.getText())); break;
				case LOCAL: chunks.add(new LocalRef(x.getText())); break;
				case PREDEFINED_RULE: chunks.add(getRulePropertyRef(x));	break;
				case PREDEFINED_TREE_RULE: chunks.add(getRulePropertyRef(x)); break;
			}
		}
		if ( node.resolver.resolvesToToken(x.getText(), node) ) {
			chunks.add(new TokenRef(getTokenLabel(x.getText()))); // $label
			return;
		}
		if ( node.resolver.resolvesToLabel(x.getText(), node) ) {
			chunks.add(new LabelRef(getTokenLabel(x.getText()))); // $x for x=ID etc...
			return;
		}
		if ( node.resolver.resolvesToListLabel(x.getText(), node) ) {
			chunks.add(new ListLabelRef(x.getText())); // $ids for ids+=ID etc...
			return;
		}
		Rule r = factory.getGrammar().getRule(x.getText());
		if ( r!=null ) {
			chunks.add(new LabelRef(getRuleLabel(x.getText()))); // $r for r rule ref
		}
	}

	/** $x.y = expr; */
	public void setQualifiedAttr(String expr, Token x, Token y, Token rhs) {
		System.out.println("setQAttr "+x+"."+y+"="+rhs);
		// x has to be current rule; just set y attr
		List<ActionChunk> rhsChunks = translateActionChunk(factory,rf,rhs.getText(),node);
		chunks.add(new SetAttr(y.getText(), rhsChunks));
	}

	public void qualifiedAttr(String expr, Token x, Token y) {
		System.out.println("qattr "+x+"."+y);
		Attribute a = node.resolver.resolveToAttribute(x.getText(), y.getText(), node);
		switch ( a.dict.type ) {
			case ARG: chunks.add(new ArgRef(y.getText())); break; // has to be current rule
			case RET:
				if ( factory.getCurrentRuleFunction()!=null &&
					 factory.getCurrentRuleFunction().name.equals(x.getText()) )
				{
					chunks.add(new RetValueRef(y.getText())); break;
				}
				else {
					chunks.add(new QRetValueRef(getRuleLabel(x.getText()), y.getText())); break;
				}
			case PREDEFINED_RULE:
			case PREDEFINED_TREE_RULE:
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
//			case PREDEFINED_TREE_RULE:
//				chunks.add(new RetValueRef(x.getText()));
//				break;
//			case PREDEFINED_LEXER_RULE: chunks.add(new RetValueRef(x.getText())); break;
		}
	}

	public void setAttr(String expr, Token x, Token rhs) {
		System.out.println("setAttr "+x+" "+rhs);
		List<ActionChunk> rhsChunks = translateActionChunk(factory,rf,rhs.getText(),node);
		SetAttr s = new SetAttr(x.getText(), rhsChunks);
		if ( factory.getGrammar().isLexer() ) s = new LexerSetAttr(x.getText(), rhsChunks);
		chunks.add(s);
	}

	public void nonLocalAttr(String expr, Token x, Token y) {
		System.out.println("nonLocalAttr "+x+"::"+y);
		Rule r = factory.getGrammar().getRule(x.getText());
		chunks.add(new NonLocalAttrRef(x.getText(), y.getText(), r.index));
	}

	public void setNonLocalAttr(String expr, Token x, Token y, Token rhs) {
		System.out.println("setNonLocalAttr "+x+"::"+y+"="+rhs);
		Rule r = factory.getGrammar().getRule(x.getText());
		List<ActionChunk> rhsChunks = translateActionChunk(factory,rf,rhs.getText(),node);
		SetNonLocalAttr s = new SetNonLocalAttr(x.getText(), y.getText(), r.index, rhsChunks);
		chunks.add(s);
	}

	public void templateInstance(String expr) {
	}

	public void indirectTemplateInstance(String expr) {
	}

	public void setExprAttribute(String expr) {
	}

	public void setSTAttribute(String expr) {
	}

	public void templateExpr(String expr) {
	}

	public void unknownSyntax(Token t) {
	}

	public void text(String text) {
		chunks.add(new ActionText(text));
	}

	TokenPropertyRef getTokenPropertyRef(Token x, Token y) {
		try {
			Class c = tokenPropToModelMap.get(y.getText());
			Constructor ctor = c.getConstructor(new Class[] {String.class});
			TokenPropertyRef ref =
				(TokenPropertyRef)ctor.newInstance(getTokenLabel(x.getText()));
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
			Constructor ctor = c.getConstructor(new Class[] {String.class});
			RulePropertyRef ref =
				(RulePropertyRef)ctor.newInstance(getRuleLabel(prop.getText()));
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
			Class c = g.isTreeGrammar() ?
				treeRulePropToModelMap.get(prop.getText()) :
				rulePropToModelMap.get(prop.getText());
			Constructor ctor = c.getConstructor(new Class[] {String.class});
			RulePropertyRef ref =
				(RulePropertyRef)ctor.newInstance(getRuleLabel(x.getText()));
			return ref;
		}
		catch (Exception e) {
			g.tool.errMgr.toolError(ErrorType.INTERNAL_ERROR, e);
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

//	public String getTokenLabel(String x, ActionAST node) {
//		Alternative alt = node.resolver.
//		Rule r = node.ATNState.rule;
//		if ( r.tokenRefs.get(x)!=null ) return true;
//		LabelElementPair anyLabelDef = getAnyLabelDef(x);
//		if ( anyLabelDef!=null && anyLabelDef.type== LabelType.TOKEN_LABEL ) return true;
//		return false;
//	}
}
