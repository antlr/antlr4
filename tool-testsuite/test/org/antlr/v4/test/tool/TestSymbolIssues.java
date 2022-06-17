/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.jupiter.api.Test;

import static org.antlr.v4.test.tool.ToolTestUtils.realElements;
import static org.antlr.v4.test.tool.ToolTestUtils.testErrors;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** */
public class TestSymbolIssues {
    final static String[] A = {
        // INPUT
        "grammar A;\n" +
        "options { opt='sss'; k=3; }\n" +
        "\n" +
        "@members {foo}\n" +
        "@members {bar}\n" +
        "@lexer::header {package jj;}\n" +
        "@lexer::header {package kk;}\n" +
        "\n" +
        "a[int i] returns [foo f] : X ID a[3] b[34] c ;\n" +
        "b returns [int g] : Y 'y' 'if' a ;\n" +
        "c : FJKD ;\n" +
        "\n" +
        "ID : 'a'..'z'+ ID ;",
        // YIELDS
			"error(" + ErrorType.ACTION_REDEFINITION.code + "): A.g4:5:1: redefinition of members action\n" +
			"error(" + ErrorType.ACTION_REDEFINITION.code + "): A.g4:7:1: redefinition of header action\n" +
			"warning(" + ErrorType.ILLEGAL_OPTION.code + "): A.g4:2:10: unsupported option opt\n" +
			"warning(" + ErrorType.ILLEGAL_OPTION.code + "): A.g4:2:21: unsupported option k\n" +
			"error(" + ErrorType.ACTION_REDEFINITION.code + "): A.g4:5:1: redefinition of members action\n" +
			"warning(" + ErrorType.IMPLICIT_TOKEN_DEFINITION.code + "): A.g4:9:27: implicit definition of token X in parser\n" +
			"warning(" + ErrorType.IMPLICIT_TOKEN_DEFINITION.code + "): A.g4:10:20: implicit definition of token Y in parser\n" +
			"warning(" + ErrorType.IMPLICIT_TOKEN_DEFINITION.code + "): A.g4:11:4: implicit definition of token FJKD in parser\n" +
			"error(" + ErrorType.RULE_HAS_NO_ARGS.code + "): A.g4:9:37: rule b has no defined parameters\n" +
			"error(" + ErrorType.MISSING_RULE_ARGS.code + "): A.g4:10:31: missing argument(s) on rule reference: a\n"
    };

	final static String[] B = {
        // INPUT
        "parser grammar B;\n" +
        "tokens { ID, FOO, X, Y }\n" +
        "\n" +
        "a : s=ID b+=ID X=ID '.' ;\n" +
        "\n" +
        "b : x=ID x+=ID ;\n" +
        "\n" +
        "s : FOO ;",
        // YIELDS
		"error(" + ErrorType.LABEL_CONFLICTS_WITH_RULE.code + "): B.g4:4:4: label s conflicts with rule with same name\n" +
		"error(" + ErrorType.LABEL_CONFLICTS_WITH_RULE.code + "): B.g4:4:9: label b conflicts with rule with same name\n" +
		"error(" + ErrorType.LABEL_CONFLICTS_WITH_TOKEN.code + "): B.g4:4:15: label X conflicts with token with same name\n" +
		"error(" + ErrorType.LABEL_TYPE_CONFLICT.code + "): B.g4:6:9: label x type mismatch with previous definition: TOKEN_LIST_LABEL!=TOKEN_LABEL\n" +
		"error(" + ErrorType.IMPLICIT_STRING_DEFINITION.code + "): B.g4:4:20: cannot create implicit token for string literal in non-combined grammar: '.'\n"
    };

