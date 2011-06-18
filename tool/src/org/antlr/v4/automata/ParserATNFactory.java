package org.antlr.v4.automata;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.v4.misc.*;
import org.antlr.v4.parse.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.tool.*;

import java.lang.reflect.Constructor;
import java.util.*;

/** ATN construction routines triggered by ATNBuilder.g.
 *
 *  No side-effects. It builds an ATN object and returns it.
 */
public class ParserATNFactory implements ATNFactory {
	public Grammar g;
	public Rule currentRule;
	ATN atn;

	public ParserATNFactory(Grammar g) { this.g = g; atn = new ATN(g); }

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
		RuleStartState start = atn.ruleToStartState.get(r);
		epsilon(start, blk.left);
		RuleStopState stop = atn.ruleToStopState.get(r);
		epsilon(blk.right, stop);
		Handle h = new Handle(start, stop);
//		FASerializer ser = new FASerializer(g, h.left);
//		System.out.println(ruleAST.toStringTree()+":\n"+ser);
		ruleAST.atnState = start;
		return h;
	}

	/** From label A build Graph o-A->o */
	public Handle tokenRef(TerminalAST node) {
		ATNState left = newState(node);
		ATNState right = newState(node);
		int ttype = g.getTokenType(node.getText());
		left.transition = new AtomTransition(ttype, right);
		right.incidentTransition = left.transition;
		node.atnState = left;
		return new Handle(left, right);
	}

	/** From set build single edge graph o->o-set->o.  To conform to
     *  what an alt block looks like, must have extra state on left.
     */
	public Handle set(IntervalSet set, GrammarAST associatedAST) {
		ATNState left = newState(associatedAST);
		ATNState right = newState(associatedAST);
		left.transition = new SetTransition(associatedAST, set, right);
		right.incidentTransition = left.transition;
		associatedAST.atnState = left;
		return new Handle(left, right);
	}

	public Handle tree(List<Handle> els) {
		return null;
	}

	/** Not valid for non-lexers */
	public Handle range(GrammarAST a, GrammarAST b) { throw new UnsupportedOperationException(); }

	/** ~atom only */
	public Handle not(GrammarAST node) {
		ATNState left = newState(node);
		ATNState right = newState(node);
		int ttype = getTokenType((GrammarAST) node.getChild(0));
		left.transition = new NotAtomTransition(ttype, right);
		right.incidentTransition = left.transition;
		node.atnState = left;
		return new Handle(left, right);
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
		RuleStartState start = atn.ruleToStartState.get(r);
		ATNState left = newState(node);
		ATNState right = newState(node);
		RuleTransition call = new RuleTransition(r, start, right);
		left.addTransition(call);

		node.atnState = left;
		return new Handle(left, right);
	}

	public void addFollowLink(Rule r, ATNState right) {
		// add follow edge from end of invoked rule
		RuleStopState stop = atn.ruleToStopState.get(r);
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
		PredicateTransition p = new PredicateTransition(pred, right);
		p.ruleIndex = currentRule.index;
		p.predIndex = g.sempreds.get(pred);
		left.transition = p;
		pred.atnState = left;
		return new Handle(left, right);
	}

	public Handle gated_sempred(GrammarAST pred) {
		ATNState left = newState(pred);
		ATNState right = newState(pred);
		left.transition = new PredicateTransition(pred, right);
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
		ActionTransition a = new ActionTransition(action, right);
		a.ruleIndex = currentRule.index;
		if ( action.getType()==ANTLRParser.FORCED_ACTION ) {
			a.actionIndex = g.actions.get(action);
		}
		left.transition = a;
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
			return makeBlock(start, blkAST, alts);
		}
		switch ( ebnfRoot.getType() ) {
			case ANTLRParser.OPTIONAL :
				BlockStartState start = (BlockStartState)newState(BlockStartState.class, blkAST);
				Handle h = makeBlock(start, blkAST, alts);
				return optional(ebnfRoot, h);
			case ANTLRParser.CLOSURE :
				BlockStartState star = (StarBlockStartState)newState(StarBlockStartState.class, ebnfRoot);
				h = makeBlock(star, blkAST, alts);
				return star(ebnfRoot, h);
			case ANTLRParser.POSITIVE_CLOSURE :
				PlusBlockStartState plus = (PlusBlockStartState)newState(PlusBlockStartState.class, ebnfRoot);
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
		atn.defineDecisionState(start);
		Handle h = new Handle(start, end);
//		FASerializer ser = new FASerializer(g, h.left);
//		System.out.println(blkAST.toStringTree()+":\n"+ser);
		blkAST.atnState = start;
		return h;
	}

	public Handle notBlock(GrammarAST notAST, List<GrammarAST> terminals) {
		// assume list of atoms
		IntervalSet notSet = new IntervalSet();
		for (GrammarAST elemAST : terminals) {
			if ( elemAST.getType()==ANTLRParser.RANGE ) {
				GrammarAST from = (GrammarAST)elemAST.getChild(0);
				GrammarAST to = (GrammarAST)elemAST.getChild(1);
				notSet.add(getTokenType(from), getTokenType(to));
			}
			else {
				notSet.add(getTokenType(elemAST));
			}
		}

		ATNState left = newState(notAST);
		ATNState right = newState(notAST);
		left.transition = new NotSetTransition(notAST, notSet, right);
		right.incidentTransition = left.transition;
		notAST.atnState = left;
		return new Handle(left, right);
	}

	public Handle alt(List<Handle> els) {
		Handle prev = null;
		for (Handle el : els) { // hook up elements
			if ( prev!=null ) epsilon(prev.right, el.left);
			prev = el;
		}
		Handle first = els.get(0);
		Handle last = els.get(els.size()-1);
		if ( first==null || last==null ) {
			g.tool.errMgr.toolError(ErrorType.INTERNAL_ERROR, "alt Handle has first|last == null");
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
	public Handle optional(GrammarAST optAST, Handle blk) {
		// TODO: no such thing as nongreedy ()? so give error
		BlockStartState blkStart = (BlockStartState)blk.left;
		epsilon(blkStart, blk.right);
		optAST.atnState = blk.left;
		return blk;
	}

	/** From (blk)+ build
	 *
	 *     |---------|
	 *     v         |
	 *  o->o-A-o->o->o->o     loop back points at start of all alts
	 *  |         ^
	 *  |->o-B-o--|
	 *
	 *  Meaning that the last ATNState in A blk points to loop back node,
	 *  which points back to block start.  We add start/end nodes to
	 *  outside.
	 */
	public Handle plus(GrammarAST plusAST, Handle blk) {
		PlusBlockStartState start = (PlusBlockStartState)blk.left;
		atn.defineDecisionState(start); // we don't use in code gen though
		plusAST.atnState = start;
		PlusLoopbackState loop = (PlusLoopbackState)newState(PlusLoopbackState.class, plusAST);
		ATNState end = (ATNState)newState(ATNState.class, plusAST);
		start.loopBackState = loop;
		epsilon(blk.right, loop);
		BlockAST blkAST = (BlockAST)plusAST.getChild(0);
		// if not greedy, priority to exit branch; make it first
		if ( !isGreedy(blkAST) ) epsilon(loop, end);
		// connect loop back to all alt left edges
		for (Transition trans : start.transitions) {
			epsilon(loop, trans.target);
		}
		// if greedy, last alt of decisions is exit branch
		if ( isGreedy(blkAST) ) epsilon(loop, end);
		atn.defineDecisionState(loop);
		return new Handle(start, end);
	}

	/** From (blk)* build
	 *
	 *     |----------|
	 *     v          |
	 *     o-[blk]-o->o  o
	 *     |             ^
	 *     o-------------| (optional branch is nth alt of StarBlockStartState)
	 *
	 *  There 1 decision point in a A*.
	 *
	 *  Note that the optional bypass must jump outside the loop as (A|B)* is
	 *  not the same thing as (A|B|)+.
	 */
	public Handle star(GrammarAST starAST, Handle elem) {
		BlockAST blkAST = (BlockAST)starAST.getChild(0);

		StarBlockStartState blkStart = (StarBlockStartState)elem.left;
		BlockEndState blkEnd = (BlockEndState)elem.right;

		StarLoopbackState loop = (StarLoopbackState)newState(StarLoopbackState.class, starAST);
		ATNState end = (ATNState)newState(ATNState.class, starAST);
		// If greedy, exit alt is last, else exit is first
		if ( isGreedy(blkAST) ) {
			epsilon(blkStart, end); // bypass edge
		}
		else {
			blkStart.addTransitionFirst(new EpsilonTransition(end));
		}
		epsilon(loop, blkStart);
		epsilon(blkEnd, loop);
		starAST.atnState = blkStart;
		return new Handle(blkStart, end);
	}

	/** Build an atom with all possible values in its label */
	public Handle wildcard(GrammarAST node) {
		ATNState left = newState(node);
		ATNState right = newState(node);
		int ttype = g.getTokenType(node.getText());
		left.transition = new WildcardTransition(right);
		right.incidentTransition = left.transition;
		node.atnState = left;
		return new Handle(left, right);
	}

	/** Build a subrule matching ^(. .*) (any tree or node). Let's use
	 *  (^(. .+) | .) to be safe.
	 */
	public Handle wildcardTree(GrammarAST associatedAST) { throw new UnsupportedOperationException(); }

	void epsilon(ATNState a, ATNState b) {
		if ( a!=null ) a.addTransition(new EpsilonTransition(b));
	}

	/** Define all the rule begin/end ATNStates to solve forward reference
	 *  issues.
	 */
	void createRuleStartAndStopATNStates() {
		for (Rule r : g.rules.values()) {
			RuleStartState start = (RuleStartState)newState(RuleStartState.class, r.ast);
			RuleStopState stop = (RuleStopState)newState(RuleStopState.class, r.ast);
			start.stopState = stop;
			start.setRule(r);
			stop.setRule(r);
			atn.ruleToStartState.put(r, start);
			atn.rules.add(start);
			atn.ruleToStopState.put(r, stop);
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
			ATNState stop = atn.ruleToStopState.get(r);
			if ( stop.getNumberOfTransitions()>0 ) continue;
			n++;
			Transition t = new AtomTransition(Token.EOF, eofTarget);
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

	public ATNState newState(Class nodeType, GrammarAST node) {
		try {
			Constructor ctor = nodeType.getConstructor();
			ATNState s = (ATNState)ctor.newInstance();
			s.ast = node;
			s.setRule(currentRule);
			atn.addState(s);
			return s;
		}
		catch (Exception e) {
			ErrorManager.internalError("can't create ATN node: "+nodeType.getName(), e);
		}
		return null;
	}

	public ATNState newState(GrammarAST node) {
		ATNState n = new ATNState();
		n.setRule(currentRule);
		n.ast = node;
		atn.addState(n);
		return n;
	}

	public ATNState newState() { return newState(null); }

	public boolean isGreedy(BlockAST blkAST) {
		boolean greedy = true;
		String greedyOption = blkAST.getOption("greedy");
		if ( blockHasWildcardAlt(blkAST) || greedyOption!=null&&greedyOption.equals("false") ) {
			greedy = false;
		}
		return greedy;
	}

	// (BLOCK (ALT .)) or (BLOCK (ALT 'a') (ALT .))
	public static boolean blockHasWildcardAlt(GrammarAST block) {
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
