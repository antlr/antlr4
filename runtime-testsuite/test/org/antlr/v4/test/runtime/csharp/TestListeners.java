package org.antlr.v4.test.runtime.csharp;

import org.antlr.v4.test.runtime.BaseRuntimeTest;
import org.antlr.v4.test.runtime.RuntimeTestDescriptor;
import org.antlr.v4.test.runtime.descriptors.ListenersDescriptors;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestListeners extends BaseRuntimeTest {
	public TestListeners(RuntimeTestDescriptor descriptor) {
		super(descriptor,new BaseCSharpTest());
	}

	@Parameterized.Parameters(name="{0}")
	public static RuntimeTestDescriptor[] getAllTestDescriptors() {
		return BaseRuntimeTest.getRuntimeTestDescriptors(ListenersDescriptors.class, "CSharp");
	}
}
