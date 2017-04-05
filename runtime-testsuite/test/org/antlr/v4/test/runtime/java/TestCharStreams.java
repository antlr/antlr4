/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.java;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.InputStream;
import java.io.Reader;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

public class TestCharStreams {
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void fromBMPStringHasExpectedSize() {
		CharStream s = CharStreams.fromString("hello");
		assertEquals(5, s.size());
		assertEquals(0, s.index());
		assertEquals("hello", s.toString());
	}

	@Test
	public void fromSMPStringHasExpectedSize() {
		CharStream s = CharStreams.fromString(
				"hello \uD83C\uDF0E");
		assertEquals(7, s.size());
		assertEquals(0, s.index());
		assertEquals("hello \uD83C\uDF0E", s.toString());
	}

	@Test
	public void fromBMPUTF8PathHasExpectedSize() throws Exception {
		Path p = folder.newFile().toPath();
		Files.write(p, "hello".getBytes(StandardCharsets.UTF_8));
		CharStream s = CharStreams.fromPath(p);
		assertEquals(5, s.size());
		assertEquals(0, s.index());
		assertEquals("hello", s.toString());
		assertEquals(p.toString(), s.getSourceName());
	}

	@Test
	public void fromSMPUTF8PathHasExpectedSize() throws Exception {
		Path p = folder.newFile().toPath();
		Files.write(p, "hello \uD83C\uDF0E".getBytes(StandardCharsets.UTF_8));
		CharStream s = CharStreams.fromPath(p);
		assertEquals(7, s.size());
		assertEquals(0, s.index());
		assertEquals("hello \uD83C\uDF0E", s.toString());
		assertEquals(p.toString(), s.getSourceName());
	}

	@Test
	public void fromBMPUTF8InputStreamHasExpectedSize() throws Exception {
		Path p = folder.newFile().toPath();
		Files.write(p, "hello".getBytes(StandardCharsets.UTF_8));
		try (InputStream is = Files.newInputStream(p)) {
			CharStream s = CharStreams.fromStream(is);
			assertEquals(5, s.size());
			assertEquals(0, s.index());
			assertEquals("hello", s.toString());
		}
	}

	@Test
	public void fromSMPUTF8InputStreamHasExpectedSize() throws Exception {
		Path p = folder.newFile().toPath();
		Files.write(p, "hello \uD83C\uDF0E".getBytes(StandardCharsets.UTF_8));
		try (InputStream is = Files.newInputStream(p)) {
			CharStream s = CharStreams.fromStream(is);
			assertEquals(7, s.size());
			assertEquals(0, s.index());
			assertEquals("hello \uD83C\uDF0E", s.toString());
		}
	}

	@Test
	public void fromBMPUTF8ChannelHasExpectedSize() throws Exception {
		Path p = folder.newFile().toPath();
		Files.write(p, "hello".getBytes(StandardCharsets.UTF_8));
		try (SeekableByteChannel c = Files.newByteChannel(p)) {
			CharStream s = CharStreams.fromChannel(
					c, 4096, CodingErrorAction.REPLACE, "foo");
			assertEquals(5, s.size());
			assertEquals(0, s.index());
			assertEquals("hello", s.toString());
			assertEquals("foo", s.getSourceName());
		}
	}

	@Test
	public void fromSMPUTF8ChannelHasExpectedSize() throws Exception {
		Path p = folder.newFile().toPath();
		Files.write(p, "hello \uD83C\uDF0E".getBytes(StandardCharsets.UTF_8));
		try (SeekableByteChannel c = Files.newByteChannel(p)) {
			CharStream s = CharStreams.fromChannel(
					c, 4096, CodingErrorAction.REPLACE, "foo");
			assertEquals(7, s.size());
			assertEquals(0, s.index());
			assertEquals("hello \uD83C\uDF0E", s.toString());
			assertEquals("foo", s.getSourceName());
		}
	}