	final static String[] D = {
        // INPUT
        "parser grammar D;\n" +
		"tokens{ID}\n" +
        "a[int j] \n" +
        "        :       i=ID j=ID ;\n" +
        "\n" +
        "b[int i] returns [int i] : ID ;\n" +
        "\n" +
        "c[int i] returns [String k]\n" +
        "        :       ID ;",

        // YIELDS
        "error(" + ErrorType.LABEL_CONFLICTS_WITH_ARG.code + "): D.g4:4:21: label j conflicts with parameter with same name\n" +
		"error(" + ErrorType.RETVAL_CONFLICTS_WITH_ARG.code + "): D.g4:6:22: return value i conflicts with parameter with same name\n"
    };

	final static String[] E = {
		// INPUT
		"grammar E;\n" +
		"tokens {\n" +
		"	A, A,\n" +
		"	B,\n" +
		"	C\n" +
		"}\n" +
		"a : A ;\n",

		// YIELDS
		"warning(" + ErrorType.TOKEN_NAME_REASSIGNMENT.code + "): E.g4:3:4: token name A is already defined\n"
	};

	final static String[] F = {
		// INPUT
		"lexer grammar F;\n" +
		"A: 'a';\n" +
		"mode M1;\n" +
		"A1: 'a';\n" +
		"mode M2;\n" +
		"A2: 'a';\n" +
		"M1: 'b';\n",

		// YIELDS
		"error(" + ErrorType.MODE_CONFLICTS_WITH_TOKEN.code + "): F.g4:3:0: mode M1 conflicts with token with same name\n"
	};

    @Test public void testA() { testErrors(A, false); }
    @Test public void testB() { testErrors(B, false); }
	@Test public void testD() { testErrors(D, false); }
	@Test public void testE() { testErrors(E, false); }
	@Test public void testF() { testErrors(F, false); }

	@Test public void testStringLiteralRedefs() throws Exception {
		String grammar =
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"mode X;\n"+
			"B : 'a' ;\n"+
			"mode Y;\n"+
			"C : 'a' ;\n";

		LexerGrammar g = new LexerGrammar(grammar);

		String expectedTokenIDToTypeMap = "{EOF=-1, A=1, B=2, C=3}";
		String expectedStringLiteralToTypeMap = "{}";
		String expectedTypeToTokenList = "[A, B, C]";

		assertEquals(expectedTokenIDToTypeMap, g.tokenNameToTypeMap.toString());
		assertEquals(expectedStringLiteralToTypeMap, g.stringLiteralToTypeMap.toString());
		assertEquals(expectedTypeToTokenList, realElements(g.typeToTokenList).toString());
	}

	@Test public void testEmptyLexerModeDetection() throws Exception {
		String[] test = {
			"lexer grammar L;\n" +
			"A : 'a';\n" +
			"mode X;\n" +
			"fragment B : 'b';",

			"error(" + ErrorType.MODE_WITHOUT_RULES.code + "): L.g4:3:5: lexer mode X must contain at least one non-fragment rule\n"
		};

		testErrors(test, false);
	}

	@Test public void testEmptyLexerRuleDetection() throws Exception {
		String[] test = {
			"lexer grammar L;\n" +
			"A : 'a';\n" +
			"WS : [ \t]* -> skip;\n" +
			"mode X;\n" +
			"  B : C;\n" +
			"  fragment C : A | (A C)?;",

			"warning(" + ErrorType.EPSILON_TOKEN.code + "): L.g4:3:0: non-fragment lexer rule WS can match the empty string\n" +
			"warning(" + ErrorType.EPSILON_TOKEN.code + "): L.g4:5:2: non-fragment lexer rule B can match the empty string\n"
		};

		testErrors(test, false);
	}

