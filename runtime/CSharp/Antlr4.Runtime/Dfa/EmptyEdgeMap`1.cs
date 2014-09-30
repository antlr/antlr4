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
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Sharpen;

#if NET45PLUS
using System.Collections.ObjectModel;
#endif

namespace Antlr4.Runtime.Dfa
{
    /// <summary>
    /// This implementation of
    /// <see cref="AbstractEdgeMap{T}"/>
    /// represents an empty edge map.
    /// </summary>
    /// <author>Sam Harwell</author>
    public sealed class EmptyEdgeMap<T> : AbstractEdgeMap<T>
        where T : class
    {
        public EmptyEdgeMap(int minIndex, int maxIndex)
            : base(minIndex, maxIndex)
        {
        }

        public override AbstractEdgeMap<T> Put(int key, T value)
        {
            if (value == null || key < minIndex || key > maxIndex)
            {
                // remains empty
                return this;
            }
            return new SingletonEdgeMap<T>(minIndex, maxIndex, key, value);
        }

        public override AbstractEdgeMap<T> Clear()
        {
            return this;
        }

        public override AbstractEdgeMap<T> Remove(int key)
        {
            return this;
        }

        public override int Count
        {
            get
            {
                return 0;
            }
        }

        public override bool IsEmpty
        {
            get
            {
                return true;
            }
        }

        public override bool ContainsKey(int key)
        {
            return false;
        }

        public override T this[int key]
        {
            get
            {
                return null;
            }
        }

#if NET45PLUS
        public override IReadOnlyDictionary<int, T> ToMap()
#else
        public override IDictionary<int, T> ToMap()
#endif
        {
            Dictionary<int, T> result = new Dictionary<int, T>();
#if NET45PLUS
            return new ReadOnlyDictionary<int, T>(result);
#else
            return result;
#endif
        }
    }
}
