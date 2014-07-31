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
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Misc
{
    /// <author>Sam Harwell</author>
    public sealed class MurmurHash
    {
        private const int DefaultSeed = 0;

        /// <summary>Initialize the hash using the default seed value.</summary>
        /// <remarks>Initialize the hash using the default seed value.</remarks>
        /// <returns>the intermediate hash value</returns>
        public static int Initialize()
        {
            return Initialize(DefaultSeed);
        }

        /// <summary>
        /// Initialize the hash using the specified
        /// <paramref name="seed"/>
        /// .
        /// </summary>
        /// <param name="seed">the seed</param>
        /// <returns>the intermediate hash value</returns>
        public static int Initialize(int seed)
        {
            return seed;
        }

        /// <summary>
        /// Update the intermediate hash value for the next input
        /// <paramref name="value"/>
        /// .
        /// </summary>
        /// <param name="hash">the intermediate hash value</param>
        /// <param name="value">the value to add to the current hash</param>
        /// <returns>the updated intermediate hash value</returns>
        public static int Update(int hash, int value)
        {
            int c1 = unchecked((int)(0xCC9E2D51));
            int c2 = unchecked((int)(0x1B873593));
            int r1 = 15;
            int r2 = 13;
            int m = 5;
            int n = unchecked((int)(0xE6546B64));
            int k = value;
            k = k * c1;
            k = (k << r1) | ((int)(((uint)k) >> (32 - r1)));
            k = k * c2;
            hash = hash ^ k;
            hash = (hash << r2) | ((int)(((uint)hash) >> (32 - r2)));
            hash = hash * m + n;
            return hash;
        }

        /// <summary>
        /// Update the intermediate hash value for the next input
        /// <paramref name="value"/>
        /// .
        /// </summary>
        /// <param name="hash">the intermediate hash value</param>
        /// <param name="value">the value to add to the current hash</param>
        /// <returns>the updated intermediate hash value</returns>
        public static int Update(int hash, object value)
        {
            return Update(hash, value != null ? value.GetHashCode() : 0);
        }

        /// <summary>
        /// Apply the final computation steps to the intermediate value
        /// <paramref name="hash"/>
        /// to form the final result of the MurmurHash 3 hash function.
        /// </summary>
        /// <param name="hash">the intermediate hash value</param>
        /// <param name="numberOfWords">the number of integer values added to the hash</param>
        /// <returns>the final hash result</returns>
        public static int Finish(int hash, int numberOfWords)
        {
            hash = hash ^ (numberOfWords * 4);
            hash = hash ^ ((int)(((uint)hash) >> 16));
            hash = hash * unchecked((int)(0x85EBCA6B));
            hash = hash ^ ((int)(((uint)hash) >> 13));
            hash = hash * unchecked((int)(0xC2B2AE35));
            hash = hash ^ ((int)(((uint)hash) >> 16));
            return hash;
        }

        /// <summary>
        /// Utility function to compute the hash code of an array using the
        /// MurmurHash algorithm.
        /// </summary>
        /// <remarks>
        /// Utility function to compute the hash code of an array using the
        /// MurmurHash algorithm.
        /// </remarks>
        /// <param name="data">the array data</param>
        /// <param name="seed">the seed for the MurmurHash algorithm</param>
        /// <returns>the hash code of the data</returns>
        public static int HashCode<T>(T[] data, int seed)
        {
            int hash = Initialize(seed);
            foreach (T value in data)
            {
                hash = Update(hash, value);
            }
            hash = Finish(hash, data.Length);
            return hash;
        }

        private MurmurHash()
        {
        }
    }
}