	@Test public void testTokensModesChannelsDeclarationConflictsWithReserved() throws Exception {
		String[] test = {
			"lexer grammar L;\n" +
			"channels { SKIP, HIDDEN, channel0 }\n" +
			"A: 'a';\n" +
			"mode MAX_CHAR_VALUE;\n" +
			"MIN_CHAR_VALUE: 'a';\n" +
			"mode DEFAULT_MODE;\n" +
			"B: 'b';\n" +
			"mode M;\n" +
			"C: 'c';",

			"error(" + ErrorType.RESERVED_RULE_NAME.code + "): L.g4:5:0: cannot declare a rule with reserved name MIN_CHAR_VALUE\n" +
			"error(" + ErrorType.MODE_CONFLICTS_WITH_COMMON_CONSTANTS.code + "): L.g4:4:0: cannot use or declare mode with reserved name MAX_CHAR_VALUE\n" +
			"error(" + ErrorType.CHANNEL_CONFLICTS_WITH_COMMON_CONSTANTS.code + "): L.g4:2:11: cannot use or declare channel with reserved name SKIP\n" +
			"error(" + ErrorType.CHANNEL_CONFLICTS_WITH_COMMON_CONSTANTS.code + "): L.g4:2:17: cannot use or declare channel with reserved name HIDDEN\n"
		};

		testErrors(test, false);
	}

	@Test public void testTokensModesChannelsUsingConflictsWithReserved() throws Exception {
		String[] test = {
			"lexer grammar L;\n" +
			"A: 'a' -> channel(SKIP);\n" +
			"B: 'b' -> type(MORE);\n" +
			"C: 'c' -> mode(SKIP);\n" +
			"D: 'd' -> channel(HIDDEN);\n" +
			"E: 'e' -> type(EOF);\n" +
			"F: 'f' -> pushMode(DEFAULT_MODE);",

			"error(" + ErrorType.CHANNEL_CONFLICTS_WITH_COMMON_CONSTANTS.code + "): L.g4:2:18: cannot use or declare channel with reserved name SKIP\n" +
			"error(" + ErrorType.TOKEN_CONFLICTS_WITH_COMMON_CONSTANTS.code + "): L.g4:3:15: cannot use or declare token with reserved name MORE\n" +
			"error(" + ErrorType.MODE_CONFLICTS_WITH_COMMON_CONSTANTS.code + "): L.g4:4:15: cannot use or declare mode with reserved name SKIP\n"
		};

		testErrors(test, false);
	}

	// https://github.com/antlr/antlr4/issues/1411
	@Test public void testWrongIdForTypeChannelModeCommand() throws Exception {
		String[] test = {
			"lexer grammar L;\n" +
			"tokens { TOKEN1 }\n" +
			"channels { CHANNEL1 }\n" +
			"TOKEN: 'asdf' -> type(CHANNEL1), channel(MODE1), mode(TOKEN1);\n" +
			"mode MODE1;\n" +
			"MODE1_TOKEN: 'qwer';",

			"error(" + ErrorType.CONSTANT_VALUE_IS_NOT_A_RECOGNIZED_TOKEN_NAME.code + "): L.g4:4:22: CHANNEL1 is not a recognized token name\n" +
			"error(" + ErrorType.CONSTANT_VALUE_IS_NOT_A_RECOGNIZED_CHANNEL_NAME.code + "): L.g4:4:41: MODE1 is not a recognized channel name\n" +
			"error(" + ErrorType.CONSTANT_VALUE_IS_NOT_A_RECOGNIZED_MODE_NAME.code + "): L.g4:4:54: TOKEN1 is not a recognized mode name\n"
		};

		testErrors(test, false);
	}

