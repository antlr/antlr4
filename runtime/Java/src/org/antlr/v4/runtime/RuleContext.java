/*
 [The "BSD license"]
  Copyright (c) 2011 Terence Parr
  All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions
  are met:

  1. Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
  2. Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
  3. The name of the author may not be used to endorse or promote products
     derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Trees;
import org.antlr.v4.runtime.tree.gui.TreeViewer;

import javax.print.PrintException;
import java.io.IOException;

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
 *  These objects are used during lexing, parsing, and prediction.
 *  For the special case of parsers and tree parsers, we use the subclass
 *  ParserRuleContext.
 *
 *  @see ParserRuleContext
 */
public class RuleContext implements ParseTree.RuleNode {
	/** What context invoked this rule? */
	public RuleContext parent;

	/** What state invoked the rule associated with this context?
	 *  The "return address" is the followState of invokingState
	 *  If parent is null, this should be -1.
	 */
	public int invokingState = -1;

	/** Computing the hashCode is very expensive and closureBusy()
	 *  uses it to track when it's seen a state|ctx before to avoid
	 *  infinite loops.  As we add new contexts, record the hash code
	 *  as this.invokingState + parent.cachedHashCode.  Avoids walking
	 *  up the tree for every hashCode().  Note that this caching works
	 *  because a context is a monotonically growing tree of context nodes
	 *  and nothing on the stack is ever modified...ctx just grows
	 *  or shrinks.
	 */
	protected int cachedHashCode;

	public RuleContext() {}

	public RuleContext(RuleContext parent, int invokingState) {
		this.parent = parent;
		//if ( parent!=null ) System.out.println("invoke "+stateNumber+" from "+parent);
		this.invokingState = invokingState;

		this.cachedHashCode = invokingState;
		if ( parent!=null ) {
			this.cachedHashCode += parent.cachedHashCode;
		}
	}

