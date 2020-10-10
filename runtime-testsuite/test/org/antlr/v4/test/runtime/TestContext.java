package org.antlr.v4.test.runtime;

public abstract class TestContext {

	public static boolean isTravisCI() {
		return "true".equals(System.getenv("TRAVIS"));
	}

	public static boolean isAppVeyorCI() {
		return "true".equals(System.getenv("APPVEYOR"));
	}

	public static boolean isUnsupportedTarget(String target) {
		if(isAppVeyorCI())
			return !"CSharp".equals(target);
			// return target.matches("Cpp|Node");
		else
			return false;
	}
}
