/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.semantics;

import org.antlr.runtime.Token;
import org.antlr.v4.parse.ActionSplitterListener;

public class BlankActionSplitterListener implements ActionSplitterListener {
	@Override
	public void qualifiedAttr(String expr, Token x, Token y) {
	}

	@Override
	public void setAttr(String expr, Token x, Token rhs) {
	}

	@Override
	public void attr(String expr, Token x) {
	}

	public void templateInstance(String expr) {
	}

	@Override
	public void nonLocalAttr(String expr, Token x, Token y) {
	}

	@Override
	public void setNonLocalAttr(String expr, Token x, Token y, Token rhs) {
	}

	public void indirectTemplateInstance(String expr) {
	}

	public void setExprAttribute(String expr) {
	}

	public void setSTAttribute(String expr) {
	}

	public void templateExpr(String expr) {
	}

	@Override
	public void text(String text) {
	}
}
