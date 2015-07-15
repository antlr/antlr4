/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.Trees;

import javax.print.PrintException;
import javax.swing.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

/** /** A rule context is a record of a single rule invocation.
 *
 *  We form a stack of these context objects using the parent
 *  pointer. A parent pointer of null indicates that the current
 *  context is the bottom of the stack. The ParserRuleContext subclass
 *  as a children list so that we can turn this data structure into a
 *  tree.
 *
 *  The root node always has a null pointer and invokingState of -1.
 *
 *  Upon entry to parsing, the first invoked rule function creates a
 *  context object (asubclass specialized for that rule such as
 *  SContext) and makes it the root of a parse tree, recorded by field
 *  Parser._ctx.
 *
 *  public final SContext s() throws RecognitionException {
 *      SContext _localctx = new SContext(_ctx, getState()); <-- create new node
 *      enterRule(_localctx, 0, RULE_s);                     <-- push it
 *      ...
 *      exitRule();                                          <-- pop back to _localctx
 *      return _localctx;
 *  }
 *
 *  A subsequent rule invocation of r from the start rule s pushes a
 *  new context object for r whose parent points at s and use invoking
 *  state is the state with r emanating as edge label.
 *
 *  The invokingState fields from a context object to the root
 *  together form a stack of rule indication states where the root
 *  (bottom of the stack) has a -1 sentinel value. If we invoke start
 *  symbol s then call r1, which calls r2, the  would look like
 *  this:
 *
 *     SContext[-1]   <- root node (bottom of the stack)
 *     R1Context[p]   <- p in rule s called r1
 *     R2Context[q]   <- q in rule r1 called r2
 *
 *  So the top of the stack, _ctx, represents a call to the current
 *  rule and it holds the return address from another rule that invoke
 *  to this rule. To invoke a rule, we must always have a current context.
 *
 *  The parent contexts are useful for computing lookahead sets and
 *  getting error information.
 *
 *  These objects are used during parsing and prediction.
 *  For the special case of parsers, we use the subclass
 *  ParserRuleContext.
 *
 *  @see ParserRuleContext
 */
public class RuleContext implements RuleNode {
	public static final ParserRuleContext EMPTY = new ParserRuleContext();

	/** What context invoked this rule? */
	public RuleContext parent;

	/** What state invoked the rule associated with this context?
	 *  The "return address" is the followState of invokingState
	 *  If parent is null, this should be -1 this context object represents
	 *  the start rule.
	 */
	public int invokingState = -1;

	public RuleContext() {}

	public RuleContext(RuleContext parent, int invokingState) {
		this.parent = parent;
		//if ( parent!=null ) System.out.println("invoke "+stateNumber+" from "+parent);
		this.invokingState = invokingState;
	}

	public int depth() {
		int n = 0;
		RuleContext p = this;
		while ( p!=null ) {
			p = p.parent;
			n++;
		}
		return n;
	}

	/** A context is empty if there is no invoking state; meaning nobody called
	 *  current context.
	 */
	public boolean isEmpty() {
		return invokingState == -1;
	}

	// satisfy the ParseTree / SyntaxTree interface

	@Override
	public Interval getSourceInterval() {
		return Interval.INVALID;
	}

	@Override
	public RuleContext getRuleContext() { return this; }

	@Override
	public RuleContext getParent() { return parent; }

	@Override
	public RuleContext getPayload() { return this; }

	/** Return the combined text of all child nodes. This method only considers
	 *  tokens which have been added to the parse tree.
	 *  <p>
	 *  Since tokens on hidden channels (e.g. whitespace or comments) are not
	 *  added to the parse trees, they will not appear in the output of this
	 *  method.
	 */
	@Override
	public String getText() {
		if (getChildCount() == 0) {
			return "";
		}

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < getChildCount(); i++) {
			builder.append(getChild(i).getText());
		}

		return builder.toString();
	}

	public int getRuleIndex() { return -1; }

	@Override
	public ParseTree getChild(int i) {
		return null;
	}

	@Override
	public int getChildCount() {
		return 0;
	}

	@Override
	public <T> T accept(ParseTreeVisitor<? extends T> visitor) { return visitor.visitChildren(this); }

	/** Print out a whole tree, not just a node, in LISP format
	 *  (root child1 .. childN). Print just a node if this is a leaf.
	 *  We have to know the recognizer so we can get rule names.
	 */
	@Override
	public String toStringTree(Parser recog) {
		return Trees.toStringTree(this, recog);
	}

	/** Print out a whole tree, not just a node, in LISP format
	 *  (root child1 .. childN). Print just a node if this is a leaf.
	 */
	public String toStringTree(List<String> ruleNames) {
		return Trees.toStringTree(this, ruleNames);
	}

	@Override
	public String toStringTree() {
		return toStringTree((List<String>)null);
	}

	@Override
	public String toString() {
		return toString((List<String>)null, (RuleContext)null);
	}

	public final String toString(Recognizer<?,?> recog) {
		return toString(recog, ParserRuleContext.EMPTY);
	}

	public final String toString(List<String> ruleNames) {
		return toString(ruleNames, null);
	}

	// recog null unless ParserRuleContext, in which case we use subclass toString(...)
	public String toString(Recognizer<?,?> recog, RuleContext stop) {
		String[] ruleNames = recog != null ? recog.getRuleNames() : null;
		List<String> ruleNamesList = ruleNames != null ? Arrays.asList(ruleNames) : null;
		return toString(ruleNamesList, stop);
	}

	public String toString(List<String> ruleNames, RuleContext stop) {
		StringBuilder buf = new StringBuilder();
		RuleContext p = this;
		buf.append("[");
		while (p != null && p != stop) {
			if (ruleNames == null) {
				if (!p.isEmpty()) {
					buf.append(p.invokingState);
				}
			}
			else {
				int ruleIndex = p.getRuleIndex();
				String ruleName = ruleIndex >= 0 && ruleIndex < ruleNames.size() ? ruleNames.get(ruleIndex) : Integer.toString(ruleIndex);
				buf.append(ruleName);
			}

			if (p.parent != null && (ruleNames != null || !p.parent.isEmpty())) {
				buf.append(" ");
			}

			p = p.parent;
		}

		buf.append("]");
		return buf.toString();
	}
}
