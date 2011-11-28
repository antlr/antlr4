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

import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RuleContext;

/** A tree structure used to record the semantic context in which
 *  an ATN configuration is valid.  It's either a single predicate,
 *  a conjunction p1&&p2, or a sum of products p1||p2.
 *
 *  I have scoped the AND, OR, and Predicate subclasses of
 *  SemanticContext within the scope of this outer class.
 */
public abstract class SemanticContext {
    public static final SemanticContext NONE = new Predicate();

    public abstract boolean eval(Recognizer<?,?> parser, RuleContext ctx);

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

        public Predicate(Predicate p) {
            this.ruleIndex = p.ruleIndex;
            this.predIndex = p.predIndex;
            this.isCtxDependent = p.isCtxDependent;
		}

        public boolean eval(Recognizer<?,?> parser, RuleContext ctx) {
            return parser.sempred(ctx, ruleIndex, predIndex);
        }

        public String toString() {
            return ruleIndex+":"+predIndex;
        }
    }

	public static class TruePredicate extends Predicate {
		@Override
		public String toString() {
			return "true"; // not used for code gen, just DOT and print outs
		}
	}

	public static class FalsePredicate extends Predicate {
        @Override
		public String toString() {
			return "false"; // not used for code gen, just DOT and print outs
		}
	}

    public static class AND extends SemanticContext {
        public SemanticContext a;
        public SemanticContext b;
        public AND() { }
		public AND(SemanticContext a, SemanticContext b) {
            this.a = a;
            this.b = b;
        }

        public boolean eval(Recognizer<?,?> parser, RuleContext ctx) {
            if ( a == NONE ) return b.eval(parser, ctx);
            if ( b == NONE ) return a.eval(parser, ctx);
            return a.eval(parser, ctx) && b.eval(parser, ctx);
        }

		public String toString() {
            if ( a == NONE ) return b.toString();
            if ( b == NONE ) return a.toString();
            return a+"&&"+b;
        }
    }

    public static class OR extends SemanticContext {
        public SemanticContext a;
        public SemanticContext b;
        public OR() { }
        public OR(SemanticContext a, SemanticContext b) {
            this.a = a;
            this.b = b;
        }

        public boolean eval(Recognizer<?,?> parser, RuleContext ctx) {
            if ( a == NONE ) return b.eval(parser, ctx);
            if ( b == NONE ) return a.eval(parser, ctx);
            return a.eval(parser, ctx) && b.eval(parser, ctx);
        }

        @Override
        public String toString() {
            if ( a == NONE ) return b.toString();
            if ( b == NONE ) return a.toString();
            return a+"||"+b;
        }
    }

//    public static SemanticContext and(AND a, Predicate b) {
//        if ( a == NONE ) return new AND(b);
//        if ( b == NONE ) {a.add(b); return a;}
//        a.add(b);
//        AND and = new SemanticContext.AND();
//        if ( a instanceof AND ) {
//            and.add(b);
//        }
//        else {
//            and.add((Predicate)a);
//            and.add(b);
//        }
//        return and;
//    }
//
//    public static SemanticContext or(SemanticContext a, SemanticContext b) {
//        return null;
//    }
}
