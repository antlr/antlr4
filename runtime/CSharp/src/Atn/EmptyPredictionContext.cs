/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
	#pragma warning disable 0659 // 'class' overrides Object.Equals(object o) but does not override Object.GetHashCode()
    public sealed class EmptyPredictionContext : SingletonPredictionContext
    {
        public static readonly EmptyPredictionContext Instance = new EmptyPredictionContext();

        internal EmptyPredictionContext()
            : base(null, EMPTY_RETURN_STATE)
        {
        }

        public override PredictionContext GetParent(int index)
        {
            return null;
        }

        public override int GetReturnState(int index)
        {
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
                return true;
            }
        }

        public override bool Equals(object o)
        {
            return this == o;
        }

		public override string ToString()
		{
			return "$";
		}

		public override string[] ToStrings(IRecognizer recognizer, int currentState)
        {
            return new string[] { "[]" };
        }

        public override string[] ToStrings(IRecognizer recognizer, PredictionContext stop, int currentState)
        {
            return new string[] { "[]" };
        }
    }
}
