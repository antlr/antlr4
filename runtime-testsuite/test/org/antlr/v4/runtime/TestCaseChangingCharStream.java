package org.antlr.v4.runtime;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

import static org.antlr.v4.runtime.CharStreams.fromString;
import static org.junit.Assert.assertEquals;

public class TestCaseChangingCharStream {

	/**
	 * Helper function to return a complete list of symbols read from the stream.
	 * @param stream
	 * @return
	 */
	private static List<Integer> readAll(CharStream stream) {
		List<Integer> symbols = Lists.newArrayList();

		for (int i = 1; i <= stream.size()+1; i++) {
			symbols.add( stream.LA(i) );
		}

		return symbols;
	}

	@Test
	public void testUpper() {
		List<Integer> expected = Lists.newArrayList((int)'A', (int)'B', (int)'C', (int)'D', IntStream.EOF);

		CharStream stream = CharStreams.forceUpper(fromString("abcd"));
		assertEquals(expected, readAll(stream));

		stream = CharStreams.forceUpper(fromString("ABCD"));
		assertEquals(expected,  readAll(stream));
	}

	@Test
	public void testLower() {
		List<Integer> expected = Lists.newArrayList((int)'a', (int)'b', (int)'c', (int)'d', IntStream.EOF);

		CharStream stream = CharStreams.forceLower(fromString("abcd"));
		assertEquals(expected, readAll(stream));

		stream = CharStreams.forceLower(fromString("ABCD"));
		assertEquals(expected,  readAll(stream));
	}
}
