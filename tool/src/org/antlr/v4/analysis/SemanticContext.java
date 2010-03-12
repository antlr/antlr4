package org.antlr.v4.analysis;

import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.tool.GrammarAST;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/** A tree structure used to record the semantic context in which
 *  an NFA configuration is valid.  It's either a single predicate or
 *  a tree representing an operation tree such as: p1&&p2 or p1||p2.
 *
 *  For NFA o-p1->o-p2->o, create tree AND(p1,p2).
 *  For NFA (1)-p1->(2)
 *           |       ^
 *           |       |
 *          (3)-p2----
 *  we will have to combine p1 and p2 into DFA state as we will be
 *  adding NFA configurations for state 2 with two predicates p1,p2.
 *  So, set context for combined NFA config for state 2: OR(p1,p2).
 */
public abstract class SemanticContext {
	/** Create a default value for the semantic context shared among all
	 *  NFAConfigurations that do not have an actual semantic context.
	 *  This prevents lots of if!=null type checks all over; it represents
	 *  just an empty set of predicates.
	 */
	public static final SemanticContext EMPTY_SEMANTIC_CONTEXT = new Predicate();

	/** Given a semantic context expression tree, return a tree with all
	 *  nongated predicates set to true and then reduced.  So p&&(q||r) would
	 *  return p&&r if q is nongated but p and r are gated.
	 */
	public abstract SemanticContext getGatedPredicateContext();

	public abstract boolean isSyntacticPredicate();

	public static class Predicate extends SemanticContext {
		/** The AST node in tree created from the grammar holding the predicate */
		public GrammarAST predicateAST;

		/** Is this a {...}?=> gating predicate or a normal disambiguating {..}?
		 *  If any predicate in expression is gated, then expression is considered
		 *  gated.
		 *
		 *  The simple Predicate object's predicate AST's type is used to set
		 *  gated to true if type==GATED_SEMPRED.
		 */
		protected boolean gated = false;

		/** syntactic predicates are converted to semantic predicates
		 *  but synpreds are generated slightly differently.
		 */
		protected boolean synpred = false;

		public static final int INVALID_PRED_VALUE = -1;
		public static final int TRUE_PRED = 1;

		/** sometimes predicates are known to be true or false; we need
		 *  a way to represent this without resorting to a target language
		 *  value like true or TRUE.
		 */
		protected int constantValue = INVALID_PRED_VALUE;

		public Predicate() {
			this.gated=false;
		}

		public Predicate(GrammarAST predicate) {
			this.predicateAST = predicate;
			this.gated =
				predicate.getType()== ANTLRParser.GATED_SEMPRED ||
				predicate.getType()==ANTLRParser.SYN_SEMPRED ;
			this.synpred =
				predicate.getType()==ANTLRParser.SYN_SEMPRED ||
				predicate.getType()== ANTLRParser.BACKTRACK_SEMPRED;
		}

		public Predicate(Predicate p) {
			this.predicateAST = p.predicateAST;
			this.gated = p.gated;
			this.synpred = p.synpred;
			this.constantValue = p.constantValue;
		}

		/** Two predicates are the same if they are literally the same
		 *  text rather than same node in the grammar's AST.
		 */
		public boolean equals(Object o) {
			if ( !(o instanceof Predicate) ) return false;
			Predicate p = (Predicate) o;
			if ( predicateAST!=null && p.predicateAST!=null )
				return predicateAST.getText().equals(p.predicateAST.getText());
			return predicateAST==null && p.predicateAST==null;
		}

		public int hashCode() {
			if ( predicateAST ==null ) {
				return 0;
			}
			return predicateAST.getText().hashCode();
		}

		public SemanticContext getGatedPredicateContext() {
			if ( gated ) {
				return this;
			}
			return null;
		}

		public boolean isSyntacticPredicate() {
			return predicateAST !=null &&
				( predicateAST.getType()==ANTLRParser.SYN_SEMPRED ||
				  predicateAST.getType()==ANTLRParser.BACKTRACK_SEMPRED );
		}

		public String toString() {
			if ( predicateAST ==null ) {
				return "<nopred>";
			}
			return predicateAST.getText();
		}
	}

	public static class TruePredicate extends Predicate {
		public TruePredicate() {
			super();
			this.constantValue = TRUE_PRED;
		}

		public String toString() {
			return "true"; // not used for code gen, just DOT and print outs
		}
	}

