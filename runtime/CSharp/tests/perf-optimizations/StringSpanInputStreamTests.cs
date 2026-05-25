/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.IO;
using System.Text;
using Antlr4.Runtime;
using Antlr4.Runtime.Misc;
using Xunit;

namespace PerfOptimizations
{
    /// <summary>
    /// Tests that StringSpanInputStream behaves identically to AntlrInputStream
    /// for all ICharStream operations, and that its string-backed construction
    /// avoids any ToCharArray() copy.
    /// </summary>
    public class StringSpanInputStreamTests
    {
        // ── Constructor tests ────────────────────────────────────────────

        [Fact]
        public void Ctor_String_SetsSize()
        {
            var stream = new StringSpanInputStream("hello");
            Assert.Equal(5, stream.Size);
            Assert.Equal(0, stream.Index);
        }

        [Fact]
        public void Ctor_EmptyString_SizeIsZero()
        {
            var stream = new StringSpanInputStream("");
            Assert.Equal(0, stream.Size);
        }

        [Fact]
        public void Ctor_NullString_Throws()
        {
            Assert.Throws<ArgumentNullException>(() => new StringSpanInputStream(null));
        }

        // ── LA (lookahead) tests ─────────────────────────────────────────

        [Fact]
        public void LA_ReturnsCorrectCharacter()
        {
            var stream = new StringSpanInputStream("abcdef");
            Assert.Equal('a', stream.LA(1));
            Assert.Equal('b', stream.LA(2));
        }

        [Fact]
        public void LA_Zero_ReturnsZero()
        {
            var stream = new StringSpanInputStream("abc");
            Assert.Equal(0, stream.LA(0));
        }

        [Fact]
        public void LA_PastEnd_ReturnsEOF()
        {
            var stream = new StringSpanInputStream("ab");
            Assert.Equal(IntStreamConstants.EOF, stream.LA(3));
        }

        [Fact]
        public void LA_Negative_LooksBack()
        {
            var stream = new StringSpanInputStream("abcd");
            stream.Consume(); // p=1
            stream.Consume(); // p=2
            Assert.Equal('b', stream.LA(-1));
            Assert.Equal('a', stream.LA(-2));
        }

        [Fact]
        public void LA_NegativeBeforeStart_ReturnsEOF()
        {
            var stream = new StringSpanInputStream("abc");
            Assert.Equal(IntStreamConstants.EOF, stream.LA(-1));
        }

        // ── Consume tests ────────────────────────────────────────────────

        [Fact]
        public void Consume_AdvancesIndex()
        {
            var stream = new StringSpanInputStream("abc");
            stream.Consume();
            Assert.Equal(1, stream.Index);
            Assert.Equal('b', stream.LA(1));
        }

        [Fact]
        public void Consume_AtEOF_Throws()
        {
            var stream = new StringSpanInputStream("a");
            stream.Consume();
            Assert.Throws<InvalidOperationException>(() => stream.Consume());
        }

        // ── Seek tests ───────────────────────────────────────────────────

        [Fact]
        public void Seek_Forward_MovesIndex()
        {
            var stream = new StringSpanInputStream("abcdef");
            stream.Seek(3);
            Assert.Equal(3, stream.Index);
            Assert.Equal('d', stream.LA(1));
        }

        [Fact]
        public void Seek_Backward_MovesIndex()
        {
            var stream = new StringSpanInputStream("abcdef");
            stream.Seek(4);
            stream.Seek(1);
            Assert.Equal(1, stream.Index);
            Assert.Equal('b', stream.LA(1));
        }

        [Fact]
        public void Seek_ToEnd()
        {
            var stream = new StringSpanInputStream("abc");
            stream.Seek(3);
            Assert.Equal(3, stream.Index);
            Assert.Equal(IntStreamConstants.EOF, stream.LA(1));
        }

        // ── GetText tests ────────────────────────────────────────────────

        [Fact]
        public void GetText_ReturnsSubstring()
        {
            var stream = new StringSpanInputStream("hello world");
            Assert.Equal("world", stream.GetText(Interval.Of(6, 10)));
        }

