/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Parser;
import org.antlr.runtime.RuleReturnScope;
import org.antlr.runtime.TokenSource;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeAdaptor;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

//  NO LONGER using gunit!!!

public class TestASTStructure {
	String lexerClassName = "org.antlr.v4.parse.ANTLRLexer";
	String parserClassName = "org.antlr.v4.parse.ANTLRParser";
	String  adaptorClassName = "org.antlr.v4.parse.GrammarASTAdaptor";

	public Object execParser(
	String ruleName,
	String input,
	int scriptLine)
	throws Exception
	{
		ANTLRStringStream is = new ANTLRStringStream(input);
		Class<? extends TokenSource> lexerClass = Class.forName(lexerClassName).asSubclass(TokenSource.class);
		Constructor<? extends TokenSource> lexConstructor = lexerClass.getConstructor(CharStream.class);
		TokenSource lexer = lexConstructor.newInstance(is);
		is.setLine(scriptLine);

		CommonTokenStream tokens = new CommonTokenStream(lexer);

		Class<? extends Parser> parserClass = Class.forName(parserClassName).asSubclass(Parser.class);
		Constructor<? extends Parser> parConstructor = parserClass.getConstructor(TokenStream.class);
		Parser parser = parConstructor.newInstance(tokens);

		// set up customized tree adaptor if necessary
		if ( adaptorClassName!=null ) {
			Method m = parserClass.getMethod("setTreeAdaptor", TreeAdaptor.class);
			Class<? extends TreeAdaptor> adaptorClass = Class.forName(adaptorClassName).asSubclass(TreeAdaptor.class);
			m.invoke(parser, adaptorClass.newInstance());
		}

		Method ruleMethod = parserClass.getMethod(ruleName);

		// INVOKE RULE
		return ruleMethod.invoke(parser);
	}

	@Test public void test_grammarSpec1() throws Exception {
		// gunit test on line 15
		RuleReturnScope rstruct = (RuleReturnScope)execParser("grammarSpec", "parser grammar P; a : A;", 15);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(PARSER_GRAMMAR P (RULES (RULE a (BLOCK (ALT A)))))";
		assertEquals("testing rule grammarSpec", expecting, actual);
	}

