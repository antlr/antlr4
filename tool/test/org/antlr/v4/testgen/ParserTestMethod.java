package org.antlr.v4.testgen;

public class ParserTestMethod extends TestMethod {

	public String startRule;

	public ParserTestMethod(String name, String grammarName, String startRule,
			String input, String expectedOutput, String expectedErrors, Integer index) {
		super(name, grammarName, input, expectedOutput, expectedErrors, index);
		this.startRule = startRule;
	}
	
}
