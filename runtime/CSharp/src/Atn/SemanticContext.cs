/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.Collections.Generic;
using System.Linq;
using Antlr4.Runtime.Misc;

namespace Antlr4.Runtime.Atn
{
    public abstract class SemanticContext
    {
        public abstract bool Eval<Symbol, ATNInterpreter>(Recognizer<Symbol, ATNInterpreter> parser, RuleContext parserCallStack)
            where ATNInterpreter : ATNSimulator;

		public virtual SemanticContext EvalPrecedence<Symbol, ATNInterpreter>(Recognizer<Symbol, ATNInterpreter> parser, RuleContext parserCallStack)
            where ATNInterpreter : ATNSimulator
        {
            return this;
        }

        public class Empty : SemanticContext
        {
            public static readonly SemanticContext Instance = new Empty();

            public override bool Eval<Symbol, ATNInterpreter>(Recognizer<Symbol, ATNInterpreter> parser, RuleContext parserCallStack)
            {
                return false;
            }
        }

        public class Predicate : SemanticContext
        {
            public readonly int ruleIndex;

            public readonly int predIndex;

            public readonly bool isCtxDependent;

            protected internal Predicate()
            {
                // e.g., $i ref in pred
                ruleIndex = -1;
                predIndex = -1;
                isCtxDependent = false;
            }

            public Predicate(int ruleIndex, int predIndex, bool isCtxDependent)
            {
                this.ruleIndex = ruleIndex;
                this.predIndex = predIndex;
                this.isCtxDependent = isCtxDependent;
            }

            public override bool Eval<Symbol, ATNInterpreter>(Recognizer<Symbol, ATNInterpreter> parser, RuleContext parserCallStack)
            {
                RuleContext localctx = isCtxDependent ? parserCallStack : null;
                return parser.Sempred(localctx, ruleIndex, predIndex);
            }

            public override int GetHashCode()
            {
                int hashCode = MurmurHash.Initialize();
                hashCode = MurmurHash.Update(hashCode, ruleIndex);
                hashCode = MurmurHash.Update(hashCode, predIndex);
                hashCode = MurmurHash.Update(hashCode, isCtxDependent ? 1 : 0);
                hashCode = MurmurHash.Finish(hashCode, 3);
                return hashCode;
            }

            public override bool Equals(object obj)
            {
                if (!(obj is Predicate o))
                {
                    return false;
                }
                if (this == o)
                {
                    return true;
                }

                return ruleIndex == o.ruleIndex && predIndex == o.predIndex && isCtxDependent == o.isCtxDependent;
            }

            public override string ToString()
            {
                return "{" + ruleIndex + ":" + predIndex + "}?";
            }
        }

        public class PrecedencePredicate : SemanticContext, IComparable<PrecedencePredicate>
        {
            public readonly int precedence;

            protected internal PrecedencePredicate()
            {
                precedence = 0;
            }

            public PrecedencePredicate(int precedence)
            {
                this.precedence = precedence;
            }

            public override bool Eval<Symbol, ATNInterpreter>(Recognizer<Symbol, ATNInterpreter> parser, RuleContext parserCallStack)
            {
                return parser.Precpred(parserCallStack, precedence);
            }

            public override SemanticContext EvalPrecedence<Symbol, ATNInterpreter>(Recognizer<Symbol, ATNInterpreter> parser, RuleContext parserCallStack)
            {
                if (parser.Precpred(parserCallStack, precedence))
                {
                    return Empty.Instance;
                }
                else
                {
                    return null;
                }
            }

            public virtual int CompareTo(PrecedencePredicate o)
            {
                return precedence - o.precedence;
            }

            public override int GetHashCode()
            {
                int hashCode = 1;
                hashCode = 31 * hashCode + precedence;
                return hashCode;
            }

            public override bool Equals(object obj)
            {
                if (!(obj is PrecedencePredicate other))
                {
                    return false;
                }
                if (this == other)
                {
                    return true;
                }

                return precedence == other.precedence;
            }

            public override string ToString()
            {
                // precedence >= _precedenceStack.peek()
                return "{" + precedence + ">=prec}?";
            }
        }

        public abstract class Operator : SemanticContext
        {
            [NotNull]
            public abstract ICollection<SemanticContext> Operands
            {
                get;
            }
        }

        public class AND : Operator
        {
            [NotNull]
            public readonly SemanticContext[] opnds;

            public AND(SemanticContext a, SemanticContext b)
            {
                HashSet<SemanticContext> operands = new HashSet<SemanticContext>();
                if (a is AND aAnd)
                {
                    operands.UnionWith(aAnd.opnds);
                }
                else
                {
                    operands.Add(a);
                }
                if (b is AND bAnd)
                {
                    operands.UnionWith(bAnd.opnds);
                }
                else
                {
                    operands.Add(b);
                }
                IList<PrecedencePredicate> precedencePredicates = FilterPrecedencePredicates(operands);
                if (precedencePredicates.Count > 0)
                {
                    // interested in the transition with the lowest precedence
                    PrecedencePredicate reduced = precedencePredicates.Min();
                    operands.Add(reduced);
                }
                opnds = operands.ToArray();
            }

            public override ICollection<SemanticContext> Operands => opnds;

            public override bool Equals(object obj)
            {
                if (this == obj)
                {
                    return true;
                }
                if (!(obj is AND other))
                {
                    return false;
                }

                return Arrays.Equals(opnds, other.opnds);
            }

            public override int GetHashCode()
            {
                return MurmurHash.HashCode(opnds, typeof(AND).GetHashCode());
            }

