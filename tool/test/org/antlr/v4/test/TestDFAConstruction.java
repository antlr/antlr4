package org.antlr.v4.test;

import org.antlr.v4.analysis.LeftRecursionDetector;
import org.antlr.v4.automata.NFA;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.Message;
import org.junit.Test;

import java.util.List;

public class TestDFAConstruction extends BaseTest {
	@Test public void testSimpleLinearApproxDecisionAsDFA() throws Exception {
		String g =
			"parser grammar P;\n"+
			"a : A | B ;";
		String expecting =
			"s0-A->:s1=>1\n" +
			"s0-B->:s2=>2\n";
		checkRuleDFA(g, "a", expecting);
	}

	@Test public void testApproxRecur() throws Exception {
		String g =
			"parser grammar A;\n" +
			"a : e X\n" +
			"  | e Y\n" +
			"  ;\n" +
			"e : L e R\n" +
			"  | I\n" +
			"  ;";
		String expecting =
			"s0-I->s2\n" +
			"s0-L->s1\n" +
			"s1-I->s2\n" +
			"s1-L->s1\n" +
			"s2-R->s3\n" +
			"s2-X->:s5=>1\n" +
			"s2-Y->:s4=>2\n" +
			"s3-R->s3\n" +
			"s3-X->:s5=>1\n" +
			"s3-Y->:s4=>2\n";
		checkRuleDFA(g, "a", expecting);
	}

	@Test public void checkNullableRuleAndMultipleCalls() throws Exception {
		String g =
			"parser grammar B;\n" +
			"  \n" +
			"a : b X\n"+
			"  | b Y\n"+
			"  ; \n" +
			"b : c D\n"+
			"  | c E\n"+
			"  ;\n" +
			"c : C | ;";
		String expecting =
			"s0-C->s3\n" +
			"s0-D->s1\n" +
			"s0-E->s2\n" +
			"s1-X->:s5=>1\n" +
			"s1-Y->:s4=>2\n" +
			"s2-X->:s5=>1\n" +
			"s2-Y->:s4=>2\n" +
			"s3-D->s1\n" +
			"s3-E->s2\n";
		checkRuleDFA(g, "a", expecting);
	}

	@Test public void avoidsGlobalFollowSequence() throws Exception {
		String g =
			"parser grammar C;\n" +
			"a : b X\n" +
			"  | b Y\n" +
			"  ; \n" +
			"b : F\n" +
			"  |\n" +
			"  ; \n" +
			"q : b Q ;";
		String expecting =
			"s0-F->s1\n" +
			"s0-X->:s3=>1\n" +
			"s0-Y->:s2=>2\n" +
			"s1-X->:s3=>1\n" +
			"s1-Y->:s2=>2\n";
		checkRuleDFA(g, "a", expecting);
	}

	@Test public void strongLL() throws Exception {
		String g =
			"parser grammar D;\n" +
			"\n" +
			"s : X a A B\n" +
			"  | Y a B\n" +
			"  ;\n" +
			"a : A | B | ;";
		// AB predicts 1 and 3 but AB only happens when called from 1st alt for 3rd alt
		// In that case, 1st alt would be AA not AB.  LL(2) but not strong LL(2)
		// dup rules to reduce to strong LL(2)
		String expecting =
			"s0-A->s1\n" +
			"s0-B->s2\n" +
			"s1-A->:s3=>1\n" +
			"s1-B->:s4=>1\n" +
			"s2-A->:s5=>2\n" +
			"s2-B->:s6=>2\n" +
			"s2-EOF->:s7=>3\n";
		List<Message> msgs = checkRuleDFA(g, "a", expecting);
		System.out.println(msgs);
	}

	@Test public void testMultiAltsWithRecursion() throws Exception {
		String g =
			"parser grammar T;\n" +
			"s : b X\n" +
			"  | b Y\n" +
			"  ;\n" +
			"b : L b R\n" +
			"  | A b B\n" +
			"  | I\n" +
			"  ;";
		String expecting =
			"s0-A->s1\n" +
			"s0-I->s3\n" +
			"s0-L->s2\n" +
			"s1-A->s1\n" +
			"s1-I->s3\n" +
			"s1-L->s2\n" +
			"s2-A->s1\n" +
			"s2-I->s3\n" +
			"s2-L->s2\n" +
			"s3-B->s4\n" +
			"s3-R->s5\n" +
			"s3-X->:s7=>1\n" +
			"s3-Y->:s6=>2\n" +
			"s4-B->s4\n" +
			"s4-R->s5\n" +
			"s4-X->:s7=>1\n" +
			"s4-Y->:s6=>2\n" +
			"s5-B->s4\n" +
			"s5-R->s5\n" +
			"s5-X->:s7=>1\n" +
			"s5-Y->:s6=>2\n";
		checkRuleDFA(g, "s", expecting);
	}

