package org.antlr.v4.test.runtime;

public abstract class TestContext {

	public static boolean isTravisCI() {
		return "true".equals(String.valueOf(System.getenv("TRAVIS")).toLowerCase());
	}

	public static boolean isGitHubCI() {
		return "true".equals(String.valueOf(System.getenv("CI")).toLowerCase());
	}

	public static boolean isAppVeyorCI() {
		return "true".equals(String.valueOf(System.getenv("APPVEYOR")).toLowerCase());
	}

	public static boolean isCircleCI() {
		return "true".equals(String.valueOf(System.getenv("CIRCLECI")).toLowerCase());
	}

	public static boolean isCI() {
		return isAppVeyorCI() || isCircleCI() || isGitHubCI() || isTravisCI();
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean isSupportedTarget(String target) {
		if(isAppVeyorCI())
			return !target.matches("Swift");
		else
			return true;
	}

}