            public override bool Eval<Symbol, ATNInterpreter>(Recognizer<Symbol, ATNInterpreter> parser, RuleContext parserCallStack)
            {
                foreach (SemanticContext opnd in opnds)
                {
                    if (!opnd.Eval(parser, parserCallStack))
                    {
                        return false;
                    }
                }
                return true;
            }

            public override SemanticContext EvalPrecedence<Symbol, ATNInterpreter>(Recognizer<Symbol, ATNInterpreter> parser, RuleContext parserCallStack)
            {
                bool differs = false;
                IList<SemanticContext> operands = new List<SemanticContext>();
                foreach (SemanticContext context in opnds)
                {
                    SemanticContext evaluated = context.EvalPrecedence(parser, parserCallStack);
                    differs |= (evaluated != context);
                    if (evaluated == null)
                    {
                        // The AND context is false if any element is false
                        return null;
                    }
                    else
                    {
                        if (evaluated != Empty.Instance)
                        {
                            // Reduce the result by skipping true elements
                            operands.Add(evaluated);
                        }
                    }
                }
                if (!differs)
                {
                    return this;
                }
                if (operands.Count == 0)
                {
                    // all elements were true, so the AND context is true
                    return Empty.Instance;
                }
                SemanticContext result = operands[0];
                for (int i = 1; i < operands.Count; i++)
                {
                    result = AndOp(result, operands[i]);
                }
                return result;
            }

            public override string ToString()
            {
                return Utils.Join("&&", opnds);
            }
        }

        public class OR : Operator
        {
            [NotNull]
            public readonly SemanticContext[] opnds;

            public OR(SemanticContext a, SemanticContext b)
            {
                HashSet<SemanticContext> operands = new HashSet<SemanticContext>();
                if (a is OR aOr)
                {
                    operands.UnionWith(aOr.opnds);
                }
                else
                {
                    operands.Add(a);
                }
                if (b is OR bOr)
                {
                    operands.UnionWith(bOr.opnds);
                }
                else
                {
                    operands.Add(b);
                }
                IList<PrecedencePredicate> precedencePredicates = FilterPrecedencePredicates(operands);
                if (precedencePredicates.Count > 0)
                {
                    // interested in the transition with the highest precedence
                    PrecedencePredicate reduced = precedencePredicates.Max();
                    operands.Add(reduced);
                }
                opnds = operands.ToArray();
            }

            public override ICollection<SemanticContext> Operands => opnds;

            public override bool Equals(object obj)
            {
                if (this == obj)
                {
                    return true;
                }
                if (!(obj is OR other))
                {
                    return false;
                }

                return Arrays.Equals(opnds, other.opnds);
            }

            public override int GetHashCode()
            {
                return MurmurHash.HashCode(opnds, typeof(OR).GetHashCode());
            }

            public override bool Eval<Symbol, ATNInterpreter>(Recognizer<Symbol, ATNInterpreter> parser, RuleContext parserCallStack)
            {
                foreach (SemanticContext opnd in opnds)
                {
                    if (opnd.Eval(parser, parserCallStack))
                    {
                        return true;
                    }
                }
                return false;
            }

            public override SemanticContext EvalPrecedence<Symbol, ATNInterpreter>(Recognizer<Symbol, ATNInterpreter> parser, RuleContext parserCallStack)
            {
                bool differs = false;
                IList<SemanticContext> operands = new List<SemanticContext>();
                foreach (SemanticContext context in opnds)
                {
                    SemanticContext evaluated = context.EvalPrecedence(parser, parserCallStack);
                    differs |= (evaluated != context);
                    if (evaluated == Empty.Instance)
                    {
                        // The OR context is true if any element is true
                        return Empty.Instance;
                    }
                    else
                    {
                        if (evaluated != null)
                        {
                            // Reduce the result by skipping false elements
                            operands.Add(evaluated);
                        }
                    }
                }
                if (!differs)
                {
                    return this;
                }
                if (operands.Count == 0)
                {
                    // all elements were false, so the OR context is false
                    return null;
                }
                SemanticContext result = operands[0];
                for (int i = 1; i < operands.Count; i++)
                {
                    result = OrOp(result, operands[i]);
                }
                return result;
            }

            public override string ToString()
            {
                return Utils.Join("||", opnds);
            }
        }

        public static SemanticContext AndOp(SemanticContext a, SemanticContext b)
        {
            if (a == null || a == Empty.Instance)
            {
                return b;
            }
            if (b == null || b == Empty.Instance)
            {
                return a;
            }
            AND result = new AND(a, b);
            if (result.opnds.Length == 1)
            {
                return result.opnds[0];
            }
            return result;
        }

        public static SemanticContext OrOp(SemanticContext a, SemanticContext b)
        {
            if (a == null)
            {
                return b;
            }
            if (b == null)
            {
                return a;
            }
            if (a == Empty.Instance || b == Empty.Instance)
            {
                return Empty.Instance;
            }
            OR result = new OR(a, b);
            if (result.opnds.Length == 1)
            {
                return result.opnds[0];
            }
            return result;
        }

        private static IList<PrecedencePredicate> FilterPrecedencePredicates(HashSet<SemanticContext> collection)
        {
            if (!collection.OfType<PrecedencePredicate>().Any())
                return Array.Empty<PrecedencePredicate>();

            List<PrecedencePredicate> result = collection.OfType<PrecedencePredicate>().ToList();
            collection.ExceptWith(result);
            return result;
        }
    }
}
