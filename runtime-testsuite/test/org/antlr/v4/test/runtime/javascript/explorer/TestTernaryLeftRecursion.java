package org.antlr.v4.test.runtime.javascript.explorer;

import org.antlr.v4.test.runtime.BaseRuntimeTest;
import org.antlr.v4.test.runtime.RuntimeTestDescriptor;
import org.antlr.v4.test.runtime.descriptors.TernaryLeftRecursionDescriptors;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestTernaryLeftRecursion extends BaseRuntimeTest {
	public TestTernaryLeftRecursion(RuntimeTestDescriptor descriptor) {
		super(descriptor,new BaseExplorerTest());
	}

	@Parameterized.Parameters(name="{0}")
	public static RuntimeTestDescriptor[] getAllTestDescriptors() {
		return BaseRuntimeTest.getRuntimeTestDescriptors(TernaryLeftRecursionDescriptors.class, "Java");
	}
}
