package org.antlr.v4.test.runtime.go;

import org.antlr.v4.test.runtime.BaseRuntimeTest;
import org.antlr.v4.test.runtime.RuntimeTestDescriptor;
import org.antlr.v4.test.runtime.descriptors.ReturnValueLeftRecursionDescriptors;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestReturnValueLeftRecursion extends BaseRuntimeTest {
	public TestReturnValueLeftRecursion(RuntimeTestDescriptor descriptor) {
		super(descriptor,new BaseGoTest());
	}

	@Parameterized.Parameters(name="{0}")
	public static RuntimeTestDescriptor[] getAllTestDescriptors() {
		return BaseRuntimeTest.getRuntimeTestDescriptors(ReturnValueLeftRecursionDescriptors.class, "Go");
	}
}
