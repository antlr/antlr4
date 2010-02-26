package org.antlr.v4.automata;


import org.antlr.v4.misc.IntSet;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.GrammarAST;
import org.antlr.v4.tool.Rule;

import java.util.List;

/** Superclass of NFABuilder.g that provides actual NFA construction routines. */
public class ParserNFAFactory implements NFAFactory {
	public Grammar g;
	public Rule currentRule;

	public ParserNFAFactory(Grammar g) { this.g = g; }

	public NFA getNFA() {
		return null;
	}

	/** add an EOF transition to any rule end NFAState that points to nothing
     *  (i.e., for all those rules not invoked by another rule).  These
     *  are start symbols then.
	 *
	 *  Return the number of grammar entry points; i.e., how many rules are
	 *  not invoked by another rule (they can only be invoked from outside).
	 *  These are the start rules.
     */
	public int addEOFStates(List rules) { return 0; }
	
	
	public Rule getCurrentRule() { return currentRule; }

	public void setCurrentRuleName(String name) { this.currentRule = g.getRule(name); }

	public NFAState newState() { return null; }

	/** From label A build Graph o-A->o */
	public Grip atom(int label, GrammarAST associatedAST) { return null; }

	public Grip atom(GrammarAST atomAST) { return null; }

	/** From set build single edge graph o->o-set->o.  To conform to
     *  what an alt block looks like, must have extra state on left.
     */
	public Grip set(IntSet set, GrammarAST associatedAST) { return null; }

	/** Can only complement block of simple alts; can complement build_Set()
	 *  result, that is.  Get set and complement, replace old with complement.
	public Grip build_AlternativeBlockComplement(Grip blk) {
		State s0 = blk.left;
		IntSet set = getCollapsedBlockAsSet(s0) { return null; }
		if ( set!=null ) {
			// if set is available, then structure known and blk is a set
			set = nfa.grammar.complement(set) { return null; }
			Label label = s0.transition(0).target.transition(0).label;
			label.setSet(set) { return null; }
		}
		return blk;
	}
	 */

	public Grip range(int a, int b) { return null; }

	/** From char 'c' build Grip o-intValue(c)->o
	 */
	public Grip charLiteral(GrammarAST charLiteralAST) { return null; }

	/** From char 'c' build Grip o-intValue(c)->o
	 *  can include unicode spec likes '\u0024' later.  Accepts
	 *  actual unicode 16-bit now, of course, by default.
     *  TODO not supplemental char clean!
	 */
	public Grip charRange(String a, String b) { return null; }

	/** For a non-lexer, just build a simple token reference atom.
	 *  For a lexer, a string is a sequence of char to match.  That is,
	 *  "fog" is treated as 'f' 'o' 'g' not as a single transition in
	 *  the DFA.  Machine== o-'f'->o-'o'->o-'g'->o and has n+1 states
	 *  for n characters.
	 */
	public Grip stringLiteral(GrammarAST stringLiteralAST) { return null; }

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
	 *  it would help much in the NFA->DFA construction.
	 *
	 *  TODO add to codegen: collapse alt blks that are sets into single matchSet
	 */
	public Grip ruleRef(Rule refDef, NFAState ruleStart) { return null; }

	/** From an empty alternative build Grip o-e->o */
	public Grip epsilon() { return null; }

	/** Build what amounts to an epsilon transition with a semantic
	 *  predicate action.  The pred is a pointer into the AST of
	 *  the SEMPRED token.
	 */
	public Grip sempred(GrammarAST pred) { return null; }

	/** Build what amounts to an epsilon transition with an action.
	 *  The action goes into NFA though it is ignored during analysis.
	 *  It slows things down a bit, but I must ignore predicates after
	 *  having seen an action (5-5-2008).
	 */
	public Grip action(GrammarAST action) { return null; }

	/** From A B build A-e->B (that is, build an epsilon arc from right
	 *  of A to left of B).
	 *
	 *  As a convenience, return B if A is null or return A if B is null.
	 */
	public Grip sequence(Grip A, Grip B) { return null; }

	/** From a set ('a'|'b') build
     *
     *  o->o-'a'..'b'->o->o (last NFAState is blockEndNFAState pointed to by all alts)
	 */
	public Grip blockFromSet(Grip set) { return null; }

	/** From A|B|..|Z alternative block build
     *
     *  o->o-A->o->o (last NFAState is blockEndNFAState pointed to by all alts)
     *  |          ^
     *  o->o-B->o--|
     *  |          |
     *  ...        |
     *  |          |
     *  o->o-Z->o--|
     *
     *  So every alternative gets begin NFAState connected by epsilon
     *  and every alt right side points at a block end NFAState.  There is a
     *  new NFAState in the NFAState in the Grip for each alt plus one for the
     *  end NFAState.
     *
     *  Special case: only one alternative: don't make a block with alt
     *  begin/end.
     *
     *  Special case: if just a list of tokens/chars/sets, then collapse
     *  to a single edge'd o-set->o graph.
     *
     *  Set alt number (1..n) in the left-Transition NFAState.
     */
	public Grip block(List alternativeGrips) { return null; }

	/** From (A)? build either:
	 *
	 *  o--A->o
	 *  |     ^
	 *  o---->|
	 *
	 *  or, if A is a block, just add an empty alt to the end of the block
	 */
	public Grip optional(Grip A) { return null; }

	/** From (A)+ build
	 *
	 *     |---|    (Transition 2 from A.right points at alt 1)
	 *     v   |    (follow of loop is Transition 1)
	 *  o->o-A-o->o
	 *
	 *  Meaning that the last NFAState in A points back to A's left Transition NFAState
	 *  and we add a new begin/end NFAState.  A can be single alternative or
	 *  multiple.
	 *
	 *  During analysis we'll call the follow link (transition 1) alt n+1 for
	 *  an n-alt A block.
	 */
	public Grip plus(Grip A) { return null; }

	/** From (A)* build
	 *
	 *     |---|
	 *     v   |
	 *  o->o-A-o--o (Transition 2 from block end points at alt 1; follow is Transition 1)
	 *  |         ^
	 *  o---------| (optional branch is 2nd alt of optional block containing A+)
	 *
	 *  Meaning that the last (end) NFAState in A points back to A's
	 *  left side NFAState and we add 3 new NFAStates (the
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
	 *  This is an accurate NFA representation of the meaning of (A)*, but
	 *  for generating code, I don't need a DFA for the optional branch by
	 *  virtue of how I generate code.  The exit-loopback-branch decision
	 *  is sufficient to let me make an appropriate enter, exit, loop
	 *  determination.  See codegen.g
	 */
	public Grip star(Grip A) { return null; }

	/** Build an atom with all possible values in its label */
	public Grip wildcard(GrammarAST associatedAST) { return null; }

	/** Build a subrule matching ^(. .*) (any tree or node). Let's use
	 *  (^(. .+) | .) to be safe.
	 */
	public Grip wildcardTree(GrammarAST associatedAST) { return null; }
}
