namespace Sharpen
{
    using System;

    public class CRC32 : Checksum
    {
        private uint _crc;

        public long Value
        {
            get
            {
                return (int)_crc;
            }
        }

        public void Reset()
        {
            _crc = 0;
        }

        public void Update(byte[] buffer, int offset, int length)
        {
            if (buffer == null)
                throw new ArgumentNullException("buffer");
            if (offset < 0)
                throw new ArgumentOutOfRangeException("offset");
            if (length < 0)
                throw new ArgumentOutOfRangeException("length");
            if (offset > buffer.Length || length > buffer.Length || offset + length > buffer.Length)
                throw new ArgumentException();

            unsafe
            {
                fixed (byte* data = buffer)
                {
                    _crc = crc32(_crc, data + offset, (uint)length);
                }
            }
        }

        public void Update(int byteValue)
        {
            byte value = (byte)byteValue;
            unsafe
            {
                _crc = crc32(_crc, &value, 1);
            }
        }

        /*
         * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
         *
         * This code is free software; you can redistribute it and/or modify it
         * under the terms of the GNU General Public License version 2 only, as
         * published by the Free Software Foundation.  Oracle designates this
         * particular file as subject to the "Classpath" exception as provided
         * by Oracle in the LICENSE file that accompanied this code.
         *
         * This code is distributed in the hope that it will be useful, but WITHOUT
         * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
         * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
         * version 2 for more details (a copy is included in the LICENSE file that
         * accompanied this code).
         *
         * You should have received a copy of the GNU General Public License version
         * 2 along with this work; if not, write to the Free Software Foundation,
         * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
         *
         * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
         * or visit www.oracle.com if you need additional information or have any
         * questions.
         */

        /* crc32.c -- compute the CRC-32 of a data stream
         * Copyright (C) 1995-2005 Mark Adler
         * For conditions of distribution and use, see copyright notice in zlib.h
         *
         * Thanks to Rodney Brown <rbrown64@csc.com.au> for his contribution of faster
         * CRC methods: exclusive-oring 32 bits of data at a time, and pre-computing
         * tables for updating the shift register in one step with three exclusive-ors
         * instead of four steps with four exclusive-ors.  This results in about a
         * factor of two increase in speed on a Power PC G4 (PPC7455) using gcc -O3.
         */

        /* @(#) $Id$ */

        /*
          Note on the use of DYNAMIC_CRC_TABLE: there is no mutex or semaphore
          protection on the static variables used to control the first-use generation
          of the crc tables.  Therefore, if you #define DYNAMIC_CRC_TABLE, you should
          first call get_crc_table() to initialize the tables before allowing more than
          one thread to use crc32().
         */

        /* Definitions for doing the crc four data bytes at a time. */

        private static uint REV(uint w)
        {
            return (w >> 24) + ((w >> 8) & 0xff00) + ((w & 0xff00) << 8) + ((w & 0xff) << 24);
        }

        private const uint TBLS = 8;

        /* Local functions for crc concatenation */

        private static readonly uint[][] crc_table;

        static CRC32()
        {
            crc_table = new uint[TBLS][];
            for (int i = 0; i < TBLS; i++)
                crc_table[i] = new uint[256];

            make_crc_table();
        }

