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
namespace Antlr4.Runtime.Sharpen
{
    using System;
    using System.Text;

    public class BitSet
    {
        private static readonly ulong[] EmptyBits = new ulong[0];
        private const int BitsPerElement = 8 * sizeof(ulong);

        private ulong[] _data = EmptyBits;

        public BitSet()
        {
        }

        public BitSet(int nbits)
        {
            if (nbits < 0)
                throw new ArgumentOutOfRangeException("nbits");

            if (nbits > 0)
            {
                int length = (nbits + BitsPerElement - 1) / BitsPerElement;
                _data = new ulong[length];
            }
        }

        private static int GetBitCount(ulong[] value)
        {
            int data = 0;
            uint size = (uint)value.Length;
            const ulong m1 = 0x5555555555555555;
            const ulong m2 = 0x3333333333333333;
            const ulong m4 = 0x0F0F0F0F0F0F0F0F;
            const ulong m8 = 0x00FF00FF00FF00FF;
            const ulong m16 = 0x0000FFFF0000FFFF;
            const ulong h01 = 0x0101010101010101;

            uint bitCount = 0;
            uint limit30 = size - size % 30;

            // 64-bit tree merging (merging3)
            for (uint i = 0; i < limit30; i += 30, data += 30)
            {
                ulong acc = 0;
                for (uint j = 0; j < 30; j += 3)
                {
                    ulong count1 = value[data + j];
                    ulong count2 = value[data + j + 1];
                    ulong half1 = value[data + j + 2];
                    ulong half2 = half1;
                    half1 &= m1;
                    half2 = (half2 >> 1) & m1;
                    count1 -= (count1 >> 1) & m1;
                    count2 -= (count2 >> 1) & m1;
                    count1 += half1;
                    count2 += half2;
                    count1 = (count1 & m2) + ((count1 >> 2) & m2);
                    count1 += (count2 & m2) + ((count2 >> 2) & m2);
                    acc += (count1 & m4) + ((count1 >> 4) & m4);
                }

                acc = (acc & m8) + ((acc >> 8) & m8);
                acc = (acc + (acc >> 16)) & m16;
                acc = acc + (acc >> 32);
                bitCount += (uint)acc;
            }

            // count the bits of the remaining bytes (MAX 29*8) using 
            // "Counting bits set, in parallel" from the "Bit Twiddling Hacks",
            // the code uses wikipedia's 64-bit popcount_3() implementation:
            // http://en.wikipedia.org/wiki/Hamming_weight#Efficient_implementation
            for (uint i = 0; i < size - limit30; i++)
            {
                ulong x = value[data + i];
                x = x - ((x >> 1) & m1);
                x = (x & m2) + ((x >> 2) & m2);
                x = (x + (x >> 4)) & m4;
                bitCount += (uint)((x * h01) >> 56);
            }

            return (int)bitCount;
        }

        private static readonly int[] index64 =
        {
            0, 47,  1, 56, 48, 27,  2, 60,
           57, 49, 41, 37, 28, 16,  3, 61,
           54, 58, 35, 52, 50, 42, 21, 44,
           38, 32, 29, 23, 17, 11,  4, 62,
           46, 55, 26, 59, 40, 36, 15, 53,
           34, 51, 20, 43, 31, 22, 10, 45,
           25, 39, 14, 33, 19, 30,  9, 24,
           13, 18,  8, 12,  7,  6,  5, 63
        };

        private static int BitScanForward(ulong value)
        {
            if (value == 0)
                return -1;

            const ulong debruijn64 = 0x03f79d71b4cb0a89;
            return index64[((value ^ (value - 1)) * debruijn64) >> 58];
        }

        public BitSet Clone()
        {
            BitSet result = new BitSet();
            result._data = (ulong[])_data.Clone();
            return result;
        }

        public void Clear(int index)
        {
            if (index < 0)
                throw new ArgumentOutOfRangeException("index");

            int element = index / BitsPerElement;
            if (element >= _data.Length)
                return;

            _data[element] &= ~(1UL << (index % BitsPerElement));
        }

        public bool Get(int index)
        {
            if (index < 0)
                throw new ArgumentOutOfRangeException("index");

            int element = index / BitsPerElement;
            if (element >= _data.Length)
                return false;

            return (_data[element] & (1UL << (index % BitsPerElement))) != 0;
        }

        public void Set(int index)
        {
            if (index < 0)
                throw new ArgumentOutOfRangeException("index");

            int element = index / BitsPerElement;
            if (element >= _data.Length)
                Array.Resize(ref _data, Math.Max(_data.Length * 2, element + 1));

            _data[element] |= 1UL << (index % BitsPerElement);
        }

        public bool IsEmpty()
        {
            for (int i = 0; i < _data.Length; i++)
            {
                if (_data[i] != 0)
                    return false;
            }

            return true;
        }

        public int Cardinality()
        {
            return GetBitCount(_data);
        }

        public int NextSetBit(int fromIndex)
        {
            if (fromIndex < 0)
                throw new ArgumentOutOfRangeException("fromIndex");

            if (IsEmpty())
                return -1;

            int i = fromIndex / BitsPerElement;
            if (i >= _data.Length)
                return -1;

            ulong current = _data[i] & ~((1UL << (fromIndex % BitsPerElement)) - 1);

            while (true)
            {
                int bit = BitScanForward(current);
                if (bit >= 0)
                    return bit + i * BitsPerElement;

                i++;
                if (i >= _data.Length)
                    break;

                current = _data[i];
            }

            return -1;
        }

        public void And(BitSet set)
        {
            if (set == null)
                throw new ArgumentNullException("set");

            int length = Math.Min(_data.Length, set._data.Length);
            for (int i = 0; i < length; i++)
                _data[i] &= set._data[i];

            for (int i = length; i < _data.Length; i++)
                _data[i] = 0;
        }

        public void Or(BitSet set)
        {
            if (set == null)
                throw new ArgumentNullException("set");

            if (set._data.Length > _data.Length)
                Array.Resize(ref _data, set._data.Length);

            for (int i = 0; i < set._data.Length; i++)
                _data[i] |= set._data[i];
        }

        public override bool Equals(object obj)
        {
            BitSet other = obj as BitSet;
            if (other == null)
                return false;

            if (IsEmpty())
                return other.IsEmpty();

            int minLength = Math.Min(_data.Length, other._data.Length);
            for (int i = 0; i < minLength; i++)
            {
                if (_data[i] != other._data[i])
                    return false;
            }

            for (int i = minLength; i < _data.Length; i++)
            {
                if (_data[i] != 0)
                    return false;
            }

            for (int i = minLength; i < other._data.Length; i++)
            {
                if (other._data[i] != 0)
                    return false;
            }

            return true;
        }

        public override int GetHashCode()
        {
            ulong result = 1;
            for (uint i = 0; i < _data.Length; i++)
            {
                if (_data[i] != 0)
                {
                    result = result * 31 ^ i;
                    result = result * 31 ^ _data[i];
                }
            }

            return result.GetHashCode();
        }

        public override string ToString()
        {
            StringBuilder builder = new StringBuilder();
            builder.Append('{');

            for (int i = NextSetBit(0); i >= 0; i = NextSetBit(i + 1))
            {
                if (builder.Length > 1)
                    builder.Append(", ");

                builder.Append(i);
            }

            builder.Append('}');
            return builder.ToString();
        }
    }
}
