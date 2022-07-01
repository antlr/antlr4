/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.python;

import org.antlr.v4.test.runtime.*;
import org.stringtemplate.v4.ST;

public abstract class PythonRunner extends RuntimeRunner {
	@Override
	public String getExtension() { return "py"; }

	@Override
	protected void addExtraRecognizerParameters(ST template) {
		template.add("python3", getLanguage().equals("Python3"));
	}
}
