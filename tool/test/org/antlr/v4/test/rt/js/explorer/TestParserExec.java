package org.antlr.v4.test.rt.js.explorer;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestParserExec extends BaseTest {

	@Test
	public void testLabels() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : b1=b b2+=b* b3+=';' ;\r\n" +
	                  "b : id_=ID val+=INT*;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "abc 34;", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testListLabelsOnSet() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : b b* ';' ;\r\n" +
	                  "b : ID val+=(INT | FLOAT)*;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+;\r\n" +
	                  "FLOAT : [0-9]+ '.' [0-9]+;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "abc 34;", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testAorB() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : ID {\r\n" +
	                  "document.getElementById('output').value += \"alt 1\" + '\\n';\r\n" +
	                  "} | INT {\r\n" +
	                  "document.getElementById('output').value += \"alt 2\" + '\\n';\r\n" +
	                  "};\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "34", false);
		assertEquals("alt 2\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testBasic() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : ID INT {\r\n" +
	                  "document.getElementById('output').value += $text + '\\n';\r\n" +
	                  "};\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "abc 34", false);
		assertEquals("abc34\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testAPlus() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : ID+ {\r\n" +
	                  "document.getElementById('output').value += $text + '\\n';\r\n" +
	                  "};\r\n" +
	                  "ID : 'a'..'z'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "a b c", false);
		assertEquals("abc\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testAorAPlus() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : (ID|ID)+ {\r\n" +
	                  "document.getElementById('output').value += $text + '\\n';\r\n" +
	                  "};\r\n" +
	                  "ID : 'a'..'z'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "a b c", false);
		assertEquals("abc\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testIfIfElseGreedyBinding1() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "start : statement+ ;\r\n" +
	                  "statement : 'x' | ifStatement;\r\n" +
	                  "ifStatement : 'if' 'y' statement ('else' statement)? {\r\n" +
	                  "document.getElementById('output').value += $text + '\\n';\r\n" +
	                  "};\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "WS : (' '|'\\n') -> channel(HIDDEN);\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "start", "if y if y x else x", false);
		assertEquals("if y x else x\nif y if y x else x\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testIfIfElseGreedyBinding2() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "start : statement+ ;\r\n" +
	                  "statement : 'x' | ifStatement;\r\n" +
	                  "ifStatement : 'if' 'y' statement ('else' statement|) {\r\n" +
	                  "document.getElementById('output').value += $text + '\\n';\r\n" +
	                  "};\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "WS : (' '|'\\n') -> channel(HIDDEN);\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "start", "if y if y x else x", false);
		assertEquals("if y x else x\nif y if y x else x\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testIfIfElseNonGreedyBinding1() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "start : statement+ ;\r\n" +
	                  "statement : 'x' | ifStatement;\r\n" +
	                  "ifStatement : 'if' 'y' statement ('else' statement)?? {\r\n" +
	                  "document.getElementById('output').value += $text + '\\n';\r\n" +
	                  "};\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "WS : (' '|'\\n') -> channel(HIDDEN);\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "start", "if y if y x else x", false);
		assertEquals("if y x\nif y if y x else x\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testIfIfElseNonGreedyBinding2() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "start : statement+ ;\r\n" +
	                  "statement : 'x' | ifStatement;\r\n" +
	                  "ifStatement : 'if' 'y' statement (|'else' statement) {\r\n" +
	                  "document.getElementById('output').value += $text + '\\n';\r\n" +
	                  "};\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "WS : (' '|'\\n') -> channel(HIDDEN);\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "start", "if y if y x else x", false);
		assertEquals("if y x\nif y if y x else x\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testAStar(String input) throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : ID* {\r\n" +
	                  "document.getElementById('output').value += $text + '\\n';\r\n" +
	                  "};\r\n" +
	                  "ID : 'a'..'z'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip;\r";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", input, false);
	}

	@Test
	public void testAStar_1() throws Exception {
		String found = testAStar("");
		assertEquals("\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testAStar_2() throws Exception {
		String found = testAStar("a b c");
		assertEquals("abc\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testLL1OptionalBlock(String input) throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : (ID|{}INT)? {\r\n" +
	                  "document.getElementById('output').value += $text + '\\n';\r\n" +
	                  "};\r\n" +
	                  "ID : 'a'..'z'+;\r\n" +
	                  "INT : '0'..'9'+ ;\r\n" +
	                  "WS : (' '|'\\n') -> skip;\r";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", input, false);
	}

	@Test
	public void testLL1OptionalBlock_1() throws Exception {
		String found = testLL1OptionalBlock("");
		assertEquals("\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testLL1OptionalBlock_2() throws Exception {
		String found = testLL1OptionalBlock("a");
		assertEquals("a\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testAorAStar(String input) throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : (ID|ID)* {\r\n" +
	                  "document.getElementById('output').value += $text + '\\n';\r\n" +
	                  "};\r\n" +
	                  "ID : 'a'..'z'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip;\r";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", input, false);
	}

	@Test
	public void testAorAStar_1() throws Exception {
		String found = testAorAStar("");
		assertEquals("\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testAorAStar_2() throws Exception {
		String found = testAorAStar("a b c");
		assertEquals("abc\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testAorBPlus() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : (ID|INT{\r\n" +
	                  "})+ {\r\n" +
	                  "document.getElementById('output').value += $text + '\\n';\r\n" +
	                  "};\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "a 34 c", false);
		assertEquals("a34c\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testAorBStar(String input) throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : (ID|INT{\r\n" +
	                  "})* {\r\n" +
	                  "document.getElementById('output').value += $text + '\\n';\r\n" +
	                  "};\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", input, false);
	}

	@Test
	public void testAorBStar_1() throws Exception {
		String found = testAorBStar("");
		assertEquals("\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testAorBStar_2() throws Exception {
		String found = testAorBStar("a 34 c");
		assertEquals("a34c\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testOptional(String input) throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "stat : ifstat | 'x';\r\n" +
	                  "ifstat : 'if' stat ('else' stat)?;\r\n" +
	                  "WS : [ \\n\\t]+ -> skip ;\r";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "stat", input, false);
	}

	@Test
	public void testOptional_1() throws Exception {
		String found = testOptional("x");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testOptional_2() throws Exception {
		String found = testOptional("if x");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testOptional_3() throws Exception {
		String found = testOptional("if x else x");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testOptional_4() throws Exception {
		String found = testOptional("if if x else x");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testPredicatedIfIfElse() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s : stmt EOF ;\r\n" +
	                  "stmt : ifStmt | ID;\r\n" +
	                  "ifStmt : 'if' ID stmt ('else' stmt | { this._input.LA(1)!=ELSE }?);\r\n" +
	                  "ELSE : 'else';\r\n" +
	                  "ID : [a-zA-Z]+;\r\n" +
	                  "WS : [ \\n\\t]+ -> skip;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "if x if x a else b", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testLabelAliasingAcrossLabeledAlternatives() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "start : a* EOF;\r\n" +
	                  "a\r\n" +
	                  "  : label=subrule {document.getElementById('output').value += $label.text + '\\n';} #One\r\n" +
	                  "  | label='y' {document.getElementById('output').value += $label.text + '\\n';} #Two\r\n" +
	                  "  ;\r\n" +
	                  "subrule : 'x';\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "start", "xy", false);
		assertEquals("x\ny\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testPredictionIssue334() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "file_ @init{\r\n" +
	                  "this._errHandler = new antlr4.error.BailErrorStrategy();\r\n" +
	                  "} \r\n" +
	                  "@after {\r\n" +
	                  "document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';\r\n" +
	                  "}\r\n" +
	                  "  :   item (SEMICOLON item)* SEMICOLON? EOF ;\r\n" +
	                  "item : A B?;\r\n" +
	                  "SEMICOLON: ';';\r\n" +
	                  "A : 'a'|'A';\r\n" +
	                  "B : 'b'|'B';\r\n" +
	                  "WS      : [ \\r\\t\\n]+ -> skip;\r\n" +
	                  "\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "file_", "a", false);
		assertEquals("(file_ (item a) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testListLabelForClosureContext() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "ifStatement\r\n" +
	                  "@after {\r\n" +
	                  "var items = $ctx.elseIfStatement(); \r\n" +
	                  "}\r\n" +
	                  "    : 'if' expression\r\n" +
	                  "      ( ( 'then'\r\n" +
	                  "          executableStatement*\r\n" +
	                  "          elseIfStatement*  // <--- problem is here\r\n" +
	                  "          elseStatement?\r\n" +
	                  "          'end' 'if'\r\n" +
	                  "        ) | executableStatement )\r\n" +
	                  "    ;\r\n" +
	                  "\r\n" +
	                  "elseIfStatement\r\n" +
	                  "    : 'else' 'if' expression 'then' executableStatement*\r\n" +
	                  "    ;\r\n" +
	                  "expression : 'a' ;\r\n" +
	                  "executableStatement : 'a' ;\r\n" +
	                  "elseStatement : 'a' ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "expression", "a", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testMultipleEOFHandling() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "prog : ('x' | 'x' 'y') EOF EOF;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "prog", "x", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testEOFInClosure() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "prog : stat EOF;\r\n" +
	                  "stat : 'x' ('y' | EOF)*?;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "prog", "x", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	String testReferenceToATN(String input) throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : (ID|ATN_)* ATN_? {document.getElementById('output').value += $text + '\\n';} ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "ATN_ : '0'..'9'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", input, false);
	}

	@Test
	public void testReferenceToATN_1() throws Exception {
		String found = testReferenceToATN("");
		assertEquals("\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testReferenceToATN_2() throws Exception {
		String found = testReferenceToATN("a 34 c");
		assertEquals("a34c\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testAlternateQuotes() throws Exception {
		String slave_ModeTagsLexer = "lexer grammar ModeTagsLexer;\r\n" +
	                              "// Default mode rules (the SEA)\r\n" +
	                              "OPEN  : '«'     -> mode(ISLAND) ;       // switch to ISLAND mode\r\n" +
	                              "TEXT  : ~'«'+ ;                         // clump all text together\r\n" +
	                              "mode ISLAND;\r\n" +
	                              "CLOSE : '»'     -> mode(DEFAULT_MODE) ; // back to SEA mode\r\n" +
	                              "SLASH : '/' ;\r\n" +
	                              "ID    : [a-zA-Z]+ ;                     // match/send ID in tag to parser\r";
		rawGenerateAndBuildRecognizer("ModeTagsLexer.g4", slave_ModeTagsLexer, null, "ModeTagsLexer");

		String grammar = "parser grammar ModeTagsParser;\r\n" +
	                  "options { tokenVocab=ModeTagsLexer; } // use tokens from ModeTagsLexer.g4\r\n" +
	                  "file_: (tag | TEXT)* ;\r\n" +
	                  "tag : '«' ID '»'\r\n" +
	                  "    | '«' '/' ID '»'\r\n" +
	                  "    ;\r";
		String found = execParser("ModeTagsParser.g4", grammar, "ModeTagsParser", "ModeTagsLexer", "ModeTagsParserListener", "ModeTagsParserVisitor", "file_", "", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}


}