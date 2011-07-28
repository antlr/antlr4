package org.antlr.v4.test;

import org.antlr.runtime.RuleReturnScope;
import org.antlr.runtime.tree.Tree;
import org.junit.*;

import static org.junit.Assert.assertEquals;

public class TestASTStructure extends org.antlr.v4.gunit.gUnitBase {
	@Before public void setup() {
	    lexerClassName = "org.antlr.v4.parse.ANTLRLexer";
	    parserClassName = "org.antlr.v4.parse.ANTLRParser";
	    adaptorClassName = "org.antlr.v4.parse.GrammarASTAdaptor";	}
	@Test public void test_grammarSpec1() throws Exception {
		// gunit test on line 15
		RuleReturnScope rstruct = (RuleReturnScope)execParser("grammarSpec", "parser grammar P; a : A;", 15);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(PARSER_GRAMMAR P (RULES (RULE a (BLOCK (ALT A)))))";
		assertEquals("testing rule grammarSpec", expecting, actual);
	}

	@Test public void test_grammarSpec2() throws Exception {
		// gunit test on line 18
		RuleReturnScope rstruct = (RuleReturnScope)execParser("grammarSpec", "\n    parser grammar P;\n    options {k=2; output=AST;}\n    tokens { A; B='33'; }\n    @header {foo}\n    a : A;\n    ", 18);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(PARSER_GRAMMAR P (OPTIONS (= k 2) (= output AST)) (tokens { A (= B '33')) (@ header {foo}) (RULES (RULE a (BLOCK (ALT A)))))";
		assertEquals("testing rule grammarSpec", expecting, actual);
	}

	@Test public void test_grammarSpec3() throws Exception {
		// gunit test on line 32
		RuleReturnScope rstruct = (RuleReturnScope)execParser("grammarSpec", "\n    parser grammar P;\n    @header {foo}\n    tokens { A; B='33'; }\n    options {k=2; ASTLabel=a.b.c; output=AST;}\n    a : A;\n    ", 32);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(PARSER_GRAMMAR P (@ header {foo}) (tokens { A (= B '33')) (OPTIONS (= k 2) (= ASTLabel a.b.c) (= output AST)) (RULES (RULE a (BLOCK (ALT A)))))";
		assertEquals("testing rule grammarSpec", expecting, actual);
	}

	@Test public void test_grammarSpec4() throws Exception {
		// gunit test on line 46
		RuleReturnScope rstruct = (RuleReturnScope)execParser("grammarSpec", "\n    parser grammar P;\n    import A=B, C;\n    a : A;\n    ", 46);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(PARSER_GRAMMAR P (import (= A B) C) (RULES (RULE a (BLOCK (ALT A)))))";
		assertEquals("testing rule grammarSpec", expecting, actual);
	} @Test public void test_delegateGrammars1() throws Exception {
		// gunit test on line 57
		RuleReturnScope rstruct = (RuleReturnScope)execParser("delegateGrammars", "import A;", 57);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(import A)";
		assertEquals("testing rule delegateGrammars", expecting, actual);
	} @Test public void test_rule1() throws Exception {
		// gunit test on line 60
		RuleReturnScope rstruct = (RuleReturnScope)execParser("rule", "a : A<X,Y=a.b.c>;", 60);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(RULE a (BLOCK (ALT (A (ELEMENT_OPTIONS X (= Y a.b.c))))))";
		assertEquals("testing rule rule", expecting, actual);
	}

	@Test public void test_rule2() throws Exception {
		// gunit test on line 62
		RuleReturnScope rstruct = (RuleReturnScope)execParser("rule", "A : B+;", 62);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(RULE A (BLOCK (ALT (+ (BLOCK (ALT B))))))";
		assertEquals("testing rule rule", expecting, actual);
	}

	@Test public void test_rule3() throws Exception {
		// gunit test on line 64
		RuleReturnScope rstruct = (RuleReturnScope)execParser("rule", "\n    public a[int i] returns [int y]\n    options {backtrack=true;}\n    @init {blort}\n      : ID ;\n    ", 64);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(RULE a (RULEMODIFIERS public) int i (returns int y) (OPTIONS (= backtrack true)) (@ init {blort}) (BLOCK (ALT ID)))";
		assertEquals("testing rule rule", expecting, actual);
	}

	@Test public void test_rule4() throws Exception {
		// gunit test on line 79
		RuleReturnScope rstruct = (RuleReturnScope)execParser("rule", "\n    a[int i] returns [int y]\n    @init {blort}\n    options {backtrack=true;}\n      : ID;\n    ", 79);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(RULE a int i (returns int y) (@ init {blort}) (OPTIONS (= backtrack true)) (BLOCK (ALT ID)))";
		assertEquals("testing rule rule", expecting, actual);
	}

	@Test public void test_rule5() throws Exception {
		// gunit test on line 92
		RuleReturnScope rstruct = (RuleReturnScope)execParser("rule", "\n    a : ID ;\n      catch[A b] {foo}\n      finally {bar}\n    ", 92);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(RULE a (BLOCK (ALT ID)) (catch A b {foo}) (finally {bar}))";
		assertEquals("testing rule rule", expecting, actual);
	}

	@Test public void test_rule6() throws Exception {
		// gunit test on line 101
		RuleReturnScope rstruct = (RuleReturnScope)execParser("rule", "\n    a : ID ;\n      catch[A a] {foo}\n      catch[B b] {fu}\n      finally {bar}\n    ", 101);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(RULE a (BLOCK (ALT ID)) (catch A a {foo}) (catch B b {fu}) (finally {bar}))";
		assertEquals("testing rule rule", expecting, actual);
	}

	@Test public void test_rule7() throws Exception {
		// gunit test on line 111
		RuleReturnScope rstruct = (RuleReturnScope)execParser("rule", "\n\ta[int i]\n\tlocals [int a, float b]\n\t\t:\tA\n\t\t;\n\t", 111);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(RULE a int i (locals int a, float b) (BLOCK (ALT A)))";
		assertEquals("testing rule rule", expecting, actual);
	}

	@Test public void test_rule8() throws Exception {
		// gunit test on line 119
		RuleReturnScope rstruct = (RuleReturnScope)execParser("rule", "\n\ta[int i] throws a.b.c\n\t\t:\tA\n\t\t;\n\t", 119);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(RULE a int i (throws a.b.c) (BLOCK (ALT A)))";
		assertEquals("testing rule rule", expecting, actual);
	} @Test public void test_block1() throws Exception {
		// gunit test on line 127
		RuleReturnScope rstruct = (RuleReturnScope)execParser("block", "( ^(A B) | ^(b C) )", 127);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(BLOCK (ALT (^( A B)) (ALT (^( b C)))";
		assertEquals("testing rule block", expecting, actual);
	} @Test public void test_ebnf1() throws Exception {
		// gunit test on line 130
		RuleReturnScope rstruct = (RuleReturnScope)execParser("ebnf", "(A|B)", 130);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(BLOCK (ALT A) (ALT B))";
		assertEquals("testing rule ebnf", expecting, actual);
	}

	@Test public void test_ebnf2() throws Exception {
		// gunit test on line 131
		RuleReturnScope rstruct = (RuleReturnScope)execParser("ebnf", "(A|B)?", 131);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(? (BLOCK (ALT A) (ALT B)))";
		assertEquals("testing rule ebnf", expecting, actual);
	}

	@Test public void test_ebnf3() throws Exception {
		// gunit test on line 132
		RuleReturnScope rstruct = (RuleReturnScope)execParser("ebnf", "(A|B)*", 132);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(* (BLOCK (ALT A) (ALT B)))";
		assertEquals("testing rule ebnf", expecting, actual);
	}

	@Test public void test_ebnf4() throws Exception {
		// gunit test on line 133
		RuleReturnScope rstruct = (RuleReturnScope)execParser("ebnf", "(A|B)+", 133);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(+ (BLOCK (ALT A) (ALT B)))";
		assertEquals("testing rule ebnf", expecting, actual);
	} @Test public void test_alternative1() throws Exception {
		// gunit test on line 136
		RuleReturnScope rstruct = (RuleReturnScope)execParser("alternative", "x+=ID* -> $x*", 136);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(ALT_REWRITE (ALT (* (BLOCK (ALT (+= x ID))))) (-> (REWRITE_SEQ (* (REWRITE_BLOCK (REWRITE_SEQ x))))))";
		assertEquals("testing rule alternative", expecting, actual);
	}

	@Test public void test_alternative2() throws Exception {
		// gunit test on line 141
		RuleReturnScope rstruct = (RuleReturnScope)execParser("alternative", "A -> ...", 141);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(ALT_REWRITE (ALT A) (-> ...))";
		assertEquals("testing rule alternative", expecting, actual);
	}

	@Test public void test_alternative3() throws Exception {
		// gunit test on line 142
		RuleReturnScope rstruct = (RuleReturnScope)execParser("alternative", "A -> ", 142);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(ALT_REWRITE (ALT A) (-> EPSILON))";
		assertEquals("testing rule alternative", expecting, actual);
	}

	@Test public void test_alternative4() throws Exception {
		// gunit test on line 144
		RuleReturnScope rstruct = (RuleReturnScope)execParser("alternative", "A -> foo(a={x}, b={y})", 144);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(ALT_REWRITE (ALT A) (-> (TEMPLATE foo (ARGLIST (= a {x}) (= b {y})))))";
		assertEquals("testing rule alternative", expecting, actual);
	}

	@Test public void test_alternative5() throws Exception {
		// gunit test on line 149
		RuleReturnScope rstruct = (RuleReturnScope)execParser("alternative", "A -> template(a={x}, b={y}) <<ick>>", 149);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(ALT_REWRITE (ALT A) (-> (TEMPLATE (ARGLIST (= a {x}) (= b {y})) <<ick>>)))";
		assertEquals("testing rule alternative", expecting, actual);
	}

	@Test public void test_alternative6() throws Exception {
		// gunit test on line 154
		RuleReturnScope rstruct = (RuleReturnScope)execParser("alternative", "A -> ({name})()", 154);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(ALT_REWRITE (ALT A) (-> (TEMPLATE {name})))";
		assertEquals("testing rule alternative", expecting, actual);
	}

	@Test public void test_alternative7() throws Exception {
		// gunit test on line 156
		RuleReturnScope rstruct = (RuleReturnScope)execParser("alternative", "A -> {expr}", 156);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(ALT_REWRITE (ALT A) (-> (REWRITE_SEQ {expr})))";
		assertEquals("testing rule alternative", expecting, actual);
	}

	@Test public void test_alternative8() throws Exception {
		// gunit test on line 158
		RuleReturnScope rstruct = (RuleReturnScope)execParser("alternative", "\n    A -> {p1}? {e1}\n    -> {e2}\n    ->\n    ", 158);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(ALT_REWRITE (ALT A) (-> {p1}? (REWRITE_SEQ {e1})) (-> (REWRITE_SEQ {e2})))";
		assertEquals("testing rule alternative", expecting, actual);
	}

	@Test public void test_alternative9() throws Exception {
		// gunit test on line 170
		RuleReturnScope rstruct = (RuleReturnScope)execParser("alternative", "A -> A", 170);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(ALT_REWRITE (ALT A) (-> (REWRITE_SEQ A)))";
		assertEquals("testing rule alternative", expecting, actual);
	}

	@Test public void test_alternative10() throws Exception {
		// gunit test on line 172
		RuleReturnScope rstruct = (RuleReturnScope)execParser("alternative", "a -> a", 172);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(ALT_REWRITE (ALT a) (-> (REWRITE_SEQ a)))";
		assertEquals("testing rule alternative", expecting, actual);
	}

	@Test public void test_alternative11() throws Exception {
		// gunit test on line 174
		RuleReturnScope rstruct = (RuleReturnScope)execParser("alternative", "a A X? Y* -> A a ^(TOP X)? Y*", 174);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(ALT_REWRITE (ALT a A (? (BLOCK (ALT X))) (* (BLOCK (ALT Y)))) (-> (REWRITE_SEQ A a (? (REWRITE_BLOCK (REWRITE_SEQ (^( TOP X)))) (* (REWRITE_BLOCK (REWRITE_SEQ Y))))))";
		assertEquals("testing rule alternative", expecting, actual);
	}

	@Test public void test_alternative12() throws Exception {
		// gunit test on line 182
		RuleReturnScope rstruct = (RuleReturnScope)execParser("alternative", "A -> A[33]", 182);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(ALT_REWRITE (ALT A) (-> (REWRITE_SEQ (A 33))))";
		assertEquals("testing rule alternative", expecting, actual);
	}

	@Test public void test_alternative13() throws Exception {
		// gunit test on line 184
		RuleReturnScope rstruct = (RuleReturnScope)execParser("alternative", "A -> 'int' ^(A A)*", 184);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(ALT_REWRITE (ALT A) (-> (REWRITE_SEQ 'int' (* (REWRITE_BLOCK (REWRITE_SEQ (^( A A)))))))";
		assertEquals("testing rule alternative", expecting, actual);
	}

	@Test public void test_alternative14() throws Exception {
		// gunit test on line 189
		RuleReturnScope rstruct = (RuleReturnScope)execParser("alternative", "\n    A -> {p1}? A\n      -> {p2}? B\n      ->\n    ", 189);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(ALT_REWRITE (ALT A) (-> {p1}? (REWRITE_SEQ A)) (-> {p2}? (REWRITE_SEQ B)) (-> EPSILON))";
		assertEquals("testing rule alternative", expecting, actual);
	} @Test public void test_element1() throws Exception {
		// gunit test on line 201
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "~A", 201);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(~ (SET A))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element2() throws Exception {
		// gunit test on line 202
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "b+", 202);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(+ (BLOCK (ALT b)))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element3() throws Exception {
		// gunit test on line 203
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "(b)+", 203);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(+ (BLOCK (ALT b)))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element4() throws Exception {
		// gunit test on line 204
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "b?", 204);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(? (BLOCK (ALT b)))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element5() throws Exception {
		// gunit test on line 205
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "(b)?", 205);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(? (BLOCK (ALT b)))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element6() throws Exception {
		// gunit test on line 206
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "(b)*", 206);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(* (BLOCK (ALT b)))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element7() throws Exception {
		// gunit test on line 207
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "b*", 207);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(* (BLOCK (ALT b)))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element8() throws Exception {
		// gunit test on line 208
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "'while'*", 208);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(* (BLOCK (ALT 'while')))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element9() throws Exception {
		// gunit test on line 209
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "'a'+", 209);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(+ (BLOCK (ALT 'a')))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element10() throws Exception {
		// gunit test on line 210
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "a[3]", 210);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(a 3)";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element11() throws Exception {
		// gunit test on line 211
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "'a'..'z'+", 211);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(+ (BLOCK (ALT (.. 'a' 'z'))))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element12() throws Exception {
		// gunit test on line 212
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "x=ID", 212);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(= x ID)";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element13() throws Exception {
		// gunit test on line 213
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "x=ID?", 213);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(? (BLOCK (ALT (= x ID))))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element14() throws Exception {
		// gunit test on line 214
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "x=ID*", 214);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(* (BLOCK (ALT (= x ID))))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element15() throws Exception {
		// gunit test on line 215
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "x=b", 215);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(= x b)";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element16() throws Exception {
		// gunit test on line 216
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "x=(A|B)", 216);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(= x (BLOCK (ALT A) (ALT B)))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element17() throws Exception {
		// gunit test on line 217
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "x=(A|B)^", 217);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(= x (^ (BLOCK (ALT A) (ALT B))))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element18() throws Exception {
		// gunit test on line 218
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "x=~(A|B)", 218);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(= x (~ (BLOCK (ALT A) (ALT B))))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element19() throws Exception {
		// gunit test on line 219
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "x+=~(A|B)", 219);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(+= x (~ (BLOCK (ALT A) (ALT B))))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element20() throws Exception {
		// gunit test on line 220
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "x+=~(A|B)+", 220);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(+ (BLOCK (ALT (+= x (~ (BLOCK (ALT A) (ALT B)))))))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element21() throws Exception {
		// gunit test on line 221
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "x=b+", 221);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(+ (BLOCK (ALT (= x b))))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element22() throws Exception {
		// gunit test on line 222
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "x+=ID*", 222);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(* (BLOCK (ALT (+= x ID))))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element23() throws Exception {
		// gunit test on line 223
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "x+='int'*", 223);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(* (BLOCK (ALT (+= x 'int'))))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element24() throws Exception {
		// gunit test on line 224
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "x+=b+", 224);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(+ (BLOCK (ALT (+= x b))))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element25() throws Exception {
		// gunit test on line 225
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "('*'^)*", 225);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(* (BLOCK (ALT (^ '*'))))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element26() throws Exception {
		// gunit test on line 226
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "({blort} 'x')*", 226);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(* (BLOCK (ALT {blort} 'x')))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element27() throws Exception {
		// gunit test on line 227
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "A!", 227);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(! A)";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element28() throws Exception {
		// gunit test on line 228
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "A^", 228);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(^ A)";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element29() throws Exception {
		// gunit test on line 229
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "x=A^", 229);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(= x (^ A))";
		assertEquals("testing rule element", expecting, actual);
	}
}