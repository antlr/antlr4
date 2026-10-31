/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.Collections.Generic;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

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
                this.ruleIndex = -1;
                this.predIndex = -1;
                this.isCtxDependent = false;
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
                if (!(obj is SemanticContext.Predicate))
                {
                    return false;
                }
                if (this == obj)
                {
                    return true;
                }
                SemanticContext.Predicate p = (SemanticContext.Predicate)obj;
                return this.ruleIndex == p.ruleIndex && this.predIndex == p.predIndex && this.isCtxDependent == p.isCtxDependent;
            }

            public override string ToString()
            {
                return "{" + ruleIndex + ":" + predIndex + "}?";
            }
        }

        public class PrecedencePredicate : SemanticContext, IComparable<SemanticContext.PrecedencePredicate>
        {
            public readonly int precedence;

            protected internal PrecedencePredicate()
            {
                this.precedence = 0;
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
                    return SemanticContext.Empty.Instance;
                }
                else
                {
                    return null;
                }
            }

            public virtual int CompareTo(SemanticContext.PrecedencePredicate o)
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
                if (!(obj is SemanticContext.PrecedencePredicate))
                {
                    return false;
                }
                if (this == obj)
                {
                    return true;
                }
                SemanticContext.PrecedencePredicate other = (SemanticContext.PrecedencePredicate)obj;
                return this.precedence == other.precedence;
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

            /// <summary>
            /// Orders contexts by kind and then by their fields, so an operator holds
            /// its operands in one order whichever order they were combined in.
            /// </summary>
            internal static int Compare(SemanticContext a, SemanticContext b)
            {
                int kind = Kind(a).CompareTo(Kind(b));
                if (kind != 0)
                {
                    return kind;
                }
                if (a is SemanticContext.Predicate)
                {
                    SemanticContext.Predicate p = (SemanticContext.Predicate)a;
                    SemanticContext.Predicate q = (SemanticContext.Predicate)b;
                    if (p.ruleIndex != q.ruleIndex)
                    {
                        return p.ruleIndex.CompareTo(q.ruleIndex);
                    }
                    if (p.predIndex != q.predIndex)
                    {
                        return p.predIndex.CompareTo(q.predIndex);
                    }
                    return p.isCtxDependent.CompareTo(q.isCtxDependent);
                }
                if (a is SemanticContext.PrecedencePredicate)
                {
                    return ((SemanticContext.PrecedencePredicate)a).precedence.CompareTo(((SemanticContext.PrecedencePredicate)b).precedence);
                }
                if (a is SemanticContext.AND || a is SemanticContext.OR)
                {
                    SemanticContext[] x = a is SemanticContext.AND ? ((AND)a).opnds : ((OR)a).opnds;
                    SemanticContext[] y = b is SemanticContext.AND ? ((AND)b).opnds : ((OR)b).opnds;
                    if (x.Length != y.Length)
                    {
                        return x.Length.CompareTo(y.Length);
                    }
                    for (int i = 0; i < x.Length; i++)
                    {
                        int c = Compare(x[i], y[i]);
                        if (c != 0)
                        {
                            return c;
                        }
                    }
                    return 0;
                }
                return a.GetHashCode().CompareTo(b.GetHashCode());
            }

            /// <summary>
            /// Merges two operand arrays, each already in <see cref="Compare"/> order, into
            /// one list in that order without duplicates.
            /// </summary>
            internal static List<SemanticContext> Merge(SemanticContext[] x, SemanticContext[] y)
            {
                List<SemanticContext> merged = new List<SemanticContext>(x.Length + y.Length);
                int i = 0;
                int j = 0;
                while (i < x.Length && j < y.Length)
                {
                    int c = Compare(x[i], y[j]);
                    // Compare orders contexts of other kinds by hash code, so it can tie two unequal ones.
                    if (c < 0 || c == 0 && !x[i].Equals(y[j]))
                    {
                        merged.Add(x[i++]);
                    }
                    else if (c > 0)
                    {
                        merged.Add(y[j++]);
                    }
                    else
                    {
                        merged.Add(x[i++]);
                        j++;
                    }
                }
                while (i < x.Length)
                {
                    merged.Add(x[i++]);
                }
                while (j < y.Length)
                {
                    merged.Add(y[j++]);
                }
                return merged;
            }

            /// <summary>
            /// Removes all precedence predicates but the one with the lowest or highest
            /// precedence. They sort next to each other, by precedence.
            /// </summary>
            internal static void KeepOnePrecedencePredicate(List<SemanticContext> operands, bool lowest)
            {
                int first = -1;
                int last = -1;
                for (int i = 0; i < operands.Count; i++)
                {
                    if (operands[i] is SemanticContext.PrecedencePredicate)
                    {
                        if (first < 0)
                        {
                            first = i;
                        }
                        last = i;
                    }
                }
                if (first < 0)
                {
                    return;
                }
                SemanticContext kept = operands[lowest ? first : last];
                operands.RemoveRange(first, last - first + 1);
                operands.Insert(first, kept);
            }

            private static int Kind(SemanticContext context)
            {
                if (context is SemanticContext.Predicate)
                {
                    return 0;
                }
                if (context is SemanticContext.PrecedencePredicate)
                {
                    return 1;
                }
                if (context is SemanticContext.AND)
                {
                    return 2;
                }
                if (context is SemanticContext.OR)
                {
                    return 3;
                }
                return 4;
            }
        }

        public class AND : SemanticContext.Operator
        {
            [NotNull]
            public readonly SemanticContext[] opnds;

            private readonly int hashCode;

            public AND(SemanticContext a, SemanticContext b)
            {
                List<SemanticContext> operands = Merge(
                    a is SemanticContext.AND ? ((AND)a).opnds : new SemanticContext[] { a },
                    b is SemanticContext.AND ? ((AND)b).opnds : new SemanticContext[] { b });
                // interested in the transition with the lowest precedence
                KeepOnePrecedencePredicate(operands, true);
                opnds = operands.ToArray();
                hashCode = MurmurHash.HashCode(opnds, typeof(SemanticContext.AND).GetHashCode());
            }

            public override ICollection<SemanticContext> Operands
            {
                get
                {
                    return Arrays.AsList(opnds);
                }
            }

            public override bool Equals(object obj)
            {
                if (this == obj)
                {
                    return true;
                }
                if (!(obj is SemanticContext.AND))
                {
                    return false;
                }
                SemanticContext.AND other = (SemanticContext.AND)obj;
                return hashCode == other.hashCode && Arrays.Equals(this.opnds, other.opnds);
            }

            public override int GetHashCode()
            {
                return hashCode;
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
                    result = SemanticContext.AndOp(result, operands[i]);
                }
                return result;
            }

            public override string ToString()
            {
                return Utils.Join("&&", opnds);
            }
        }

        public class OR : SemanticContext.Operator
        {
            [NotNull]
            public readonly SemanticContext[] opnds;

            private readonly int hashCode;

            public OR(SemanticContext a, SemanticContext b)
            {
                List<SemanticContext> operands = Merge(
                    a is SemanticContext.OR ? ((OR)a).opnds : new SemanticContext[] { a },
                    b is SemanticContext.OR ? ((OR)b).opnds : new SemanticContext[] { b });
                // interested in the transition with the highest precedence
                KeepOnePrecedencePredicate(operands, false);
                this.opnds = operands.ToArray();
                hashCode = MurmurHash.HashCode(opnds, typeof(SemanticContext.OR).GetHashCode());
            }

            public override ICollection<SemanticContext> Operands
            {
                get
                {
                    return Arrays.AsList(opnds);
                }
            }

            public override bool Equals(object obj)
            {
                if (this == obj)
                {
                    return true;
                }
                if (!(obj is SemanticContext.OR))
                {
                    return false;
                }
                SemanticContext.OR other = (SemanticContext.OR)obj;
                return hashCode == other.hashCode && Arrays.Equals(this.opnds, other.opnds);
            }

            public override int GetHashCode()
            {
                return hashCode;
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
                    result = SemanticContext.OrOp(result, operands[i]);
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
            SemanticContext.AND result = new SemanticContext.AND(a, b);
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
            SemanticContext.OR result = new SemanticContext.OR(a, b);
            if (result.opnds.Length == 1)
            {
                return result.opnds[0];
            }
            return result;
        }
    }
}
