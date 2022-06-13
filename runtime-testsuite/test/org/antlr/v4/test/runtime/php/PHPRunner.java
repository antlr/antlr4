/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.php;

import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.test.runtime.*;

public class PHPRunner extends RuntimeRunner {
	private static final Map<String, String> environment;

	static {
		environment = new HashMap<>();
		environment.put("RUNTIME", getRuntimePath("PHP"));
	}

	@Override
	public String getLanguage() {
		return "PHP";
	}

	@Override
	public Map<String, String> getExecEnvironment() {
		return environment;
	}
}
