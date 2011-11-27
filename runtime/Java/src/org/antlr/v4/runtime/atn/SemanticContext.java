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

package org.antlr.v4.runtime.atn;

import java.util.*;

/** A binary tree structure used to record the semantic context in which
 *  an NFA configuration is valid.  It's either a single predicate or
 *  a tree representing an operation tree such as: p1&&p2 or p1||p2.
 *
 *  For ATN o-p1->o-p2->o, create tree AND(p1,p2).
 *  For ATN (1)-p1->(2)
 *           |       ^
 *           |       |
 *          (3)-p2----
 *  we will have to combine p1 and p2 into DFA state as we will be
 *  adding NFA configurations for state 2 with two predicates p1,p2.
 *  So, set context for combined NFA config for state 2: OR(p1,p2).
 *
 *  I have scoped the AND, NOT, OR, and Predicate subclasses of
 *  SemanticContext within the scope of this outer class.
 *
 *  Pulled from v3; lots of fixes by Sam Harwell in v3 I notice.
 */
public abstract class SemanticContext {
	/** Create a default value for the semantic context shared among all
	 *  ATNConfigs that do not have an actual semantic context.
	 *  This prevents lots of if!=null type checks all over; it represents
	 *  just an empty set of predicates.
	 */
	public static final SemanticContext EMPTY_SEMANTIC_CONTEXT = new Predicate();

	public static class Predicate extends SemanticContext {
        public final int ruleIndex;
       	public final int predIndex;
       	public final boolean isCtxDependent;  // e.g., $i ref in pred

//		public static final int INVALID_PRED_VALUE = -2;
//		public static final int FALSE_PRED = 0;
//		public static final int TRUE_PRED = ~0;

		/** sometimes predicates are known to be true or false; we need
		 *  a way to represent this without resorting to a target language
		 *  value like true or TRUE.
		 */
//		protected int constantValue = INVALID_PRED_VALUE;

        protected Predicate() {
            this.ruleIndex = -1;
            this.predIndex = -1;
            this.isCtxDependent = false;
        }

        public Predicate(int ruleIndex, int predIndex, boolean isCtxDependent) {
            this.ruleIndex = ruleIndex;
            this.predIndex = predIndex;
            this.isCtxDependent = isCtxDependent;
        }

        public Predicate(Predicate p) {
            this.ruleIndex = p.ruleIndex;
            this.predIndex = p.predIndex;
            this.isCtxDependent = p.isCtxDependent;
		}

		public boolean equals(Object o) {
			if ( !(o instanceof Predicate) ) {
				return false;
			}

            if ( this != o ) return false;

            Predicate other = (Predicate)o;
            return this.ruleIndex == other.ruleIndex &&
                   this.predIndex == other.predIndex;
        }

        public int hashCode() {
            return ruleIndex+predIndex;
		}
        public String toString() {
            return ruleIndex+":"+predIndex;
        }
    }

