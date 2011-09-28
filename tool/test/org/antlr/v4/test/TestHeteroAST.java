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
package org.antlr.v4.test;

import org.junit.Test;

/** Test hetero trees in parsers and tree parsers */
public class TestHeteroAST extends BaseTest {
	protected boolean debug = false;

	// PARSERS -- AUTO AST

    @Test public void testToken() throws Exception {
        String grammar =
            "grammar T;\n" +
            "options {output=AST;}\n" +
            "@members {static class V extends CommonAST {\n" +
            "  public V(Token t) { token=t;}\n" +
            "  public String toString() { return token.getText()+\"<V>\";}\n" +
            "}\n" +
            "}\n"+
            "a : ID<V> ;\n"+
            "ID : 'a'..'z'+ ;\n" +
            "WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
        String found = execParser("T.g", grammar, "TParser", "TLexer",
                    "a", "a", debug);
        assertEquals("a<V>\n", found);
    }

	@Test public void testTokenCommonAST() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID<CommonAST> ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
					"a", "a", debug);
		assertEquals("a\n", found);
	}

    @Test public void testTokenWithQualifiedType() throws Exception {
        String grammar =
            "grammar T;\n" +
            "options {output=AST;}\n" +
            "@members {static class V extends CommonAST {\n" +
            "  public V(Token t) { token=t;}\n" +
            "  public String toString() { return token.getText()+\"<V>\";}\n" +
            "}\n" +
            "}\n"+
            "a : ID<TParser.V> ;\n"+ // TParser.V is qualified name
            "ID : 'a'..'z'+ ;\n" +
            "WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
        String found = execParser("T.g", grammar, "TParser", "TLexer",
                    "a", "a", debug);
        assertEquals("a<V>\n", found);
    }

	@Test public void testNamedType() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"@members {static class V extends CommonAST {\n" +
			"  public V(Token t) { token=t;}\n" +
			"  public String toString() { return token.getText()+\"<V>\";}\n" +
			"}\n" +
			"}\n"+
			"a : ID<node=V> ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
					"a", "a", debug);
		assertEquals("a<V>\n", found);
	}


	@Test public void testTokenWithLabel() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"@members {static class V extends CommonAST {\n" +
			"  public V(Token t) { token=t;}\n" +
			"  public String toString() { return token.getText()+\"<V>\";}\n" +
			"}\n" +
			"}\n"+
			"a : x=ID<V> ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a", debug);
		assertEquals("a<V>\n", found);
	}

	@Test public void testTokenWithListLabel() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"@members {static class V extends CommonAST {\n" +
			"  public V(Token t) { token=t;}\n" +
			"  public String toString() { return token.getText()+\"<V>\";}\n" +
			"}\n" +
			"}\n"+
			"a : x+=ID<V> ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a", debug);
		assertEquals("a<V>\n", found);
	}

	@Test public void testTokenRoot() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"@members {static class V extends CommonAST {\n" +
			"  public V(Token t) { token=t;}\n" +
			"  public String toString() { return token.getText()+\"<V>\";}\n" +
			"}\n" +
			"}\n"+
			"a : ID<V>^ ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a", debug);
		assertEquals("a<V>\n", found);
	}

	@Test public void testTokenRootWithListLabel() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"@members {static class V extends CommonAST {\n" +
			"  public V(Token t) { token=t;}\n" +
			"  public String toString() { return token.getText()+\"<V>\";}\n" +
			"}\n" +
			"}\n"+
			"a : x+=ID<V>^ ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a", debug);
		assertEquals("a<V>\n", found);
	}

	@Test public void testString() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"@members {static class V extends CommonAST {\n" +
			"  public V(Token t) { token=t;}\n" +
			"  public String toString() { return token.getText()+\"<V>\";}\n" +
			"}\n" +
			"}\n"+
			"a : 'begin'<V> ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "begin", debug);
		assertEquals("begin<V>\n", found);
	}

	@Test public void testStringRoot() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"@members {static class V extends CommonAST {\n" +
			"  public V(Token t) { token=t;}\n" +
			"  public String toString() { return token.getText()+\"<V>\";}\n" +
			"}\n" +
			"}\n"+
			"a : 'begin'<V>^ ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "begin", debug);
		assertEquals("begin<V>\n", found);
	}

	// PARSERS -- REWRITE AST

	@Test public void testRewriteToken() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"@members {static class V extends CommonAST {\n" +
			"  public V(Token t) { token=t;}\n" +
			"  public String toString() { return token.getText()+\"<V>\";}\n" +
			"}\n" +
			"}\n"+
			"a : ID -> ID<V> ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a", debug);
		assertEquals("a<V>\n", found);
	}

	@Test public void testRewriteTokenWithArgs() throws Exception {
		// arg to ID<V>[42,19,30] means you're constructing node not associated with ID
		// so must pass in token manually
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"@members {\n" +
			"static class V extends CommonAST {\n" +
			"  public int x,y,z;\n"+
			"  public V(int ttype, int x, int y, int z) { this.x=x; this.y=y; this.z=z; token=new CommonToken(ttype,\"\"); }\n" +
			"  public V(int ttype, Token t, int x) { token=t; this.x=x;}\n" +
			"  public String toString() { return (token!=null?token.getText():\"\")+\"<V>;\"+x+y+z;}\n" +
			"}\n" +
			"}\n"+
			"a : ID -> ID<V>[42,19,30] ID<V>[$ID,99] ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a", debug);
		assertEquals("<V>;421930 a<V>;9900\n", found);
	}

	@Test public void testRewriteTokenRoot() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"@members {static class V extends CommonAST {\n" +
			"  public V(Token t) { token=t;}\n" +
			"  public String toString() { return token.getText()+\"<V>\";}\n" +
			"}\n" +
			"}\n"+
			"a : ID INT -> ^(ID<V> INT) ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "a 2", debug);
		assertEquals("(a<V> 2)\n", found);
	}

	@Test public void testRewriteString() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"@members {static class V extends CommonAST {\n" +
			"  public V(Token t) { token=t;}\n" +
			"  public String toString() { return token.getText()+\"<V>\";}\n" +
			"}\n" +
			"}\n"+
			"a : 'begin' -> 'begin'<V> ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "begin", debug);
		assertEquals("begin<V>\n", found);
	}

	@Test public void testRewriteStringRoot() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"@members {static class V extends CommonAST {\n" +
			"  public V(Token t) { token=t;}\n" +
			"  public String toString() { return token.getText()+\"<V>\";}\n" +
			"}\n" +
			"}\n"+
			"a : 'begin' INT -> ^('begin'<V> INT) ;\n"+
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+ ;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
		String found = execParser("T.g", grammar, "TParser", "TLexer",
				    "a", "begin 2", debug);
		assertEquals("(begin<V> 2)\n", found);
	}

    @Test public void testRewriteRuleResults() throws Exception {
        String grammar =
            "grammar T;\n" +
            "options {output=AST;}\n" +
            "tokens {LIST;}\n" +
            "@members {\n" +
            "static class V extends CommonAST {\n" +
            "  public V(Token t) { token=t;}\n" +
            "  public String toString() { return token.getText()+\"<V>\";}\n" +
            "}\n" +
            "static class W extends CommonAST {\n" +
            "  public W(int tokenType, String txt) { super(new CommonToken(tokenType,txt)); }\n" +
            "  public W(Token t) { token=t;}\n" +
            "  public String toString() { return token.getText()+\"<W>\";}\n" +
            "}\n" +
            "}\n"+
            "a : id (',' id)* -> ^(LIST<W>[\"LIST\"] id+);\n" +
            "id : ID -> ID<V>;\n"+
            "ID : 'a'..'z'+ ;\n" +
            "WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
        String found = execParser("T.g", grammar, "TParser", "TLexer",
                    "a", "a,b,c", debug);
        assertEquals("(LIST<W> a<V> b<V> c<V>)\n", found);
    }

    @Test public void testCopySemanticsWithHetero() throws Exception {
        String grammar =
            "grammar T;\n" +
            "options {output=AST;}\n" +
            "@members {\n" +
            "static class V extends CommonAST {\n" +
            "  public V(Token t) { token=t;}\n" +  // for 'int'<V>
            "  public V(V node) { super(node); }\n\n" + // for dupNode
            "  public Tree dupNode() { return new V(this); }\n" + // for dup'ing type
            "  public String toString() { return token.getText()+\"<V>\";}\n" +
            "}\n" +
            "}\n" +
            "a : type ID (',' ID)* ';' -> ^(type ID)+;\n" +
            "type : 'int'<V> ;\n" +
            "ID : 'a'..'z'+ ;\n" +
            "INT : '0'..'9'+;\n" +
            "WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";
        String found = execParser("T.g", grammar, "TParser", "TLexer",
                    "a", "int a, b, c;", debug);
        assertEquals("(int<V> a) (int<V> b) (int<V> c)\n", found);
    }

    // TREE PARSERS -- REWRITE AST

	@Test public void testTreeParserRewriteFlatList() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID INT;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

		String treeGrammar =
			"tree grammar TP;\n"+
			"options {output=AST; ASTLabelType=CommonAST; tokenVocab=T;}\n" +
			"@members {\n" +
			"static class V extends CommonAST {\n" +
			"  public V(Object t) { super((CommonAST)t); }\n" +
			"  public String toString() { return token.getText()+\"<V>\";}\n" +
			"}\n" +
			"static class W extends CommonAST {\n" +
			"  public W(Object t) { super((CommonAST)t); }\n" +
			"  public String toString() { return token.getText()+\"<W>\";}\n" +
			"}\n" +
			"}\n"+
			"a : ID INT -> INT<V> ID<W>\n" +
			"  ;\n";

		String found = execTreeParser("T.g", grammar, "TParser", "TP.g",
				    treeGrammar, "TP", "TLexer", "a", "a", "abc 34");
		assertEquals("34<V> abc<W>\n", found);
	}

	@Test public void testTreeParserRewriteTree() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID INT;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

		String treeGrammar =
			"tree grammar TP;\n"+
			"options {output=AST; ASTLabelType=CommonAST; tokenVocab=T;}\n" +
			"@members {\n" +
			"static class V extends CommonAST {\n" +
			"  public V(Object t) { super((CommonAST)t); }\n" +
			"  public String toString() { return token.getText()+\"<V>\";}\n" +
			"}\n" +
			"static class W extends CommonAST {\n" +
			"  public W(Object t) { super((CommonAST)t); }\n" +
			"  public String toString() { return token.getText()+\"<W>\";}\n" +
			"}\n" +
			"}\n"+
			"a : ID INT -> ^(INT<V> ID<W>)\n" +
			"  ;\n";

		String found = execTreeParser("T.g", grammar, "TParser", "TP.g",
				    treeGrammar, "TP", "TLexer", "a", "a", "abc 34");
		assertEquals("(34<V> abc<W>)\n", found);
	}

	@Test public void testTreeParserRewriteImaginary() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

		String treeGrammar =
			"tree grammar TP;\n"+
			"options {output=AST; ASTLabelType=CommonAST; tokenVocab=T;}\n" +
			"tokens { ROOT; }\n" +
			"@members {\n" +
			"class V extends CommonAST {\n" +
			"  public V(int tokenType) { super(new CommonToken(tokenType)); }\n" +
			"  public String toString() { return tokenNames[token.getType()]+\"<V>\";}\n" +
			"}\n" +
			"}\n"+
			"a : ID -> ROOT<V> ID\n" +
			"  ;\n";

		String found = execTreeParser("T.g", grammar, "TParser", "TP.g",
				    treeGrammar, "TP", "TLexer", "a", "a", "abc");
		assertEquals("ROOT<V> abc\n", found);
	}

	@Test public void testTreeParserRewriteImaginaryWithArgs() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

		String treeGrammar =
			"tree grammar TP;\n"+
			"options {output=AST; ASTLabelType=CommonAST; tokenVocab=T;}\n" +
			"tokens { ROOT; }\n" +
			"@members {\n" +
			"class V extends CommonAST {\n" +
			"  public int x;\n" +
			"  public V(int tokenType, int x) { super(new CommonToken(tokenType)); this.x=x;}\n" +
			"  public String toString() { return tokenNames[token.getType()]+\"<V>;\"+x;}\n" +
			"}\n" +
			"}\n"+
			"a : ID -> ROOT<V>[42] ID\n" +
			"  ;\n";

		String found = execTreeParser("T.g", grammar, "TParser", "TP.g",
				    treeGrammar, "TP", "TLexer", "a", "a", "abc");
		assertEquals("ROOT<V>;42 abc\n", found);
	}

	@Test public void testTreeParserRewriteImaginaryRoot() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

		String treeGrammar =
			"tree grammar TP;\n"+
			"options {output=AST; ASTLabelType=CommonAST; tokenVocab=T;}\n" +
			"tokens { ROOT; }\n" +
			"@members {\n" +
			"class V extends CommonAST {\n" +
			"  public V(int tokenType) { super(new CommonToken(tokenType)); }\n" +
			"  public String toString() { return tokenNames[token.getType()]+\"<V>\";}\n" +
			"}\n" +
			"}\n"+
			"a : ID -> ^(ROOT<V> ID)\n" +
			"  ;\n";

		String found = execTreeParser("T.g", grammar, "TParser", "TP.g",
				    treeGrammar, "TP", "TLexer", "a", "a", "abc");
		assertEquals("(ROOT<V> abc)\n", found);
	}

	@Test public void testTreeParserRewriteImaginaryFromReal() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

		String treeGrammar =
			"tree grammar TP;\n"+
			"options {output=AST; ASTLabelType=CommonAST; tokenVocab=T;}\n" +
			"tokens { ROOT; }\n" +
			"@members {\n" +
			"class V extends CommonAST {\n" +
			"  public V(int tokenType) { super(new CommonToken(tokenType)); }\n" +
			"  public V(int tokenType, Object tree) { super((CommonAST)tree); token.setType(tokenType); }\n" +
			"  public String toString() { return tokenNames[token.getType()]+\"<V>@\"+token.getLine();}\n" +
			"}\n" +
			"}\n"+
			"a : ID -> ROOT<V>[$ID]\n" +
			"  ;\n";

		String found = execTreeParser("T.g", grammar, "TParser", "TP.g",
				    treeGrammar, "TP", "TLexer", "a", "a", "abc");
		assertEquals("ROOT<V>@1\n", found); // at line 1; shows copy of ID's stuff
	}

	@Test public void testTreeParserAutoHeteroAST() throws Exception {
		String grammar =
			"grammar T;\n" +
			"options {output=AST;}\n" +
			"a : ID ';' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : '0'..'9'+;\n" +
			"WS : (' '|'\\n') {$channel=HIDDEN;} ;\n";

		String treeGrammar =
			"tree grammar TP;\n"+
			"options {output=AST; ASTLabelType=CommonAST; tokenVocab=T;}\n" +
			"tokens { ROOT; }\n" +
			"@members {\n" +
			"class V extends CommonAST {\n" +
			"  public V(CommonAST t) { super(t); }\n" + // NEEDS SPECIAL CTOR
			"  public String toString() { return super.toString()+\"<V>\";}\n" +
			"}\n" +
			"}\n"+
			"a : ID<V> ';'<V>\n" +
			"  ;\n";

		String found = execTreeParser("T.g", grammar, "TParser", "TP.g",
				    treeGrammar, "TP", "TLexer", "a", "a", "abc;");
		assertEquals("abc<V> ;<V>\n", found);
	}

}
