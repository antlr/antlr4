/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
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
