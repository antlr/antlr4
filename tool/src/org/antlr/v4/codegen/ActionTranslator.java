package org.antlr.v4.codegen;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;
import org.antlr.v4.codegen.src.RuleFunction;
import org.antlr.v4.codegen.src.actions.*;
import org.antlr.v4.parse.ActionSplitter;
import org.antlr.v4.parse.ActionSplitterListener;
import org.antlr.v4.tool.ActionAST;
import org.antlr.v4.tool.Attribute;
import org.antlr.v4.tool.ErrorType;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** */
public class ActionTranslator implements ActionSplitterListener {
	public static final Map<String, Class> rulePropToModelMap = new HashMap<String, Class>() {{
		put("start", RulePropertyRef_start.class);
		put("stop", RulePropertyRef_stop.class);
		put("tree", RulePropertyRef_tree.class);
		put("text", RulePropertyRef_text.class);
		put("st", RulePropertyRef_st.class);
	}};

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

	ActionAST node;
	RuleFunction rf;
	List<ActionChunk> chunks = new ArrayList<ActionChunk>();
	OutputModelFactory factory;

	public ActionTranslator(OutputModelFactory factory, ActionAST node) {
		this.factory = factory;
		this.node = node;
	}

	public static List<ActionChunk> translateAction(OutputModelFactory factory,
													RuleFunction rf,
													Token tokenWithinAction,
													ActionAST node)
	{
		String action = tokenWithinAction.getText();
		int firstCurly = action.indexOf('{');
		int lastCurly = action.lastIndexOf('}');
		if ( firstCurly>=0 && lastCurly>=0 ) {
			action = action.substring(firstCurly+1, lastCurly); // trim {...}
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
		System.out.println("translate "+action);
		ANTLRStringStream in = new ANTLRStringStream(action);
		in.setLine(tokenWithinAction.getLine());
		in.setCharPositionInLine(tokenWithinAction.getCharPositionInLine());
		ActionSplitter trigger = new ActionSplitter(in, translator);
		// forces eval, triggers listener methods
		trigger.getActionTokens();
		return translator.chunks;
	}

	public void attr(String expr, Token x) {
		// TODO: $SCOPENAME
		System.out.println("attr "+x);
		Attribute a = node.resolver.resolveToAttribute(x.getText(), node);
		if ( a!=null ) {
			switch ( a.dict.type ) {
				case ARG: chunks.add(new ArgRef(x.getText())); break;
				case RET: chunks.add(new RetValueRef(x.getText())); break;
//			case PREDEFINED_RULE: chunks.add(new RetValueRef(x.getText())); break;
//			case PREDEFINED_TREE_RULE: chunks.add(new RetValueRef(x.getText())); break;
			}
		}
		if ( node.resolver.resolvesToToken(x.getText(), node) ) {
			chunks.add(new TokenRef(getTokenLabel(x.getText()))); // $label
			return;
		}
		if ( node.resolver.resolvesToListLabel(x.getText(), node) ) {
			return; // $ids for ids+=ID etc...
		}
		if ( node.resolver.resolveToDynamicScope(x.getText(), node)!=null ) {
			return; // $S for scope S is ok
		}
//		switch ( a.dict.type ) {
//			case ARG: chunks.add(new ArgRef(x.getText())); break;
//			case RET: chunks.add(new RetValueRef(x.getText())); break;
//			case PREDEFINED_RULE: chunks.add(new RetValueRef(x.getText())); break;
//			case PREDEFINED_LEXER_RULE: chunks.add(new RetValueRef(x.getText())); break;
//			case PREDEFINED_TREE_RULE: chunks.add(new RetValueRef(x.getText())); break;
//			case GLOBAL_SCOPE: chunks.add(new RetValueRef(x.getText())); break;
//			case RULE_SCOPE: chunks.add(new RetValueRef(x.getText())); break;
//			case TOKEN: chunks.add(new TokenRef(x.getText())); break;
//		}
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
				if ( factory.currentRule.size()>0 && factory.currentRule.peek().name.equals(x.getText()) ) {
					chunks.add(new RetValueRef(y.getText())); break;
				}
				else {
					chunks.add(new QRetValueRef(getRuleLabel(x.getText()), y.getText())); break;
				}
			case PREDEFINED_RULE: chunks.add(getRulePropertyRef(x, y));	break;
			case TOKEN: chunks.add(getTokenPropertyRef(x, y));	break;
//			case PREDEFINED_LEXER_RULE: chunks.add(new RetValueRef(x.getText())); break;
//			case PREDEFINED_TREE_RULE: chunks.add(new RetValueRef(x.getText())); break;
		}
	}

