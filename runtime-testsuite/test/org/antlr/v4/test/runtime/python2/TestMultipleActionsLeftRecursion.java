package org.antlr.v4.test.runtime.python2;

import org.antlr.v4.test.runtime.BaseRuntimeTest;
import org.antlr.v4.test.runtime.RuntimeTestDescriptor;
import org.antlr.v4.test.runtime.descriptors.MultipleActionsLeftRecursionDescriptors;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestMultipleActionsLeftRecursion extends BaseRuntimeTest {
	public TestMultipleActionsLeftRecursion(RuntimeTestDescriptor descriptor) {
		super(descriptor,new BasePython2Test());
	}

	@Parameterized.Parameters(name="{0}")
	public static RuntimeTestDescriptor[] getAllTestDescriptors() {
		return BaseRuntimeTest.getRuntimeTestDescriptors(MultipleActionsLeftRecursionDescriptors.class, "Python2");
	}
}
