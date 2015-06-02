package org.antlr.v4.test.runtime.java;

public class AbstractParserTestMethod extends JUnitTestMethod {

	public String startRule;

	public AbstractParserTestMethod(String name, String grammarName, String startRule) {
		super(name, grammarName, null, null, null, null);
		this.startRule = startRule;
	}

}
