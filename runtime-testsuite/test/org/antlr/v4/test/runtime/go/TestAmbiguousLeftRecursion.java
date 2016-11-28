package org.antlr.v4.test.runtime.go;

import org.antlr.v4.test.runtime.BaseRuntimeTest;
import org.antlr.v4.test.runtime.RuntimeTestDescriptor;
import org.antlr.v4.test.runtime.descriptors.AmbiguousLeftRecursionDescriptors;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestAmbiguousLeftRecursion extends BaseRuntimeTest {
	public TestAmbiguousLeftRecursion(RuntimeTestDescriptor descriptor) {
		super(descriptor,new BaseGoTest());
	}

	@Parameterized.Parameters(name="{0}")
	public static RuntimeTestDescriptor[] getAllTestDescriptors() {
		return BaseRuntimeTest.getRuntimeTestDescriptors(AmbiguousLeftRecursionDescriptors.class, "Java");
	}
}
