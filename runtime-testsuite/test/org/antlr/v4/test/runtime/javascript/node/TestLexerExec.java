package org.antlr.v4.test.runtime.javascript.node;

import org.antlr.v4.test.runtime.BaseRuntimeTest;
import org.antlr.v4.test.runtime.RuntimeTestDescriptor;
import org.antlr.v4.test.runtime.descriptors.LexerExecDescriptors;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestLexerExec extends BaseRuntimeTest {
	public TestLexerExec(RuntimeTestDescriptor descriptor) {
		super(descriptor,new BaseNodeTest());
	}

	@Parameterized.Parameters(name="{0}")
	public static RuntimeTestDescriptor[] getAllTestDescriptors() {
		return BaseRuntimeTest.getRuntimeTestDescriptors(LexerExecDescriptors.class, "Node");
	}
}
