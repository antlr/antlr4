/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
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

package org.antlr.v4.js.node.test;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestSemPredEvalLexer extends BaseTest {

	@Test public void testDisableRule() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"E1 : 'enum' { false }? ;\n" +
			"E2 : 'enum' { true }? ;\n" +  // winner not E1 or ID
			"ID : 'a'..'z'+ ;\n"+
			"WS : (' '|'\\n') -> skip ;";
		String found = execLexer("L.g4", grammar, "L", "enum abc", true);
		String expecting =
			"[@0,0:3='enum',<2>,1:0]\n" +
			"[@1,5:7='abc',<3>,1:5]\n" +
			"[@2,8:7='<EOF>',<-1>,1:8]\n" +
			"s0-' '->:s5=>4\n" +
			"s0-'a'->:s6=>3\n" +
			"s0-'e'->:s1=>3\n" +
			":s1=>3-'n'->:s2=>3\n" +
			":s2=>3-'u'->:s3=>3\n" +
			":s6=>3-'b'->:s6=>3\n" +
			":s6=>3-'c'->:s6=>3\n";
		assertEquals(expecting, found);
	}

	@Test public void testIDvsEnum() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"ENUM : 'enum' { false }? ;\n" +
			"ID : 'a'..'z'+ ;\n"+
			"WS : (' '|'\\n') -> skip ;";
		String found = execLexer("L.g4", grammar, "L", "enum abc enum", true);
		String expecting =
			"[@0,0:3='enum',<2>,1:0]\n" +
			"[@1,5:7='abc',<2>,1:5]\n" +
			"[@2,9:12='enum',<2>,1:9]\n" +
			"[@3,13:12='<EOF>',<-1>,1:13]\n" +
			"s0-' '->:s5=>3\n" +
			"s0-'a'->:s4=>2\n" +
			"s0-'e'->:s1=>2\n" +
			":s1=>2-'n'->:s2=>2\n" +
			":s2=>2-'u'->:s3=>2\n" +
			":s4=>2-'b'->:s4=>2\n" +
			":s4=>2-'c'->:s4=>2\n"; // no 'm'-> transition...conflicts with pred
		assertEquals(expecting, found);
	}

	@Test public void testIDnotEnum() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"ENUM : [a-z]+ { false }? ;\n" +
			"ID   : [a-z]+ ;\n"+
			"WS : (' '|'\\n') -> skip ;";
		String found = execLexer("L.g4", grammar, "L", "enum abc enum", true);
		String expecting =
			"[@0,0:3='enum',<2>,1:0]\n" +
			"[@1,5:7='abc',<2>,1:5]\n" +
			"[@2,9:12='enum',<2>,1:9]\n" +
			"[@3,13:12='<EOF>',<-1>,1:13]\n" +
			"s0-' '->:s2=>3\n"; // no edges in DFA for enum/id. all paths lead to pred.
		assertEquals(expecting, found);
	}

	@Test public void testEnumNotID() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"ENUM : [a-z]+ { this.text===\"enum\" }? ;\n" +
			"ID   : [a-z]+ ;\n"+
			"WS : (' '|'\\n') -> skip ;";
		String found = execLexer("L.g4", grammar, "L", "enum abc enum", true);
		String expecting =
			"[@0,0:3='enum',<1>,1:0]\n" +
			"[@1,5:7='abc',<2>,1:5]\n" +
			"[@2,9:12='enum',<1>,1:9]\n" +
			"[@3,13:12='<EOF>',<-1>,1:13]\n" +
			"s0-' '->:s3=>3\n"; // no edges in DFA for enum/id. all paths lead to pred.
		assertEquals(expecting, found);
	}

	@Test public void testIndent() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"ID : [a-z]+ ;\n"+
			"INDENT : [ \\t]+ { this._tokenStartColumn===0 }? \n" +
			"         { console.log(\"INDENT\"); }  ;"+
			"NL     : '\\n' ;"+
			"WS     : [ \\t]+ ;";
		String found = execLexer("L.g4", grammar, "L", "abc\n  def  \n", true);
		String expecting =
			"INDENT\n" +                        // action output
			"[@0,0:2='abc',<1>,1:0]\n" +		// ID
			"[@1,3:3='\\n',<3>,1:3]\n" +  		// NL
			"[@2,4:5='  ',<2>,2:0]\n" +			// INDENT
			"[@3,6:8='def',<1>,2:2]\n" +		// ID
			"[@4,9:10='  ',<4>,2:5]\n" +		// WS
			"[@5,11:11='\\n',<3>,2:7]\n" +
			"[@6,12:11='<EOF>',<-1>,3:8]\n" +
			"s0-'\n" +
			"'->:s2=>3\n" +
			"s0-'a'->:s1=>1\n" +
			"s0-'d'->:s1=>1\n" +
			":s1=>1-'b'->:s1=>1\n" +
			":s1=>1-'c'->:s1=>1\n" +
			":s1=>1-'e'->:s1=>1\n" +
			":s1=>1-'f'->:s1=>1\n";
		assertEquals(expecting, found);
	}

	@Test public void testLexerInputPositionSensitivePredicates() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"WORD1 : ID1+ { console.log( this.text ); } ;\n"+
			"WORD2 : ID2+ { console.log( this.text ); } ;\n"+
			"fragment ID1 : { this.column < 2 }? [a-zA-Z];\n"+
			"fragment ID2 : { this.column >= 2 }? [a-zA-Z];\n"+
			"WS : (' '|'\\n') -> skip;\n";
		String found = execLexer("L.g4", grammar, "L", "a cde\nabcde\n");
		String expecting =
			"a\n" +
			"cde\n" +
			"ab\n" +
			"cde\n" +
			"[@0,0:0='a',<1>,1:0]\n" +
			"[@1,2:4='cde',<2>,1:2]\n" +
			"[@2,6:7='ab',<1>,2:0]\n" +
			"[@3,8:10='cde',<2>,2:2]\n" +
			"[@4,12:11='<EOF>',<-1>,3:0]\n";
		assertEquals(expecting, found);
	}

	@Test public void testPredicatedKeywords() {
		String grammar =
			"lexer grammar A;" +
			"ENUM : [a-z]+ { this.text===\"enum\" }? { console.log(\"enum!\"); } ;\n" +
			"ID   : [a-z]+ { console.log(\"ID \"+this.text); } ;\n" +
			"WS   : [ \\n] -> skip ;";
		String found = execLexer("A.g4", grammar, "A", "enum enu a");
		String expecting =
			"enum!\n" +
			"ID enu\n" +
			"ID a\n" +
			"[@0,0:3='enum',<1>,1:0]\n" +
			"[@1,5:7='enu',<2>,1:5]\n" +
			"[@2,9:9='a',<2>,1:9]\n" +
			"[@3,10:9='<EOF>',<-1>,1:10]\n";
		assertEquals(expecting, found);
	}
}
