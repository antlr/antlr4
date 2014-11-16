package org.antlr.v4.test.rt.gen;

import java.io.File;

import org.stringtemplate.v4.STGroup;

public class CompositeParserTestMethod extends ParserTestMethod {

	public Grammar[] slaveGrammars;
	public boolean slaveIsLexer = false;
	
	public CompositeParserTestMethod(String name, String grammarName,
			String startRule, String input, String expectedOutput,
			String expectedErrors, String ... slaves) {
		super(name, grammarName, startRule, input, expectedOutput, expectedErrors);
		this.slaveGrammars = new Grammar[slaves.length];
		for(int i=0;i<slaves.length;i++)
			this.slaveGrammars[i] = new Grammar(name + "_" + slaves[i], slaves[i]);
			
	}

	@Override
	public void loadGrammars(File grammarDir, String testFileName) throws Exception {
		for(Grammar slave : slaveGrammars)
			slave.load(new File(grammarDir, testFileName));
		super.loadGrammars(grammarDir, testFileName);
	}
	
	@Override
	public void generateGrammars(STGroup group) {
		for(Grammar slave : slaveGrammars)
			slave.generate(group);
		super.generateGrammars(group);
	}

}
