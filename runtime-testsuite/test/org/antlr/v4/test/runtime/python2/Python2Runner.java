/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.python2;

import org.antlr.v4.test.runtime.python.PythonRunner;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Python2Runner extends PythonRunner {
	public final static Map<String, String> environment;

	static {
		environment = new HashMap<>();
		environment.put("PYTHONPATH", Paths.get(getRuntimePath("Python2"), "src").toString());
		environment.put("PYTHONIOENCODING", "utf-8");
	}

	@Override
	public String getLanguage() {
		return "Python2";
	}

	@Override
	public Map<String, String> getExecEnvironment() {
		return environment;
	}
}