	@Test public void recursionInMultipleWithoutNonRecursiveAlt() throws Exception {
		String g =
			"parser grammar t;\n"+
			"a : A a X | A a Y;";
		String expecting =
			"s0-A->:s1=>1\n";
		List<Message> msgs = checkRuleDFA(g, "a", expecting);
		System.out.println(msgs);
		ambig(msgs, new int[] {1,2}, "A");
		unreachable(msgs, new int[] {2});
		assertEquals(msgs.size(), 2);
	}
	
	@Test public void emptyAndNonEmptyContextStack() throws Exception {
		String g =
			"parser grammar S4;\n" +
			"a : A+ ;\n" +
			"x : a a ;";
		String expecting =
			"s0-A->:s1=>1\n" +
			"s0-EOF->:s2=>1\n";
		List<Message> msgs = checkRuleDFA(g, 0, expecting);
		System.out.println(msgs);
		ambig(msgs, new int[] {1,2}, "A");
		unreachable(msgs, new int[] {2});
		assertEquals(msgs.size(), 2);
	}


	@Test public void simpleNullableRule() throws Exception {
		String g =
			"parser grammar S2;\n" +
			"a : b X \n" +
			"  | b Y\n" +
			"  ; \n" +
			"b : F     \n" +
			"  |       \n" +
			"  ;";
		String expecting =
			"s0-F->s1\n" +
			"s0-X->:s3=>1\n" +
			"s0-Y->:s2=>2\n" +
			"s1-X->:s3=>1\n" +
			"s1-Y->:s2=>2\n";
		List<Message> msgs = checkRuleDFA(g, "a", expecting);
		System.out.println(msgs);
		assertEquals(msgs.size(), 0);
	}

	@Test public void testRecursion() throws Exception {
		String g =
			"parser grammar t;\n"+
			"s : a Y | A+ X ;\n" +
			"a : A a | Q;";
		String expecting =
			"s0-A->s2\n" +
			"s0-Q->:s1=>1\n" +
			"s2-A->s2\n" +
			"s2-Q->:s3=>1\n" +
			"s2-X->:s4=>2\n";
		List<Message> msgs = checkRuleDFA(g, "s", expecting);
		System.out.println(msgs);
		assertEquals(msgs.size(), 0);
	}

	@Test public void testimmediateLeftRecursion() throws Exception {
		ErrorQueue equeue = new ErrorQueue();
		Grammar g = new Grammar(
			"parser grammar t;\n"+
			"s : a ;\n" +
			"a : a A | B;", equeue);
		NFA nfa = createNFA(g);
		LeftRecursionDetector lr = new LeftRecursionDetector(nfa);
		lr.check();
		String expecting = "[[Rule{name=a}]]";
		assertEquals(expecting, lr.listOfRecursiveCycles.toString());
	}

	@Test public void testLeftRecursionInMultipleCycles() throws Exception {
		ErrorQueue equeue = new ErrorQueue();
		Grammar g = new Grammar(
			"parser grammar t;\n"+
				"s : a x ;\n" +
				"a : b | A ;\n" +
				"b : c ;\n" +
				"c : a | C ;\n" +
				"x : y | X ;\n" +
				"y : x ;\n", equeue);
		NFA nfa = createNFA(g);
		LeftRecursionDetector lr = new LeftRecursionDetector(nfa);
		lr.check();
		String expecting = "[[Rule{name=a}, Rule{name=c}, Rule{name=b}], [Rule{name=x}, Rule{name=y}]]";
		assertEquals(expecting, lr.listOfRecursiveCycles.toString());
	}

	@Test public void selfRecurseNonDet() throws Exception {
		String g =
			"parser grammar t;\n"+
			"s : a ;\n" +
			"a : P a P | P;";
		// nondeterministic from left edge
		String expecting =
			"s0-P->s1\n" +
			"s1-EOF->:s3=>2\n" +
			"s1-P->:s2=>1\n";
		List<Message> msgs = checkRuleDFA(g, "a", expecting);
		System.out.println(msgs);
		ambig(msgs, new int[] {1,2}, "P P");
		assertEquals(msgs.size(), 1);
	}

	@Test public void testIndirectRecursionLoop() throws Exception {
		ErrorQueue equeue = new ErrorQueue();
		Grammar g = new Grammar(
			"parser grammar t;\n"+
			"s : a ;\n" +
			"a : b X ;\n"+
			"b : a B ;\n", equeue);
		NFA nfa = createNFA(g);
		LeftRecursionDetector lr = new LeftRecursionDetector(nfa);
		lr.check();
		String expecting = "[[Rule{name=a}, Rule{name=b}]]";
		assertEquals(lr.listOfRecursiveCycles.toString(), expecting);
	}

