package org.antlr.v4.test.rt.gen;

public class AbstractParserTestMethod extends TestMethod {

	public String startRule;

	public AbstractParserTestMethod(String name, String grammarName, String startRule) {
		super(name, grammarName, null, null, null, null);
		this.startRule = startRule;
	}
	
}
