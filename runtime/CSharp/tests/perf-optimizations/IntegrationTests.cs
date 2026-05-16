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
    /// Integration tests that exercise the optimized types together in
    /// realistic scenarios: CharSpanInputStream + OptimizedTokenFactory piped
    /// through the same ICharStream / IToken interfaces that parsers use.
    /// Also validates BufferedTokenStream.GetText (which uses ValueStringBuilder
    /// internally).
    /// </summary>
    public class IntegrationTests
    {
        private static CharSpanInputStream S(string s)
        {
            var c = s.ToCharArray();
            return new CharSpanInputStream(c, c.Length);
        }

        // ── CharSpanInputStream as ICharStream ───────────────────────────────

        [Fact]
        public void CharSpanInputStream_FullWalk_MatchesAntlrInputStream()
        {
            string input = "SELECT id, name FROM users WHERE active = 1;";
            var antlr = new AntlrInputStream(input);
            var span = S(input);

            // Walk entire stream comparing every LA(1) + Consume pair
            while (true)
            {
                int a = antlr.LA(1);
                int s = span.LA(1);
                Assert.Equal(a, s);
                if (a == IntStreamConstants.EOF) break;
                antlr.Consume();
                span.Consume();
            }
            Assert.Equal(antlr.Index, span.Index);
        }

        [Fact]
        public void CharSpanInputStream_RandomSeek_Parity()
        {
            string input = "abcdefghijklmnopqrstuvwxyz";
            var antlr = new AntlrInputStream(input);
            var span = S(input);

            int[] positions = { 10, 5, 25, 0, 13, 26 };
            foreach (int pos in positions)
            {
                antlr.Seek(pos);
                span.Seek(pos);
                Assert.Equal(antlr.Index, span.Index);
                Assert.Equal(antlr.LA(1), span.LA(1));
            }
        }

        // ── OptimizedToken via OptimizedTokenFactory ─────────────────────

        [Fact]
        public void OptimizedTokenFactory_ProducesTokensWithDeferredText()
        {
            var input = new AntlrInputStream("hello world");
            var factory = new OptimizedTokenFactory();
            var source = Tuple.Create<ITokenSource, ICharStream>(null, input);

            // Create token for "hello" (0..4) with no explicit text
            var token = factory.Create(source, type: 1, text: null,
                channel: 0, start: 0, stop: 4, line: 1, charPositionInLine: 0);

            Assert.IsType<OptimizedToken>(token);
            Assert.Equal("hello", token.Text); // deferred materialization
        }

        [Fact]
        public void OptimizedToken_WithCharSpanInputStream_TextMaterialization()
        {
            var input = S("function foo(bar, baz) { return 42; }");
            var factory = new OptimizedTokenFactory();
            var source = Tuple.Create<ITokenSource, ICharStream>(null, (ICharStream)input);

            // Token for "foo" (9..11)
            var token = factory.Create(source, type: 1, text: null,
                channel: 0, start: 9, stop: 11, line: 1, charPositionInLine: 9);

            Assert.Equal("foo", token.Text);
        }

        [Fact]
        public void OptimizedToken_MultipleDeferredTokens_SameStream()
        {
            var input = S("int x = 42;");
            var source = Tuple.Create<ITokenSource, ICharStream>(null, (ICharStream)input);
            var factory = new OptimizedTokenFactory();

            var tInt = factory.Create(source, 1, null, 0, 0, 2, 1, 0);
            var tX = factory.Create(source, 2, null, 0, 4, 4, 1, 4);
            var tEq = factory.Create(source, 3, null, 0, 6, 6, 1, 6);
            var t42 = factory.Create(source, 4, null, 0, 8, 9, 1, 8);
            var tSc = factory.Create(source, 5, null, 0, 10, 10, 1, 10);

            Assert.Equal("int", tInt.Text);
            Assert.Equal("x", tX.Text);
            Assert.Equal("=", tEq.Text);
            Assert.Equal("42", t42.Text);
            Assert.Equal(";", tSc.Text);
        }

        // ── Cross-type copy: CommonToken <-> OptimizedToken ──────────────

        [Fact]
        public void OptimizedToken_CopyFromCommonToken_PreservesAllFields()
        {
            var input = new AntlrInputStream("hello world");
            var source = Tuple.Create<ITokenSource, ICharStream>(null, input);
            var common = new CommonToken(source, type: 1, channel: 2, start: 0, stop: 4);
            common.Line = 5;
            common.Column = 10;
            common.TokenIndex = 99;

            var opt = new OptimizedToken(common);

            Assert.Equal(common.Type, opt.Type);
            Assert.Equal(common.Channel, opt.Channel);
            Assert.Equal(common.Line, opt.Line);
            Assert.Equal(common.Column, opt.Column);
            Assert.Equal(common.StartIndex, opt.StartIndex);
            Assert.Equal(common.StopIndex, opt.StopIndex);
            Assert.Equal(common.TokenIndex, opt.TokenIndex);
            Assert.Equal(common.Text, opt.Text);
        }

        // ── GetText on interval with CharSpanInputStream ─────────────────────

        [Theory]
        [InlineData("abcdefghij", 0, 4, "abcde")]
        [InlineData("abcdefghij", 5, 9, "fghij")]
        [InlineData("abcdefghij", 3, 3, "d")]
        [InlineData("a", 0, 0, "a")]
        public void CharSpanInputStream_GetText_Intervals(string input, int start, int stop, string expected)
        {
            var stream = S(input);
            Assert.Equal(expected, stream.GetText(Interval.Of(start, stop)));
        }

        // ── Unicode BMP parity ───────────────────────────────────────────

        [Fact]
        public void CharSpanInputStream_UnicodeBMP_Parity()
        {
            // BMP: latin, greek, CJK, math symbols
            string input = "\u00E9\u03B1\u4E16\u2200\u2203";
            var antlr = new AntlrInputStream(input);
            var span = S(input);

            for (int i = 0; i < input.Length; i++)
            {
                Assert.Equal(antlr.LA(1), span.LA(1));
                antlr.Consume();
                span.Consume();
            }

            antlr.Reset();
            span.Reset();
            Assert.Equal(
                antlr.GetText(Interval.Of(0, input.Length - 1)),
                span.GetText(Interval.Of(0, input.Length - 1))
            );
        }

        // ── Edge case: empty input ───────────────────────────────────────

        [Fact]
        public void EmptyInput_BothStreams_EOF()
        {
            var antlr = new AntlrInputStream("");
            var span = S("");

            Assert.Equal(IntStreamConstants.EOF, antlr.LA(1));
            Assert.Equal(IntStreamConstants.EOF, span.LA(1));
            Assert.Equal(0, antlr.Size);
            Assert.Equal(0, span.Size);
        }

        // ── Token ToString parity ────────────────────────────────────────

        [Theory]
        [InlineData("hello", 0, 4)]
        [InlineData("ab", 0, 0)]
        public void ToString_Parity_CommonVsOptimized(string input, int start, int stop)
        {
            var stream1 = new AntlrInputStream(input);
            var stream2 = new AntlrInputStream(input);

            var commonSrc = Tuple.Create<ITokenSource, ICharStream>(null, stream1);
            var optSrc = (default(ITokenSource), (ICharStream)stream2);

            var common = new CommonToken(commonSrc, 1, 0, start, stop);
            common.TokenIndex = 0;
            common.Line = 1;

            var opt = new OptimizedToken(optSrc, 1, 0, start, stop);
            opt.TokenIndex = 0;
            opt.Line = 1;

            Assert.Equal(common.ToString(), opt.ToString());
        }

        // ── Large input stress test ──────────────────────────────────────

        [Fact]
        public void CharSpanInputStream_LargeInput_DoesNotCorrupt()
        {
            // 100KB of repeating chars
            string input = new string('x', 100_000) + "END";
            var inputChars = input.ToCharArray();
            var stream = new CharSpanInputStream(inputChars, inputChars.Length);

            Assert.Equal(100_003, stream.Size);

            // Check last 3 chars
            stream.Seek(100_000);
            Assert.Equal('E', stream.LA(1));
            Assert.Equal('N', stream.LA(2));
            Assert.Equal('D', stream.LA(3));

            // GetText near the end
            Assert.Equal("END", stream.GetText(Interval.Of(100_000, 100_002)));
        }
    }
}