	// https://github.com/antlr/antlr4/issues/1388
	@Test public void testDuplicatedCommands() throws Exception {
		String[] test = {
			"lexer grammar Lexer;\n" +
			"channels { CHANNEL1, CHANNEL2 }\n" +
			"tokens { TEST1, TEST2 }\n" +
			"TOKEN: 'a' -> mode(MODE1), mode(MODE2);\n" +
			"TOKEN1: 'b' -> pushMode(MODE1), mode(MODE2);\n" +
			"TOKEN2: 'c' -> pushMode(MODE1), pushMode(MODE2); // pushMode is not duplicate\n" +
			"TOKEN3: 'd' -> popMode, popMode;                 // popMode is not duplicate\n" +
			"mode MODE1;\n" +
			"MODE1_TOKEN: 'e';\n" +
			"mode MODE2;\n" +
			"MODE2_TOKEN: 'f';\n" +
			"MODE2_TOKEN1: 'g' -> type(TEST1), type(TEST2);\n" +
			"MODE2_TOKEN2: 'h' -> channel(CHANNEL1), channel(CHANNEL2), channel(DEFAULT_TOKEN_CHANNEL);",

			"warning(" + ErrorType.DUPLICATED_COMMAND.code + "): Lexer.g4:4:27: duplicated command mode\n" +
			"warning(" + ErrorType.DUPLICATED_COMMAND.code + "): Lexer.g4:12:34: duplicated command type\n" +
			"warning(" + ErrorType.DUPLICATED_COMMAND.code + "): Lexer.g4:13:40: duplicated command channel\n" +
			"warning(" + ErrorType.DUPLICATED_COMMAND.code + "): Lexer.g4:13:59: duplicated command channel\n"
		};

		testErrors(test, false);
	}

	// https://github.com/antlr/antlr4/issues/1388
	@Test public void testIncompatibleCommands() throws Exception {
		String[] test = {
				"lexer grammar L;\n" +
				"channels { CHANNEL1 }\n" +
				"tokens { TYPE1 }\n" +
				"// Incompatible\n" +
				"T00: 'a00' -> skip, more;\n" +
				"T01: 'a01' -> skip, type(TYPE1);\n" +
				"T02: 'a02' -> skip, channel(CHANNEL1);\n" +
				"T03: 'a03' -> more, type(TYPE1);\n" +
				"T04: 'a04' -> more, channel(CHANNEL1);\n" +
				"T05: 'a05' -> more, skip;\n" +
				"T06: 'a06' -> type(TYPE1), skip;\n" +
				"T07: 'a07' -> type(TYPE1), more;\n" +
				"T08: 'a08' -> channel(CHANNEL1), skip;\n" +
				"T09: 'a09' -> channel(CHANNEL1), more;\n" +
				"// Allowed\n" +
				"T10: 'a10' -> type(TYPE1), channel(CHANNEL1);\n" +
				"T11: 'a11' -> channel(CHANNEL1), type(TYPE1);",

				"warning(" + ErrorType.INCOMPATIBLE_COMMANDS.code + "): L.g4:5:20: incompatible commands skip and more\n" +
				"warning(" + ErrorType.INCOMPATIBLE_COMMANDS.code + "): L.g4:6:20: incompatible commands skip and type\n" +
				"warning(" + ErrorType.INCOMPATIBLE_COMMANDS.code + "): L.g4:7:20: incompatible commands skip and channel\n" +
				"warning(" + ErrorType.INCOMPATIBLE_COMMANDS.code + "): L.g4:8:20: incompatible commands more and type\n" +
				"warning(" + ErrorType.INCOMPATIBLE_COMMANDS.code + "): L.g4:9:20: incompatible commands more and channel\n" +
				"warning(" + ErrorType.INCOMPATIBLE_COMMANDS.code + "): L.g4:10:20: incompatible commands more and skip\n" +
				"warning(" + ErrorType.INCOMPATIBLE_COMMANDS.code + "): L.g4:11:27: incompatible commands type and skip\n" +
				"warning(" + ErrorType.INCOMPATIBLE_COMMANDS.code + "): L.g4:12:27: incompatible commands type and more\n" +
				"warning(" + ErrorType.INCOMPATIBLE_COMMANDS.code + "): L.g4:13:33: incompatible commands channel and skip\n" +
				"warning(" + ErrorType.INCOMPATIBLE_COMMANDS.code + "): L.g4:14:33: incompatible commands channel and more\n"
		};

		testErrors(test, false);
	}

