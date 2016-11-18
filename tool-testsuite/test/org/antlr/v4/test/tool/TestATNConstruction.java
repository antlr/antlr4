/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.antlr.v4.test.tool;

import org.antlr.v4.Tool;
import org.antlr.v4.automata.ATNPrinter;
import org.antlr.v4.automata.LexerATNFactory;
import org.antlr.v4.automata.ParserATNFactory;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.test.runtime.ErrorQueue;
import org.antlr.v4.test.runtime.java.BaseJavaTest;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.GrammarRootAST;
import org.antlr.v4.tool.ast.RuleAST;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestATNConstruction extends BaseJavaTest {
	@Before
	@Override
	public void testSetUp() throws Exception {
		super.testSetUp();
	}

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
		checkRuleATN(g, "a", expecting);
	}
	@Test public void testAB() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : A B ;");
		String expecting =
			"RuleStart_a_0->s2\n" +
				"s2-A->s3\n" +
				"s3-B->s4\n" +
				"s4->RuleStop_a_1\n" +
				"RuleStop_a_1-EOF->s5\n";
		checkRuleATN(g, "a", expecting);
	}
	@Test public void testAorB() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : A | B {;} ;");
		String expecting =
			"RuleStart_a_0->BlockStart_5\n" +
				"BlockStart_5->s2\n" +
				"BlockStart_5->s3\n" +
				"s2-A->BlockEnd_6\n" +
				"s3-B->s4\n" +
				"BlockEnd_6->RuleStop_a_1\n" +
				"s4-action_0:-1->BlockEnd_6\n" +
				"RuleStop_a_1-EOF->s7\n";
		checkRuleATN(g, "a", expecting);
	}
	@Test public void testSetAorB() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : A | B ;");
		String expecting =
			"RuleStart_a_0->s2\n" +
				"s2-{A, B}->s3\n" +
				"s3->RuleStop_a_1\n" +
				"RuleStop_a_1-EOF->s4\n";
		checkRuleATN(g, "a", expecting);
	}
	@Test public void testLexerIsntSetMultiCharString() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar P;\n"+
			"A : ('0x' | '0X') ;");
		String expecting =
			"s0->RuleStart_A_1\n" +
				"RuleStart_A_1->BlockStart_7\n" +
				"BlockStart_7->s3\n" +
				"BlockStart_7->s5\n" +
				"s3-'0'->s4\n" +
				"s5-'0'->s6\n" +
				"s4-'x'->BlockEnd_8\n" +
				"s6-'X'->BlockEnd_8\n" +
				"BlockEnd_8->RuleStop_A_2\n";
		checkTokensRule(g, null, expecting);
	}
	@Test public void testRange() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar P;\n"+
			"A : 'a'..'c' ;"
		);
		String expecting =
			"s0->RuleStart_A_1\n" +
				"RuleStart_A_1->s3\n" +
				"s3-'a'..'c'->s4\n" +
				"s4->RuleStop_A_2\n";
		checkTokensRule(g, null, expecting);
	}
	@Test public void testRangeOrRange() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar P;\n"+
			"A : ('a'..'c' 'h' | 'q' 'j'..'l') ;"
		);
		String expecting =
			"s0->RuleStart_A_1\n" +
				"RuleStart_A_1->BlockStart_7\n" +
				"BlockStart_7->s3\n" +
				"BlockStart_7->s5\n" +
				"s3-'a'..'c'->s4\n" +
				"s5-'q'->s6\n" +
				"s4-'h'->BlockEnd_8\n" +
				"s6-'j'..'l'->BlockEnd_8\n" +
				"BlockEnd_8->RuleStop_A_2\n";
		checkTokensRule(g, null, expecting);
	}
	@Test public void testStringLiteralInParser() throws Exception {
		Grammar g = new Grammar(
			"grammar P;\n"+
			"a : A|'b' ;"
		);
		String expecting =
			"RuleStart_a_0->s2\n" +
				"s2-{'b', A}->s3\n" +
				"s3->RuleStop_a_1\n" +
				"RuleStop_a_1-EOF->s4\n";
		checkRuleATN(g, "a", expecting);
	}
	@Test public void testABorCD() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : A B | C D;");
		String expecting =
			"RuleStart_a_0->BlockStart_6\n" +
				"BlockStart_6->s2\n" +
				"BlockStart_6->s4\n" +
				"s2-A->s3\n" +
				"s4-C->s5\n" +
				"s3-B->BlockEnd_7\n" +
				"s5-D->BlockEnd_7\n" +
				"BlockEnd_7->RuleStop_a_1\n" +
				"RuleStop_a_1-EOF->s8\n";
		checkRuleATN(g, "a", expecting);
	}
	@Test public void testbA() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : b A ;\n"+
			"b : B ;");
		String expecting =
			"RuleStart_a_0->s4\n" +
				"s4-b->RuleStart_b_2\n" +
				"s5-A->s6\n" +
				"s6->RuleStop_a_1\n" +
				"RuleStop_a_1-EOF->s9\n";
		checkRuleATN(g, "a", expecting);
		expecting =
			"RuleStart_b_2->s7\n" +
				"s7-B->s8\n" +
				"s8->RuleStop_b_3\n" +
				"RuleStop_b_3->s5\n";
		checkRuleATN(g, "b", expecting);
	}
	@Test public void testFollow() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : b A ;\n"+
			"b : B ;\n"+
			"c : b C;");
		String expecting =
			"RuleStart_b_2->s9\n" +
				"s9-B->s10\n" +
				"s10->RuleStop_b_3\n" +
				"RuleStop_b_3->s7\n" +
				"RuleStop_b_3->s12\n";
		checkRuleATN(g, "b", expecting);
	}
	@Test public void testAorEpsilon() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : A | ;");
		String expecting =
			"RuleStart_a_0->BlockStart_4\n" +
				"BlockStart_4->s2\n" +
				"BlockStart_4->s3\n" +
				"s2-A->BlockEnd_5\n" +
				"s3->BlockEnd_5\n" +
				"BlockEnd_5->RuleStop_a_1\n" +
				"RuleStop_a_1-EOF->s6\n";
		checkRuleATN(g, "a", expecting);
	}
	@Test public void testAOptional() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : A?;");
		String expecting =
			"RuleStart_a_0->BlockStart_3\n" +
				"BlockStart_3->s2\n" +
				"BlockStart_3->BlockEnd_4\n" +
				"s2-A->BlockEnd_4\n" +
				"BlockEnd_4->RuleStop_a_1\n" +
				"RuleStop_a_1-EOF->s5\n";
		checkRuleATN(g, "a", expecting);
	}
	@Test public void testAorBoptional() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : (A{;}|B)?;");
		String expecting =
			"RuleStart_a_0->BlockStart_5\n" +
				"BlockStart_5->s2\n" +
				"BlockStart_5->s4\n" +
				"BlockStart_5->BlockEnd_6\n" +
				"s2-A->s3\n" +
				"s4-B->BlockEnd_6\n" +
				"BlockEnd_6->RuleStop_a_1\n" +
				"s3-action_0:-1->BlockEnd_6\n" +
				"RuleStop_a_1-EOF->s7\n";
		checkRuleATN(g, "a", expecting);
	}
	@Test public void testSetAorBoptional() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : (A|B)?;");
		String expecting =
			"RuleStart_a_0->BlockStart_3\n" +
				"BlockStart_3->s2\n" +
				"BlockStart_3->BlockEnd_4\n" +
				"s2-{A, B}->BlockEnd_4\n" +
				"BlockEnd_4->RuleStop_a_1\n" +
				"RuleStop_a_1-EOF->s5\n";
		checkRuleATN(g, "a", expecting);
	}
	@Test public void testAorBthenC() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : (A | B) C;");
		String expecting =
			"RuleStart_a_0->s2\n" +
				"s2-{A, B}->s3\n" +
				"s3-C->s4\n" +
				"s4->RuleStop_a_1\n" +
				"RuleStop_a_1-EOF->s5\n";
		checkRuleATN(g, "a", expecting);
	}
	@Test public void testAplus() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : A+;");
		String expecting =
			"RuleStart_a_0->PlusBlockStart_3\n" +
				"PlusBlockStart_3->s2\n" +
				"s2-A->BlockEnd_4\n" +
				"BlockEnd_4->PlusLoopBack_5\n" +
				"PlusLoopBack_5->PlusBlockStart_3\n" +
				"PlusLoopBack_5->s6\n" +
				"s6->RuleStop_a_1\n" +
				"RuleStop_a_1-EOF->s7\n";
		checkRuleATN(g, "a", expecting);
	}
	@Test public void testAplusSingleAltHasPlusASTPointingAtLoopBackState() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"s : a B ;\n" +			// (RULE a (BLOCK (ALT (+ (BLOCK (ALT A))))))
			"a : A+;");
		String expecting =
			"RuleStart_a_2->PlusBlockStart_8\n" +
			"PlusBlockStart_8->s7\n" +
			"s7-A->BlockEnd_9\n" +
			"BlockEnd_9->PlusLoopBack_10\n" +
			"PlusLoopBack_10->PlusBlockStart_8\n" +
			"PlusLoopBack_10->s11\n" +
			"s11->RuleStop_a_3\n" +
			"RuleStop_a_3->s5\n";
		checkRuleATN(g, "a", expecting);
		// Get all AST -> ATNState relationships. Make sure loopback is covered when no loop entry decision
		List<GrammarAST> ruleNodes = g.ast.getNodesWithType(ANTLRParser.RULE);
		RuleAST a = (RuleAST)ruleNodes.get(1);
		List<GrammarAST> nodesInRule = a.getNodesWithType(null);
		Map<GrammarAST, ATNState> covered = new LinkedHashMap<GrammarAST, ATNState>();
		for (GrammarAST node : nodesInRule) {
			if ( node.atnState != null ) {
				covered.put(node, node.atnState);
			}
		}
		assertEquals("{RULE=2, BLOCK=8, +=10, BLOCK=8, A=7}", covered.toString());
	}
	@Test public void testAorBplus() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : (A|B{;})+;");
		String expecting =
			"RuleStart_a_0->PlusBlockStart_5\n" +
				"PlusBlockStart_5->s2\n" +
				"PlusBlockStart_5->s3\n" +
				"s2-A->BlockEnd_6\n" +
				"s3-B->s4\n" +
				"BlockEnd_6->PlusLoopBack_7\n" +
				"s4-action_0:-1->BlockEnd_6\n" +
				"PlusLoopBack_7->PlusBlockStart_5\n" +
				"PlusLoopBack_7->s8\n" +
				"s8->RuleStop_a_1\n" +
				"RuleStop_a_1-EOF->s9\n";
		checkRuleATN(g, "a", expecting);
	}
	@Test public void testAorBorEmptyPlus() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : (A | B | )+ ;");
		String expecting =
			"RuleStart_a_0->PlusBlockStart_5\n" +
				"PlusBlockStart_5->s2\n" +
				"PlusBlockStart_5->s3\n" +
				"PlusBlockStart_5->s4\n" +
				"s2-A->BlockEnd_6\n" +
				"s3-B->BlockEnd_6\n" +
				"s4->BlockEnd_6\n" +
				"BlockEnd_6->PlusLoopBack_7\n" +
				"PlusLoopBack_7->PlusBlockStart_5\n" +
				"PlusLoopBack_7->s8\n" +
				"s8->RuleStop_a_1\n" +
				"RuleStop_a_1-EOF->s9\n";
		checkRuleATN(g, "a", expecting);
	}
	@Test public void testAStar() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : A*;");
		String expecting =
			"RuleStart_a_0->StarLoopEntry_5\n" +
				"StarLoopEntry_5->StarBlockStart_3\n" +
				"StarLoopEntry_5->s6\n" +
				"StarBlockStart_3->s2\n" +
				"s6->RuleStop_a_1\n" +
				"s2-A->BlockEnd_4\n" +
				"RuleStop_a_1-EOF->s8\n" +
				"BlockEnd_4->StarLoopBack_7\n" +
				"StarLoopBack_7->StarLoopEntry_5\n";
		checkRuleATN(g, "a", expecting);
	}
	@Test public void testNestedAstar() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : (COMMA ID*)*;");
		String expecting =
			"RuleStart_a_0->StarLoopEntry_11\n" +
				"StarLoopEntry_11->StarBlockStart_9\n" +
				"StarLoopEntry_11->s12\n" +
				"StarBlockStart_9->s2\n" +
				"s12->RuleStop_a_1\n" +
				"s2-COMMA->StarLoopEntry_6\n" +
				"RuleStop_a_1-EOF->s14\n" +
				"StarLoopEntry_6->StarBlockStart_4\n" +
				"StarLoopEntry_6->s7\n" +
				"StarBlockStart_4->s3\n" +
				"s7->BlockEnd_10\n" +
				"s3-ID->BlockEnd_5\n" +
				"BlockEnd_10->StarLoopBack_13\n" +
				"BlockEnd_5->StarLoopBack_8\n" +
				"StarLoopBack_13->StarLoopEntry_11\n" +
				"StarLoopBack_8->StarLoopEntry_6\n";
		checkRuleATN(g, "a", expecting);
	}
	@Test public void testAorBstar() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : (A | B{;})* ;");
		String expecting =
			"RuleStart_a_0->StarLoopEntry_7\n" +
				"StarLoopEntry_7->StarBlockStart_5\n" +
				"StarLoopEntry_7->s8\n" +
				"StarBlockStart_5->s2\n" +
				"StarBlockStart_5->s3\n" +
				"s8->RuleStop_a_1\n" +
				"s2-A->BlockEnd_6\n" +
				"s3-B->s4\n" +
				"RuleStop_a_1-EOF->s10\n" +
				"BlockEnd_6->StarLoopBack_9\n" +
				"s4-action_0:-1->BlockEnd_6\n" +
				"StarLoopBack_9->StarLoopEntry_7\n";
		checkRuleATN(g, "a", expecting);
	}
	@Test public void testPredicatedAorB() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : {p1}? A | {p2}? B ;");
		String expecting =
			"RuleStart_a_0->BlockStart_6\n" +
				"BlockStart_6->s2\n" +
				"BlockStart_6->s4\n" +
				"s2-pred_0:0->s3\n" +
				"s4-pred_0:1->s5\n" +
				"s3-A->BlockEnd_7\n" +
				"s5-B->BlockEnd_7\n" +
				"BlockEnd_7->RuleStop_a_1\n" +
				"RuleStop_a_1-EOF->s8\n";
		checkRuleATN(g, "a", expecting);
	}

	@Test public void testParserRuleRefInLexerRule() throws Exception {
		boolean threwException = false;
		ErrorQueue errorQueue = new ErrorQueue();
		try {
			String gstr =
				"grammar U;\n"+
				"a : A;\n"+
				"A : a;\n";

			Tool tool = new Tool();
			tool.removeListeners();
			tool.addListener(errorQueue);
			assertEquals(0, errorQueue.size());
			GrammarRootAST grammarRootAST = tool.parseGrammarFromString(gstr);
			assertEquals(0, errorQueue.size());
			Grammar g = tool.createGrammar(grammarRootAST);
			assertEquals(0, errorQueue.size());
			g.fileName = "<string>";
			tool.process(g, false);
		}
		catch (Exception e) {
			threwException = true;
			e.printStackTrace();
		}
		System.out.println(errorQueue);
		assertEquals(1, errorQueue.errors.size());
		assertEquals(ErrorType.PARSER_RULE_REF_IN_LEXER_RULE, errorQueue.errors.get(0).getErrorType());
		assertEquals("[a, A]", Arrays.toString(errorQueue.errors.get(0).getArgs()));
		assertTrue(!threwException);
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
			"s0->RuleStart_A_2\n" +
				"s0->RuleStart_X_4\n" +
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
			"s1->RuleStart_B_6\n" +
				"s1->RuleStart_C_8\n" +
				"RuleStart_B_6->s14\n" +
				"RuleStart_C_8->s16\n" +
				"s14-'b'->s15\n" +
				"s16-'c'->s17\n" +
				"s15->RuleStop_B_7\n" +
				"s17->RuleStop_C_9\n";
		checkTokensRule(g, "FOO", expecting);
	}
	void checkTokensRule(LexerGrammar g, String modeName, String expecting) {
//		if ( g.ast!=null && !g.ast.hasErrors ) {
//			System.out.println(g.ast.toStringTree());
//			Tool antlr = new Tool();
//			SemanticPipeline sem = new SemanticPipeline(g);
//			sem.process();
//			if ( g.getImportedGrammars()!=null ) { // process imported grammars (if any)
//				for (Grammar imp : g.getImportedGrammars()) {
//					antlr.processNonCombinedGrammar(imp);
//				}
//			}
//		}
		if ( modeName==null ) modeName = "DEFAULT_MODE";
		if ( g.modes.get(modeName)==null ) {
			System.err.println("no such mode "+modeName);
			return;
		}
		ParserATNFactory f = new LexerATNFactory(g);
		ATN nfa = f.createATN();
		ATNState startState = nfa.modeNameToStartState.get(modeName);
		ATNPrinter serializer = new ATNPrinter(g, startState);
		String result = serializer.asString();
		//System.out.print(result);
		assertEquals(expecting, result);
	}
}
