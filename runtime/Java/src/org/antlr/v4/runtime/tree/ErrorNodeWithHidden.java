/*
 * Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.Token;

/** A version of {@link org.antlr.v4.runtime.tree.TerminalNodeWithHidden} tagged
 *  as an {@link ErrorNode}.
 */
public class ErrorNodeWithHidden extends TerminalNodeWithHidden implements ErrorNode {
	public ErrorNodeWithHidden(BufferedTokenStream tokens, int channel, Token symbol) {
		super(tokens, channel, symbol);
	}
}
