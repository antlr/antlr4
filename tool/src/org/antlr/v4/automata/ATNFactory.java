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

package org.antlr.v4.automata;

import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.BlockAST;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.PredAST;
import org.antlr.v4.tool.ast.TerminalAST;

import java.util.List;

public interface ATNFactory {
	/** A pair of states pointing to the left/right (start and end) states of a
	 *  state submachine.  Used to build ATNs.
	 */
	public static class Handle {
		public ATNState left;
		public ATNState right;

		public Handle(ATNState left, ATNState right) {
			this.left = left;
			this.right = right;
		}

		@Override
		public String toString() {
			return "("+left+","+right+")";
		}
	}

	@NotNull
	ATN createATN();

	void setCurrentRuleName(@NotNull String name);

	void setCurrentOuterAlt(int alt);

	@NotNull
	Handle rule(@NotNull GrammarAST ruleAST, @NotNull String name, @NotNull Handle blk);

	@NotNull
	ATNState newState();

	@NotNull
	Handle label(@NotNull Handle t);

	@NotNull
	Handle listLabel(@NotNull Handle t);

	@NotNull
	Handle tokenRef(@NotNull TerminalAST node);

	@NotNull
	Handle set(@NotNull GrammarAST associatedAST, @NotNull List<GrammarAST> alts, boolean invert);

	@NotNull
	Handle charSetLiteral(@NotNull GrammarAST charSetAST);

	@NotNull
	Handle range(@NotNull GrammarAST a, @NotNull GrammarAST b);

	/** For a non-lexer, just build a simple token reference atom.
	 *  For a lexer, a string is a sequence of char to match.  That is,
	 *  "fog" is treated as 'f' 'o' 'g' not as a single transition in
	 *  the DFA.  Machine== o-'f'->o-'o'->o-'g'->o and has n+1 states
	 *  for n characters.
	 */
	@NotNull
	Handle stringLiteral(@NotNull TerminalAST stringLiteralAST);

	/** For reference to rule r, build
	 *
	 *  o-e->(r)  o
	 *
	 *  where (r) is the start of rule r and the trailing o is not linked
	 *  to from rule ref state directly (it's done thru the transition(0)
	 *  RuleClosureTransition.
	 *
	 *  If the rule r is just a list of tokens, it's block will be just
	 *  a set on an edge o->o->o-set->o->o->o, could inline it rather than doing
	 *  the rule reference, but i'm not doing this yet as I'm not sure
	 *  it would help much in the ATN->DFA construction.
	 *
	 *  TODO add to codegen: collapse alt blks that are sets into single matchSet
	 * @param node
	 */
	@NotNull
	Handle ruleRef(@NotNull GrammarAST node);

	/** From an empty alternative build Grip o-e->o */
	@NotNull
	Handle epsilon(@NotNull GrammarAST node);

	/** Build what amounts to an epsilon transition with a semantic
	 *  predicate action.  The pred is a pointer into the AST of
	 *  the SEMPRED token.
	 */
	@NotNull
	Handle sempred(@NotNull PredAST pred);

	/** Build what amounts to an epsilon transition with an action.
	 *  The action goes into ATN though it is ignored during analysis.
	 */
	@NotNull
	Handle action(@NotNull ActionAST action);

	@NotNull
	Handle action(@NotNull String action);

	@NotNull
	Handle alt(@NotNull List<Handle> els);

	/** From A|B|..|Z alternative block build
     *
     *  o->o-A->o->o (last ATNState is blockEndATNState pointed to by all alts)
     *  |          ^
     *  o->o-B->o--|
     *  |          |
     *  ...        |
     *  |          |
     *  o->o-Z->o--|
     *
     *  So every alternative gets begin ATNState connected by epsilon
     *  and every alt right side points at a block end ATNState.  There is a
     *  new ATNState in the ATNState in the Grip for each alt plus one for the
     *  end ATNState.
     *
     *  Special case: only one alternative: don't make a block with alt
     *  begin/end.
     *
     *  Special case: if just a list of tokens/chars/sets, then collapse
     *  to a single edge'd o-set->o graph.
     *
     *  Set alt number (1..n) in the left-Transition ATNState.
     */
	@NotNull
	Handle block(@NotNull BlockAST blockAST, @NotNull GrammarAST ebnfRoot, @NotNull List<Handle> alternativeGrips);

//	Handle notBlock(GrammarAST blockAST, Handle set);

	/** From (A)? build either:
	 *
	 *  o--A->o
	 *  |     ^
	 *  o---->|
	 *
	 *  or, if A is a block, just add an empty alt to the end of the block
	 */
	@NotNull
	Handle optional(@NotNull GrammarAST optAST, @NotNull Handle blk);

	/** From (A)+ build
	 *
	 *     |---|    (Transition 2 from A.right points at alt 1)
	 *     v   |    (follow of loop is Transition 1)
	 *  o->o-A-o->o
	 *
	 *  Meaning that the last ATNState in A points back to A's left Transition ATNState
	 *  and we add a new begin/end ATNState.  A can be single alternative or
	 *  multiple.
	 *
	 *  During analysis we'll call the follow link (transition 1) alt n+1 for
	 *  an n-alt A block.
	 */
	@NotNull
	Handle plus(@NotNull GrammarAST plusAST, @NotNull Handle blk);

	/** From (A)* build
	 *
	 *     |---|
	 *     v   |
	 *  o->o-A-o--o (Transition 2 from block end points at alt 1; follow is Transition 1)
	 *  |         ^
	 *  o---------| (optional branch is 2nd alt of optional block containing A+)
	 *
	 *  Meaning that the last (end) ATNState in A points back to A's
	 *  left side ATNState and we add 3 new ATNStates (the
	 *  optional branch is built just like an optional subrule).
	 *  See the Aplus() method for more on the loop back Transition.
	 *  The new node on right edge is set to RIGHT_EDGE_OF_CLOSURE so we
	 *  can detect nested (A*)* loops and insert an extra node.  Previously,
	 *  two blocks shared same EOB node.
	 *
	 *  There are 2 or 3 decision points in a A*.  If A is not a block (i.e.,
	 *  it only has one alt), then there are two decisions: the optional bypass
	 *  and then loopback.  If A is a block of alts, then there are three
	 *  decisions: bypass, loopback, and A's decision point.
	 *
	 *  Note that the optional bypass must be outside the loop as (A|B)* is
	 *  not the same thing as (A|B|)+.
	 *
	 *  This is an accurate ATN representation of the meaning of (A)*, but
	 *  for generating code, I don't need a DFA for the optional branch by
	 *  virtue of how I generate code.  The exit-loopback-branch decision
	 *  is sufficient to let me make an appropriate enter, exit, loop
	 *  determination.  See codegen.g
	 */
	@NotNull
	Handle star(@NotNull GrammarAST starAST, @NotNull Handle blk);

	/** Build an atom with all possible values in its label */
	@NotNull
	Handle wildcard(@NotNull GrammarAST associatedAST);

	@NotNull
	Handle lexerAltCommands(@NotNull Handle alt, @NotNull Handle cmds);

	@NotNull
	Handle lexerCallCommand(@NotNull GrammarAST ID, @NotNull GrammarAST arg);

	@NotNull
	Handle lexerCommand(@NotNull GrammarAST ID);
}
