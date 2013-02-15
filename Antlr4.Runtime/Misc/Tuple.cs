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
using Sharpen;

namespace Antlr4.Runtime.Misc
{
	/// <author>Sam Harwell</author>
	public sealed class Tuple
	{
		public static System.Tuple<T1, T2> Create<T1, T2>(T1 item1, T2 item2)
		{
			return new System.Tuple<T1, T2>(item1, item2);
		}

		public static System.Tuple<T1, T2, T3> Create<T1, T2, T3>(T1 item1, T2 item2, T3 
			item3)
		{
			return new System.Tuple<T1, T2, T3>(item1, item2, item3);
		}

		internal static bool Equals(object x, object y)
		{
			return x == y || (x != null && x.Equals(y));
		}

		public Tuple()
		{
		}
		// static utility class
	}
}
