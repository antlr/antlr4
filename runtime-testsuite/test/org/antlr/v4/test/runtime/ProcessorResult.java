/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime;

public class ProcessorResult {
	public final int exitCode;
	public final String output;
	public final String errors;

	public ProcessorResult(int exitCode, String output, String errors) {
		this.exitCode = exitCode;
		this.output = output;
		this.errors = errors;
	}

	public boolean isSuccess() {
		return exitCode == 0;
	}
}
