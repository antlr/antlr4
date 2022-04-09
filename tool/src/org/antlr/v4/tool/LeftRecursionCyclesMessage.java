/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.tool;

import java.util.Collection;

public class LeftRecursionCyclesMessage extends ANTLRMessage {
	public LeftRecursionCyclesMessage(String fileName, Rule rule, Collection<Rule> cycle) {
		super(ErrorType.LEFT_RECURSION_CYCLES, fileName, rule.ast.getToken(), cycle);
	}
}
