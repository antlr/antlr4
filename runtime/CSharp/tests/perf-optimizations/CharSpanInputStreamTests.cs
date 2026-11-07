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
    /// Tests that CharSpanInputStream behaves identically to AntlrInputStream
    /// for all ICharStream operations.
    /// </summary>
    public class CharSpanInputStreamTests
    {
        private static CharSpanInputStream S(string s)
        {
            var c = s.ToCharArray();
            return new CharSpanInputStream(c, c.Length);
        }

        // ── Constructor tests ────────────────────────────────────────────

        [Fact]
        public void Ctor_String_SetsSize()
        {
            var stream = S("hello");
            Assert.Equal(5, stream.Size);
            Assert.Equal(0, stream.Index);
        }

        [Fact]
        public void Ctor_EmptyString_SizeIsZero()
        {
            var stream = S("");
            Assert.Equal(0, stream.Size);
        }

        [Fact]
        public void Ctor_NullCharArray_Throws()
        {
            Assert.Throws<ArgumentNullException>(() => new CharSpanInputStream((char[])null, 0));
        }

        [Fact]
        public void Ctor_NullTextReader_Throws()
        {
            Assert.Throws<ArgumentNullException>(() => new CharSpanInputStream((TextReader)null));
        }

        [Fact]
        public void Ctor_NullStream_Throws()
        {
            Assert.Throws<ArgumentNullException>(() => new CharSpanInputStream((Stream)null));
        }

        [Fact]
        public void Ctor_TextReader_ReadsAll()
        {
            using var reader = new StringReader("abc");
            var stream = new CharSpanInputStream(reader);
            Assert.Equal(3, stream.Size);
            Assert.Equal('a', stream.LA(1));
        }

        [Fact]
        public void Ctor_StreamWithEncoding_ReadsAll()
        {
            var bytes = Encoding.UTF8.GetBytes("xyz");
            using var ms = new MemoryStream(bytes);
            var stream = new CharSpanInputStream(ms, Encoding.UTF8);
            Assert.Equal(3, stream.Size);
        }

        [Fact]
        public void Ctor_StreamDefaultUtf8()
        {
            var bytes = Encoding.UTF8.GetBytes("test");
            using var ms = new MemoryStream(bytes);
            var stream = new CharSpanInputStream(ms);
            Assert.Equal(4, stream.Size);
        }

        // ── LA (lookahead) tests ─────────────────────────────────────────

        [Fact]
        public void LA_ReturnsCorrectCharacter()
        {
            var stream = S("abcdef");
            Assert.Equal('a', stream.LA(1));
            Assert.Equal('b', stream.LA(2));
        }

        [Fact]
        public void LA_Zero_ReturnsZero()
        {
            var stream = S("abc");
            Assert.Equal(0, stream.LA(0));
        }

        [Fact]
        public void LA_PastEnd_ReturnsEOF()
        {
            var stream = S("ab");
            Assert.Equal(IntStreamConstants.EOF, stream.LA(3));
        }

        [Fact]
        public void LA_Negative_LooksBack()
        {
            var stream = S("abcd");
            stream.Consume(); // p=1
            stream.Consume(); // p=2
            Assert.Equal('b', stream.LA(-1));
            Assert.Equal('a', stream.LA(-2));
        }

        [Fact]
        public void LA_NegativeBeforeStart_ReturnsEOF()
        {
            var stream = S("abc");
            Assert.Equal(IntStreamConstants.EOF, stream.LA(-1));
        }

        // ── Consume tests ────────────────────────────────────────────────

        [Fact]
        public void Consume_AdvancesIndex()
        {
            var stream = S("abc");
            stream.Consume();
            Assert.Equal(1, stream.Index);
            Assert.Equal('b', stream.LA(1));
        }

        [Fact]
        public void Consume_AtEOF_Throws()
        {
            var stream = S("a");
            stream.Consume();
            Assert.Throws<InvalidOperationException>(() => stream.Consume());
        }

        // ── Seek tests ───────────────────────────────────────────────────

        [Fact]
        public void Seek_Forward_MovesIndex()
        {
            var stream = S("abcdef");
            stream.Seek(3);
            Assert.Equal(3, stream.Index);
            Assert.Equal('d', stream.LA(1));
        }

        [Fact]
        public void Seek_Backward_MovesIndex()
        {
            var stream = S("abcdef");
            stream.Seek(4);
            stream.Seek(1);
            Assert.Equal(1, stream.Index);
            Assert.Equal('b', stream.LA(1));
        }

        [Fact]
        public void Seek_ToEnd()
        {
            var stream = S("abc");
            stream.Seek(3);
            Assert.Equal(3, stream.Index);
            Assert.Equal(IntStreamConstants.EOF, stream.LA(1));
        }

        // ── GetText tests ────────────────────────────────────────────────

        [Fact]
        public void GetText_ReturnsSubstring()
        {
            var stream = S("hello world");
            Assert.Equal("world", stream.GetText(Interval.Of(6, 10)));
        }

        [Fact]
        public void GetText_EntireString()
        {
            var stream = S("abc");
            Assert.Equal("abc", stream.GetText(Interval.Of(0, 2)));
        }

        [Fact]
        public void GetText_StopBeyondEnd_Clamps()
        {
            var stream = S("abc");
            // stop >= n should be clamped to n-1
            Assert.Equal("abc", stream.GetText(Interval.Of(0, 999)));
        }

        [Fact]
        public void GetText_StartBeyondEnd_ReturnsEmpty()
        {
            var stream = S("abc");
            Assert.Equal(string.Empty, stream.GetText(Interval.Of(999, 1000)));
        }

        // ── Reset / Mark / Release ───────────────────────────────────────

        [Fact]
        public void Reset_RestoresIndexToZero()
        {
            var stream = S("abc");
            stream.Consume();
            stream.Consume();
            stream.Reset();
            Assert.Equal(0, stream.Index);
        }

        [Fact]
        public void Mark_ReturnsMinusOne()
        {
            var stream = S("abc");
            Assert.Equal(-1, stream.Mark());
        }

        // ── SourceName ───────────────────────────────────────────────────

        [Fact]
        public void SourceName_DefaultIsUnknown()
        {
            var stream = S("abc");
            Assert.Equal(IntStreamConstants.UnknownSourceName, stream.SourceName);
        }

        [Fact]
        public void FromPath_SetsSourceName()
        {
            var tmp = Path.GetTempFileName();
            try
            {
                File.WriteAllText(tmp, "file content");
                var stream = CharSpanInputStream.FromPath(tmp);
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
            var stream = S("hello");
            Assert.Equal("hello", stream.ToString());
        }

        // ── Behavioral parity with AntlrInputStream ──────────────────────

        [Theory]
        [InlineData("")]
        [InlineData("a")]
        [InlineData("hello world")]
        [InlineData("line1\nline2\r\nline3")]
        [InlineData("unicode: \u00E9\u00F1\u00FC")] // BMP chars
        public void Parity_WithAntlrInputStream(string input)
        {
            var antlr = new AntlrInputStream(input);
            var chars = input.ToCharArray(); var span = new CharSpanInputStream(chars, chars.Length);

            Assert.Equal(antlr.Size, span.Size);
            Assert.Equal(antlr.ToString(), span.ToString());

            // Walk through every character via LA
            for (int i = 0; i < input.Length; i++)
            {
                Assert.Equal(antlr.LA(1), span.LA(1));
                antlr.Consume();
                span.Consume();
            }
            Assert.Equal(IntStreamConstants.EOF, antlr.LA(1));
            Assert.Equal(IntStreamConstants.EOF, span.LA(1));

            // GetText on full range
            if (input.Length > 0)
            {
                antlr.Reset();
                span.Reset();
                var interval = Interval.Of(0, input.Length - 1);
                Assert.Equal(antlr.GetText(interval), span.GetText(interval));
            }
        }
    }
}
