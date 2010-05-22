package org.antlr.v4.analysis;

import org.antlr.runtime.Token;
import org.antlr.v4.automata.*;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.misc.OrderedHashSet;
import org.antlr.v4.tool.Grammar;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** From NFA, return a linear approximate DFA if deterministic at
 *  a particular depth less than a max.  Compute all depths at once rather
 *  than, like v2, trying more and more lookahead.
 *
 *  No side effects outside class.
 */
public class LinearApproximator {
	public int MAX_LINEAR_APPROXIMATE_DEPTH = 2;

	Grammar g;
	int decision;

	int max_k = MAX_LINEAR_APPROXIMATE_DEPTH;

	/** Records state of a LOOK operation; used just for lookahead busy checks */
	static class LookaheadNFAConfig {
		public NFAState s;
		public int k;
		public NFAContext context;
		public LookaheadNFAConfig(NFAState s, int k, NFAContext context) {
			this.s = s;
			this.k = k;
			this.context = context;
		}

		public int hashCode() {	return s.stateNumber+k;	}

		public boolean equals(Object obj) {
			LookaheadNFAConfig ac = (LookaheadNFAConfig)obj;
			return this.s == ac.s &&
				   this.k == ac.k &&
				   this.context.equals(ac.context);
		}
	}

	/** Used during LOOK to detect computation cycles. E.g., ()* causes
	 *  infinite loop without it.  If we get to same state with same k
	 *  and same context, must be infinite loop.  Analogous to
	 *  closureBusy in NFA to DFA conversion.
	 */
	Set<LookaheadNFAConfig> lookBusy = new HashSet<LookaheadNFAConfig>();

	/** The lookahead associated with an alternative, 1..k. A WORK ARRAY. */
	IntervalSet[] look;

	/** Our goal is to produce a DFA that looks like we created the
	 *  usual way through subset construction. To look the same, we
	 *  have to store a set of NFA configurations within each DFA state.
	 *
	 *  A WORK ARRAY. Stores the NFA configurations for each lookahead
	 *  depth, 1..k.
	 */
	OrderedHashSet<NFAConfig>[] configs;

	public LinearApproximator(Grammar g, int decision) {
		this.g = g;
		this.decision = decision;
	}

	public LinearApproximator(Grammar g, int decision, int k) {
		this(g, decision);
		max_k = k;
	}

	public DFA createDFA(DecisionState s) {
		List<IntervalSet[]> altLook = new ArrayList<IntervalSet[]>();
		List<OrderedHashSet[]> altConfigs = new ArrayList<OrderedHashSet[]>();
		altLook.add(null); // alt 0 invalid
		altConfigs.add(null);

		look = new IntervalSet[max_k+1];
		configs = (OrderedHashSet<NFAConfig>[])Array.newInstance(OrderedHashSet.class, max_k+1);

		// COLLECT LOOKAHEAD 1..k
		for (int i=0; i<s.getNumberOfTransitions(); i++) {
			Transition t = s.transition(i);
			LOOK(t.target, MAX_LINEAR_APPROXIMATE_DEPTH);
			altLook.add(look.clone());
			altConfigs.add(configs.clone());
//			for (int k=1; k<=MAX_LINEAR_APPROXIMATE_DEPTH; k++) {
//				System.out.println(s.rule.name+"["+(i+1)+"]["+k+"]="+look[k].toString(g));
//				System.out.println("configs["+(i+1)+"]["+k+"]="+ configs[k].toString());
//			}
		}

		// FIND MIN DISJOINT k
		int k = disjoint(altLook);

		if ( k==0 ) return null;
		System.out.println("disjoint at k="+k);

		// BUILD DFA
		return createApproximateDFA(altLook, altConfigs, k);
	}

	/** Return lookahead depth at which lookahead sets are disjoint or return 0 */
	int disjoint(List<IntervalSet[]> altLook) {
		int k = 1;
		while ( k<=MAX_LINEAR_APPROXIMATE_DEPTH ) {
			boolean collision = false;
			IntervalSet combined = new IntervalSet();
			for (int a=1; a<altLook.size(); a++) {
				IntervalSet look = altLook.get(a)[k];
				if ( !look.and(combined).isNil() ) {
					System.out.println("alt "+a+" not disjoint with "+combined+"; look = "+look);
					collision = true;
					break;
				}
				combined.addAll(look);
			}
			if ( !collision ) return k;
			k++;
		}
		return 0;
	}

