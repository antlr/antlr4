package org.antlr.v4.test.rt.js.explorer;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestLeftRecursion extends BaseTest {

	String testSimple(String input) throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : a ;\r\n" +
	                  "a : a ID\r\n" +
	                  "  | ID\r\n" +
	                  "  ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	@Test
	public void testSimple_1() throws Exception {
		String found = testSimple("x");
		assertEquals("(s (a x))\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testSimple_2() throws Exception {
		String found = testSimple("x y");
		assertEquals("(s (a (a x) y))\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testSimple_3() throws Exception {
		String found = testSimple("x y z");
		assertEquals("(s (a (a (a x) y) z))\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testDirectCallToLeftRecursiveRule(String input) throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : a ID\r\n" +
	                  "  | ID\r\n" +
	                  "  ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", input, false);
	}

	@Test
	public void testDirectCallToLeftRecursiveRule_1() throws Exception {
		String found = testDirectCallToLeftRecursiveRule("x");
		assertEquals("(a x)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testDirectCallToLeftRecursiveRule_2() throws Exception {
		String found = testDirectCallToLeftRecursiveRule("x y");
		assertEquals("(a (a x) y)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testDirectCallToLeftRecursiveRule_3() throws Exception {
		String found = testDirectCallToLeftRecursiveRule("x y z");
		assertEquals("(a (a (a x) y) z)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testSemPred() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : a ;\r\n" +
	                  "a : a {true}? ID\r\n" +
	                  "  | ID\r\n" +
	                  "  ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x y z", false);
		assertEquals("(s (a (a (a x) y) z))\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testTernaryExpr(String input) throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : e EOF ; // must indicate EOF can follow or 'a<EOF>' won't match\r\n" +
	                  "e : e '*' e\r\n" +
	                  "  | e '+' e\r\n" +
	                  "  |<assoc=right> e '?' e ':' e\r\n" +
	                  "  |<assoc=right> e '=' e\r\n" +
	                  "  | ID\r\n" +
	                  "  ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	@Test
	public void testTernaryExpr_1() throws Exception {
		String found = testTernaryExpr("a");
		assertEquals("(s (e a) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testTernaryExpr_2() throws Exception {
		String found = testTernaryExpr("a+b");
		assertEquals("(s (e (e a) + (e b)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testTernaryExpr_3() throws Exception {
		String found = testTernaryExpr("a*b");
		assertEquals("(s (e (e a) * (e b)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testTernaryExpr_4() throws Exception {
		String found = testTernaryExpr("a?b:c");
		assertEquals("(s (e (e a) ? (e b) : (e c)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testTernaryExpr_5() throws Exception {
		String found = testTernaryExpr("a=b=c");
		assertEquals("(s (e (e a) = (e (e b) = (e c))) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testTernaryExpr_6() throws Exception {
		String found = testTernaryExpr("a?b+c:d");
		assertEquals("(s (e (e a) ? (e (e b) + (e c)) : (e d)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testTernaryExpr_7() throws Exception {
		String found = testTernaryExpr("a?b=c:d");
		assertEquals("(s (e (e a) ? (e (e b) = (e c)) : (e d)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testTernaryExpr_8() throws Exception {
		String found = testTernaryExpr("a? b?c:d : e");
		assertEquals("(s (e (e a) ? (e (e b) ? (e c) : (e d)) : (e e)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testTernaryExpr_9() throws Exception {
		String found = testTernaryExpr("a?b: c?d:e");
		assertEquals("(s (e (e a) ? (e b) : (e (e c) ? (e d) : (e e))) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testExpressions(String input) throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : e EOF ; // must indicate EOF can follow\r\n" +
	                  "e : e '.' ID\r\n" +
	                  "  | e '.' 'this'\r\n" +
	                  "  | '-' e\r\n" +
	                  "  | e '*' e\r\n" +
	                  "  | e ('+'|'-') e\r\n" +
	                  "  | INT\r\n" +
	                  "  | ID\r\n" +
	                  "  ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+ ;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	@Test
	public void testExpressions_1() throws Exception {
		String found = testExpressions("a");
		assertEquals("(s (e a) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testExpressions_2() throws Exception {
		String found = testExpressions("1");
		assertEquals("(s (e 1) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testExpressions_3() throws Exception {
		String found = testExpressions("a-1");
		assertEquals("(s (e (e a) - (e 1)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testExpressions_4() throws Exception {
		String found = testExpressions("a.b");
		assertEquals("(s (e (e a) . b) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testExpressions_5() throws Exception {
		String found = testExpressions("a.this");
		assertEquals("(s (e (e a) . this) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testExpressions_6() throws Exception {
		String found = testExpressions("-a");
		assertEquals("(s (e - (e a)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testExpressions_7() throws Exception {
		String found = testExpressions("-a+b");
		assertEquals("(s (e (e - (e a)) + (e b)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testJavaExpressions(String input) throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : e EOF ; // must indicate EOF can follow\r\n" +
	                  "expressionList\r\n" +
	                  "    :   e (',' e)*\r\n" +
	                  "    ;\r\n" +
	                  "e   :   '(' e ')'\r\n" +
	                  "    |   'this' \r\n" +
	                  "    |   'super'\r\n" +
	                  "    |   INT\r\n" +
	                  "    |   ID\r\n" +
	                  "    |   type_ '.' 'class'\r\n" +
	                  "    |   e '.' ID\r\n" +
	                  "    |   e '.' 'this'\r\n" +
	                  "    |   e '.' 'super' '(' expressionList? ')'\r\n" +
	                  "    |   e '.' 'new' ID '(' expressionList? ')'\r\n" +
	                  "	 |	 'new' type_ ( '(' expressionList? ')' | ('[' e ']')+)\r\n" +
	                  "    |   e '[' e ']'\r\n" +
	                  "    |   '(' type_ ')' e\r\n" +
	                  "    |   e ('++' | '--')\r\n" +
	                  "    |   e '(' expressionList? ')'\r\n" +
	                  "    |   ('+'|'-'|'++'|'--') e\r\n" +
	                  "    |   ('~'|'!') e\r\n" +
	                  "    |   e ('*'|'/'|'%') e\r\n" +
	                  "    |   e ('+'|'-') e\r\n" +
	                  "    |   e ('<<' | '>>>' | '>>') e\r\n" +
	                  "    |   e ('<=' | '>=' | '>' | '<') e\r\n" +
	                  "    |   e 'instanceof' e\r\n" +
	                  "    |   e ('==' | '!=') e\r\n" +
	                  "    |   e '&' e\r\n" +
	                  "    |<assoc=right> e '^' e\r\n" +
	                  "    |   e '|' e\r\n" +
	                  "    |   e '&&' e\r\n" +
	                  "    |   e '||' e\r\n" +
	                  "    |   e '?' e ':' e\r\n" +
	                  "    |<assoc=right>\r\n" +
	                  "        e ('='\r\n" +
	                  "          |'+='\r\n" +
	                  "          |'-='\r\n" +
	                  "          |'*='\r\n" +
	                  "          |'/='\r\n" +
	                  "          |'&='\r\n" +
	                  "          |'|='\r\n" +
	                  "          |'^='\r\n" +
	                  "          |'>>='\r\n" +
	                  "          |'>>>='\r\n" +
	                  "          |'<<='\r\n" +
	                  "          |'%=') e\r\n" +
	                  "    ;\r\n" +
	                  "type_: ID \r\n" +
	                  "    | ID '[' ']'\r\n" +
	                  "    | 'int'\r\n" +
	                  "	 | 'int' '[' ']' \r\n" +
	                  "    ;\r\n" +
	                  "ID : ('a'..'z'|'A'..'Z'|'_'|'$')+;\r\n" +
	                  "INT : '0'..'9'+ ;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	@Test
	public void testJavaExpressions_1() throws Exception {
		String found = testJavaExpressions("a|b&c");
		assertEquals("(s (e (e a) | (e (e b) & (e c))) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testJavaExpressions_2() throws Exception {
		String found = testJavaExpressions("(a|b)&c");
		assertEquals("(s (e (e ( (e (e a) | (e b)) )) & (e c)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testJavaExpressions_3() throws Exception {
		String found = testJavaExpressions("a > b");
		assertEquals("(s (e (e a) > (e b)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testJavaExpressions_4() throws Exception {
		String found = testJavaExpressions("a >> b");
		assertEquals("(s (e (e a) >> (e b)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testJavaExpressions_5() throws Exception {
		String found = testJavaExpressions("a=b=c");
		assertEquals("(s (e (e a) = (e (e b) = (e c))) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testJavaExpressions_6() throws Exception {
		String found = testJavaExpressions("a^b^c");
		assertEquals("(s (e (e a) ^ (e (e b) ^ (e c))) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testJavaExpressions_7() throws Exception {
		String found = testJavaExpressions("(T)x");
		assertEquals("(s (e ( (type_ T) ) (e x)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testJavaExpressions_8() throws Exception {
		String found = testJavaExpressions("new A().b");
		assertEquals("(s (e (e new (type_ A) ( )) . b) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testJavaExpressions_9() throws Exception {
		String found = testJavaExpressions("(T)t.f()");
		assertEquals("(s (e (e ( (type_ T) ) (e (e t) . f)) ( )) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testJavaExpressions_10() throws Exception {
		String found = testJavaExpressions("a.f(x)==T.c");
		assertEquals("(s (e (e (e (e a) . f) ( (expressionList (e x)) )) == (e (e T) . c)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testJavaExpressions_11() throws Exception {
		String found = testJavaExpressions("a.f().g(x,1)");
		assertEquals("(s (e (e (e (e (e a) . f) ( )) . g) ( (expressionList (e x) , (e 1)) )) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testJavaExpressions_12() throws Exception {
		String found = testJavaExpressions("new T[((n-1) * x) + 1]");
		assertEquals("(s (e new (type_ T) [ (e (e ( (e (e ( (e (e n) - (e 1)) )) * (e x)) )) + (e 1)) ]) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testDeclarations(String input) throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : declarator EOF ; // must indicate EOF can follow\r\n" +
	                  "declarator\r\n" +
	                  "        : declarator '[' e ']'\r\n" +
	                  "        | declarator '[' ']'\r\n" +
	                  "        | declarator '(' ')'\r\n" +
	                  "        | '*' declarator // binds less tight than suffixes\r\n" +
	                  "        | '(' declarator ')'\r\n" +
	                  "        | ID\r\n" +
	                  "        ;\r\n" +
	                  "e : INT ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+ ;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	@Test
	public void testDeclarations_1() throws Exception {
		String found = testDeclarations("a");
		assertEquals("(s (declarator a) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testDeclarations_2() throws Exception {
		String found = testDeclarations("*a");
		assertEquals("(s (declarator * (declarator a)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testDeclarations_3() throws Exception {
		String found = testDeclarations("**a");
		assertEquals("(s (declarator * (declarator * (declarator a))) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testDeclarations_4() throws Exception {
		String found = testDeclarations("a[3]");
		assertEquals("(s (declarator (declarator a) [ (e 3) ]) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testDeclarations_5() throws Exception {
		String found = testDeclarations("b[]");
		assertEquals("(s (declarator (declarator b) [ ]) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testDeclarations_6() throws Exception {
		String found = testDeclarations("(a)");
		assertEquals("(s (declarator ( (declarator a) )) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testDeclarations_7() throws Exception {
		String found = testDeclarations("a[]()");
		assertEquals("(s (declarator (declarator (declarator a) [ ]) ( )) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testDeclarations_8() throws Exception {
		String found = testDeclarations("a[][]");
		assertEquals("(s (declarator (declarator (declarator a) [ ]) [ ]) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testDeclarations_9() throws Exception {
		String found = testDeclarations("*a[]");
		assertEquals("(s (declarator * (declarator (declarator a) [ ])) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testDeclarations_10() throws Exception {
		String found = testDeclarations("(*a)[]");
		assertEquals("(s (declarator (declarator ( (declarator * (declarator a)) )) [ ]) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testReturnValueAndActions(String input) throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s : e {document.getElementById('output').value += $e.v + '\\n';}; \r\n" +
	                  "e returns [int v, list ignored]\r\n" +
	                  "  : a=e '*' b=e {$v = $a.v * $b.v;}\r\n" +
	                  "  | a=e '+' b=e {$v = $a.v + $b.v;}\r\n" +
	                  "  | INT {$v = $INT.int;}\r\n" +
	                  "  | '(' x=e ')' {$v = $x.v;}\r\n" +
	                  "  ;\r\n" +
	                  "INT : '0'..'9'+ ;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r\n" +
	                  "\r";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	@Test
	public void testReturnValueAndActions_1() throws Exception {
		String found = testReturnValueAndActions("4");
		assertEquals("4\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testReturnValueAndActions_2() throws Exception {
		String found = testReturnValueAndActions("1+2");
		assertEquals("3\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testReturnValueAndActions_3() throws Exception {
		String found = testReturnValueAndActions("1+2*3");
		assertEquals("7\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testReturnValueAndActions_4() throws Exception {
		String found = testReturnValueAndActions("(1+2)*3");
		assertEquals("9\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testLabelsOnOpSubrule(String input) throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : e;\r\n" +
	                  "e : a=e op=('*'|'/') b=e  {}\r\n" +
	                  "  | INT {}\r\n" +
	                  "  | '(' x=e ')' {}\r\n" +
	                  "  ;\r\n" +
	                  "INT : '0'..'9'+ ;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	@Test
	public void testLabelsOnOpSubrule_1() throws Exception {
		String found = testLabelsOnOpSubrule("4");
		assertEquals("(s (e 4))\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testLabelsOnOpSubrule_2() throws Exception {
		String found = testLabelsOnOpSubrule("1*2/3");
		assertEquals("(s (e (e (e 1) * (e 2)) / (e 3)))\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testLabelsOnOpSubrule_3() throws Exception {
		String found = testLabelsOnOpSubrule("(1/2)*3");
		assertEquals("(s (e (e ( (e (e 1) / (e 2)) )) * (e 3)))\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testReturnValueAndActionsAndLabels(String input) throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s : q=e {document.getElementById('output').value += $e.v + '\\n';}; \r\n" +
	                  "e returns [int v]\r\n" +
	                  "  : a=e op='*' b=e {$v = $a.v * $b.v;}  # mult\r\n" +
	                  "  | a=e '+' b=e {$v = $a.v + $b.v;}     # add\r\n" +
	                  "  | INT         {$v = $INT.int;}        # anInt\r\n" +
	                  "  | '(' x=e ')' {$v = $x.v;}            # parens\r\n" +
	                  "  | x=e '++'    {$v = $x.v+1;}          # inc\r\n" +
	                  "  | e '--'                              # dec\r\n" +
	                  "  | ID          {$v = 3;}               # anID\r\n" +
	                  "  ; \r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+ ;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	@Test
	public void testReturnValueAndActionsAndLabels_1() throws Exception {
		String found = testReturnValueAndActionsAndLabels("4");
		assertEquals("4\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testReturnValueAndActionsAndLabels_2() throws Exception {
		String found = testReturnValueAndActionsAndLabels("1+2");
		assertEquals("3\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testReturnValueAndActionsAndLabels_3() throws Exception {
		String found = testReturnValueAndActionsAndLabels("1+2*3");
		assertEquals("7\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testReturnValueAndActionsAndLabels_4() throws Exception {
		String found = testReturnValueAndActionsAndLabels("i++*3");
		assertEquals("12\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testMultipleAlternativesWithCommonLabel(String input) throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s : e {document.getElementById('output').value += $e.v + '\\n';}; \r\n" +
	                  "e returns [int v]\r\n" +
	                  "  : e '*' e     {$v = $ctx.e(0).v * $ctx.e(1).v;}  # binary\r\n" +
	                  "  | e '+' e     {$v = $ctx.e(0).v + $ctx.e(1).v;}  # binary\r\n" +
	                  "  | INT         {$v = $INT.int;}                   # anInt\r\n" +
	                  "  | '(' e ')'   {$v = $e.v;}                       # parens\r\n" +
	                  "  | left=e INC  {$v = $left.v + 1;}      # unary\r\n" +
	                  "  | left=e DEC  {$v = $left.v - 1;}      # unary\r\n" +
	                  "  | ID          {$v = 3;}                                                     # anID\r\n" +
	                  "  ; \r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+ ;\r\n" +
	                  "INC : '++' ;\r\n" +
	                  "DEC : '--' ;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	@Test
	public void testMultipleAlternativesWithCommonLabel_1() throws Exception {
		String found = testMultipleAlternativesWithCommonLabel("4");
		assertEquals("4\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testMultipleAlternativesWithCommonLabel_2() throws Exception {
		String found = testMultipleAlternativesWithCommonLabel("1+2");
		assertEquals("3\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testMultipleAlternativesWithCommonLabel_3() throws Exception {
		String found = testMultipleAlternativesWithCommonLabel("1+2*3");
		assertEquals("7\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testMultipleAlternativesWithCommonLabel_4() throws Exception {
		String found = testMultipleAlternativesWithCommonLabel("i++*3");
		assertEquals("12\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testPrefixOpWithActionAndLabel(String input) throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s : e {document.getElementById('output').value += $e.result + '\\n';} ;\r\n" +
	                  "e returns [String result]\r\n" +
	                  "    :   ID '=' e1=e    {$result = \"(\" + $ID.text + \"=\" + $e1.result + \")\";}\r\n" +
	                  "    |   ID             {$result = $ID.text;}\r\n" +
	                  "    |   e1=e '+' e2=e  {$result = \"(\" + $e1.result + \"+\" + $e2.result + \")\";}\r\n" +
	                  "    ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+ ;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r\n" +
	                  "\r";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	@Test
	public void testPrefixOpWithActionAndLabel_1() throws Exception {
		String found = testPrefixOpWithActionAndLabel("a");
		assertEquals("a\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testPrefixOpWithActionAndLabel_2() throws Exception {
		String found = testPrefixOpWithActionAndLabel("a+b");
		assertEquals("(a+b)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testPrefixOpWithActionAndLabel_3() throws Exception {
		String found = testPrefixOpWithActionAndLabel("a=b+c");
		assertEquals("((a=b)+c)\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testAmbigLR(String input) throws Exception {
		String grammar = "grammar Expr;\r\n" +
	                  "prog:   stat ;\r\n" +
	                  "stat:   expr NEWLINE                # printExpr\r\n" +
	                  "    |   ID '=' expr NEWLINE         # assign\r\n" +
	                  "    |   NEWLINE                     # blank\r\n" +
	                  "    ;\r\n" +
	                  "expr:   expr ('*'|'/') expr      # MulDiv\r\n" +
	                  "    |   expr ('+'|'-') expr      # AddSub\r\n" +
	                  "    |   INT                      # int\r\n" +
	                  "    |   ID                       # id\r\n" +
	                  "    |   '(' expr ')'             # parens\r\n" +
	                  "    ;\r\n" +
	                  "\r\n" +
	                  "MUL :   '*' ; // assigns token name to '*' used above in grammar\r\n" +
	                  "DIV :   '/' ;\r\n" +
	                  "ADD :   '+' ;\r\n" +
	                  "SUB :   '-' ;\r\n" +
	                  "ID  :   [a-zA-Z]+ ;      // match identifiers\r\n" +
	                  "INT :   [0-9]+ ;         // match integers\r\n" +
	                  "NEWLINE:'\\r'? '\\n' ;     // return newlines to parser (is end-statement signal)\r\n" +
	                  "WS  :   [ \\t]+ -> skip ; // toss out whitespace\r";
		return execParser("Expr.g4", grammar, "ExprParser", "ExprLexer", "ExprListener", "ExprVisitor", "prog", input, false);
	}

	@Test
	public void testAmbigLR_1() throws Exception {
		String found = testAmbigLR("1\n");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testAmbigLR_2() throws Exception {
		String found = testAmbigLR("a = 5\n");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testAmbigLR_3() throws Exception {
		String found = testAmbigLR("b = 6\n");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testAmbigLR_4() throws Exception {
		String found = testAmbigLR("a+b*2\n");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testAmbigLR_5() throws Exception {
		String found = testAmbigLR("(1+2)*3\n");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	String testWhitespaceInfluence(String input) throws Exception {
		String grammar = "grammar Expr;\r\n" +
	                  "prog : expression EOF;\r\n" +
	                  "expression\r\n" +
	                  "    : ID '(' expression (',' expression)* ')'               # doFunction\r\n" +
	                  "    | '(' expression ')'                                    # doParenthesis\r\n" +
	                  "    | '!' expression                                        # doNot\r\n" +
	                  "    | '-' expression                                        # doNegate\r\n" +
	                  "    | '+' expression                                        # doPositiv\r\n" +
	                  "    | expression '^' expression                             # doPower\r\n" +
	                  "    | expression '*' expression                             # doMultipy\r\n" +
	                  "    | expression '/' expression                             # doDivide\r\n" +
	                  "    | expression '%' expression                             # doModulo\r\n" +
	                  "    | expression '-' expression                             # doMinus\r\n" +
	                  "    | expression '+' expression                             # doPlus\r\n" +
	                  "    | expression '=' expression                             # doEqual\r\n" +
	                  "    | expression '!=' expression                            # doNotEqual\r\n" +
	                  "    | expression '>' expression                             # doGreather\r\n" +
	                  "    | expression '>=' expression                            # doGreatherEqual\r\n" +
	                  "    | expression '<' expression                             # doLesser\r\n" +
	                  "    | expression '<=' expression                            # doLesserEqual\r\n" +
	                  "    | expression K_IN '(' expression (',' expression)* ')'  # doIn\r\n" +
	                  "    | expression ( '&' | K_AND) expression                  # doAnd\r\n" +
	                  "    | expression ( '|' | K_OR) expression                   # doOr\r\n" +
	                  "    | '[' expression (',' expression)* ']'                  # newArray\r\n" +
	                  "    | K_TRUE                                                # newTrueBoolean\r\n" +
	                  "    | K_FALSE                                               # newFalseBoolean\r\n" +
	                  "    | NUMBER                                                # newNumber\r\n" +
	                  "    | DATE                                                  # newDateTime\r\n" +
	                  "    | ID                                                    # newIdentifier\r\n" +
	                  "    | SQ_STRING                                             # newString\r\n" +
	                  "    | K_NULL                                                # newNull\r\n" +
	                  "    ;\r\n" +
	                  "\r\n" +
	                  "// Fragments\r\n" +
	                  "fragment DIGIT    : '0' .. '9';  \r\n" +
	                  "fragment UPPER    : 'A' .. 'Z';\r\n" +
	                  "fragment LOWER    : 'a' .. 'z';\r\n" +
	                  "fragment LETTER   : LOWER | UPPER;\r\n" +
	                  "fragment WORD     : LETTER | '_' | '$' | '#' | '.';\r\n" +
	                  "fragment ALPHANUM : WORD | DIGIT;  \r\n" +
	                  "\r\n" +
	                  "// Tokens\r\n" +
	                  "ID              : LETTER ALPHANUM*;\r\n" +
	                  "NUMBER          : DIGIT+ ('.' DIGIT+)? (('e'|'E')('+'|'-')? DIGIT+)?;\r\n" +
	                  "DATE            : '\\'' DIGIT DIGIT DIGIT DIGIT '-' DIGIT DIGIT '-' DIGIT DIGIT (' ' DIGIT DIGIT ':' DIGIT DIGIT ':' DIGIT DIGIT ('.' DIGIT+)?)? '\\'';\r\n" +
	                  "SQ_STRING       : '\\'' ('\\'\\'' | ~'\\'')* '\\'';\r\n" +
	                  "DQ_STRING       : '\\\"' ('\\\\\"' | ~'\\\"')* '\\\"';\r\n" +
	                  "WS              : [ \\t\\n\\r]+ -> skip ;\r\n" +
	                  "COMMENTS        : ('/*' .*? '*/' | '//' ~'\\n'* '\\n' ) -> skip;\r";
		return execParser("Expr.g4", grammar, "ExprParser", "ExprLexer", "ExprListener", "ExprVisitor", "prog", input, false);
	}

	@Test
	public void testWhitespaceInfluence_1() throws Exception {
		String found = testWhitespaceInfluence("Test(1,3)");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testWhitespaceInfluence_2() throws Exception {
		String found = testWhitespaceInfluence("Test(1, 3)");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testPrecedenceFilterConsidersContext() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "prog \r\n" +
	                  "@after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';}\r\n" +
	                  ": statement* EOF {};\r\n" +
	                  "statement: letterA | statement letterA 'b' ;\r\n" +
	                  "letterA: 'a';\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "prog", "aa", false);
		assertEquals("(prog (statement (letterA a)) (statement (letterA a)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testMultipleActions(String input) throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : e ;\r\n" +
	                  "e : a=e op=('*'|'/') b=e  {}{}\r\n" +
	                  "  | INT {}{}\r\n" +
	                  "  | '(' x=e ')' {}{}\r\n" +
	                  "  ;\r\n" +
	                  "INT : '0'..'9'+ ;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	@Test
	public void testMultipleActions_1() throws Exception {
		String found = testMultipleActions("4");
		assertEquals("(s (e 4))\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testMultipleActions_2() throws Exception {
		String found = testMultipleActions("1*2/3");
		assertEquals("(s (e (e (e 1) * (e 2)) / (e 3)))\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testMultipleActions_3() throws Exception {
		String found = testMultipleActions("(1/2)*3");
		assertEquals("(s (e (e ( (e (e 1) / (e 2)) )) * (e 3)))\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testMultipleActionsPredicatesOptions(String input) throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : e ;\r\n" +
	                  "e : a=e op=('*'|'/') b=e  {}{true}?\r\n" +
	                  "  | a=e op=('+'|'-') b=e  {}<p=3>{true}?<fail='Message'>\r\n" +
	                  "  | INT {}{}\r\n" +
	                  "  | '(' x=e ')' {}{}\r\n" +
	                  "  ;\r\n" +
	                  "INT : '0'..'9'+ ;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	@Test
	public void testMultipleActionsPredicatesOptions_1() throws Exception {
		String found = testMultipleActionsPredicatesOptions("4");
		assertEquals("(s (e 4))\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testMultipleActionsPredicatesOptions_2() throws Exception {
		String found = testMultipleActionsPredicatesOptions("1*2/3");
		assertEquals("(s (e (e (e 1) * (e 2)) / (e 3)))\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testMultipleActionsPredicatesOptions_3() throws Exception {
		String found = testMultipleActionsPredicatesOptions("(1/2)*3");
		assertEquals("(s (e (e ( (e (e 1) / (e 2)) )) * (e 3)))\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testSemPredFailOption() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : a ;\r\n" +
	                  "a : a ID {false}?<fail='custom message'>\r\n" +
	                  "  | ID\r\n" +
	                  "  ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x y z", false);
		assertEquals("(s (a (a x) y z))\n", found);
		assertEquals("line 1:4 rule a custom message\n", this.stderrDuringParse);
	}

	String testTernaryExprExplicitAssociativity(String input) throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : e EOF; // must indicate EOF can follow or 'a<EOF>' won't match\r\n" +
	                  "e :<assoc=right> e '*' e\r\n" +
	                  "  |<assoc=right> e '+' e\r\n" +
	                  "  |<assoc=right> e '?' e ':' e\r\n" +
	                  "  |<assoc=right> e '=' e\r\n" +
	                  "  | ID\r\n" +
	                  "  ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	@Test
	public void testTernaryExprExplicitAssociativity_1() throws Exception {
		String found = testTernaryExprExplicitAssociativity("a");
		assertEquals("(s (e a) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testTernaryExprExplicitAssociativity_2() throws Exception {
		String found = testTernaryExprExplicitAssociativity("a+b");
		assertEquals("(s (e (e a) + (e b)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testTernaryExprExplicitAssociativity_3() throws Exception {
		String found = testTernaryExprExplicitAssociativity("a*b");
		assertEquals("(s (e (e a) * (e b)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testTernaryExprExplicitAssociativity_4() throws Exception {
		String found = testTernaryExprExplicitAssociativity("a?b:c");
		assertEquals("(s (e (e a) ? (e b) : (e c)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testTernaryExprExplicitAssociativity_5() throws Exception {
		String found = testTernaryExprExplicitAssociativity("a=b=c");
		assertEquals("(s (e (e a) = (e (e b) = (e c))) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testTernaryExprExplicitAssociativity_6() throws Exception {
		String found = testTernaryExprExplicitAssociativity("a?b+c:d");
		assertEquals("(s (e (e a) ? (e (e b) + (e c)) : (e d)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testTernaryExprExplicitAssociativity_7() throws Exception {
		String found = testTernaryExprExplicitAssociativity("a?b=c:d");
		assertEquals("(s (e (e a) ? (e (e b) = (e c)) : (e d)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testTernaryExprExplicitAssociativity_8() throws Exception {
		String found = testTernaryExprExplicitAssociativity("a? b?c:d : e");
		assertEquals("(s (e (e a) ? (e (e b) ? (e c) : (e d)) : (e e)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testTernaryExprExplicitAssociativity_9() throws Exception {
		String found = testTernaryExprExplicitAssociativity("a?b: c?d:e");
		assertEquals("(s (e (e a) ? (e b) : (e (e c) ? (e d) : (e e))) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testReturnValueAndActionsList1(String input) throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : expr EOF;\r\n" +
	                  "expr:\r\n" +
	                  "    a=expr '*' a=expr #Factor\r\n" +
	                  "    | b+=expr (',' b+=expr)* '>>' c=expr #Send\r\n" +
	                  "    | ID #JustId //semantic check on modifiers\r\n" +
	                  ";\r\n" +
	                  "\r\n" +
	                  "ID  : ('a'..'z'|'A'..'Z'|'_')\r\n" +
	                  "      ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*\r\n" +
	                  ";\r\n" +
	                  "\r\n" +
	                  "WS : [ \\t\\n]+ -> skip ;\r";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	@Test
	public void testReturnValueAndActionsList1_1() throws Exception {
		String found = testReturnValueAndActionsList1("a*b");
		assertEquals("(s (expr (expr a) * (expr b)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testReturnValueAndActionsList1_2() throws Exception {
		String found = testReturnValueAndActionsList1("a,c>>x");
		assertEquals("(s (expr (expr a) , (expr c) >> (expr x)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testReturnValueAndActionsList1_3() throws Exception {
		String found = testReturnValueAndActionsList1("x");
		assertEquals("(s (expr x) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testReturnValueAndActionsList1_4() throws Exception {
		String found = testReturnValueAndActionsList1("a*b,c,x*y>>r");
		assertEquals("(s (expr (expr (expr a) * (expr b)) , (expr c) , (expr (expr x) * (expr y)) >> (expr r)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testReturnValueAndActionsList2(String input) throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s @after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';} : expr EOF;\r\n" +
	                  "expr:\r\n" +
	                  "    a=expr '*' a=expr #Factor\r\n" +
	                  "    | b+=expr ',' b+=expr #Comma\r\n" +
	                  "    | b+=expr '>>' c=expr #Send\r\n" +
	                  "    | ID #JustId //semantic check on modifiers\r\n" +
	                  "	;\r\n" +
	                  "ID  : ('a'..'z'|'A'..'Z'|'_')\r\n" +
	                  "      ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*\r\n" +
	                  ";\r\n" +
	                  "WS : [ \\t\\n]+ -> skip ;\r";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}

	@Test
	public void testReturnValueAndActionsList2_1() throws Exception {
		String found = testReturnValueAndActionsList2("a*b");
		assertEquals("(s (expr (expr a) * (expr b)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testReturnValueAndActionsList2_2() throws Exception {
		String found = testReturnValueAndActionsList2("a,c>>x");
		assertEquals("(s (expr (expr (expr a) , (expr c)) >> (expr x)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testReturnValueAndActionsList2_3() throws Exception {
		String found = testReturnValueAndActionsList2("x");
		assertEquals("(s (expr x) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testReturnValueAndActionsList2_4() throws Exception {
		String found = testReturnValueAndActionsList2("a*b,c,x*y>>r");
		assertEquals("(s (expr (expr (expr (expr (expr a) * (expr b)) , (expr c)) , (expr (expr x) * (expr y))) >> (expr r)) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}


}