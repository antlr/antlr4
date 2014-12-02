package org.antlr.v4.test.rt.js.node;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestParserExec extends BaseTest {

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testLabels() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : b1=b b2+=b* b3+=';' ;\n" +
	                  "b : id_=ID val+=INT*;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "abc 34;", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testListLabelsOnSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : b b* ';' ;\n" +
	                  "b : ID val+=(INT | FLOAT)*;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "FLOAT : [0-9]+ '.' [0-9]+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "abc 34;", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testAorB() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : ID {\n" +
	                  "console.log(\"alt 1\");\n" +
	                  "} | INT {\n" +
	                  "console.log(\"alt 2\");\n" +
	                  "};\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "34", false);
		assertEquals("alt 2\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testBasic() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : ID INT {\n" +
	                  "console.log($text);\n" +
	                  "};\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "abc 34", false);
		assertEquals("abc34\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testAPlus() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : ID+ {\n" +
	                  "console.log($text);\n" +
	                  "};\n" +
	                  "ID : 'a'..'z'+;\n" +
	                  "WS : (' '|'\\n') -> skip;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "a b c", false);
		assertEquals("abc\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testAorAPlus() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : (ID|ID)+ {\n" +
	                  "console.log($text);\n" +
	                  "};\n" +
	                  "ID : 'a'..'z'+;\n" +
	                  "WS : (' '|'\\n') -> skip;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "a b c", false);
		assertEquals("abc\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testIfIfElseGreedyBinding1() throws Exception {
		String grammar = "grammar T;\n" +
	                  "start : statement+ ;\n" +
	                  "statement : 'x' | ifStatement;\n" +
	                  "ifStatement : 'if' 'y' statement ('else' statement)? {\n" +
	                  "console.log($text);\n" +
	                  "};\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "WS : (' '|'\\n') -> channel(HIDDEN);";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "start", "if y if y x else x", false);
		assertEquals("if y x else x\nif y if y x else x\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testIfIfElseGreedyBinding2() throws Exception {
		String grammar = "grammar T;\n" +
	                  "start : statement+ ;\n" +
	                  "statement : 'x' | ifStatement;\n" +
	                  "ifStatement : 'if' 'y' statement ('else' statement|) {\n" +
	                  "console.log($text);\n" +
	                  "};\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "WS : (' '|'\\n') -> channel(HIDDEN);";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "start", "if y if y x else x", false);
		assertEquals("if y x else x\nif y if y x else x\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testIfIfElseNonGreedyBinding1() throws Exception {
		String grammar = "grammar T;\n" +
	                  "start : statement+ ;\n" +
	                  "statement : 'x' | ifStatement;\n" +
	                  "ifStatement : 'if' 'y' statement ('else' statement)?? {\n" +
	                  "console.log($text);\n" +
	                  "};\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "WS : (' '|'\\n') -> channel(HIDDEN);";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "start", "if y if y x else x", false);
		assertEquals("if y x\nif y if y x else x\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testIfIfElseNonGreedyBinding2() throws Exception {
		String grammar = "grammar T;\n" +
	                  "start : statement+ ;\n" +
	                  "statement : 'x' | ifStatement;\n" +
	                  "ifStatement : 'if' 'y' statement (|'else' statement) {\n" +
	                  "console.log($text);\n" +
	                  "};\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "WS : (' '|'\\n') -> channel(HIDDEN);";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "start", "if y if y x else x", false);
		assertEquals("if y x\nif y if y x else x\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testAStar(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : ID* {\n" +
	                  "console.log($text);\n" +
	                  "};\n" +
	                  "ID : 'a'..'z'+;\n" +
	                  "WS : (' '|'\\n') -> skip;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testAStar_1() throws Exception {
		String found = testAStar("");
		assertEquals("\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testAStar_2() throws Exception {
		String found = testAStar("a b c");
		assertEquals("abc\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testLL1OptionalBlock(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : (ID|{}INT)? {\n" +
	                  "console.log($text);\n" +
	                  "};\n" +
	                  "ID : 'a'..'z'+;\n" +
	                  "INT : '0'..'9'+ ;\n" +
	                  "WS : (' '|'\\n') -> skip;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testLL1OptionalBlock_1() throws Exception {
		String found = testLL1OptionalBlock("");
		assertEquals("\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testLL1OptionalBlock_2() throws Exception {
		String found = testLL1OptionalBlock("a");
		assertEquals("a\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testAorAStar(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : (ID|ID)* {\n" +
	                  "console.log($text);\n" +
	                  "};\n" +
	                  "ID : 'a'..'z'+;\n" +
	                  "WS : (' '|'\\n') -> skip;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testAorAStar_1() throws Exception {
		String found = testAorAStar("");
		assertEquals("\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testAorAStar_2() throws Exception {
		String found = testAorAStar("a b c");
		assertEquals("abc\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testAorBPlus() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : (ID|INT{\n" +
	                  "})+ {\n" +
	                  "console.log($text);\n" +
	                  "};\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "a 34 c", false);
		assertEquals("a34c\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testAorBStar(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : (ID|INT{\n" +
	                  "})* {\n" +
	                  "console.log($text);\n" +
	                  "};\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testAorBStar_1() throws Exception {
		String found = testAorBStar("");
		assertEquals("\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testAorBStar_2() throws Exception {
		String found = testAorBStar("a 34 c");
		assertEquals("a34c\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testOptional(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "stat : ifstat | 'x';\n" +
	                  "ifstat : 'if' stat ('else' stat)?;\n" +
	                  "WS : [ \\n\\t]+ -> skip ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "stat", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testOptional_1() throws Exception {
		String found = testOptional("x");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testOptional_2() throws Exception {
		String found = testOptional("if x");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testOptional_3() throws Exception {
		String found = testOptional("if x else x");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testOptional_4() throws Exception {
		String found = testOptional("if if x else x");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testPredicatedIfIfElse() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : stmt EOF ;\n" +
	                  "stmt : ifStmt | ID;\n" +
	                  "ifStmt : 'if' ID stmt ('else' stmt | { this._input.LA(1)!=TParser.ELSE }?);\n" +
	                  "ELSE : 'else';\n" +
	                  "ID : [a-zA-Z]+;\n" +
	                  "WS : [ \\n\\t]+ -> skip;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "if x if x a else b", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testLabelAliasingAcrossLabeledAlternatives() throws Exception {
		String grammar = "grammar T;\n" +
	                  "start : a* EOF;\n" +
	                  "a\n" +
	                  "  : label=subrule {console.log($label.text);} #One\n" +
	                  "  | label='y' {console.log($label.text);} #Two\n" +
	                  "  ;\n" +
	                  "subrule : 'x';\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "start", "xy", false);
		assertEquals("x\ny\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testPredictionIssue334() throws Exception {
		String grammar = "grammar T;\n" +
	                  "file_ @init{\n" +
	                  "this._errHandler = new antlr4.error.BailErrorStrategy();\n" +
	                  "} \n" +
	                  "@after {\n" +
	                  "console.log($ctx.toStringTree(null, this));\n" +
	                  "}\n" +
	                  "  :   item (SEMICOLON item)* SEMICOLON? EOF ;\n" +
	                  "item : A B?;\n" +
	                  "SEMICOLON: ';';\n" +
	                  "A : 'a'|'A';\n" +
	                  "B : 'b'|'B';\n" +
	                  "WS      : [ \\r\\t\\n]+ -> skip;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "file_", "a", false);
		assertEquals("(file_ (item a) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testListLabelForClosureContext() throws Exception {
		String grammar = "grammar T;\n" +
	                  "ifStatement\n" +
	                  "@after {\n" +
	                  "var items = $ctx.elseIfStatement(); \n" +
	                  "}\n" +
	                  "    : 'if' expression\n" +
	                  "      ( ( 'then'\n" +
	                  "          executableStatement*\n" +
	                  "          elseIfStatement*  // <--- problem is here\n" +
	                  "          elseStatement?\n" +
	                  "          'end' 'if'\n" +
	                  "        ) | executableStatement )\n" +
	                  "    ;\n" +
	                  "\n" +
	                  "elseIfStatement\n" +
	                  "    : 'else' 'if' expression 'then' executableStatement*\n" +
	                  "    ;\n" +
	                  "expression : 'a' ;\n" +
	                  "executableStatement : 'a' ;\n" +
	                  "elseStatement : 'a' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "expression", "a", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testMultipleEOFHandling() throws Exception {
		String grammar = "grammar T;\n" +
	                  "prog : ('x' | 'x' 'y') EOF EOF;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "prog", "x", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testEOFInClosure() throws Exception {
		String grammar = "grammar T;\n" +
	                  "prog : stat EOF;\n" +
	                  "stat : 'x' ('y' | EOF)*?;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "prog", "x", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testReferenceToATN(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : (ID|ATN_)* ATN_? {console.log($text);} ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "ATN_ : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testReferenceToATN_1() throws Exception {
		String found = testReferenceToATN("");
		assertEquals("\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testReferenceToATN_2() throws Exception {
		String found = testReferenceToATN("a 34 c");
		assertEquals("a34c\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testParserProperty() throws Exception {
		String grammar = "grammar T;\n" +
	                  "@members {\n" +
	                  "this.Property = function() {\n" +
	                  "    return true;\n" +
	                  "}\n" +
	                  "}\n" +
	                  "a : {$parser.Property()}? ID {console.log(\"valid\");}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "abc", false);
		assertEquals("valid\n", found);
		assertNull(this.stderrDuringParse);
	}


}