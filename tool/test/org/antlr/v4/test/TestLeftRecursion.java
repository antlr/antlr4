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

package org.antlr.v4.test;

import org.antlr.v4.tool.ErrorType;
import org.junit.Test;

import static org.junit.Assert.*;

/** */
public class TestLeftRecursion extends BaseTest {
	protected boolean debug = false;

	@Test public void testSimple() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s @after {System.out.println($ctx.toStringTree(this));} : a ;\n" +
			"a : a ID\n" +
			"  | ID" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer",
								  "s", "x", debug);
		String expecting = "(s (a x))\n";
		assertEquals(expecting, found);

		found = execParser("T.g4", grammar, "TParser", "TLexer",
						   "s", "x y", debug);
		expecting = "(s (a (a x) y))\n";
		assertEquals(expecting, found);

		found = execParser("T.g4", grammar, "TParser", "TLexer",
						   "s", "x y z", debug);
		expecting = "(s (a (a (a x) y) z))\n";
		assertEquals(expecting, found);
	}

	/**
	 * This is a regression test for "Support direct calls to left-recursive
	 * rules".
	 * https://github.com/antlr/antlr4/issues/161
	 */
	@Test public void testDirectCallToLeftRecursiveRule() throws Exception {
		String grammar =
			"grammar T;\n" +
			"@parser::members{public AContext a() { return a(0); }}\n" +
			"a @after {System.out.println($ctx.toStringTree(this));} : a ID\n" +
			"  | ID" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer",
								  "a", "x", debug);
		String expecting = "(a x)\n";
		assertEquals(expecting, found);

		found = execParser("T.g4", grammar, "TParser", "TLexer",
						   "a", "x y", debug);
		expecting = "(a (a x) y)\n";
		assertEquals(expecting, found);

		found = execParser("T.g4", grammar, "TParser", "TLexer",
						   "a", "x y z", debug);
		expecting = "(a (a (a x) y) z)\n";
		assertEquals(expecting, found);
	}

	@Test public void testSemPred() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s @after {System.out.println($ctx.toStringTree(this));} : a ;\n" +
			"a : a {true}? ID\n" +
			"  | ID" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer",
								  "s", "x y z", debug);
		String expecting = "(s (a (a (a x) y) z))\n";
		assertEquals(expecting, found);
	}

	@Test public void testTernaryExpr() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s @after {System.out.println($ctx.toStringTree(this));} : e EOF ;\n" + // must indicate EOF can follow or 'a<EOF>' won't match
			"e : e '*' e" +
			"  | e '+' e" +
			"  | e '?'<assoc=right> e ':' e" +
			"  | e '='<assoc=right> e" +
			"  | ID" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;\n";
		String[] tests = {
			"a",			"(s (e a) <EOF>)",
			"a+b",			"(s (e (e a) + (e b)) <EOF>)",
			"a*b",			"(s (e (e a) * (e b)) <EOF>)",
			"a?b:c",		"(s (e (e a) ? (e b) : (e c)) <EOF>)",
			"a=b=c",		"(s (e (e a) = (e (e b) = (e c))) <EOF>)",
			"a?b+c:d",		"(s (e (e a) ? (e (e b) + (e c)) : (e d)) <EOF>)",
			"a?b=c:d",		"(s (e (e a) ? (e (e b) = (e c)) : (e d)) <EOF>)",
			"a? b?c:d : e",	"(s (e (e a) ? (e (e b) ? (e c) : (e d)) : (e e)) <EOF>)",
			"a?b: c?d:e",	"(s (e (e a) ? (e b) : (e (e c) ? (e d) : (e e))) <EOF>)",
		};
		runTests(grammar, tests, "s");
	}


	@Test public void testExpressions() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s @after {System.out.println($ctx.toStringTree(this));} : e EOF ;\n" + // must indicate EOF can follow
			"e : e '.' ID\n" +
			"  | e '.' 'this'\n" +
			"  | '-' e\n" +
			"  | e '*' e\n" +
			"  | e ('+'|'-') e\n" +
			"  | INT\n" +
			"  | ID\n" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;\n";
		String[] tests = {
			"a",		"(s (e a) <EOF>)",
			"1",		"(s (e 1) <EOF>)",
			"a-1",		"(s (e (e a) - (e 1)) <EOF>)",
			"a.b",		"(s (e (e a) . b) <EOF>)",
			"a.this",	"(s (e (e a) . this) <EOF>)",
			"-a",		"(s (e - (e a)) <EOF>)",
			"-a+b",		"(s (e (e - (e a)) + (e b)) <EOF>)",
		};
		runTests(grammar, tests, "s");
	}

	@Test public void testJavaExpressions() throws Exception {
		// Generates about 7k in bytecodes for generated e_ rule;
		// Well within the 64k method limit. e_primary compiles
		// to about 2k in bytecodes.
		// this is simplified from real java
		String grammar =
			"grammar T;\n" +
			"s @after {System.out.println($ctx.toStringTree(this));} : e EOF ;\n" + // must indicate EOF can follow
			"expressionList\n" +
			"    :   e (',' e)*\n" +
			"    ;\n" +
			"e   :   '(' e ')'\n" +
			"    |   'this' \n" +
			"    |   'super'\n" +
			"    |   INT\n" +
			"    |   ID\n" +
			"    |   type '.' 'class'\n" +
			"    |   e '.' ID\n" +
			"    |   e '.' 'this'\n" +
			"    |   e '.' 'super' '(' expressionList? ')'\n" +
			"    |   e '.' 'new' ID '(' expressionList? ')'\n" +
			"	 |	 'new' type ( '(' expressionList? ')' | ('[' e ']')+)\n" +
			"    |   e '[' e ']'\n" +
			"    |   '(' type ')' e\n" +
			"    |   e ('++' | '--')\n" +
			"    |   e '(' expressionList? ')'\n" +
			"    |   ('+'|'-'|'++'|'--') e\n" +
			"    |   ('~'|'!') e\n" +
			"    |   e ('*'|'/'|'%') e\n" +
			"    |   e ('+'|'-') e\n" +
			"    |   e ('<<' | '>>>' | '>>') e\n" +
			"    |   e ('<=' | '>=' | '>' | '<') e\n" +
			"    |   e 'instanceof' e\n" +
			"    |   e ('==' | '!=') e\n" +
			"    |   e '&' e\n" +
			"    |   e '^'<assoc=right> e\n" +
			"    |   e '|' e\n" +
			"    |   e '&&' e\n" +
			"    |   e '||' e\n" +
			"    |   e '?' e ':' e\n" +
			"    |   e ('='<assoc=right>\n" +
			"          |'+='<assoc=right>\n" +
			"          |'-='<assoc=right>\n" +
			"          |'*='<assoc=right>\n" +
			"          |'/='<assoc=right>\n" +
			"          |'&='<assoc=right>\n" +
			"          |'|='<assoc=right>\n" +
			"          |'^='<assoc=right>\n" +
			"          |'>>='<assoc=right>\n" +
			"          |'>>>='<assoc=right>\n" +
			"          |'<<='<assoc=right>\n" +
			"          |'%='<assoc=right>) e\n" +
			"    ;\n" +
			"type: ID \n" +
			"    | ID '[' ']'\n" +
			"    | 'int'\n" +
			"	 | 'int' '[' ']' \n" +
			"    ;\n" +
			"ID : ('a'..'z'|'A'..'Z'|'_'|'$')+;\n" +
			"INT : '0'..'9'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;\n";
		String[] tests = {
			"a|b&c",	"(s (e (e a) | (e (e b) & (e c))) <EOF>)",
			"(a|b)&c",	"(s (e (e ( (e (e a) | (e b)) )) & (e c)) <EOF>)",
            "a > b",	"(s (e (e a) > (e b)) <EOF>)",
			"a >> b",	"(s (e (e a) >> (e b)) <EOF>)",
			"(T)x",							"(s (e ( (type T) ) (e x)) <EOF>)",
			"new A().b",					"(s (e (e new (type A) ( )) . b) <EOF>)",
			"(T)t.f()",						"(s (e (e ( (type T) ) (e (e t) . f)) ( )) <EOF>)",
			"a.f(x)==T.c",					"(s (e (e (e (e a) . f) ( (expressionList (e x)) )) == (e (e T) . c)) <EOF>)",
			"a.f().g(x,1)",					"(s (e (e (e (e (e a) . f) ( )) . g) ( (expressionList (e x) , (e 1)) )) <EOF>)",
			"new T[((n-1) * x) + 1]",		"(s (e new (type T) [ (e (e ( (e (e ( (e (e n) - (e 1)) )) * (e x)) )) + (e 1)) ]) <EOF>)",
		};
		runTests(grammar, tests, "s");
	}

	@Test public void testDeclarations() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s @after {System.out.println($ctx.toStringTree(this));} : declarator EOF ;\n" + // must indicate EOF can follow
			"declarator\n" +
			"        : declarator '[' e ']'\n" +
			"        | declarator '[' ']'\n" +
			"        | declarator '(' ')'\n" +
			"        | '*' declarator\n" + // binds less tight than suffixes
			"        | '(' declarator ')'\n" +
			"        | ID\n" +
			"        ;\n" +
			"e : INT ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;\n";
		String[] tests = {
			"a",		"(s (declarator a) <EOF>)",
			"*a",		"(s (declarator * (declarator a)) <EOF>)",
			"**a",		"(s (declarator * (declarator * (declarator a))) <EOF>)",
			"a[3]",		"(s (declarator (declarator a) [ (e 3) ]) <EOF>)",
			"b[]",		"(s (declarator (declarator b) [ ]) <EOF>)",
			"(a)",		"(s (declarator ( (declarator a) )) <EOF>)",
			"a[]()",	"(s (declarator (declarator (declarator a) [ ]) ( )) <EOF>)",
			"a[][]",	"(s (declarator (declarator (declarator a) [ ]) [ ]) <EOF>)",
			"*a[]",		"(s (declarator * (declarator (declarator a) [ ])) <EOF>)",
			"(*a)[]",	"(s (declarator (declarator ( (declarator * (declarator a)) )) [ ]) <EOF>)",
		};
		runTests(grammar, tests, "s");
	}

	@Test public void testReturnValueAndActions() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s : e {System.out.println($e.v);} ;\n" +
			"e returns [int v, List<String> ignored]\n" +
			"  : a=e '*' b=e {$v = $a.v * $b.v;}\n" +
			"  | a=e '+' b=e {$v = $a.v + $b.v;}\n" +
			"  | INT {$v = $INT.int;}\n" +
			"  | '(' x=e ')' {$v = $x.v;}\n" +
			"  ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;\n";
		String[] tests = {
			"4",			"4",
		"1+2",			"3",
		"1+2*3",		"7",
		"(1+2)*3",		"9",
		};
		runTests(grammar, tests, "s");
	}

	@Test public void testLabelsOnOpSubrule() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s @after {System.out.println($ctx.toStringTree(this));} : e ;\n" +
			"e : a=e op=('*'|'/') b=e  {}\n" +
			"  | INT {}\n" +
			"  | '(' x=e ')' {}\n" +
			"  ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;\n";
		String[] tests = {
			"4",		"(s (e 4))",
		"1*2/3",		"(s (e (e (e 1) * (e 2)) / (e 3)))",
		"(1/2)*3",		"(s (e (e ( (e (e 1) / (e 2)) )) * (e 3)))",
		};
		runTests(grammar, tests, "s");
	}

	@Test public void testReturnValueAndActionsAndLabels() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s : q=e {System.out.println($e.v);} ;\n" +
			"\n" +
			"e returns [int v]\n" +
			"  : a=e op='*' b=e {$v = $a.v * $b.v;}  # mult\n" +
			"  | a=e '+' b=e {$v = $a.v + $b.v;}     # add\n" +
			"  | INT         {$v = $INT.int;}        # anInt\n" +
			"  | '(' x=e ')' {$v = $x.v;}            # parens\n" +
			"  | x=e '++'    {$v = $x.v+1;}          # inc\n" +
			"  | e '--'                              # dec\n" +
			"  | ID          {$v = 3;}               # anID\n" +
			"  ; \n" +
			"\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;\n";
		String[] tests = {
			"4",			"4",
			"1+2",			"3",
			"1+2*3",		"7",
			"i++*3",		"12",
		};
		runTests(grammar, tests, "s");
	}

	@Test public void testPrefixOpWithActionAndLabel() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s : e {System.out.println($e.result);} ;\n" +
			"\n" +
			"e returns [String result]\n" +
			"    :   ID '=' e1=e    { $result = \"(\" + $ID.getText() + \"=\" + $e1.result + \")\"; }\n" +
			"    |   ID             { $result = $ID.getText(); }\n" +
			"    |   e1=e '+' e2=e  { $result = \"(\" + $e1.result + \"+\" + $e2.result + \")\"; }\n" +
			"    ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;\n";
		String[] tests = {
			"a",			"a",
			"a+b",			"(a+b)",
			"a=b+c",		"((a=b)+c)",
		};
		runTests(grammar, tests, "s");
	}

	@Test
	public void testAmbigLR() throws Exception {
		String grammar =
			"grammar Expr;\n" +
			"prog:   stat ;\n" +
			"stat:   expr NEWLINE                # printExpr\n" +
			"    |   ID '=' expr NEWLINE         # assign\n" +
			"    |   NEWLINE                     # blank\n" +
			"    ;\n" +
			"expr:   expr ('*'|'/') expr      # MulDiv\n" +
			"    |   expr ('+'|'-') expr      # AddSub\n" +
			"    |   INT                      # int\n" +
			"    |   ID                       # id\n" +
			"    |   '(' expr ')'             # parens\n" +
			"    ;\n" +
			"\n" +
			"MUL :   '*' ; // assigns token name to '*' used above in grammar\n" +
			"DIV :   '/' ;\n" +
			"ADD :   '+' ;\n" +
			"SUB :   '-' ;\n" +
			"ID  :   [a-zA-Z]+ ;      // match identifiers\n" +
			"INT :   [0-9]+ ;         // match integers\n" +
			"NEWLINE:'\\r'? '\\n' ;     // return newlines to parser (is end-statement signal)\n" +
			"WS  :   [ \\t]+ -> skip ; // toss out whitespace\n";
		String result = execParser("Expr.g4", grammar, "ExprParser", "ExprLexer", "prog", "1\n", true);
		assertNull(stderrDuringParse);

		result = execParser("Expr.g4", grammar, "ExprParser", "ExprLexer", "prog", "a = 5\n", true);
		assertNull(stderrDuringParse);

		result = execParser("Expr.g4", grammar, "ExprParser", "ExprLexer", "prog", "b = 6\n", true);
		assertNull(stderrDuringParse);

		result = execParser("Expr.g4", grammar, "ExprParser", "ExprLexer", "prog", "a+b*2\n", true);
		assertEquals("line 1:1 reportAttemptingFullContext d=3 (expr), input='+'\n" +
					 "line 1:1 reportContextSensitivity d=3 (expr), input='+'\n" +
					 "line 1:3 reportAttemptingFullContext d=3 (expr), input='*'\n",
					 stderrDuringParse);

		result = execParser("Expr.g4", grammar, "ExprParser", "ExprLexer", "prog", "(1+2)*3\n", true);
		assertEquals("line 1:2 reportAttemptingFullContext d=3 (expr), input='+'\n" +
					 "line 1:2 reportContextSensitivity d=3 (expr), input='+'\n" +
					 "line 1:5 reportAttemptingFullContext d=3 (expr), input='*'\n" +
					 "line 1:5 reportContextSensitivity d=3 (expr), input='*'\n",
					 stderrDuringParse);
	}

	@Test public void testCheckForNonLeftRecursiveRule() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s @after {System.out.println($ctx.toStringTree(this));} : a ;\n" +
			"a : a ID\n" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;\n";
		String expected =
			"error(" + ErrorType.NO_NON_LR_ALTS.code + "): T.g4:3:0: left recursive rule 'a' must contain an alternative which is not left recursive\n";
		testErrors(new String[] { grammar, expected }, false);
	}

	@Test public void testCheckForLeftRecursiveEmptyFollow() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s @after {System.out.println($ctx.toStringTree(this));} : a ;\n" +
			"a : a ID?\n" +
			"  | ID\n" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;\n";
		String expected =
			"error(" + ErrorType.EPSILON_LR_FOLLOW.code + "): T.g4:3:0: left recursive rule 'a' contains a left recursive alternative which can be followed by the empty string\n";
		testErrors(new String[] { grammar, expected }, false);
	}

	public void runTests(String grammar, String[] tests, String startRule) {
		rawGenerateAndBuildRecognizer("T.g4", grammar, "TParser", "TLexer");
		writeRecognizerAndCompile("TParser",
								  "TLexer",
								  startRule,
								  debug);

		for (int i=0; i<tests.length; i+=2) {
			String test = tests[i];
			String expecting = tests[i+1]+"\n";
			writeFile(tmpdir, "input", test);
			String found = execRecognizer();
			System.out.print(test+" -> "+found);
			assertEquals(expecting, found);
		}
	}

}
