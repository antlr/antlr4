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
	/// This interface provides an abstract concept of object equality independent of
	/// <see cref="object.Equals(object)">object.Equals(object)</see>
	/// (object equality) and the
	/// <code>==</code>
	/// operator
	/// (reference equality). It can be used to provide algorithm-specific unordered
	/// comparisons without requiring changes to the object itself.
	/// </summary>
	/// <author>Sam Harwell</author>
	public interface IEqualityComparator<T>
	{
		/// <summary>This method returns a hash code for the specified object.</summary>
		/// <remarks>This method returns a hash code for the specified object.</remarks>
		/// <param name="obj">The object.</param>
		/// <returns>
		/// The hash code for
		/// <code>obj</code>
		/// .
		/// </returns>
		int HashCode(T obj);

		/// <summary>This method tests if two objects are equal.</summary>
		/// <remarks>This method tests if two objects are equal.</remarks>
		/// <param name="a">The first object to compare.</param>
		/// <param name="b">The second object to compare.</param>
		/// <returns>
		/// 
		/// <code>true</code>
		/// if
		/// <code>a</code>
		/// equals
		/// <code>b</code>
		/// , otherwise
		/// <code>false</code>
		/// .
		/// </returns>
		bool Equals(T a, T b);
	}
}
