/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.java.api;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.ListTokenSource;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.TokenFactory;
import org.antlr.v4.runtime.WritableToken;
import org.antlr.v4.runtime.misc.Pair;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for the optional {@code file} property on {@link Token},
 * {@link TokenSource}, and {@link WritableToken}.
 *
 * <p>These tests cover the new public API (default interface methods on
 * {@code Token}/{@code TokenSource}/{@code WritableToken}, the
 * {@code file} field on {@link CommonToken}, the propagation through
 * {@link CommonToken}'s {@code (Pair, ...)} and copy constructors, and
 * {@link ListTokenSource#getFile()} behavior).</p>
 */
public class TestTokenFile {

	private static final String FILE_A = "alpha.g4";
	private static final String FILE_B = "beta.g4";

	// 1. Lexer.setFile propagates to tokens emitted afterward.
	@Test
	public void testLexerSetFilePropagatesToTokens() {
		CharStream input = new ANTLRInputStream("AAA");
		VisitorBasicLexer lexer = new VisitorBasicLexer(input);
		lexer.setFile(FILE_A);

		Token first = lexer.nextToken();
		Token second = lexer.nextToken();
		assertEquals(VisitorBasicLexer.A, first.getType());
		assertEquals(VisitorBasicLexer.A, second.getType());
		assertEquals(FILE_A, first.getFile());
		assertEquals(FILE_A, second.getFile());
		assertEquals(FILE_A, lexer.getFile());
	}

	// 2. CommonToken copy constructor preserves file.
	@Test
	public void testCommonTokenCopyConstructorPropagatesFile() {
		CommonToken orig = new CommonToken(1, "x");
		orig.setFile(FILE_A);
		orig.setLine(7);

		CommonToken copy = new CommonToken(orig);
		assertEquals(FILE_A, copy.getFile());
		assertEquals(7, copy.getLine());
	}

	// 3. CommonToken (Pair, ...) ctor pulls file from the source TokenSource.
	@Test
	public void testCommonTokenFromPairCarriesSourceFile() {
		CharStream input = new ANTLRInputStream("AAA");
		VisitorBasicLexer src = new VisitorBasicLexer(input);
		src.setFile(FILE_B);

		Pair<TokenSource, CharStream> pair = new Pair<TokenSource, CharStream>(src, input);
		CommonToken t = new CommonToken(pair, 1, Token.DEFAULT_CHANNEL, 0, 0);
		assertEquals(FILE_B, t.getFile());
	}

	// 4. ListTokenSource.getFile reflects the current position's file.
	@Test
	public void testListTokenSourceGetFileWithTokens() {
		CommonToken t1 = new CommonToken(1, "a");
		t1.setFile(FILE_A);
		CommonToken t2 = new CommonToken(1, "b");
		t2.setFile(FILE_A);

		ListTokenSource src = new ListTokenSource(Arrays.<Token>asList(t1, t2));
		assertEquals(FILE_A, src.getFile());
		src.nextToken();
		assertEquals(FILE_A, src.getFile());
	}

	// 5. Empty ListTokenSource returns "" (never null).
	@Test
	public void testListTokenSourceGetFileEmpty() {
		ListTokenSource src = new ListTokenSource(Collections.<Token>emptyList());
		assertNotNull(src.getFile());
		assertEquals("", src.getFile());
	}

	// 6. setFile/getFile round-trips on a WritableToken.
	@Test
	public void testWritableTokenSetFileRoundTrip() {
		WritableToken t = new CommonToken(1, "x");
		t.setFile(FILE_A);
		assertEquals(FILE_A, t.getFile());
		t.setFile(FILE_B);
		assertEquals(FILE_B, t.getFile());
	}

	// 7. A Token implementation that does NOT override getFile() picks up
	// the interface default and returns "". This is the binary-compatibility
	// guarantee for third-party Token implementations.
	@Test
	public void testTokenInterfaceDefaultGetFile() {
		Token t = new MinimalToken();
		assertEquals("", t.getFile());
	}

	// 8. A WritableToken implementation that does NOT override setFile()
	// picks up the no-op default. Pre-existing third-party implementations
	// keep compiling and calling setFile is a harmless no-op.
	@Test
	public void testWritableTokenInterfaceDefaultSetFile() {
		MinimalWritableToken t = new MinimalWritableToken();
		t.setFile(FILE_A);          // default no-op
		assertEquals("", t.getFile()); // still default ""
	}

	/** Minimal Token impl that does not override the new default methods. */
	private static class MinimalToken implements Token {
		@Override public String getText() { return ""; }
		@Override public int getType() { return 0; }
		@Override public int getLine() { return 0; }
		@Override public int getCharPositionInLine() { return 0; }
		@Override public int getChannel() { return DEFAULT_CHANNEL; }
		@Override public int getTokenIndex() { return -1; }
		@Override public int getStartIndex() { return 0; }
		@Override public int getStopIndex() { return 0; }
		@Override public TokenSource getTokenSource() { return null; }
		@Override public CharStream getInputStream() { return null; }
		// getFile() intentionally omitted — exercises the default method.
	}

	/** Minimal WritableToken impl that does not override the new defaults. */
	private static class MinimalWritableToken extends MinimalToken implements WritableToken {
		@Override public void setText(String text) { }
		@Override public void setType(int ttype) { }
		@Override public void setLine(int line) { }
		@Override public void setCharPositionInLine(int pos) { }
		@Override public void setChannel(int channel) { }
		@Override public void setTokenIndex(int index) { }
		// setFile(String) intentionally omitted — exercises the default no-op.
	}
}
