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
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
#pragma warning disable 0659 // 'class' overrides Object.Equals(object o) but does not override Object.GetHashCode()
    public class SingletonPredictionContext : PredictionContext
    {
        [NotNull]
        public readonly PredictionContext parent;

        public readonly int returnState;

        internal SingletonPredictionContext(PredictionContext parent, int returnState)
            : base(CalculateHashCode(parent, returnState))
        {
            System.Diagnostics.Debug.Assert(returnState != EmptyFullStateKey && returnState != EmptyLocalStateKey);
            this.parent = parent;
            this.returnState = returnState;
        }

        public override PredictionContext GetParent(int index)
        {
            System.Diagnostics.Debug.Assert(index == 0);
            return parent;
        }

        public override int GetReturnState(int index)
        {
            System.Diagnostics.Debug.Assert(index == 0);
            return returnState;
        }

        public override int FindReturnState(int returnState)
        {
            return this.returnState == returnState ? 0 : -1;
        }

        public override int Size
        {
            get
            {
                return 1;
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
                return false;
            }
        }

        public override PredictionContext AppendContext(PredictionContext suffix, PredictionContextCache contextCache)
        {
            return contextCache.GetChild(parent.AppendContext(suffix, contextCache), returnState);
        }

        protected internal override PredictionContext AddEmptyContext()
        {
            PredictionContext[] parents = new PredictionContext[] { parent, EmptyFull };
            int[] returnStates = new int[] { returnState, EmptyFullStateKey };
            return new ArrayPredictionContext(parents, returnStates);
        }

        protected internal override PredictionContext RemoveEmptyContext()
        {
            return this;
        }

        public override bool Equals(object o)
        {
            if (o == this)
            {
                return true;
            }
            else
            {
                if (!(o is Antlr4.Runtime.Atn.SingletonPredictionContext))
                {
                    return false;
                }
            }
            Antlr4.Runtime.Atn.SingletonPredictionContext other = (Antlr4.Runtime.Atn.SingletonPredictionContext)o;
            if (this.GetHashCode() != other.GetHashCode())
            {
                return false;
            }
            return returnState == other.returnState && parent.Equals(other.parent);
        }
    }
}
