package org.antlr.v4.test.rt.js.explorer;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestFullContextParsing extends BaseTest {

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testAmbigYieldsCtxSensitiveDFA() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s @after {this.dumpDFA();}\n" +
	                  "	: ID | ID {} ;\n" +
	                  "ID : 'a'..'z'+;\n" +
	                  "WS : (' '|'\\t'|'\\n')+ -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "abc", true);
		assertEquals("Decision 0:\ns0-ID->:s1^=>1\n", found);
		assertEquals("line 1:0 reportAttemptingFullContext d=0 (s), input='abc'\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testCtxSensitiveDFA(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "s @after {this.dumpDFA();}\n" +
	                  "  : '$' a | '@' b ;\n" +
	                  "a : e ID ;\n" +
	                  "b : e INT ID ;\n" +
	                  "e : INT | ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+ ;\n" +
	                  "WS : (' '|'\\t'|'\\n')+ -> skip ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, true);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testCtxSensitiveDFA_1() throws Exception {
		String found = testCtxSensitiveDFA("$ 34 abc");
		assertEquals("Decision 1:\ns0-INT->s1\ns1-ID->:s2^=>1\n", found);
		assertEquals("line 1:5 reportAttemptingFullContext d=1 (e), input='34abc'\nline 1:2 reportContextSensitivity d=1 (e), input='34'\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testCtxSensitiveDFA_2() throws Exception {
		String found = testCtxSensitiveDFA("@ 34 abc");
		assertEquals("Decision 1:\ns0-INT->s1\ns1-ID->:s2^=>1\n", found);
		assertEquals("line 1:5 reportAttemptingFullContext d=1 (e), input='34abc'\nline 1:5 reportContextSensitivity d=1 (e), input='34abc'\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testCtxSensitiveDFATwoDiffInput() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s @after {this.dumpDFA();}\n" +
	                  "  : ('$' a | '@' b)+ ;\n" +
	                  "a : e ID ;\n" +
	                  "b : e INT ID ;\n" +
	                  "e : INT | ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+ ;\n" +
	                  "WS : (' '|'\\t'|'\\n')+ -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "$ 34 abc @ 34 abc", true);
		assertEquals("Decision 2:\ns0-INT->s1\ns1-ID->:s2^=>1\n", found);
		assertEquals("line 1:5 reportAttemptingFullContext d=2 (e), input='34abc'\nline 1:2 reportContextSensitivity d=2 (e), input='34'\nline 1:14 reportAttemptingFullContext d=2 (e), input='34abc'\nline 1:14 reportContextSensitivity d=2 (e), input='34abc'\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testSLLSeesEOFInLLGrammar() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s @after {this.dumpDFA();}\n" +
	                  "  : a;\n" +
	                  "a : e ID ;\n" +
	                  "b : e INT ID ;\n" +
	                  "e : INT | ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+ ;\n" +
	                  "WS : (' '|'\\t'|'\\n')+ -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "34 abc", true);
		assertEquals("Decision 0:\ns0-INT->s1\ns1-ID->:s2^=>1\n", found);
		assertEquals("line 1:3 reportAttemptingFullContext d=0 (e), input='34abc'\nline 1:0 reportContextSensitivity d=0 (e), input='34'\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testFullContextIF_THEN_ELSEParse(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "s \n" +
	                  "@init {this._interp.predictionMode = antlr4.atn.PredictionMode.LL_EXACT_AMBIG_DETECTION;}\n" +
	                  "@after {this.dumpDFA();}\n" +
	                  "	: '{' stat* '}' ;\n" +
	                  "stat: 'if' ID 'then' stat ('else' ID)?\n" +
	                  "		| 'return'\n" +
	                  "		;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "WS : (' '|'\\t'|'\\n')+ -> skip ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, true);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testFullContextIF_THEN_ELSEParse_1() throws Exception {
		String found = testFullContextIF_THEN_ELSEParse("{ if x then return }");
		assertEquals("Decision 1:\ns0-'}'->:s1=>2\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testFullContextIF_THEN_ELSEParse_2() throws Exception {
		String found = testFullContextIF_THEN_ELSEParse("{ if x then return else foo }");
		assertEquals("Decision 1:\ns0-'else'->:s1^=>1\n", found);
		assertEquals("line 1:19 reportAttemptingFullContext d=1 (stat), input='else'\nline 1:19 reportContextSensitivity d=1 (stat), input='else'\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testFullContextIF_THEN_ELSEParse_3() throws Exception {
		String found = testFullContextIF_THEN_ELSEParse("{ if x then if y then return else foo }");
		assertEquals("Decision 1:\ns0-'}'->:s2=>2\ns0-'else'->:s1^=>1\n", found);
		assertEquals("line 1:29 reportAttemptingFullContext d=1 (stat), input='else'\nline 1:38 reportAmbiguity d=1 (stat): ambigAlts={1, 2}, input='elsefoo}'\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testFullContextIF_THEN_ELSEParse_4() throws Exception {
		String found = testFullContextIF_THEN_ELSEParse("{ if x then if y then return else foo else bar }");
		assertEquals("Decision 1:\ns0-'else'->:s1^=>1\n", found);
		assertEquals("line 1:29 reportAttemptingFullContext d=1 (stat), input='else'\nline 1:38 reportContextSensitivity d=1 (stat), input='elsefooelse'\nline 1:38 reportAttemptingFullContext d=1 (stat), input='else'\nline 1:38 reportContextSensitivity d=1 (stat), input='else'\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testFullContextIF_THEN_ELSEParse_5() throws Exception {
		String found = testFullContextIF_THEN_ELSEParse("{ if x then return else foo\nif x then if y then return else foo }");
		assertEquals("Decision 1:\ns0-'}'->:s2=>2\ns0-'else'->:s1^=>1\n", found);
		assertEquals("line 1:19 reportAttemptingFullContext d=1 (stat), input='else'\nline 1:19 reportContextSensitivity d=1 (stat), input='else'\nline 2:27 reportAttemptingFullContext d=1 (stat), input='else'\nline 2:36 reportAmbiguity d=1 (stat): ambigAlts={1, 2}, input='elsefoo}'\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testFullContextIF_THEN_ELSEParse_6() throws Exception {
		String found = testFullContextIF_THEN_ELSEParse("{ if x then return else foo\nif x then if y then return else foo }");
		assertEquals("Decision 1:\ns0-'}'->:s2=>2\ns0-'else'->:s1^=>1\n", found);
		assertEquals("line 1:19 reportAttemptingFullContext d=1 (stat), input='else'\nline 1:19 reportContextSensitivity d=1 (stat), input='else'\nline 2:27 reportAttemptingFullContext d=1 (stat), input='else'\nline 2:36 reportAmbiguity d=1 (stat): ambigAlts={1, 2}, input='elsefoo}'\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testLoopsSimulateTailRecursion() throws Exception {
		String grammar = "grammar T;\n" +
	                  "prog\n" +
	                  "@init {this._interp.predictionMode = antlr4.atn.PredictionMode.LL_EXACT_AMBIG_DETECTION;}\n" +
	                  "	: expr_or_assign*;\n" +
	                  "expr_or_assign\n" +
	                  "	: expr '++' {document.getElementById('output').value += \"fail.\" + '\\n';}\n" +
	                  "	|  expr {document.getElementById('output').value += \"pass: \"+$expr.text + '\\n';}\n" +
	                  "	;\n" +
	                  "expr: expr_primary ('<-' ID)?;\n" +
	                  "expr_primary\n" +
	                  "	: '(' ID ')'\n" +
	                  "	| ID '(' ID ')'\n" +
	                  "	| ID\n" +
	                  "	;\n" +
	                  "ID  : [a-z]+ ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "prog", "a(i)<-x", true);
		assertEquals("pass: a(i)<-x\n", found);
		assertEquals("line 1:3 reportAttemptingFullContext d=3 (expr_primary), input='a(i)'\nline 1:7 reportAmbiguity d=3 (expr_primary): ambigAlts={2, 3}, input='a(i)<-x'\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testAmbiguityNoLoop() throws Exception {
		String grammar = "grammar T;\n" +
	                  "prog\n" +
	                  "@init {this._interp.predictionMode = antlr4.atn.PredictionMode.LL_EXACT_AMBIG_DETECTION;}\n" +
	                  "	: expr expr {document.getElementById('output').value += \"alt 1\" + '\\n';}\n" +
	                  "	| expr\n" +
	                  "	;\n" +
	                  "expr: '@'\n" +
	                  "	| ID '@'\n" +
	                  "	| ID\n" +
	                  "	;\n" +
	                  "ID  : [a-z]+ ;\n" +
	                  "WS  : [ \\r\\n\\t]+ -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "prog", "a@", true);
		assertEquals("alt 1\n", found);
		assertEquals("line 1:2 reportAttemptingFullContext d=0 (prog), input='a@'\nline 1:2 reportAmbiguity d=0 (prog): ambigAlts={1, 2}, input='a@'\nline 1:2 reportAttemptingFullContext d=1 (expr), input='a@'\nline 1:2 reportContextSensitivity d=1 (expr), input='a@'\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testExprAmbiguity(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "s\n" +
	                  "@init {this._interp.predictionMode = antlr4.atn.PredictionMode.LL_EXACT_AMBIG_DETECTION;}\n" +
	                  ":   expr[0] {document.getElementById('output').value += $expr.ctx.toStringTree(null, this) + '\\n';};\n" +
	                  "	expr[int _p]\n" +
	                  "		: ID \n" +
	                  "		( \n" +
	                  "			{5 >= $_p}? '*' expr[6]\n" +
	                  "			| {4 >= $_p}? '+' expr[5]\n" +
	                  "		)*\n" +
	                  "		;\n" +
	                  "ID  : [a-zA-Z]+ ;\n" +
	                  "WS  : [ \\r\\n\\t]+ -> skip ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, true);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testExprAmbiguity_1() throws Exception {
		String found = testExprAmbiguity("a+b");
		assertEquals("(expr a + (expr b))\n", found);
		assertEquals("line 1:1 reportAttemptingFullContext d=1 (expr), input='+'\nline 1:2 reportContextSensitivity d=1 (expr), input='+b'\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testExprAmbiguity_2() throws Exception {
		String found = testExprAmbiguity("a+b*c");
		assertEquals("(expr a + (expr b * (expr c)))\n", found);
		assertEquals("line 1:1 reportAttemptingFullContext d=1 (expr), input='+'\nline 1:2 reportContextSensitivity d=1 (expr), input='+b'\nline 1:3 reportAttemptingFullContext d=1 (expr), input='*'\nline 1:5 reportAmbiguity d=1 (expr): ambigAlts={1, 2}, input='*c'\n", this.stderrDuringParse);
	}


}