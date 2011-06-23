package org.antlr.v4.test;

import org.junit.Test;

/** */
public class TestLeftRecursion extends BaseTest {
	protected boolean debug = false;

	@Test public void testSimple() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s : a {System.out.println($a.text);} ;\n" +
			"a : a ID\n" +
			"  | ID" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "s", "a b c", debug);
		String expecting = "abc\n";
		assertEquals(expecting, found);
	}

	@Test public void testSemPred() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s : a {System.out.println($a.text);} ;\n" +
			"a : a {true}? ID\n" +
			"  | ID" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
								  "s", "a b c", debug);
		String expecting = "abc\n";
		assertEquals(expecting, found);
	}

	@Test public void testTernaryExpr() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"e : e '*'^ e" +
			"  | e '+'^ e" +
			"  | e '?'<assoc=right>^ e ':'! e" +
			"  | e '='<assoc=right>^ e" +
			"  | ID" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";
		String[] tests = {
			"a",			"a",
			"a+b",			"(+ a b)",
			"a*b",			"(* a b)",
			"a?b:c",		"(? a b c)",
			"a=b=c",		"(= a (= b c))",
			"a?b+c:d",		"(? a (+ b c) d)",
			"a?b=c:d",		"(? a (= b c) d)",
			"a? b?c:d : e",	"(? a (? b c d) e)",
			"a?b: c?d:e",	"(? a b (? c d e))",
		};
		runTests(grammar, tests, "e");
	}

	@Test public void testDeclarationsUsingASTOperators() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"declarator\n" +
			"        : declarator '['^ e ']'!\n" +
			"        | declarator '['^ ']'!\n" +
			"        | declarator '('^ ')'!\n" +
			"        | '*'^ declarator\n" + // binds less tight than suffixes
			"        | '('! declarator ')'!\n" +
			"        | ID\n" +
			"        ;\n" +
			"e : INT ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";
		String[] tests = {
			"a",		"a",
			"*a",		"(* a)",
			"**a",		"(* (* a))",
			"a[3]",		"([ a 3)",
			"b[]",		"([ b)",
			"(a)",		"a",
			"a[]()",	"(( ([ a))",
			"a[][]",	"([ ([ a))",
			"*a[]",		"(* ([ a))",
			"(*a)[]",	"([ (* a))",
		};
		runTests(grammar, tests, "declarator");
	}

	@Test public void testDeclarationsUsingRewriteOperators() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"declarator\n" +
			"        : declarator '[' e ']' -> ^('[' declarator e)\n" +
			"        | declarator '[' ']' -> ^('[' declarator)\n" +
			"        | declarator '(' ')' -> ^('(' declarator)\n" +
			"        | '*' declarator -> ^('*' declarator) \n" + // binds less tight than suffixes
			"        | '(' declarator ')' -> declarator\n" +
			"        | ID -> ID\n" +
			"        ;\n" +
			"e : INT ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";
		String[] tests = {
			"a",		"a",
			"*a",		"(* a)",
			"**a",		"(* (* a))",
			"a[3]",		"([ a 3)",
			"b[]",		"([ b)",
			"(a)",		"a",
			"a[]()",	"(( ([ a))",
			"a[][]",	"([ ([ a))",
			"*a[]",		"(* ([ a))",
			"(*a)[]",	"([ (* a))",
		};
		runTests(grammar, tests, "declarator");
	}

	@Test public void testExpressionsUsingASTOperators() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"e : e '.'^ ID\n" +
			"  | e '.'^ 'this'\n" +
			"  | '-'^ e\n" +
			"  | e '*'^ e\n" +
			"  | e ('+'^|'-'^) e\n" +
			"  | INT\n" +
			"  | ID\n" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";
		String[] tests = {
			"a",		"a",
			"1",		"1",
			"a+1",		"(+ a 1)",
			"a*1",		"(* a 1)",
			"a.b",		"(. a b)",
			"a.this",	"(. a this)",
			"a-b+c",	"(+ (- a b) c)",
			"a+b*c",	"(+ a (* b c))",
			"a.b+1",	"(+ (. a b) 1)",
			"-a",		"(- a)",
			"-a+b",		"(+ (- a) b)",
			"-a.b",		"(- (. a b))",
		};
		runTests(grammar, tests, "e");
	}

	@Test public void testExpressionsUsingRewriteOperators() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"e : e '.' ID 				-> ^('.' e ID)\n" +
			"  | e '.' 'this' 			-> ^('.' e 'this')\n" +
			"  | '-' e 					-> ^('-' e)\n" +
			"  | e '*' b=e 				-> ^('*' e $b)\n" +
			"  | e (op='+'|op='-') b=e	-> ^($op e $b)\n" +
			"  | INT 					-> INT\n" +
			"  | ID 					-> ID\n" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";
		String[] tests = {
			"a",		"a",
			"1",		"1",
			"a+1",		"(+ a 1)",
			"a*1",		"(* a 1)",
			"a.b",		"(. a b)",
			"a.this",	"(. a this)",
			"a+b*c",	"(+ a (* b c))",
			"a.b+1",	"(+ (. a b) 1)",
			"-a",		"(- a)",
			"-a+b",		"(+ (- a) b)",
			"-a.b",		"(- (. a b))",
		};
		runTests(grammar, tests, "e");
	}

	@Test public void testExpressionAssociativity() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"e\n" +
			"  : e '.'^ ID\n" +
			"  | '-'^ e\n" +
			"  | e '^'<assoc=right>^ e\n" +
			"  | e '*'^ e\n" +
			"  | e ('+'^|'-'^) e\n" +
			"  | e ('='<assoc=right>^ |'+='<assoc=right>^) e\n" +
			"  | INT\n" +
			"  | ID\n" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";
		String[] tests = {
			"a",		"a",
			"1",		"1",
			"a+1",		"(+ a 1)",
			"a*1",		"(* a 1)",
			"a.b",		"(. a b)",
			"a-b+c",	"(+ (- a b) c)",

			"a+b*c",	"(+ a (* b c))",
			"a.b+1",	"(+ (. a b) 1)",
			"-a",		"(- a)",
			"-a+b",		"(+ (- a) b)",
			"-a.b",		"(- (. a b))",
			"a^b^c",	"(^ a (^ b c))",
			"a=b=c",	"(= a (= b c))",
			"a=b=c+d.e","(= a (= b (+ c (. d e))))",
		};
		runTests(grammar, tests, "e");
	}

	@Test public void testJavaExpressions() throws Exception {
		// Generates about 7k in bytecodes for generated e_ rule;
		// Well within the 64k method limit. e_primary compiles
		// to about 2k in bytecodes.
		// this is simplified from real java
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"expressionList\n" +
			"    :   e (','! e)*\n" +
			"    ;\n" +
			"e   :   '('! e ')'!\n" +
			"    |   'this' \n" +
			"    |   'super'\n" +
			"    |   INT\n" +
			"    |   ID\n" +
			"    |   type '.'^ 'class'\n" +
			"    |   e '.'^ ID\n" +
			"    |   e '.'^ 'this'\n" +
			"    |   e '.'^ 'super' '('^ expressionList? ')'!\n" +
			"    |   e '.'^ 'new'^ ID '('! expressionList? ')'!\n" +
			"	 |	 'new'^ type ( '(' expressionList? ')'! | (options {k=1;}:'[' e ']'!)+)\n" + // ugly; simplified
			"    |   e '['^ e ']'!\n" +
			"    |   '('^ type ')'! e\n" +
			"    |   e ('++'^ | '--'^)\n" +
			"    |   e '('^ expressionList? ')'!\n" +
			"    |   ('+'^|'-'^|'++'^|'--'^) e\n" +
			"    |   ('~'^|'!'^) e\n" +
			"    |   e ('*'^|'/'^|'%'^) e\n" +
			"    |   e ('+'^|'-'^) e\n" +
			"    |   e ('<'^ '<' | '>'^ '>' '>' | '>'^ '>') e\n" +
			"    |   e ('<='^ | '>='^ | '>'^ | '<'^) e\n" +
			"    |   e 'instanceof'^ e\n" +
			"    |   e ('=='^ | '!='^) e\n" +
			"    |   e '&'^ e\n" +
			"    |   e '^'<assoc=right>^ e\n" +
			"    |   e '|'^ e\n" +
			"    |   e '&&'^ e\n" +
			"    |   e '||'^ e\n" +
			"    |   e '?' e ':' e\n" +
			"    |   e ('='<assoc=right>^\n" +
			"          |'+='<assoc=right>^\n" +
			"          |'-='<assoc=right>^\n" +
			"          |'*='<assoc=right>^\n" +
			"          |'/='<assoc=right>^\n" +
			"          |'&='<assoc=right>^\n" +
			"          |'|='<assoc=right>^\n" +
			"          |'^='<assoc=right>^\n" +
			"          |'>>='<assoc=right>^\n" +
			"          |'>>>='<assoc=right>^\n" +
			"          |'<<='<assoc=right>^\n" +
			"          |'%='<assoc=right>^) e\n" +
			"    ;\n" +
			"type: ID \n" +
			"    | ID '['^ ']'!\n" +
			"    | 'int'\n" +
			"	 | 'int' '['^ ']'! \n" +
			"    ;\n" +
			"ID : ('a'..'z'|'A'..'Z'|'_'|'$')+;\n" +
			"INT : '0'..'9'+ ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";
		String[] tests = {
			"a",		"a",
			"1",		"1",
			"a+1",		"(+ a 1)",
			"a*1",		"(* a 1)",
			"a.b",		"(. a b)",
			"a-b+c",	"(+ (- a b) c)",

			"a+b*c",	"(+ a (* b c))",
			"a.b+1",	"(+ (. a b) 1)",
			"-a",		"(- a)",
			"-a+b",		"(+ (- a) b)",
			"-a.b",		"(- (. a b))",
			"a^b^c",	"(^ a (^ b c))",
			"a=b=c",	"(= a (= b c))",
			"a=b=c+d.e","(= a (= b (+ c (. d e))))",
			"a|b&c",	"(| a (& b c))",
			"(a|b)&c",	"(& (| a b) c)",
			"a > b",	"(> a b)",
			"a >> b",	"(> a b)",  // text is from one token
			"a < b",	"(< a b)",

			"(T)x",							"(( T x)",
			"new A().b",					"(. (new A () b)",
			"(T)t.f()",						"(( (( T (. t f)))",
			"a.f(x)==T.c",					"(== (( (. a f) x) (. T c))",
			"a.f().g(x,1)",					"(( (. (( (. a f)) g) x 1)",
			"new T[((n-1) * x) + 1]",		"(new T [ (+ (* (- n 1) x) 1))",
		};
		runTests(grammar, tests, "e");
	}

	@Test public void testReturnValueAndActions() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s : e {System.out.println($e.v);} ;\n" +
			"e returns [int v, List<String> ignored]\n" +
			"  : e '*' b=e {$v *= $b.v;}\n" +
			"  | e '+' b=e {$v += $b.v;}\n" +
			"  | INT {$v = $INT.int;}\n" +
			"  ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";
		String[] tests = {
			"4",			"4",
			"1+2",			"3",
		};
		runTests(grammar, tests, "s");
	}

	@Test public void testReturnValueAndActionsAndASTs() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"s : e {System.out.print(\"v=\"+$e.v+\", \");} ;\n" +
			"e returns [int v, List<String> ignored]\n" +
			"  : e '*'^ b=e {$v *= $b.v;}\n" +
			"  | e '+'^ b=e {$v += $b.v;}\n" +
			"  | INT {$v = $INT.int;}\n" +
			"  ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"WS : (' '|'\\n') {skip();} ;\n";
		String[] tests = {
			"4",			"v=4, 4",
			"1+2",			"v=3, (+ 1 2)",
		};
		runTests(grammar, tests, "s");
	}

	public void runTests(String grammar, String[] tests, String startRule) {
		rawGenerateAndBuildRecognizer("T.g", grammar, "TParser", "TLexer", debug);
		boolean parserBuildsTrees =
			grammar.indexOf("output=AST")>=0 ||
			grammar.indexOf("output = AST")>=0;
		writeRecognizerAndCompile("TParser",
										 null,
										 "TLexer",
										 startRule,
										 null,
										 parserBuildsTrees,
										 false,
										 false,
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
