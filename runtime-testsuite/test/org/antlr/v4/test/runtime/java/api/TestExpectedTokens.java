package org.antlr.v4.test.runtime.java.api;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.test.runtime.java.BaseJavaTest;
import org.antlr.v4.tool.DOTGenerator;
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

	@Test public void testFollowIncludedInLeftRecursiveRule() throws Exception {
		String gtext =
			"grammar T;\n" +
			"expr : L expr R\n"+
			"     | expr PLUS expr\n"+
			"     | ID\n"+
			"     ;\n";
		Grammar g = new Grammar(gtext);
		String atnText =
			"RuleStart_expr_0->BlockStart_8\n"+
			"BlockStart_8->s2\n"+
			"BlockStart_8->s7\n"+
			"s2-action_0:-1->s3\n"+
			"s7-ID->BlockEnd_9\n"+
			"s3-L->s4\n"+
			"BlockEnd_9->StarLoopEntry_15\n"+
			"s4-expr->RuleStart_expr_0\n"+
			"StarLoopEntry_15->StarBlockStart_13\n"+
			"StarLoopEntry_15->s16\n"+
			"s5-R->s6\n"+
			"StarBlockStart_13->s10\n"+
			"s16->RuleStop_expr_1\n"+
			"s6->BlockEnd_9\n"+
			"s10-2 >= _p->s11\n"+
			"RuleStop_expr_1->s5\n"+
			"RuleStop_expr_1->BlockEnd_14\n"+
			"s11-PLUS->s12\n"+
			"s12-expr->RuleStart_expr_0\n"+
			"BlockEnd_14->StarLoopBack_17\n"+
			"StarLoopBack_17->StarLoopEntry_15\n";
		checkRuleATN(g, "expr", atnText);

		ATN atn = g.getATN();
		DOTGenerator gen = new DOTGenerator(g);
		String dot = gen.getDOT(atn.states.get(0), g.getRuleNames(), false);
		System.out.println(dot);
		int blkStartStateNumber = 9;
		IntervalSet tokens = atn.getExpectedTokens(blkStartStateNumber, null);
		assertEquals("{<EOF>, PLUS}", tokens.toString(g.getTokenNames()));
	}
}
