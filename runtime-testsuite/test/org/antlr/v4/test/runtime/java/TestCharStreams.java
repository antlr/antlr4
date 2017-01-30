/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.java;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.channels.SeekableByteChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Arrays;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

public class TestCharStreams {
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void createWithBMPStringHasExpectedSize() {
		CodePointCharStream s = CharStreams.createWithString("hello");
		assertEquals(5, s.size());
		assertEquals(0, s.index());
		assertEquals("hello", s.toString());
	}

	@Test
	public void createWithSMPStringHasExpectedSize() {
		CodePointCharStream s = CharStreams.createWithString(
				"hello \uD83C\uDF0E");
		assertEquals(7, s.size());
		assertEquals(0, s.index());
		assertEquals("hello \uD83C\uDF0E", s.toString());
	}

	@Test
	public void createWithBMPUTF8PathHasExpectedSize() throws Exception {
		Path p = folder.newFile().toPath();
		Files.write(p, "hello".getBytes(StandardCharsets.UTF_8));
		CodePointCharStream s = CharStreams.createWithUTF8(p);
		assertEquals(5, s.size());
		assertEquals(0, s.index());
		assertEquals("hello", s.toString());
		assertEquals(p.toString(), s.getSourceName());
	}

	@Test
	public void createWithSMPUTF8PathHasExpectedSize() throws Exception {
		Path p = folder.newFile().toPath();
		Files.write(p, "hello \uD83C\uDF0E".getBytes(StandardCharsets.UTF_8));
		CodePointCharStream s = CharStreams.createWithUTF8(p);
		assertEquals(7, s.size());
		assertEquals(0, s.index());
		assertEquals("hello \uD83C\uDF0E", s.toString());
		assertEquals(p.toString(), s.getSourceName());
	}

	@Test
	public void createWithBMPUTF8InputStreamHasExpectedSize() throws Exception {
		Path p = folder.newFile().toPath();
		Files.write(p, "hello".getBytes(StandardCharsets.UTF_8));
		try (InputStream is = Files.newInputStream(p)) {
			CodePointCharStream s = CharStreams.createWithUTF8Stream(is);
			assertEquals(5, s.size());
			assertEquals(0, s.index());
			assertEquals("hello", s.toString());
		}
	}

	@Test
	public void createWithSMPUTF8InputStreamHasExpectedSize() throws Exception {
		Path p = folder.newFile().toPath();
		Files.write(p, "hello \uD83C\uDF0E".getBytes(StandardCharsets.UTF_8));
		try (InputStream is = Files.newInputStream(p)) {
			CodePointCharStream s = CharStreams.createWithUTF8Stream(is);
			assertEquals(7, s.size());
			assertEquals(0, s.index());
			assertEquals("hello \uD83C\uDF0E", s.toString());
		}
	}

	@Test
	public void createWithBMPUTF8ChannelHasExpectedSize() throws Exception {
		Path p = folder.newFile().toPath();
		Files.write(p, "hello".getBytes(StandardCharsets.UTF_8));
		try (SeekableByteChannel c = Files.newByteChannel(p)) {
			CodePointCharStream s = CharStreams.createWithUTF8Channel(
					c, 4096, CodingErrorAction.REPLACE, "foo");
			assertEquals(5, s.size());
			assertEquals(0, s.index());
			assertEquals("hello", s.toString());
			assertEquals("foo", s.getSourceName());
		}
	}

	@Test
	public void createWithSMPUTF8ChannelHasExpectedSize() throws Exception {
		Path p = folder.newFile().toPath();
		Files.write(p, "hello \uD83C\uDF0E".getBytes(StandardCharsets.UTF_8));
		try (SeekableByteChannel c = Files.newByteChannel(p)) {
			CodePointCharStream s = CharStreams.createWithUTF8Channel(
					c, 4096, CodingErrorAction.REPLACE, "foo");
			assertEquals(7, s.size());
			assertEquals(0, s.index());
			assertEquals("hello \uD83C\uDF0E", s.toString());
			assertEquals("foo", s.getSourceName());
		}
	}

	@Test
	public void createWithInvalidUTF8BytesChannelReplacesWithSubstCharInReplaceMode()
		throws Exception {
		Path p = folder.newFile().toPath();
		byte[] toWrite = new byte[] { (byte)0xCA, (byte)0xFE, (byte)0xFE, (byte)0xED };
		Files.write(p, toWrite);
		try (SeekableByteChannel c = Files.newByteChannel(p)) {
			CodePointCharStream s = CharStreams.createWithUTF8Channel(
					c, 4096, CodingErrorAction.REPLACE, "foo");
			assertEquals(3, s.size());
			assertEquals(0, s.index());
			assertEquals("\uFFFD\uFFFD\uFFFD", s.toString());
		}
	}

	@Test
	public void createWithInvalidUTF8BytesThrowsInReportMode() throws Exception {
		Path p = folder.newFile().toPath();
		byte[] toWrite = new byte[] { (byte)0xCA, (byte)0xFE };
		Files.write(p, toWrite);
		try (SeekableByteChannel c = Files.newByteChannel(p)) {
			thrown.expect(CharacterCodingException.class);
			CharStreams.createWithUTF8Channel(c, 4096, CodingErrorAction.REPORT, "foo");
		}
	}

	@Test
	public void createWithSMPUTF8SequenceStraddlingBufferBoundary() throws Exception {
		Path p = folder.newFile().toPath();
		Files.write(p, "hello \uD83C\uDF0E".getBytes(StandardCharsets.UTF_8));
		try (SeekableByteChannel c = Files.newByteChannel(p)) {
			CodePointCharStream s = CharStreams.createWithUTF8Channel(
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
}
