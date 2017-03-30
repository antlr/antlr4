/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.java.api;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.test.runtime.java.BaseJavaTest;
import org.antlr.v4.tool.Grammar;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestExpectedTokens extends BaseJavaTest {
	@Test public void testEpsilonAltSubrule() throws Exception {
		String gtext =
			"parser grammar T;\n" +
			"a : A (B | ) C ;\n";
		Grammar g = new Grammar(gtext);
		String atnText =
			"RuleStart_a_0->s2\n"+
			"s2-A->BlockStart_5\n"+
			"BlockStart_5->s3\n"+
			"BlockStart_5->s4\n"+
			"s3-B->BlockEnd_6\n"+
			"s4->BlockEnd_6\n"+
			"BlockEnd_6->s7\n"+
			"s7-C->s8\n"+
			"s8->RuleStop_a_1\n"+
			"RuleStop_a_1-EOF->s9\n";
		checkRuleATN(g, "a", atnText);

		ATN atn = g.getATN();
		int blkStartStateNumber = 5;
		IntervalSet tokens = atn.getExpectedTokens(blkStartStateNumber, null);
		assertEquals("{B, C}", tokens.toString(g.getTokenNames()));
	}

	@Test public void testOptionalSubrule() throws Exception {
		String gtext =
			"parser grammar T;\n" +
			"a : A B? C ;\n";
		Grammar g = new Grammar(gtext);
		String atnText =
			"RuleStart_a_0->s2\n"+
			"s2-A->BlockStart_4\n"+
			"BlockStart_4->s3\n"+
			"BlockStart_4->BlockEnd_5\n"+
			"s3-B->BlockEnd_5\n"+
			"BlockEnd_5->s6\n"+
			"s6-C->s7\n"+
			"s7->RuleStop_a_1\n"+
			"RuleStop_a_1-EOF->s8\n";
		checkRuleATN(g, "a", atnText);

		ATN atn = g.getATN();
		int blkStartStateNumber = 4;
		IntervalSet tokens = atn.getExpectedTokens(blkStartStateNumber, null);
		assertEquals("{B, C}", tokens.toString(g.getTokenNames()));
	}

	@Test public void testFollowIncluded() throws Exception {
		String gtext =
			"parser grammar T;\n" +
				"a : b A ;\n" +
				"b : B | ;";
		Grammar g = new Grammar(gtext);
		String atnText =
			"RuleStart_a_0->s4\n"+
			"s4-b->RuleStart_b_2\n"+
			"s5-A->s6\n"+
			"s6->RuleStop_a_1\n"+
			"RuleStop_a_1-EOF->s11\n";
		checkRuleATN(g, "a", atnText);
		atnText =
			"RuleStart_b_2->BlockStart_9\n"+
			"BlockStart_9->s7\n"+
			"BlockStart_9->s8\n"+
			"s7-B->BlockEnd_10\n"+
			"s8->BlockEnd_10\n"+
			"BlockEnd_10->RuleStop_b_3\n"+
			"RuleStop_b_3->s5\n";
		checkRuleATN(g, "b", atnText);

		ATN atn = g.getATN();

		// From the start of 'b' with empty stack, can only see B and EOF
		int blkStartStateNumber = 9;
		IntervalSet tokens = atn.getExpectedTokens(blkStartStateNumber, RuleContext.EMPTY);
		assertEquals("{<EOF>, B}", tokens.toString(g.getTokenNames()));

		// Now call from 'a'
		tokens = atn.getExpectedTokens(blkStartStateNumber, new ParserRuleContext(ParserRuleContext.EMPTY, 4));
		assertEquals("{A, B}", tokens.toString(g.getTokenNames()));
	}

	// Test for https://github.com/antlr/antlr4/issues/1480
	// can't reproduce
	@Test public void testFollowIncludedInLeftRecursiveRule() throws Exception {
		String gtext =
			"grammar T;\n" +
			"s : expr EOF ;\n" +
			"expr : L expr R\n"+
			"     | expr PLUS expr\n"+
			"     | ID\n"+
			"     ;\n";
		Grammar g = new Grammar(gtext);
		String atnText =
			"RuleStart_expr_2->BlockStart_13\n"+
			"BlockStart_13->s7\n"+
			"BlockStart_13->s12\n"+
			"s7-action_1:-1->s8\n"+
			"s12-ID->BlockEnd_14\n"+
			"s8-L->s9\n"+
			"BlockEnd_14->StarLoopEntry_20\n"+
			"s9-expr->RuleStart_expr_2\n"+
			"StarLoopEntry_20->StarBlockStart_18\n"+
			"StarLoopEntry_20->s21\n"+
			"s10-R->s11\n"+
			"StarBlockStart_18->s15\n"+
			"s21->RuleStop_expr_3\n"+
			"s11->BlockEnd_14\n"+
			"s15-2 >= _p->s16\n"+
			"RuleStop_expr_3->s5\n"+
			"RuleStop_expr_3->s10\n"+
			"RuleStop_expr_3->BlockEnd_19\n"+
			"s16-PLUS->s17\n"+
			"s17-expr->RuleStart_expr_2\n"+
			"BlockEnd_19->StarLoopBack_22\n"+
			"StarLoopBack_22->StarLoopEntry_20\n";
		checkRuleATN(g, "expr", atnText);

		ATN atn = g.getATN();

//		DOTGenerator gen = new DOTGenerator(g);
//		String dot = gen.getDOT(atn.states.get(2), g.getRuleNames(), false);
//		System.out.println(dot);

		// Simulate call stack after input '(x' from rule s
		ParserRuleContext callStackFrom_s = new ParserRuleContext(null, 4);
		ParserRuleContext callStackFrom_expr = new ParserRuleContext(callStackFrom_s, 9);
		int afterID = 14;
		IntervalSet tokens = atn.getExpectedTokens(afterID, callStackFrom_expr);
		assertEquals("{R, PLUS}", tokens.toString(g.getTokenNames()));

		// Simulate call stack after input '(x' from within rule expr
		callStackFrom_expr = new ParserRuleContext(null, 9);
		tokens = atn.getExpectedTokens(afterID, callStackFrom_expr);
		assertEquals("{R, PLUS}", tokens.toString(g.getTokenNames()));
	}
}
