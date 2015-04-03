package org.antlr.v4.test.rt.java;

import org.antlr.v4.test.ErrorQueue;
import org.antlr.v4.tool.Grammar;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestCompositeParsers extends BaseTest {

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testDelegatorInvokesDelegateRule() throws Exception {
		String slave_S = "parser grammar S;\n" +
	                  "a : B {System.out.println(\"S.a\");};";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "grammar M;\n" +
	                  "import S;\n" +
	                  "s : a ;\n" +
	                  "B : 'b' ; // defines B from inherited token space\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "s", "b", false);
		assertEquals("S.a\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testBringInLiteralsFromDelegate() throws Exception {
		String slave_S = "parser grammar S;\n" +
	                  "a : '=' 'a' {System.out.print(\"S.a\");};";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "grammar M;\n" +
	                  "import S;\n" +
	                  "s : a ;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "s", "=a", false);
		assertEquals("S.a\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testDelegatorInvokesDelegateRuleWithArgs() throws Exception {
		String slave_S = "parser grammar S;\n" +
	                  "a[int x] returns [int y] : B {System.out.print(\"S.a\");;$y=1000;};";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "grammar M;\n" +
	                  "import S;\n" +
	                  "s : label=a[3] {System.out.println($label.y);} ;\n" +
	                  "B : 'b' ; // defines B from inherited token space\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "s", "b", false);
		assertEquals("S.a1000\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testDelegatorInvokesDelegateRuleWithReturnStruct() throws Exception {
		String slave_S = "parser grammar S;\n" +
	                  "a : B {System.out.print(\"S.a\");};";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "grammar M;\n" +
	                  "import S;\n" +
	                  "s : a {System.out.print($a.text);} ;\n" +
	                  "B : 'b' ; // defines B from inherited token space\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "s", "b", false);
		assertEquals("S.ab\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testDelegatorAccessesDelegateMembers() throws Exception {
		String slave_S = "parser grammar S;\n" +
	                  "@members {\n" +
	                  "public void foo() {System.out.println(\"foo\");}\n" +
	                  "}\n" +
	                  "a : B;";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "grammar M; // uses no rules from the import\n" +
	                  "import S;\n" +
	                  "s : 'b'{this.foo();}; // gS is import pointer\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "s", "b", false);
		assertEquals("foo\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testDelegatorInvokesFirstVersionOfDelegateRule() throws Exception {
		String slave_S = "parser grammar S;\n" +
	                  "a : B {System.out.println(\"S.a\");};\n" +
	                  "b : B;";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String slave_T = "parser grammar T;\n" +
	                  "a : B {System.out.println(\"T.a\");};";
		mkdir(tmpdir);
		writeFile(tmpdir, "T.g4", slave_T);

		String grammar = "grammar M;\n" +
	                  "import S,T;\n" +
	                  "s : a ;\n" +
	                  "B : 'b' ; // defines B from inherited token space\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "s", "b", false);
		assertEquals("S.a\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testDelegatesSeeSameTokenType() throws Exception {
		String slave_S = "parser grammar S;\n" +
	                  "tokens { A, B, C }\n" +
	                  "x : A {System.out.println(\"S.x\");};";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String slave_T = "parser grammar S;\n" +
	                  "tokens { C, B, A } // reverse order\n" +
	                  "y : A {System.out.println(\"T.y\");};";
		mkdir(tmpdir);
		writeFile(tmpdir, "T.g4", slave_T);

		String grammar = "// The lexer will create rules to match letters a, b, c.\n" +
	                  "// The associated token types A, B, C must have the same value\n" +
	                  "// and all import'd parsers.  Since ANTLR regenerates all imports\n" +
	                  "// for use with the delegator M, it can generate the same token type\n" +
	                  "// mapping in each parser:\n" +
	                  "// public static final int C=6;\n" +
	                  "// public static final int EOF=-1;\n" +
	                  "// public static final int B=5;\n" +
	                  "// public static final int WS=7;\n" +
	                  "// public static final int A=4;\n" +
	                  "grammar M;\n" +
	                  "import S,T;\n" +
	                  "s : x y ; // matches AA, which should be 'aa'\n" +
	                  "B : 'b' ; // another order: B, A, C\n" +
	                  "A : 'a' ; \n" +
	                  "C : 'c' ; \n" +
	                  "WS : (' '|'\\n') -> skip ;";
		writeFile(tmpdir, "M.g4", grammar);
		ErrorQueue equeue = new ErrorQueue();
		Grammar g = new Grammar(tmpdir+"/M.g4", grammar, equeue);
		String expectedTokenIDToTypeMap = "{EOF=-1, B=1, A=2, C=3, WS=4}";
		String expectedStringLiteralToTypeMap = "{'a'=2, 'b'=1, 'c'=3}";
		String expectedTypeToTokenList = "[B, A, C, WS]";
		assertEquals(expectedTokenIDToTypeMap, g.tokenNameToTypeMap.toString());
		assertEquals(expectedStringLiteralToTypeMap, sort(g.stringLiteralToTypeMap).toString());
		assertEquals(expectedTypeToTokenList, realElements(g.typeToTokenList).toString());
		assertEquals("unexpected errors: "+equeue, 0, equeue.errors.size());

		String found = execParser("M.g4", grammar, "MParser", "MLexer", "s", "aa", false);
		assertEquals("S.x\nT.y\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testCombinedImportsCombined() throws Exception {
		String slave_S = "parser grammar S;\n" +
	                  "tokens { A, B, C }\n" +
	                  "x : 'x' INT {System.out.println(\"S.x\");};\n" +
	                  "INT : '0'..'9'+ ;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "grammar M;\n" +
	                  "import S;\n" +
	                  "s : x INT;";
		writeFile(tmpdir, "M.g4", grammar);
		ErrorQueue equeue = new ErrorQueue();
		new Grammar(tmpdir+"/M.g4", grammar, equeue);
		assertEquals("unexpected errors: " + equeue, 0, equeue.errors.size());

		String found = execParser("M.g4", grammar, "MParser", "MLexer", "s", "x 34 9", false);
		assertEquals("S.x\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testDelegatorRuleOverridesDelegate() throws Exception {
		String slave_S = "parser grammar S;\n" +
	                  "a : b {System.out.print(\"S.a\");};\n" +
	                  "b : B ;";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "grammar M;\n" +
	                  "import S;\n" +
	                  "b : 'b'|'c';\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "a", "c", false);
		assertEquals("S.a\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testDelegatorRuleOverridesLookaheadInDelegate() throws Exception {
		String slave_S = "parser grammar S;\n" +
	                  "type_ : 'int' ;\n" +
	                  "decl : type_ ID ';'\n" +
	                  "	| type_ ID init ';' {System.out.print(\"Decl: \" + $text);};\n" +
	                  "init : '=' INT;";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "grammar M;\n" +
	                  "import S;\n" +
	                  "prog : decl ;\n" +
	                  "type_ : 'int' | 'float' ;\n" +
	                  "ID  : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+ ;\n" +
	                  "WS : (' '|'\\n') -> skip;";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "prog", "float x = 3;", false);
		assertEquals("Decl: floatx=3;\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testDelegatorRuleOverridesDelegates() throws Exception {
		String slave_S = "parser grammar S;\n" +
	                  "a : b {System.out.println(\"S.a\");};\n" +
	                  "b : 'b' ;\n" +
	                  "   ";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String slave_T = "parser grammar S;\n" +
	                  "tokens { A }\n" +
	                  "b : 'b' {System.out.println(\"T.b\");};";
		mkdir(tmpdir);
		writeFile(tmpdir, "T.g4", slave_T);

		String grammar = "grammar M;\n" +
	                  "import S, T;\n" +
	                  "b : 'b'|'c' {System.out.println(\"M.b\");}|B|A;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "a", "c", false);
		assertEquals("M.b\nS.a\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testKeywordVSIDOrder() throws Exception {
		String slave_S = "lexer grammar S;\n" +
	                  "ID : 'a'..'z'+;";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "grammar M;\n" +
	                  "import S;\n" +
	                  "a : A {System.out.println(\"M.a: \" + $A);};\n" +
	                  "A : 'abc' {System.out.println(\"M.A\");};\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "a", "abc", false);
		assertEquals("M.A\nM.a: [@0,0:2='abc',<1>,1:0]\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testImportedRuleWithAction() throws Exception {
		String slave_S = "parser grammar S;\n" +
	                  "a @after {} : B;";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "grammar M;\n" +
	                  "import S;\n" +
	                  "s : a;\n" +
	                  "B : 'b';\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "s", "b", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testImportedGrammarWithEmptyOptions() throws Exception {
		String slave_S = "parser grammar S;\n" +
	                  "options {}\n" +
	                  "a : B;";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "grammar M;\n" +
	                  "import S;\n" +
	                  "s : a;\n" +
	                  "B : 'b';\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "s", "b", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testImportLexerWithOnlyFragmentRules() throws Exception {
		String slave_S = "lexer grammar S;\n" +
	                  "fragment\n" +
	                  "UNICODE_CLASS_Zs    : '\\u0020' | '\\u00A0' | '\\u1680' | '\\u180E'\n" +
	                  "                    | '\\u2000'..'\\u200A'\n" +
	                  "                    | '\\u202F' | '\\u205F' | '\\u3000'\n" +
	                  "                    ;";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "grammar M;\n" +
	                  "import S;\n" +
	                  "program : 'test' 'test';\n" +
	                  "WS : (UNICODE_CLASS_Zs)+ -> skip;";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "program", "test test", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}


}