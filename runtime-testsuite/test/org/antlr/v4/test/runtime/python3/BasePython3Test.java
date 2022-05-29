/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.python3;

import org.antlr.v4.test.runtime.python.BasePythonTest;
import java.util.Arrays;
import java.util.List;

public class BasePython3Test extends BasePythonTest {
	@Override
	public String getLanguage() {
		return "Python3";
	}

	@Override
	protected List<String> getPythonExecutables() {
		return Arrays.asList("python3.7", "python3.8");
	} // force 3.7 or 3.8
}