	public static class AND extends SemanticContext {
		protected SemanticContext left,right;
		public AND(SemanticContext a, SemanticContext b) {
			this.left = a;
			this.right = b;
		}
		public SemanticContext getGatedPredicateContext() {
			SemanticContext gatedLeft = left.getGatedPredicateContext();
			SemanticContext gatedRight = right.getGatedPredicateContext();
			if ( gatedLeft==null ) {
				return gatedRight;
			}
			if ( gatedRight==null ) {
				return gatedLeft;
			}
			return new AND(gatedLeft, gatedRight);
		}
		public boolean isSyntacticPredicate() {
			return left.isSyntacticPredicate()||right.isSyntacticPredicate();
		}
		public String toString() {
			return "("+left+"&&"+right+")";
		}
	}

	public static class OR extends SemanticContext {
		protected Set<SemanticContext> operands;
		public OR(SemanticContext a, SemanticContext b) {
			operands = new HashSet<SemanticContext>();
			if ( a instanceof OR ) {
				operands.addAll(((OR)a).operands);
			}
			else if ( a!=null ) {
				operands.add(a);
			}
			if ( b instanceof OR ) {
				operands.addAll(((OR)b).operands);
			}
			else if ( b!=null ) {
				operands.add(b);
			}
		}
		public SemanticContext getGatedPredicateContext() {
			SemanticContext result = null;
			for (Iterator it = operands.iterator(); it.hasNext();) {
				SemanticContext semctx = (SemanticContext) it.next();
				SemanticContext gatedPred = semctx.getGatedPredicateContext();
				if ( gatedPred!=null ) {
					result = or(result, gatedPred);
					// result = new OR(result, gatedPred);
				}
			}
			return result;
		}
		public boolean isSyntacticPredicate() {
			for (Iterator it = operands.iterator(); it.hasNext();) {
				SemanticContext semctx = (SemanticContext) it.next();
				if ( semctx.isSyntacticPredicate() ) {
					return true;
				}
			}
			return false;
		}
		public String toString() {
			StringBuffer buf = new StringBuffer();
			buf.append("(");
			int i = 0;
			for (Iterator it = operands.iterator(); it.hasNext();) {
				SemanticContext semctx = (SemanticContext) it.next();
				if ( i>0 ) {
					buf.append("||");
				}
				buf.append(semctx.toString());
				i++;
			}
			buf.append(")");
			return buf.toString();
		}
	}

	public static class NOT extends SemanticContext {
		protected SemanticContext ctx;
		public NOT(SemanticContext ctx) {
			this.ctx = ctx;
		}
		public SemanticContext getGatedPredicateContext() {
			SemanticContext p = ctx.getGatedPredicateContext();
			if ( p==null ) {
				return null;
			}
			return new NOT(p);
		}
		public boolean isSyntacticPredicate() {
			return ctx.isSyntacticPredicate();
		}
		public boolean equals(Object object) {
			if ( !(object instanceof NOT) ) {
				return false;
			}
			return this.ctx.equals(((NOT)object).ctx);
		}

		public String toString() {
			return "!("+ctx+")";
		}
	}

	public static SemanticContext and(SemanticContext a, SemanticContext b) {
		//System.out.println("AND: "+a+"&&"+b);
		if ( a==EMPTY_SEMANTIC_CONTEXT || a==null ) {
			return b;
		}
		if ( b==EMPTY_SEMANTIC_CONTEXT || b==null ) {
			return a;
		}
		if ( a.equals(b) ) {
			return a; // if same, just return left one
		}
		//System.out.println("## have to AND");
		return new AND(a,b);
	}

	public static SemanticContext or(SemanticContext a, SemanticContext b) {
		//System.out.println("OR: "+a+"||"+b);
		if ( a==EMPTY_SEMANTIC_CONTEXT || a==null ) {
			return b;
		}
		if ( b==EMPTY_SEMANTIC_CONTEXT || b==null ) {
			return a;
		}
		if ( a instanceof TruePredicate ) {
			return a;
		}
		if ( b instanceof TruePredicate ) {
			return b;
		}
		if ( a instanceof NOT && b instanceof Predicate ) {
			NOT n = (NOT)a;
			// check for !p||p
			if ( n.ctx.equals(b) ) {
				return new TruePredicate();
			}
		}
		else if ( b instanceof NOT && a instanceof Predicate ) {
			NOT n = (NOT)b;
			// check for p||!p
			if ( n.ctx.equals(a) ) {
				return new TruePredicate();
			}
		}
		else if ( a.equals(b) ) {
			return a;
		}
		//System.out.println("## have to OR");
		return new OR(a,b);
	}

	public static SemanticContext not(SemanticContext a) {
		return new NOT(a);
	}

}
