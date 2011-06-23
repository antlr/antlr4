package org.antlr.v4.test;

import org.antlr.runtime.RecognitionException;
import org.antlr.v4.Tool;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.Test;
import org.stringtemplate.v4.ST;

public class TestLexerAttributes extends BaseTest {
	@Test
		public void testSetType() throws RecognitionException {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar T;\n" +
			"A : 'a' {#$type=101;#} ;\n"
		);
		Tool antlr = new Tool();
		antlr.process(g);
		CodeGenerator gen = new CodeGenerator(g);
		ST outputFileST = gen.generate();
		String output = outputFileST.render();
		int start = output.indexOf('#');
		int end = output.lastIndexOf('#');
		String snippet = output.substring(start+1,end);
		assertEquals("type = 101;", snippet);
	}

}
