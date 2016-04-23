/*
 * [The "BSD license"]
 *  Copyright (c) 2014 Terence Parr
 *  Copyright (c) 2014 Sam Harwell
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
package org.antlr.v4.test.tool;

import org.antlr.runtime.Token;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.test.runtime.java.BaseTest;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.ast.GrammarAST;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestTokenPositionOptions extends BaseTest {
	@Test public void testLeftRecursionRewrite() throws Exception {
		Grammar g = new Grammar(
				"grammar T;\n" +
				"s : e ';' ;\n" +
				"e : e '*' e\n" +
				"  | e '+' e\n" +
				"  | e '.' ID\n" +
				"  | '-' e\n" +
				"  | ID\n" +
				"  ;\n" +
				"ID : [a-z]+ ;\n"
		);

		String expectedTree =
				"(COMBINED_GRAMMAR T (RULES (RULE s (BLOCK (ALT e ';'))) (RULE e (BLOCK (ALT (BLOCK (ALT {} ('-' (ELEMENT_OPTIONS (= tokenIndex 43))) (e (ELEMENT_OPTIONS (= tokenIndex 45) (= p 2)))) (ALT (ID (ELEMENT_OPTIONS (= tokenIndex 49))))) (* (BLOCK (ALT ({precpred(_ctx, 5)}? (ELEMENT_OPTIONS (= p 5))) ('*' (ELEMENT_OPTIONS (= tokenIndex 21))) (e (ELEMENT_OPTIONS (= tokenIndex 23) (= p 6)))) (ALT ({precpred(_ctx, 4)}? (ELEMENT_OPTIONS (= p 4))) ('+' (ELEMENT_OPTIONS (= tokenIndex 29))) (e (ELEMENT_OPTIONS (= tokenIndex 31) (= p 5)))) (ALT ({precpred(_ctx, 3)}? (ELEMENT_OPTIONS (= p 3))) ('.' (ELEMENT_OPTIONS (= tokenIndex 37))) (ID (ELEMENT_OPTIONS (= tokenIndex 39)))))))))))";
		assertEquals(expectedTree, g.ast.toStringTree());

		String expectedElementTokens =
				"[@5,11:11='s',<57>,2:0]\n" +
				"[@9,15:15='e',<57>,2:4]\n" +
				"[@11,17:19='';'',<62>,2:6]\n" +
				"[@15,23:23='e',<57>,3:0]\n" +
				"[@43,64:66=''-'',<62>,6:4]\n" +
				"[@45,68:68='e',<57>,6:8]\n" +
				"[@49,74:75='ID',<66>,7:4]\n" +
				"[@21,29:31=''*'',<62>,3:6]\n" +
				"[@23,33:33='e',<57>,3:10]\n" +
				"[@29,41:43=''+'',<62>,4:6]\n" +
				"[@31,45:45='e',<57>,4:10]\n" +
				"[@37,53:55=''.'',<62>,5:6]\n" +
				"[@39,57:58='ID',<66>,5:10]";

		IntervalSet types =
				new IntervalSet(ANTLRParser.TOKEN_REF,
				ANTLRParser.STRING_LITERAL,
				ANTLRParser.RULE_REF);
		List<GrammarAST> nodes = g.ast.getNodesWithTypePreorderDFS(types);
		List<Token> tokens = new ArrayList<Token>();
		for (GrammarAST node : nodes) {
			tokens.add(node.getToken());
		}
		assertEquals(expectedElementTokens, Utils.join(tokens.toArray(), "\n"));
	}

	@Test public void testLeftRecursionWithLabels() throws Exception {
		Grammar g = new Grammar(
				"grammar T;\n" +
				"s : e ';' ;\n" +
				"e : e '*' x=e\n" +
				"  | e '+' e\n" +
				"  | e '.' y=ID\n" +
				"  | '-' e\n" +
				"  | ID\n" +
				"  ;\n" +
				"ID : [a-z]+ ;\n"
		);

		String expectedTree =
				"(COMBINED_GRAMMAR T (RULES (RULE s (BLOCK (ALT e ';'))) (RULE e (BLOCK (ALT (BLOCK (ALT {} ('-' (ELEMENT_OPTIONS (= tokenIndex 47))) (e (ELEMENT_OPTIONS (= tokenIndex 49) (= p 2)))) (ALT (ID (ELEMENT_OPTIONS (= tokenIndex 53))))) (* (BLOCK (ALT ({precpred(_ctx, 5)}? (ELEMENT_OPTIONS (= p 5))) ('*' (ELEMENT_OPTIONS (= tokenIndex 21))) (= x (e (ELEMENT_OPTIONS (= tokenIndex 25) (= p 6))))) (ALT ({precpred(_ctx, 4)}? (ELEMENT_OPTIONS (= p 4))) ('+' (ELEMENT_OPTIONS (= tokenIndex 31))) (e (ELEMENT_OPTIONS (= tokenIndex 33) (= p 5)))) (ALT ({precpred(_ctx, 3)}? (ELEMENT_OPTIONS (= p 3))) ('.' (ELEMENT_OPTIONS (= tokenIndex 39))) (= y (ID (ELEMENT_OPTIONS (= tokenIndex 43))))))))))))";
		assertEquals(expectedTree, g.ast.toStringTree());

		String expectedElementTokens =
				"[@5,11:11='s',<57>,2:0]\n" +
				"[@9,15:15='e',<57>,2:4]\n" +
				"[@11,17:19='';'',<62>,2:6]\n" +
				"[@15,23:23='e',<57>,3:0]\n" +
				"[@47,68:70=''-'',<62>,6:4]\n" +
				"[@49,72:72='e',<57>,6:8]\n" +
				"[@53,78:79='ID',<66>,7:4]\n" +
				"[@21,29:31=''*'',<62>,3:6]\n" +
				"[@25,35:35='e',<57>,3:12]\n" +
				"[@31,43:45=''+'',<62>,4:6]\n" +
				"[@33,47:47='e',<57>,4:10]\n" +
				"[@39,55:57=''.'',<62>,5:6]\n" +
				"[@43,61:62='ID',<66>,5:12]";

		IntervalSet types =
				new IntervalSet(ANTLRParser.TOKEN_REF,
				ANTLRParser.STRING_LITERAL,
				ANTLRParser.RULE_REF);
		List<GrammarAST> nodes = g.ast.getNodesWithTypePreorderDFS(types);
		List<Token> tokens = new ArrayList<Token>();
		for (GrammarAST node : nodes) {
			tokens.add(node.getToken());
		}
		assertEquals(expectedElementTokens, Utils.join(tokens.toArray(), "\n"));
	}

	@Test public void testLeftRecursionWithSet() throws Exception {
		Grammar g = new Grammar(
				"grammar T;\n" +
				"s : e ';' ;\n" +
				"e : e op=('*'|'/') e\n" +
				"  | e '+' e\n" +
				"  | e '.' ID\n" +
				"  | '-' e\n" +
				"  | ID\n" +
				"  ;\n" +
				"ID : [a-z]+ ;\n"
		);

		String expectedTree =
				"(COMBINED_GRAMMAR T (RULES (RULE s (BLOCK (ALT e ';'))) (RULE e (BLOCK (ALT (BLOCK (ALT {} ('-' (ELEMENT_OPTIONS (= tokenIndex 49))) (e (ELEMENT_OPTIONS (= tokenIndex 51) (= p 2)))) (ALT (ID (ELEMENT_OPTIONS (= tokenIndex 55))))) (* (BLOCK (ALT ({precpred(_ctx, 5)}? (ELEMENT_OPTIONS (= p 5))) (= op (SET ('*' (ELEMENT_OPTIONS (= tokenIndex 24))) ('/' (ELEMENT_OPTIONS (= tokenIndex 26))))) (e (ELEMENT_OPTIONS (= tokenIndex 29) (= p 6)))) (ALT ({precpred(_ctx, 4)}? (ELEMENT_OPTIONS (= p 4))) ('+' (ELEMENT_OPTIONS (= tokenIndex 35))) (e (ELEMENT_OPTIONS (= tokenIndex 37) (= p 5)))) (ALT ({precpred(_ctx, 3)}? (ELEMENT_OPTIONS (= p 3))) ('.' (ELEMENT_OPTIONS (= tokenIndex 43))) (ID (ELEMENT_OPTIONS (= tokenIndex 45)))))))))))";
		assertEquals(expectedTree, g.ast.toStringTree());

		String expectedElementTokens =
				"[@5,11:11='s',<57>,2:0]\n" +
				"[@9,15:15='e',<57>,2:4]\n" +
				"[@11,17:19='';'',<62>,2:6]\n" +
				"[@15,23:23='e',<57>,3:0]\n" +
				"[@49,73:75=''-'',<62>,6:4]\n" +
				"[@51,77:77='e',<57>,6:8]\n" +
				"[@55,83:84='ID',<66>,7:4]\n" +
				"[@24,33:35=''*'',<62>,3:10]\n" +
				"[@26,37:39=''/'',<62>,3:14]\n" +
				"[@29,42:42='e',<57>,3:19]\n" +
				"[@35,50:52=''+'',<62>,4:6]\n" +
				"[@37,54:54='e',<57>,4:10]\n" +
				"[@43,62:64=''.'',<62>,5:6]\n" +
				"[@45,66:67='ID',<66>,5:10]";

		IntervalSet types =
				new IntervalSet(ANTLRParser.TOKEN_REF,
				ANTLRParser.STRING_LITERAL,
				ANTLRParser.RULE_REF);
		List<GrammarAST> nodes = g.ast.getNodesWithTypePreorderDFS(types);
		List<Token> tokens = new ArrayList<Token>();
		for (GrammarAST node : nodes) {
			tokens.add(node.getToken());
		}
		assertEquals(expectedElementTokens, Utils.join(tokens.toArray(), "\n"));
	}

}
