package org.antlr.v4.test;

import org.antlr.v4.Tool;
import org.antlr.v4.automata.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.semantics.SemanticPipeline;
import org.antlr.v4.tool.*;
import org.junit.Test;

public class TestATNConstruction extends BaseTest {
	@Test
	public void testA() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : A;");
		String expecting =
			"RuleStart_a_0->s2\n" +
			"s2-A->s3\n" +
			"s3->RuleStop_a_1\n" +
			"RuleStop_a_1-EOF->s4\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testAB() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : A B ;");
		String expecting =
			"RuleStart_a_0->s2\n" +
			"s2-A->s3\n" +
			"s3->s4\n" +
			"s4-B->s5\n" +
			"s5->RuleStop_a_1\n" +
			"RuleStop_a_1-EOF->s6\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testAorB() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : A | B {;} ;");
		String expecting =
			"RuleStart_a_0->BlockStart_8\n" +
			"BlockStart_8->s2\n" +
			"BlockStart_8->s4\n" +
			"s2-A->s3\n" +
			"s4-B->s5\n" +
			"s3->BlockEnd_9\n" +
			"s5->s6\n" +
			"BlockEnd_9->RuleStop_a_1\n" +
			"s6-action_0:-1->s7\n" + // actionIndex -1 since not forced action
			"RuleStop_a_1-EOF->s10\n" +
			"s7->BlockEnd_9\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testSetAorB() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : A | B ;");
		String expecting =
			"RuleStart_a_0->BlockStart_6\n" +
			"BlockStart_6->s2\n" +
			"BlockStart_6->s4\n" +
			"s2-A->s3\n" +
			"s4-B->s5\n" +
			"s3->BlockEnd_7\n" +
			"s5->BlockEnd_7\n" +
			"BlockEnd_7->RuleStop_a_1\n" +
			"RuleStop_a_1-EOF->s8\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testRange() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar P;\n"+
			"A : 'a'..'c' ;"
		);
		String expecting =
			"RuleStart_A_1->s3\n" +
			"s3-'a'..'c'->s4\n" +
			"s4->RuleStop_A_2\n";
		checkTokensRule(g, "A", expecting);
	}

	@Test public void testRangeOrRange() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar P;\n"+
			"A : ('a'..'c' 'h' | 'q' 'j'..'l') ;"
		);
		String expecting =
			"RuleStart_A_1->BlockStart_11\n" +
			"BlockStart_11->s3\n" +
			"BlockStart_11->s7\n" +
			"s3-'a'..'c'->s4\n" +
			"s7-'q'->s8\n" +
			"s4->s5\n" +
			"s8->s9\n" +
			"s5-'h'->s6\n" +
			"s9-'j'..'l'->s10\n" +
			"s6->BlockEnd_12\n" +
			"s10->BlockEnd_12\n" +
			"BlockEnd_12->RuleStop_A_2\n";
		checkTokensRule(g, "A", expecting);
	}

	@Test public void testStringLiteralInParser() throws Exception {
		Grammar g = new Grammar(
			"grammar P;\n"+
			"a : A|'b' ;"
		);
		String expecting =
			"RuleStart_a_0->BlockStart_6\n" +
			"BlockStart_6->s2\n" +
			"BlockStart_6->s4\n" +
			"s2-A->s3\n" +
			"s4-'b'->s5\n" +
			"s3->BlockEnd_7\n" +
			"s5->BlockEnd_7\n" +
			"BlockEnd_7->RuleStop_a_1\n" +
			"RuleStop_a_1-EOF->s8\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testABorCD() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : A B | C D;");
		String expecting =
			"RuleStart_a_0->BlockStart_10\n" +
			"BlockStart_10->s2\n" +
			"BlockStart_10->s6\n" +
			"s2-A->s3\n" +
			"s6-C->s7\n" +
			"s3->s4\n" +
			"s7->s8\n" +
			"s4-B->s5\n" +
			"s8-D->s9\n" +
			"s5->BlockEnd_11\n" +
			"s9->BlockEnd_11\n" +
			"BlockEnd_11->RuleStop_a_1\n" +
			"RuleStop_a_1-EOF->s12\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testbA() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : b A ;\n"+
			"b : B ;");
		String expecting =
			"RuleStart_a_0->s4\n" +
			"s4-b->RuleStart_b_2\n" +
			"s5->s6\n" +
			"s6-A->s7\n" +
			"s7->RuleStop_a_1\n" +
			"RuleStop_a_1-EOF->s10\n";
		checkRule(g, "a", expecting);
		expecting =
			"RuleStart_b_2->s8\n" +
			"s8-B->s9\n" +
			"s9->RuleStop_b_3\n" +
			"RuleStop_b_3->s5\n";
		checkRule(g, "b", expecting);
	}

	@Test public void testFollow() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : b A ;\n"+
			"b : B ;\n"+
			"c : b C;");
		String expecting =
			"RuleStart_b_2->s10\n" +
			"s10-B->s11\n" +
			"s11->RuleStop_b_3\n" +
			"RuleStop_b_3->s7\n" +
			"RuleStop_b_3->s13\n";
		checkRule(g, "b", expecting);
	}

	@Test public void testAorEpsilon() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : A | ;");
		String expecting =
			"RuleStart_a_0->BlockStart_6\n" +
			"BlockStart_6->s2\n" +
			"BlockStart_6->s4\n" +
			"s2-A->s3\n" +
			"s4->s5\n" +
			"s3->BlockEnd_7\n" +
			"s5->BlockEnd_7\n" +
			"BlockEnd_7->RuleStop_a_1\n" +
			"RuleStop_a_1-EOF->s8\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testAOptional() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : A?;");
		String expecting =
			"RuleStart_a_0->BlockStart_4\n" +
			"BlockStart_4->s2\n" +
			"BlockStart_4->BlockEnd_5\n" +
			"s2-A->s3\n" +
			"BlockEnd_5->RuleStop_a_1\n" +
			"s3->BlockEnd_5\n" +
			"RuleStop_a_1-EOF->s6\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testAorBoptional() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : (A{;}|B)?;");
		String expecting =
			"RuleStart_a_0->BlockStart_8\n" +
			"BlockStart_8->s2\n" +
			"BlockStart_8->s6\n" +
			"BlockStart_8->BlockEnd_9\n" +
			"s2-A->s3\n" +
			"s6-B->s7\n" +
			"BlockEnd_9->RuleStop_a_1\n" +
			"s3->s4\n" +
			"s7->BlockEnd_9\n" +
			"RuleStop_a_1-EOF->s10\n" +
			"s4-action_0:-1->s5\n" +
			"s5->BlockEnd_9\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testSetAorBoptional() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : (A|B)?;");
		String expecting =
			"RuleStart_a_0->BlockStart_8\n" +
			"BlockStart_8->s6\n" +
			"BlockStart_8->BlockEnd_9\n" +
			"s6-{A..B}->s7\n" +
			"BlockEnd_9->RuleStop_a_1\n" +
			"s7->BlockEnd_9\n" +
			"RuleStop_a_1-EOF->s10\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testAorBthenC() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : (A | B) C;");
		String expecting =
			"RuleStart_a_0->s6\n" +
			"s6-{A..B}->s7\n" +
			"s7->s8\n" +
			"s8-C->s9\n" +
			"s9->RuleStop_a_1\n" +
			"RuleStop_a_1-EOF->s10\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testAplus() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : A+;");
		String expecting =
			"RuleStart_a_0->PlusBlockStart_4\n" +
			"PlusBlockStart_4->s2\n" +
			"s2-A->s3\n" +
			"s3->BlockEnd_5\n" +
			"BlockEnd_5->PlusLoopBack_6\n" +
			"PlusLoopBack_6->s2\n" +
			"PlusLoopBack_6->s7\n" +
			"s7->RuleStop_a_1\n" +
			"RuleStop_a_1-EOF->s8\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testAorBplus() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : (A|B)+;");
		String expecting =
			"RuleStart_a_0->PlusBlockStart_8\n" +
			"PlusBlockStart_8->s6\n" +
			"s6-{A..B}->s7\n" +
			"s7->BlockEnd_9\n" +
			"BlockEnd_9->PlusLoopBack_10\n" +
			"PlusLoopBack_10->s6\n" +
			"PlusLoopBack_10->s11\n" +
			"s11->RuleStop_a_1\n" +
			"RuleStop_a_1-EOF->s12\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testAorBorEmptyPlus() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : (A | B | )+ ;");
		String expecting =
			"RuleStart_a_0->PlusBlockStart_8\n" +
			"PlusBlockStart_8->s2\n" +
			"PlusBlockStart_8->s4\n" +
			"PlusBlockStart_8->s6\n" +
			"s2-A->s3\n" +
			"s4-B->s5\n" +
			"s6->s7\n" +
			"s3->BlockEnd_9\n" +
			"s5->BlockEnd_9\n" +
			"s7->BlockEnd_9\n" +
			"BlockEnd_9->PlusLoopBack_10\n" +
			"PlusLoopBack_10->s2\n" +
			"PlusLoopBack_10->s4\n" +
			"PlusLoopBack_10->s6\n" +
			"PlusLoopBack_10->s11\n" +
			"s11->RuleStop_a_1\n" +
			"RuleStop_a_1-EOF->s12\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testAStar() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : A*;");
		String expecting =
			"RuleStart_a_0->StarBlockStart_4\n" +
			"StarBlockStart_4->s2\n" +
			"StarBlockStart_4->s7\n" +
			"s2-A->s3\n" +
			"s7->RuleStop_a_1\n" +
			"s3->BlockEnd_5\n" +
			"RuleStop_a_1-EOF->s8\n" +
			"BlockEnd_5->StarLoopBack_6\n" +
			"StarLoopBack_6->StarBlockStart_4\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testNestedAstar() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : (',' ID*)*;");
		String expecting =
			"RuleStart_a_0->StarBlockStart_10\n" +
			"StarBlockStart_10->s2\n" +
			"StarBlockStart_10->s13\n" +
			"s2-','->s3\n" +
			"s13->RuleStop_a_1\n" +
			"s3->StarBlockStart_6\n" +
			"RuleStop_a_1-EOF->s14\n" +
			"StarBlockStart_6->s4\n" +
			"StarBlockStart_6->s9\n" +
			"s4-ID->s5\n" +
			"s9->BlockEnd_11\n" +
			"s5->BlockEnd_7\n" +
			"BlockEnd_11->StarLoopBack_12\n" +
			"BlockEnd_7->StarLoopBack_8\n" +
			"StarLoopBack_12->StarBlockStart_10\n" +
			"StarLoopBack_8->StarBlockStart_6\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testAorBstar() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : (A | B{;})* ;");
		String expecting =
			"RuleStart_a_0->StarBlockStart_8\n" +
			"StarBlockStart_8->s2\n" +
			"StarBlockStart_8->s4\n" +
			"StarBlockStart_8->s11\n" +
			"s2-A->s3\n" +
			"s4-B->s5\n" +
			"s11->RuleStop_a_1\n" +
			"s3->BlockEnd_9\n" +
			"s5->s6\n" +
			"RuleStop_a_1-EOF->s12\n" +
			"BlockEnd_9->StarLoopBack_10\n" +
			"s6-action_0:-1->s7\n" +
			"StarLoopBack_10->StarBlockStart_8\n" +
			"s7->BlockEnd_9\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testPredicatedAorB() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : {p1}? A | {p2}? B ;");
		String expecting =
			"RuleStart_a_0->BlockStart_10\n" +
			"BlockStart_10->s2\n" +
			"BlockStart_10->s6\n" +
			"s2-pred_0:0->s3\n" +
			"s6-pred_0:1->s7\n" +
			"s3->s4\n" +
			"s7->s8\n" +
			"s4-A->s5\n" +
			"s8-B->s9\n" +
			"s5->BlockEnd_11\n" +
			"s9->BlockEnd_11\n" +
			"BlockEnd_11->RuleStop_a_1\n" +
			"RuleStop_a_1-EOF->s12\n";
		checkRule(g, "a", expecting);
	}