	@Override
	public int hashCode() {
		return cachedHashCode; // works with tests; don't recompute.
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

	/** Two contexts are equals() if both have
	 *  same call stack; walk upwards to the root.
	 *  Note that you may be comparing contexts in different alt trees.
	 *
	 *  The hashCode is cheap as it's computed once upon each context
	 *  push on the stack.  Using it to make equals() more efficient.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof RuleContext)) {
			return false;
		}

		RuleContext other = (RuleContext)o;
		if ( this.hashCode() != other.hashCode() ) {
			return false; // can't be same if hash is different
		}

		// System.out.println("comparing "+this+" with "+other);
		RuleContext sp = this;
		while ( sp!=null && other!=null ) {
			if ( sp == other ) return true;
			if ( sp.invokingState != other.invokingState) return false;
			sp = sp.parent;
			other = other.parent;
		}
		if ( !(sp==null && other==null) ) {
			return false; // both pointers must be at their roots after walk
		}
		return true;
	}

	/** Two contexts conflict() if they are equals() or one is a stack suffix
	 *  of the other.  For example, contexts [21 12 $] and [21 9 $] do not
	 *  conflict, but [21 $] and [21 12 $] do conflict.  Note that I should
	 *  probably not show the $ in this case.  There is a dummy node for each
	 *  stack that just means empty; $ is a marker that's all.
	 *
	 *  This is used in relation to checking conflicts associated with a
	 *  single NFA state's configurations within a single DFA state.
	 *  If there are configurations s and t within a DFA state such that
	 *  s.state=t.state && s.alt != t.alt && s.ctx conflicts t.ctx then
	 *  the DFA state predicts more than a single alt--it's nondeterministic.
	 *  Two contexts conflict if they are the same or if one is a suffix
	 *  of the other.
	 *
	 *  When comparing contexts, if one context has a stack and the other
	 *  does not then they should be considered the same context.  The only
	 *  way for an NFA state p to have an empty context and a nonempty context
	 *  is the case when closure falls off end of rule without a call stack
	 *  and re-enters the rule with a context.  This resolves the issue I
	 *  discussed with Sriram Srinivasan Feb 28, 2005 about not terminating
	 *  fast enough upon nondeterminism.
	 */
	public boolean conflictsWith(RuleContext other) {
		return this.suffix(other) || this.equals(other);
	}

	/** [$] suffix any context
	 *  [21 $] suffix [21 12 $]
	 *  [21 12 $] suffix [21 $]
	 *  [21 18 $] suffix [21 18 12 9 $]
	 *  [21 18 12 9 $] suffix [21 18 $]
	 *  [21 12 $] not suffix [21 9 $]
	 *
	 *  Example "[21 $] suffix [21 12 $]" means: rule r invoked current rule
	 *  from state 21.  Rule s invoked rule r from state 12 which then invoked
	 *  current rule also via state 21.  While the context prior to state 21
	 *  is different, the fact that both contexts emanate from state 21 implies
	 *  that they are now going to track perfectly together.  Once they
	 *  converged on state 21, there is no way they can separate.  In other
	 *  words, the prior stack state is not consulted when computing where to
	 *  go in the closure operation.  ?$ and ??$ are considered the same stack.
	 *  If ? is popped off then $ and ?$ remain; they are now an empty and
	 *  nonempty context comparison.  So, if one stack is a suffix of
	 *  another, then it will still degenerate to the simple empty stack
	 *  comparison case.
	 */
	protected boolean suffix(RuleContext other) {
		RuleContext sp = this;
		// if one of the contexts is empty, it never enters loop and returns true
		while ( sp.parent!=null && other.parent!=null ) {
			if ( sp.invokingState != other.invokingState ) {
				return false;
			}
			sp = sp.parent;
			other = other.parent;
		}
		//System.out.println("suffix");
		return true;
	}

	/** A context is empty if there is no invoking state; meaning nobody call
	 *  current context.
	 */
	public boolean isEmpty() {
		return invokingState == -1;
	}

	// satisfy the ParseTree interface

	@Override
	public RuleContext getRuleContext() { return this; }

	@Override
	public ParseTree getParent() { return parent; }

	@Override
	public RuleContext getPayload() { return this; }

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
	public Interval getSourceInterval() {
		if ( getChildCount()==0 ) return Interval.INVALID;
		int start = getChild(0).getSourceInterval().a;
		int stop = getChild(getChildCount()-1).getSourceInterval().b;
		return new Interval(start, stop);
	}

	public void inspect(Parser parser) {
		TreeViewer viewer = new TreeViewer(parser, this);
		viewer.open();
	}

	public void save(Parser parser, String fileName)
		throws IOException, PrintException
	{
		Trees.writePS(this, parser, fileName);
	}

	public void save(Parser parser, String fileName,
					 String fontName, int fontSize)
		throws IOException
	{
		Trees.writePS(this, parser, fileName, fontName, fontSize);
	}

	/** Print out a whole tree, not just a node, in LISP format
	 *  (root child1 .. childN). Print just a node if this is a leaf.
	 *  We have to know the recognizer so we can get rule names.
	 */
	public String toStringTree(Parser recog) {
		return Trees.toStringTree(this, recog);
	}

	@Override
	public String toStringTree() { return toStringTree(null); }

	@Override
	public String toString() {
		return toString(null);
	}

	public String toString(@Nullable Recognizer<?,?> recog) {
		return toString(recog, ParserRuleContext.EMPTY);
	}

	// recog null unless ParserRuleContext, in which case we use subclass toString(...)
	public String toString(@Nullable Recognizer<?,?> recog, RuleContext stop) {
		StringBuilder buf = new StringBuilder();
		RuleContext p = this;
		buf.append("[");
		while ( p != null && p != stop ) {
			if ( !p.isEmpty() ) buf.append(p.invokingState);
			if ( p.parent != null && !p.parent.isEmpty() ) buf.append(" ");
			p = p.parent;
		}
		buf.append("]");
		return buf.toString();
	}
}
