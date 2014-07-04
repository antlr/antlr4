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
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Misc
{
    /// <summary>A HashMap that remembers the order that the elements were added.</summary>
    /// <remarks>
    /// A HashMap that remembers the order that the elements were added.
    /// You can alter the ith element with set(i,value) too :)  Unique list.
    /// I need the replace/set-element-i functionality so I'm subclassing
    /// LinkedHashSet.
    /// </remarks>
    [System.Serializable]
    public class OrderedHashSet<T> : LinkedHashSet<T>
    {
        private const long serialVersionUID = 5281944403755906761L;

        /// <summary>Track the elements as they are added to the set</summary>
        protected internal List<T> elements = new List<T>();

        public virtual T Get(int i)
        {
            return elements[i];
        }

        /// <summary>
        /// Replace an existing value with a new value; updates the element
        /// list and the hash table, but not the key as that has not changed.
        /// </summary>
        /// <remarks>
        /// Replace an existing value with a new value; updates the element
        /// list and the hash table, but not the key as that has not changed.
        /// </remarks>
        public virtual T Set(int i, T value)
        {
            T oldElement = elements[i];
            elements.Set(i, value);
            // update list
            base.Remove(oldElement);
            // now update the set: remove/add
            base.Add(value);
            return oldElement;
        }

        public virtual bool Remove(int i)
        {
            T o = elements.RemoveAt(i);
            return base.Remove(o);
        }

        /// <summary>
        /// Add a value to list; keep in hashtable for consistency also;
        /// Key is object itself.
        /// </summary>
        /// <remarks>
        /// Add a value to list; keep in hashtable for consistency also;
        /// Key is object itself.  Good for say asking if a certain string is in
        /// a list of strings.
        /// </remarks>
        public override bool Add(T value)
        {
            bool result = base.Add(value);
            if (result)
            {
                // only track if new element not in set
                elements.Add(value);
            }
            return result;
        }

        public override bool Remove(object o)
        {
            throw new NotSupportedException();
        }

        public override void Clear()
        {
            elements.Clear();
            base.Clear();
        }

        public override int GetHashCode()
        {
            return elements.GetHashCode();
        }

        public override bool Equals(object o)
        {
            if (!(o is OrderedHashSet<object>))
            {
                return false;
            }
            //		System.out.print("equals " + this + ", " + o+" = ");
            bool same = elements != null && elements.Equals(((OrderedHashSet<object>)o).elements);
            //		System.out.println(same);
            return same;
        }

        public override IEnumerator<T> GetEnumerator()
        {
            return elements.GetEnumerator();
        }

        /// <summary>Return the List holding list of table elements.</summary>
        /// <remarks>
        /// Return the List holding list of table elements.  Note that you are
        /// NOT getting a copy so don't write to the list.
        /// </remarks>
        public virtual IList<T> Elements
        {
            get
            {
                return elements;
            }
        }

        public override object Clone()
        {
            OrderedHashSet<T> dup = (OrderedHashSet<T>)base.Clone();
            // safe (result of clone)
            dup.elements = new List<T>(this.elements);
            return dup;
        }

        public override object[] ToArray()
        {
            return Sharpen.Collections.ToArray(elements);
        }

        public override string ToString()
        {
            return elements.ToString();
        }
    }
}