	// https://github.com/antlr/antlr4/issues/1409
	@Test public void testLabelsForTokensWithMixedTypes() {
		String[] test = {
				"grammar L;\n" +
				"\n" +
				"rule1                                      // Correct (Alternatives)\n" +
				"    : t1=a  #aLabel\n" +
				"    | t1=b  #bLabel\n" +
				"    ;\n" +
				"rule2                         //Incorrect type casting in generated code (RULE_LABEL)\n" +
				"    : t2=a | t2=b\n" +
				"    ;\n" +
				"rule3\n" +
				"    : t3+=a+ b t3+=c+     //Incorrect type casting in generated code (RULE_LIST_LABEL)\n" +
				"    ;\n" +
				"rule4\n" +
				"    : a t4=A b t4=B c                  // Correct (TOKEN_LABEL)\n" +
				"    ;\n" +
				"rule5\n" +
				"    : a t5+=A b t5+=B c                // Correct (TOKEN_LIST_LABEL)\n" +
				"    ;\n" +
				"rule6                     // Correct (https://github.com/antlr/antlr4/issues/1543)\n" +
				"    : t6=a                          #t6_1_Label\n" +
				"    | t6=rule6 b (t61=c)? t62=rule6 #t6_2_Label\n" +
				"    | t6=A     a (t61=B)? t62=A     #t6_3_Label\n" +
				"    ;\n" +
				"rule7                     // Incorrect (https://github.com/antlr/antlr4/issues/1543)\n" +
				"    : a\n" +
				"    | t7=rule7 b (t71=c)? t72=rule7 \n" +
				"    | t7=A     a (t71=B)? t72=A     \n" +
				"    ;\n" +
				"rule8                     // Correct (https://github.com/antlr/antlr4/issues/1543)\n" +
				"    : a\n" +
				"    | t8=rule8 a t8=rule8\n" +
				"    | t8=rule8 b t8=rule8\n" +
				"    ;\n" +
				"a: A;\n" +
				"b: B;\n" +
				"c: C;\n" +
				"A: 'a';\n" +
				"B: 'b';\n" +
				"C: 'c';\n",

				"error(" + ErrorType.LABEL_TYPE_CONFLICT.code + "): L.g4:8:13: label t2=b type mismatch with previous definition: t2=a\n" +
				"error(" + ErrorType.LABEL_TYPE_CONFLICT.code + "): L.g4:11:15: label t3+=c type mismatch with previous definition: t3+=a\n" +

				"error(" + ErrorType.LABEL_TYPE_CONFLICT.code + "): L.g4:24:0: label t7 type mismatch with previous definition: TOKEN_LABEL!=RULE_LABEL\n" +
				"error(" + ErrorType.LABEL_TYPE_CONFLICT.code + "): L.g4:24:0: label t71 type mismatch with previous definition: RULE_LABEL!=TOKEN_LABEL\n" +
				"error(" + ErrorType.LABEL_TYPE_CONFLICT.code + "): L.g4:24:0: label t72 type mismatch with previous definition: RULE_LABEL!=TOKEN_LABEL\n"
		};

		testErrors(test, false);
	}

	// https://github.com/antlr/antlr4/issues/1543
	@Test public void testLabelsForTokensWithMixedTypesLRWithLabels() {
		String[] test = {
				"grammar L;\n" +
				"\n" +
				"expr\n" +
				"    : left=A '+' right=A        #primary\n" +
				"    | left=expr '-' right=expr  #sub\n" +
				"    ;\n" +
				"\n" +
				"A: 'a';\n" +
				"B: 'b';\n" +
				"C: 'c';\n",

				""
		};

		testErrors(test, false);
	}

