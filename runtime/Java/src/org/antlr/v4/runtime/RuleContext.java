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
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.Trees;
import org.antlr.v4.runtime.tree.gui.TreeViewer;

import javax.print.PrintException;
import javax.swing.JDialog;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

/** A rule context is a record of a single rule invocation. It knows
 *  which context invoked it, if any. If there is no parent context, then
 *  naturally the invoking state is not valid.  The parent link
 *  provides a chain upwards from the current rule invocation to the root
 *  of the invocation tree, forming a stack. We actually carry no
 *  information about the rule associated with this context (except
 *  when parsing). We keep only the state number of the invoking state from
 *  the ATN submachine that invoked this. Contrast this with the s
 *  pointer inside ParserRuleContext that tracks the current state
 *  being "executed" for the current rule.
 *
 *  The parent contexts are useful for computing lookahead sets and
 *  getting error information.
 *
 *  These objects are used during parsing and prediction.
 *  For the special case of parsers and tree parsers, we use the subclass
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
	 *  If parent is null, this should be -1.
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

	/** A context is empty if there is no invoking state; meaning nobody call
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

	/** Call this method to view a parse tree in a dialog box visually. */
	public Future<JDialog> inspect(@Nullable Parser parser) {
		List<String> ruleNames = parser != null ? Arrays.asList(parser.getRuleNames()) : null;
		return inspect(ruleNames);
	}

	public Future<JDialog> inspect(@Nullable List<String> ruleNames) {
		TreeViewer viewer = new TreeViewer(ruleNames, this);
		return viewer.open();
	}

	/** Save this tree in a postscript file */
	public void save(@Nullable Parser parser, String fileName)
		throws IOException, PrintException
	{
		List<String> ruleNames = parser != null ? Arrays.asList(parser.getRuleNames()) : null;
		save(ruleNames, fileName);
	}

	/** Save this tree in a postscript file using a particular font name and size */
	public void save(@Nullable Parser parser, String fileName,
					 String fontName, int fontSize)
		throws IOException
	{
		List<String> ruleNames = parser != null ? Arrays.asList(parser.getRuleNames()) : null;
		save(ruleNames, fileName, fontName, fontSize);
	}

	/** Save this tree in a postscript file */
	public void save(@Nullable List<String> ruleNames, String fileName)
		throws IOException, PrintException
	{
		Trees.writePS(this, ruleNames, fileName);
	}

	/** Save this tree in a postscript file using a particular font name and size */
	public void save(@Nullable List<String> ruleNames, String fileName,
					 String fontName, int fontSize)
		throws IOException
	{
		Trees.writePS(this, ruleNames, fileName, fontName, fontSize);
	}

	/** Print out a whole tree, not just a node, in LISP format
	 *  (root child1 .. childN). Print just a node if this is a leaf.
	 *  We have to know the recognizer so we can get rule names.
	 */
	@Override
	public String toStringTree(@Nullable Parser recog) {
		return Trees.toStringTree(this, recog);
	}

	/** Print out a whole tree, not just a node, in LISP format
	 *  (root child1 .. childN). Print just a node if this is a leaf.
	 */
	public String toStringTree(@Nullable List<String> ruleNames) {
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

	public final String toString(@Nullable Recognizer<?,?> recog) {
		return toString(recog, ParserRuleContext.EMPTY);
	}

	public final String toString(@Nullable List<String> ruleNames) {
		return toString(ruleNames, null);
	}

	// recog null unless ParserRuleContext, in which case we use subclass toString(...)
	public String toString(@Nullable Recognizer<?,?> recog, @Nullable RuleContext stop) {
		String[] ruleNames = recog != null ? recog.getRuleNames() : null;
		List<String> ruleNamesList = ruleNames != null ? Arrays.asList(ruleNames) : null;
		return toString(ruleNamesList, stop);
	}

	public String toString(@Nullable List<String> ruleNames, @Nullable RuleContext stop) {
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