        /*
          Generate tables for a byte-wise 32-bit CRC calculation on the polynomial:
          x^32+x^26+x^23+x^22+x^16+x^12+x^11+x^10+x^8+x^7+x^5+x^4+x^2+x+1.

          Polynomials over GF(2) are represented in binary, one bit per coefficient,
          with the lowest powers in the most significant bit.  Then adding polynomials
          is just exclusive-or, and multiplying a polynomial by x is a right shift by
          one.  If we call the above polynomial p, and represent a byte as the
          polynomial q, also with the lowest power in the most significant bit (so the
          byte 0xb1 is the polynomial x^7+x^3+x+1), then the CRC is (q*x^32) mod p,
          where a mod b means the remainder after dividing a by b.

          This calculation is done using the shift-register method of multiplying and
          taking the remainder.  The register is initialized to zero, and for each
          incoming bit, x^32 is added mod p to the register if the bit is a one (where
          x^32 mod p is p+x^32 = x^26+...+1), and the register is multiplied mod p by
          x (which is shifting right by one and adding x^32 mod p if the bit shifted
          out is a one).  We start with the highest power (least significant bit) of
          q and repeat for all eight bits of q.

          The first table is simply the CRC of all possible eight bit values.  This is
          all the information needed to generate CRCs on data a byte at a time for all
          combinations of CRC register values and incoming bytes.  The remaining tables
          allow for word-at-a-time CRC calculation for both big-endian and little-
          endian machines, where a word is four bytes.
        */
        private static void make_crc_table()
        {
            /* terms of polynomial defining this crc (except x^32): */
            byte[] p = { 0, 1, 2, 4, 5, 7, 8, 10, 11, 12, 16, 22, 23, 26 };

            /* make exclusive-or pattern from polynomial (0xedb88320UL) */
            uint poly = 0U;
            for (int n = 0; n < p.Length; n++)
                poly |= 1U << (31 - p[n]);

            /* generate a crc for every 8-bit value */
            for (int n = 0; n < 256; n++)
            {
                uint c = (uint)n;
                for (int k = 0; k < 8; k++)
                    c = (c & 1) != 0 ? poly ^ (c >> 1) : c >> 1;

                crc_table[0][n] = c;
            }

            /* generate crc for each value followed by one, two, and three zeros,
               and then the byte reversal of those as well as the first table */
            for (int n = 0; n < 256; n++)
            {
                uint c = crc_table[0][n];
                crc_table[4][n] = REV(c);
                for (int k = 1; k < 4; k++)
                {
                    c = crc_table[0][c & 0xff] ^ (c >> 8);
                    crc_table[k][n] = c;
                    crc_table[k + 4][n] = REV(c);
                }
            }
        }

        /* =========================================================================
         * This function can be used by asm versions of crc32()
         */
        private static uint[][] get_crc_table()
        {
            return crc_table;
        }

        ///* ========================================================================= */
        private static unsafe void DO1(ref uint crc, ref byte* buf)
        {
            crc = crc_table[0][(crc ^ (*buf++)) & 0xFF] ^ (crc >> 8);
        }

        private static unsafe void DO8(ref uint crc, ref byte* buf)
        {
            DO1(ref crc, ref buf);
            DO1(ref crc, ref buf);
            DO1(ref crc, ref buf);
            DO1(ref crc, ref buf);
            DO1(ref crc, ref buf);
            DO1(ref crc, ref buf);
            DO1(ref crc, ref buf);
            DO1(ref crc, ref buf);
        }

        /* ========================================================================= */
        private static unsafe uint crc32(uint crc, byte* buf, uint len)
        {
            if (buf == null)
                return 0;

            if (BitConverter.IsLittleEndian)
                return (uint)crc32_little(crc, buf, len);
            else
                return (uint)crc32_big(crc, buf, len);
        }

        ///* ========================================================================= */
        private static unsafe void DOLIT4(ref uint c, ref uint* buf4)
        {
            c ^= *buf4++;
            c = crc_table[3][c & 0xff] ^ crc_table[2][(c >> 8) & 0xff] ^
                crc_table[1][(c >> 16) & 0xff] ^ crc_table[0][c >> 24];
        }

        private static unsafe void DOLIT32(ref uint c, ref uint* buf4)
        {
            DOLIT4(ref c, ref buf4);
            DOLIT4(ref c, ref buf4);
            DOLIT4(ref c, ref buf4);
            DOLIT4(ref c, ref buf4);
            DOLIT4(ref c, ref buf4);
            DOLIT4(ref c, ref buf4);
            DOLIT4(ref c, ref buf4);
            DOLIT4(ref c, ref buf4);
        }

        /* ========================================================================= */
        private static unsafe uint crc32_little(uint crc, byte* buf, uint len)
        {
            uint c = (uint)crc;
            c = ~c;
            while (len != 0 && ((uint)buf & 3) != 0)
            {
                c = crc_table[0][(c ^ *buf++) & 0xff] ^ (c >> 8);
                len--;
            }

            uint* buf4 = (uint*)buf;
            while (len >= 32)
            {
                DOLIT32(ref c, ref buf4);
                len -= 32;
            }

            while (len >= 4)
            {
                DOLIT4(ref c, ref buf4);
                len -= 4;
            }

            buf = (byte*)buf4;

            if (len != 0)
            {
                do
                {
                    c = crc_table[0][(c ^ *buf++) & 0xff] ^ (c >> 8);
                } while (--len != 0);
            }

            c = ~c;
            return (uint)c;
        }

