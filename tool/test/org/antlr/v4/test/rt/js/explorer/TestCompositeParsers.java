package org.antlr.v4.test.rt.js.explorer;

import org.junit.Test;
import static org.junit.Assert.*;

import org.antlr.v4.test.tool.ErrorQueue;
import org.antlr.v4.tool.Grammar;

public class TestCompositeParsers extends BaseTest {

	@Test
	public void testDelegatorInvokesDelegateRule() throws Exception {
		String slave_S = "parser grammar S;\r\n" +
	                  "a : B {document.getElementById('output').value += \"S.a\" + '\\n';};\r";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "grammar M;\r\n" +
	                  "import S;\r\n" +
	                  "s : a ;\r\n" +
	                  "B : 'b' ; // defines B from inherited token space\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "MListener", "MVisitor", "s", "b", false);
		assertEquals("S.a\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testBringInLiteralsFromDelegate() throws Exception {
		String slave_S = "parser grammar S;\r\n" +
	                  "a : '=' 'a' {document.getElementById('output').value += \"S.a\";};\r";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "grammar M;\r\n" +
	                  "import S;\r\n" +
	                  "s : a ;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "MListener", "MVisitor", "s", "=a", false);
		assertEquals("S.a\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testDelegatorInvokesDelegateRuleWithArgs() throws Exception {
		String slave_S = "parser grammar S;\r\n" +
	                  "a[int x] returns [int y] : B {document.getElementById('output').value += \"S.a\";;$y=1000;};\r";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "grammar M;\r\n" +
	                  "import S;\r\n" +
	                  "s : label=a[3] {document.getElementById('output').value += $label.y + '\\n';} ;\r\n" +
	                  "B : 'b' ; // defines B from inherited token space\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "MListener", "MVisitor", "s", "b", false);
		assertEquals("S.a1000\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testDelegatorInvokesDelegateRuleWithReturnStruct() throws Exception {
		String slave_S = "parser grammar S;\r\n" +
	                  "a : B {document.getElementById('output').value += \"S.a\";};\r";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "grammar M;\r\n" +
	                  "import S;\r\n" +
	                  "s : a {document.getElementById('output').value += $a.text;} ;\r\n" +
	                  "B : 'b' ; // defines B from inherited token space\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "MListener", "MVisitor", "s", "b", false);
		assertEquals("S.ab\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testDelegatorAccessesDelegateMembers() throws Exception {
		String slave_S = "parser grammar S;\r\n" +
	                  "@members {\r\n" +
	                  "this.foo = function() {document.getElementById('output').value += 'foo' + '\\n';};\r\n" +
	                  "}\r\n" +
	                  "a : B;\r";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "grammar M; // uses no rules from the import\r\n" +
	                  "import S;\r\n" +
	                  "s : 'b'{this.foo();}; // gS is import pointer\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "MListener", "MVisitor", "s", "b", false);
		assertEquals("foo\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testDelegatorInvokesFirstVersionOfDelegateRule() throws Exception {
		String slave_S = "parser grammar S;\r\n" +
	                  "a : B {document.getElementById('output').value += \"S.a\" + '\\n';};\r\n" +
	                  "b : B;\r";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String slave_T = "parser grammar T;\r\n" +
	                  "a : B {document.getElementById('output').value += \"T.a\" + '\\n';};\r";
		mkdir(tmpdir);
		writeFile(tmpdir, "T.g4", slave_T);

		String grammar = "grammar M;\r\n" +
	                  "import S,T;\r\n" +
	                  "s : a ;\r\n" +
	                  "B : 'b' ; // defines B from inherited token space\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "MListener", "MVisitor", "s", "b", false);
		assertEquals("S.a\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testDelegatesSeeSameTokenType() throws Exception {
		String slave_S = "parser grammar S;\r\n" +
	                  "tokens { A, B, C }\r\n" +
	                  "x : A {document.getElementById('output').value += \"S.x\" + '\\n';};\r";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String slave_T = "parser grammar S;\r\n" +
	                  "tokens { C, B, A } // reverse order\r\n" +
	                  "y : A {document.getElementById('output').value += \"T.y\" + '\\n';};\r";
		mkdir(tmpdir);
		writeFile(tmpdir, "T.g4", slave_T);

		String grammar = "// The lexer will create rules to match letters a, b, c.\r\n" +
	                  "// The associated token types A, B, C must have the same value\r\n" +
	                  "// and all import'd parsers.  Since ANTLR regenerates all imports\r\n" +
	                  "// for use with the delegator M, it can generate the same token type\r\n" +
	                  "// mapping in each parser:\r\n" +
	                  "// public static final int C=6;\r\n" +
	                  "// public static final int EOF=-1;\r\n" +
	                  "// public static final int B=5;\r\n" +
	                  "// public static final int WS=7;\r\n" +
	                  "// public static final int A=4;\r\n" +
	                  "grammar M;\r\n" +
	                  "import S,T;\r\n" +
	                  "s : x y ; // matches AA, which should be 'aa'\r\n" +
	                  "B : 'b' ; // another order: B, A, C\r\n" +
	                  "A : 'a' ; \r\n" +
	                  "C : 'c' ; \r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
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

		String found = execParser("M.g4", grammar, "MParser", "MLexer", "MListener", "MVisitor", "s", "aa", false);
		assertEquals("S.x\nT.y\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testCombinedImportsCombined() throws Exception {
		String slave_S = "parser grammar S;\r\n" +
	                  "tokens { A, B, C }\r\n" +
	                  "x : 'x' INT {document.getElementById('output').value += \"S.x\" + '\\n';};\r\n" +
	                  "INT : '0'..'9'+ ;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "grammar M;\r\n" +
	                  "import S;\r\n" +
	                  "s : x INT;\r";
		writeFile(tmpdir, "M.g4", grammar);
		ErrorQueue equeue = new ErrorQueue();
		new Grammar(tmpdir+"/M.g4", grammar, equeue);
		assertEquals("unexpected errors: " + equeue, 0, equeue.errors.size());

		String found = execParser("M.g4", grammar, "MParser", "MLexer", "MListener", "MVisitor", "s", "x 34 9", false);
		assertEquals("S.x\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testDelegatorRuleOverridesDelegate() throws Exception {
		String slave_S = "parser grammar S;\r\n" +
	                  "a : b {document.getElementById('output').value += \"S.a\";};\r\n" +
	                  "b : B ;\r";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "grammar M;\r\n" +
	                  "import S;\r\n" +
	                  "b : 'b'|'c';\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "MListener", "MVisitor", "a", "c", false);
		assertEquals("S.a\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testDelegatorRuleOverridesLookaheadInDelegate() throws Exception {
		String slave_S = "parser grammar S;\r\n" +
	                  "type_ : 'int' ;\r\n" +
	                  "decl : type_ ID ';'\r\n" +
	                  "	| type_ ID init ';' {document.getElementById('output').value += \"Decl: \" + $text;};\r\n" +
	                  "init : '=' INT;\r";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "grammar M;\r\n" +
	                  "import S;\r\n" +
	                  "prog : decl ;\r\n" +
	                  "type_ : 'int' | 'float' ;\r\n" +
	                  "ID  : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+ ;\r\n" +
	                  "WS : (' '|'\\n') -> skip;\r";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "MListener", "MVisitor", "prog", "float x = 3;", false);
		assertEquals("Decl: floatx=3;\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testDelegatorRuleOverridesDelegates() throws Exception {
		String slave_S = "parser grammar S;\r\n" +
	                  "a : b {document.getElementById('output').value += \"S.a\" + '\\n';};\r\n" +
	                  "b : 'b' ;\r\n" +
	                  "   ";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String slave_T = "parser grammar S;\r\n" +
	                  "tokens { A }\r\n" +
	                  "b : 'b' {document.getElementById('output').value += \"T.b\" + '\\n';};\r";
		mkdir(tmpdir);
		writeFile(tmpdir, "T.g4", slave_T);

		String grammar = "grammar M;\r\n" +
	                  "import S, T;\r\n" +
	                  "b : 'b'|'c' {document.getElementById('output').value += \"M.b\" + '\\n';}|B|A;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "MListener", "MVisitor", "a", "c", false);
		assertEquals("M.b\nS.a\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testKeywordVSIDOrder() throws Exception {
		String slave_S = "lexer grammar S;\r\n" +
	                  "ID : 'a'..'z'+;\r";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "grammar M;\r\n" +
	                  "import S;\r\n" +
	                  "a : A {document.getElementById('output').value += \"M.a: \" + $A + '\\n';};\r\n" +
	                  "A : 'abc' {document.getElementById('output').value += \"M.A\" + '\\n';};\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "MListener", "MVisitor", "a", "abc", false);
		assertEquals("M.A\nM.a: [@0,0:2='abc',<1>,1:0]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testImportedRuleWithAction() throws Exception {
		String slave_S = "parser grammar S;\r\n" +
	                  "a @after {} : B;\r";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "grammar M;\r\n" +
	                  "import S;\r\n" +
	                  "s : a;\r\n" +
	                  "B : 'b';\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "MListener", "MVisitor", "s", "b", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testImportedGrammarWithEmptyOptions() throws Exception {
		String slave_S = "parser grammar S;\r\n" +
	                  "options {}\r\n" +
	                  "a : B;\r";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "grammar M;\r\n" +
	                  "import S;\r\n" +
	                  "s : a;\r\n" +
	                  "B : 'b';\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "MListener", "MVisitor", "s", "b", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testImportLexerWithOnlyFragmentRules() throws Exception {
		String slave_S = "lexer grammar S;\r\n" +
	                  "fragment\r\n" +
	                  "UNICODE_CLASS_Zs    : '\\u0020' | '\\u00A0' | '\\u1680' | '\\u180E'\r\n" +
	                  "                    | '\\u2000'..'\\u200A'\r\n" +
	                  "                    | '\\u202F' | '\\u205F' | '\\u3000'\r\n" +
	                  "                    ;\r";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "grammar M;\r\n" +
	                  "import S;\r\n" +
	                  "program : 'test' 'test';\r\n" +
	                  "WS : (UNICODE_CLASS_Zs)+ -> skip;\r";
		String found = execParser("M.g4", grammar, "MParser", "MLexer", "MListener", "MVisitor", "program", "test test", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}


}