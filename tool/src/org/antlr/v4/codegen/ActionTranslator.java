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
import org.antlr.v4.tool.Rule;

import java.util.ArrayList;
import java.util.List;

/** */
public class ActionTranslator implements ActionSplitterListener {
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
		if ( node.resolver.resolveToDynamicScope(x.getText(), node)!=null ) {
			return; // $S for scope S is ok
		}
		if ( node.resolver.resolvesToToken(x.getText(), node) ) {
			if ( node.resolver.resolvesToLabel(x.getText(), node) ) {
				chunks.add(new TokenRef(x.getText())); // $label
			}
			else { // $ID for token ref or label of token; find label
				String label = factory.gen.target.getImplicitTokenLabel(x.getText());
				chunks.add(new TokenRef(label)); // $label
			}
			return;
		}
		if ( node.resolver.resolvesToListLabel(x.getText(), node) ) {
			return; // $ids for ids+=ID etc...
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

	public void setQualifiedAttr(String expr, Token x, Token y, Token rhs) {
	}

	public void qualifiedAttr(String expr, Token x, Token y) {
		System.out.println("qattr "+x+"."+y);
		if ( node.resolver.resolveToAttribute(x.getText(), y.getText(), node)==null ) {
			Rule rref = isolatedRuleRef(x.getText());
			if ( rref!=null ) {
				if ( rref!=null && rref.args!=null && rref.args.get(y.getText())!=null ) {
					g.tool.errMgr.grammarError(ErrorType.INVALID_RULE_PARAMETER_REF,
											  g.fileName, y, y.getText(), expr);
				}
				else {
					errMgr.grammarError(ErrorType.UNKNOWN_RULE_ATTRIBUTE,
											  g.fileName, y, y.getText(), rref.name, expr);
				}
			}
			else if ( !node.resolver.resolvesToAttributeDict(x.getText(), node) ) {
				errMgr.grammarError(ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE,
										  g.fileName, x, x.getText(), expr);
			}
			else {
				errMgr.grammarError(ErrorType.UNKNOWN_ATTRIBUTE_IN_SCOPE,
										  g.fileName, y, y.getText(), expr);
			}
		}
	}

	public void setAttr(String expr, Token x, Token rhs) {
		System.out.println("setAttr "+x+" "+rhs);
		List<ActionChunk> exprchunks = translateActionChunk(factory,rf,rhs.getText(),node);
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

//	public String getTokenLabel(String x, ActionAST node) {
//		Alternative alt = node.resolver.
//		Rule r = node.nfaState.rule;
//		if ( r.tokenRefs.get(x)!=null ) return true;
//		LabelElementPair anyLabelDef = getAnyLabelDef(x);
//		if ( anyLabelDef!=null && anyLabelDef.type== LabelType.TOKEN_LABEL ) return true;
//		return false;
//	}
}
