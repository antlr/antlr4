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
    /// Tests that OptimizedTokenFactory creates tokens correctly and is
    /// a valid drop-in replacement for CommonTokenFactory.
    /// </summary>
    public class OptimizedTokenFactoryTests
    {
        // ── Default instance ─────────────────────────────────────────────

        [Fact]
        public void Default_IsNotNull()
        {
            Assert.NotNull(OptimizedTokenFactory.Default);
        }

        [Fact]
        public void Default_ImplementsITokenFactory()
        {
            Assert.IsAssignableFrom<ITokenFactory>(OptimizedTokenFactory.Default);
        }

        // ── Create(source, type, text, channel, start, stop, line, col) ──

        [Fact]
        public void Create_Full_ReturnsOptimizedToken()
        {
            var factory = new OptimizedTokenFactory();
            var stream = new AntlrInputStream("hello world");
            var source = Tuple.Create<ITokenSource, ICharStream>(null, stream);

            var token = factory.Create(source, type: 1, text: null, channel: 0,
                start: 6, stop: 10, line: 1, charPositionInLine: 6);

            Assert.IsType<OptimizedToken>(token);
            Assert.Equal(1, token.Type);
            Assert.Equal(0, token.Channel);
            Assert.Equal(1, token.Line);
            Assert.Equal(6, token.Column);
            Assert.Equal(6, token.StartIndex);
            Assert.Equal(10, token.StopIndex);
            // Text deferred — should materialize to "world"
            Assert.Equal("world", token.Text);
        }

        [Fact]
        public void Create_WithExplicitText_SetsTextImmediately()
        {
            var factory = new OptimizedTokenFactory();
            var source = Tuple.Create<ITokenSource, ICharStream>(null, null);

            var token = factory.Create(source, type: 1, text: "explicit",
                channel: 0, start: 0, stop: 7, line: 1, charPositionInLine: 0);

            Assert.Equal("explicit", token.Text);
        }

        [Fact]
        public void Create_CopyTextMode_MaterializesEagerly()
        {
            var factory = new OptimizedTokenFactory(copyText: true);
            var stream = new AntlrInputStream("hello world");
            var source = Tuple.Create<ITokenSource, ICharStream>(null, stream);

            var token = factory.Create(source, type: 1, text: null, channel: 0,
                start: 0, stop: 4, line: 1, charPositionInLine: 0);

            // Even though text was null, copyText=true should have materialized it
            Assert.Equal("hello", token.Text);
        }

        [Fact]
        public void Create_CopyTextMode_NullStream_NoThrow()
        {
            var factory = new OptimizedTokenFactory(copyText: true);
            var source = Tuple.Create<ITokenSource, ICharStream>(null, null);

            var token = factory.Create(source, type: 1, text: null, channel: 0,
                start: 0, stop: 4, line: 1, charPositionInLine: 0);

            // No stream, no text, no crash
            Assert.Null(token.Text);
        }

        // ── Create(type, text) ───────────────────────────────────────────

        [Fact]
        public void Create_TypeAndText_ReturnsOptimizedToken()
        {
            var factory = new OptimizedTokenFactory();
            var token = factory.Create(type: 99, text: "keyword");

            Assert.IsType<OptimizedToken>(token);
            Assert.Equal(99, token.Type);
            Assert.Equal("keyword", token.Text);
        }

        // ── Parity with CommonTokenFactory ───────────────────────────────

        [Theory]
        [InlineData("hello world", 0, 4)]
        [InlineData("hello world", 6, 10)]
        [InlineData("a", 0, 0)]
        public void Parity_CommonTokenFactory_TextMatch(string input, int start, int stop)
        {
            var stream1 = new AntlrInputStream(input);
            var stream2 = new AntlrInputStream(input);
            var commonSource = Tuple.Create<ITokenSource, ICharStream>(null, stream1);
            var optSource = Tuple.Create<ITokenSource, ICharStream>(null, stream2);

            var commonFactory = CommonTokenFactory.Default;
            var optFactory = OptimizedTokenFactory.Default;

            var commonToken = commonFactory.Create(commonSource, 1, null, 0, start, stop, 1, 0);
            var optToken = optFactory.Create(optSource, 1, null, 0, start, stop, 1, 0);

            Assert.Equal(commonToken.Text, optToken.Text);
            Assert.Equal(commonToken.Type, optToken.Type);
            Assert.Equal(commonToken.Channel, optToken.Channel);
            Assert.Equal(commonToken.StartIndex, optToken.StartIndex);
            Assert.Equal(commonToken.StopIndex, optToken.StopIndex);
        }
    }
}
