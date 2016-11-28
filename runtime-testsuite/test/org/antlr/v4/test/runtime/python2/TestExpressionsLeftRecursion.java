package org.antlr.v4.test.runtime.python2;

import org.antlr.v4.test.runtime.BaseRuntimeTest;
import org.antlr.v4.test.runtime.RuntimeTestDescriptor;
import org.antlr.v4.test.runtime.descriptors.ExpressionsLeftRecursionDescriptors;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestExpressionsLeftRecursion extends BaseRuntimeTest {
	public TestExpressionsLeftRecursion(RuntimeTestDescriptor descriptor) {
		super(descriptor,new BasePython2Test());
	}

	@Parameterized.Parameters(name="{0}")
	public static RuntimeTestDescriptor[] getAllTestDescriptors() {
		return BaseRuntimeTest.getRuntimeTestDescriptors(ExpressionsLeftRecursionDescriptors.class, "Python2");
	}
}
