/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class StreamReader implements Runnable {
	private final StringBuilder buffer = new StringBuilder();
	private final BufferedReader in;
	private final Thread worker;

	public StreamReader(InputStream in) {
		this.in = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8) );
		worker = new Thread(this);
	}

	public void start() {
		worker.start();
	}

	@Override
	public void run() {
		try {
			while (true) {
				int c = in.read();
				if (c == -1) {
					break;
				}
				if (c == '\r') {
					continue;
				}
				buffer.append((char) c);
			}
		}
		catch (IOException ioe) {
			System.err.println("can't read output from process");
		}
	}

	/** wait for the thread to finish */
	public void join() throws InterruptedException {
		worker.join();
	}

	@Override
	public String toString() {
		return buffer.toString();
	}
}
