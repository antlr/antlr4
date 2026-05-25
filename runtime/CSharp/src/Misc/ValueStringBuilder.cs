/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
#if NETSTANDARD2_0_OR_GREATER || NET8_0_OR_GREATER
using System;
using System.Buffers;
using System.Runtime.CompilerServices;

namespace Antlr4.Runtime.Misc
{
    /// <summary>
    /// A stack-allocated string builder that avoids heap allocation for short strings.
    /// Based on the pattern from dotnet/runtime (System.Text.ValueStringBuilder).
    /// <para>Falls back to <see cref="ArrayPool{T}"/> for strings that exceed
    /// the initial stack-allocated buffer.</para>
    /// </summary>
    internal ref struct ValueStringBuilder
    {
        private char[] _arrayToReturnToPool;
        private Span<char> _chars;
        private int _pos;

        public ValueStringBuilder(Span<char> initialBuffer)
        {
            _arrayToReturnToPool = null;
            _chars = initialBuffer;
            _pos = 0;
        }

        public int Length => _pos;

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public void Append(char c)
        {
            int pos = _pos;
            Span<char> chars = _chars;
            if ((uint)pos < (uint)chars.Length)
            {
                chars[pos] = c;
                _pos = pos + 1;
            }
            else
            {
                GrowAndAppend(c);
            }
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public void Append(string s)
        {
            if (s == null) return;

            int pos = _pos;
            // Very common case: single-char string
            if (s.Length == 1 && (uint)pos < (uint)_chars.Length)
            {
                _chars[pos] = s[0];
                _pos = pos + 1;
            }
            else
            {
                AppendSlow(s);
            }
        }

        private void AppendSlow(string s)
        {
            int pos = _pos;
            if (pos > _chars.Length - s.Length)
            {
                Grow(s.Length);
            }

            s.AsSpan().CopyTo(_chars.Slice(_pos));
            _pos += s.Length;
        }

        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public void Append(ReadOnlySpan<char> value)
        {
            int pos = _pos;
            if (pos > _chars.Length - value.Length)
            {
                Grow(value.Length);
            }

            value.CopyTo(_chars.Slice(_pos));
            _pos += value.Length;
        }

        [MethodImpl(MethodImplOptions.NoInlining)]
        private void GrowAndAppend(char c)
        {
            Grow(1);
            Append(c);
        }

        /// <summary>
        /// Resize the internal buffer. The new capacity is the maximum of
        /// (current * 2) and (current + additionalCapacity).
        /// </summary>
        [MethodImpl(MethodImplOptions.NoInlining)]
        private void Grow(int additionalCapacityBeyondPos)
        {
            // Same growth logic as dotnet/runtime ValueStringBuilder
            int newCapacity = (int)Math.Max(
                (uint)(_pos + additionalCapacityBeyondPos),
                Math.Min((uint)_chars.Length * 2, 0x3FFFFFDF));

            char[] poolArray = ArrayPool<char>.Shared.Rent(Math.Max(newCapacity, 256));

            _chars.Slice(0, _pos).CopyTo(poolArray);

            char[] toReturn = _arrayToReturnToPool;
            _chars = _arrayToReturnToPool = poolArray;
            if (toReturn != null)
            {
                ArrayPool<char>.Shared.Return(toReturn);
            }
        }

        public override string ToString()
        {
            string s = _chars.Slice(0, _pos).ToString();
            Dispose();
            return s;
        }

        /// <summary>
        /// Returns the pooled array (if any) back to the <see cref="ArrayPool{T}"/>.
        /// </summary>
        [MethodImpl(MethodImplOptions.AggressiveInlining)]
        public void Dispose()
        {
            char[] toReturn = _arrayToReturnToPool;
            this = default; // defensive clear
            if (toReturn != null)
            {
                ArrayPool<char>.Shared.Return(toReturn);
            }
        }
    }
}
#endif
