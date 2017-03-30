/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.tool;

import org.antlr.runtime.Token;

import java.util.Collection;

public class LeftRecursionCyclesMessage extends ANTLRMessage {
	public LeftRecursionCyclesMessage(String fileName, Collection<? extends Collection<Rule>> cycles) {
		super(ErrorType.LEFT_RECURSION_CYCLES, getStartTokenOfFirstRule(cycles), cycles);
		this.fileName = fileName;
	}

	protected static Token getStartTokenOfFirstRule(Collection<? extends Collection<Rule>> cycles) {
	    if (cycles == null) {
	        return null;
	    }

	    for (Collection<Rule> collection : cycles) {
	        if (collection == null) {
	            return null;
	        }

	        for (Rule rule : collection) {
	            if (rule.ast != null) {
	                return rule.ast.getToken();
	            }
	        }
	    }
		return null;
	}
}
