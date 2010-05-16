package org.antlr.v4.test;

import org.antlr.v4.Tool;
import org.antlr.v4.codegen.NFABytecodeGenerator;
import org.antlr.v4.runtime.pda.Bytecode;
import org.antlr.v4.runtime.pda.PDA;
import org.antlr.v4.semantics.SemanticPipeline;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.Test;

public class TestNFABytecodeGeneration extends BaseTest {
	@Test public void testString() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'ab' ;");
		String expecting =
			"0000:\tsplit         5\n" +
			"0005:\tmatch8        'a'\n" +
			"0007:\tmatch8        'b'\n" +
			"0009:\taccept        4\n";
		checkBytecode(g, expecting);
	}

	@Test public void testIDandIntandKeyword() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'ab';\n" +
			"B : 'a'..'z'+ ;\n" +
			"I : '0'..'9'+ ;\n");
		String expecting =
			"0000:\tsplit         9, 16, 29\n" +
			"0009:\tmatch8        'a'\n" +
			"0011:\tmatch8        'b'\n" +
			"0013:\taccept        4\n" +
			"0016:\trange8        'a', 'z'\n" +
			"0019:\tsplit         16, 26\n" +
			"0026:\taccept        5\n" +
			"0029:\trange8        '0', '9'\n" +
			"0032:\tsplit         29, 39\n" +
			"0039:\taccept        6\n";
		checkBytecode(g, expecting);
	}

	@Test public void testNonGreedy() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"\n" +
			"CMT : '/*' (options {greedy=false;}:.)* '*/' ;\n" +
			"ID  : 'ab' ;\n");
		String expecting =
			"0000:\tsplit         7, 29\n" +
			"0007:\tmatch8        '/'\n" +
			"0009:\tmatch8        '*'\n" +
			"0011:\tsplit         22, 18\n" +
			"0018:\twildcard        \n" +
			"0019:\tjmp           11\n" +
			"0022:\tmatch8        '*'\n" +
			"0024:\tmatch8        '/'\n" +
			"0026:\taccept        4\n" +
			"0029:\tmatch8        'a'\n" +
			"0031:\tmatch8        'b'\n" +
			"0033:\taccept        5\n";
		checkBytecode(g, expecting);
	}

	@Test public void testCallFragment() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"I : D+ ;\n" +
			"fragment D : '0'..'9'+ ;\n");
		String expecting =
			"0000:\tsplit         5\n" +
			"0005:\tcall          18\n" +
			"0008:\tsplit         5, 15\n" +
			"0015:\taccept        4\n" +
			"0018:\trange8        '0', '9'\n" +
			"0021:\tsplit         18, 28\n" +
			"0028:\tret             \n";
		checkBytecode(g, expecting);
	}

	@Test public void testLabeledChar() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : a='a' ;\n");
		String expecting =
			"0000:\tsplit         5\n" +
			"0005:\tlabel         0\n" +
			"0008:\tmatch8        'a'\n" +
			"0010:\tsave          0\n" +
			"0013:\taccept        4\n";
		checkBytecode(g, expecting);
	}

	@Test public void testLabeledString() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : a='aa' ;\n");
		String expecting =
			"0000:\tsplit         5\n" +
			"0005:\tlabel         0\n" +
			"0008:\tmatch8        'a'\n" +
			"0010:\tmatch8        'a'\n" +
			"0012:\tsave          0\n" +
			"0015:\taccept        4\n";
		checkBytecode(g, expecting);
	}

	@Test public void testLabeledToken() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"I : d=D ;\n" +
			"fragment D : '0'..'9'+ ;\n");
		String expecting =
			"0000:\tsplit         5\n" +
			"0005:\tlabel         0\n" +
			"0008:\tcall          17\n" +
			"0011:\tsave          0\n" +
			"0014:\taccept        4\n" +
			"0017:\trange8        '0', '9'\n" +
			"0020:\tsplit         17, 27\n" +
			"0027:\tret             \n";
		checkBytecode(g, expecting);
	}

	@Test public void testLabelIndexes() throws Exception {
		// labels indexed from 0 in each rule
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : a='a' ;\n" +
			"B : a='b' b='c' ;\n");
		String expecting =
			"0000:\tsplit         7, 18\n" +
			"0007:\tlabel         0\n" +
			"0010:\tmatch8        'a'\n" +
			"0012:\tsave          0\n" +
			"0015:\taccept        4\n" +
			"0018:\tlabel         1\n" +
			"0021:\tmatch8        'b'\n" +
			"0023:\tsave          1\n" +
			"0026:\tlabel         2\n" +
			"0029:\tmatch8        'c'\n" +
			"0031:\tsave          2\n" +
			"0034:\taccept        5\n";
		checkBytecode(g, expecting);
	}

	@Test public void testLabelReuseWithinRule() throws Exception {
		// labels indexed from 0 in each rule
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : a='b' a='c' ;\n");
		String expecting =
			"0000:\tsplit         5\n" +
			"0005:\tlabel         0\n" +
			"0008:\tmatch8        'b'\n" +
			"0010:\tsave          0\n" +
			"0013:\tlabel         0\n" +
			"0016:\tmatch8        'c'\n" +
			"0018:\tsave          0\n" +
			"0021:\taccept        4\n";
		checkBytecode(g, expecting);
	}

	@Test public void testAction() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : {foo} 'a' | 'b' {bar} ;\n");
		String expecting =
			"0000:\tsplit         5\n" +
			"0005:\tsplit         12, 22\n" +
			"0012:\taction        1, 0\n" +
			"0017:\tmatch8        'a'\n" +
			"0019:\tjmp           29\n" +
			"0022:\tmatch8        'b'\n" +
			"0024:\taction        1, 1\n" +
			"0029:\taccept        4\n";
		checkBytecode(g, expecting);
	}

	@Test public void testSempred() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : {foo}? 'a' | 'b' {bar}? ;\n");
		String expecting =
			"0000:\tsplit         5\n" +
			"0005:\tsplit         12, 22\n" +
			"0012:\tsempred       1, 0\n" +
			"0017:\tmatch8        'a'\n" +
			"0019:\tjmp           29\n" +
			"0022:\tmatch8        'b'\n" +
			"0024:\tsempred       1, 1\n" +
			"0029:\taccept        4\n";
		checkBytecode(g, expecting);
	}

	public void _template() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"\n");
		String expecting =
			"\n";
		checkBytecode(g, expecting);
	}

	void checkBytecode(LexerGrammar g, String expecting) {
		if ( g.ast!=null && !g.ast.hasErrors ) {
			System.out.println(g.ast.toStringTree());
			Tool antlr = new Tool();
			SemanticPipeline sem = new SemanticPipeline(g);
			sem.process();
			if ( g.getImportedGrammars()!=null ) { // process imported grammars (if any)
				for (Grammar imp : g.getImportedGrammars()) {
					antlr.process(imp);
				}
			}
		}
		PDA PDA = NFABytecodeGenerator.getBytecode(g, LexerGrammar.DEFAULT_MODE_NAME);
		assertEquals(expecting, Bytecode.disassemble(PDA.code));
	}
}
