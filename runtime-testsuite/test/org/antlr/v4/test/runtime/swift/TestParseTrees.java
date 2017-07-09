/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.swift;

import org.antlr.v4.test.runtime.BaseRuntimeTest;
import org.antlr.v4.test.runtime.RuntimeTestDescriptor;
import org.antlr.v4.test.runtime.category.ParserTests;
import org.antlr.v4.test.runtime.descriptors.ParseTreesDescriptors;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@Category(ParserTests.class)
@RunWith(Parameterized.class)
public class TestParseTrees extends BaseRuntimeTest {
	public TestParseTrees(RuntimeTestDescriptor descriptor) {
		super(descriptor,new BaseSwiftTest());
	}

	@Parameterized.Parameters(name="{0}")
	public static RuntimeTestDescriptor[] getAllTestDescriptors() {
		return BaseRuntimeTest.getRuntimeTestDescriptors(ParseTreesDescriptors.class, "Swift");
	}
}

