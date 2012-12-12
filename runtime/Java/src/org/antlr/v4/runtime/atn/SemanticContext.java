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

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.NotNull;
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
 *  a conjunction p1&&p2, or a sum of products p1||p2.
 *
 *  I have scoped the AND, OR, and Predicate subclasses of
 *  SemanticContext within the scope of this outer class.
 */
public abstract class SemanticContext {
    public static final SemanticContext NONE = new Predicate();

	public SemanticContext parent;

    /**
     For context independent predicates, we evaluate them without a local
     context (i.e., null context). That way, we can evaluate them without having to create
     proper rule-specific context during prediction (as opposed to the parser,
     which creates them naturally). In a practical sense, this avoids a cast exception
     from RuleContext to myruleContext.

     For context dependent predicates, we must pass in a local context so that
     references such as $arg evaluate properly as _localctx.arg. We only capture
     context dependent predicates in the context in which we begin prediction,
     so we passed in the outer context here in case of context dependent predicate
     evaluation.
    */
    public abstract <T> boolean eval(Recognizer<T, ?> parser, RuleContext<T> outerContext);

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
        public <T> boolean eval(Recognizer<T, ?> parser, RuleContext<T> outerContext) {
            RuleContext<T> localctx = isCtxDependent ? outerContext : null;
            return parser.sempred(localctx, ruleIndex, predIndex);
        }

		@Override
		public int hashCode() {
			int hashCode = 1;
			hashCode = 31 * hashCode + ruleIndex;
			hashCode = 31 * hashCode + predIndex;
			hashCode = 31 * hashCode + (isCtxDependent ? 1 : 0);
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
		public <T> boolean eval(Recognizer<T, ?> parser, RuleContext<T> outerContext) {
			return parser.precpred(outerContext, precedence);
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
		public String toString() {
			return super.toString();
		}
	}

    public static class AND extends SemanticContext {
		@NotNull public final SemanticContext[] opnds;

		public AND(@NotNull SemanticContext a, @NotNull SemanticContext b) {
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

			opnds = operands.toArray(new SemanticContext[operands.size()]);
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
			return Arrays.hashCode(opnds);
		}

		@Override
		public <T> boolean eval(Recognizer<T, ?> parser, RuleContext<T> outerContext) {
			for (SemanticContext opnd : opnds) {
				if ( !opnd.eval(parser, outerContext) ) return false;
			}
			return true;
        }

		@Override
		public String toString() {
			return Utils.join(opnds, "&&");
        }
    }

    public static class OR extends SemanticContext {
		@NotNull public final SemanticContext[] opnds;

		public OR(@NotNull SemanticContext a, @NotNull SemanticContext b) {
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

			this.opnds = operands.toArray(new SemanticContext[operands.size()]);
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
			return Arrays.hashCode(opnds) + 1; // differ from AND slightly
		}

		@Override
        public <T> boolean eval(Recognizer<T, ?> parser, RuleContext<T> outerContext) {
			for (SemanticContext opnd : opnds) {
				if ( opnd.eval(parser, outerContext) ) return true;
			}
			return false;
        }

        @Override
        public String toString() {
			return Utils.join(opnds, "||");
        }
    }

	public static SemanticContext and(SemanticContext a, SemanticContext b) {
		if ( a == null || a == NONE ) return b;
		if ( b == null || b == NONE ) return a;
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
		if ( a == NONE || b == NONE ) return NONE;
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
