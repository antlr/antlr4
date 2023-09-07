/*
 * Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.java.api;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.antlr.v4.runtime.tree.Tree;

import java.util.Arrays;
import java.util.List;

/** For testing purposes, it is useful to have a specific id
 *  associated with each node, which this definition accomplishes.
 *  Given that there is only one mvn pom.xml file for all of these API tests,
 *  all API tests that use grammars in this section will derived from
 *  TreeRootForTesting not the usual ParserRuleContext.
 *
 *  Also {@see TerminalNodeForTesting} in this class.
 */
public class TreeRootForTesting extends ParserRuleContext {
	/** Every thread needs a unique sequence of 0..n-1 for n tree nodes created */
	public static class ThreadLocalCounter extends ThreadLocal<Integer> {
		@Override
		protected Integer initialValue() { return 0; }
		public int getAndIncrement() { int v = get(); set(v+1); return v; }
	};
	public static ThreadLocalCounter TREE_NUMBER = new ThreadLocalCounter();

	public static class TerminalNodeForTesting extends TerminalNodeImpl {
		public int id = TREE_NUMBER.getAndIncrement();
		public TerminalNodeForTesting(Token symbol) {
			super(symbol);
		}
	}

	protected int id = TREE_NUMBER.getAndIncrement();

	public TreeRootForTesting() {
		super();
	}

	public TreeRootForTesting(ParserRuleContext parent, int invokingStateNumber) {
		super(parent, invokingStateNumber);
	}

	public static String toStringTreeWithIDs(final Tree t, Parser recog) {
		return toStringTreeWithIDs(t, Arrays.asList(recog.getRuleNames()));
	}

	public static String toStringTreeWithIDs(final Tree t, final List<String> ruleNames) {
		String s = Utils.escapeWhitespace(getNodeText(t, ruleNames), false);
		if ( t.getChildCount()==0 ) return s;
		StringBuilder buf = new StringBuilder();
		buf.append("(");
		s = Utils.escapeWhitespace(getNodeText(t, ruleNames), false);
		buf.append(s);
		buf.append(' ');
		for (int i = 0; i<t.getChildCount(); i++) {
			if ( i>0 ) buf.append(' ');
			buf.append(toStringTreeWithIDs(t.getChild(i), ruleNames));
		}
		buf.append(")");
		return buf.toString();
	}

	public static String getNodeText(Tree t, List<String> ruleNames) {
		if ( ruleNames!=null ) {
			if ( t instanceof RuleContext ) {
				int ruleIndex = ((RuleContext)t).getRuleContext().getRuleIndex();
				String ruleName = ruleNames.get(ruleIndex);
				if ( t instanceof TreeRootForTesting ) {
					return ruleName+"@"+((TreeRootForTesting) t).id; // HIDEOUS CUT/PASTE of getNodeText/toStringTree SO I CAN CHANGE THIS
				}
				else {
					int altNumber = ((RuleContext) t).getAltNumber();
					if ( altNumber!=ATN.INVALID_ALT_NUMBER ) {
						return ruleName+":"+altNumber;
					}
					return ruleName;
				}
			}
			else if ( t instanceof ErrorNode ) {
				return t.toString();
			}
			else if ( t instanceof TerminalNode ) {
				Token symbol = ((TerminalNode)t).getSymbol();
				if (symbol != null) {
					String s = symbol.getText();
					if ( t instanceof TerminalNodeForTesting ) {
						return s+"@"+((TerminalNodeForTesting) t).id;
					}
					else {
						return s;
					}
				}
			}
		}
		// no recog for rule names
		Object payload = t.getPayload();
		if ( payload instanceof Token ) {
			return ((Token)payload).getText();
		}
		return t.getPayload().toString();
	}
}
