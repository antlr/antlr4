/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using Antlr4.Runtime;
using Antlr4.Runtime.Misc;
using Xunit;

namespace PerfOptimizations
{
    /// <summary>
    /// Tests that OptimizedToken behaves identically to CommonToken
    /// for all IToken / IWritableToken operations, including deferred
    /// text materialization.
    /// </summary>
    public class OptimizedTokenTests
    {
        // Helper: create a mock source for tokens backed by a char stream
        private static Tuple<ITokenSource, ICharStream> MakeTupleSource(string text)
        {
            var stream = new AntlrInputStream(text);
            return Tuple.Create<ITokenSource, ICharStream>(null, stream);
        }

        private static (ITokenSource, ICharStream) MakeValueTupleSource(string text)
        {
            var stream = new AntlrInputStream(text);
            return (null, stream);
        }

        // ── Constructor: type-only ───────────────────────────────────────

        [Fact]
        public void Ctor_TypeOnly_SetsDefaults()
        {
            var token = new OptimizedToken(42);
            Assert.Equal(42, token.Type);
            Assert.Equal(TokenConstants.DefaultChannel, token.Channel);
            Assert.Equal(-1, token.Column);
            Assert.Equal(-1, token.TokenIndex);
            Assert.Null(token.TokenSource);
            Assert.Null(token.InputStream);
        }

        // ── Constructor: type + text ─────────────────────────────────────

        [Fact]
        public void Ctor_TypeAndText_SetsText()
        {
            var token = new OptimizedToken(5, "hello");
            Assert.Equal(5, token.Type);
            Assert.Equal("hello", token.Text);
        }

        [Fact]
        public void Ctor_TypeAndText_TextIsExplicit()
        {
            // Explicit text should survive even if start/stop are 0
            var token = new OptimizedToken(1, "explicit");
            Assert.Equal("explicit", token.Text);
        }

        // ── Constructor: full source ─────────────────────────────────────

        [Fact]
        public void Ctor_Source_DefersText()
        {
            var stream = new AntlrInputStream("hello world");
            var source = (default(ITokenSource), (ICharStream)stream);
            var token = new OptimizedToken(source, type: 1, channel: 0, start: 6, stop: 10);
            // Text should be deferred — accessing it should materialize "world"
            Assert.Equal("world", token.Text);
        }

        [Fact]
        public void Ctor_Source_SetsChannelAndType()
        {
            var source = MakeValueTupleSource("x");
            var token = new OptimizedToken(source, type: 99, channel: 3, start: 0, stop: 0);
            Assert.Equal(99, token.Type);
            Assert.Equal(3, token.Channel);
        }

        // ── Deferred text materialization ────────────────────────────────

        [Fact]
        public void Text_DeferredUntilAccessed()
        {
            var stream = new AntlrInputStream("abcdef");
            var source = (default(ITokenSource), (ICharStream)stream);
            var token = new OptimizedToken(source, type: 1, channel: 0, start: 2, stop: 4);

            // The token was constructed — text should be deferred
            // Accessing .Text materializes it
            Assert.Equal("cde", token.Text);
        }

        [Fact]
        public void Text_CachedAfterFirstAccess()
        {
            var stream = new AntlrInputStream("abcdef");
            var source = (default(ITokenSource), (ICharStream)stream);
            var token = new OptimizedToken(source, type: 1, channel: 0, start: 0, stop: 2);

            string first = token.Text;
            string second = token.Text;
            Assert.Same(first, second); // same reference = cached
        }

        [Fact]
        public void Text_ExplicitOverridesDeferred()
        {
            var stream = new AntlrInputStream("abcdef");
            var source = (default(ITokenSource), (ICharStream)stream);
            var token = new OptimizedToken(source, type: 1, channel: 0, start: 0, stop: 2);

            token.Text = "override";
            Assert.Equal("override", token.Text);
        }

        [Fact]
        public void Text_NoStream_ReturnsNull()
        {
            var token = new OptimizedToken(1);
            // No source stream, no explicit text
            Assert.Null(token.Text);
        }

        [Fact]
        public void Text_BeyondStreamSize_ReturnsEOF()
        {
            var stream = new AntlrInputStream("ab");
            var source = (default(ITokenSource), (ICharStream)stream);
            var token = new OptimizedToken(source, type: 1, channel: 0, start: 999, stop: 1000);
            Assert.Equal("<EOF>", token.Text);
        }

