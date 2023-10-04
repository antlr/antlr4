/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.javascript;

import org.antlr.v4.test.runtime.RuntimeRunner;
import org.antlr.v4.test.runtime.RuntimeTests;

public class JavaScriptRuntimeTests extends RuntimeTests {
	@Override
	protected RuntimeRunner createRuntimeRunner() {
		return new NodeRunner();
	}
}
