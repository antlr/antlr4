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
using System.Collections.Generic;
using Sharpen;

namespace Antlr4.Runtime.Misc
{
	/// <summary>A generic set of ints.</summary>
	/// <remarks>A generic set of ints.</remarks>
	/// <seealso cref="IntervalSet">IntervalSet</seealso>
	public interface IntSet
	{
		/// <summary>Add an element to the set</summary>
		void Add(int el);

		/// <summary>Add all elements from incoming set to this set.</summary>
		/// <remarks>
		/// Add all elements from incoming set to this set.  Can limit
		/// to set of its own type. Return "this" so we can chain calls.
		/// </remarks>
		IIntSet AddAll(IIntSet set);

		/// <summary>
		/// Return the intersection of this set with the argument, creating
		/// a new set.
		/// </summary>
		/// <remarks>
		/// Return the intersection of this set with the argument, creating
		/// a new set.
		/// </remarks>
		IIntSet And(IIntSet a);

		IIntSet Complement(IIntSet elements);

		IIntSet Or(IIntSet a);

		IIntSet Subtract(IIntSet a);

		/// <summary>
		/// Return the size of this set (not the underlying implementation's
		/// allocated memory size, for example).
		/// </summary>
		/// <remarks>
		/// Return the size of this set (not the underlying implementation's
		/// allocated memory size, for example).
		/// </remarks>
		int Size();

		bool IsNil();

		bool Equals(object obj);

		int GetSingleElement();

		bool Contains(int el);

		/// <summary>remove this element from this set</summary>
		void Remove(int el);

		IList<int> ToList();

		string ToString();
	}
}