	// https://github.com/antlr/antlr4/issues/1543
	@Test
	public void testLabelsForTokensWithMixedTypesLRWithoutLabels() {
		String[] test = {
				"grammar L;\n" +
				"\n" +
				"expr\n" +
				"    : left=A '+' right=A\n" +
				"    | left=expr '-' right=expr\n" +
				"    ;\n" +
				"\n" +
				"A: 'a';\n" +
				"B: 'b';\n" +
				"C: 'c';\n",

				"error(" + ErrorType.LABEL_TYPE_CONFLICT.code + "): L.g4:3:0: label left type mismatch with previous definition: TOKEN_LABEL!=RULE_LABEL\n" +
				"error(" + ErrorType.LABEL_TYPE_CONFLICT.code + "): L.g4:3:0: label right type mismatch with previous definition: RULE_LABEL!=TOKEN_LABEL\n"
		};

		testErrors(test, false);
	}

	@Test public void testCharsCollision() throws  Exception {
		String[] test = {
				"lexer grammar L;\n" +
				"TOKEN_RANGE:      [aa-f];\n" +
				"TOKEN_RANGE_2:    [A-FD-J];\n" +
				"TOKEN_RANGE_3:    'Z' | 'K'..'R' | 'O'..'V';\n" +
				"TOKEN_RANGE_4:    'g'..'l' | [g-l];\n" +
				"TOKEN_RANGE_WITHOUT_COLLISION: '_' | [a-zA-Z];\n" +
				"TOKEN_RANGE_WITH_ESCAPED_CHARS: [\\n-\\r] | '\\n'..'\\r';",

				"warning(" + ErrorType.CHARACTERS_COLLISION_IN_SET.code + "): L.g4:2:18: chars a-f used multiple times in set [aa-f]\n" +
				"warning(" + ErrorType.CHARACTERS_COLLISION_IN_SET.code + "): L.g4:3:18: chars D-J used multiple times in set [A-FD-J]\n" +
				"warning(" + ErrorType.CHARACTERS_COLLISION_IN_SET.code + "): L.g4:4:38: chars O-V used multiple times in set 'Z' | 'K'..'R' | 'O'..'V'\n" +
				"warning(" + ErrorType.CHARACTERS_COLLISION_IN_SET.code + "): L.g4::: chars 'g' used multiple times in set 'g'..'l'\n" +
				"warning(" + ErrorType.CHARACTERS_COLLISION_IN_SET.code + "): L.g4::: chars '\\n' used multiple times in set '\\n'..'\\r'\n"
		};

		testErrors(test, false);
	}

	@Test public void testCaseInsensitiveCharsCollision() {
		String[] test = {
				"lexer grammar L;\n" +
				"options { caseInsensitive = true; }\n" +
				"TOKEN_RANGE:      [a-fA-F0-9];\n" +
				"TOKEN_RANGE_2:    'g'..'l' | 'G'..'L';\n" +
				"TOKEN_RANGE_3:    'm'..'q' | [M-Q];\n",

				"warning(" + ErrorType.CHARACTERS_COLLISION_IN_SET.code + "): L.g4:3:18: chars a-f used multiple times in set [a-fA-F0-9]\n" +
				"warning(" + ErrorType.CHARACTERS_COLLISION_IN_SET.code + "): L.g4:4:32: chars g-l used multiple times in set 'g'..'l' | 'G'..'L'\n" +
				"warning(" + ErrorType.CHARACTERS_COLLISION_IN_SET.code + "): L.g4::: chars 'M' used multiple times in set 'M'..'Q' | 'm'..'q'\n" +
				"warning(" + ErrorType.CHARACTERS_COLLISION_IN_SET.code + "): L.g4::: chars 'm' used multiple times in set 'M'..'Q' | 'm'..'q'\n"
		};

		testErrors(test, false);
	}

