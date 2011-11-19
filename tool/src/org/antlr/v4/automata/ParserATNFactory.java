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

package org.antlr.v4.automata;


import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.v4.misc.CharSupport;
import org.antlr.v4.parse.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.semantics.UseDefAnalyzer;
import org.antlr.v4.tool.*;
import org.antlr.v4.tool.ast.*;

import java.lang.reflect.Constructor;
import java.util.*;

/** ATN construction routines triggered by ATNBuilder.g.
 *
 *  No side-effects. It builds an ATN object and returns it.
 */
public class ParserATNFactory implements ATNFactory {
	@NotNull
	public final Grammar g;

	@NotNull
	public final ATN atn;

	public Rule currentRule;

	public ParserATNFactory(@NotNull Grammar g) { this.g = g; atn = new ATN(); }

	public ATN createATN() {
		_createATN(g.rules.values());
		atn.maxTokenType = g.getMaxTokenType();
		addEOFTransitionToStartRules();
		return atn;
	}

	public void _createATN(Collection<Rule> rules) {
		createRuleStartAndStopATNStates();

		GrammarASTAdaptor adaptor = new GrammarASTAdaptor();
		for (Rule r : rules) {
			// find rule's block
			GrammarAST blk = (GrammarAST)r.ast.getFirstChildWithType(ANTLRParser.BLOCK);
			CommonTreeNodeStream nodes = new CommonTreeNodeStream(adaptor,blk);
			ATNBuilder b = new ATNBuilder(nodes,this);
			try {
				setCurrentRuleName(r.name);
				Handle h = b.block(null);
				rule(r.ast, r.name, h);
			}
			catch (RecognitionException re) {
				ErrorManager.fatalInternalError("bad grammar AST structure", re);
			}
		}
	}

	public void setCurrentRuleName(String name) {
		this.currentRule = g.getRule(name);
	}

	/* start->ruleblock->end */
	public Handle rule(GrammarAST ruleAST, String name, Handle blk) {
		Rule r = g.getRule(name);
		RuleStartState start = atn.ruleToStartState[r.index];
		epsilon(start, blk.left);
		RuleStopState stop = atn.ruleToStopState[r.index];
		epsilon(blk.right, stop);
		Handle h = new Handle(start, stop);
		ATNPrinter ser = new ATNPrinter(g, h.left);
//		System.out.println(ruleAST.toStringTree()+":\n"+ser.asString());
		ruleAST.atnState = start;
		return h;
	}

	/** From label A build Graph o-A->o */
	public Handle tokenRef(TerminalAST node) {
		ATNState left = newState(node);
		ATNState right = newState(node);
		int ttype = g.getTokenType(node.getText());
		left.addTransition(new AtomTransition(right, ttype));
		right.incidentTransition = left.transition(0);
		node.atnState = left;
		return new Handle(left, right);
	}

	/** From set build single edge graph o->o-set->o.  To conform to
     *  what an alt block looks like, must have extra state on left.
	 *  This handles ~A also, converted to ~{A} set.
     */
	public Handle set(GrammarAST associatedAST, List<GrammarAST> terminals, boolean invert) {
		ATNState left = newState(associatedAST);
		ATNState right = newState(associatedAST);
		IntervalSet set = new IntervalSet();
		for (GrammarAST t : terminals) {
			int ttype = g.getTokenType(t.getText());
			set.add(ttype);
		}
		if ( invert ) {
			IntervalSet notSet = (IntervalSet)set.complement(Token.MIN_TOKEN_TYPE, g.getMaxTokenType());
			left.addTransition(new NotSetTransition(right, set, notSet));
		}
		else {
			left.addTransition(new SetTransition(right, set));
		}
		right.incidentTransition = left.transition(0);
		associatedAST.atnState = left;
		return new Handle(left, right);
	}

	public Handle tree(GrammarAST node, List<Handle> els) {
		throw new UnsupportedOperationException("^(...) not allowed in non-tree grammar");
	}

	/** Not valid for non-lexers */
	public Handle range(GrammarAST a, GrammarAST b) {
		throw new UnsupportedOperationException();
	}

	protected int getTokenType(GrammarAST atom) {
		int ttype;
		if ( g.isLexer() ) {
			ttype = CharSupport.getCharValueFromGrammarCharLiteral(atom.getText());
		}
		else {
			ttype = g.getTokenType(atom.getText());
		}
		return ttype;
	}

	/** For a non-lexer, just build a simple token reference atom. */
	public Handle stringLiteral(TerminalAST stringLiteralAST) {
		return tokenRef(stringLiteralAST);
	}

