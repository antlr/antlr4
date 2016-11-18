package org.antlr.v4.test.runtime.javascript.safari;

import org.antlr.v4.test.runtime.BaseRuntimeTest;
import org.antlr.v4.test.runtime.RuntimeTestDescriptor;
import org.antlr.v4.test.runtime.descriptors.SemPredEvalLexerDescriptors;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestSemPredEvalLexer extends BaseRuntimeTest {
	public TestSemPredEvalLexer(RuntimeTestDescriptor descriptor) {
		super(descriptor,new BaseSafariTest());
	}

	@Parameterized.Parameters(name="{0}")
	public static RuntimeTestDescriptor[] getAllTestDescriptors() {
		return BaseRuntimeTest.getRuntimeTestDescriptors(SemPredEvalLexerDescriptors.class, "Safari");
	}
}
