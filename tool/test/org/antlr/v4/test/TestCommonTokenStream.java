/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
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

package org.antlr.v4.test;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenFactory;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.WritableToken;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestCommonTokenStream extends TestBufferedTokenStream {

	@Override
	protected TokenStream createTokenStream(TokenSource src) {
		return new CommonTokenStream(src);
	}

	@Test public void testOffChannel() throws Exception {
        TokenSource lexer = // simulate input " x =34  ;\n"
            new TokenSource() {
                int i = 0;
                WritableToken[] tokens = {
                    new CommonToken(1," ") {{channel = Lexer.HIDDEN;}},
                    new CommonToken(1,"x"),
                    new CommonToken(1," ") {{channel = Lexer.HIDDEN;}},
                    new CommonToken(1,"="),
                    new CommonToken(1,"34"),
                    new CommonToken(1," ") {{channel = Lexer.HIDDEN;}},
                    new CommonToken(1," ") {{channel = Lexer.HIDDEN;}},
                    new CommonToken(1,";"),
                    new CommonToken(1,"\n") {{channel = Lexer.HIDDEN;}},
                    new CommonToken(Token.EOF,"")
                };
                @Override
                public Token nextToken() {
                    return tokens[i++];
                }
                @Override
                public String getSourceName() { return "test"; }
				@Override
				public int getCharPositionInLine() {
					return 0;
				}
				@Override
				public int getLine() {
					return 0;
				}
				@Override
				public CharStream getInputStream() {
					return null;
				}

				@Override
				public void setTokenFactory(TokenFactory<?> factory) {
				}

				@Override
				public TokenFactory<?> getTokenFactory() {
					return null;
				}
			};

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        assertEquals("x", tokens.LT(1).getText()); // must skip first off channel token
        tokens.consume();
        assertEquals("=", tokens.LT(1).getText());
        assertEquals("x", tokens.LT(-1).getText());

        tokens.consume();
        assertEquals("34", tokens.LT(1).getText());
        assertEquals("=", tokens.LT(-1).getText());

        tokens.consume();
        assertEquals(";", tokens.LT(1).getText());
        assertEquals("34", tokens.LT(-1).getText());

        tokens.consume();
        assertEquals(Token.EOF, tokens.LA(1));
        assertEquals(";", tokens.LT(-1).getText());

        assertEquals("34", tokens.LT(-2).getText());
        assertEquals("=", tokens.LT(-3).getText());
        assertEquals("x", tokens.LT(-4).getText());
    }

	@Test public void testFetchOffChannel() throws Exception {
		TokenSource lexer = // simulate input " x =34  ; \n"
		                    // token indexes   01234 56789
			new TokenSource() {
				int i = 0;
				WritableToken[] tokens = {
				new CommonToken(1," ") {{channel = Lexer.HIDDEN;}}, // 0
				new CommonToken(1,"x"),								// 1
				new CommonToken(1," ") {{channel = Lexer.HIDDEN;}},	// 2
				new CommonToken(1,"="),								// 3
				new CommonToken(1,"34"),							// 4
				new CommonToken(1," ") {{channel = Lexer.HIDDEN;}},	// 5
				new CommonToken(1," ") {{channel = Lexer.HIDDEN;}}, // 6
				new CommonToken(1,";"),								// 7
				new CommonToken(1," ")  {{channel = Lexer.HIDDEN;}},// 8
				new CommonToken(1,"\n") {{channel = Lexer.HIDDEN;}},// 9
				new CommonToken(Token.EOF,"")						// 10
				};
				@Override
				public Token nextToken() {
					return tokens[i++];
				}
				@Override
				public String getSourceName() { return "test"; }
				@Override
				public int getCharPositionInLine() {
					return 0;
				}
				@Override
				public int getLine() {
					return 0;
				}
				@Override
				public CharStream getInputStream() {
					return null;
				}

				@Override
				public void setTokenFactory(TokenFactory<?> factory) {
				}

				@Override
				public TokenFactory<?> getTokenFactory() {
					return null;
				}
			};

		CommonTokenStream tokens = new CommonTokenStream(lexer);
		tokens.fill();
		assertEquals(null, tokens.getHiddenTokensToLeft(0));
		assertEquals(null, tokens.getHiddenTokensToRight(0));

		assertEquals("[[@0,0:0=' ',<1>,channel=1,0:-1]]",
					 tokens.getHiddenTokensToLeft(1).toString());
		assertEquals("[[@2,0:0=' ',<1>,channel=1,0:-1]]",
					 tokens.getHiddenTokensToRight(1).toString());

		assertEquals(null, tokens.getHiddenTokensToLeft(2));
		assertEquals(null, tokens.getHiddenTokensToRight(2));

		assertEquals("[[@2,0:0=' ',<1>,channel=1,0:-1]]",
					 tokens.getHiddenTokensToLeft(3).toString());
		assertEquals(null, tokens.getHiddenTokensToRight(3));

		assertEquals(null, tokens.getHiddenTokensToLeft(4));
		assertEquals("[[@5,0:0=' ',<1>,channel=1,0:-1], [@6,0:0=' ',<1>,channel=1,0:-1]]",
					 tokens.getHiddenTokensToRight(4).toString());

		assertEquals(null, tokens.getHiddenTokensToLeft(5));
		assertEquals("[[@6,0:0=' ',<1>,channel=1,0:-1]]",
					 tokens.getHiddenTokensToRight(5).toString());

		assertEquals("[[@5,0:0=' ',<1>,channel=1,0:-1]]",
					 tokens.getHiddenTokensToLeft(6).toString());
		assertEquals(null, tokens.getHiddenTokensToRight(6));

		assertEquals("[[@5,0:0=' ',<1>,channel=1,0:-1], [@6,0:0=' ',<1>,channel=1,0:-1]]",
					 tokens.getHiddenTokensToLeft(7).toString());
		assertEquals("[[@8,0:0=' ',<1>,channel=1,0:-1], [@9,0:0='\\n',<1>,channel=1,0:-1]]",
					 tokens.getHiddenTokensToRight(7).toString());

		assertEquals(null, tokens.getHiddenTokensToLeft(8));
		assertEquals("[[@9,0:0='\\n',<1>,channel=1,0:-1]]",
					 tokens.getHiddenTokensToRight(8).toString());

		assertEquals("[[@8,0:0=' ',<1>,channel=1,0:-1]]",
					 tokens.getHiddenTokensToLeft(9).toString());
		assertEquals(null, tokens.getHiddenTokensToRight(9));
	}

	@Test
	public void testSingleEOF() throws Exception {
		TokenSource lexer = new TokenSource() {

			@Override
			public Token nextToken() {
				return new CommonToken(Token.EOF);
			}

			@Override
			public int getLine() {
				return 0;
			}

			@Override
			public int getCharPositionInLine() {
				return 0;
			}

			@Override
			public CharStream getInputStream() {
				return null;
			}

			@Override
			public String getSourceName() {
				return IntStream.UNKNOWN_SOURCE_NAME;
			}

			@Override
			public TokenFactory<?> getTokenFactory() {
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public void setTokenFactory(TokenFactory<?> factory) {
				throw new UnsupportedOperationException("Not supported yet.");
			}
		};

		CommonTokenStream tokens = new CommonTokenStream(lexer);
		tokens.fill();

		assertEquals(Token.EOF, tokens.LA(1));
		assertEquals(0, tokens.index());
		assertEquals(1, tokens.size());
	}

	@Test(expected = IllegalStateException.class)
	public void testCannotConsumeEOF() throws Exception {
		TokenSource lexer = new TokenSource() {

			@Override
			public Token nextToken() {
				return new CommonToken(Token.EOF);
			}

			@Override
			public int getLine() {
				return 0;
			}

			@Override
			public int getCharPositionInLine() {
				return 0;
			}

			@Override
			public CharStream getInputStream() {
				return null;
			}

			@Override
			public String getSourceName() {
				return IntStream.UNKNOWN_SOURCE_NAME;
			}

			@Override
			public TokenFactory<?> getTokenFactory() {
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public void setTokenFactory(TokenFactory<?> factory) {
				throw new UnsupportedOperationException("Not supported yet.");
			}
		};

		CommonTokenStream tokens = new CommonTokenStream(lexer);
		tokens.fill();

		assertEquals(Token.EOF, tokens.LA(1));
		assertEquals(0, tokens.index());
		assertEquals(1, tokens.size());
		tokens.consume();
	}
}
