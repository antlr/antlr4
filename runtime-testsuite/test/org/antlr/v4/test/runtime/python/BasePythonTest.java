/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.python;

import org.antlr.v4.test.runtime.*;
import org.stringtemplate.v4.ST;

public abstract class BasePythonTest extends BaseRuntimeTestSupport {
	@Override
	public String getExtension() { return "py"; }

	@Override
	public String getRuntimeToolName() {
		String toolFileName = System.getProperty(getPropertyPrefix() + "-exec");
		if (toolFileName != null && toolFileName.length() > 0) {
			return toolFileName;
		}

		return getLanguage().toLowerCase();
	}

	@Override
	protected void addExtraRecognizerParameters(ST template) {
		template.add("python3", getLanguage().equals("Python3"));
	}
}
