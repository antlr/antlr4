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
using Antlr4.Runtime.Misc;
using Sharpen;

namespace Antlr4.Runtime.Misc
{
	/// <summary>
	/// This default implementation of
	/// <see cref="IEqualityComparator{T}">IEqualityComparator&lt;T&gt;</see>
	/// uses object equality
	/// for comparisons by calling
	/// <see cref="object.GetHashCode()">object.GetHashCode()</see>
	/// and
	/// <see cref="object.Equals(object)">object.Equals(object)</see>
	/// .
	/// </summary>
	/// <author>Sam Harwell</author>
	public sealed class ObjectEqualityComparator : AbstractEqualityComparator<object>
	{
		public static readonly ObjectEqualityComparator Instance = new ObjectEqualityComparator
			();

		/// <summary>
		/// <inheritDoc></inheritDoc>
		/// <p/>
		/// This implementation returns
		/// <code>obj.</code>
		/// <see cref="object.GetHashCode()">hashCode()</see>
		/// .
		/// </summary>
		public override int HashCode(object obj)
		{
			if (obj == null)
			{
				return 0;
			}
			return obj.GetHashCode();
		}

		/// <summary>
		/// <inheritDoc></inheritDoc>
		/// <p/>
		/// This implementation relies on object equality. If both objects are
		/// <code>null</code>
		/// , this method returns
		/// <code>true</code>
		/// . Otherwise if only
		/// <code>a</code>
		/// is
		/// <code>null</code>
		/// , this method returns
		/// <code>false</code>
		/// . Otherwise,
		/// this method returns the result of
		/// <code>a.</code>
		/// <see cref="object.Equals(object)">equals</see>
		/// <code>(b)</code>
		/// .
		/// </summary>
		public override bool Equals(object a, object b)
		{
			if (a == null)
			{
				return b == null;
			}
			return a.Equals(b);
		}
	}
}
