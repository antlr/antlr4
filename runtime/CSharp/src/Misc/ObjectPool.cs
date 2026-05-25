/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.Collections.Generic;

namespace Antlr4.Runtime.Misc
{
    /// <summary>
    /// A simple object pool that reuses instances to reduce GC pressure.
    /// Not thread-safe — intended for use by a single parser/lexer instance
    /// which already cannot be shared across threads during a parse.
    /// </summary>
    internal sealed class ObjectPool<T> where T : class
    {
        private readonly Func<T> _factory;
        private readonly Action<T> _reset;
        private readonly Stack<T> _pool;
        private readonly int _maxSize;

        /// <param name="factory">Creates a new instance when the pool is empty.</param>
        /// <param name="reset">Resets an instance to a clean state before returning it to the pool.</param>
        /// <param name="maxSize">Maximum number of instances to retain. Excess items are left for GC.</param>
        public ObjectPool(Func<T> factory, Action<T> reset, int maxSize = 16)
        {
            _factory = factory ?? throw new ArgumentNullException(nameof(factory));
            _reset = reset ?? throw new ArgumentNullException(nameof(reset));
            _maxSize = maxSize;
            _pool = new Stack<T>(Math.Min(maxSize, 4));
        }

        /// <summary>
        /// Gets an instance from the pool, or creates a new one if the pool is empty.
        /// </summary>
        public T Get()
        {
            return _pool.Count > 0 ? _pool.Pop() : _factory();
        }

        /// <summary>
        /// Returns an instance to the pool after resetting it.
        /// If the pool is full, the instance is simply discarded for GC.
        /// </summary>
        public void Return(T item)
        {
            if (item == null) return;
            if (_pool.Count < _maxSize)
            {
                _reset(item);
                _pool.Push(item);
            }
        }
    }
}
