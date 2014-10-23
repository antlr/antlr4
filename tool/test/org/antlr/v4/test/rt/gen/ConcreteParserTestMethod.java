package org.antlr.v4.test.rt.gen;

import java.io.File;

import org.stringtemplate.v4.STGroup;

public class ConcreteParserTestMethod extends TestMethod {

	public String baseName;
	
	public ConcreteParserTestMethod(String name, String input, String expectedOutput,
			String expectedErrors, Integer index) {
		super(name, null, input, expectedOutput, expectedErrors, index);
		this.baseName = name;
	}
	
	@Override
	public void loadGrammars(File grammarDir, String testFileName) throws Exception {
	}

	@Override
	public void generateGrammars(STGroup group) {
	}

}
