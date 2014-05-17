package org.antlr.v4.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;

import org.antlr.v4.tool.ast.GrammarRootAST;
import org.junit.Test;

public class TestGrammarOptions extends BaseTest {
	
	private static final String GRAMMAR_FILE_NAME = "T.g4";
	
	private static final String GRAMMAR_WITHOUT_OPTIONS =
			"grammar T;\n" +
			"a: 'x';";
	
	private static final String GRAMMAR_WITH_OPTIONS =
			"grammar T;\n" +
			"options {language=Python;}" +
			"a: 'x';";

	@Test
	public void testDefaultOption() {
		this.mkdir(this.tmpdir);
		BaseTest.writeFile(this.tmpdir, GRAMMAR_FILE_NAME, GRAMMAR_WITHOUT_OPTIONS);
		
		final File grammarFile = new File(this.tmpdir, GRAMMAR_FILE_NAME);
		final GrammarRootAST root = this.newTool().loadGrammar(grammarFile.getAbsolutePath());
		
		assertEquals("Java", root.getOptionString("language"));
	}
	
	@Test
	public void testGrammarOption() {
		this.mkdir(this.tmpdir);
		BaseTest.writeFile(this.tmpdir, GRAMMAR_FILE_NAME, GRAMMAR_WITH_OPTIONS);
		
		final File grammarFile = new File(this.tmpdir, GRAMMAR_FILE_NAME);
		final GrammarRootAST root = this.newTool().loadGrammar(grammarFile.getAbsolutePath());
		
		assertEquals("Python", root.getOptionString("language"));
	}
	
	@Test
	public void testCommandLineOption() {
		
		final HashMap<String, String> commandLineOptions = new HashMap<String, String>();
		commandLineOptions.put("language", "C");
		
		this.mkdir(this.tmpdir);
		
		{
			BaseTest.writeFile(this.tmpdir, GRAMMAR_FILE_NAME, GRAMMAR_WITHOUT_OPTIONS);
			
			final File grammarFile = new File(this.tmpdir, GRAMMAR_FILE_NAME);
			final GrammarRootAST root = this.newTool().loadGrammar(grammarFile.getAbsolutePath());
			root.applyCommandLineOptions(commandLineOptions);
			assertEquals("C", root.getOptionString("language"));
		}
		
		{
			BaseTest.writeFile(this.tmpdir, GRAMMAR_FILE_NAME, GRAMMAR_WITH_OPTIONS);
			
			final File grammarFile = new File(this.tmpdir, GRAMMAR_FILE_NAME);
			final GrammarRootAST root = this.newTool().loadGrammar(grammarFile.getAbsolutePath());
			root.applyCommandLineOptions(commandLineOptions);
			assertEquals("C", root.getOptionString("language"));
		}
	}
	
	@Test
	public void testArbitraryGrammarOption() {
		final String grammar =
				"grammar T;\n" +
				"options {color=purple;}" +
				"a: 'x';";

		this.mkdir(this.tmpdir);
		BaseTest.writeFile(this.tmpdir, GRAMMAR_FILE_NAME, grammar);
		
		final File grammarFile = new File(this.tmpdir, GRAMMAR_FILE_NAME);
		final GrammarRootAST root = this.newTool().loadGrammar(grammarFile.getAbsolutePath());
		assertEquals("purple", root.getOptionString("color"));
		
		final HashMap<String, String> commandLineOptions = new HashMap<String, String>();
		commandLineOptions.put("size", "large");
		
		root.applyCommandLineOptions(commandLineOptions);
		assertEquals("large", root.getOptionString("size"));
	}
	
}