	@Test public void testCaseInsensitiveWithUnicodeRanges() {
		String[] test = {
				"lexer grammar L;\n" +
				"options { caseInsensitive=true; }\n" +
				"FullWidthLetter\n" +
				"    : '\\u00c0'..'\\u00d6' // ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ\n" +
				"    | '\\u00f8'..'\\u00ff' // øùúûüýþÿ\n" +
				"    ;",

				""
		};

		// Don't transform øùúûüýþÿ to uppercase
		// ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿĀāĂăĄąĆćĈĉĊċČčĎďĐđĒēĔĕĖėĘęĚěĜĝĞğĠġĢģĤĥĦħĨĩĪīĬĭĮįİıĲĳĴĵĶķĸĹĺĻļĽľĿŀŁłŃńŅņŇňŉŊŋŌōŎŏŐőŒœŔŕŖŗŘřŚśŜŝŞşŠšŢţŤťŦŧŨũŪūŬŭŮůŰűŲųŴŵŶŷŸ
		// because of different length of lower and UPPER range
		testErrors(test, false);
	}

	@Test public void testUnreachableTokens() {
		String[] test = {
				"lexer grammar Test;\n" +
				"TOKEN1: 'as' 'df' | 'qwer';\n" +
				"TOKEN2: [0-9];\n" +
				"TOKEN3: 'asdf';\n" +
				"TOKEN4: 'q' 'w' 'e' 'r' | A;\n" +
				"TOKEN5: 'aaaa';\n" +
				"TOKEN6: 'asdf';\n" +
				"TOKEN7: 'qwer'+;\n" +
				"TOKEN8: 'a' 'b' | 'b' | 'a' 'b';\n" +
				"fragment\n" +
				"TOKEN9: 'asdf' | 'qwer' | 'qwer';\n" +
				"TOKEN10: '\\r\\n' | '\\r\\n';\n" +
				"TOKEN11: '\\r\\n';\n" +
				"\n" +
				"mode MODE1;\n" +
				"TOKEN12: 'asdf';\n" +
				"\n" +
				"fragment A: 'A';",

				"warning(" + ErrorType.TOKEN_UNREACHABLE.code + "): Test.g4:4:0: One of the token TOKEN3 values unreachable. asdf is always overlapped by token TOKEN1\n" +
				"warning(" + ErrorType.TOKEN_UNREACHABLE.code + "): Test.g4:5:0: One of the token TOKEN4 values unreachable. qwer is always overlapped by token TOKEN1\n" +
				"warning(" + ErrorType.TOKEN_UNREACHABLE.code + "): Test.g4:7:0: One of the token TOKEN6 values unreachable. asdf is always overlapped by token TOKEN1\n" +
				"warning(" + ErrorType.TOKEN_UNREACHABLE.code + "): Test.g4:7:0: One of the token TOKEN6 values unreachable. asdf is always overlapped by token TOKEN3\n" +
				"warning(" + ErrorType.TOKEN_UNREACHABLE.code + "): Test.g4:9:0: One of the token TOKEN8 values unreachable. ab is always overlapped by token TOKEN8\n" +
				"warning(" + ErrorType.TOKEN_UNREACHABLE.code + "): Test.g4:11:0: One of the token TOKEN9 values unreachable. qwer is always overlapped by token TOKEN9\n" +
				"warning(" + ErrorType.TOKEN_UNREACHABLE.code + "): Test.g4:12:0: One of the token TOKEN10 values unreachable. \\r\\n is always overlapped by token TOKEN10\n" +
				"warning(" + ErrorType.TOKEN_UNREACHABLE.code + "): Test.g4:13:0: One of the token TOKEN11 values unreachable. \\r\\n is always overlapped by token TOKEN10\n" +
				"warning(" + ErrorType.TOKEN_UNREACHABLE.code + "): Test.g4:13:0: One of the token TOKEN11 values unreachable. \\r\\n is always overlapped by token TOKEN10\n"
		};

		testErrors(test, false);
	}