	/** For reference to rule r, build
	 *
	 *  o->(r)  o
	 *
	 *  where (r) is the start of rule r and the trailing o is not linked
	 *  to from rule ref state directly (uses followState).
	 */
	public Handle ruleRef(GrammarAST node) {
		Handle h = _ruleRef(node);
		Rule r = g.getRule(node.getText());
		addFollowLink(r, h.right);
		return h;
	}

	public Handle _ruleRef(GrammarAST node) {
		Rule r = g.getRule(node.getText());
		RuleStartState start = atn.ruleToStartState[r.index];
		ATNState left = newState(node);
		ATNState right = newState(node);
		RuleTransition call = new RuleTransition(start, r.index, right);
		left.addTransition(call);

		node.atnState = left;
		return new Handle(left, right);
	}

	public void addFollowLink(Rule r, ATNState right) {
		// add follow edge from end of invoked rule
		RuleStopState stop = atn.ruleToStopState[r.index];
		epsilon(stop, right);
	}

	/** From an empty alternative build  o-e->o */
	public Handle epsilon(GrammarAST node) {
		ATNState left = newState(node);
		ATNState right = newState(node);
		epsilon(left, right);
		node.atnState = left;
		return new Handle(left, right);
	}

	/** Build what amounts to an epsilon transition with a semantic
	 *  predicate action.  The pred is a pointer into the AST of
	 *  the SEMPRED token.
	 */
	public Handle sempred(PredAST pred) {
		//System.out.println("sempred: "+ pred);
		ATNState left = newState(pred);
		ATNState right = newState(pred);
		boolean isCtxDependent = UseDefAnalyzer.actionIsContextDependent(pred);
		PredicateTransition p = new PredicateTransition(right, currentRule.index, g.sempreds.get(pred), isCtxDependent);
		left.addTransition(p);
		pred.atnState = left;
		return new Handle(left, right);
	}

	/** Build what amounts to an epsilon transition with an action.
	 *  The action goes into ATN though it is ignored during prediction
	 *  if actionIndex < 0.  Only forced are executed during prediction.
	 */
	public Handle action(ActionAST action) {
		//System.out.println("action: "+action);
		ATNState left = newState(action);
		ATNState right = newState(action);
		ActionTransition a = new ActionTransition(right, currentRule.index);
		left.addTransition(a);
		action.atnState = left;
		return new Handle(left, right);
	}

	/** From A|B|..|Z alternative block build
     *
     *  o->o-A->o->o (last ATNState is BlockEndState pointed to by all alts)
     *  |          ^
     *  |->o-B->o--|
     *  |          |
     *  ...        |
     *  |          |
     *  |->o-Z->o--|
     *
     *  So start node points at every alternative with epsilon transition
	 *  and every alt right side points at a block end ATNState.
	 *
	 *  Special case: only one alternative: don't make a block with alt
	 *  begin/end.
	 *
	 *  Special case: if just a list of tokens/chars/sets, then collapse
	 *  to a single edge'd o-set->o graph.
	 *
	 *  TODO: Set alt number (1..n) in the states?
	 */
	public Handle block(BlockAST blkAST, GrammarAST ebnfRoot, List<Handle> alts) {
		if ( ebnfRoot==null ) {
			if ( alts.size()==1 ) {
				Handle h = alts.get(0);
				blkAST.atnState = h.left;
				return h;
			}
			BlockStartState start = (BlockStartState)newState(BlockStartState.class, blkAST);
			if ( alts.size()>1 ) atn.defineDecisionState(start);
			return makeBlock(start, blkAST, alts);
		}
		switch ( ebnfRoot.getType() ) {
			case ANTLRParser.OPTIONAL :
				BlockStartState start = (BlockStartState)newState(BlockStartState.class, blkAST);
				atn.defineDecisionState(start);
				Handle h = makeBlock(start, blkAST, alts);
				return optional(ebnfRoot, h);
			case ANTLRParser.CLOSURE :
				BlockStartState star = (StarBlockStartState)newState(StarBlockStartState.class, ebnfRoot);
				if ( alts.size()>1 ) atn.defineDecisionState(star);
				h = makeBlock(star, blkAST, alts);
				return star(ebnfRoot, h);
			case ANTLRParser.POSITIVE_CLOSURE :
				PlusBlockStartState plus = (PlusBlockStartState)newState(PlusBlockStartState.class, ebnfRoot);
				if ( alts.size()>1 ) atn.defineDecisionState(plus);
				h = makeBlock(plus, blkAST, alts);
				return plus(ebnfRoot, h);
		}
		return null;
	}

