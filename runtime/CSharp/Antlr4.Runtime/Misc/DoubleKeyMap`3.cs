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
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Misc
{
    /// <summary>Sometimes we need to map a key to a value but key is two pieces of data.</summary>
    /// <remarks>
    /// Sometimes we need to map a key to a value but key is two pieces of data.
    /// This nested hash table saves creating a single key each time we access
    /// map; avoids mem creation.
    /// </remarks>
    public class DoubleKeyMap<Key1, Key2, Value>
    {
        internal IDictionary<Key1, IDictionary<Key2, Value>> data = new LinkedHashMap<Key1, IDictionary<Key2, Value>>();

        public virtual Value Put(Key1 k1, Key2 k2, Value v)
        {
            IDictionary<Key2, Value> data2 = data.Get(k1);
            Value prev = null;
            if (data2 == null)
            {
                data2 = new LinkedHashMap<Key2, Value>();
                data.Put(k1, data2);
            }
            else
            {
                prev = data2.Get(k2);
            }
            data2.Put(k2, v);
            return prev;
        }

        public virtual Value Get(Key1 k1, Key2 k2)
        {
            IDictionary<Key2, Value> data2 = data.Get(k1);
            if (data2 == null)
            {
                return null;
            }
            return data2.Get(k2);
        }

        public virtual IDictionary<Key2, Value> Get(Key1 k1)
        {
            return data.Get(k1);
        }

        /// <summary>Get all values associated with primary key</summary>
        public virtual ICollection<Value> Values(Key1 k1)
        {
            IDictionary<Key2, Value> data2 = data.Get(k1);
            if (data2 == null)
            {
                return null;
            }
            return data2.Values;
        }

        /// <summary>get all primary keys</summary>
        public virtual HashSet<Key1> KeySet()
        {
            return data.Keys;
        }

        /// <summary>get all secondary keys associated with a primary key</summary>
        public virtual HashSet<Key2> KeySet(Key1 k1)
        {
            IDictionary<Key2, Value> data2 = data.Get(k1);
            if (data2 == null)
            {
                return null;
            }
            return data2.Keys;
        }
    }
}
