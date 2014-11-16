package org.antlr.v4.test.rt.gen;

import java.io.File;

import org.stringtemplate.v4.STGroup;

public abstract class TestMethod {

	public String name;
	public Grammar grammar;
	public String afterGrammar;
	public String input;
	public String expectedOutput;
	public String expectedErrors;
	public boolean debug = false;
	
	protected TestMethod(String name, String grammarName, String input, 
			String expectedOutput, String expectedErrors, Integer index) {
		this.name = name + (index==null ? "" : "_" + index);
		this.grammar = new Grammar(name, grammarName);
		this.input = Generator.escape(input);
		this.expectedOutput = Generator.escape(expectedOutput);
		this.expectedErrors = Generator.escape(expectedErrors);
	}

	public void loadGrammars(File grammarDir, String testFileName) throws Exception {
		grammar.load(new File(grammarDir, testFileName));
	}

	public void generateGrammars(STGroup group) {
		grammar.generate(group);
	}

}