	protected Handle makeBlock(BlockStartState start, GrammarAST blkAST, List<Handle> alts) {
		BlockEndState end = (BlockEndState)newState(BlockEndState.class, blkAST);
		start.endState = end;
		for (Handle alt : alts) {
			epsilon(start, alt.left);
			epsilon(alt.right, end);
		}
//		if ( alts.size()>1 ) atn.defineDecisionState(start);
		Handle h = new Handle(start, end);
//		FASerializer ser = new FASerializer(g, h.left);
//		System.out.println(blkAST.toStringTree()+":\n"+ser);
		blkAST.atnState = start;
		return h;
	}

//	public Handle notBlock(GrammarAST notAST, Handle set) {
//		SetTransition st = (SetTransition)set.left.transition;
//		set.left.addTransition(new NotSetTransition(st.label, set.right);
//		notAST.atnState = set.left;
//		return set;
//	}

	@NotNull
	public Handle alt(@NotNull List<Handle> els) {
		return elemList(els);
	}

	@NotNull
	public Handle elemList(@NotNull List<Handle> els) {
		Handle prev = null;
		for (Handle el : els) { // hook up elements
			if ( prev!=null ) epsilon(prev.right, el.left);
			prev = el;
		}
		Handle first = els.get(0);
		Handle last = els.get(els.size()-1);
		if ( first==null || last==null ) {
			g.tool.errMgr.toolError(ErrorType.INTERNAL_ERROR, "element list has first|last == null");
		}
		return new Handle(first.left, last.right);
	}

	/** From (A)? build either:
	 *
	 *  o--A->o
	 *  |     ^
	 *  o---->|
	 *
	 *  or, if A is a block, just add an empty alt to the end of the block
	 */
	@NotNull
	public Handle optional(@NotNull GrammarAST optAST, @NotNull Handle blk) {
		// TODO: no such thing as nongreedy ()? so give error
		BlockStartState blkStart = (BlockStartState)blk.left;
		epsilon(blkStart, blk.right);
		optAST.atnState = blk.left;
		return blk;
	}

	/** From (blk)+ build
	 *
	 *   |---------|
	 *   v         |
	 *  [o-blk-o]->o->o
	 *
	 *  We add a decision for loop back node to the existing one at
	 *  blk start.
	 */
	@NotNull
	public Handle plus(@NotNull GrammarAST plusAST, @NotNull Handle blk) {
		PlusBlockStartState blkStart = (PlusBlockStartState)blk.left;
		BlockEndState blkEnd = (BlockEndState)blk.right;

		PlusLoopbackState loop = (PlusLoopbackState)newState(PlusLoopbackState.class, plusAST);
		atn.defineDecisionState(loop);
		ATNState end = newState(ATNState.class, plusAST);
		blkStart.loopBackState = loop;

		plusAST.atnState = blkStart;
		epsilon(blkEnd, loop);		// blk can see loop back

		BlockAST blkAST = (BlockAST)plusAST.getChild(0);
		loop.isGreedy = isGreedy(blkAST);
		if ( !g.isLexer() || loop.isGreedy ) {
			epsilon(loop, blkStart);	// loop back to start
			epsilon(loop, end);			// or exit
		}
		else { // only lexers flip entry/exit branches for nongreedy
			// if not greedy, priority to exit branch; make it first
			epsilon(loop, end);			// exit
			epsilon(loop, blkStart);	// loop back to start
		}

		return new Handle(blkStart, end);
	}

	/** From (blk)* build ( blk+ )? with *two* decisions, one for entry
	 *  and one for choosing alts of blk.
	 *
	 *   |-------------|
	 *   v             |
	 *   o--[o-blk-o]->o->o
	 *   |                ^
	 *   o----------------|
	 *
	 *  Note that the optional bypass must jump outside the loop as (A|B)* is
	 *  not the same thing as (A|B|)+.
	 */
	@NotNull
	public Handle star(@NotNull GrammarAST starAST, @NotNull Handle elem) {
		StarBlockStartState blkStart = (StarBlockStartState)elem.left;
		BlockEndState blkEnd = (BlockEndState)elem.right;

		StarLoopEntryState entry = (StarLoopEntryState)newState(StarLoopEntryState.class, starAST);
		atn.defineDecisionState(entry);
		ATNState end = newState(ATNState.class, starAST);
		StarLoopbackState loop = (StarLoopbackState)newState(StarLoopbackState.class, starAST);
		entry.loopBackState = loop;

		BlockAST blkAST = (BlockAST)starAST.getChild(0);
		entry.isGreedy = isGreedy(blkAST);
		if ( !g.isLexer() || entry.isGreedy ) {
			epsilon(entry, blkStart);	// loop enter edge (alt 1)
			epsilon(entry, end);		// bypass loop edge (alt 2)
		}
		else { // only lexers flip entry/exit branches for nongreedy
			// if not greedy, priority to exit branch; make it first
			epsilon(entry, end);		// bypass loop edge (alt 1)
			epsilon(entry, blkStart);	// loop enter edge (alt 2)
		}
		epsilon(blkEnd, loop);		// block end hits loop back
		epsilon(loop, entry);		// loop back to entry/exit decision

		starAST.atnState = entry;	// decision is to enter/exit; blk is its own decision
		return new Handle(entry, end);
	}

