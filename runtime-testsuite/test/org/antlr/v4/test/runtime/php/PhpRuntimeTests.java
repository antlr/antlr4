package org.antlr.v4.test.runtime.php;

import org.antlr.v4.test.runtime.RuntimeTests;
import org.antlr.v4.test.runtime.RuntimeRunner;

public class PhpRuntimeTests extends RuntimeTests {
	@Override
	protected RuntimeRunner createRuntimeRunner() {
		return new PHPRunner();
	}
}
