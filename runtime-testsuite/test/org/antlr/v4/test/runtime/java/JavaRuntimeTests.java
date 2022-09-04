/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.java;

import org.antlr.v4.test.runtime.RuntimeTests;
import org.antlr.v4.test.runtime.RuntimeRunner;

public class JavaRuntimeTests extends RuntimeTests {
	@Override
	protected RuntimeRunner createRuntimeRunner() {
		return new JavaRunner();
	}
}
