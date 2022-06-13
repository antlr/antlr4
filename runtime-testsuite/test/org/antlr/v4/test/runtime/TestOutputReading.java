/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public abstract class TestOutputReading {
	public static void append(BufferedReader in, StringBuilder buf) throws IOException {
		String line = in.readLine();
		while (line!=null) {
			buf.append(line);
			// NOTE: This appends a newline at EOF
			// regardless of whether or not the
			// input actually ended with a
			// newline.
			//
			// We should revisit this and read a
			// block at a time rather than a line
			// at a time, and change all tests
			// which rely on this behavior to
			// remove the trailing newline at EOF.
			//
			// When we fix this, we can remove the
			// TestOutputReading class entirely.
			buf.append('\n');
			line = in.readLine();
		}
	}
}
