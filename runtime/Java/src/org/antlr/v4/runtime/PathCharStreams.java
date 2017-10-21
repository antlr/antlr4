/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/** This class adds Java 7-friendly interfaces for creating {@link CharStream}s.
 *
 * This is in a separate class from {@link CharStreams} so it can be
 * stripped entirely (e.g. using ProGuard) for Android (pre-Android O)
 * and other clients which don't support Java 7 APIs.
 *
 * @see CharStreams
 */
public final class PathCharStreams {
	// Utility class; do not construct.
	private PathCharStreams() { }

	/**
	 * Creates a {@link CharStream} given a path to a UTF-8
	 * encoded file on disk.
	 *
	 * Reads the entire contents of the file into the result before returning.
	 */
	public static CharStream fromPath(Path path) throws IOException {
		return fromPath(path, StandardCharsets.UTF_8);
	}

	/**
	 * Creates a {@link CharStream} given a path to a file on disk and the
	 * charset of the bytes contained in the file.
	 *
	 * Reads the entire contents of the file into the result before returning.
	 */
	public static CharStream fromPath(Path path, Charset charset) throws IOException {
		try (FileChannel channel = FileChannel.open(path)) {
			return CharStreams.fromChannel(
				channel,
				charset,
				CharStreams.DEFAULT_BUFFER_SIZE,
				CodingErrorAction.REPLACE,
				path.toString(),
				channel.size());
		}
	}
}
