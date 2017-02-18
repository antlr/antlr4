/*
 * Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;

public interface TerminalNode extends ParseTree {
	Token getSymbol();

	/** Set the parent for this leaf node.
	 *
	 *  Technically, this is not backward compatible as it changes
	 *  the interface but no one was able to create custom
	 *  TerminalNodes anyway so I'm adding as it improves internal
	 *  code quality.
	 *
	 *  @since 4.6.1
	 */
	void setParent(RuleContext parent);
}
