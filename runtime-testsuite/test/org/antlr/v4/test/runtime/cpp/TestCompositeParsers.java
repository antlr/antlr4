/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.cpp;

import org.antlr.v4.codegen.model.Parser;
import org.antlr.v4.test.runtime.BaseRuntimeTest;
import org.antlr.v4.test.runtime.RuntimeTestDescriptor;
import org.antlr.v4.test.runtime.category.ParserTests;
import org.antlr.v4.test.runtime.descriptors.CompositeParsersDescriptors;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@Category(ParserTests.class)
@RunWith(Parameterized.class)
public class TestCompositeParsers extends BaseRuntimeTest {
	public TestCompositeParsers(RuntimeTestDescriptor descriptor) {
		super(descriptor,new BaseCppTest());
	}

	@Parameterized.Parameters(name="{0}")
	public static RuntimeTestDescriptor[] getAllTestDescriptors() {
		return BaseRuntimeTest.getRuntimeTestDescriptors(CompositeParsersDescriptors.class, "Cpp");
	}
}
