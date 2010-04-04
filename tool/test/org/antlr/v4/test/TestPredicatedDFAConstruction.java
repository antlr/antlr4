package org.antlr.v4.test;

import org.antlr.v4.tool.Message;
import org.junit.Test;

import java.util.List;

/** */
public class TestPredicatedDFAConstruction extends BaseTest {
	@Test
		public void TwoAltsOnePred() throws Exception {
		String g =
			"parser grammar E;\n" +
			"a : {p1}? ID\n" +
			"  | ID\n" +
			"  ;";
		String expecting =
			"s0-ID->s1\n" +
			"s1-true->:s3=>2\n" +
			"s1-{p1}?->:s2=>1\n";
		checkRuleDFA(g, "a", expecting);
	}

	@Test public void ambigButPredicatedTokens() throws Exception {
		// accept state matches both; try them in order since at least 1 has pred
		String g =
			"lexer grammar L4;\n" +
			"A : {p1}? 'a' ; \n" +
			"B : {p2}? 'a' ;";
		String expecting =
			"s0-'a'->:s1=> A B\n";
		checkLexerDFA(g, expecting);
	}

	@Test public void hoistPredIntoCallingRule() throws Exception {
		String g =
			"grammar Q;\n" +
			"\n" +
			"prog: stat+ ;\n" +
			"/** ANTLR pulls predicates from keyIF and keyCALL into\n" +
			"* decision for this rule.\n" +
			"*/\n" +
			"stat: keyIF expr stat\n" +
			"\t| keyCALL ID ';'\n" +
			"\t| ';'\n" +
			"\t;\n" +
			"/** An ID whose text is \"if\" */\n" +
			"keyIF : {IF}? ID ;\n" +
			"/** An ID whose text is \"call\" */\n" +
			"keyCALL : {CALL}? ID ;\n" +
			"\n" +
			"expr : ID;";
		String expecting =
			"s0-';'->:s2=>3\n" +
			"s0-ID->s1\n" +
			"s1-ID->s3\n" +
			"s3-';'->s5\n" +
			"s3-ID->:s4=>1\n" +
			"s5-{CALL}?->:s7=>2\n" +
			"s5-{IF}?->:s6=>1\n";
		List<Message> msgs = checkRuleDFA(g, "stat", expecting);
		System.err.println(msgs);
	}

	@Test public void _template() throws Exception {
		String g =
			"";
		String expecting =
			"";
		checkRuleDFA(g, "a", expecting);
	}
	
}
