package org.antlr.v4.test;

import org.antlr.v4.analysis.LinearApproximator;
import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.DecisionState;
import org.antlr.v4.automata.NFA;
import org.antlr.v4.automata.NFAState;
import org.antlr.v4.tool.Grammar;
import org.junit.Test;

public class TestLinearApproximateLookahead extends BaseTest {
	@Test
	public void testLL1Block() throws Exception {
		String g =
			"parser grammar P;\n"+
			"a : A | B ;";
		String expecting =
			"s0-A->:s1=>1\n" +
			"s0-B->:s2=>2\n";
		checkRule(g, "a", expecting);
	}

	@Test
	public void testLL1Chain() throws Exception {
		String g =
			"parser grammar P;\n"+
			"a : b | B ;\n" +
			"b : c | C ;\n" +
			"c : D ;";
		String expecting =
			"s0-B->:s2=>2\n" +
			"s0-{D, C}->:s1=>1\n";
		checkRule(g, "a", expecting);
	}

	@Test
	public void testLL2Block() throws Exception {
		String g =
			"parser grammar P;\n"+
			"a : A B | A C ;";
		String expecting =
			"s0-A->s1\n" +
			"s0-A->s3\n" +
			"s1-B->:s2=>1\n" +
			"s3-C->:s4=>2\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testLL1NullableRuleRef() throws Exception {
		String g =
			"parser grammar P;\n"+
			"a : b B | X b C ;\n" +
			"b : A | ;";
		String expecting =
			"s0-X->:s2=>2\n" +
			"s0-{A, B}->:s1=>1\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testLL2FOLLOW() throws Exception {
		String g =
			"parser grammar P;\n"+
			"a : X b Q | Y b Z ;\n" +
			"b : A B | A | ;\n" +
			"c : b C ;";
		String expecting =
			"s0-A->s1\n" +
			"s0-A->s3\n" +
			"s0-{Q, C, Z}->s5\n" +
			"s1-B->:s2=>1\n" +
			"s3-{Q, C, Z}->:s4=>2\n" +
			"s5-EOF->:s6=>3\n";
		checkRule(g, "b", expecting);
	}

	@Test
	public void testNonDetLL1Block() throws Exception {
		String g =
			"parser grammar P;\n"+
			"a : A | B | A ;";
		String expecting = null;
		checkRule(g, "a", expecting);
	}

	@Test
	public void testNonDetLL2Block() throws Exception {
		String g =
			"parser grammar P;\n"+
			"a : A B | A B | C ;";
		String expecting = null;
		checkRule(g, "a", expecting);
	}

	void checkRule(String gtext, String ruleName, String expecting)
		throws Exception
	{
		Grammar g = new Grammar(gtext);
		NFA nfa = createNFA(g);
		NFAState s = nfa.ruleToStartState.get(g.getRule(ruleName));
		if ( s==null ) {
			System.err.println("no start state for rule "+ruleName);
			return;
		}
		DecisionState blk = (DecisionState)s.transition(0).target;
		LinearApproximator lin = new LinearApproximator(g);
		DFA dfa = lin.createDFA(blk);
		String result = null;
		if ( dfa!=null ) result = dfa.toString();
		assertEquals(expecting, result);
	}
}
