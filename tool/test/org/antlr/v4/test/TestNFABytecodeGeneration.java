package org.antlr.v4.test;

import org.antlr.v4.Tool;
import org.antlr.v4.codegen.NFABytecodeGenerator;
import org.antlr.v4.runtime.nfa.Bytecode;
import org.antlr.v4.runtime.nfa.NFA;
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
		NFA nfa = NFABytecodeGenerator.getBytecode(g, LexerGrammar.DEFAULT_MODE_NAME);
		assertEquals(expecting, Bytecode.disassemble(nfa.code));
	}
}