        // ── IWritableToken setters ───────────────────────────────────────

        [Fact]
        public void Setters_Work()
        {
            var token = new OptimizedToken(1);
            token.Type = 42;
            token.Channel = 7;
            token.Line = 10;
            token.Column = 5;
            token.StartIndex = 100;
            token.StopIndex = 200;
            token.TokenIndex = 50;

            Assert.Equal(42, token.Type);
            Assert.Equal(7, token.Channel);
            Assert.Equal(10, token.Line);
            Assert.Equal(5, token.Column);
            Assert.Equal(100, token.StartIndex);
            Assert.Equal(200, token.StopIndex);
            Assert.Equal(50, token.TokenIndex);
        }

        // ── Copy constructor ─────────────────────────────────────────────

        [Fact]
        public void CopyCtor_FromOptimizedToken_PreservesDeferred()
        {
            var stream = new AntlrInputStream("hello world");
            var source = (default(ITokenSource), (ICharStream)stream);
            var original = new OptimizedToken(source, type: 1, channel: 0, start: 6, stop: 10);

            var copy = new OptimizedToken(original);
            Assert.Equal("world", copy.Text);
            Assert.Equal(1, copy.Type);
            Assert.Equal(6, copy.StartIndex);
            Assert.Equal(10, copy.StopIndex);
        }

        [Fact]
        public void CopyCtor_FromCommonToken_Works()
        {
            var stream = new AntlrInputStream("hello world");
            var source = Tuple.Create<ITokenSource, ICharStream>(null, stream);
            var common = new CommonToken(source, type: 1, channel: 0, start: 0, stop: 4);

            var optimized = new OptimizedToken(common);
            Assert.Equal("hello", optimized.Text);
            Assert.Equal(1, optimized.Type);
            Assert.Equal(0, optimized.StartIndex);
            Assert.Equal(4, optimized.StopIndex);
        }

        [Fact]
        public void CopyCtor_FromCommonToken_ExplicitText()
        {
            var common = new CommonToken(1, "explicit");
            var optimized = new OptimizedToken(common);
            Assert.Equal("explicit", optimized.Text);
        }

        // ── ToString ─────────────────────────────────────────────────────

        [Fact]
        public void ToString_Format_MatchesCommonToken()
        {
            var stream = new AntlrInputStream("hello");
            var tupleSource = Tuple.Create<ITokenSource, ICharStream>(null, stream);
            var vtSource = (default(ITokenSource), (ICharStream)stream);

            var common = new CommonToken(tupleSource, type: 1, channel: 0, start: 0, stop: 4);
            common.TokenIndex = 0;

            var optimized = new OptimizedToken(vtSource, type: 1, channel: 0, start: 0, stop: 4);
            optimized.TokenIndex = 0;

            // Both should produce the same ToString format
            Assert.Equal(common.ToString(), optimized.ToString());
        }

        [Fact]
        public void ToString_EscapesNewlines()
        {
            var token = new OptimizedToken(1, "line1\nline2\r\ttab");
            string s = token.ToString();
            Assert.Contains("\\n", s);
            Assert.Contains("\\r", s);
            Assert.Contains("\\t", s);
        }

        [Fact]
        public void ToString_WithChannel()
        {
            var token = new OptimizedToken(1, "x");
            token.Channel = 2;
            string s = token.ToString();
            Assert.Contains("channel=2", s);
        }

        // ── Parity with CommonToken ──────────────────────────────────────

        [Theory]
        [InlineData("simple")]
        [InlineData("with spaces and stuff")]
        [InlineData("")]
        public void Parity_TextRetrieval(string input)
        {
            if (input.Length == 0) return;

            var stream1 = new AntlrInputStream(input);
            var stream2 = new AntlrInputStream(input);

            var commonSource = Tuple.Create<ITokenSource, ICharStream>(null, stream1);
            var optSource = (default(ITokenSource), (ICharStream)stream2);

            var common = new CommonToken(commonSource, 1, 0, 0, input.Length - 1);
            var opt = new OptimizedToken(optSource, 1, 0, 0, input.Length - 1);

            Assert.Equal(common.Text, opt.Text);
        }
    }
}
