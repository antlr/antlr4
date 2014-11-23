package org.antlr.v4.test.rt.js.safari;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestLeftRecursion extends BaseTest {

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testSimple(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "s @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : a ;\n" +
	                  "a : a ID\n" +
	                  "  | ID\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testSimple_1() throws Exception {
		String found = testSimple("x");
		assertEquals("(s (a x))\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testSimple_2() throws Exception {
		String found = testSimple("x y");
		assertEquals("(s (a (a x) y))\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testSimple_3() throws Exception {
		String found = testSimple("x y z");
		assertEquals("(s (a (a (a x) y) z))\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testDirectCallToLeftRecursiveRule(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "a @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : a ID\n" +
	                  "  | ID\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testDirectCallToLeftRecursiveRule_1() throws Exception {
		String found = testDirectCallToLeftRecursiveRule("x");
		assertEquals("(a x)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testDirectCallToLeftRecursiveRule_2() throws Exception {
		String found = testDirectCallToLeftRecursiveRule("x y");
		assertEquals("(a (a x) y)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testDirectCallToLeftRecursiveRule_3() throws Exception {
		String found = testDirectCallToLeftRecursiveRule("x y z");
		assertEquals("(a (a (a x) y) z)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testSemPred() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : a ;\n" +
	                  "a : a {true}? ID\n" +
	                  "  | ID\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x y z", false);
		assertEquals("(s (a (a (a x) y) z))\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testTernaryExpr(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "s @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : e EOF ; // must indicate EOF can follow or 'a<EOF>' won't match\n" +
	                  "e : e '*' e\n" +
	                  "  | e '+' e\n" +
	                  "  |<assoc=right> e '?' e ':' e\n" +
	                  "  |<assoc=right> e '=' e\n" +
	                  "  | ID\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testTernaryExpr_1() throws Exception {
		String found = testTernaryExpr("a");
		assertEquals("(s (e a) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testTernaryExpr_2() throws Exception {
		String found = testTernaryExpr("a+b");
		assertEquals("(s (e (e a) + (e b)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testTernaryExpr_3() throws Exception {
		String found = testTernaryExpr("a*b");
		assertEquals("(s (e (e a) * (e b)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testTernaryExpr_4() throws Exception {
		String found = testTernaryExpr("a?b:c");
		assertEquals("(s (e (e a) ? (e b) : (e c)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testTernaryExpr_5() throws Exception {
		String found = testTernaryExpr("a=b=c");
		assertEquals("(s (e (e a) = (e (e b) = (e c))) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testTernaryExpr_6() throws Exception {
		String found = testTernaryExpr("a?b+c:d");
		assertEquals("(s (e (e a) ? (e (e b) + (e c)) : (e d)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testTernaryExpr_7() throws Exception {
		String found = testTernaryExpr("a?b=c:d");
		assertEquals("(s (e (e a) ? (e (e b) = (e c)) : (e d)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testTernaryExpr_8() throws Exception {
		String found = testTernaryExpr("a? b?c:d : e");
		assertEquals("(s (e (e a) ? (e (e b) ? (e c) : (e d)) : (e e)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testTernaryExpr_9() throws Exception {
		String found = testTernaryExpr("a?b: c?d:e");
		assertEquals("(s (e (e a) ? (e b) : (e (e c) ? (e d) : (e e))) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testExpressions(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "s @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : e EOF ; // must indicate EOF can follow\n" +
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
	                  "WS : (' '|'\\n') -> skip ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testExpressions_1() throws Exception {
		String found = testExpressions("a");
		assertEquals("(s (e a) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testExpressions_2() throws Exception {
		String found = testExpressions("1");
		assertEquals("(s (e 1) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testExpressions_3() throws Exception {
		String found = testExpressions("a-1");
		assertEquals("(s (e (e a) - (e 1)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testExpressions_4() throws Exception {
		String found = testExpressions("a.b");
		assertEquals("(s (e (e a) . b) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testExpressions_5() throws Exception {
		String found = testExpressions("a.this");
		assertEquals("(s (e (e a) . this) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testExpressions_6() throws Exception {
		String found = testExpressions("-a");
		assertEquals("(s (e - (e a)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testExpressions_7() throws Exception {
		String found = testExpressions("-a+b");
		assertEquals("(s (e (e - (e a)) + (e b)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testJavaExpressions(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "s @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : e EOF ; // must indicate EOF can follow\n" +
	                  "expressionList\n" +
	                  "    :   e (',' e)*\n" +
	                  "    ;\n" +
	                  "e   :   '(' e ')'\n" +
	                  "    |   'this' \n" +
	                  "    |   'super'\n" +
	                  "    |   INT\n" +
	                  "    |   ID\n" +
	                  "    |   type_ '.' 'class'\n" +
	                  "    |   e '.' ID\n" +
	                  "    |   e '.' 'this'\n" +
	                  "    |   e '.' 'super' '(' expressionList? ')'\n" +
	                  "    |   e '.' 'new' ID '(' expressionList? ')'\n" +
	                  "	 |	 'new' type_ ( '(' expressionList? ')' | ('[' e ']')+)\n" +
	                  "    |   e '[' e ']'\n" +
	                  "    |   '(' type_ ')' e\n" +
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
	                  "    |<assoc=right> e '^' e\n" +
	                  "    |   e '|' e\n" +
	                  "    |   e '&&' e\n" +
	                  "    |   e '||' e\n" +
	                  "    |   e '?' e ':' e\n" +
	                  "    |<assoc=right>\n" +
	                  "        e ('='\n" +
	                  "          |'+='\n" +
	                  "          |'-='\n" +
	                  "          |'*='\n" +
	                  "          |'/='\n" +
	                  "          |'&='\n" +
	                  "          |'|='\n" +
	                  "          |'^='\n" +
	                  "          |'>>='\n" +
	                  "          |'>>>='\n" +
	                  "          |'<<='\n" +
	                  "          |'%=') e\n" +
	                  "    ;\n" +
	                  "type_: ID \n" +
	                  "    | ID '[' ']'\n" +
	                  "    | 'int'\n" +
	                  "	 | 'int' '[' ']' \n" +
	                  "    ;\n" +
	                  "ID : ('a'..'z'|'A'..'Z'|'_'|'$')+;\n" +
	                  "INT : '0'..'9'+ ;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testJavaExpressions_1() throws Exception {
		String found = testJavaExpressions("a|b&c");
		assertEquals("(s (e (e a) | (e (e b) & (e c))) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testJavaExpressions_2() throws Exception {
		String found = testJavaExpressions("(a|b)&c");
		assertEquals("(s (e (e ( (e (e a) | (e b)) )) & (e c)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testJavaExpressions_3() throws Exception {
		String found = testJavaExpressions("a > b");
		assertEquals("(s (e (e a) > (e b)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testJavaExpressions_4() throws Exception {
		String found = testJavaExpressions("a >> b");
		assertEquals("(s (e (e a) >> (e b)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testJavaExpressions_5() throws Exception {
		String found = testJavaExpressions("a=b=c");
		assertEquals("(s (e (e a) = (e (e b) = (e c))) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testJavaExpressions_6() throws Exception {
		String found = testJavaExpressions("a^b^c");
		assertEquals("(s (e (e a) ^ (e (e b) ^ (e c))) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testJavaExpressions_7() throws Exception {
		String found = testJavaExpressions("(T)x");
		assertEquals("(s (e ( (type_ T) ) (e x)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testJavaExpressions_8() throws Exception {
		String found = testJavaExpressions("new A().b");
		assertEquals("(s (e (e new (type_ A) ( )) . b) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testJavaExpressions_9() throws Exception {
		String found = testJavaExpressions("(T)t.f()");
		assertEquals("(s (e (e ( (type_ T) ) (e (e t) . f)) ( )) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testJavaExpressions_10() throws Exception {
		String found = testJavaExpressions("a.f(x)==T.c");
		assertEquals("(s (e (e (e (e a) . f) ( (expressionList (e x)) )) == (e (e T) . c)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testJavaExpressions_11() throws Exception {
		String found = testJavaExpressions("a.f().g(x,1)");
		assertEquals("(s (e (e (e (e (e a) . f) ( )) . g) ( (expressionList (e x) , (e 1)) )) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testJavaExpressions_12() throws Exception {
		String found = testJavaExpressions("new T[((n-1) * x) + 1]");
		assertEquals("(s (e new (type_ T) [ (e (e ( (e (e ( (e (e n) - (e 1)) )) * (e x)) )) + (e 1)) ]) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testDeclarations(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "s @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : declarator EOF ; // must indicate EOF can follow\n" +
	                  "declarator\n" +
	                  "        : declarator '[' e ']'\n" +
	                  "        | declarator '[' ']'\n" +
	                  "        | declarator '(' ')'\n" +
	                  "        | '*' declarator // binds less tight than suffixes\n" +
	                  "        | '(' declarator ')'\n" +
	                  "        | ID\n" +
	                  "        ;\n" +
	                  "e : INT ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+ ;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testDeclarations_1() throws Exception {
		String found = testDeclarations("a");
		assertEquals("(s (declarator a) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testDeclarations_2() throws Exception {
		String found = testDeclarations("*a");
		assertEquals("(s (declarator * (declarator a)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testDeclarations_3() throws Exception {
		String found = testDeclarations("**a");
		assertEquals("(s (declarator * (declarator * (declarator a))) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testDeclarations_4() throws Exception {
		String found = testDeclarations("a[3]");
		assertEquals("(s (declarator (declarator a) [ (e 3) ]) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testDeclarations_5() throws Exception {
		String found = testDeclarations("b[]");
		assertEquals("(s (declarator (declarator b) [ ]) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testDeclarations_6() throws Exception {
		String found = testDeclarations("(a)");
		assertEquals("(s (declarator ( (declarator a) )) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testDeclarations_7() throws Exception {
		String found = testDeclarations("a[]()");
		assertEquals("(s (declarator (declarator (declarator a) [ ]) ( )) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testDeclarations_8() throws Exception {
		String found = testDeclarations("a[][]");
		assertEquals("(s (declarator (declarator (declarator a) [ ]) [ ]) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testDeclarations_9() throws Exception {
		String found = testDeclarations("*a[]");
		assertEquals("(s (declarator * (declarator (declarator a) [ ])) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testDeclarations_10() throws Exception {
		String found = testDeclarations("(*a)[]");
		assertEquals("(s (declarator (declarator ( (declarator * (declarator a)) )) [ ]) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testReturnValueAndActions(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : e {document.getElementById('output').value += $e.v + '\\n';}; \n" +
	                  "e returns [int v, list ignored]\n" +
	                  "  : a=e '*' b=e {$v = $a.v * $b.v;}\n" +
	                  "  | a=e '+' b=e {$v = $a.v + $b.v;}\n" +
	                  "  | INT {$v = $INT.int;}\n" +
	                  "  | '(' x=e ')' {$v = $x.v;}\n" +
	                  "  ;\n" +
	                  "INT : '0'..'9'+ ;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testReturnValueAndActions_1() throws Exception {
		String found = testReturnValueAndActions("4");
		assertEquals("4\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testReturnValueAndActions_2() throws Exception {
		String found = testReturnValueAndActions("1+2");
		assertEquals("3\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testReturnValueAndActions_3() throws Exception {
		String found = testReturnValueAndActions("1+2*3");
		assertEquals("7\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testReturnValueAndActions_4() throws Exception {
		String found = testReturnValueAndActions("(1+2)*3");
		assertEquals("9\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testLabelsOnOpSubrule(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "s @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : e;\n" +
	                  "e : a=e op=('*'|'/') b=e  {}\n" +
	                  "  | INT {}\n" +
	                  "  | '(' x=e ')' {}\n" +
	                  "  ;\n" +
	                  "INT : '0'..'9'+ ;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testLabelsOnOpSubrule_1() throws Exception {
		String found = testLabelsOnOpSubrule("4");
		assertEquals("(s (e 4))\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testLabelsOnOpSubrule_2() throws Exception {
		String found = testLabelsOnOpSubrule("1*2/3");
		assertEquals("(s (e (e (e 1) * (e 2)) / (e 3)))\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testLabelsOnOpSubrule_3() throws Exception {
		String found = testLabelsOnOpSubrule("(1/2)*3");
		assertEquals("(s (e (e ( (e (e 1) / (e 2)) )) * (e 3)))\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testReturnValueAndActionsAndLabels(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : q=e {document.getElementById('output').value += $e.v + '\\n';}; \n" +
	                  "e returns [int v]\n" +
	                  "  : a=e op='*' b=e {$v = $a.v * $b.v;}  # mult\n" +
	                  "  | a=e '+' b=e {$v = $a.v + $b.v;}     # add\n" +
	                  "  | INT         {$v = $INT.int;}        # anInt\n" +
	                  "  | '(' x=e ')' {$v = $x.v;}            # parens\n" +
	                  "  | x=e '++'    {$v = $x.v+1;}          # inc\n" +
	                  "  | e '--'                              # dec\n" +
	                  "  | ID          {$v = 3;}               # anID\n" +
	                  "  ; \n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+ ;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testReturnValueAndActionsAndLabels_1() throws Exception {
		String found = testReturnValueAndActionsAndLabels("4");
		assertEquals("4\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testReturnValueAndActionsAndLabels_2() throws Exception {
		String found = testReturnValueAndActionsAndLabels("1+2");
		assertEquals("3\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testReturnValueAndActionsAndLabels_3() throws Exception {
		String found = testReturnValueAndActionsAndLabels("1+2*3");
		assertEquals("7\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testReturnValueAndActionsAndLabels_4() throws Exception {
		String found = testReturnValueAndActionsAndLabels("i++*3");
		assertEquals("12\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testMultipleAlternativesWithCommonLabel(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : e {document.getElementById('output').value += $e.v + '\\n';}; \n" +
	                  "e returns [int v]\n" +
	                  "  : e '*' e     {$v = $ctx.e(0).v * $ctx.e(1).v;}  # binary\n" +
	                  "  | e '+' e     {$v = $ctx.e(0).v + $ctx.e(1).v;}  # binary\n" +
	                  "  | INT         {$v = $INT.int;}                   # anInt\n" +
	                  "  | '(' e ')'   {$v = $e.v;}                       # parens\n" +
	                  "  | left=e INC  {$v = $left.v + 1;}      # unary\n" +
	                  "  | left=e DEC  {$v = $left.v - 1;}      # unary\n" +
	                  "  | ID          {$v = 3;}                                                     # anID\n" +
	                  "  ; \n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+ ;\n" +
	                  "INC : '++' ;\n" +
	                  "DEC : '--' ;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testMultipleAlternativesWithCommonLabel_1() throws Exception {
		String found = testMultipleAlternativesWithCommonLabel("4");
		assertEquals("4\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testMultipleAlternativesWithCommonLabel_2() throws Exception {
		String found = testMultipleAlternativesWithCommonLabel("1+2");
		assertEquals("3\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testMultipleAlternativesWithCommonLabel_3() throws Exception {
		String found = testMultipleAlternativesWithCommonLabel("1+2*3");
		assertEquals("7\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testMultipleAlternativesWithCommonLabel_4() throws Exception {
		String found = testMultipleAlternativesWithCommonLabel("i++*3");
		assertEquals("12\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testPrefixOpWithActionAndLabel(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : e {document.getElementById('output').value += $e.result + '\\n';} ;\n" +
	                  "e returns [String result]\n" +
	                  "    :   ID '=' e1=e    {$result = \"(\" + $ID.text + \"=\" + $e1.result + \")\";}\n" +
	                  "    |   ID             {$result = $ID.text;}\n" +
	                  "    |   e1=e '+' e2=e  {$result = \"(\" + $e1.result + \"+\" + $e2.result + \")\";}\n" +
	                  "    ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+ ;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testPrefixOpWithActionAndLabel_1() throws Exception {
		String found = testPrefixOpWithActionAndLabel("a");
		assertEquals("a\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testPrefixOpWithActionAndLabel_2() throws Exception {
		String found = testPrefixOpWithActionAndLabel("a+b");
		assertEquals("(a+b)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testPrefixOpWithActionAndLabel_3() throws Exception {
		String found = testPrefixOpWithActionAndLabel("a=b+c");
		assertEquals("((a=b)+c)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testAmbigLR(String input) throws Exception {
		String grammar = "grammar Expr;\n" +
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
	                  "WS  :   [ \\t]+ -> skip ; // toss out whitespace";
		return execParser("Expr.g4", grammar, "ExprParser", "ExprLexer", "ExprListener", "ExprVisitor", "prog", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testAmbigLR_1() throws Exception {
		String found = testAmbigLR("1\n");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testAmbigLR_2() throws Exception {
		String found = testAmbigLR("a = 5\n");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testAmbigLR_3() throws Exception {
		String found = testAmbigLR("b = 6\n");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testAmbigLR_4() throws Exception {
		String found = testAmbigLR("a+b*2\n");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testAmbigLR_5() throws Exception {
		String found = testAmbigLR("(1+2)*3\n");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testWhitespaceInfluence(String input) throws Exception {
		String grammar = "grammar Expr;\n" +
	                  "prog : expression EOF;\n" +
	                  "expression\n" +
	                  "    : ID '(' expression (',' expression)* ')'               # doFunction\n" +
	                  "    | '(' expression ')'                                    # doParenthesis\n" +
	                  "    | '!' expression                                        # doNot\n" +
	                  "    | '-' expression                                        # doNegate\n" +
	                  "    | '+' expression                                        # doPositiv\n" +
	                  "    | expression '^' expression                             # doPower\n" +
	                  "    | expression '*' expression                             # doMultipy\n" +
	                  "    | expression '/' expression                             # doDivide\n" +
	                  "    | expression '%' expression                             # doModulo\n" +
	                  "    | expression '-' expression                             # doMinus\n" +
	                  "    | expression '+' expression                             # doPlus\n" +
	                  "    | expression '=' expression                             # doEqual\n" +
	                  "    | expression '!=' expression                            # doNotEqual\n" +
	                  "    | expression '>' expression                             # doGreather\n" +
	                  "    | expression '>=' expression                            # doGreatherEqual\n" +
	                  "    | expression '<' expression                             # doLesser\n" +
	                  "    | expression '<=' expression                            # doLesserEqual\n" +
	                  "    | expression K_IN '(' expression (',' expression)* ')'  # doIn\n" +
	                  "    | expression ( '&' | K_AND) expression                  # doAnd\n" +
	                  "    | expression ( '|' | K_OR) expression                   # doOr\n" +
	                  "    | '[' expression (',' expression)* ']'                  # newArray\n" +
	                  "    | K_TRUE                                                # newTrueBoolean\n" +
	                  "    | K_FALSE                                               # newFalseBoolean\n" +
	                  "    | NUMBER                                                # newNumber\n" +
	                  "    | DATE                                                  # newDateTime\n" +
	                  "    | ID                                                    # newIdentifier\n" +
	                  "    | SQ_STRING                                             # newString\n" +
	                  "    | K_NULL                                                # newNull\n" +
	                  "    ;\n" +
	                  "\n" +
	                  "// Fragments\n" +
	                  "fragment DIGIT    : '0' .. '9';  \n" +
	                  "fragment UPPER    : 'A' .. 'Z';\n" +
	                  "fragment LOWER    : 'a' .. 'z';\n" +
	                  "fragment LETTER   : LOWER | UPPER;\n" +
	                  "fragment WORD     : LETTER | '_' | '$' | '#' | '.';\n" +
	                  "fragment ALPHANUM : WORD | DIGIT;  \n" +
	                  "\n" +
	                  "// Tokens\n" +
	                  "ID              : LETTER ALPHANUM*;\n" +
	                  "NUMBER          : DIGIT+ ('.' DIGIT+)? (('e'|'E')('+'|'-')? DIGIT+)?;\n" +
	                  "DATE            : '\\'' DIGIT DIGIT DIGIT DIGIT '-' DIGIT DIGIT '-' DIGIT DIGIT (' ' DIGIT DIGIT ':' DIGIT DIGIT ':' DIGIT DIGIT ('.' DIGIT+)?)? '\\'';\n" +
	                  "SQ_STRING       : '\\'' ('\\'\\'' | ~'\\'')* '\\'';\n" +
	                  "DQ_STRING       : '\\\"' ('\\\\\"' | ~'\\\"')* '\\\"';\n" +
	                  "WS              : [ \\t\\n\\r]+ -> skip ;\n" +
	                  "COMMENTS        : ('/*' .*? '*/' | '//' ~'\\n'* '\\n' ) -> skip;";
		return execParser("Expr.g4", grammar, "ExprParser", "ExprLexer", "ExprListener", "ExprVisitor", "prog", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testWhitespaceInfluence_1() throws Exception {
		String found = testWhitespaceInfluence("Test(1,3)");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testWhitespaceInfluence_2() throws Exception {
		String found = testWhitespaceInfluence("Test(1, 3)");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testPrecedenceFilterConsidersContext() throws Exception {
		String grammar = "grammar T;\n" +
	                  "prog \n" +
	                  "@after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';}\n" +
	                  ": statement* EOF {};\n" +
	                  "statement: letterA | statement letterA 'b' ;\n" +
	                  "letterA: 'a';";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "prog", "aa", false);
		assertEquals("(prog (statement (letterA a)) (statement (letterA a)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testMultipleActions(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "s @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : e ;\n" +
	                  "e : a=e op=('*'|'/') b=e  {}{}\n" +
	                  "  | INT {}{}\n" +
	                  "  | '(' x=e ')' {}{}\n" +
	                  "  ;\n" +
	                  "INT : '0'..'9'+ ;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testMultipleActions_1() throws Exception {
		String found = testMultipleActions("4");
		assertEquals("(s (e 4))\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testMultipleActions_2() throws Exception {
		String found = testMultipleActions("1*2/3");
		assertEquals("(s (e (e (e 1) * (e 2)) / (e 3)))\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testMultipleActions_3() throws Exception {
		String found = testMultipleActions("(1/2)*3");
		assertEquals("(s (e (e ( (e (e 1) / (e 2)) )) * (e 3)))\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testMultipleActionsPredicatesOptions(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "s @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : e ;\n" +
	                  "e : a=e op=('*'|'/') b=e  {}{true}?\n" +
	                  "  | a=e op=('+'|'-') b=e  {}<p=3>{true}?<fail='Message'>\n" +
	                  "  | INT {}{}\n" +
	                  "  | '(' x=e ')' {}{}\n" +
	                  "  ;\n" +
	                  "INT : '0'..'9'+ ;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testMultipleActionsPredicatesOptions_1() throws Exception {
		String found = testMultipleActionsPredicatesOptions("4");
		assertEquals("(s (e 4))\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testMultipleActionsPredicatesOptions_2() throws Exception {
		String found = testMultipleActionsPredicatesOptions("1*2/3");
		assertEquals("(s (e (e (e 1) * (e 2)) / (e 3)))\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testMultipleActionsPredicatesOptions_3() throws Exception {
		String found = testMultipleActionsPredicatesOptions("(1/2)*3");
		assertEquals("(s (e (e ( (e (e 1) / (e 2)) )) * (e 3)))\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testSemPredFailOption() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : a ;\n" +
	                  "a : a ID {false}?<fail='custom message'>\n" +
	                  "  | ID\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x y z", false);
		assertEquals("(s (a (a x) y z))\n", found);
		assertEquals("line 1:4 rule a custom message\n", this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testTernaryExprExplicitAssociativity(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "s @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : e EOF; // must indicate EOF can follow or 'a<EOF>' won't match\n" +
	                  "e :<assoc=right> e '*' e\n" +
	                  "  |<assoc=right> e '+' e\n" +
	                  "  |<assoc=right> e '?' e ':' e\n" +
	                  "  |<assoc=right> e '=' e\n" +
	                  "  | ID\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testTernaryExprExplicitAssociativity_1() throws Exception {
		String found = testTernaryExprExplicitAssociativity("a");
		assertEquals("(s (e a) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testTernaryExprExplicitAssociativity_2() throws Exception {
		String found = testTernaryExprExplicitAssociativity("a+b");
		assertEquals("(s (e (e a) + (e b)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testTernaryExprExplicitAssociativity_3() throws Exception {
		String found = testTernaryExprExplicitAssociativity("a*b");
		assertEquals("(s (e (e a) * (e b)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testTernaryExprExplicitAssociativity_4() throws Exception {
		String found = testTernaryExprExplicitAssociativity("a?b:c");
		assertEquals("(s (e (e a) ? (e b) : (e c)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testTernaryExprExplicitAssociativity_5() throws Exception {
		String found = testTernaryExprExplicitAssociativity("a=b=c");
		assertEquals("(s (e (e a) = (e (e b) = (e c))) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testTernaryExprExplicitAssociativity_6() throws Exception {
		String found = testTernaryExprExplicitAssociativity("a?b+c:d");
		assertEquals("(s (e (e a) ? (e (e b) + (e c)) : (e d)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testTernaryExprExplicitAssociativity_7() throws Exception {
		String found = testTernaryExprExplicitAssociativity("a?b=c:d");
		assertEquals("(s (e (e a) ? (e (e b) = (e c)) : (e d)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testTernaryExprExplicitAssociativity_8() throws Exception {
		String found = testTernaryExprExplicitAssociativity("a? b?c:d : e");
		assertEquals("(s (e (e a) ? (e (e b) ? (e c) : (e d)) : (e e)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testTernaryExprExplicitAssociativity_9() throws Exception {
		String found = testTernaryExprExplicitAssociativity("a?b: c?d:e");
		assertEquals("(s (e (e a) ? (e b) : (e (e c) ? (e d) : (e e))) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testReturnValueAndActionsList1(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "s @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : expr EOF;\n" +
	                  "expr:\n" +
	                  "    a=expr '*' a=expr #Factor\n" +
	                  "    | b+=expr (',' b+=expr)* '>>' c=expr #Send\n" +
	                  "    | ID #JustId //semantic check on modifiers\n" +
	                  ";\n" +
	                  "\n" +
	                  "ID  : ('a'..'z'|'A'..'Z'|'_')\n" +
	                  "      ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*\n" +
	                  ";\n" +
	                  "\n" +
	                  "WS : [ \\t\\n]+ -> skip ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testReturnValueAndActionsList1_1() throws Exception {
		String found = testReturnValueAndActionsList1("a*b");
		assertEquals("(s (expr (expr a) * (expr b)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testReturnValueAndActionsList1_2() throws Exception {
		String found = testReturnValueAndActionsList1("a,c>>x");
		assertEquals("(s (expr (expr a) , (expr c) >> (expr x)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testReturnValueAndActionsList1_3() throws Exception {
		String found = testReturnValueAndActionsList1("x");
		assertEquals("(s (expr x) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testReturnValueAndActionsList1_4() throws Exception {
		String found = testReturnValueAndActionsList1("a*b,c,x*y>>r");
		assertEquals("(s (expr (expr (expr a) * (expr b)) , (expr c) , (expr (expr x) * (expr y)) >> (expr r)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testReturnValueAndActionsList2(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "s @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : expr EOF;\n" +
	                  "expr:\n" +
	                  "    a=expr '*' a=expr #Factor\n" +
	                  "    | b+=expr ',' b+=expr #Comma\n" +
	                  "    | b+=expr '>>' c=expr #Send\n" +
	                  "    | ID #JustId //semantic check on modifiers\n" +
	                  "	;\n" +
	                  "ID  : ('a'..'z'|'A'..'Z'|'_')\n" +
	                  "      ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*\n" +
	                  ";\n" +
	                  "WS : [ \\t\\n]+ -> skip ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testReturnValueAndActionsList2_1() throws Exception {
		String found = testReturnValueAndActionsList2("a*b");
		assertEquals("(s (expr (expr a) * (expr b)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testReturnValueAndActionsList2_2() throws Exception {
		String found = testReturnValueAndActionsList2("a,c>>x");
		assertEquals("(s (expr (expr (expr a) , (expr c)) >> (expr x)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testReturnValueAndActionsList2_3() throws Exception {
		String found = testReturnValueAndActionsList2("x");
		assertEquals("(s (expr x) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testReturnValueAndActionsList2_4() throws Exception {
		String found = testReturnValueAndActionsList2("a*b,c,x*y>>r");
		assertEquals("(s (expr (expr (expr (expr (expr a) * (expr b)) , (expr c)) , (expr (expr x) * (expr y))) >> (expr r)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}


}