	@Test
	public void fromInvalidUTF8BytesChannelReplacesWithSubstCharInReplaceMode()
		throws Exception {
		Path p = folder.newFile().toPath();
		byte[] toWrite = new byte[] { (byte)0xCA, (byte)0xFE, (byte)0xFE, (byte)0xED };
		Files.write(p, toWrite);
		try (SeekableByteChannel c = Files.newByteChannel(p)) {
			CharStream s = CharStreams.fromChannel(
					c, 4096, CodingErrorAction.REPLACE, "foo");
			assertEquals(4, s.size());
			assertEquals(0, s.index());
			assertEquals("\uFFFD\uFFFD\uFFFD\uFFFD", s.toString());
		}
	}

	@Test
	public void fromInvalidUTF8BytesThrowsInReportMode() throws Exception {
		Path p = folder.newFile().toPath();
		byte[] toWrite = new byte[] { (byte)0xCA, (byte)0xFE };
		Files.write(p, toWrite);
		try (SeekableByteChannel c = Files.newByteChannel(p)) {
			thrown.expect(CharacterCodingException.class);
			CharStreams.fromChannel(c, 4096, CodingErrorAction.REPORT, "foo");
		}
	}

	@Test
	public void fromSMPUTF8SequenceStraddlingBufferBoundary() throws Exception {
		Path p = folder.newFile().toPath();
		Files.write(p, "hello \uD83C\uDF0E".getBytes(StandardCharsets.UTF_8));
		try (SeekableByteChannel c = Files.newByteChannel(p)) {
			CharStream s = CharStreams.fromChannel(
					c,
					// Note this buffer size ensures the SMP code point
					// straddles the boundary of two buffers
					8,
					CodingErrorAction.REPLACE,
					"foo");
			assertEquals(7, s.size());
			assertEquals(0, s.index());
			assertEquals("hello \uD83C\uDF0E", s.toString());
		}
	}

	@Test
	public void fromFileName() throws Exception {
		Path p = folder.newFile().toPath();
		Files.write(p, "hello \uD83C\uDF0E".getBytes(StandardCharsets.UTF_8));
		CharStream s = CharStreams.fromFileName(p.toString());
		assertEquals(7, s.size());
		assertEquals(0, s.index());
		assertEquals("hello \uD83C\uDF0E", s.toString());
		assertEquals(p.toString(), s.getSourceName());

	}

	@Test
	public void fromFileNameWithLatin1() throws Exception {
		Path p = folder.newFile().toPath();
		Files.write(p, "hello \u00CA\u00FE".getBytes(StandardCharsets.ISO_8859_1));
		CharStream s = CharStreams.fromFileName(p.toString(), StandardCharsets.ISO_8859_1);
		assertEquals(8, s.size());
		assertEquals(0, s.index());
		assertEquals("hello \u00CA\u00FE", s.toString());
		assertEquals(p.toString(), s.getSourceName());

	}

	@Test
	public void fromReader() throws Exception {
		Path p = folder.newFile().toPath();
		Files.write(p, "hello \uD83C\uDF0E".getBytes(StandardCharsets.UTF_8));
		try (Reader r = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
			CharStream s = CharStreams.fromReader(r);
			assertEquals(7, s.size());
			assertEquals(0, s.index());
			assertEquals("hello \uD83C\uDF0E", s.toString());
		}
	}

	@Test
	public void fromSMPUTF16LEPathSMPHasExpectedSize() throws Exception {
		Path p = folder.newFile().toPath();
		Files.write(p, "hello \uD83C\uDF0E".getBytes(StandardCharsets.UTF_16LE));
		CharStream s = CharStreams.fromPath(p, StandardCharsets.UTF_16LE);
		assertEquals(7, s.size());
		assertEquals(0, s.index());
		assertEquals("hello \uD83C\uDF0E", s.toString());
		assertEquals(p.toString(), s.getSourceName());
	}

	@Test
	public void fromSMPUTF32LEPathSMPHasExpectedSize() throws Exception {
		Path p = folder.newFile().toPath();
		// UTF-32 isn't popular enough to have an entry in StandardCharsets.
		Charset c = Charset.forName("UTF-32LE");
		Files.write(p, "hello \uD83C\uDF0E".getBytes(c));
		CharStream s = CharStreams.fromPath(p, c);
		assertEquals(7, s.size());
		assertEquals(0, s.index());
		assertEquals("hello \uD83C\uDF0E", s.toString());
		assertEquals(p.toString(), s.getSourceName());
	}
}
