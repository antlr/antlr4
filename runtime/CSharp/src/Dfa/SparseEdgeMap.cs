/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using Antlr4.Runtime.Misc;

namespace Antlr4.Runtime.Dfa
{
    /// <author>Sam Harwell</author>
    public sealed class SparseEdgeMap<T> : AbstractEdgeMap<T>
        where T : class
    {
        private const int DefaultMaxSize = 5;

        private readonly int[] keys;

        private readonly List<T> values;

        public SparseEdgeMap(int minIndex, int maxIndex)
            : this(minIndex, maxIndex, DefaultMaxSize)
        {
        }

        public SparseEdgeMap(int minIndex, int maxIndex, int maxSparseSize)
            : base(minIndex, maxIndex)
        {
            keys = new int[maxSparseSize];
            values = new List<T>(maxSparseSize);
        }

        private SparseEdgeMap(SparseEdgeMap<T> map, int maxSparseSize)
            : base(map.minIndex, map.maxIndex)
        {
            lock (map)
            {
                if (maxSparseSize < map.values.Count)
                {
                    throw new ArgumentException();
                }
                keys = new int[maxSparseSize];
                Array.Copy(map.keys, keys, map.keys.Length);
                values = new List<T>(maxSparseSize);
                values.AddRange(map.Values);
            }
        }

        public int[] Keys => keys;

        public IList<T> Values => values;

        public int MaxSparseSize => keys.Length;

        public override int Count => values.Count;

        public override bool IsEmpty => values.Count == 0;

        public override bool ContainsKey(int key)
        {
            return this[key] != null;
        }

        public override T this[int key]
        {
            get
            {
                // Special property of this collection: values are only even added to
                // the end, else a new object is returned from put(). Therefore no lock
                // is required in this method.
                int index = Array.BinarySearch(keys, 0, Count, key);
                if (index < 0)
                {
                    return null;
                }
                return values[index];
            }
        }

        public override AbstractEdgeMap<T> Put(int key, T value)
        {
            if (key < minIndex || key > maxIndex)
            {
                return this;
            }
            if (value == null)
            {
                return Remove(key);
            }
            lock (this)
            {
                int index = Array.BinarySearch(keys, 0, Count, key);
                if (index >= 0)
                {
                    // replace existing entry
                    values[index] = value;
                    return this;
                }
                System.Diagnostics.Debug.Assert(index < 0 && value != null);
                int insertIndex = -index - 1;
                if (Count < MaxSparseSize && insertIndex == Count)
                {
                    // stay sparse and add new entry
                    keys[insertIndex] = key;
                    values.Add(value);
                    return this;
                }
                int desiredSize = Count >= MaxSparseSize ? MaxSparseSize * 2 : MaxSparseSize;
                int space = maxIndex - minIndex + 1;
                // SparseEdgeMap only uses less memory than ArrayEdgeMap up to half the size of the symbol space
                if (desiredSize >= space / 2)
                {
                    ArrayEdgeMap<T> arrayMap = new ArrayEdgeMap<T>(minIndex, maxIndex);
                    arrayMap = ((ArrayEdgeMap<T>)arrayMap.PutAll(this));
                    arrayMap.Put(key, value);
                    return arrayMap;
                }
                else
                {
                    SparseEdgeMap<T> resized = new SparseEdgeMap<T>(this, desiredSize);
                    Array.Copy(resized.keys, insertIndex, resized.keys, insertIndex + 1, Count - insertIndex);
                    resized.keys[insertIndex] = key;
                    resized.values.Insert(insertIndex, value);
                    return resized;
                }
            }
        }

        public override AbstractEdgeMap<T> Remove(int key)
        {
            lock (this)
            {
                int index = Array.BinarySearch(keys, 0, Count, key);
                if (index < 0)
                {
                    return this;
                }
                SparseEdgeMap<T> result = new SparseEdgeMap<T>(this, MaxSparseSize);
                Array.Copy(result.keys, index + 1, result.keys, index, Count - index - 1);
                result.values.RemoveAt(index);
                return result;
            }
        }

        public override AbstractEdgeMap<T> Clear()
        {
            if (IsEmpty)
            {
                return this;
            }
            return new EmptyEdgeMap<T>(minIndex, maxIndex);
        }

        public override IReadOnlyDictionary<int, T> ToMap()
        {
            if (IsEmpty)
            {
                return Collections.EmptyMap<int, T>();
            }
            lock (this)
            {
                IDictionary<int, T> result = new SortedDictionary<int, T>();

                for (int i = 0; i < Count; i++)
                {
                    result[keys[i]] = values[i];
                }

                return new ReadOnlyDictionary<int, T>(result);
            }
        }
    }
}
