/*
 * [The "BSD license"]
 *  Copyright (c) 2010 Terence Parr
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *      derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.antlr.test;

import org.junit.Test;

public class TestTreeParsing extends BaseTest {
	@Test public void testFlatList() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID INT;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

		String treeGrammar =
			"tree grammar TP; options {ASTLabelType=CommonTree;}\n" +
			"a : ID INT\n" +
			"    {System.out.println($ID+\", \"+$INT);}\n" +
			"  ;\n";

		String found = execTreeParser("T.g", grammar, "TParser", "TP.g",
				    treeGrammar, "TP", "TLexer", "a", "a", "abc 34");
		assertEquals("abc, 34\n", found);
	}

	@Test public void testSimpleTree() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID INT -> ^(ID INT);\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

		String treeGrammar =
			"tree grammar TP; options {ASTLabelType=CommonTree;}\n" +
			"a : ^(ID INT)\n" +
			"    {System.out.println($ID+\", \"+$INT);}\n" +
			"  ;\n";

		String found = execTreeParser("T.g", grammar, "TParser", "TP.g",
				    treeGrammar, "TP", "TLexer", "a", "a", "abc 34");
		assertEquals("abc, 34\n", found);
	}

	@Test public void testFlatVsTreeDecision() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : b c ;\n" +
			"b : ID INT -> ^(ID INT);\n" +
			"c : ID INT;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

		String treeGrammar =
			"tree grammar TP; options {ASTLabelType=CommonTree;}\n" +
			"a : b b ;\n" +
			"b : ID INT    {System.out.print($ID+\" \"+$INT);}\n" +
			"  | ^(ID INT) {System.out.print(\"^(\"+$ID+\" \"+$INT+')');}\n" +
			"  ;\n";

		String found = execTreeParser("T.g", grammar, "TParser", "TP.g",
				    treeGrammar, "TP", "TLexer", "a", "a", "a 1 b 2");
		assertEquals("^(a 1)b 2\n", found);
	}

	@Test public void testFlatVsTreeDecision2() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : b c ;\n" +
			"b : ID INT+ -> ^(ID INT+);\n" +
			"c : ID INT+;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

		String treeGrammar =
			"tree grammar TP; options {ASTLabelType=CommonTree;}\n" +
			"a : b b ;\n" +
			"b : ID INT+    {System.out.print($ID+\" \"+$INT);}\n" +
			"  | ^(x=ID (y=INT)+) {System.out.print(\"^(\"+$x+' '+$y+')');}\n" +
			"  ;\n";

		String found = execTreeParser("T.g", grammar, "TParser", "TP.g",
				    treeGrammar, "TP", "TLexer", "a", "a",
				    "a 1 2 3 b 4 5");
		assertEquals("^(a 3)b 5\n", found);
	}

	@Test public void testCyclicDFALookahead() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID INT+ PERIOD;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"SEMI : ';' ;\n"+
			"PERIOD : '.' ;\n"+
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

		String treeGrammar =
			"tree grammar TP; options {ASTLabelType=CommonTree;}\n" +
			"a : ID INT+ PERIOD {System.out.print(\"alt 1\");}"+
			"  | ID INT+ SEMI   {System.out.print(\"alt 2\");}\n" +
			"  ;\n";

		String found = execTreeParser("T.g", grammar, "TParser", "TP.g",
				    treeGrammar, "TP", "TLexer", "a", "a", "a 1 2 3.");
		assertEquals("alt 1\n", found);
	}

	@Test public void testTemplateOutput() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID INT;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

		String treeGrammar =
			"tree grammar TP;\n" +
			"options {output=template; ASTLabelType=CommonTree;}\n" +
			"s : a {System.out.println($a.st);};\n" +
			"a : ID INT -> {new StringTemplate($INT.text)}\n" +
			"  ;\n";

		String found = execTreeParser("T.g", grammar, "TParser", "TP.g",
				    treeGrammar, "TP", "TLexer", "a", "s", "abc 34");
		assertEquals("34\n", found);
	}

	@Test public void testNullableChildList() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID INT? -> ^(ID INT?);\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

		String treeGrammar =
			"tree grammar TP; options {ASTLabelType=CommonTree;}\n" +
			"a : ^(ID INT?)\n" +
			"    {System.out.println($ID);}\n" +
			"  ;\n";

		String found = execTreeParser("T.g", grammar, "TParser", "TP.g",
				    treeGrammar, "TP", "TLexer", "a", "a", "abc");
		assertEquals("abc\n", found);
	}

	@Test public void testNullableChildList2() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID INT? SEMI -> ^(ID INT?) SEMI ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"SEMI : ';' ;\n"+
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

		String treeGrammar =
			"tree grammar TP; options {ASTLabelType=CommonTree;}\n" +
			"a : ^(ID INT?) SEMI\n" +
			"    {System.out.println($ID);}\n" +
			"  ;\n";

		String found = execTreeParser("T.g", grammar, "TParser", "TP.g",
				    treeGrammar, "TP", "TLexer", "a", "a", "abc;");
		assertEquals("abc\n", found);
	}

	@Test public void testNullableChildList3() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : x=ID INT? (y=ID)? SEMI -> ^($x INT? $y?) SEMI ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"SEMI : ';' ;\n"+
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

		String treeGrammar =
			"tree grammar TP; options {ASTLabelType=CommonTree;}\n" +
			"a : ^(ID INT? b) SEMI\n" +
			"    {System.out.println($ID+\", \"+$b.text);}\n" +
			"  ;\n"+
			"b : ID? ;\n";

		String found = execTreeParser("T.g", grammar, "TParser", "TP.g",
				    treeGrammar, "TP", "TLexer", "a", "a", "abc def;");
		assertEquals("abc, def\n", found);
	}

	@Test public void testActionsAfterRoot() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : x=ID INT? SEMI -> ^($x INT?) ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"SEMI : ';' ;\n"+
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

		String treeGrammar =
			"tree grammar TP; options {ASTLabelType=CommonTree;}\n" +
			"a @init {int x=0;} : ^(ID {x=1;} {x=2;} INT?)\n" +
			"    {System.out.println($ID+\", \"+x);}\n" +
			"  ;\n";

		String found = execTreeParser("T.g", grammar, "TParser", "TP.g",
				    treeGrammar, "TP", "TLexer", "a", "a", "abc;");
		assertEquals("abc, 2\n", found);
	}

    @Test public void testWildcardLookahead() throws Exception {
        String grammar =
            "grammar T;\n" +
            "options {output=AST;}\n" +
            "a : ID '+'^ INT;\n" +
            "ID : 'a'..'z'+ ;\n" +
            "INT : '0'..'9'+;\n" +
            "SEMI : ';' ;\n"+
            "PERIOD : '.' ;\n"+
            "WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

        String treeGrammar =
            "tree grammar TP; options {tokenVocab=T; ASTLabelType=CommonTree;}\n" +
            "a : ^('+' . INT) {System.out.print(\"alt 1\");}"+
            "  ;\n";

        String found = execTreeParser("T.g", grammar, "TParser", "TP.g",
                    treeGrammar, "TP", "TLexer", "a", "a", "a + 2");
        assertEquals("alt 1\n", found);
    }

    @Test public void testWildcardLookahead2() throws Exception {
        String grammar =
            "grammar T;\n" +
            "options {output=AST;}\n" +
            "a : ID '+'^ INT;\n" +
            "ID : 'a'..'z'+ ;\n" +
            "INT : '0'..'9'+;\n" +
            "SEMI : ';' ;\n"+
            "PERIOD : '.' ;\n"+
            "WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

        String treeGrammar =
            "tree grammar TP; options {tokenVocab=T; ASTLabelType=CommonTree;}\n" +
            "a : ^('+' . INT) {System.out.print(\"alt 1\");}"+
            "  | ^('+' . .)   {System.out.print(\"alt 2\");}\n" +
            "  ;\n";

        // AMBIG upon '+' DOWN INT UP etc.. but so what.

        String found = execTreeParser("T.g", grammar, "TParser", "TP.g",
                    treeGrammar, "TP", "TLexer", "a", "a", "a + 2");
        assertEquals("alt 1\n", found);
    }

    @Test public void testWildcardLookahead3() throws Exception {
        String grammar =
            "grammar T;\n" +
            "options {output=AST;}\n" +
            "a : ID '+'^ INT;\n" +
            "ID : 'a'..'z'+ ;\n" +
            "INT : '0'..'9'+;\n" +
            "SEMI : ';' ;\n"+
            "PERIOD : '.' ;\n"+
            "WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

        String treeGrammar =
            "tree grammar TP; options {tokenVocab=T; ASTLabelType=CommonTree;}\n" +
            "a : ^('+' ID INT) {System.out.print(\"alt 1\");}"+
            "  | ^('+' . .)   {System.out.print(\"alt 2\");}\n" +
            "  ;\n";

        // AMBIG upon '+' DOWN INT UP etc.. but so what.

        String found = execTreeParser("T.g", grammar, "TParser", "TP.g",
                    treeGrammar, "TP", "TLexer", "a", "a", "a + 2");
        assertEquals("alt 1\n", found);
    }

    @Test public void testWildcardPlusLookahead() throws Exception {
        String grammar =
            "grammar T;\n" +
            "options {output=AST;}\n" +
            "a : ID '+'^ INT;\n" +
            "ID : 'a'..'z'+ ;\n" +
            "INT : '0'..'9'+;\n" +
            "SEMI : ';' ;\n"+
            "PERIOD : '.' ;\n"+
            "WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

        String treeGrammar =
            "tree grammar TP; options {tokenVocab=T; ASTLabelType=CommonTree;}\n" +
            "a : ^('+' INT INT ) {System.out.print(\"alt 1\");}"+
            "  | ^('+' .+)   {System.out.print(\"alt 2\");}\n" +
            "  ;\n";

        // AMBIG upon '+' DOWN INT UP etc.. but so what.

        String found = execTreeParser("T.g", grammar, "TParser", "TP.g",
                    treeGrammar, "TP", "TLexer", "a", "a", "a + 2");
        assertEquals("alt 2\n", found);
    }

}
