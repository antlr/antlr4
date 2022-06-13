package org.antlr.v4.test.runtime.swift;

import org.antlr.v4.test.runtime.RuntimeTests;
import org.antlr.v4.test.runtime.RuntimeRunner;

public class SwiftRuntimeTests extends RuntimeTests {
	@Override
	protected RuntimeRunner createRuntimeRunner() {
		return new SwiftRunner();
	}
}
