package org.antlr.v4.test.runtime.javascript.firefox;

import org.antlr.v4.test.runtime.BaseRuntimeTest;
import org.antlr.v4.test.runtime.RuntimeTestDescriptor;
import org.antlr.v4.test.runtime.descriptors.JavaExpressionsLeftRecursionDescriptors;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestJavaExpressionsLeftRecursion extends BaseRuntimeTest {
	public TestJavaExpressionsLeftRecursion(RuntimeTestDescriptor descriptor) {
		super(descriptor,new BaseFirefoxTest());
	}

	@Parameterized.Parameters(name="{0}")
	public static RuntimeTestDescriptor[] getAllTestDescriptors() {
		return BaseRuntimeTest.getRuntimeTestDescriptors(JavaExpressionsLeftRecursionDescriptors.class, "Firefox");
	}
}