	@Test public void testIndirectRecursionLoop2() throws Exception {
		ErrorQueue equeue = new ErrorQueue();
		Grammar g = new Grammar(
			"parser grammar t;\n"+
			"s : a ;\n" +
			"a : i b X ;\n"+ // should see through i
			"b : a B ;\n" +
			"i : ;\n", equeue);
		NFA nfa = createNFA(g);
		LeftRecursionDetector lr = new LeftRecursionDetector(nfa);
		lr.check();
		String expecting = "[[Rule{name=a}, Rule{name=b}]]";
		assertEquals(expecting, lr.listOfRecursiveCycles.toString());
	}

	@Test public void testifThenElse() throws Exception {
		String g =
			"parser grammar t;\n"+
			"s : IF s (E s)? | B;\n" +
			"slist: s SEMI ;";
		String expecting =
			"s0-E->:s1=>1\n" +
			"s0-SEMI->:s2=>2\n";
		List<Message> msgs = checkRuleDFA(g, 0, expecting);
		System.out.println(msgs);
		ambig(msgs, new int[] {1,2}, "E");
		//unreachable(msgs, new int[] {2});
		assertEquals(msgs.size(), 1);
	}

	@Test public void testifThenElseChecksStackSuffixConflict() throws Exception {
		// if you don't check stack soon enough, this finds E B not just E
		// as ambig input
		String g =
			"parser grammar t;\n"+
			"slist: s SEMI ;\n"+
			"s : IF s el | B;\n" +
			"el: (E s)? ;\n";
		String expecting =
			"s0-E->:s1=>1\n" +
			"s0-SEMI->:s2=>2\n";
		List<Message> msgs = checkRuleDFA(g, 1, expecting);
		System.out.println(msgs);
		ambig(msgs, new int[] {1,2}, "E");
		assertEquals(msgs.size(), 1);
	}

	@Test
    public void testDoubleInvokeRuleLeftEdge() throws Exception {
		String g =
			"parser grammar t;\n"+
			"a : b X\n" +
			"  | b Y\n" +
			"  ;\n" +
			"b : c B\n" +
			"  | c\n" +
			"  ;\n" +
			"c : C ;\n";
		String expecting =
			"s0-C->s1\n" +
			"s1-B->s2\n" +
			"s1-X->:s4=>1\n" +
			"s1-Y->:s3=>2\n" +
			"s2-X->:s4=>1\n" +
			"s2-Y->:s3=>2\n";
		List<Message> msgs = checkRuleDFA(g, "a", expecting);
		System.out.println(msgs);
		assertEquals(msgs.size(), 0);
	}

	@Test public void testimmediateTailRecursion() throws Exception {
		String g =
			"parser grammar t;\n"+
			"s : a ;\n" +
			"a : A a | A B;";
		String expecting =
			"s0-A->s1\n" +
			"s1-A->:s2=>1\n" +
			"s1-B->:s3=>2\n";
		List<Message> msgs = checkRuleDFA(g, "a", expecting);
		assertEquals(msgs.size(), 0);
	}

	@Test public void testCycleInsideRuleDoesNotForceInfiniteRecursion() throws Exception {
		// shouldn't be possible to loop
		// forever inside of a rule if there is an epsilon loop.
		String g =
			"parser grammar t;\n"+
			"s : a ;\n" +
			"a : (A|)+ B;\n";
		String expecting =
			"s0-A->:s1=>1\n" +
			"s0-B->:s2=>2\n";
		List<Message> msgs = checkRuleDFA(g, 0, expecting);
		System.out.println(msgs);
		ambig(msgs, new int[] {1,2}, "A");
		assertEquals(msgs.size(), 1);

		expecting =
			"s0-A->:s1=>2\n" +
			"s0-B->:s2=>1\n";
		msgs = checkRuleDFA(g, 1, expecting);
		System.out.println(msgs);
		ambig(msgs, new int[] {1,2}, "B");
		assertEquals(msgs.size(), 1);
	}

	/*
	@Test public void _template() throws Exception {
		String g =
			"";
		String expecting =
			"";
		List<Message> msgs = checkRuleDFA(g, "a", expecting);
		System.out.println(msgs);
		//ambig(msgs, new int[] {1,2}, "A");
		//unreachable(msgs, new int[] {2});
		assertEquals(msgs.size(), 0);
	}
	 */

}
