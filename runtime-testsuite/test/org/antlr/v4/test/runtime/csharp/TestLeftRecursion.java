/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.csharp;

import org.antlr.v4.test.runtime.BaseRuntimeTest;
import org.antlr.v4.test.runtime.RuntimeTestDescriptor;
import org.antlr.v4.test.runtime.category.LeftRecursionTests;
import org.antlr.v4.test.runtime.descriptors.LeftRecursionDescriptors;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@Category(LeftRecursionTests.class)
@RunWith(Parameterized.class)
public class TestLeftRecursion extends BaseRuntimeTest {
	public TestLeftRecursion(RuntimeTestDescriptor descriptor) {
		super(descriptor,new BaseCSharpTest());
	}

	@Parameterized.Parameters(name="{0}")
	public static RuntimeTestDescriptor[] getAllTestDescriptors() {
		return BaseRuntimeTest.getRuntimeTestDescriptors(LeftRecursionDescriptors.class, "CSharp");
	}
}
