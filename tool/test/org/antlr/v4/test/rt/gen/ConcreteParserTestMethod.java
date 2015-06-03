package org.antlr.v4.test.rt.gen;

import org.stringtemplate.v4.STGroup;

import java.io.File;

public class ConcreteParserTestMethod extends JUnitTestMethod {

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
