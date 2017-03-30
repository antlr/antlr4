/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.Token;

/** Represents a token that was consumed during resynchronization
 *  rather than during a valid match operation. For example,
 *  we will create this kind of a node during single token insertion
 *  and deletion as well as during "consume until error recovery set"
 *  upon no viable alternative exceptions.
 */
public class ErrorNodeImpl extends TerminalNodeImpl implements ErrorNode {
	public ErrorNodeImpl(Token token) {
		super(token);
	}

	@Override
	public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
		return visitor.visitErrorNode(this);
	}
}