	public static class TruePredicate extends Predicate {
        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object o) {
            return this == o || o instanceof TruePredicate;
        }
		@Override
		public String toString() {
			return "true"; // not used for code gen, just DOT and print outs
		}
	}

	public static class FalsePredicate extends Predicate {
        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object o) {
            return this == o || o instanceof FalsePredicate;
        }

        @Override
		public String toString() {
			return "false"; // not used for code gen, just DOT and print outs
		}
	}

	public static abstract class CommutativePredicate extends SemanticContext {
		protected final Set<SemanticContext> operands = new HashSet<SemanticContext>();
		protected int hashCode;

		public CommutativePredicate(SemanticContext a, SemanticContext b) {
			if (a.getClass() == this.getClass()){
				CommutativePredicate predicate = (CommutativePredicate)a;
				operands.addAll(predicate.operands);
			}
            else {
				operands.add(a);
			}

			if (b.getClass() == this.getClass()){
				CommutativePredicate predicate = (CommutativePredicate)b;
				operands.addAll(predicate.operands);
			}
            else {
				operands.add(b);
			}

			hashCode = calculateHashCode();
		}

		public CommutativePredicate(HashSet<SemanticContext> contexts){
			for (SemanticContext context : contexts){
				if (context.getClass() == this.getClass()){
					CommutativePredicate predicate = (CommutativePredicate)context;
					operands.addAll(predicate.operands);
				}
                else {
					operands.add(context);
				}
			}

			hashCode = calculateHashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;

			if (obj.getClass() == this.getClass()) {
				CommutativePredicate commutative = (CommutativePredicate)obj;
				Set<SemanticContext> otherOperands = commutative.operands;
				if (operands.size() != otherOperands.size())
					return false;

				return operands.containsAll(otherOperands);
			}

			if (obj instanceof NOT)	{
				NOT not = (NOT)obj;
				if (not.ctx instanceof CommutativePredicate && not.ctx.getClass() != this.getClass()) {
					Set<SemanticContext> otherOperands = ((CommutativePredicate)not.ctx).operands;
					if (operands.size() != otherOperands.size())
						return false;

					ArrayList<SemanticContext> temp = new ArrayList<SemanticContext>(operands.size());
					for (SemanticContext context : otherOperands) {
						temp.add(not(context));
					}

					return operands.containsAll(temp);
				}
			}

			return false;
		}

		@Override
		public int hashCode(){
			return hashCode;
		}

		@Override
		public String toString() {
			StringBuffer buf = new StringBuffer();
			buf.append("(");
			int i = 0;
			for (SemanticContext semctx : operands) {
				if ( i>0 ) {
					buf.append(getOperandString());
				}
				buf.append(semctx.toString());
				i++;
			}
			buf.append(")");
			return buf.toString();
		}

		public abstract String getOperandString();

        public abstract SemanticContext combinePredicates(SemanticContext left, SemanticContext right);

        public int calculateHashCode() {
            int hashcode = 0;
            for (SemanticContext context : operands) {
                hashcode = hashcode ^ context.hashCode();
            }

            return hashcode;
        }
    }

    public static class AND extends CommutativePredicate {
		public AND(SemanticContext a, SemanticContext b) {
			super(a,b);
		}

		public AND(HashSet<SemanticContext> contexts) {
			super(contexts);
		}

		@Override
		public String getOperandString() {
			return "&&";
		}

		@Override
		public SemanticContext combinePredicates(SemanticContext left, SemanticContext right) {
			return SemanticContext.and(left, right);
		}

//		@Override
//		public int calculateHashCode() {
//			int hashcode = 0;
//			for (SemanticContext context : operands) {
//				hashcode = hashcode ^ context.hashCode();
//			}
//
//			return hashcode;
//		}
	}

	public static class OR extends CommutativePredicate {
		public OR(SemanticContext a, SemanticContext b) {
			super(a,b);
		}

		public OR(HashSet<SemanticContext> contexts) {
			super(contexts);
		}

		@Override
		public String getOperandString() {
			return "||";
		}

		@Override
		public SemanticContext combinePredicates(SemanticContext left, SemanticContext right) {
			return SemanticContext.or(left, right);
		}
//
//		@Override
//		public int calculateHashCode() {
//			int hashcode = 0;
//			for (SemanticContext context : operands) {
//				hashcode = ~hashcode ^ context.hashCode();
//			}
//
//			return hashcode;
//		}
	}

	public static class NOT extends SemanticContext {
		protected SemanticContext ctx;
		public NOT(SemanticContext ctx) {
			this.ctx = ctx;
		}

		@Override
		public boolean equals(Object object) {
			if ( !(object instanceof NOT) ) {
				return false;
			}
			return this.ctx.equals(((NOT)object).ctx);
		}

		@Override
		public int hashCode() {
			return ~ctx.hashCode();
		}

		@Override
		public String toString() {
			return "!("+ctx+")";
		}
	}

	public static SemanticContext and(SemanticContext a, SemanticContext b) {
		//System.out.println("AND: "+a+"&&"+b);
		if (a instanceof FalsePredicate || b instanceof FalsePredicate)
			return new FalsePredicate();

		SemanticContext[] terms = factorOr(a, b);
		SemanticContext commonTerms = terms[0];
		a = terms[1];
		b = terms[2];

		boolean factored = commonTerms != null && commonTerms != EMPTY_SEMANTIC_CONTEXT && !(commonTerms instanceof TruePredicate);
		if (factored) {
			return or(commonTerms, and(a, b));
		}

		//System.Console.Out.WriteLine( "AND: " + a + "&&" + b );
		if (a instanceof FalsePredicate || b instanceof FalsePredicate)
			return new FalsePredicate();

		if ( a==EMPTY_SEMANTIC_CONTEXT || a==null ) {
			return b;
		}
		if ( b==EMPTY_SEMANTIC_CONTEXT || b==null ) {
			return a;
		}

		if (a instanceof TruePredicate)
			return b;

		if (b instanceof TruePredicate)
			return a;

		//// Factoring takes care of this case
		//if (a.Equals(b))
		//    return a;

		//System.out.println("## have to AND");
		return new AND(a,b);
	}

	public static SemanticContext or(SemanticContext a, SemanticContext b) {
		//System.out.println("OR: "+a+"||"+b);
		if (a instanceof TruePredicate || b instanceof TruePredicate)
			return new TruePredicate();

		SemanticContext[] terms = factorAnd(a, b);
		SemanticContext commonTerms = terms[0];
		a = terms[1];
		b = terms[2];
		boolean factored = commonTerms != null && commonTerms != EMPTY_SEMANTIC_CONTEXT && !(commonTerms instanceof FalsePredicate);
		if (factored) {
			return and(commonTerms, or(a, b));
		}

		if ( a==EMPTY_SEMANTIC_CONTEXT || a==null || a instanceof FalsePredicate ) {
			return b;
		}

		if ( b==EMPTY_SEMANTIC_CONTEXT || b==null || b instanceof FalsePredicate ) {
			return a;
		}

		if ( a instanceof TruePredicate || b instanceof TruePredicate || commonTerms instanceof TruePredicate ) {
			return new TruePredicate();
		}

		//// Factoring takes care of this case
		//if (a.equals(b))
		//    return a;

		if ( a instanceof NOT ) {
			NOT n = (NOT)a;
			// check for !p||p
			if ( n.ctx.equals(b) ) {
				return new TruePredicate();
			}
		}
		else if ( b instanceof NOT ) {
			NOT n = (NOT)b;
			// check for p||!p
			if ( n.ctx.equals(a) ) {
				return new TruePredicate();
			}
		}

		//System.out.println("## have to OR");
		OR result = new OR(a,b);
		if (result.operands.size() == 1)
			return result.operands.iterator().next();

		return result;
	}

	public static SemanticContext not(SemanticContext a) {
		if (a instanceof NOT) return ((NOT)a).ctx;

		if (a instanceof TruePredicate) return new FalsePredicate();
		else if (a instanceof FalsePredicate) return new TruePredicate();

		return new NOT(a);
	}

    // Factor so (a && b) == (result && a && b)
    public static SemanticContext[] factorAnd(SemanticContext a, SemanticContext b)	{
        if (a == EMPTY_SEMANTIC_CONTEXT || a == null || a instanceof FalsePredicate) {
            return new SemanticContext[] { EMPTY_SEMANTIC_CONTEXT, a, b };
        }
        if (b == EMPTY_SEMANTIC_CONTEXT || b == null || b instanceof FalsePredicate) {
            return new SemanticContext[] { EMPTY_SEMANTIC_CONTEXT, a, b };
        }

        if (a instanceof TruePredicate || b instanceof TruePredicate) {
			return new SemanticContext[] {
                new TruePredicate(), EMPTY_SEMANTIC_CONTEXT, EMPTY_SEMANTIC_CONTEXT
            };
		}

		HashSet<SemanticContext> opsA = new HashSet<SemanticContext>(getAndOperands(a));
		HashSet<SemanticContext> opsB = new HashSet<SemanticContext>(getAndOperands(b));

		HashSet<SemanticContext> result = new HashSet<SemanticContext>(opsA);
		result.retainAll(opsB);
		if (result.size() == 0) {
			return new SemanticContext[] { EMPTY_SEMANTIC_CONTEXT, a, b };
        }

		opsA.removeAll(result);
		if (opsA.size() == 0) a = new TruePredicate();
		else if (opsA.size() == 1) a = opsA.iterator().next();
		else a = new AND(opsA);

		opsB.removeAll(result);
		if (opsB.size() == 0) b = new TruePredicate();
		else if (opsB.size() == 1) b = opsB.iterator().next();
		else b = new AND(opsB);

		if (result.size() == 1) {
			return new SemanticContext[] { result.iterator().next(), a, b };
        }

		return new SemanticContext[] { new AND(result), a, b };
	}

	// Factor so (a || b) == (result || a || b)
	public static SemanticContext[] factorOr(SemanticContext a, SemanticContext b) {
		HashSet<SemanticContext> opsA = new HashSet<SemanticContext>(getOrOperands(a));
		HashSet<SemanticContext> opsB = new HashSet<SemanticContext>(getOrOperands(b));

		HashSet<SemanticContext> result = new HashSet<SemanticContext>(opsA);
		result.retainAll(opsB);
		if (result.size() == 0) {
			return new SemanticContext[] { EMPTY_SEMANTIC_CONTEXT, a, b };
        }

		opsA.removeAll(result);
		if (opsA.size() == 0) a = new FalsePredicate();
		else if (opsA.size() == 1) a = opsA.iterator().next();
		else a = new OR(opsA);

		opsB.removeAll(result);
		if (opsB.size() == 0) b = new FalsePredicate();
		else if (opsB.size() == 1) b = opsB.iterator().next();
		else b = new OR(opsB);

		if (result.size() == 1) {
			return new SemanticContext[] { result.iterator().next(), a, b };
        }

		return new SemanticContext[] { new OR(result), a, b };
	}

	public static Collection<SemanticContext> getAndOperands(SemanticContext context) {
		if (context instanceof AND)	return ((AND)context).operands;

		if (context instanceof NOT) {
			Collection<SemanticContext> operands = getOrOperands(((NOT)context).ctx);
			List<SemanticContext> result = new ArrayList<SemanticContext>(operands.size());
			for (SemanticContext operand : operands) {
				result.add(not(operand));
			}
			return result;
		}

		ArrayList<SemanticContext> result = new ArrayList<SemanticContext>();
		result.add(context);
		return result;
	}

	public static Collection<SemanticContext> getOrOperands(SemanticContext context) {
		if (context instanceof OR) return ((OR)context).operands;

		if (context instanceof NOT) {
			Collection<SemanticContext> operands = getAndOperands(((NOT)context).ctx);
			List<SemanticContext> result = new ArrayList<SemanticContext>(operands.size());
			for (SemanticContext operand : operands) {
				result.add(not(operand));
			}
			return result;
		}

		ArrayList<SemanticContext> result = new ArrayList<SemanticContext>();
		result.add(context);
		return result;
	}
}
