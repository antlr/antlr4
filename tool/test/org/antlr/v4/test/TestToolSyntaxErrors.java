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

package org.antlr.v4.test;

import org.antlr.v4.tool.ErrorType;
import org.junit.Test;

public class TestToolSyntaxErrors extends BaseTest {
    static String[] A = {
        // INPUT
        "grammar A;\n" +
        "",
        // YIELDS
        "error(" + ErrorType.NO_RULES.code + "): A.g4::: grammar 'A' has no rules\n",

		"A;",
		"error(" + ErrorType.SYNTAX_ERROR.code + "): A.g4:1:0: syntax error: 'A' came as a complete surprise to me\n",

		"grammar ;",
		"error(" + ErrorType.SYNTAX_ERROR.code + "): A.g4:1:8: syntax error: ';' came as a complete surprise to me while looking for an identifier\n",

		"grammar A\n" +
		"a : ID ;\n",
		"error(" + ErrorType.SYNTAX_ERROR.code + "): A.g4:2:0: syntax error: missing SEMI at 'a'\n",

		"grammar A;\n" +
		"a : ID ;;\n"+
		"b : B ;",
		"error(" + ErrorType.SYNTAX_ERROR.code + "): A.g4:2:8: syntax error: ';' came as a complete surprise to me\n",

		"grammar A;;\n" +
		"a : ID ;\n",
		"error(" + ErrorType.SYNTAX_ERROR.code + "): A;.g4:1:10: syntax error: ';' came as a complete surprise to me\n",

		"grammar A;\n" +
		"a @init : ID ;\n",
		"error(" + ErrorType.SYNTAX_ERROR.code + "): A.g4:2:8: syntax error: mismatched input ':' expecting ACTION while matching rule preamble\n",

		"grammar A;\n" +
		"a  ( A | B ) D ;\n" +
		"b : B ;",
		"error(" + ErrorType.SYNTAX_ERROR.code + "): A.g4:2:3: syntax error: '(' came as a complete surprise to me while matching rule preamble\n" +
		"error(" + ErrorType.SYNTAX_ERROR.code + "): A.g4:2:11: syntax error: mismatched input ')' expecting SEMI while matching a rule\n" +
		"error(" + ErrorType.SYNTAX_ERROR.code + "): A.g4:2:15: syntax error: mismatched input ';' expecting COLON while matching a lexer rule\n",
    };

	@Test public void testA() { super.testErrors(A, true); }

