package org.antlr.v4.test.runtime.cpp;

import org.antlr.v4.test.runtime.BaseRuntimeTest;
import org.antlr.v4.test.runtime.RuntimeTestDescriptor;
import org.antlr.v4.test.runtime.descriptors.ParseTreesDescriptors;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestParseTrees extends BaseRuntimeTest {
	public TestParseTrees(RuntimeTestDescriptor descriptor) {
		super(descriptor,new BaseCppTest());
	}

	@Parameterized.Parameters(name="{0}")
	public static RuntimeTestDescriptor[] getAllTestDescriptors() {
		return BaseRuntimeTest.getRuntimeTestDescriptors(ParseTreesDescriptors.class, "Cpp");
	}
}
