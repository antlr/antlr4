package org.antlr.v4.test.runtime.python2;

import org.antlr.v4.test.runtime.RuntimeTests;
import org.antlr.v4.test.runtime.RuntimeRunner;

public class Python2RuntimeTests extends RuntimeTests {
	@Override
	protected RuntimeRunner createRuntimeRunner() {
		return new Python2Runner();
	}
}
