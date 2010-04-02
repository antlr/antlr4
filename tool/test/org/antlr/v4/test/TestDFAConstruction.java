package org.antlr.v4.test;

import org.antlr.v4.tool.Grammar;
import org.junit.Test;

public class TestDFAConstruction extends BaseTest {
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
		//checkRule(g, "a", expecting);
	}

}
