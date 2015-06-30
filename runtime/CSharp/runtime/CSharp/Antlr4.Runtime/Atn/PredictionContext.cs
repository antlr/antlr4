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
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Text;
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    public abstract class PredictionContext
    {
        [NotNull]
        public static readonly Antlr4.Runtime.Atn.PredictionContext EmptyLocal = EmptyPredictionContext.LocalContext;

        [NotNull]
        public static readonly Antlr4.Runtime.Atn.PredictionContext EmptyFull = EmptyPredictionContext.FullContext;

        public const int EmptyLocalStateKey = int.MinValue;

        public const int EmptyFullStateKey = int.MaxValue;

        private const int InitialHash = 1;

        /// <summary>
        /// Stores the computed hash code of this
        /// <see cref="PredictionContext"/>
        /// . The hash
        /// code is computed in parts to match the following reference algorithm.
        /// <pre>
        /// private int referenceHashCode() {
        /// int hash =
        /// <see cref="Antlr4.Runtime.Misc.MurmurHash.Initialize()">MurmurHash.initialize</see>
        /// (
        /// <see cref="InitialHash"/>
        /// );
        /// for (int i = 0; i &lt;
        /// <see cref="Size()"/>
        /// ; i++) {
        /// hash =
        /// <see cref="Antlr4.Runtime.Misc.MurmurHash.Update(int, int)">MurmurHash.update</see>
        /// (hash,
        /// <see cref="GetParent(int)">getParent</see>
        /// (i));
        /// }
        /// for (int i = 0; i &lt;
        /// <see cref="Size()"/>
        /// ; i++) {
        /// hash =
        /// <see cref="Antlr4.Runtime.Misc.MurmurHash.Update(int, int)">MurmurHash.update</see>
        /// (hash,
        /// <see cref="GetReturnState(int)">getReturnState</see>
        /// (i));
        /// }
        /// hash =
        /// <see cref="Antlr4.Runtime.Misc.MurmurHash.Finish(int, int)">MurmurHash.finish</see>
        /// (hash, 2 *
        /// <see cref="Size()"/>
        /// );
        /// return hash;
        /// }
        /// </pre>
        /// </summary>
        private readonly int cachedHashCode;

        protected internal PredictionContext(int cachedHashCode)
        {
            this.cachedHashCode = cachedHashCode;
        }

        protected internal static int CalculateEmptyHashCode()
        {
            int hash = MurmurHash.Initialize(InitialHash);
            hash = MurmurHash.Finish(hash, 0);
            return hash;
        }

        protected internal static int CalculateHashCode(Antlr4.Runtime.Atn.PredictionContext parent, int returnState)
        {
            int hash = MurmurHash.Initialize(InitialHash);
            hash = MurmurHash.Update(hash, parent);
            hash = MurmurHash.Update(hash, returnState);
            hash = MurmurHash.Finish(hash, 2);
            return hash;
        }

        protected internal static int CalculateHashCode(Antlr4.Runtime.Atn.PredictionContext[] parents, int[] returnStates)
        {
            int hash = MurmurHash.Initialize(InitialHash);
            foreach (Antlr4.Runtime.Atn.PredictionContext parent in parents)
            {
                hash = MurmurHash.Update(hash, parent);
            }
            foreach (int returnState in returnStates)
            {
                hash = MurmurHash.Update(hash, returnState);
            }
            hash = MurmurHash.Finish(hash, 2 * parents.Length);
            return hash;
        }

        public abstract int Size
        {
            get;
        }

        public abstract int GetReturnState(int index);

        public abstract int FindReturnState(int returnState);

        [return: NotNull]
        public abstract Antlr4.Runtime.Atn.PredictionContext GetParent(int index);

        protected internal abstract Antlr4.Runtime.Atn.PredictionContext AddEmptyContext();

        protected internal abstract Antlr4.Runtime.Atn.PredictionContext RemoveEmptyContext();

        public static Antlr4.Runtime.Atn.PredictionContext FromRuleContext(ATN atn, RuleContext outerContext)
        {
            return FromRuleContext(atn, outerContext, true);
        }

        public static Antlr4.Runtime.Atn.PredictionContext FromRuleContext(ATN atn, RuleContext outerContext, bool fullContext)
        {
            if (outerContext.IsEmpty)
            {
                return fullContext ? EmptyFull : EmptyLocal;
            }
            Antlr4.Runtime.Atn.PredictionContext parent;
            if (outerContext.Parent != null)
            {
                parent = Antlr4.Runtime.Atn.PredictionContext.FromRuleContext(atn, outerContext.Parent, fullContext);
            }
            else
            {
                parent = fullContext ? EmptyFull : EmptyLocal;
            }
            ATNState state = atn.states[outerContext.invokingState];
            RuleTransition transition = (RuleTransition)state.Transition(0);
            return parent.GetChild(transition.followState.stateNumber);
        }

        private static Antlr4.Runtime.Atn.PredictionContext AddEmptyContext(Antlr4.Runtime.Atn.PredictionContext context)
        {
            return context.AddEmptyContext();
        }

        private static Antlr4.Runtime.Atn.PredictionContext RemoveEmptyContext(Antlr4.Runtime.Atn.PredictionContext context)
        {
            return context.RemoveEmptyContext();
        }

        public static Antlr4.Runtime.Atn.PredictionContext Join(Antlr4.Runtime.Atn.PredictionContext context0, Antlr4.Runtime.Atn.PredictionContext context1)
        {
            return Join(context0, context1, PredictionContextCache.Uncached);
        }

        internal static Antlr4.Runtime.Atn.PredictionContext Join(Antlr4.Runtime.Atn.PredictionContext context0, Antlr4.Runtime.Atn.PredictionContext context1, PredictionContextCache contextCache)
        {
            if (context0 == context1)
            {
                return context0;
            }
            if (context0.IsEmpty)
            {
                return IsEmptyLocal(context0) ? context0 : AddEmptyContext(context1);
            }
            else
            {
                if (context1.IsEmpty)
                {
                    return IsEmptyLocal(context1) ? context1 : AddEmptyContext(context0);
                }
            }
            int context0size = context0.Size;
            int context1size = context1.Size;
            if (context0size == 1 && context1size == 1 && context0.GetReturnState(0) == context1.GetReturnState(0))
            {
                Antlr4.Runtime.Atn.PredictionContext merged = contextCache.Join(context0.GetParent(0), context1.GetParent(0));
                if (merged == context0.GetParent(0))
                {
                    return context0;
                }
                else
                {
                    if (merged == context1.GetParent(0))
                    {
                        return context1;
                    }
                    else
                    {
                        return merged.GetChild(context0.GetReturnState(0));
                    }
                }
            }
            int count = 0;
            Antlr4.Runtime.Atn.PredictionContext[] parentsList = new Antlr4.Runtime.Atn.PredictionContext[context0size + context1size];
            int[] returnStatesList = new int[parentsList.Length];
            int leftIndex = 0;
            int rightIndex = 0;
            bool canReturnLeft = true;
            bool canReturnRight = true;
            while (leftIndex < context0size && rightIndex < context1size)
            {
                if (context0.GetReturnState(leftIndex) == context1.GetReturnState(rightIndex))
                {
                    parentsList[count] = contextCache.Join(context0.GetParent(leftIndex), context1.GetParent(rightIndex));
                    returnStatesList[count] = context0.GetReturnState(leftIndex);
                    canReturnLeft = canReturnLeft && parentsList[count] == context0.GetParent(leftIndex);
                    canReturnRight = canReturnRight && parentsList[count] == context1.GetParent(rightIndex);
                    leftIndex++;
                    rightIndex++;
                }
                else
                {
                    if (context0.GetReturnState(leftIndex) < context1.GetReturnState(rightIndex))
                    {
                        parentsList[count] = context0.GetParent(leftIndex);
                        returnStatesList[count] = context0.GetReturnState(leftIndex);
                        canReturnRight = false;
                        leftIndex++;
                    }
                    else
                    {
                        System.Diagnostics.Debug.Assert(context1.GetReturnState(rightIndex) < context0.GetReturnState(leftIndex));
                        parentsList[count] = context1.GetParent(rightIndex);
                        returnStatesList[count] = context1.GetReturnState(rightIndex);
                        canReturnLeft = false;
                        rightIndex++;
                    }
                }
                count++;
            }
            while (leftIndex < context0size)
            {
                parentsList[count] = context0.GetParent(leftIndex);
                returnStatesList[count] = context0.GetReturnState(leftIndex);
                leftIndex++;
                canReturnRight = false;
                count++;
            }
            while (rightIndex < context1size)
            {
                parentsList[count] = context1.GetParent(rightIndex);
                returnStatesList[count] = context1.GetReturnState(rightIndex);
                rightIndex++;
                canReturnLeft = false;
                count++;
            }
            if (canReturnLeft)
            {
                return context0;
            }
            else
            {
                if (canReturnRight)
                {
                    return context1;
                }
            }
            if (count < parentsList.Length)
            {
                parentsList = Arrays.CopyOf(parentsList, count);
                returnStatesList = Arrays.CopyOf(returnStatesList, count);
            }
            if (parentsList.Length == 0)
            {
                // if one of them was EMPTY_LOCAL, it would be empty and handled at the beginning of the method
                return EmptyFull;
            }
            else
            {
                if (parentsList.Length == 1)
                {
                    return new SingletonPredictionContext(parentsList[0], returnStatesList[0]);
                }
                else
                {
                    return new ArrayPredictionContext(parentsList, returnStatesList);
                }
            }
        }

        public static bool IsEmptyLocal(Antlr4.Runtime.Atn.PredictionContext context)
        {
            return context == EmptyLocal;
        }

        public static Antlr4.Runtime.Atn.PredictionContext GetCachedContext(Antlr4.Runtime.Atn.PredictionContext context, ConcurrentDictionary<Antlr4.Runtime.Atn.PredictionContext, Antlr4.Runtime.Atn.PredictionContext> contextCache, PredictionContext.IdentityHashMap visited)
        {
            if (context.IsEmpty)
            {
                return context;
            }
            Antlr4.Runtime.Atn.PredictionContext existing;
            if (visited.TryGetValue(context, out existing))
            {
                return existing;
            }
            if (contextCache.TryGetValue(context, out existing))
            {
                visited[context] = existing;
                return existing;
            }
            bool changed = false;
            Antlr4.Runtime.Atn.PredictionContext[] parents = new Antlr4.Runtime.Atn.PredictionContext[context.Size];
            for (int i = 0; i < parents.Length; i++)
            {
                Antlr4.Runtime.Atn.PredictionContext parent = GetCachedContext(context.GetParent(i), contextCache, visited);
                if (changed || parent != context.GetParent(i))
                {
                    if (!changed)
                    {
                        parents = new Antlr4.Runtime.Atn.PredictionContext[context.Size];
                        for (int j = 0; j < context.Size; j++)
                        {
                            parents[j] = context.GetParent(j);
                        }
                        changed = true;
                    }
                    parents[i] = parent;
                }
            }
            if (!changed)
            {
                existing = contextCache.GetOrAdd(context, context);
                visited[context] = existing;
                return context;
            }
            // We know parents.length>0 because context.isEmpty() is checked at the beginning of the method.
            Antlr4.Runtime.Atn.PredictionContext updated;
            if (parents.Length == 1)
            {
                updated = new SingletonPredictionContext(parents[0], context.GetReturnState(0));
            }
            else
            {
                ArrayPredictionContext arrayPredictionContext = (ArrayPredictionContext)context;
                updated = new ArrayPredictionContext(parents, arrayPredictionContext.returnStates, context.cachedHashCode);
            }
            existing = contextCache.GetOrAdd(updated, updated);
            visited[updated] = existing;
            visited[context] = existing;
            return updated;
        }

        public virtual Antlr4.Runtime.Atn.PredictionContext AppendContext(int returnContext, PredictionContextCache contextCache)
        {
            return AppendContext(Antlr4.Runtime.Atn.PredictionContext.EmptyFull.GetChild(returnContext), contextCache);
        }

        public abstract Antlr4.Runtime.Atn.PredictionContext AppendContext(Antlr4.Runtime.Atn.PredictionContext suffix, PredictionContextCache contextCache);

        public virtual Antlr4.Runtime.Atn.PredictionContext GetChild(int returnState)
        {
            return new SingletonPredictionContext(this, returnState);
        }

        public abstract bool IsEmpty
        {
            get;
        }

        public abstract bool HasEmpty
        {
            get;
        }

        public sealed override int GetHashCode()
        {
            return cachedHashCode;
        }

        public abstract override bool Equals(object o);

        //@Override
        //public String toString() {
        //	return toString(null, Integer.MAX_VALUE);
        //}
        public virtual string[] ToStrings(IRecognizer recognizer, int currentState)
        {
            return ToStrings(recognizer, Antlr4.Runtime.Atn.PredictionContext.EmptyFull, currentState);
        }

        public virtual string[] ToStrings(IRecognizer recognizer, Antlr4.Runtime.Atn.PredictionContext stop, int currentState)
        {
            List<string> result = new List<string>();
            for (int perm = 0; ; perm++)
            {
                int offset = 0;
                bool last = true;
                Antlr4.Runtime.Atn.PredictionContext p = this;
                int stateNumber = currentState;
                StringBuilder localBuffer = new StringBuilder();
                localBuffer.Append("[");
                while (!p.IsEmpty && p != stop)
                {
                    int index = 0;
                    if (p.Size > 0)
                    {
                        int bits = 1;
                        while ((1 << bits) < p.Size)
                        {
                            bits++;
                        }
                        int mask = (1 << bits) - 1;
                        index = (perm >> offset) & mask;
                        last &= index >= p.Size - 1;
                        if (index >= p.Size)
                        {
                            goto outer_continue;
                        }
                        offset += bits;
                    }
                    if (recognizer != null)
                    {
                        if (localBuffer.Length > 1)
                        {
                            // first char is '[', if more than that this isn't the first rule
                            localBuffer.Append(' ');
                        }
                        ATN atn = recognizer.Atn;
                        ATNState s = atn.states[stateNumber];
                        string ruleName = recognizer.RuleNames[s.ruleIndex];
                        localBuffer.Append(ruleName);
                    }
                    else
                    {
                        if (p.GetReturnState(index) != EmptyFullStateKey)
                        {
                            if (!p.IsEmpty)
                            {
                                if (localBuffer.Length > 1)
                                {
                                    // first char is '[', if more than that this isn't the first rule
                                    localBuffer.Append(' ');
                                }
                                localBuffer.Append(p.GetReturnState(index));
                            }
                        }
                    }
                    stateNumber = p.GetReturnState(index);
                    p = p.GetParent(index);
                }
                localBuffer.Append("]");
                result.Add(localBuffer.ToString());
                if (last)
                {
                    break;
                }
outer_continue: ;
            }

            return result.ToArray();
        }

        public sealed class IdentityHashMap : Dictionary<PredictionContext, PredictionContext>
        {
            public IdentityHashMap()
                : base(PredictionContext.IdentityEqualityComparator.Instance)
            {
            }
        }

        public sealed class IdentityEqualityComparator : EqualityComparer<PredictionContext>
        {
            public static readonly PredictionContext.IdentityEqualityComparator Instance = new PredictionContext.IdentityEqualityComparator();

            private IdentityEqualityComparator()
            {
            }

            public override int GetHashCode(PredictionContext obj)
            {
                return obj.GetHashCode();
            }

            public override bool Equals(PredictionContext a, PredictionContext b)
            {
                return a == b;
            }
        }
    }
}
