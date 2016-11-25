package org.antlr.v4.test.runtime.go;

import org.antlr.v4.test.runtime.BaseRuntimeTest;
import org.antlr.v4.test.runtime.RuntimeTestDescriptor;
import org.antlr.v4.test.runtime.descriptors.VisitorsDescriptors;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestVisitors extends BaseRuntimeTest {
	public TestVisitors(RuntimeTestDescriptor descriptor) {
		super(descriptor,new BaseGoTest());
	}

	@BeforeClass
	public static void groupSetUp() throws Exception { BaseGoTest.groupSetUp(); }

	@AfterClass
	public static void groupTearDown() throws Exception { BaseGoTest.groupTearDown(); }

	@Parameterized.Parameters(name="{0}")
	public static RuntimeTestDescriptor[] getAllTestDescriptors() {
		return BaseRuntimeTest.getRuntimeTestDescriptors(VisitorsDescriptors.class, "Go");
	}
}
