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
import org.antlr.v4.test.runtime.RuntimeTestUtils;
import org.antlr.v4.test.runtime.java.JavaRunner;
import org.antlr.v4.tool.Grammar;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestExpectedTokens extends JavaRunner {
	@Test
	public void testEpsilonAltSubrule() throws Exception {
		String gtext =
			"parser grammar T;\n" +
			"a : A (B | ) C ;\n";
		Grammar g = new Grammar(gtext);
		String atnText =
			"RuleStart_a_0-A->BlockStart_3\n" +
			"BlockStart_3->s2\n" +
			"BlockStart_3->BlockEnd_4\n" +
			"s2-B->BlockEnd_4\n" +
			"BlockEnd_4-C->RuleStop_a_1\n" +
			"RuleStop_a_1-EOF->s5\n";
		RuntimeTestUtils.checkRuleATN(g, "a", atnText);

		ATN atn = g.getATN();
		int blkStartStateNumber = 3;
		IntervalSet tokens = atn.getExpectedTokens(blkStartStateNumber, null);
		assertEquals("{B, C}", tokens.toString(g.getTokenNames()));
	}

	@Test public void testOptionalSubrule() throws Exception {
		String gtext =
			"parser grammar T;\n" +
			"a : A B? C ;\n";
		Grammar g = new Grammar(gtext);
		String atnText =
			"RuleStart_a_0-A->BlockStart_3\n" +
			"BlockStart_3->s2\n" +
			"BlockStart_3->BlockEnd_4\n" +
			"s2-B->BlockEnd_4\n" +
			"BlockEnd_4-C->RuleStop_a_1\n" +
			"RuleStop_a_1-EOF->s5\n";
		RuntimeTestUtils.checkRuleATN(g, "a", atnText);

		ATN atn = g.getATN();
		int blkStartStateNumber = 3;
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
			"RuleStart_a_0-b->RuleStart_b_2\n" +
			"s4-A->RuleStop_a_1\n" +
			"RuleStop_a_1-EOF->s8\n";
		RuntimeTestUtils.checkRuleATN(g, "a", atnText);
		atnText =
			"RuleStart_b_2->BlockStart_6\n" +
			"BlockStart_6->s5\n" +
			"BlockStart_6->BlockEnd_7\n" +
			"s5-B->BlockEnd_7\n" +
			"BlockEnd_7->RuleStop_b_3\n" +
			"RuleStop_b_3->s4\n";
		RuntimeTestUtils.checkRuleATN(g, "b", atnText);

		ATN atn = g.getATN();

		// From the start of 'b' with empty stack, can only see B and EOF
		int blkStartStateNumber = 6;
		IntervalSet tokens = atn.getExpectedTokens(blkStartStateNumber, ParserRuleContext.EMPTY);
		assertEquals("{<EOF>, B}", tokens.toString(g.getTokenNames()));

		// Now call from 'a'
		tokens = atn.getExpectedTokens(blkStartStateNumber, new ParserRuleContext(ParserRuleContext.EMPTY, 0));
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
			"RuleStart_expr_2->BlockStart_10\n" +
			"BlockStart_10->s5\n" +
			"BlockStart_10->s9\n" +
			"s5-action_1:-1->s6\n" +
			"s9-ID->BlockEnd_11\n" +
			"s6-L->s7\n" +
			"BlockEnd_11->StarLoopEntry_16\n" +
			"s7-expr->RuleStart_expr_2\n" +
			"StarLoopEntry_16->StarBlockStart_14\n" +
			"StarLoopEntry_16->s17\n" +
			"s8-R->BlockEnd_11\n" +
			"StarBlockStart_14-2 >= _p->s12\n" +
			"s17->RuleStop_expr_3\n" +
			"s12-PLUS->s13\n" +
			"RuleStop_expr_3->s4\n" +
			"RuleStop_expr_3->s8\n" +
			"RuleStop_expr_3->BlockEnd_15\n" +
			"s13-expr->RuleStart_expr_2\n" +
			"BlockEnd_15->StarLoopBack_18\n" +
			"StarLoopBack_18->StarLoopEntry_16\n";
		RuntimeTestUtils.checkRuleATN(g, "expr", atnText);

		ATN atn = g.getATN();

//		DOTGenerator gen = new DOTGenerator(g);
//		String dot = gen.getDOT(atn.states.get(2), g.getRuleNames(), false);
//		System.out.println(dot);

		// Simulate call stack after input '(x' from rule s
		ParserRuleContext callStackFrom_s = new ParserRuleContext(null, 0);
		ParserRuleContext callStackFrom_expr = new ParserRuleContext(callStackFrom_s, 7);
		int afterID = 11;
		IntervalSet tokens = atn.getExpectedTokens(afterID, callStackFrom_expr);
		assertEquals("{R, PLUS}", tokens.toString(g.getTokenNames()));

		// Simulate call stack after input '(x' from within rule expr
		callStackFrom_expr = new ParserRuleContext(null, 7);
		tokens = atn.getExpectedTokens(afterID, callStackFrom_expr);
		assertEquals("{R, PLUS}", tokens.toString(g.getTokenNames()));
	}
}
