package org.antlr.v4.test.runtime;

import junit.framework.TestCase;
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

public abstract class RuntimeTestUtils {
	public static final String NewLine = System.getProperty("line.separator");
	public static final String PathSeparator = System.getProperty("path.separator");
	public static final String FileSeparator = System.getProperty("file.separator");

	public final static Path runtimeTestsuitePath;

	private final static Path resourcePath;
	private final static Object resourceLockObject = new Object();
	private final static Map<String, String> resourceCache = new HashMap<>();
	private static String detectedOS;
	private static Boolean isWindows;

	static {
		String locationPath = BaseRuntimeTestSupport.class.getProtectionDomain().getCodeSource().getLocation().getPath();
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

		resourcePath = Paths.get(runtimeTestsuitePath.toString(), "resources");
	}

	public static boolean isWindows() {
		if (isWindows == null) {
			isWindows = getOS().equalsIgnoreCase("windows");
		}

		return isWindows;
	}

	public static String getOS() {
		if (detectedOS == null) {
			String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
			if (os.contains("mac") || os.contains("darwin")) {
				detectedOS = "mac";
			}
			else if (os.contains("win")) {
				detectedOS = "windows";
			}
			else if (os.contains("nux")) {
				detectedOS = "linux";
			}
			else {
				detectedOS = "unknown";
			}
		}
		return detectedOS;
	}

	public static String getTextFromResource(String name) {
		try {
			String text = resourceCache.get(name);
			if (text == null) {
				synchronized (resourceLockObject) {
					Path path = Paths.get(resourcePath.toString(), name);
					text = new String(Files.readAllBytes(path));
					resourceCache.put(name, text);
				}
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

		TestCase.assertEquals(expecting, result);
	}
}
