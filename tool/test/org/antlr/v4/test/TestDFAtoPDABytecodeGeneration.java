package org.antlr.v4.test;

import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.DecisionState;
import org.antlr.v4.automata.NFA;
import org.antlr.v4.codegen.PDABytecodeGenerator;
import org.antlr.v4.runtime.pda.Bytecode;
import org.antlr.v4.runtime.pda.PDA;
import org.antlr.v4.tool.Grammar;
import org.junit.Test;

/** */
public class TestDFAtoPDABytecodeGeneration extends BaseTest {
	@Test public void testAorB() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A | B ;");
		String expecting =
			"0000:\tsplit         7, 15\n" +
			"0007:\tmatch8        5\n" +
			"0009:\tjmp           12\n" +
			"0012:\taccept        2\n" +
			"0015:\tmatch8        4\n" +
			"0017:\tjmp           20\n" +
			"0020:\taccept        1\n";
		checkBytecode(g, 0, expecting);
	}

	@Test public void testABorAC() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A B | A C ;");
		String expecting =
			"0000:\tmatch8        4\n" +
			"0002:\tjmp           5\n" +
			"0005:\tsplit         12, 20\n" +
			"0012:\tmatch8        6\n" +
			"0014:\tjmp           17\n" +
			"0017:\taccept        2\n" +
			"0020:\tmatch8        5\n" +
			"0022:\tjmp           25\n" +
			"0025:\taccept        1\n";
		checkBytecode(g, 0, expecting);
	}

	@Test public void testAPlus() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A+ B | A+ C ;");
		String expecting =
			"0000:\tmatch8        4\n" +
			"0002:\tjmp           5\n" +
			"0005:\tsplit         14, 22, 30\n" +
			"0014:\tmatch8        6\n" +
			"0016:\tjmp           19\n" +
			"0019:\taccept        2\n" +
			"0022:\tmatch8        5\n" +
			"0024:\tjmp           27\n" +
			"0027:\taccept        1\n" +
			"0030:\tmatch8        4\n" +
			"0032:\tjmp           5\n";
		checkBytecode(g, 2, expecting);
	}

	// TODO: ORDER OF TESTS MATTERS? DFA edge orders get changed. ack!

	void checkBytecode(Grammar g, int decision, String expecting) {
		NFA nfa = createNFA(g);
		DecisionState blk = nfa.decisionToNFAState.get(decision);
		DFA dfa = createDFA(g, blk);
//		Edge e0 = dfa.states.get(1).edge(0);
//		Edge e1 = dfa.states.get(1).edge(1);
//		e0.target = e1.target;
//		System.out.print("altered DFA="+dfa);		
		PDA PDA = PDABytecodeGenerator.getPDA(dfa);
		assertEquals(expecting, Bytecode.disassemble(PDA.code, false));
	}	
}
