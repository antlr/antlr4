package org.antlr.v4.test;

import org.antlr.runtime.Token;
import org.antlr.v4.parse.ActionSplitterListener;

public class BlankActionSplitterListener implements ActionSplitterListener {
	public void setQualifiedAttr(String expr, Token x, Token y, Token rhs) {
	}

	public void qualifiedAttr(String expr, Token x, Token y) {
	}

	public void setAttr(String expr, Token x, Token rhs) {
	}

	public void attr(String expr, Token x) {
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
	}
}