/*
	@Test public void testMultiplePredicates() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : {p1}? {p1a}? A | {p2}? B | {p3} b;\n" +
			"b : {p4}? B ;");
		String expecting =
			"\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testSets() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : ( A | B )+ ;\n" +
			"b : ( A | B{;} )+ ;\n" +
			"c : (A|B) (A|B) ;\n" +
			"d : ( A | B )* ;\n" +
			"e : ( A | B )? ;");
		String expecting =
			"\n";
		checkRule(g, "a", expecting);
		expecting =
			"\n";
		checkRule(g, "b", expecting);
		expecting =
			"\n";
		checkRule(g, "c", expecting);
		expecting =
			"\n";
		checkRule(g, "d", expecting);
		expecting =
			"\n";
		checkRule(g, "e", expecting);
	}

	@Test public void testNotSet() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"tokens { A; B; C; }\n"+
			"a : ~A ;\n");
		String expecting =
			"\n";
		checkRule(g, "a", expecting);

	}

	@Test public void testNotSingletonBlockSet() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"tokens { A; B; C; }\n"+
			"a : ~(A) ;\n");
		String expecting =
			"\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testNotCharSet() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar P;\n"+
			"A : ~'3' ;\n");
		String expecting =
			"RuleStart_A_1->s5\n" +
			"s5-{'\\u0000'..'2', '4'..'\\uFFFE'}->s6\n" +
			"s6->RuleStop_A_2\n";
		checkRule(g, "A", expecting);
	}

	@Test public void testNotBlockSet() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar P;\n"+
			"A : ~('3'|'b') ;\n");
		String expecting =
			"\n";
		checkRule(g, "A", expecting);
	}

	@Test public void testNotSetLoop() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar P;\n"+
			"A : ~('3')* ;\n");
		String expecting =
			"\n";
		checkRule(g, "A", expecting);
	}

	@Test public void testNotBlockSetLoop() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar P;\n"+
			"A : ~('3'|'b')* ;\n");
		String expecting =
			"\n";
		checkRule(g, "A", expecting);
	}

	@Test public void testLabeledNotSet() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"tokens { A; B; C; }\n"+
			"a : t=~A ;\n");
		String expecting =
			".s0->.s1\n" +
			".s1->.s2\n" +
			".s2-B..C->.s3\n" +
			".s3->:s4\n" +
			":s4-EOF->.s5\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testLabeledNotCharSet() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar P;\n"+
			"A : t=~'3' ;\n");
		String expecting =
			".s0->.s1\n" +
			".s1->.s2\n" +
			".s2-{'\\u0000'..'2', '4'..'\\uFFFF'}->.s3\n" +
			".s3->:s4\n" +
			":s4-<EOT>->.s5\n";
		checkRule(g, "A", expecting);
	}

	@Test public void testLabeledNotBlockSet() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar P;\n"+
			"A : t=~('3'|'b') ;\n");
		String expecting =
			".s0->.s1\n" +
			".s1->.s2\n" +
			".s2-{'\\u0000'..'2', '4'..'a', 'c'..'\\uFFFF'}->.s3\n" +
			".s3->:s4\n" +
			":s4-<EOT>->.s5\n";
		checkRule(g, "A", expecting);
	}

	@Test public void testEscapedCharLiteral() throws Exception {
		Grammar g = new Grammar(
			"grammar P;\n"+
			"a : '\\n';");
		String expecting =
			".s0->.s1\n" +
			".s1->.s2\n" +
			".s2-'\\n'->.s3\n" +
			".s3->:s4\n" +
			":s4-EOF->.s5\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testEscapedStringLiteral() throws Exception {
		Grammar g = new Grammar(
			"grammar P;\n"+
			"a : 'a\\nb\\u0030c\\'';");
		String expecting =
			"RuleStart_a_0->s2\n" +
			"s2-'a\\nb\\u0030c\\''->s3\n" +
			"s3->RuleStop_a_1\n" +
			"RuleStop_a_1-EOF->s4\n";
		checkRule(g, "a", expecting);
	}

	// AUTO BACKTRACKING STUFF

	@Test public void testAutoBacktracking_RuleBlock() throws Exception {
		Grammar g = new Grammar(
			"grammar t;\n" +
			"options {backtrack=true;}\n"+
			"a : 'a'{;}|'b';"
		);
		String expecting =
			".s0->.s1\n" +
			".s1->.s2\n" +
			".s1->.s9\n" +
			".s10-'b'->.s11\n" +
			".s11->.s6\n" +
			".s2-{synpred1_t}?->.s3\n" +
			".s3-'a'->.s4\n" +
			".s4-{}->.s5\n" +
			".s5->.s6\n" +
			".s6->:s7\n" +
			".s9->.s10\n" +
			":s7-EOF->.s8\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testAutoBacktracking_RuleSetBlock() throws Exception {
		Grammar g = new Grammar(
			"grammar t;\n" +
			"options {backtrack=true;}\n"+
			"a : 'a'|'b';"
		);
		String expecting =
			".s0->.s1\n" +
			".s1->.s2\n" +
			".s2-'a'..'b'->.s3\n" +
			".s3->:s4\n" +
			":s4-EOF->.s5\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testAutoBacktracking_SimpleBlock() throws Exception {
		Grammar g = new Grammar(
			"grammar t;\n" +
			"options {backtrack=true;}\n"+
			"a : ('a'{;}|'b') ;"
		);
		String expecting =
			".s0->.s1\n" +
			".s1->.s2\n" +
			".s10->.s11\n" +
			".s11-'b'->.s12\n" +
			".s12->.s7\n" +
			".s2->.s10\n" +
			".s2->.s3\n" +
			".s3-{synpred1_t}?->.s4\n" +
			".s4-'a'->.s5\n" +
			".s5-{}->.s6\n" +
			".s6->.s7\n" +
			".s7->:s8\n" +
			":s8-EOF->.s9\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testAutoBacktracking_SetBlock() throws Exception {
		Grammar g = new Grammar(
			"grammar t;\n" +
			"options {backtrack=true;}\n"+
			"a : ('a'|'b') ;"
		);
		String expecting =
			".s0->.s1\n" +
			".s1->.s2\n" +
			".s2-'a'..'b'->.s3\n" +
			".s3->:s4\n" +
			":s4-EOF->.s5\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testAutoBacktracking_StarBlock() throws Exception {
		Grammar g = new Grammar(
			"grammar t;\n" +
			"options {backtrack=true;}\n"+
			"a : ('a'{;}|'b')* ;"
		);
		String expecting =
			".s0->.s1\n" +
			".s1->.s2\n" +
			".s12->.s13\n" +
			".s13-{synpred2_t}?->.s14\n" +
			".s14-'b'->.s15\n" +
			".s15->.s8\n" +
			".s16->.s9\n" +
			".s2->.s16\n" +
			".s2->.s3\n" +
			".s3->.s12\n" +
			".s3->.s4\n" +
			".s4-{synpred1_t}?->.s5\n" +
			".s5-'a'->.s6\n" +
			".s6-{}->.s7\n" +
			".s7->.s8\n" +
			".s8->.s3\n" +
			".s8->.s9\n" +
			".s9->:s10\n" +
			":s10-EOF->.s11\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testAutoBacktracking_StarSetBlock_IgnoresPreds() throws Exception {
		Grammar g = new Grammar(
			"grammar t;\n" +
			"options {backtrack=true;}\n"+
			"a : ('a'|'b')* ;"
		);
		String expecting =
			".s0->.s1\n" +
			".s1->.s2\n" +
			".s2->.s3\n" +
			".s2->.s9\n" +
			".s3->.s4\n" +
			".s4-'a'..'b'->.s5\n" +
			".s5->.s3\n" +
			".s5->.s6\n" +
			".s6->:s7\n" +
			".s9->.s6\n" +
			":s7-EOF->.s8\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testAutoBacktracking_StarSetBlock() throws Exception {
		Grammar g = new Grammar(
			"grammar t;\n" +
			"options {backtrack=true;}\n"+
			"a : ('a'|'b'{;})* ;"
		);
		String expecting =
			".s0->.s1\n" +
			".s1->.s2\n" +
			".s11->.s12\n" +
			".s12-{synpred2_t}?->.s13\n" +
			".s13-'b'->.s14\n" +
			".s14-{}->.s15\n" +
			".s15->.s7\n" +
			".s16->.s8\n" +
			".s2->.s16\n" +
			".s2->.s3\n" +
			".s3->.s11\n" +
			".s3->.s4\n" +
			".s4-{synpred1_t}?->.s5\n" +
			".s5-'a'->.s6\n" +
			".s6->.s7\n" +
			".s7->.s3\n" +
			".s7->.s8\n" +
			".s8->:s9\n" +
			":s9-EOF->.s10\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testAutoBacktracking_StarBlock1Alt() throws Exception {
		Grammar g = new Grammar(
			"grammar t;\n" +
			"options {backtrack=true;}\n"+
			"a : ('a')* ;"
		);
		String expecting =
			".s0->.s1\n" +
			".s1->.s2\n" +
			".s10->.s7\n" +
			".s2->.s10\n" +
			".s2->.s3\n" +
			".s3->.s4\n" +
			".s4-{synpred1_t}?->.s5\n" +
			".s5-'a'->.s6\n" +
			".s6->.s3\n" +
			".s6->.s7\n" +
			".s7->:s8\n" +
			":s8-EOF->.s9\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testAutoBacktracking_PlusBlock() throws Exception {
		Grammar g = new Grammar(
			"grammar t;\n" +
			"options {backtrack=true;}\n"+
			"a : ('a'{;}|'b')+ ;"
		);
		String expecting =
			".s0->.s1\n" +
			".s1->.s2\n" +
			".s12->.s13\n" +
			".s13-{synpred2_t}?->.s14\n" +
			".s14-'b'->.s15\n" +
			".s15->.s8\n" +
			".s2->.s3\n" +
			".s3->.s12\n" +
			".s3->.s4\n" +
			".s4-{synpred1_t}?->.s5\n" +
			".s5-'a'->.s6\n" +
			".s6-{}->.s7\n" +
			".s7->.s8\n" +
			".s8->.s3\n" +
			".s8->.s9\n" +
			".s9->:s10\n" +
			":s10-EOF->.s11\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testAutoBacktracking_PlusSetBlock() throws Exception {
		Grammar g = new Grammar(
			"grammar t;\n" +
			"options {backtrack=true;}\n"+
			"a : ('a'|'b'{;})+ ;"
		);
		String expecting =
			".s0->.s1\n" +
			".s1->.s2\n" +
			".s11->.s12\n" +
			".s12-{synpred2_t}?->.s13\n" +
			".s13-'b'->.s14\n" +
			".s14-{}->.s15\n" +
			".s15->.s7\n" +
			".s2->.s3\n" +
			".s3->.s11\n" +
			".s3->.s4\n" +
			".s4-{synpred1_t}?->.s5\n" +
			".s5-'a'->.s6\n" +
			".s6->.s7\n" +
			".s7->.s3\n" +
			".s7->.s8\n" +
			".s8->:s9\n" +
			":s9-EOF->.s10\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testAutoBacktracking_PlusBlock1Alt() throws Exception {
		Grammar g = new Grammar(
			"grammar t;\n" +
			"options {backtrack=true;}\n"+
			"a : ('a')+ ;"
		);
		String expecting =
			".s0->.s1\n" +
			".s1->.s2\n" +
			".s2->.s3\n" +
			".s3->.s4\n" +
			".s4-{synpred1_t}?->.s5\n" +
			".s5-'a'->.s6\n" +
			".s6->.s3\n" +
			".s6->.s7\n" +
			".s7->:s8\n" +
			":s8-EOF->.s9\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testAutoBacktracking_OptionalBlock2Alts() throws Exception {
		Grammar g = new Grammar(
			"grammar t;\n" +
			"options {backtrack=true;}\n"+
			"a : ('a'{;}|'b')?;"
		);
		String expecting =
			".s0->.s1\n" +
			".s1->.s2\n" +
			".s10->.s11\n" +
			".s10->.s14\n" +
			".s11-{synpred2_t}?->.s12\n" +
			".s12-'b'->.s13\n" +
			".s13->.s7\n" +
			".s14->.s7\n" +
			".s2->.s10\n" +
			".s2->.s3\n" +
			".s3-{synpred1_t}?->.s4\n" +
			".s4-'a'->.s5\n" +
			".s5-{}->.s6\n" +
			".s6->.s7\n" +
			".s7->:s8\n" +
			":s8-EOF->.s9\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testAutoBacktracking_OptionalBlock1Alt() throws Exception {
		Grammar g = new Grammar(
			"grammar t;\n" +
			"options {backtrack=true;}\n"+
			"a : ('a')?;"
		);
		String expecting =
			".s0->.s1\n" +
			".s1->.s2\n" +
			".s2->.s3\n" +
			".s2->.s9\n" +
			".s3-{synpred1_t}?->.s4\n" +
			".s4-'a'->.s5\n" +
			".s5->.s6\n" +
			".s6->:s7\n" +
			".s9->.s6\n" +
			":s7-EOF->.s8\n";
		checkRule(g, "a", expecting);
	}

	@Test public void testAutoBacktracking_ExistingPred() throws Exception {
		Grammar g = new Grammar(
			"grammar t;\n" +
			"options {backtrack=true;}\n"+
			"a : ('a')=> 'a' | 'b';"
		);
		String expecting =
			".s0->.s1\n" +
			".s1->.s2\n" +
			".s1->.s8\n" +
			".s10->.s5\n" +
			".s2-{synpred1_t}?->.s3\n" +
			".s3-'a'->.s4\n" +
			".s4->.s5\n" +
			".s5->:s6\n" +
			".s8->.s9\n" +
			".s9-'b'->.s10\n" +
			":s6-EOF->.s7\n";
		checkRule(g, "a", expecting);
	}
*/

	@Test public void testDefaultMode() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'a' ;\n" +
			"X : 'x' ;\n" +
			"mode FOO;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n");
		String expecting =
			"BlockStart_0->RuleStart_A_2\n" +
			"BlockStart_0->RuleStart_X_4\n" +
			"RuleStart_A_2->s10\n" +
			"RuleStart_X_4->s12\n" +
			"s10-'a'->s11\n" +
			"s12-'x'->s13\n" +
			"s11->RuleStop_A_3\n" +
			"s13->RuleStop_X_5\n";
		checkTokensRule(g, "DEFAULT_MODE", expecting);
	}

	@Test public void testMode() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'a' ;\n" +
			"X : 'x' ;\n" +
			"mode FOO;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n");
		String expecting =
			"BlockStart_1->RuleStart_B_6\n" +
			"BlockStart_1->RuleStart_C_8\n" +
			"RuleStart_B_6->s14\n" +
			"RuleStart_C_8->s16\n" +
			"s14-'b'->s15\n" +
			"s16-'c'->s17\n" +
			"s15->RuleStop_B_7\n" +
			"s17->RuleStop_C_9\n";
		checkTokensRule(g, "FOO", expecting);
	}

	void checkTokensRule(LexerGrammar g, String modeName, String expecting) {
		if ( g.ast!=null && !g.ast.hasErrors ) {
			System.out.println(g.ast.toStringTree());
			Tool antlr = new Tool();
			SemanticPipeline sem = new SemanticPipeline(g);
			sem.process();
			if ( g.getImportedGrammars()!=null ) { // process imported grammars (if any)
				for (Grammar imp : g.getImportedGrammars()) {
					antlr.processNonCombinedGrammar(imp);
				}
			}
		}

		if ( g.modes.get(modeName)==null ) {
			System.err.println("no such mode "+modeName);
			return;
		}

		ParserATNFactory f = new LexerATNFactory((LexerGrammar)g);
		ATN nfa = f.createATN();
		ATNState startState = nfa.modeNameToStartState.get(modeName);
		ATNPrinter serializer = new ATNPrinter(g, startState);
		String result = serializer.asString();

		//System.out.print(result);
		assertEquals(expecting, result);
	}

	void checkRule(Grammar g, String ruleName, String expecting) {
		if ( g.ast!=null && !g.ast.hasErrors ) {
			System.out.println(g.ast.toStringTree());
			Tool antlr = new Tool();
			SemanticPipeline sem = new SemanticPipeline(g);
			sem.process();
			if ( g.getImportedGrammars()!=null ) { // process imported grammars (if any)
				for (Grammar imp : g.getImportedGrammars()) {
					antlr.processNonCombinedGrammar(imp);
				}
			}
		}

		ParserATNFactory f = new ParserATNFactory(g);
		ATN atn = f.createATN();

		DOTGenerator dot = new DOTGenerator(g);
		System.out.println(dot.getDOT(atn.ruleToStartState[g.getRule(ruleName).index]));

		Rule r = g.getRule(ruleName);
		ATNState startState = atn.ruleToStartState[r.index];
		ATNPrinter serializer = new ATNPrinter(g, startState);
		String result = serializer.asString();

		//System.out.print(result);
		assertEquals(expecting, result);
	}
}
