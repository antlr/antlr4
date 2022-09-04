/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.MurmurHash;
import org.antlr.v4.runtime.misc.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/** A tree structure used to record the semantic context in which
 *  an ATN configuration is valid.  It's either a single predicate,
 *  a conjunction {@code p1&&p2}, or a sum of products {@code p1||p2}.
 *
 *  <p>I have scoped the {@link AND}, {@link OR}, and {@link Predicate} subclasses of
 *  {@link SemanticContext} within the scope of this outer class.</p>
 */
public abstract class SemanticContext {
	/**
	 * For context independent predicates, we evaluate them without a local
	 * context (i.e., null context). That way, we can evaluate them without
	 * having to create proper rule-specific context during prediction (as
	 * opposed to the parser, which creates them naturally). In a practical
	 * sense, this avoids a cast exception from RuleContext to myruleContext.
	 *
	 * <p>For context dependent predicates, we must pass in a local context so that
	 * references such as $arg evaluate properly as _localctx.arg. We only
	 * capture context dependent predicates in the context in which we begin
	 * prediction, so we passed in the outer context here in case of context
	 * dependent predicate evaluation.</p>
	 */
    public abstract boolean eval(Recognizer<?,?> parser, RuleContext parserCallStack);

	/**
	 * Evaluate the precedence predicates for the context and reduce the result.
	 *
	 * @param parser The parser instance.
	 * @param parserCallStack
	 * @return The simplified semantic context after precedence predicates are
	 * evaluated, which will be one of the following values.
	 * <ul>
	 * <li>{@link Empty#Instance}: if the predicate simplifies to {@code true} after
	 * precedence predicates are evaluated.</li>
	 * <li>{@code null}: if the predicate simplifies to {@code false} after
	 * precedence predicates are evaluated.</li>
	 * <li>{@code this}: if the semantic context is not changed as a result of
	 * precedence predicate evaluation.</li>
	 * <li>A non-{@code null} {@link SemanticContext}: the new simplified
	 * semantic context after precedence predicates are evaluated.</li>
	 * </ul>
	 */
	public SemanticContext evalPrecedence(Recognizer<?,?> parser, RuleContext parserCallStack) {
		return this;
	}

	public static class Empty extends SemanticContext {
		/**
		 * The default {@link SemanticContext}, which is semantically equivalent to
		 * a predicate of the form {@code {true}?}.
		 */
		public static final Empty Instance = new Empty();

		@Override
		public boolean eval(Recognizer<?, ?> parser, RuleContext parserCallStack) {
			return false;
		}
	}

    public static class Predicate extends SemanticContext {
        public final int ruleIndex;
       	public final int predIndex;
       	public final boolean isCtxDependent;  // e.g., $i ref in pred

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

        @Override
        public boolean eval(Recognizer<?,?> parser, RuleContext parserCallStack) {
            RuleContext localctx = isCtxDependent ? parserCallStack : null;
            return parser.sempred(localctx, ruleIndex, predIndex);
        }

		@Override
		public int hashCode() {
			int hashCode = MurmurHash.initialize();
			hashCode = MurmurHash.update(hashCode, ruleIndex);
			hashCode = MurmurHash.update(hashCode, predIndex);
			hashCode = MurmurHash.update(hashCode, isCtxDependent ? 1 : 0);
			hashCode = MurmurHash.finish(hashCode, 3);
			return hashCode;
		}

		@Override
		public boolean equals(Object obj) {
			if ( !(obj instanceof Predicate) ) return false;
			if ( this == obj ) return true;
			Predicate p = (Predicate)obj;
			return this.ruleIndex == p.ruleIndex &&
				   this.predIndex == p.predIndex &&
				   this.isCtxDependent == p.isCtxDependent;
		}

		@Override
		public String toString() {
            return "{"+ruleIndex+":"+predIndex+"}?";
        }
    }

	public static class PrecedencePredicate extends SemanticContext implements Comparable<PrecedencePredicate> {
		public final int precedence;

		protected PrecedencePredicate() {
			this.precedence = 0;
		}

		public PrecedencePredicate(int precedence) {
			this.precedence = precedence;
		}

		@Override
		public boolean eval(Recognizer<?, ?> parser, RuleContext parserCallStack) {
			return parser.precpred(parserCallStack, precedence);
		}

