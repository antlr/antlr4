/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
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
using System.Globalization;
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
    /// <summary>A semantic predicate failed during validation.</summary>
    /// <remarks>
    /// A semantic predicate failed during validation.  Validation of predicates
    /// occurs when normally parsing the alternative just like matching a token.
    /// Disambiguating predicate evaluation occurs when we test a predicate during
    /// prediction.
    /// </remarks>
    [System.Serializable]
    public class FailedPredicateException : RecognitionException
    {
        private const long serialVersionUID = 5379330841495778709L;

        private readonly int ruleIndex;

        private readonly int predicateIndex;

        private readonly string predicate;

        public FailedPredicateException(Parser recognizer)
            : this(recognizer, null)
        {
        }

        public FailedPredicateException(Parser recognizer, string predicate)
            : this(recognizer, predicate, null)
        {
        }

        public FailedPredicateException(Parser recognizer, string predicate, string message)
			: base(FormatMessage(predicate, message), recognizer, ((ITokenStream)recognizer.InputStream), recognizer.RuleContext)
        {
            ATNState s = recognizer.Interpreter.atn.states[recognizer.State];
            AbstractPredicateTransition trans = (AbstractPredicateTransition)s.Transition(0);
            if (trans is PredicateTransition)
            {
                this.ruleIndex = ((PredicateTransition)trans).ruleIndex;
                this.predicateIndex = ((PredicateTransition)trans).predIndex;
            }
            else
            {
                this.ruleIndex = 0;
                this.predicateIndex = 0;
            }
            this.predicate = predicate;
            this.OffendingToken = recognizer.CurrentToken;
        }

        public virtual int RuleIndex
        {
            get
            {
                return ruleIndex;
            }
        }

        public virtual int PredIndex
        {
            get
            {
                return predicateIndex;
            }
        }

        [Nullable]
        public virtual string Predicate
        {
            get
            {
                return predicate;
            }
        }

        [return: NotNull]
        private static string FormatMessage(string predicate, string message)
        {
            if (message != null)
            {
                return message;
            }
            return string.Format(CultureInfo.CurrentCulture, "failed predicate: {{{0}}}?", predicate);
        }
    }
}
