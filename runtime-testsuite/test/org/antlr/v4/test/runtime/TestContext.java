package org.antlr.v4.test.runtime;

public abstract class TestContext {

	public static boolean isTravisCI() {
		return "true".equals(String.valueOf(System.getenv("TRAVIS")).toLowerCase());
	}

	public static boolean isAppVeyorCI() {
		return "true".equals(String.valueOf(System.getenv("APPVEYOR")).toLowerCase());
	}

	public static boolean isSupportedTarget(String target) {
		if(isAppVeyorCI())
			return !target.matches("Swift|Node");
		else
			return true;
	}
}
