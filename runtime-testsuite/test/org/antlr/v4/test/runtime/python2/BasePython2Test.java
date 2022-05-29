/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.python2;

import org.antlr.v4.test.runtime.python.BasePythonTest;

import java.util.Collections;
import java.util.List;

public class BasePython2Test extends BasePythonTest {

	@Override
	public String getLanguage() {
		return "Python2";
	}

	@Override
	protected List<String> getPythonExecutables() {
		return Collections.singletonList("python2.7");
	}
}
