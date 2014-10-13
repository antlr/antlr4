package org.antlr.v4.testgen;

public class LexerTestMethod extends TestMethod {

	public String[] outputLines;

	public LexerTestMethod(String name, String grammarName, String input, 
			String expectedOutput, String expectedErrors, Integer index) {
		super(name, grammarName, input, expectedOutput, expectedErrors, index);
		outputLines = expectedOutput.split("\n");
		for(int i=0;i<outputLines.length;i++)
			outputLines[i] = Generator.escape(outputLines[i]);
	}
	
}
