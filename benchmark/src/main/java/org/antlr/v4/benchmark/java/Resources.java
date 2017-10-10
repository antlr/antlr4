package org.antlr.v4.benchmark.java;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.nio.charset.StandardCharsets;

abstract class Resources {
	private static final int BUFFER_SIZE = 4096;

	public static final String getResourceAsString(ClassLoader loader, String resourceName) throws IOException {
		StringBuilder sb = new StringBuilder();
		char[] buf = new char[BUFFER_SIZE];
		try (InputStream is = loader.getResourceAsStream(resourceName);
		     InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
			int charsRead;
			while ((charsRead = isr.read(buf, 0, BUFFER_SIZE)) != -1) {
					sb.append(buf, 0, charsRead);
			}
		}
		return sb.toString();
	}
}