	DFA createApproximateDFA(List<IntervalSet[]> altLook,
							 List<OrderedHashSet[]> altConfigs,
							 int depth)
	{
		int nAlts = altLook.size() - 1;
		DFA dfa = new DFA(g, nAlts);
		DFAState start = new DFAState(dfa);
		dfa.startState = start;
		dfa.decision = decision;
		dfa.addState(start);
		for (int a=1; a<=nAlts; a++) {
			DFAState d = start;
			IntervalSet[] look = altLook.get(a);
			for (int k=1; k<=depth; k++) {
				DFAState t = new DFAState(dfa);
				t.nfaConfigs = altConfigs.get(a)[k];
				dfa.addState(t);
				if ( k==depth ) dfa.addAcceptState(a, t);
				Edge e = new Edge(t, look[k]);
				d.addEdge(e);
				d = t;
			}
		}

		return dfa;
	}

	/** From linear approximate LL(1) DFA, get lookahead per alt; 1..n */
	public static IntervalSet[] getLL1LookaheadSets(DFA dfa) {
		IntervalSet[] look = new IntervalSet[dfa.nAlts+1];
		DFAState s0 = dfa.startState;
		for (int a=1; a<=dfa.nAlts; a++) {
			look[a] = s0.edges.get(a-1).label;
		}
		return look;
	}

	/** From an NFA state, s, find the set of all labels reachable from s at
	 *  depth k.
	 */
	public IntervalSet[] LOOK(NFAState s, int k) {
		System.out.println("LOOK("+s.stateNumber+", "+k+")");
		lookBusy.clear();
		for (int i=1; i<=max_k; i++) { // init / reset work arrays
			look[i] = new IntervalSet();
			configs[i] = new OrderedHashSet<NFAConfig>();
		}
		
		_LOOK(s, k, NFAContext.EMPTY());
		return look;
	}

	void _LOOK(NFAState s, int k, NFAContext context) {
		//System.out.println("_LOOK("+s.stateNumber+", "+k+", ctx="+context);
		LookaheadNFAConfig ac = new LookaheadNFAConfig(s,k,context);
		if ( lookBusy.contains(ac) ) return;
		lookBusy.add(ac);

		if ( s instanceof RuleStopState && !context.isEmpty() ) {
			_LOOK(context.returnState, k, context.parent);
			return;
		}

		int n = s.getNumberOfTransitions();
		for (int i=0; i<n; i++) {
			Transition t = s.transition(i);
			if ( t instanceof RuleTransition ) {
				NFAContext newContext =
					new NFAContext(context, ((RuleTransition)t).followState);
				_LOOK(t.target, k, newContext);
			}
			else if ( t.isEpsilon() ) {
				_LOOK(t.target, k, context);
			}
			else {
				System.out.println("adding "+ t.label().toString(g) +" @ i="+(max_k-k+1));
				look[max_k-k+1].addAll( t.label() );
				NFAConfig c = new NFAConfig(t.target, 0, context,
											SemanticContext.EMPTY_SEMANTIC_CONTEXT);
				configs[max_k-k+1].add(c);
				if ( k>1 ) _LOOK(t.target, k-1, context);
			}
		}
	}

	/** Compute FOLLOW of element but don't leave rule to compute global
	 *  context-free FOLLOW.  Used for rule invocation, match token, and
	 *  error sync'ing.
	 */
	public IntervalSet LOOK(NFAState s) {
		System.out.println("LOOK("+s.stateNumber+")");
		lookBusy.clear();
		IntervalSet fset = new IntervalSet();
		_LOOK(s, NFAContext.EMPTY(), fset);
		return fset;
	}

