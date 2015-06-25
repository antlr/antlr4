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
package org.antlr.v4.test.tool;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.InterpreterRuleContext;
import org.antlr.v4.runtime.LexerInterpreter;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Trees;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.GrammarParserInterpreter;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/** Tests to ensure GrammarParserInterpreter subclass of ParserInterpreter
 *  hasn't messed anything up.
 */
public class TestGrammarParserInterpreter {
	public static final String lexerText = "lexer grammar L;\n" +
										   "PLUS : '+' ;\n" +
										   "MULT : '*' ;\n" +
										   "ID : [a-z]+ ;\n" +
										   "INT : [0-9]+ ;\n" +
										   "WS : [ \\r\\t\\n]+ ;\n";

	@Test
	public void testAlts() throws Exception {
		LexerGrammar lg = new LexerGrammar(lexerText);
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : ID\n"+
			"  | INT{;}\n"+
			"  ;\n",
			lg);
		testInterp(lg, g, "s", "a",		"(s:1 a)");
		testInterp(lg, g, "s", "3", 	"(s:2 3)");
	}

	@Test
	public void testAltsAsSet() throws Exception {
		LexerGrammar lg = new LexerGrammar(lexerText);
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : ID\n"+
			"  | INT\n"+
			"  ;\n",
			lg);
		testInterp(lg, g, "s", "a",		"(s:1 a)");
		testInterp(lg, g, "s", "3", 	"(s:1 3)");
	}

	@Test
	public void testAltsWithLabels() throws Exception {
		LexerGrammar lg = new LexerGrammar(lexerText);
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : ID  # foo\n" +
			"  | INT # bar\n" +
			"  ;\n",
			lg);
		// it won't show the labels here because my simple node text provider above just shows the alternative
		testInterp(lg, g, "s", "a",		"(s:1 a)");
		testInterp(lg, g, "s", "3", 	"(s:2 3)");
	}

	@Test
	public void testOneAlt() throws Exception {
		LexerGrammar lg = new LexerGrammar(lexerText);
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : ID\n"+
			"  ;\n",
			lg);
		testInterp(lg, g, "s", "a",		"(s:1 a)");
	}


	@Test
	public void testLeftRecursionWithMultiplePrimaryAndRecursiveOps() throws Exception {
		LexerGrammar lg = new LexerGrammar(lexerText);
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : e EOF ;\n" +
			"e : e MULT e\n" +
			"  | e PLUS e\n" +
			"  | INT\n" +
			"  | ID\n" +
			"  ;\n",
			lg);

		testInterp(lg, g, "s", "a",		"(s:1 (e:4 a) <EOF>)");
		testInterp(lg, g, "e", "a",		"(e:4 a)");
		testInterp(lg, g, "e", "34",	"(e:3 34)");
		testInterp(lg, g, "e", "a+1",	"(e:2 (e:4 a) + (e:3 1))");
		testInterp(lg, g, "e", "1+2*a",	"(e:2 (e:3 1) + (e:1 (e:3 2) * (e:4 a)))");
	}

	InterpreterRuleContext testInterp(LexerGrammar lg, Grammar g,
	                                  String startRule, String input,
	                                  String expectedParseTree)
	{
		LexerInterpreter lexEngine = lg.createLexerInterpreter(new ANTLRInputStream(input));
		CommonTokenStream tokens = new CommonTokenStream(lexEngine);
		GrammarParserInterpreter parser = g.createGrammarParserInterpreter(tokens);
		ParseTree t = parser.parse(g.rules.get(startRule).index);
		InterpreterTreeTextProvider nodeTextProvider = new InterpreterTreeTextProvider(g.getRuleNames());
		String treeStr = Trees.toStringTree(t, nodeTextProvider);
		System.out.println("parse tree: "+treeStr);
		assertEquals(expectedParseTree, treeStr);
		return (InterpreterRuleContext)t;
	}
}
