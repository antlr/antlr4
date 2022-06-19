/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime.Misc;

namespace Antlr4.Runtime.Atn
{
#pragma warning disable 0659 // 'class' overrides Object.Equals(object o) but does not override Object.GetHashCode()

	public class SingletonPredictionContext : PredictionContext
    {
		public static PredictionContext Create(PredictionContext parent, int returnState)
		{
			if (returnState == EMPTY_RETURN_STATE && parent == null)
			{
				// someone can pass in the bits of an array ctx that mean $
				return EmptyPredictionContext.Instance;
			}
			return new SingletonPredictionContext(parent, returnState);
		}

        [NotNull]
        public readonly PredictionContext parent;

        public readonly int returnState;

        internal SingletonPredictionContext(PredictionContext parent, int returnState)
            : base(CalculateHashCode(parent, returnState))
        {
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
            if (this.GetHashCode() != o.GetHashCode())
            {
                return false;
            }
			Antlr4.Runtime.Atn.SingletonPredictionContext other = (Antlr4.Runtime.Atn.SingletonPredictionContext)o;
            return returnState == other.returnState && (parent != null && parent.Equals(other.parent));
        }

		public override string ToString()
		{
			string up = parent != null ? parent.ToString() : "";
			if (up.Length == 0)
			{
				if (returnState == EMPTY_RETURN_STATE)
				{
					return "$";
				}
				return returnState.ToString();
			}
			return returnState.ToString() + " " + up;
		}
    }
}