        [Fact]
        public void GetText_EntireString()
        {
            var stream = new StringSpanInputStream("abc");
            Assert.Equal("abc", stream.GetText(Interval.Of(0, 2)));
        }

        [Fact]
        public void GetText_StopBeyondEnd_Clamps()
        {
            var stream = new StringSpanInputStream("abc");
            Assert.Equal("abc", stream.GetText(Interval.Of(0, 999)));
        }

        [Fact]
        public void GetText_StartBeyondEnd_ReturnsEmpty()
        {
            var stream = new StringSpanInputStream("abc");
            Assert.Equal(string.Empty, stream.GetText(Interval.Of(999, 1000)));
        }

        // ── Reset / Mark / Release ───────────────────────────────────────

        [Fact]
        public void Reset_RestoresIndexToZero()
        {
            var stream = new StringSpanInputStream("abc");
            stream.Consume();
            stream.Consume();
            stream.Reset();
            Assert.Equal(0, stream.Index);
        }

        [Fact]
        public void Mark_ReturnsMinusOne()
        {
            var stream = new StringSpanInputStream("abc");
            Assert.Equal(-1, stream.Mark());
        }

        // ── SourceName ───────────────────────────────────────────────────

        [Fact]
        public void SourceName_DefaultIsUnknown()
        {
            var stream = new StringSpanInputStream("abc");
            Assert.Equal(IntStreamConstants.UnknownSourceName, stream.SourceName);
        }

        [Fact]
        public void FromPath_SetsSourceName()
        {
            var tmp = Path.GetTempFileName();
            try
            {
                File.WriteAllText(tmp, "file content");
                var stream = StringSpanInputStream.FromPath(tmp);
                Assert.Equal(tmp, stream.SourceName);
                Assert.Equal(12, stream.Size);
            }
            finally
            {
                File.Delete(tmp);
            }
        }

        // ── ToString ─────────────────────────────────────────────────────

        [Fact]
        public void ToString_ReturnsFullInput()
        {
            var stream = new StringSpanInputStream("hello");
            Assert.Equal("hello", stream.ToString());
        }

        [Fact]
        public void ToString_IsOriginalStringReference()
        {
            // ToString() on a string-backed stream should return the exact
            // same string object — no allocation, no copy.
            var input = "hello world";
            var stream = new StringSpanInputStream(input);
            Assert.Same(input, stream.ToString());
        }

        // ── Behavioral parity with AntlrInputStream ──────────────────────

        [Theory]
        [InlineData("")]
        [InlineData("a")]
        [InlineData("hello world")]
        [InlineData("line1\nline2\r\nline3")]
        [InlineData("unicode: \u00E9\u00F1\u00FC")] // BMP Latin Extended
        [InlineData("CJK: \u4E2D\u6587")]           // BMP CJK Unified Ideographs
        [InlineData("Greek: \u03B1\u03B2\u03B3")]   // BMP Greek
        [InlineData("Arabic: \u0645\u0631\u062D\u0628\u0627")] // BMP Arabic
        public void Parity_WithAntlrInputStream(string input)
        {
            var antlr = new AntlrInputStream(input);
            var str = new StringSpanInputStream(input);

            Assert.Equal(antlr.Size, str.Size);
            Assert.Equal(antlr.ToString(), str.ToString());

            // Walk through every character via LA
            for (int i = 0; i < input.Length; i++)
            {
                Assert.Equal(antlr.LA(1), str.LA(1));
                antlr.Consume();
                str.Consume();
            }
            Assert.Equal(IntStreamConstants.EOF, antlr.LA(1));
            Assert.Equal(IntStreamConstants.EOF, str.LA(1));

            // GetText on full range
            if (input.Length > 0)
            {
                antlr.Reset();
                str.Reset();
                var interval = Interval.Of(0, input.Length - 1);
                Assert.Equal(antlr.GetText(interval), str.GetText(interval));
            }
        }
    }
}
