package org.antlr.v4.test.runtime.javascript.chrome;

import org.antlr.v4.test.runtime.BaseRuntimeTest;
import org.antlr.v4.test.runtime.RuntimeTestDescriptor;
import org.antlr.v4.test.runtime.descriptors.CompositeParsersDescriptors;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestCompositeParsers extends BaseRuntimeTest {
	public TestCompositeParsers(RuntimeTestDescriptor descriptor) {
		super(descriptor,new BaseChromeTest());
	}

	@Parameterized.Parameters(name="{0}")
	public static RuntimeTestDescriptor[] getAllTestDescriptors() {
		return BaseRuntimeTest.getRuntimeTestDescriptors(CompositeParsersDescriptors.class, "Chrome");
	}
}