		@Override
		public SemanticContext evalPrecedence(Recognizer<?, ?> parser, RuleContext parserCallStack) {
			if (parser.precpred(parserCallStack, precedence)) {
				return Empty.Instance;
			}
			else {
				return null;
			}
		}

		@Override
		public int compareTo(PrecedencePredicate o) {
			return precedence - o.precedence;
		}

		@Override
		public int hashCode() {
			int hashCode = 1;
			hashCode = 31 * hashCode + precedence;
			return hashCode;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof PrecedencePredicate)) {
				return false;
			}

			if (this == obj) {
				return true;
			}

			PrecedencePredicate other = (PrecedencePredicate)obj;
			return this.precedence == other.precedence;
		}

		@Override
		// precedence >= _precedenceStack.peek()
		public String toString() {
			return "{"+precedence+">=prec}?";
		}
	}

	/**
	 * This is the base class for semantic context "operators", which operate on
	 * a collection of semantic context "operands".
	 *
	 * @since 4.3
	 */
	public static abstract class Operator extends SemanticContext {
		/**
		 * Gets the operands for the semantic context operator.
		 *
		 * @return a collection of {@link SemanticContext} operands for the
		 * operator.
		 *
		 * @since 4.3
		 */

		public abstract Collection<SemanticContext> getOperands();
	}

	/**
	 * A semantic context which is true whenever none of the contained contexts
	 * is false.
	 */
    public static class AND extends Operator {
		public final SemanticContext[] opnds;

		public AND(SemanticContext a, SemanticContext b) {
			Set<SemanticContext> operands = new HashSet<SemanticContext>();
			if ( a instanceof AND ) operands.addAll(Arrays.asList(((AND)a).opnds));
			else operands.add(a);
			if ( b instanceof AND ) operands.addAll(Arrays.asList(((AND)b).opnds));
			else operands.add(b);

			List<PrecedencePredicate> precedencePredicates = filterPrecedencePredicates(operands);
			if (!precedencePredicates.isEmpty()) {
				// interested in the transition with the lowest precedence
				PrecedencePredicate reduced = Collections.min(precedencePredicates);
				operands.add(reduced);
			}

			opnds = operands.toArray(new SemanticContext[0]);
        }

		@Override
		public Collection<SemanticContext> getOperands() {
			return Arrays.asList(opnds);
		}

		@Override
		public boolean equals(Object obj) {
			if ( this==obj ) return true;
			if ( !(obj instanceof AND) ) return false;
			AND other = (AND)obj;
			return Arrays.equals(this.opnds, other.opnds);
		}

		@Override
		public int hashCode() {
			return MurmurHash.hashCode(opnds, AND.class.hashCode());
		}

		/**
		 * {@inheritDoc}
		 *
		 * <p>
		 * The evaluation of predicates by this context is short-circuiting, but
		 * unordered.</p>
		 */
		@Override
		public boolean eval(Recognizer<?,?> parser, RuleContext parserCallStack) {
			for (SemanticContext opnd : opnds) {
				if ( !opnd.eval(parser, parserCallStack) ) return false;
			}
			return true;
        }

		@Override
		public SemanticContext evalPrecedence(Recognizer<?, ?> parser, RuleContext parserCallStack) {
			boolean differs = false;
			List<SemanticContext> operands = new ArrayList<SemanticContext>();
			for (SemanticContext context : opnds) {
				SemanticContext evaluated = context.evalPrecedence(parser, parserCallStack);
				differs |= (evaluated != context);
				if (evaluated == null) {
					// The AND context is false if any element is false
					return null;
				}
				else if (evaluated != Empty.Instance) {
					// Reduce the result by skipping true elements
					operands.add(evaluated);
				}
			}

			if (!differs) {
				return this;
			}

			if (operands.isEmpty()) {
				// all elements were true, so the AND context is true
				return Empty.Instance;
			}

			SemanticContext result = operands.get(0);
			for (int i = 1; i < operands.size(); i++) {
				result = SemanticContext.and(result, operands.get(i));
			}

			return result;
		}

		@Override
		public String toString() {
			return Utils.join(Arrays.asList(opnds).iterator(), "&&");
        }
    }

	/**
	 * A semantic context which is true whenever at least one of the contained
	 * contexts is true.
	 */
    public static class OR extends Operator {
		public final SemanticContext[] opnds;

		public OR(SemanticContext a, SemanticContext b) {
			Set<SemanticContext> operands = new HashSet<SemanticContext>();
			if ( a instanceof OR ) operands.addAll(Arrays.asList(((OR)a).opnds));
			else operands.add(a);
			if ( b instanceof OR ) operands.addAll(Arrays.asList(((OR)b).opnds));
			else operands.add(b);

			List<PrecedencePredicate> precedencePredicates = filterPrecedencePredicates(operands);
			if (!precedencePredicates.isEmpty()) {
				// interested in the transition with the highest precedence
				PrecedencePredicate reduced = Collections.max(precedencePredicates);
				operands.add(reduced);
			}

			this.opnds = operands.toArray(new SemanticContext[0]);
        }

		@Override
		public Collection<SemanticContext> getOperands() {
			return Arrays.asList(opnds);
		}

		@Override
		public boolean equals(Object obj) {
			if ( this==obj ) return true;
			if ( !(obj instanceof OR) ) return false;
			OR other = (OR)obj;
			return Arrays.equals(this.opnds, other.opnds);
		}

		@Override
		public int hashCode() {
			return MurmurHash.hashCode(opnds, OR.class.hashCode());
		}

		/**
		 * {@inheritDoc}
		 *
		 * <p>
		 * The evaluation of predicates by this context is short-circuiting, but
		 * unordered.</p>
		 */
		@Override
        public boolean eval(Recognizer<?,?> parser, RuleContext parserCallStack) {
			for (SemanticContext opnd : opnds) {
				if ( opnd.eval(parser, parserCallStack) ) return true;
			}
			return false;
        }

		@Override
		public SemanticContext evalPrecedence(Recognizer<?, ?> parser, RuleContext parserCallStack) {
			boolean differs = false;
			List<SemanticContext> operands = new ArrayList<SemanticContext>();
			for (SemanticContext context : opnds) {
				SemanticContext evaluated = context.evalPrecedence(parser, parserCallStack);
				differs |= (evaluated != context);
				if (evaluated == Empty.Instance) {
					// The OR context is true if any element is true
					return Empty.Instance;
				}
				else if (evaluated != null) {
					// Reduce the result by skipping false elements
					operands.add(evaluated);
				}
			}

			if (!differs) {
				return this;
			}

			if (operands.isEmpty()) {
				// all elements were false, so the OR context is false
				return null;
			}

			SemanticContext result = operands.get(0);
			for (int i = 1; i < operands.size(); i++) {
				result = SemanticContext.or(result, operands.get(i));
			}

			return result;
		}

        @Override
        public String toString() {
			return Utils.join(Arrays.asList(opnds).iterator(), "||");
        }
    }

	public static SemanticContext and(SemanticContext a, SemanticContext b) {
		if ( a == null || a == Empty.Instance ) return b;
		if ( b == null || b == Empty.Instance ) return a;
		AND result = new AND(a, b);
		if (result.opnds.length == 1) {
			return result.opnds[0];
		}

		return result;
	}

	/**
	 *
	 *  @see ParserATNSimulator#getPredsForAmbigAlts
	 */
	public static SemanticContext or(SemanticContext a, SemanticContext b) {
		if ( a == null ) return b;
		if ( b == null ) return a;
		if ( a == Empty.Instance || b == Empty.Instance ) return Empty.Instance;
		OR result = new OR(a, b);
		if (result.opnds.length == 1) {
			return result.opnds[0];
		}

		return result;
	}

	private static List<PrecedencePredicate> filterPrecedencePredicates(Collection<? extends SemanticContext> collection) {
		ArrayList<PrecedencePredicate> result = null;
		for (Iterator<? extends SemanticContext> iterator = collection.iterator(); iterator.hasNext(); ) {
			SemanticContext context = iterator.next();
			if (context instanceof PrecedencePredicate) {
				if (result == null) {
					result = new ArrayList<PrecedencePredicate>();
				}

				result.add((PrecedencePredicate)context);
				iterator.remove();
			}
		}

		if (result == null) {
			return Collections.emptyList();
		}

		return result;
	}
}
