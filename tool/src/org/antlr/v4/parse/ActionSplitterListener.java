/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.parse;

import org.antlr.runtime.Token;

/** */
public interface ActionSplitterListener {
    void qualifiedAttr(String expr, Token x, Token y);
	void setAttr(String expr, Token x, Token rhs);
	void attr(String expr, Token x);

	void setNonLocalAttr(String expr, Token x, Token y, Token rhs);
	void nonLocalAttr(String expr, Token x, Token y);

    void text(String text);
}
