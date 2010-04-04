package org.antlr.v4.test;

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




	@Test public void _template() throws Exception {
		String g =
			"";
		String expecting =
			"";
		checkRuleDFA(g, "a", expecting);
	}


}