        ///* ========================================================================= */
        private static unsafe void DOBIG4(ref uint c, ref uint* buf4)
        {
            c ^= *++buf4;
            c = crc_table[4][c & 0xff] ^ crc_table[5][(c >> 8) & 0xff] ^
                crc_table[6][(c >> 16) & 0xff] ^ crc_table[7][c >> 24];
        }

        private static unsafe void DOBIG32(ref uint c, ref uint* buf4)
        {
            DOBIG4(ref c, ref buf4);
            DOBIG4(ref c, ref buf4);
            DOBIG4(ref c, ref buf4);
            DOBIG4(ref c, ref buf4);
            DOBIG4(ref c, ref buf4);
            DOBIG4(ref c, ref buf4);
            DOBIG4(ref c, ref buf4);
            DOBIG4(ref c, ref buf4);
        }

        /* ========================================================================= */
        private static unsafe uint crc32_big(uint crc, byte* buf, uint len)
        {
            uint c;
            uint* buf4;

            c = REV((uint)crc);
            c = ~c;
            while (len != 0 && ((uint)buf & 3) != 0)
            {
                c = crc_table[4][(c >> 24) ^ *buf++] ^ (c << 8);
                len--;
            }

            buf4 = (uint*)buf;
            buf4--;
            while (len >= 32)
            {
                DOBIG32(ref c, ref buf4);
                len -= 32;
            }

            while (len >= 4)
            {
                DOBIG4(ref c, ref buf4);
                len -= 4;
            }

            buf4++;
            buf = (byte*)buf4;

            if (len != 0)
                do
                {
                    c = crc_table[4][(c >> 24) ^ *buf++] ^ (c << 8);
                } while (--len != 0);
            c = ~c;
            return (uint)(REV(c));
        }

        private const int GF2_DIM = 32;      /* dimension of GF(2) vectors (length of CRC) */

        /* ========================================================================= */
        private static unsafe uint gf2_matrix_times(uint* mat, uint vec)
        {
            uint sum = 0;
            while (vec != 0)
            {
                if ((vec & 1) != 0)
                    sum ^= *mat;

                vec >>= 1;
                mat++;
            }

            return sum;
        }

        /* ========================================================================= */
        private static unsafe void gf2_matrix_square(uint* square, uint* mat)
        {
            for (int n = 0; n < GF2_DIM; n++)
                square[n] = gf2_matrix_times(mat, mat[n]);
        }

        /* ========================================================================= */
        private static unsafe uint crc32_combine(uint crc1, uint crc2, uint len2)
        {
            /* degenerate case */
            if (len2 == 0)
                return crc1;

            uint* even = stackalloc uint[GF2_DIM];    /* even-power-of-two zeros operator */
            uint* odd = stackalloc uint[GF2_DIM];     /* odd-power-of-two zeros operator */

            /* put operator for one zero bit in odd */
            odd[0] = 0xEDB88320U;           /* CRC-32 polynomial */
            uint row = 1;
            for (int n = 1; n < GF2_DIM; n++)
            {
                odd[n] = row;
                row <<= 1;
            }

            /* put operator for two zero bits in even */
            gf2_matrix_square(even, odd);

            /* put operator for four zero bits in odd */
            gf2_matrix_square(odd, even);

            /* apply len2 zeros to crc1 (first square will put the operator for one
               zero byte, eight zero bits, in even) */
            do
            {
                /* apply zeros operator for this bit of len2 */
                gf2_matrix_square(even, odd);
                if ((len2 & 1) != 0)
                    crc1 = gf2_matrix_times(even, crc1);

                len2 >>= 1;

                /* if no more bits set, then done */
                if (len2 == 0)
                    break;

                /* another iteration of the loop with odd and even swapped */
                gf2_matrix_square(odd, even);
                if ((len2 & 1) != 0)
                    crc1 = gf2_matrix_times(odd, crc1);

                len2 >>= 1;

                /* if no more bits set, then done */
            } while (len2 != 0);

            /* return combined crc */
            crc1 ^= crc2;
            return crc1;
        }
    }
}
