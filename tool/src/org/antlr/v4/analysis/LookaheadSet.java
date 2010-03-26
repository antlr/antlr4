package org.antlr.v4.analysis;

import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.Grammar;

/** This object holds all information needed to represent
 *  the lookahead for any particular lookahead computation
 *  for a <b>single</b> lookahead depth.
 */
public class LookaheadSet {
	public IntervalSet set;

	/** Used for rule references.  If we try
	 * to compute look(k, ruleref) and there are fewer
	 * than k lookahead terminals before the end of the
	 * the rule, eor will be returned (don't want to
	 * pass the end of the rule).  We must track when the
	 * the lookahead got stuck.  For example,
	 * <pre>
	 * 		a : b A B E F G;
	 * 		b : C ;
	 * </pre>
	 * LOOK(5, ref-to(b)) is {<EPSILON>} with depth = 4, which
	 * indicates that at 2 (5-4+1) tokens ahead, end of rule was reached.
	 * Therefore, the token at 4=5-(5-4) past rule ref b must be
	 * included in the set == F.
	 * The situation is complicated by the fact that a computation
	 * may hit the end of a rule at many different depths.  For example,
	 * <pre>
	 * 		a : b A B C ;
	 * 		b : E F		// eor depth of 1 relative to initial k=3
	 * 		  | G		// eor depth of 2
	 * 		  ;
	 * </pre>
	 * Here, LOOK(3,ref-to(b)) returns eor, but the depths are
	 * {1, 2}; i.e., 3-(3-1) and 3-(3-2).  Those are the lookahead depths
	 * past the rule ref needed for the local follow.
	 */
	public IntervalSet eorDepths;

	public LookaheadSet() {;}
	
	public LookaheadSet(IntervalSet set) { this.set = new IntervalSet(set); }
	
	public static LookaheadSet of(int a) {
		LookaheadSet s = new LookaheadSet();
		s.set = IntervalSet.of(a);
		return s;
	}

	public static LookaheadSet missingDepth(int k) {
		LookaheadSet s = new LookaheadSet();
		s.eorDepths = IntervalSet.of(k);
		return s;
	}

	public void combine(LookaheadSet other) {
		if (eorDepths != null) {
			if (other.eorDepths != null) {
				eorDepths.addAll(other.eorDepths);
			}
		}
		else if (other.eorDepths != null) {
			eorDepths = new IntervalSet(other.eorDepths);
		}
		
		if ( set==null ) set = new IntervalSet(other.set);
		else set.addAll(other.set);
	}

	public LookaheadSet intersection(LookaheadSet s) {
		IntervalSet i = (IntervalSet)this.set.and(s.set);
		return new LookaheadSet(i);
	}

	public boolean isNil() {
		return set.isNil() && (eorDepths==null || eorDepths.size()==0);
	}

	@Override
	public String toString() {
		String s = set.toString();
		if ( eorDepths!=null ) s += "+"+eorDepths;
		return s;
	}

	public String toString(Grammar g) {
		String s = set.toString(g);
		if ( eorDepths!=null ) s += "+"+eorDepths;
		return s;
	}
}
