package org.antlr.v4.test.runtime.javascript.explorer;

import org.antlr.v4.test.runtime.BaseRuntimeTest;
import org.antlr.v4.test.runtime.RuntimeTestDescriptor;
import org.antlr.v4.test.runtime.descriptors.LexerErrorsDescriptors;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestLexerErrors extends BaseRuntimeTest {
	public TestLexerErrors(RuntimeTestDescriptor descriptor) {
		super(descriptor,new BaseExplorerTest());
	}

	@Parameterized.Parameters(name="{0}")
	public static RuntimeTestDescriptor[] getAllTestDescriptors() {
		return BaseRuntimeTest.getRuntimeTestDescriptors(LexerErrorsDescriptors.class, "Explorer");
	}
}