	/** Build an atom with all possible values in its label */
	@NotNull
	public Handle wildcard(GrammarAST node) {
		ATNState left = newState(node);
		ATNState right = newState(node);
		left.addTransition(new WildcardTransition(right));
		right.incidentTransition = left.transition(0);
		node.atnState = left;
		return new Handle(left, right);
	}

	/** Build a subrule matching ^(. .*) (any tree or node). Let's use
	 *  (^(. .+) | .) to be safe.
	 */
	public Handle wildcardTree(GrammarAST associatedAST) { throw new UnsupportedOperationException(); }

	void epsilon(ATNState a, @NotNull ATNState b) {
		if ( a!=null ) a.addTransition(new EpsilonTransition(b));
	}

	/** Define all the rule begin/end ATNStates to solve forward reference
	 *  issues.
	 */
	void createRuleStartAndStopATNStates() {
		atn.ruleToStartState = new RuleStartState[g.rules.size()];
		atn.ruleToStopState = new RuleStopState[g.rules.size()];
		for (Rule r : g.rules.values()) {
			RuleStartState start = newState(RuleStartState.class, r.ast);
			RuleStopState stop = newState(RuleStopState.class, r.ast);
			start.stopState = stop;
			start.setRuleIndex(r.index);
			stop.setRuleIndex(r.index);
			atn.ruleToStartState[r.index] = start;
			atn.ruleToStopState[r.index] = stop;
		}
	}

	/** add an EOF transition to any rule end ATNState that points to nothing
     *  (i.e., for all those rules not invoked by another rule).  These
     *  are start symbols then.
	 *
	 *  Return the number of grammar entry points; i.e., how many rules are
	 *  not invoked by another rule (they can only be invoked from outside).
	 *  These are the start rules.
     */
	public int addEOFTransitionToStartRules() {
		int n = 0;
		ATNState eofTarget = newState(null); // one unique EOF target for all rules
		for (Rule r : g.rules.values()) {
			ATNState stop = atn.ruleToStopState[r.index];
			if ( stop.getNumberOfTransitions()>0 ) continue;
			n++;
			Transition t = new AtomTransition(eofTarget, Token.EOF);
			stop.addTransition(t);
		}
		return n;
	}

	public Handle label(Handle t) {
		return t;
	}

	public Handle listLabel(Handle t) {
		return t;
	}

	// TODO (sam): should we allow this to throw an exception instead of returning null?
	@Nullable
	public <T extends ATNState> T newState(@NotNull Class<T> nodeType, GrammarAST node) {
		try {
			Constructor<T> ctor = nodeType.getConstructor();
			T s = ctor.newInstance();
			if ( currentRule==null ) s.setRuleIndex(-1);
			else s.setRuleIndex(currentRule.index);
			atn.addState(s);
			return s;
		}
		catch (Exception e) {
			ErrorManager.internalError("can't create ATN node: "+nodeType.getName(), e);
		}
		return null;
	}

	@NotNull
	public ATNState newState(@Nullable GrammarAST node) {
		ATNState n = new ATNState();
		n.setRuleIndex(currentRule.index);
		atn.addState(n);
		return n;
	}

	@NotNull
	public ATNState newState() { return newState(null); }

	public boolean isGreedy(@NotNull BlockAST blkAST) {
		boolean greedy = true;
		String greedyOption = blkAST.getOptionString("greedy");
		if ( blockHasWildcardAlt(blkAST) || greedyOption!=null&&greedyOption.equals("false") ) {
			greedy = false;
		}
		return greedy;
	}

	// (BLOCK (ALT .)) or (BLOCK (ALT 'a') (ALT .))
	public static boolean blockHasWildcardAlt(@NotNull GrammarAST block) {
		for (Object alt : block.getChildren()) {
			if ( !(alt instanceof AltAST) ) continue;
			AltAST altAST = (AltAST)alt;
			if ( altAST.getChildCount()==1 ) {
				Tree e = altAST.getChild(0);
				if ( e.getType()==ANTLRParser.WILDCARD ) {
					return true;
				}
			}
		}
		return false;
	}
}
