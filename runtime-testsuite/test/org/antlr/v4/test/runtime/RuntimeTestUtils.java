/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime;

import org.antlr.v4.automata.ATNPrinter;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.Rule;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class RuntimeTestUtils {
	public static final String NewLine = System.getProperty("line.separator");
	public static final String PathSeparator = System.getProperty("path.separator");
	public static final String FileSeparator = System.getProperty("file.separator");
	public static final String TempDirectory = System.getProperty("java.io.tmpdir");

	public final static Path runtimePath;
	public final static Path runtimeTestsuitePath;
	public final static Path resourcePath;

	private final static Map<String, String> resourceCache = new HashMap<>();
	private static OSType detectedOS;
	private static Boolean isWindows;

	static {
		String locationPath = RuntimeTestUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		if (isWindows()) {
			locationPath = locationPath.replaceFirst("/", "");
		}
		Path potentialRuntimeTestsuitePath = Paths.get(locationPath, "..", "..").normalize();
		Path potentialResourcePath = Paths.get(potentialRuntimeTestsuitePath.toString(), "resources");

		if (Files.exists(potentialResourcePath)) {
			runtimeTestsuitePath = potentialRuntimeTestsuitePath;
		}
		else {
			runtimeTestsuitePath = Paths.get("..", "runtime-testsuite").normalize();
		}

		runtimePath = Paths.get(runtimeTestsuitePath.toString(), "..", "runtime").normalize();
		resourcePath = Paths.get(runtimeTestsuitePath.toString(), "resources");
	}

	public static boolean isWindows() {
		if (isWindows == null) {
			isWindows = getOS() == OSType.Windows;
		}

		return isWindows;
	}

	public static OSType getOS() {
		if (detectedOS == null) {
			String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
			if (os.contains("mac") || os.contains("darwin")) {
				detectedOS = OSType.Mac;
			}
			else if (os.contains("win")) {
				detectedOS = OSType.Windows;
			}
			else if (os.contains("nux")) {
				detectedOS = OSType.Linux;
			}
			else {
				detectedOS = OSType.Unknown;
			}
		}
		return detectedOS;
	}

	public static synchronized String getTextFromResource(String name) {
		try {
			String text = resourceCache.get(name);
			if (text == null) {
				Path path = Paths.get(resourcePath.toString(), name);
				text = new String(Files.readAllBytes(path));
				resourceCache.put(name, text);
			}
			return text;
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void checkRuleATN(Grammar g, String ruleName, String expecting) {
		Rule r = g.getRule(ruleName);
		ATNState startState = g.getATN().ruleToStartState[r.index];
		ATNPrinter serializer = new ATNPrinter(g, startState);
		String result = serializer.asString();

		assertEquals(expecting, result);
	}

	public static String joinLines(Object... args) {
		StringBuilder result = new StringBuilder();
		for (Object arg : args) {
			String str = arg.toString();
			result.append(str);
			if (!str.endsWith("\n"))
				result.append("\n");
		}
		return result.toString();
	}
}