	@Test public void testIllegalCaseInsensitiveOptionValue() {
		String[] test = {
				"lexer grammar L;\n" +
				"options { caseInsensitive = badValue; }\n" +
				"TOKEN_1 options { caseInsensitive = badValue; } : [A-F]+;\n",

				"warning(" + ErrorType.ILLEGAL_OPTION_VALUE.code + "): L.g4:2:28: unsupported option value caseInsensitive=badValue\n" +
				"warning(" + ErrorType.ILLEGAL_OPTION_VALUE.code + "): L.g4:3:36: unsupported option value caseInsensitive=badValue\n"
		};

		testErrors(test, false);
	}

	@Test public void testRedundantCaseInsensitiveLexerRuleOption() {
		String[] test = {
				"lexer grammar L;\n" +
				"options { caseInsensitive = true; }\n" +
				"TOKEN options { caseInsensitive = true; } : [A-F]+;\n",

				"warning(" + ErrorType.REDUNDANT_CASE_INSENSITIVE_LEXER_RULE_OPTION.code + "): L.g4:3:16: caseInsensitive lexer rule option is redundant because its value equals to global value (true)\n"
		};
		testErrors(test, false);

		String[] test2 = {
				"lexer grammar L;\n" +
				"options { caseInsensitive = false; }\n" +
				"TOKEN options { caseInsensitive = false; } : [A-F]+;\n",

				"warning(" + ErrorType.REDUNDANT_CASE_INSENSITIVE_LEXER_RULE_OPTION.code + "): L.g4:3:16: caseInsensitive lexer rule option is redundant because its value equals to global value (false)\n"
		};
		testErrors(test2, false);
	}

	@Test public void testCaseInsensitiveOptionInParseRule() {
		String[] test = {
				"grammar G;\n" +
				"root options { caseInsensitive=true; } : 'token';",

				"warning(" + ErrorType.ILLEGAL_OPTION.code + "): G.g4:2:15: unsupported option caseInsensitive\n"
		};

		testErrors(test, false);
	}

	@Test public void testNotImpliedCharacters() {
		String[] test = {
				"lexer grammar Test;\n" +
				"TOKEN1: 'A'..'g';\n" +
				"TOKEN2: [C-m];\n" +
				"TOKEN3: [А-я]; // OK since range does not contain intermediate characters\n" +
				"TOKEN4: '\\u0100'..'\\u1fff'; // OK since range borders are unicode characters",

				"warning(" + ErrorType.RANGE_PROBABLY_CONTAINS_NOT_IMPLIED_CHARACTERS.code + "): Test.g4:2:8: Range A..g probably contains not implied characters [\\]^_`. Both bounds should be defined in lower or UPPER case\n" +
				"warning(" + ErrorType.RANGE_PROBABLY_CONTAINS_NOT_IMPLIED_CHARACTERS.code + "): Test.g4:3:8: Range C..m probably contains not implied characters [\\]^_`. Both bounds should be defined in lower or UPPER case\n"
		};

		testErrors(test, false);
	}

	@Test public void testNotImpliedCharactersWithCaseInsensitiveOption() {
		String[] test = {
				"lexer grammar Test;\n" +
				"options { caseInsensitive=true; }\n" +
				"TOKEN: [A-z];",

				"warning(" + ErrorType.RANGE_PROBABLY_CONTAINS_NOT_IMPLIED_CHARACTERS.code + "): Test.g4:3:7: Range A..z probably contains not implied characters [\\]^_`. Both bounds should be defined in lower or UPPER case\n"
		};

		testErrors(test, false);
	}

	// ISSUE: https://github.com/antlr/antlr4/issues/2788
	@Test public void testUndefinedLabel() {
		String[] test = {
				"grammar Test;" +
				"root\n" +
				"    : root a\n" +
				"    | b [error]\n" +
				"    ;\n" +
				"\n" +
				"a: 'a';\n" +
				"b: 'b';",

				"error(" + ErrorType.INTERNAL_ERROR.code + "): Test.g4:2:30: internal error: Rule error undefined \n"
		};

		testErrors(test, false);
	}
}
