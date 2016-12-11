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
using System.Text;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{


	public class ArrayPredictionContext : PredictionContext
	{
		/** Parent can be null only if full ctx mode and we make an array
		 *  from {@link #EMPTY} and non-empty. We merge {@link #EMPTY} by using null parent and
		 *  returnState == {@link #EMPTY_RETURN_STATE}.
		 */
		public readonly PredictionContext[] parents;

		/** Sorted for merge, no duplicates; if present,
		 *  {@link #EMPTY_RETURN_STATE} is always last.
		 */
		public readonly int[] returnStates;

		public ArrayPredictionContext(SingletonPredictionContext a)
			: this(new PredictionContext[] { a.parent }, new int[] { a.returnState })
		{
		}

		public ArrayPredictionContext(PredictionContext[] parents, int[] returnStates)
			: base(CalculateHashCode(parents, returnStates))
		{
			//		System.err.println("CREATE ARRAY: "+Arrays.toString(parents)+", "+Arrays.toString(returnStates));
			this.parents = parents;
			this.returnStates = returnStates;
		}

		public override bool IsEmpty
		{
			get
			{
				// since EMPTY_RETURN_STATE can only appear in the last position, we
				// don't need to verify that size==1
				return returnStates[0] == EMPTY_RETURN_STATE;
			}
		}

		public override int Size
		{
			get
			{
				return returnStates.Length;
			}
		}

		public override PredictionContext GetParent(int index)
		{
			return parents[index];
		}

		public override int GetReturnState(int index)
		{
			return returnStates[index];
		}

		//	@Override
		//	public int findReturnState(int returnState) {
		//		return Arrays.binarySearch(returnStates, returnState);
		//	}

		public override bool Equals(Object o)
		{
			if (this == o)
			{
				return true;
			}
			else if (!(o is ArrayPredictionContext))
			{
				return false;
			}

			if (this.GetHashCode() != o.GetHashCode())
			{
				return false; // can't be same if hash is different
			}

			ArrayPredictionContext a = (ArrayPredictionContext)o;
			return Arrays.Equals(returnStates, a.returnStates) &&
				   Arrays.Equals(parents, a.parents);
		}


		public override String ToString()
		{
			if (IsEmpty)
				return "[]";
			StringBuilder buf = new StringBuilder();
			buf.Append("[");
			for (int i = 0; i < returnStates.Length; i++)
			{
				if (i > 0) buf.Append(", ");
				if (returnStates[i] == EMPTY_RETURN_STATE)
				{
					buf.Append("$");
					continue;
				}
				buf.Append(returnStates[i]);
				if (parents[i] != null)
				{
					buf.Append(' ');
					buf.Append(parents[i].ToString());
				}
				else {
					buf.Append("null");
				}
			}
			buf.Append("]");
			return buf.ToString();
		}
	}

}