	@Test public void test_grammarSpec2() throws Exception {
		// gunit test on line 18
		RuleReturnScope rstruct = (RuleReturnScope)execParser("grammarSpec", "\n    parser grammar P;\n    tokens { A, B }\n    @header {foo}\n    a : A;\n    ", 18);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(PARSER_GRAMMAR P (tokens { A B) (@ header {foo}) (RULES (RULE a (BLOCK (ALT A)))))";
		assertEquals("testing rule grammarSpec", expecting, actual);
	}

	@Test public void test_grammarSpec3() throws Exception {
		// gunit test on line 30
		RuleReturnScope rstruct = (RuleReturnScope)execParser("grammarSpec", "\n    parser grammar P;\n    @header {foo}\n    tokens { A,B }\n    a : A;\n    ", 30);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(PARSER_GRAMMAR P (@ header {foo}) (tokens { A B) (RULES (RULE a (BLOCK (ALT A)))))";
		assertEquals("testing rule grammarSpec", expecting, actual);
	}

	@Test public void test_grammarSpec4() throws Exception {
		// gunit test on line 42
		RuleReturnScope rstruct = (RuleReturnScope)execParser("grammarSpec", "\n    parser grammar P;\n    import A=B, C;\n    a : A;\n    ", 42);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(PARSER_GRAMMAR P (import (= A B) C) (RULES (RULE a (BLOCK (ALT A)))))";
		assertEquals("testing rule grammarSpec", expecting, actual);
	} @Test public void test_delegateGrammars1() throws Exception {
		// gunit test on line 53
		RuleReturnScope rstruct = (RuleReturnScope)execParser("delegateGrammars", "import A;", 53);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(import A)";
		assertEquals("testing rule delegateGrammars", expecting, actual);
	} @Test public void test_rule1() throws Exception {
		// gunit test on line 56
		RuleReturnScope rstruct = (RuleReturnScope)execParser("rule", "a : A<X,Y=a.b.c>;", 56);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(RULE a (BLOCK (ALT (A (ELEMENT_OPTIONS X (= Y a.b.c))))))";
		assertEquals("testing rule rule", expecting, actual);
	}

	@Test public void test_rule2() throws Exception {
		// gunit test on line 58
		RuleReturnScope rstruct = (RuleReturnScope)execParser("rule", "A : B+;", 58);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(RULE A (BLOCK (ALT (+ (BLOCK (ALT B))))))";
		assertEquals("testing rule rule", expecting, actual);
	}

	@Test public void test_rule3() throws Exception {
		// gunit test on line 60
		RuleReturnScope rstruct = (RuleReturnScope)execParser("rule", "\n    a[int i] returns [int y]\n    @init {blort}\n      : ID ;\n    ", 60);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(RULE a int i (returns int y) (@ init {blort}) (BLOCK (ALT ID)))";
		assertEquals("testing rule rule", expecting, actual);
	}

	@Test public void test_rule4() throws Exception {
		// gunit test on line 75
		RuleReturnScope rstruct = (RuleReturnScope)execParser("rule", "\n    a[int i] returns [int y]\n    @init {blort}\n    options {backtrack=true;}\n      : ID;\n    ", 75);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(RULE a int i (returns int y) (@ init {blort}) (OPTIONS (= backtrack true)) (BLOCK (ALT ID)))";
		assertEquals("testing rule rule", expecting, actual);
	}

	@Test public void test_rule5() throws Exception {
		// gunit test on line 88
		RuleReturnScope rstruct = (RuleReturnScope)execParser("rule", "\n    a : ID ;\n      catch[A b] {foo}\n      finally {bar}\n    ", 88);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(RULE a (BLOCK (ALT ID)) (catch A b {foo}) (finally {bar}))";
		assertEquals("testing rule rule", expecting, actual);
	}

	@Test public void test_rule6() throws Exception {
		// gunit test on line 97
		RuleReturnScope rstruct = (RuleReturnScope)execParser("rule", "\n    a : ID ;\n      catch[A a] {foo}\n      catch[B b] {fu}\n      finally {bar}\n    ", 97);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(RULE a (BLOCK (ALT ID)) (catch A a {foo}) (catch B b {fu}) (finally {bar}))";
		assertEquals("testing rule rule", expecting, actual);
	}

	@Test public void test_rule7() throws Exception {
		// gunit test on line 107
		RuleReturnScope rstruct = (RuleReturnScope)execParser("rule", "\n\ta[int i]\n\tlocals [int a, float b]\n\t\t:\tA\n\t\t;\n\t", 107);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(RULE a int i (locals int a, float b) (BLOCK (ALT A)))";
		assertEquals("testing rule rule", expecting, actual);
	}

	@Test public void test_rule8() throws Exception {
		// gunit test on line 115
		RuleReturnScope rstruct = (RuleReturnScope)execParser("rule", "\n\ta[int i] throws a.b.c\n\t\t:\tA\n\t\t;\n\t", 115);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(RULE a int i (throws a.b.c) (BLOCK (ALT A)))";
		assertEquals("testing rule rule", expecting, actual);
	} @Test public void test_ebnf1() throws Exception {
		// gunit test on line 123
		RuleReturnScope rstruct = (RuleReturnScope)execParser("ebnf", "(A|B)", 123);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(BLOCK (ALT A) (ALT B))";
		assertEquals("testing rule ebnf", expecting, actual);
	}

	@Test public void test_ebnf2() throws Exception {
		// gunit test on line 124
		RuleReturnScope rstruct = (RuleReturnScope)execParser("ebnf", "(A|B)?", 124);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(? (BLOCK (ALT A) (ALT B)))";
		assertEquals("testing rule ebnf", expecting, actual);
	}

	@Test public void test_ebnf3() throws Exception {
		// gunit test on line 125
		RuleReturnScope rstruct = (RuleReturnScope)execParser("ebnf", "(A|B)*", 125);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(* (BLOCK (ALT A) (ALT B)))";
		assertEquals("testing rule ebnf", expecting, actual);
	}

	@Test public void test_ebnf4() throws Exception {
		// gunit test on line 126
		RuleReturnScope rstruct = (RuleReturnScope)execParser("ebnf", "(A|B)+", 126);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(+ (BLOCK (ALT A) (ALT B)))";
		assertEquals("testing rule ebnf", expecting, actual);
	} @Test public void test_element1() throws Exception {
		// gunit test on line 129
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "~A", 129);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(~ (SET A))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element2() throws Exception {
		// gunit test on line 130
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "b+", 130);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(+ (BLOCK (ALT b)))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element3() throws Exception {
		// gunit test on line 131
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "(b)+", 131);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(+ (BLOCK (ALT b)))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element4() throws Exception {
		// gunit test on line 132
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "b?", 132);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(? (BLOCK (ALT b)))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element5() throws Exception {
		// gunit test on line 133
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "(b)?", 133);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(? (BLOCK (ALT b)))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element6() throws Exception {
		// gunit test on line 134
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "(b)*", 134);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(* (BLOCK (ALT b)))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element7() throws Exception {
		// gunit test on line 135
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "b*", 135);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(* (BLOCK (ALT b)))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element8() throws Exception {
		// gunit test on line 136
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "'while'*", 136);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(* (BLOCK (ALT 'while')))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element9() throws Exception {
		// gunit test on line 137
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "'a'+", 137);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(+ (BLOCK (ALT 'a')))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element10() throws Exception {
		// gunit test on line 138
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "a[3]", 138);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(a 3)";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element11() throws Exception {
		// gunit test on line 139
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "'a'..'z'+", 139);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(+ (BLOCK (ALT (.. 'a' 'z'))))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element12() throws Exception {
		// gunit test on line 140
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "x=ID", 140);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(= x ID)";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element13() throws Exception {
		// gunit test on line 141
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "x=ID?", 141);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(? (BLOCK (ALT (= x ID))))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element14() throws Exception {
		// gunit test on line 142
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "x=ID*", 142);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(* (BLOCK (ALT (= x ID))))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element15() throws Exception {
		// gunit test on line 143
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "x=b", 143);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(= x b)";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element16() throws Exception {
		// gunit test on line 144
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "x=(A|B)", 144);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(= x (BLOCK (ALT A) (ALT B)))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element17() throws Exception {
		// gunit test on line 145
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "x=~(A|B)", 145);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(= x (~ (SET A B)))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element18() throws Exception {
		// gunit test on line 146
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "x+=~(A|B)", 146);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(+= x (~ (SET A B)))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element19() throws Exception {
		// gunit test on line 147
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "x+=~(A|B)+", 147);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(+ (BLOCK (ALT (+= x (~ (SET A B))))))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element20() throws Exception {
		// gunit test on line 148
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "x=b+", 148);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(+ (BLOCK (ALT (= x b))))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element21() throws Exception {
		// gunit test on line 149
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "x+=ID*", 149);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(* (BLOCK (ALT (+= x ID))))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element22() throws Exception {
		// gunit test on line 150
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "x+='int'*", 150);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(* (BLOCK (ALT (+= x 'int'))))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element23() throws Exception {
		// gunit test on line 151
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "x+=b+", 151);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(+ (BLOCK (ALT (+= x b))))";
		assertEquals("testing rule element", expecting, actual);
	}

	@Test public void test_element24() throws Exception {
		// gunit test on line 152
		RuleReturnScope rstruct = (RuleReturnScope)execParser("element", "({blort} 'x')*", 152);
		Object actual = ((Tree)rstruct.getTree()).toStringTree();
		Object expecting = "(* (BLOCK (ALT {blort} 'x')))";
		assertEquals("testing rule element", expecting, actual);
	}
}
