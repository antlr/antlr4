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
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Misc
{
    /// <summary>
    /// <see cref="Antlr4.Runtime.Sharpen.ISet{E}"/>
    /// implementation with closed hashing (open addressing).
    /// </summary>
    public class Array2DHashSet<T> : HashSet<T>
    {
        public const int InitalCapacity = 16;

        public const int InitalBucketCapacity = 8;

        public const double LoadFactor = 0.75;

        [NotNull]
        protected internal readonly EqualityComparer<T> comparator;

        protected internal T[][] buckets;

        /// <summary>How many elements in set</summary>
        protected internal int n = 0;

        protected internal int threshold = (int)(InitalCapacity * LoadFactor);

        protected internal int currentPrime = 1;

        protected internal int initialBucketCapacity = InitalBucketCapacity;

        public Array2DHashSet()
            : this(null, InitalCapacity, InitalBucketCapacity)
        {
        }

        public Array2DHashSet(EqualityComparer<T> comparator)
            : this(comparator, InitalCapacity, InitalBucketCapacity)
        {
        }

        public Array2DHashSet(EqualityComparer<T> comparator, int initialCapacity, int initialBucketCapacity)
        {
            // must be power of 2
            // when to expand
            // jump by 4 primes each expand or whatever
            if (comparator == null)
            {
                comparator = ObjectEqualityComparator.Instance;
            }
            this.comparator = comparator;
            this.buckets = CreateBuckets(initialCapacity);
            this.initialBucketCapacity = initialBucketCapacity;
        }

        /// <summary>
        /// Add
        /// <code>o</code>
        /// to set if not there; return existing value if already
        /// there. This method performs the same operation as
        /// <see cref="Array2DHashSet{T}.Add(object)"/>
        /// aside from
        /// the return value.
        /// </summary>
        public T GetOrAdd(T o)
        {
            if (n > threshold)
            {
                Expand();
            }
            return GetOrAddImpl(o);
        }

        protected internal virtual T GetOrAddImpl(T o)
        {
            int b = GetBucket(o);
            T[] bucket = buckets[b];
            // NEW BUCKET
            if (bucket == null)
            {
                bucket = CreateBucket(initialBucketCapacity);
                bucket[0] = o;
                buckets[b] = bucket;
                n++;
                return o;
            }
            // LOOK FOR IT IN BUCKET
            for (int i = 0; i < bucket.Length; i++)
            {
                T existing = bucket[i];
                if (existing == null)
                {
                    // empty slot; not there, add.
                    bucket[i] = o;
                    n++;
                    return o;
                }
                if (comparator.Equals(existing, o))
                {
                    return existing;
                }
            }
            // found existing, quit
            // FULL BUCKET, expand and add to end
            int oldLength = bucket.Length;
            bucket = Arrays.CopyOf(bucket, bucket.Length * 2);
            buckets[b] = bucket;
            bucket[oldLength] = o;
            // add to end
            n++;
            return o;
        }

        public virtual T Get(T o)
        {
            if (o == null)
            {
                return o;
            }
            int b = GetBucket(o);
            T[] bucket = buckets[b];
            if (bucket == null)
            {
                return null;
            }
            // no bucket
            foreach (T e in bucket)
            {
                if (e == null)
                {
                    return null;
                }
                // empty slot; not there
                if (comparator.Equals(e, o))
                {
                    return e;
                }
            }
            return null;
        }

        protected internal int GetBucket(T o)
        {
            int hash = comparator.GetHashCode(o);
            int b = hash & (buckets.Length - 1);
            // assumes len is power of 2
            return b;
        }

        public override int GetHashCode()
        {
            int hash = MurmurHash.Initialize();
            foreach (T[] bucket in buckets)
            {
                if (bucket == null)
                {
                    continue;
                }
                foreach (T o in bucket)
                {
                    if (o == null)
                    {
                        break;
                    }
                    hash = MurmurHash.Update(hash, comparator.GetHashCode(o));
                }
            }
            hash = MurmurHash.Finish(hash, Count);
            return hash;
        }

        public override bool Equals(object o)
        {
            if (o == this)
            {
                return true;
            }
            if (!(o is Antlr4.Runtime.Misc.Array2DHashSet))
            {
                return false;
            }
            Antlr4.Runtime.Misc.Array2DHashSet<object> other = (Antlr4.Runtime.Misc.Array2DHashSet<object>)o;
            if (other.Count != Count)
            {
                return false;
            }
            bool same = this.ContainsAll(other);
            return same;
        }

        protected internal virtual void Expand()
        {
            T[][] old = buckets;
            currentPrime += 4;
            int newCapacity = buckets.Length * 2;
            T[][] newTable = CreateBuckets(newCapacity);
            int[] newBucketLengths = new int[newTable.Length];
            buckets = newTable;
            threshold = (int)(newCapacity * LoadFactor);
            //		System.out.println("new size="+newCapacity+", thres="+threshold);
            // rehash all existing entries
            int oldSize = Count;
            foreach (T[] bucket in old)
            {
                if (bucket == null)
                {
                    continue;
                }
                foreach (T o in bucket)
                {
                    if (o == null)
                    {
                        break;
                    }
                    int b = GetBucket(o);
                    int bucketLength = newBucketLengths[b];
                    T[] newBucket;
                    if (bucketLength == 0)
                    {
                        // new bucket
                        newBucket = CreateBucket(initialBucketCapacity);
                        newTable[b] = newBucket;
                    }
                    else
                    {
                        newBucket = newTable[b];
                        if (bucketLength == newBucket.Length)
                        {
                            // expand
                            newBucket = Arrays.CopyOf(newBucket, newBucket.Length * 2);
                            newTable[b] = newBucket;
                        }
                    }
                    newBucket[bucketLength] = o;
                    newBucketLengths[b]++;
                }
            }
            System.Diagnostics.Debug.Assert(n == oldSize);
        }

        public bool Add(T t)
        {
            T existing = GetOrAdd(t);
            return existing == t;
        }

        public int Count
        {
            get
            {
                return n;
            }
        }

        public bool IsEmpty()
        {
            return n == 0;
        }

        public bool Contains(object o)
        {
            return ContainsFast(AsElementType(o));
        }

        public virtual bool ContainsFast(T obj)
        {
            if (obj == null)
            {
                return false;
            }
            return Get(obj) != null;
        }

        public virtual IEnumerator<T> GetEnumerator()
        {
            return new Array2DHashSet.SetIterator(this, Sharpen.Collections.ToArray(this));
        }

        public virtual T[] ToArray()
        {
            T[] a = CreateBucket(Count);
            int i = 0;
            foreach (T[] bucket in buckets)
            {
                if (bucket == null)
                {
                    continue;
                }
                foreach (T o in bucket)
                {
                    if (o == null)
                    {
                        break;
                    }
                    a[i++] = o;
                }
            }
            return a;
        }

        public virtual U[] ToArray<U>(U[] a)
        {
            if (a.Length < Count)
            {
                a = Arrays.CopyOf(a, Count);
            }
            int i = 0;
            foreach (T[] bucket in buckets)
            {
                if (bucket == null)
                {
                    continue;
                }
                foreach (T o in bucket)
                {
                    if (o == null)
                    {
                        break;
                    }
                    U targetElement = (U)o;
                    // array store will check this
                    a[i++] = targetElement;
                }
            }
            return a;
        }

        public bool Remove(object o)
        {
            return RemoveFast(AsElementType(o));
        }

        public virtual bool RemoveFast(T obj)
        {
            if (obj == null)
            {
                return false;
            }
            int b = GetBucket(obj);
            T[] bucket = buckets[b];
            if (bucket == null)
            {
                // no bucket
                return false;
            }
            for (int i = 0; i < bucket.Length; i++)
            {
                T e = bucket[i];
                if (e == null)
                {
                    // empty slot; not there
                    return false;
                }
                if (comparator.Equals(e, obj))
                {
                    // found it
                    // shift all elements to the right down one
                    System.Array.Copy(bucket, i + 1, bucket, i, bucket.Length - i - 1);
                    bucket[bucket.Length - 1] = null;
                    n--;
                    return true;
                }
            }
            return false;
        }

        public virtual bool ContainsAll<_T0>(ICollection<_T0> collection)
        {
            if (collection is Antlr4.Runtime.Misc.Array2DHashSet)
            {
                Antlr4.Runtime.Misc.Array2DHashSet<object> s = (Antlr4.Runtime.Misc.Array2DHashSet<object>)collection;
                foreach (object[] bucket in s.buckets)
                {
                    if (bucket == null)
                    {
                        continue;
                    }
                    foreach (object o in bucket)
                    {
                        if (o == null)
                        {
                            break;
                        }
                        if (!this.ContainsFast(AsElementType(o)))
                        {
                            return false;
                        }
                    }
                }
            }
            else
            {
                foreach (object o in collection)
                {
                    if (!this.ContainsFast(AsElementType(o)))
                    {
                        return false;
                    }
                }
            }
            return true;
        }

        public virtual bool AddAll<_T0>(ICollection<_T0> c)
            where _T0 : T
        {
            bool changed = false;
            foreach (T o in c)
            {
                T existing = GetOrAdd(o);
                if (existing != o)
                {
                    changed = true;
                }
            }
            return changed;
        }

        public virtual bool RetainAll<_T0>(ICollection<_T0> c)
        {
            int newsize = 0;
            foreach (T[] bucket in buckets)
            {
                if (bucket == null)
                {
                    continue;
                }
                int i;
                int j;
                for (i = 0, j = 0; i < bucket.Length; i++)
                {
                    if (bucket[i] == null)
                    {
                        break;
                    }
                    if (!c.Contains(bucket[i]))
                    {
                        // removed
                        continue;
                    }
                    // keep
                    if (i != j)
                    {
                        bucket[j] = bucket[i];
                    }
                    j++;
                    newsize++;
                }
                newsize += j;
                while (j < i)
                {
                    bucket[j] = null;
                    j++;
                }
            }
            bool changed = newsize != n;
            n = newsize;
            return changed;
        }

        public virtual bool RemoveAll<_T0>(ICollection<_T0> c)
        {
            bool changed = false;
            foreach (object o in c)
            {
                changed |= RemoveFast(AsElementType(o));
            }
            return changed;
        }

        public virtual void Clear()
        {
            buckets = CreateBuckets(InitalCapacity);
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
            foreach (T[] bucket in buckets)
            {
                if (bucket == null)
                {
                    continue;
                }
                foreach (T o in bucket)
                {
                    if (o == null)
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
                    buf.Append(o.ToString());
                }
            }
            buf.Append('}');
            return buf.ToString();
        }

        public virtual string ToTableString()
        {
            StringBuilder buf = new StringBuilder();
            foreach (T[] bucket in buckets)
            {
                if (bucket == null)
                {
                    buf.Append("null\n");
                    continue;
                }
                buf.Append('[');
                bool first = true;
                foreach (T o in bucket)
                {
                    if (first)
                    {
                        first = false;
                    }
                    else
                    {
                        buf.Append(" ");
                    }
                    if (o == null)
                    {
                        buf.Append("_");
                    }
                    else
                    {
                        buf.Append(o.ToString());
                    }
                }
                buf.Append("]\n");
            }
            return buf.ToString();
        }

        /// <summary>
        /// Return
        /// <code>o</code>
        /// as an instance of the element type
        /// <code>T</code>
        /// . If
        /// <code>o</code>
        /// is non-null but known to not be an instance of
        /// <code>T</code>
        /// , this
        /// method returns
        /// <code>null</code>
        /// . The base implementation does not perform any
        /// type checks; override this method to provide strong type checks for the
        /// <see cref="Array2DHashSet{T}.Contains(object)"/>
        /// and
        /// <see cref="Array2DHashSet{T}.Remove(object)"/>
        /// methods to ensure the arguments to
        /// the
        /// <see cref="IEqualityComparator{T}"/>
        /// for the set always have the expected
        /// types.
        /// </summary>
        /// <param name="o">the object to try and cast to the element type of the set</param>
        /// <returns>
        /// 
        /// <code>o</code>
        /// if it could be an instance of
        /// <code>T</code>
        /// , otherwise
        /// <code>null</code>
        /// .
        /// </returns>
        protected internal virtual T AsElementType(object o)
        {
            return (T)o;
        }

        /// <summary>
        /// Return an array of
        /// <code>T[]</code>
        /// with length
        /// <code>capacity</code>
        /// .
        /// </summary>
        /// <param name="capacity">the length of the array to return</param>
        /// <returns>the newly constructed array</returns>
        protected internal virtual T[][] CreateBuckets(int capacity)
        {
            return new T[capacity][];
        }

        /// <summary>
        /// Return an array of
        /// <code>T</code>
        /// with length
        /// <code>capacity</code>
        /// .
        /// </summary>
        /// <param name="capacity">the length of the array to return</param>
        /// <returns>the newly constructed array</returns>
        protected internal virtual T[] CreateBucket(int capacity)
        {
            return new T[capacity];
        }

        protected internal class SetIterator : IEnumerator<T>
        {
            internal readonly T[] data;

            internal int nextIndex = 0;

            internal bool removed = true;

            public SetIterator(Array2DHashSet<T> _enclosing, T[] data)
            {
                this._enclosing = _enclosing;
                this.data = data;
            }

            public virtual bool HasNext()
            {
                return this.nextIndex < this.data.Length;
            }

            public virtual T Next()
            {
                if (!this.HasNext())
                {
                    throw new InvalidOperationException();
                }
                this.removed = false;
                return this.data[this.nextIndex++];
            }

            public virtual void Remove()
            {
                if (this.removed)
                {
                    throw new InvalidOperationException();
                }
                this._enclosing._enclosing.Remove(this.data[this.nextIndex - 1]);
                this.removed = true;
            }

            private readonly Array2DHashSet<T> _enclosing;
        }
    }
}
