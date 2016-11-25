package org.antlr.v4.test.runtime.python2;

import org.antlr.v4.test.runtime.BaseRuntimeTest;
import org.antlr.v4.test.runtime.RuntimeTestDescriptor;
import org.antlr.v4.test.runtime.descriptors.ParserErrorsDescriptors;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestParserErrors extends BaseRuntimeTest {
	public TestParserErrors(RuntimeTestDescriptor descriptor) {
		super(descriptor,new BasePython2Test());
	}

	@Parameterized.Parameters(name="{0}")
	public static RuntimeTestDescriptor[] getAllTestDescriptors() {
		return BaseRuntimeTest.getRuntimeTestDescriptors(ParserErrorsDescriptors.class, "Python2");
	}
}
