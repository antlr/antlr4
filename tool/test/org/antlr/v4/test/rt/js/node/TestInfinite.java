package org.antlr.v4.test.rt.js.node;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestInfinite extends BaseTest {

	@BeforeClass
	public static void before() {
		System.setProperty("antlr-javascript-test-dir", "/Users/ericvergnaud/Development/antlr4/antlr/antlr4-javascript/src/test");
		System.setProperty("antlr-javascript-erase-test-dir", "false");
	}
	
	@Test
	public void testLexer() throws Exception {
		String textToParse = "() any text 1\n"
				+ "\n"
				+ "            any text 2\n"
				+ "any text 3";
		InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("infinite/TestLexer.g4");
		mkdir(tmpdir);
		writeFile(tmpdir, "TestLexer.g4", input);
		input.close();
		String found = execLexer("TestLexer.g4", null, "TestLexer", textToParse, false);
	}

}
