/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Terence Parr
 *  Copyright (c) 2016 Sam Harwell
 *  Copyright (c) 2017 Egbert Voigt
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

module antlr.v4.runtime.atn.SemanticContext;

import std.conv;
import antlr.v4.runtime.RuleContext;
import antlr.v4.runtime.InterfaceRecognizer;
import antlr.v4.runtime.atn.AND;
import antlr.v4.runtime.atn.OR;
import antlr.v4.runtime.misc.MurmurHash;


/**
 * @uml
 * A tree structure used to record the semantic context in which
 * an ATN configuration is valid.  It's either a single predicate,
 * a conjunction {@code p1&&p2}, or a sum of products {@code p1||p2}.
 *
 * <p>I have scoped the {@link AND}, {@link OR}, and {@link Predicate} subclasses of
 * {@link SemanticContext} within the scope of this outer class.</p>
 */
class SemanticContext
{
    public bool eval(InterfaceRecognizer parser, RuleContext parserCallStack)
    {
        return true;
    }
    /**
     * @uml
     * The default {@link SemanticContext}, which is semantically equivalent to
     * a predicate of the form {@code {true}?}.
     */
    public static SemanticContext NONE;


    /**
     * @uml
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
    //abstract public bool eval(InterfaceRecognizer parser, RuleContext parserCallStack);

    /**
     * @uml
     * Evaluate the precedence predicates for the context and reduce the result.
     *
     *  @param parser The parser instance.
     *  @param parserCallStack
     *  @return The simplified semantic context after precedence predicates are
     *  evaluated, which will be one of the following values.
     * <ul>
     *  <li>{@link #NONE}: if the predicate simplifies to {@code true} after
     *  precedence predicates are evaluated.</li>
     *  <li>{@code null}: if the predicate simplifies to {@code false} after
     *  precedence predicates are evaluated.</li>
     *  <li>{@code this}: if the semantic context is not changed as a result of
     *  precedence predicate evaluation.</li>
     *  <li>A non-{@code null} {@link SemanticContext}: the new simplified
     *  semantic context after precedence predicates are evaluated.</li>
     * </ul>
     */
    public SemanticContext evalPrecedence(InterfaceRecognizer parser, RuleContext parserCallStack)
    {
        return this;
    }
    // Class Predicate
    /**
     * TODO add class description
     */
    class Predicate : SemanticContext
    {
        /**
         * The single instance of LexerMoreAction.
         */
        private static __gshared Predicate instance_;

        public int ruleIndex;

        public int predIndex;

        /**
         * @uml
         * e.g., $i ref in pred
         */
        public bool isCtxDependent;

        public this()
        {
            this.ruleIndex = -1;
            this.predIndex = -1;
            this.isCtxDependent = false;
        }

        public this(int ruleIndex, int predIndex, bool isCtxDependent)
        {
            this.ruleIndex = ruleIndex;
            this.predIndex = predIndex;
            this.isCtxDependent = isCtxDependent;
        }

        /**
         * @uml
         * @override
         */
        public override bool eval(InterfaceRecognizer parser, RuleContext parserCallStack)
        {
            RuleContext localctx = isCtxDependent ? parserCallStack : null;
            return parser.sempred(localctx, ruleIndex, predIndex);
        }

        /**
         * @uml
         * @override
         * @safe
         * @nothrow
         */
        public override size_t toHash() @safe nothrow
        {
            size_t hashCode = MurmurHash.initialize();
            hashCode = MurmurHash.update(hashCode, ruleIndex);
            hashCode = MurmurHash.update(hashCode, predIndex);
            hashCode = MurmurHash.update(hashCode, isCtxDependent ? 1 : 0);
            hashCode = MurmurHash.finish(hashCode, 3);
            return hashCode;
        }

        /**
         * @uml
         * @override
         */
        public override bool opEquals(Object obj)
        {
            if (typeid(typeof(obj)) != typeid(Predicate*)) return false;
            if ( this is obj ) return true;
            Predicate p = cast(Predicate)obj;
            return this.ruleIndex == p.ruleIndex &&
                this.predIndex == p.predIndex &&
                this.isCtxDependent == p.isCtxDependent;
        }

        /**
         * @uml
         * @override
         */
        public override string toString()
        {
            return "{" ~ to!string(ruleIndex) ~ ":" ~ to!string(predIndex) ~ "}?";
        }

        // /**
        //  * Creates the single instance of Predicate.
        //  */
        // private shared static this()
        // {
        //     instance_ = new Predicate;
        // }

        // /**
        //  * Returns: A single instance of LexerMoreAction.
        //  */
        // public static Predicate instance()
        // {
        //     return instance_;
        // }

    }
    // Class PrecedencePredicate
    /**
     * TODO add class description
     */
    class PrecedencePredicate : SemanticContext
    {

        /**
         * @uml
         * @final
         */
        public int precedence;

        protected this()
        {
            this.precedence = 0;
        }

        public this(int precedence)
        {
            this.precedence = precedence;
        }

        /**
         * @uml
         * @override
         */
        public override bool eval(InterfaceRecognizer parser, RuleContext parserCallStack)
        {
            return parser.precpred(parserCallStack, precedence);
        }

        /**
         * @uml
         * @override
         */
        public override SemanticContext evalPrecedence(InterfaceRecognizer parser, RuleContext parserCallStack)
        {
            if (parser.precpred(parserCallStack, precedence)) {
                if (!SemanticContext.NONE)
                    SemanticContext.NONE = new Predicate;
                return SemanticContext.NONE;
            }
            else {
                return null;
            }
        }

        /**
         * @uml
         * @override
         */
        public override int opCmp(Object o)
        {
            return precedence - (cast(PrecedencePredicate)o).precedence;
        }

        /**
         * @uml
         * @override
         * @safe
         * @nothrow
         */
        public override size_t toHash() @safe nothrow
        {
            int hashCode = 1;
            hashCode = 31 * hashCode + precedence;
            return hashCode;
        }

        /**
         * @uml
         * @override
         */
        public override bool opEquals(Object obj)
        {
            if (! cast(PrecedencePredicate)obj) {
                return false;
            }

            if (this is obj) {
                return true;
            }

            PrecedencePredicate other = cast(PrecedencePredicate)obj;
            return this.precedence == other.precedence;
        }

        /**
         * @uml
         * @override
         * precedence >= _precedenceStack.peek()
         */
        public override string toString()
        {
            return "{" ~ to!string(precedence) ~ ">=prec}?";
        }

    }

    /**
     * @uml
     * @safe
     * @nothrow
     * @override
     */
    public override size_t toHash() @safe nothrow
    {
        return 1;
    }

    public static SemanticContext and(SemanticContext a, SemanticContext b)
    {
        if (a is null || a == NONE )
            return b;
        if (b is null || b == NONE )
            return a;
        AND result = new AND(a, b);
        if (result.opnds.length == 1) {
            return result.opnds[0];
        }
        return result;
    }

    public static SemanticContext or(SemanticContext a, SemanticContext b)
    {
        if (a is null )
            return b;
        if (b is null )
            return a;
        if (a == NONE || b == NONE ) return NONE;
        OR result = new OR(a, b);
        if (result.opnds.length == 1) {
            return result.opnds[0];
        }
        return result;
    }

    public PrecedencePredicate[] filterPrecedencePredicates(SemanticContext[] collection)
    {
        PrecedencePredicate[] result;
        foreach (context; collection) {
            if (cast(PrecedencePredicate)context) {
                result ~= cast(PrecedencePredicate)context;
            }
        }
        return result;
    }

}
