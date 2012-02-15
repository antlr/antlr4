package org.antlr.v4.test;

import org.junit.Test;

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
			"WS : (' '|'\\n') {skip();} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "s", "x", debug);
		String expecting = "(s (a x))\n";
		assertEquals(expecting, found);

		found = execParser("T.g", grammar, "TParser", "TLexer",
						   "s", "x y", debug);
		expecting = "(s (a (a x) y))\n";
		assertEquals(expecting, found);

		found = execParser("T.g", grammar, "TParser", "TLexer",
						   "s", "x y z", debug);
		expecting = "(s (a (a (a x) y) z))\n";
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
			"WS : (' '|'\\n') {skip();} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
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
			"WS : (' '|'\\n') {skip();} ;\n";
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
			"WS : (' '|'\\n') {skip();} ;\n";
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
			"WS : (' '|'\\n') {skip();} ;\n";
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
			"WS : (' '|'\\n') {skip();} ;\n";
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
			"WS : (' '|'\\n') {skip();} ;\n";
		String[] tests = {
			"4",			"4",
		"1+2",			"3",
		"1+2*3",		"7",
		"(1+2)*3",		"9",
		};
		runTests(grammar, tests, "s");
	}

	@Test public void testReturnValueAndActionsAndLabels() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s : q=e {System.out.println($e.v);} ;\n" +
			"\n" +
			"e returns [int v]\n" +
			"  : a=e op='*' b=e {$v = $a.v * $b.v;}  -> mult\n" +
			"  | a=e '+' b=e {$v = $a.v + $b.v;}     -> add\n" +
			"  | INT         {$v = $INT.int;}\n" +
			"  | '(' x=e ')' {$v = $x.v;}\n" +
			"  | x=e '++'    {$v = $x.v+1;}          -> inc\n" +
			"  | e '--'\n" +
			"  | ID          {$v = 3;}               -> anID\n" +
			"  ; \n" +
			"\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";
		String[] tests = {
			"4",			"4",
			"1+2",			"3",
			"1+2*3",		"7",
			"i++*3",		"12",
		};
		runTests(grammar, tests, "s");
	}

	public void runTests(String grammar, String[] tests, String startRule) {
		rawGenerateAndBuildRecognizer("T.g", grammar, "TParser", "TLexer");
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