	public void setAttr(String expr, Token x, Token rhs) {
		System.out.println("setAttr "+x+" "+rhs);
		List<ActionChunk> rhsChunks = translateActionChunk(factory,rf,rhs.getText(),node);
		chunks.add(new SetAttr(x.getText(), rhsChunks));
	}

	public void dynamicScopeAttr(String expr, Token x, Token y) {
		System.out.println("scoped "+x+"."+y);
		// we assume valid, just gen code
		String scope = x.getText();
		if ( factory.g.getRule(x.getText())!=null ) {
			scope = factory.gen.target.getRuleDynamicScopeStructName(scope);
		}
		chunks.add(new DynScopeAttrRef(scope, y.getText()));
	}

	public void setDynamicScopeAttr(String expr, Token x, Token y, Token rhs) {
	}

	public void dynamicNegativeIndexedScopeAttr(String expr, Token x, Token y, Token index) {
		List<ActionChunk> indexChunks = translateActionChunk(factory,rf,index.getText(),node);
		chunks.add(new DynScopeAttrRef_negIndex(x.getText(), y.getText(), indexChunks));
	}

	public void setDynamicNegativeIndexedScopeAttr(String expr, Token x, Token y, Token index, Token rhs) {

	}

	public void dynamicAbsoluteIndexedScopeAttr(String expr, Token x, Token y, Token index) {
		List<ActionChunk> indexChunks = translateActionChunk(factory,rf,index.getText(),node);
		chunks.add(new DynScopeAttrRef_index(x.getText(), y.getText(), indexChunks));
	}

	public void setDynamicAbsoluteIndexedScopeAttr(String expr, Token x, Token y, Token index, Token rhs) {
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
				(TokenPropertyRef)ctor.newInstance(getRuleLabel(x.getText()));
			return ref;
		}
		catch (Exception e) {
			factory.g.tool.errMgr.toolError(ErrorType.INTERNAL_ERROR, e);
		}
		return null;
	}

	RulePropertyRef getRulePropertyRef(Token x, Token y) {
		try {
			Class c = rulePropToModelMap.get(y.getText());
			Constructor ctor = c.getConstructor(new Class[] {String.class});
			RulePropertyRef ref =
				(RulePropertyRef)ctor.newInstance(getRuleLabel(x.getText()));
			return ref;
		}
		catch (Exception e) {
			factory.g.tool.errMgr.toolError(ErrorType.INTERNAL_ERROR, e);
		}
		return null;
	}

	public String getTokenLabel(String x) {
		if ( node.resolver.resolvesToLabel(x, node) ) return x;
		return factory.gen.target.getImplicitTokenLabel(x);
	}

	public String getRuleLabel(String x) {
		if ( node.resolver.resolvesToLabel(x, node) ) return x;
		return factory.gen.target.getImplicitRuleLabel(x);
	}

//	public String getTokenLabel(String x, ActionAST node) {
//		Alternative alt = node.resolver.
//		Rule r = node.nfaState.rule;
//		if ( r.tokenRefs.get(x)!=null ) return true;
//		LabelElementPair anyLabelDef = getAnyLabelDef(x);
//		if ( anyLabelDef!=null && anyLabelDef.type== LabelType.TOKEN_LABEL ) return true;
//		return false;
//	}
}
