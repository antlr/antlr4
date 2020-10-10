package org.antlr.v4.test.runtime;

public abstract class TestContext {

	public static boolean isTravisCI() {
		String value = System.getenv("TRAVIS");
		if(value==null)
			return false;
		else
			return "true".equals(value.toLowerCase());
	}

	public static boolean isAppVeyorCI() {
		System.out.println(System.getenv("APPVEYOR"));
		String value = System.getenv("APPVEYOR");
		if(value==null)
			return false;
		else
			return "true".equals(value.toLowerCase());
	}

	public static boolean isUnsupportedTarget(String target) {
		if(isAppVeyorCI())
			return !"CSharp".equals(target);
			// return target.matches("Cpp|Node");
		else
			return false;
	}
}
