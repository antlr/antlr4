package org.antlr.v4.test.runtime.python3;

import org.antlr.v4.test.runtime.RuntimeTests;
import org.antlr.v4.test.runtime.RuntimeRunner;

public class Python3RuntimeTests extends RuntimeTests {
	@Override
	protected RuntimeRunner createRuntimeRunner() {
		return new Python3Runner();
	}
}
