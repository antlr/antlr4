package org.antlr.v4.test.tool;

import org.antlr.v4.runtime.misc.CharSupport;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCharSupport {
	@Test
	public void testGetPrintable() {
		assertEquals("'<INVALID>'", CharSupport.getPrintable(-1));
		assertEquals("'\\n'", CharSupport.getPrintable('\n'));
		assertEquals("'\\\\'", CharSupport.getPrintable('\\'));
		assertEquals("'\\''", CharSupport.getPrintable('\''));
		assertEquals("'b'", CharSupport.getPrintable('b'));
		assertEquals("'\\uFFFF'", CharSupport.getPrintable(0xFFFF));
		assertEquals("'\\u{10FFFF}'", CharSupport.getPrintable(0x10FFFF));
	}

	@Test
	public void testGetIntervalSetEscapedString() {
		assertEquals("{}", new IntervalSet().toString(true));
		assertEquals("'\\u0000'", new IntervalSet(0).toString(true));
		assertEquals("{'\\u0001'..'\\u0003'}", new IntervalSet(3, 1, 2).toString(true));
	}
}
