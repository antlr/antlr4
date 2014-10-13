package org.antlr.v4.testgen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

public class TestFile {
	
	List<TestMethod> unitTests = new ArrayList<TestMethod>();
	public String name;
	public List<String> tests = new ArrayList<String>();
	public boolean importErrorQueue = false;
	public boolean importGrammar = false;
	
	public TestFile(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void addParserTest(File grammarDir, String name, String grammarName, String methodName,
			String input, String expectedOutput, String expectedErrors) throws Exception {
		addParserTest( grammarDir, name, grammarName, methodName, input, expectedOutput, expectedErrors, null);
	}
	
	public ParserTestMethod addParserTest(File grammarDir, String name, String grammarName, String methodName,
			String input, String expectedOutput, String expectedErrors, Integer index) throws Exception {
		ParserTestMethod tm = new ParserTestMethod(name, grammarName, methodName, input, expectedOutput, expectedErrors, index);
		tm.loadGrammars(grammarDir, this.name);
		unitTests.add(tm);
		return tm;
	}

	public CompositeParserTestMethod addCompositeParserTest(File grammarDir, String name, String grammarName, String methodName,
			String input, String expectedOutput, String expectedErrors, String ... slaves) throws Exception {
		CompositeParserTestMethod tm = new CompositeParserTestMethod(name, grammarName, methodName, input, expectedOutput, expectedErrors, slaves);
		tm.loadGrammars(grammarDir, this.name);
		unitTests.add(tm);
		return tm;
	}

	public LexerTestMethod addLexerTest(File grammarDir, String name, String grammarName,
			String input, String expectedOutput, String expectedErrors) throws Exception {
		return addLexerTest(grammarDir, name, grammarName, input, expectedOutput, expectedErrors, null);
	}

	public LexerTestMethod addLexerTest(File grammarDir, String name, String grammarName,
			String input, String expectedOutput, String expectedErrors, Integer index) throws Exception {
		LexerTestMethod tm = new LexerTestMethod(name, grammarName, input, expectedOutput, expectedErrors, index);
		tm.loadGrammars(grammarDir, this.name);
		unitTests.add(tm);
		return tm;
	}

	public CompositeLexerTestMethod addCompositeLexerTest(File grammarDir, String name, String grammarName,
			String input, String expectedOutput, String expectedErrors, String ... slaves) throws Exception {
		CompositeLexerTestMethod tm = new CompositeLexerTestMethod(name, grammarName, input, expectedOutput, expectedErrors, slaves);
		tm.loadGrammars(grammarDir, this.name);
		unitTests.add(tm);
		return tm;
	}

	public void generateUnitTests(STGroup group) {
		for(TestMethod tm : unitTests) {
			tm.generateGrammars(group);
			String name = tm.getClass().getSimpleName();
			ST template = group.getInstanceOf(name);
			template.add("test", tm);
			tests.add(template.render());
		}
	}



	

}
