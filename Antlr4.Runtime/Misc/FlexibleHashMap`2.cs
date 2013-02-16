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
using Antlr4.Runtime.Misc;
using Sharpen;

namespace Antlr4.Runtime.Misc
{
    /// <summary>
    /// A limited map (many unsupported operations) that lets me use
    /// varying hashCode/equals.
    /// </summary>
    /// <remarks>
    /// A limited map (many unsupported operations) that lets me use
    /// varying hashCode/equals.
    /// </remarks>
    public class FlexibleHashMap<K, V> : IDictionary<K, V>
    {
        public const int InitalCapacity = 16;

        public const int InitalBucketCapacity = 8;

        public const double LoadFactor = 0.75;

        public class Entry<K, V>
        {
            public readonly K key;

            public V value;

            public Entry(K key, V value)
            {
                // must be power of 2
                this.key = key;
                this.value = value;
            }

            public override string ToString()
            {
                return key.ToString() + ":" + value.ToString();
            }
        }

        [NotNull]
        protected internal readonly AbstractEqualityComparator<K> comparator;

        protected internal List<FlexibleHashMap.Entry<K, V>>[] buckets;

        /// <summary>How many elements in set</summary>
        protected internal int n = 0;

        protected internal int threshold = (int)(InitalCapacity * LoadFactor);

        protected internal int currentPrime = 1;

        protected internal int initialBucketCapacity = InitalBucketCapacity;

        public FlexibleHashMap() : this(null, InitalCapacity, InitalBucketCapacity)
        {
        }

        public FlexibleHashMap(AbstractEqualityComparator<K> comparator) : this(comparator
            , InitalCapacity, InitalBucketCapacity)
        {
        }

        public FlexibleHashMap(AbstractEqualityComparator<K> comparator, int initialCapacity
            , int initialBucketCapacity)
        {
            // when to expand
            // jump by 4 primes each expand or whatever
            if (comparator == null)
            {
                comparator = ObjectEqualityComparator.Instance;
            }
            this.comparator = comparator;
            this.buckets = CreateEntryListArray(initialBucketCapacity);
            this.initialBucketCapacity = initialBucketCapacity;
        }

        private static List<FlexibleHashMap.Entry<K, V>>[] CreateEntryListArray<K, V>(int
             length)
        {
            List<FlexibleHashMap.Entry<K, V>>[] result = (List<FlexibleHashMap.Entry<K, V>>[]
                )new List<object>[length];
            return result;
        }

        protected internal virtual int GetBucket(K key)
        {
            int hash = comparator.HashCode(key);
            int b = hash & (buckets.Length - 1);
            // assumes len is power of 2
            return b;
        }

        public virtual V Get(object key)
        {
            K typedKey = (K)key;
            if (key == null)
            {
                return null;
            }
            int b = GetBucket(typedKey);
            List<FlexibleHashMap.Entry<K, V>> bucket = buckets[b];
            if (bucket == null)
            {
                return null;
            }
            // no bucket
            foreach (FlexibleHashMap.Entry<K, V> e in bucket)
            {
                if (comparator.Equals(e.key, typedKey))
                {
                    return e.value;
                }
            }
            return null;
        }

        public virtual V Put(K key, V value)
        {
            if (key == null)
            {
                return null;
            }
            if (n > threshold)
            {
                Expand();
            }
            int b = GetBucket(key);
            List<FlexibleHashMap.Entry<K, V>> bucket = buckets[b];
            if (bucket == null)
            {
                bucket = buckets[b] = new List<FlexibleHashMap.Entry<K, V>>();
            }
            foreach (FlexibleHashMap.Entry<K, V> e in bucket)
            {
                if (comparator.Equals(e.key, key))
                {
                    V prev = e.value;
                    e.value = value;
                    n++;
                    return prev;
                }
            }
            // not there
            bucket.AddItem(new FlexibleHashMap.Entry<K, V>(key, value));
            n++;
            return null;
        }

        public virtual V Remove(object key)
        {
            throw new NotSupportedException();
        }

        public virtual void PutAll<_T0>(IDictionary<_T0> m) where _T0:K
        {
            throw new NotSupportedException();
        }

        public virtual ICollection<K> Keys
        {
            get
            {
                throw new NotSupportedException();
            }
        }

        public virtual ICollection<V> Values
        {
            get
            {
                IList<V> a = new List<V>(Count);
                foreach (List<FlexibleHashMap.Entry<K, V>> bucket in buckets)
                {
                    if (bucket == null)
                    {
                        continue;
                    }
                    foreach (FlexibleHashMap.Entry<K, V> e in bucket)
                    {
                        a.AddItem(e.value);
                    }
                }
                return a;
            }
        }

        public virtual ICollection<KeyValuePair<K, V>> EntrySet()
        {
            throw new NotSupportedException();
        }

        public virtual bool ContainsKey(object key)
        {
            return Get(key) != null;
        }

        public virtual bool ContainsValue(object value)
        {
            throw new NotSupportedException();
        }

        public override int GetHashCode()
        {
            int h = 0;
            foreach (List<FlexibleHashMap.Entry<K, V>> bucket in buckets)
            {
                if (bucket == null)
                {
                    continue;
                }
                foreach (FlexibleHashMap.Entry<K, V> e in bucket)
                {
                    if (e == null)
                    {
                        break;
                    }
                    h += comparator.HashCode(e.key);
                }
            }
            return h;
        }

        public override bool Equals(object o)
        {
            throw new NotSupportedException();
        }

        protected internal virtual void Expand()
        {
            List<FlexibleHashMap.Entry<K, V>>[] old = buckets;
            currentPrime += 4;
            int newCapacity = buckets.Length * 2;
            List<FlexibleHashMap.Entry<K, V>>[] newTable = CreateEntryListArray(newCapacity);
            buckets = newTable;
            threshold = (int)(newCapacity * LoadFactor);
            //		System.out.println("new size="+newCapacity+", thres="+threshold);
            // rehash all existing entries
            int oldSize = Count;
            foreach (List<FlexibleHashMap.Entry<K, V>> bucket in old)
            {
                if (bucket == null)
                {
                    continue;
                }
                foreach (FlexibleHashMap.Entry<K, V> e in bucket)
                {
                    if (e == null)
                    {
                        break;
                    }
                    Put(e.key, e.value);
                }
            }
            n = oldSize;
        }

        public virtual int Count
        {
            get
            {
                return n;
            }
        }

        public virtual bool IsEmpty()
        {
            return n == 0;
        }

        public virtual void Clear()
        {
            buckets = CreateEntryListArray(InitalCapacity);
            n = 0;
        }

        public override string ToString()
        {
            if (Count == 0)
            {
                return "{}";
            }
            StringBuilder buf = new StringBuilder();
            buf.Append('{');
            bool first = true;
            foreach (List<FlexibleHashMap.Entry<K, V>> bucket in buckets)
            {
                if (bucket == null)
                {
                    continue;
                }
                foreach (FlexibleHashMap.Entry<K, V> e in bucket)
                {
                    if (e == null)
                    {
                        break;
                    }
                    if (first)
                    {
                        first = false;
                    }
                    else
                    {
                        buf.Append(", ");
                    }
                    buf.Append(e.ToString());
                }
            }
            buf.Append('}');
            return buf.ToString();
        }

        public virtual string ToTableString()
        {
            StringBuilder buf = new StringBuilder();
            foreach (List<FlexibleHashMap.Entry<K, V>> bucket in buckets)
            {
                if (bucket == null)
                {
                    buf.Append("null\n");
                    continue;
                }
                buf.Append('[');
                bool first = true;
                foreach (FlexibleHashMap.Entry<K, V> e in bucket)
                {
                    if (first)
                    {
                        first = false;
                    }
                    else
                    {
                        buf.Append(" ");
                    }
                    if (e == null)
                    {
                        buf.Append("_");
                    }
                    else
                    {
                        buf.Append(e.ToString());
                    }
                }
                buf.Append("]\n");
            }
            return buf.ToString();
        }

        public static void Main(string[] args)
        {
            FlexibleHashMap<string, int> map = new FlexibleHashMap<string, int>();
            map.Put("hi", 1);
            map.Put("mom", 2);
            map.Put("foo", 3);
            map.Put("ach", 4);
            map.Put("cbba", 5);
            map.Put("d", 6);
            map.Put("edf", 7);
            map.Put("mom", 8);
            map.Put("hi", 9);
            System.Console.Out.WriteLine(map);
            System.Console.Out.WriteLine(map.ToTableString());
        }
    }
}