	void _LOOK(NFAState s, NFAContext context, IntervalSet fset) {
		//System.out.println("_LOOK("+s.stateNumber+", "+k+", ctx="+context);
		LookaheadNFAConfig ac = new LookaheadNFAConfig(s,1,context);
		if ( lookBusy.contains(ac) ) return;
		lookBusy.add(ac);

		if ( s instanceof RuleStopState ) {
			if ( !context.isEmpty() ) _LOOK(context.returnState, context.parent, fset);
			else fset.add(Token.EOR_TOKEN_TYPE); // hit end of rule
			return;
		}

		int n = s.getNumberOfTransitions();
		for (int i=0; i<n; i++) {
			Transition t = s.transition(i);
			if ( t instanceof RuleTransition ) {
				NFAContext newContext =
					new NFAContext(context, ((RuleTransition)t).followState);
				_LOOK(t.target, newContext, fset);
			}
			else if ( t.isEpsilon() ) {
				_LOOK(t.target, context, fset);
			}
			else {
				fset.addAll( t.label() );
			}
		}
	}

//	LookaheadSet ____LOOK(NFAState s, int k, NFAState context) {
//		//System.out.println("_LOOK("+s.stateNumber+", "+k+", "+context+")");
//
////		if ( lookBusy.contains(s) ) {
////			// return a copy of an empty set; we may modify set inline
////			return new LookaheadSet();
////		}
////		lookBusy.add(s);
//
//		if ( s instanceof RuleStopState && context!=null ) {
//			return LookaheadSet.missingDepth(k);
//		}
//
//		LookaheadSet tset = new LookaheadSet();
//		int n = s.getNumberOfTransitions();
//		for (int i=0; i<n; i++) {
//			Transition t = s.transition(i);
//			LookaheadSet look = null;
//			if ( t instanceof RuleTransition ) {
//				look = _LOOK(t.target, k, ((RuleTransition)t).followState);
//				if ( look.eorDepths!=null ) {
//					for (Integer _k : look.eorDepths.toList() ) {
//						look.combine( _LOOK(((RuleTransition)t).followState, _k, context) );
//					}
//					look.eorDepths = null;
//				}
//			}
//			else if ( t.isEpsilon() ) look = _LOOK(t.target, k, context);
//			else if ( k==1 )          look = new LookaheadSet( t.label() );
//			else if ( k>1 )           look = _LOOK(t.target, k-1, context);
//			tset.combine( look );
//		}
//
//		//lookBusy.remove(s);
//
//		return tset;
//	}

//	public LookaheadSet FOLLOW(Rule r) {
//		LookaheadSet f = FOLLOWCache.get(r);
//		if ( f!=null ) return f;
//		f = _FIRST(r.stopState, true);
//		FOLLOWCache.put(r, f);
//		return f;
//	}

//	public LinearApproximator(DFA dfa) {
//		this.dfa = dfa;
//		// make room for max lookahead of num states [1..nAlts][1..numstates]
//		look = new LookaheadSet[dfa.nAlts+1][dfa.stateSet.size()+1];
//		max = new int[dfa.nAlts+1];
//		fillLookaheadSets(dfa.startState, 1);
//		for (IntSet[] l : look)	System.out.println(Arrays.toString(l));
//		System.out.println("max k="+maxDepth);
//	}
//
//	public LookaheadSet[] getLookaheadSets() {
//		LookaheadSet[] altLook = new LookaheadSet[dfa.nAlts+1];
//		for (int a=1; a<=dfa.nAlts; a++) altLook[a] = look[a][max[a]];
//		return altLook;
//	}

//	public int getLookaheadDepth(int alt) { return max[alt]; }
//
//	public boolean isDeterministic() {
//		// lookahead at smallest lookahead depth for alts i and j must be disjoint
//		for (int i=1; i<=dfa.nAlts; i++) {
//			for (int j=i+1; j<=dfa.nAlts; j++) {
//				int k = Math.min(max[i], max[j]);
//				// two alts aren't disjoint at depth k. nondeterministic; bolt.
//				if ( !look[i][k].and(look[j][k]).isNil() ) return false;
//			}
//		}
//		return true;
//	}
//
//
//	void fillLookaheadSets(DFAState d, int k) {
//		for (Edge e : d.edges) {
//			// if ( e instanceof PredicateEdge ) continue; NO PREDS IF NOTAMBIG
//			Set<Integer> alts = e.target.getAltSet();
//			for (int a : alts) {
//				if ( look[a][k]==null ) look[a][k] = new LookaheadSet();
//				max[a] = Math.max(max[a], k);
//				maxDepth = Math.max(maxDepth, k);
//				look[a][k].addAll(e.label);
//			}
//			fillLookaheadSets(e.target, k+1);
//		}
//	}
	
}
