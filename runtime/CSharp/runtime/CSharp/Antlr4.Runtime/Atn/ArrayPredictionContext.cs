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
using System;
using System.Collections.Generic;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
#pragma warning disable 0659 // 'class' overrides Object.Equals(object o) but does not override Object.GetHashCode()
    public class ArrayPredictionContext : PredictionContext
    {
        [NotNull]
        public readonly PredictionContext[] parents;

        [NotNull]
        public readonly int[] returnStates;

        internal ArrayPredictionContext(PredictionContext[] parents, int[] returnStates)
            : base(CalculateHashCode(parents, returnStates))
        {
            System.Diagnostics.Debug.Assert(parents.Length == returnStates.Length);
            System.Diagnostics.Debug.Assert(returnStates.Length > 1 || returnStates[0] != EmptyFullStateKey, "Should be using PredictionContext.EMPTY instead.");
            this.parents = parents;
            this.returnStates = returnStates;
        }

        internal ArrayPredictionContext(PredictionContext[] parents, int[] returnStates, int hashCode)
            : base(hashCode)
        {
            System.Diagnostics.Debug.Assert(parents.Length == returnStates.Length);
            System.Diagnostics.Debug.Assert(returnStates.Length > 1 || returnStates[0] != EmptyFullStateKey, "Should be using PredictionContext.EMPTY instead.");
            this.parents = parents;
            this.returnStates = returnStates;
        }

        public override PredictionContext GetParent(int index)
        {
            return parents[index];
        }

        public override int GetReturnState(int index)
        {
            return returnStates[index];
        }

        public override int FindReturnState(int returnState)
        {
            return System.Array.BinarySearch(returnStates, returnState);
        }

        public override int Size
        {
            get
            {
                return returnStates.Length;
            }
        }

        public override bool IsEmpty
        {
            get
            {
                return false;
            }
        }

        public override bool HasEmpty
        {
            get
            {
                return returnStates[returnStates.Length - 1] == EmptyFullStateKey;
            }
        }

        protected internal override PredictionContext AddEmptyContext()
        {
            if (HasEmpty)
            {
                return this;
            }
            PredictionContext[] parents2 = Arrays.CopyOf(parents, parents.Length + 1);
            int[] returnStates2 = Arrays.CopyOf(returnStates, returnStates.Length + 1);
            parents2[parents2.Length - 1] = PredictionContext.EmptyFull;
            returnStates2[returnStates2.Length - 1] = PredictionContext.EmptyFullStateKey;
            return new Antlr4.Runtime.Atn.ArrayPredictionContext(parents2, returnStates2);
        }

        protected internal override PredictionContext RemoveEmptyContext()
        {
            if (!HasEmpty)
            {
                return this;
            }
            if (returnStates.Length == 2)
            {
                return new SingletonPredictionContext(parents[0], returnStates[0]);
            }
            else
            {
                PredictionContext[] parents2 = Arrays.CopyOf(parents, parents.Length - 1);
                int[] returnStates2 = Arrays.CopyOf(returnStates, returnStates.Length - 1);
                return new Antlr4.Runtime.Atn.ArrayPredictionContext(parents2, returnStates2);
            }
        }

        public override PredictionContext AppendContext(PredictionContext suffix, PredictionContextCache contextCache)
        {
            return AppendContext(this, suffix, new PredictionContext.IdentityHashMap());
        }

        private static PredictionContext AppendContext(PredictionContext context, PredictionContext suffix, PredictionContext.IdentityHashMap visited)
        {
            if (suffix.IsEmpty)
            {
                if (IsEmptyLocal(suffix))
                {
                    if (context.HasEmpty)
                    {
                        return EmptyLocal;
                    }
                    throw new NotSupportedException("what to do here?");
                }
                return context;
            }
            if (suffix.Size != 1)
            {
                throw new NotSupportedException("Appending a tree suffix is not yet supported.");
            }
            PredictionContext result;
            if (!visited.TryGetValue(context, out result))
            {
                if (context.IsEmpty)
                {
                    result = suffix;
                }
                else
                {
                    int parentCount = context.Size;
                    if (context.HasEmpty)
                    {
                        parentCount--;
                    }
                    PredictionContext[] updatedParents = new PredictionContext[parentCount];
                    int[] updatedReturnStates = new int[parentCount];
                    for (int i = 0; i < parentCount; i++)
                    {
                        updatedReturnStates[i] = context.GetReturnState(i);
                    }
                    for (int i_1 = 0; i_1 < parentCount; i_1++)
                    {
                        updatedParents[i_1] = AppendContext(context.GetParent(i_1), suffix, visited);
                    }
                    if (updatedParents.Length == 1)
                    {
                        result = new SingletonPredictionContext(updatedParents[0], updatedReturnStates[0]);
                    }
                    else
                    {
                        System.Diagnostics.Debug.Assert(updatedParents.Length > 1);
                        result = new Antlr4.Runtime.Atn.ArrayPredictionContext(updatedParents, updatedReturnStates);
                    }
                    if (context.HasEmpty)
                    {
                        result = PredictionContext.Join(result, suffix);
                    }
                }
                visited[context] = result;
            }
            return result;
        }

        public override bool Equals(object o)
        {
            if (this == o)
            {
                return true;
            }
            else
            {
                if (!(o is Antlr4.Runtime.Atn.ArrayPredictionContext))
                {
                    return false;
                }
            }
            if (this.GetHashCode() != o.GetHashCode())
            {
                return false;
            }
            // can't be same if hash is different
            Antlr4.Runtime.Atn.ArrayPredictionContext other = (Antlr4.Runtime.Atn.ArrayPredictionContext)o;
            return Equals(other, new HashSet<PredictionContextCache.IdentityCommutativePredictionContextOperands>());
        }

        private bool Equals(Antlr4.Runtime.Atn.ArrayPredictionContext other, HashSet<PredictionContextCache.IdentityCommutativePredictionContextOperands> visited)
        {
            Stack<PredictionContext> selfWorkList = new Stack<PredictionContext>();
            Stack<PredictionContext> otherWorkList = new Stack<PredictionContext>();
            selfWorkList.Push(this);
            otherWorkList.Push(other);
            while (selfWorkList.Count > 0)
            {
                PredictionContextCache.IdentityCommutativePredictionContextOperands operands = new PredictionContextCache.IdentityCommutativePredictionContextOperands(selfWorkList.Pop(), otherWorkList.Pop());
                if (!visited.Add(operands))
                {
                    continue;
                }
                int selfSize = operands.X.Size;
                if (selfSize == 0)
                {
                    if (!operands.X.Equals(operands.Y))
                    {
                        return false;
                    }
                    continue;
                }
                int otherSize = operands.Y.Size;
                if (selfSize != otherSize)
                {
                    return false;
                }
                for (int i = 0; i < selfSize; i++)
                {
                    if (operands.X.GetReturnState(i) != operands.Y.GetReturnState(i))
                    {
                        return false;
                    }
                    PredictionContext selfParent = operands.X.GetParent(i);
                    PredictionContext otherParent = operands.Y.GetParent(i);
                    if (selfParent.GetHashCode() != otherParent.GetHashCode())
                    {
                        return false;
                    }
                    if (selfParent != otherParent)
                    {
                        selfWorkList.Push(selfParent);
                        otherWorkList.Push(otherParent);
                    }
                }
            }
            return true;
        }
    }
}
