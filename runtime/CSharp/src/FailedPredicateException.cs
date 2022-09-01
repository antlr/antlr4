/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System.Globalization;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;

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
                ruleIndex = ((PredicateTransition)trans).ruleIndex;
                predicateIndex = ((PredicateTransition)trans).predIndex;
            }
            else
            {
                ruleIndex = 0;
                predicateIndex = 0;
            }
            this.predicate = predicate;
            OffendingToken = recognizer.CurrentToken;
        }

        public virtual int RuleIndex => ruleIndex;

        public virtual int PredIndex => predicateIndex;

        [Nullable]
        public virtual string Predicate => predicate;

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