	@Test public void testExtraColon() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"a : : A ;\n" +
			"b : B ;",
			"error(" + ErrorType.SYNTAX_ERROR.code + "): A.g4:2:4: syntax error: ':' came as a complete surprise to me while matching alternative\n",
		};
		super.testErrors(pair, true);
	}

	@Test public void testMissingRuleSemi() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"a : A \n" +
			"b : B ;",
			"error(" + ErrorType.SYNTAX_ERROR.code + "): A.g4:3:0: syntax error: unterminated rule (missing ';') detected at 'b :' while looking for rule element\n",
		};
		super.testErrors(pair, true);
	}

	@Test public void testMissingRuleSemi2() {
		String[] pair = new String[] {
			"lexer grammar A;\n" +
			"A : 'a' \n" +
			"B : 'b' ;",
			"error(" + ErrorType.SYNTAX_ERROR.code + "): A.g4:3:0: syntax error: unterminated rule (missing ';') detected at 'B :' while looking for lexer rule element\n",
		};
		super.testErrors(pair, true);
	}

	@Test public void testMissingRuleSemi3() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"a : A \n" +
			"b[int i] returns [int y] : B ;",
			"error(" + ErrorType.SYNTAX_ERROR.code + "): A.g4:3:9: syntax error: unterminated rule (missing ';') detected at 'returns int y' while looking for rule element\n"
		};
		super.testErrors(pair, true);
	}

	@Test public void testMissingRuleSemi4() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"a : b \n" +
			"  catch [Exception e] {...}\n" +
			"b : B ;\n",

			"error(" + ErrorType.SYNTAX_ERROR.code + "): A.g4:2:4: syntax error: unterminated rule (missing ';') detected at 'b catch' while looking for rule element\n"
		};
		super.testErrors(pair, true);
	}

	@Test public void testMissingRuleSemi5() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"a : A \n" +
			"  catch [Exception e] {...}\n",

			"error(" + ErrorType.SYNTAX_ERROR.code + "): A.g4:2:4: syntax error: unterminated rule (missing ';') detected at 'A catch' while looking for rule element\n"
		};
		super.testErrors(pair, true);
	}

	@Test public void testBadRulePrequelStart() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"a @ options {k=1;} : A ;\n" +
			"b : B ;",

			"error(" + ErrorType.SYNTAX_ERROR.code + "): A.g4:2:4: syntax error: 'options {' came as a complete surprise to me while looking for an identifier\n"
		};
		super.testErrors(pair, true);
	}

	@Test public void testBadRulePrequelStart2() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"a } : A ;\n" +
			"b : B ;",

			"error(" + ErrorType.SYNTAX_ERROR.code + "): A.g4:2:2: syntax error: '}' came as a complete surprise to me while matching rule preamble\n"
		};
		super.testErrors(pair, true);
	}

	@Test public void testModeInParser() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"a : A ;\n" +
			"mode foo;\n" +
			"b : B ;",

			"error(" + ErrorType.SYNTAX_ERROR.code + "): A.g4:4:0: syntax error: 'b' came as a complete surprise to me\n" +
			"error(" + ErrorType.SYNTAX_ERROR.code + "): A.g4:4:6: syntax error: mismatched input ';' expecting COLON while matching a lexer rule\n"
		};
		super.testErrors(pair, true);
	}

	/**
	 * This is a regression test for antlr/antlr4#243
	 * "Generate a good message for unterminated strings"
	 * https://github.com/antlr/antlr4/issues/243
	 */
	@Test public void testUnterminatedStringLiteral() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"a : 'x\n" +
			"  ;\n",

			"error(" + ErrorType.UNTERMINATED_STRING_LITERAL.code + "): A.g4:2:4: unterminated string literal\n"
		};
		super.testErrors(pair, true);
	}

	/**
	 * This is a regression test for antlr/antlr4#262
	 * "Parser Rule Name Starting With an Underscore"
	 * https://github.com/antlr/antlr4/issues/262
	 */
	@Test public void testParserRuleNameStartingWithUnderscore() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"_a : 'x' ;\n",

			"error(" + ErrorType.SYNTAX_ERROR.code + "): A.g4:2:0: syntax error: '_' came as a complete surprise to me\n"
		};
		super.testErrors(pair, true);
	}

	/**
	 * This is a regression test for antlr/antlr4#194
	 * "NullPointerException on 'options{}' in grammar file"
	 * https://github.com/antlr/antlr4/issues/194
	 */
	@Test public void testEmptyGrammarOptions() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"options {}\n" +
			"a : 'x' ;\n",

			""
		};
		super.testErrors(pair, true);
	}

	/**
	 * This is a "related" regression test for antlr/antlr4#194
	 * "NullPointerException on 'options{}' in grammar file"
	 * https://github.com/antlr/antlr4/issues/194
	 */
	@Test public void testEmptyRuleOptions() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"a options{} : 'x' ;\n",

			""
		};
		super.testErrors(pair, true);
	}

	/**
	 * This is a "related" regression test for antlr/antlr4#194
	 * "NullPointerException on 'options{}' in grammar file"
	 * https://github.com/antlr/antlr4/issues/194
	 */
	@Test public void testEmptyBlockOptions() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"a : (options{} : 'x') ;\n",

			""
		};
		super.testErrors(pair, true);
	}

	@Test public void testEmptyTokensBlock() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"tokens {}\n" +
			"a : 'x' ;\n",

			""
		};
		super.testErrors(pair, true);
	}

	/**
	 * This is a regression test for antlr/antlr4#190
	 * "NullPointerException building lexer grammar using bogus 'token' action"
	 * https://github.com/antlr/antlr4/issues/190
	 */
	@Test public void testInvalidLexerCommand() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"tokens{Foo}\n" +
			"b : Foo ;\n" +
			"X : 'foo' -> popmode;\n" + // "meant" to use -> popMode
			"Y : 'foo' -> token(Foo);", // "meant" to use -> type(Foo)

			"error(" + ErrorType.INVALID_LEXER_COMMAND.code + "): A.g4:4:13: lexer command 'popmode' does not exist or is not supported by the current target\n" +
			"error(" + ErrorType.INVALID_LEXER_COMMAND.code + "): A.g4:5:13: lexer command 'token' does not exist or is not supported by the current target\n"
		};
		super.testErrors(pair, true);
	}

	@Test public void testLexerCommandArgumentValidation() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"tokens{Foo}\n" +
			"b : Foo ;\n" +
			"X : 'foo' -> popMode(Foo);\n" + // "meant" to use -> popMode
			"Y : 'foo' -> type;", // "meant" to use -> type(Foo)

			"error(" + ErrorType.UNWANTED_LEXER_COMMAND_ARGUMENT.code + "): A.g4:4:13: lexer command 'popMode' does not take any arguments\n" +
			"error(" + ErrorType.MISSING_LEXER_COMMAND_ARGUMENT.code + "): A.g4:5:13: missing argument for lexer command 'type'\n"
		};
		super.testErrors(pair, true);
	}

	@Test public void testRuleRedefinition() {
		String[] pair = new String[] {
			"grammar Oops;\n" +
			"\n" +
			"ret_ty : A ;\n" +
			"ret_ty : B ;\n" +
			"\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n",

			"error(" + ErrorType.RULE_REDEFINITION.code + "): Oops.g4:4:0: rule 'ret_ty' redefinition; previous at line 3\n"
		};
		super.testErrors(pair, true);
	}

	@Test public void testEpsilonClosureAnalysis() {
		String grammar =
			"grammar A;\n"
			+ "x : ;\n"
			+ "y1 : x+;\n"
			+ "y2 : x*;\n"
			+ "z1 : ('foo' | 'bar'? 'bar2'?)*;\n"
			+ "z2 : ('foo' | 'bar' 'bar2'? | 'bar2')*;\n";
		String expected =
			"error(" + ErrorType.EPSILON_CLOSURE.code + "): A.g4:3:0: rule 'y1' contains a closure with at least one alternative that can match an empty string\n" +
			"error(" + ErrorType.EPSILON_CLOSURE.code + "): A.g4:4:0: rule 'y2' contains a closure with at least one alternative that can match an empty string\n" +
			"error(" + ErrorType.EPSILON_CLOSURE.code + "): A.g4:5:0: rule 'z1' contains a closure with at least one alternative that can match an empty string\n";

		String[] pair = new String[] {
			grammar,
			expected
		};
			
		super.testErrors(pair, true);
	}

	@Test public void testEpsilonOptionalAnalysis() {
		String grammar =
			"grammar A;\n"
			+ "x : ;\n"
			+ "y  : x?;\n"
			+ "z1 : ('foo' | 'bar'? 'bar2'?)?;\n"
			+ "z2 : ('foo' | 'bar' 'bar2'? | 'bar2')?;\n";
		String expected =
			"warning(" + ErrorType.EPSILON_OPTIONAL.code + "): A.g4:3:0: rule 'y' contains an optional block with at least one alternative that can match an empty string\n" +
			"warning(" + ErrorType.EPSILON_OPTIONAL.code + "): A.g4:4:0: rule 'z1' contains an optional block with at least one alternative that can match an empty string\n";

		String[] pair = new String[] {
			grammar,
			expected
		};

		super.testErrors(pair, true);
	}

	/**
	 * This is a regression test for antlr/antlr4#315
	 * "Inconsistent lexer error msg for actions"
	 * https://github.com/antlr/antlr4/issues/315
	 */
	@Test public void testActionAtEndOfOneLexerAlternative() {
		String grammar =
			"grammar A;\n" +
			"stat : 'start' CharacterLiteral 'end' EOF;\n" +
			"\n" +
			"// Lexer\n" +
			"\n" +
			"CharacterLiteral\n" +
			"    :   '\\'' SingleCharacter '\\''\n" +
			"    |   '\\'' ~[\\r\\n] {notifyErrorListeners(\"unclosed character literal\");}\n" +
			"    ;\n" +
			"\n" +
			"fragment\n" +
			"SingleCharacter\n" +
			"    :   ~['\\\\\\r\\n]\n" +
			"    ;\n" +
			"\n" +
			"WS   : [ \\r\\t\\n]+ -> skip ;\n";
		String expected =
			"";
		
		String[] pair = new String[] { grammar, expected };
		super.testErrors(pair, true);
	}
}
