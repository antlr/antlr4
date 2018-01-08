/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.tool.Grammar;
import org.junit.Before;
import org.junit.Test;

/** */
@SuppressWarnings("unused")
public class TestActionTranslation extends BaseJavaToolTest {
	@Before
	@Override
	public void testSetUp() throws Exception {
		super.testSetUp();
	}

	String attributeTemplate =
		"attributeTemplate(members,init,inline,finally,inline2) ::= <<\n" +
		"parser grammar A;\n"+
		"@members {#members#<members>#end-members#}\n" +
		"a[int x, int x1] returns [int y]\n" +
		"@init {#init#<init>#end-init#}\n" +
		"    :   id=ID ids+=ID lab=b[34] c d {\n" +
		"		 #inline#<inline>#end-inline#\n" +
		"		 }\n" +
		"		 c\n" +
		"    ;\n" +
		"    finally {#finally#<finally>#end-finally#}\n" +
		"b[int d] returns [int e]\n" +
		"    :   {#inline2#<inline2>#end-inline2#}\n" +
		"    ;\n" +
		"c returns [int x, int y] : ;\n" +
		"d	 :   ;\n" +
		">>";

    @Test public void testEscapedLessThanInAction() throws Exception {
        String action = "i<3; '<xmltag>'";
		String expected = "i<3; '<xmltag>'";
		testActions(attributeTemplate, "members", action, expected);
		testActions(attributeTemplate, "init", action, expected);
		testActions(attributeTemplate, "inline", action, expected);
		testActions(attributeTemplate, "finally", action, expected);
		testActions(attributeTemplate, "inline2", action, expected);
    }

    @Test public void testEscaped$InAction() throws Exception {
		String action = "int \\$n; \"\\$in string\\$\"";
		String expected = "int $n; \"$in string$\"";
		testActions(attributeTemplate, "members", action, expected);
		testActions(attributeTemplate, "init", action, expected);
		testActions(attributeTemplate, "inline", action, expected);
		testActions(attributeTemplate, "finally", action, expected);
		testActions(attributeTemplate, "inline2", action, expected);
    }

	/**
	 * Regression test for "in antlr v4 lexer, $ translation issue in action".
	 * https://github.com/antlr/antlr4/issues/176
	 */
	@Test public void testUnescaped$InAction() throws Exception {
		String action = "\\$string$";
		String expected = "$string$";
		testActions(attributeTemplate, "members", action, expected);
		testActions(attributeTemplate, "init", action, expected);
		testActions(attributeTemplate, "inline", action, expected);
		testActions(attributeTemplate, "finally", action, expected);
		testActions(attributeTemplate, "inline2", action, expected);
	}

	@Test public void testEscapedSlash() throws Exception {
		String action   = "x = '\\n';";  // x = '\n'; -> x = '\n';
		String expected = "x = '\\n';";
		testActions(attributeTemplate, "members", action, expected);
		testActions(attributeTemplate, "init", action, expected);
		testActions(attributeTemplate, "inline", action, expected);
		testActions(attributeTemplate, "finally", action, expected);
		testActions(attributeTemplate, "inline2", action, expected);
	}

	@Test public void testComplicatedArgParsing() throws Exception {
		String action = "x, (*a).foo(21,33), 3.2+1, '\\n', "+
						"\"a,oo\\nick\", {bl, \"fdkj\"eck}";
		String expected = "x, (*a).foo(21,33), 3.2+1, '\\n', "+
						"\"a,oo\\nick\", {bl, \"fdkj\"eck}";
		testActions(attributeTemplate, "members", action, expected);
		testActions(attributeTemplate, "init", action, expected);
		testActions(attributeTemplate, "inline", action, expected);
		testActions(attributeTemplate, "finally", action, expected);
		testActions(attributeTemplate, "inline2", action, expected);
	}

	@Test public void testComplicatedArgParsingWithTranslation() throws Exception {
		String action = "x, $ID.text+\"3242\", (*$ID).foo(21,33), 3.2+1, '\\n', "+
						"\"a,oo\\nick\", {bl, \"fdkj\"eck}";
		String expected =
			"x, (((AContext)_localctx).ID!=null?((AContext)_localctx).ID.getText():null)+\"3242\", " +
			"(*((AContext)_localctx).ID).foo(21,33), 3.2+1, '\\n', \"a,oo\\nick\", {bl, \"fdkj\"eck}";
		testActions(attributeTemplate, "inline", action, expected);
	}

	@Test public void testArguments() throws Exception {
		String action = "$x; $ctx.x";
		String expected = "_localctx.x; _localctx.x";
		testActions(attributeTemplate, "inline", action, expected);
	}

	@Test public void testReturnValue() throws Exception {
		String action = "$y; $ctx.y";
		String expected = "_localctx.y; _localctx.y";
		testActions(attributeTemplate, "inline", action, expected);
	}

	@Test public void testReturnValueWithNumber() throws Exception {
		String action = "$ctx.x1";
		String expected = "_localctx.x1";
		testActions(attributeTemplate, "inline", action, expected);
	}

	@Test public void testReturnValuesCurrentRule() throws Exception {
		String action = "$y; $ctx.y;";
		String expected = "_localctx.y; _localctx.y;";
		testActions(attributeTemplate, "inline", action, expected);
	}

	@Test public void testReturnValues() throws Exception {
		String action = "$lab.e; $b.e; $y.e = \"\";";
		String expected = "((AContext)_localctx).lab.e; ((AContext)_localctx).b.e; _localctx.y.e = \"\";";
		testActions(attributeTemplate, "inline", action, expected);
	}

    @Test public void testReturnWithMultipleRuleRefs() throws Exception {
		String action = "$c.x; $c.y;";
		String expected = "((AContext)_localctx).c.x; ((AContext)_localctx).c.y;";
		testActions(attributeTemplate, "inline", action, expected);
    }

    @Test public void testTokenRefs() throws Exception {
		String action = "$id; $ID; $id.text; $id.getText(); $id.line;";
		String expected = "((AContext)_localctx).id; ((AContext)_localctx).ID; (((AContext)_localctx).id!=null?((AContext)_localctx).id.getText():null); ((AContext)_localctx).id.getText(); (((AContext)_localctx).id!=null?((AContext)_localctx).id.getLine():0);";
		testActions(attributeTemplate, "inline", action, expected);
    }

    @Test public void testRuleRefs() throws Exception {
        String action = "$lab.start; $c.text;";
		String expected = "(((AContext)_localctx).lab!=null?(((AContext)_localctx).lab.start):null); (((AContext)_localctx).c!=null?_input.getText(((AContext)_localctx).c.start,((AContext)_localctx).c.stop):null);";
		testActions(attributeTemplate, "inline", action, expected);
    }

    /** Added in response to https://github.com/antlr/antlr4/issues/1211 */
	@Test public void testUnknownAttr() throws Exception {
		String action = "$qqq.text";
		String expected = ""; // was causing an exception
		testActions(attributeTemplate, "inline", action, expected);
	}

	/**
	 * Regression test for issue #1295
     * $e.v yields incorrect value 0 in "e returns [int v] : '1' {$v = 1;} | '(' e ')' {$v = $e.v;} ;"
	 * https://github.com/antlr/antlr4/issues/1295
	 */
	@Test public void testRuleRefsRecursive() throws Exception {
        String recursiveTemplate =
            "recursiveTemplate(inline) ::= <<\n" +
            "parser grammar A;\n"+
            "e returns [int v]\n" +
            "    :   INT {$v = $INT.int;}\n" +
            "    |   '(' e ')' {\n" +
            "		 #inline#<inline>#end-inline#\n" +
            "		 }\n" +
            "    ;\n" +
            ">>";
        String leftRecursiveTemplate =
            "recursiveTemplate(inline) ::= <<\n" +
            "parser grammar A;\n"+
            "e returns [int v]\n" +
            "    :   a=e op=('*'|'/') b=e  {$v = eval($a.v, $op.type, $b.v);}\n" +
            "    |   INT {$v = $INT.int;}\n" +
            "    |   '(' e ')' {\n" +
            "		 #inline#<inline>#end-inline#\n" +
            "		 }\n" +
            "    ;\n" +
            ">>";
        // ref to value returned from recursive call to rule
        String action = "$v = $e.v;";
		String expected = "((EContext)_localctx).v =  ((EContext)_localctx).e.v;";
		testActions(recursiveTemplate, "inline", action, expected);
		testActions(leftRecursiveTemplate, "inline", action, expected);
        // ref to predefined attribute obtained from recursive call to rule
        action = "$v = $e.text.length();";
        expected = "((EContext)_localctx).v =  (((EContext)_localctx).e!=null?_input.getText(((EContext)_localctx).e.start,((EContext)_localctx).e.stop):null).length();";
		testActions(recursiveTemplate, "inline", action, expected);
		testActions(leftRecursiveTemplate, "inline", action, expected);
	}

	@Test public void testRefToTextAttributeForCurrentRule() throws Exception {
        String action = "$ctx.text; $text";

		// this is the expected translation for all cases
		String expected =
			"_localctx.text; _input.getText(_localctx.start, _input.LT(-1))";

		testActions(attributeTemplate, "init", action, expected);
		testActions(attributeTemplate, "inline", action, expected);
		testActions(attributeTemplate, "finally", action, expected);
    }

    @Test public void testEmptyActions() throws Exception {
	    String gS =
	   		"grammar A;\n"+
	   		"a[] : 'a' ;\n" +
	   		"c : a[] c[] ;\n";
	    Grammar g = new Grammar(gS);
    }


/*
    @Test public void testSimplePlusEqualLabel() throws Exception {
        String action = "$ids.size();"; // must be qualified
    }
    @Test public void testPlusEqualStringLabel() throws Exception {
        String action = "$ids.size();"; // must be qualified
    }
    @Test public void testPlusEqualSetLabel() throws Exception {
        String action = "$ids.size();"; // must be qualified
    }
    @Test public void testPlusEqualWildcardLabel() throws Exception {
        String action = "$ids.size();"; // must be qualified
    }
    @Test public void testImplicitTokenLabel() throws Exception {
        String action = "$ID; $ID.text; $ID.getText()";
    }

    @Test public void testImplicitRuleLabel() throws Exception {
        String action = "$r.start;";
    }

    @Test public void testReuseExistingLabelWithImplicitRuleLabel() throws Exception {
        String action = "$r.start;";
    }

    @Test public void testReuseExistingListLabelWithImplicitRuleLabel() throws Exception {
        String action = "$r.start;";
    }

    @Test public void testReuseExistingLabelWithImplicitTokenLabel() throws Exception {
        String action = "$ID.text;";
    }

    @Test public void testReuseExistingListLabelWithImplicitTokenLabel() throws Exception {
        String action = "$ID.text;";
    }

    @Test public void testRuleLabelWithoutOutputOption() throws Exception {
    }
    @Test public void testMissingArgs() throws Exception {
    }
    @Test public void testArgsWhenNoneDefined() throws Exception {
    }
    @Test public void testReturnInitValue() throws Exception {
    }
    @Test public void testMultipleReturnInitValue() throws Exception {
    }
    @Test public void testCStyleReturnInitValue() throws Exception {
    }
    @Test public void testArgsWithInitValues() throws Exception {
    }
    @Test public void testArgsOnToken() throws Exception {
    }
    @Test public void testArgsOnTokenInLexer() throws Exception {
    }
    @Test public void testLabelOnRuleRefInLexer() throws Exception {
        String action = "$i.text";
    }

    @Test public void testRefToRuleRefInLexer() throws Exception {
        String action = "$ID.text";
    }

    @Test public void testRefToRuleRefInLexerNoAttribute() throws Exception {
        String action = "$ID";
    }

    @Test public void testCharLabelInLexer() throws Exception {
    }
    @Test public void testCharListLabelInLexer() throws Exception {
    }
    @Test public void testWildcardCharLabelInLexer() throws Exception {
    }
    @Test public void testWildcardCharListLabelInLexer() throws Exception {
    }
    @Test public void testMissingArgsInLexer() throws Exception {
    }
    @Test public void testLexerRulePropertyRefs() throws Exception {
        String action = "$text $type $line $pos $channel $index $start $stop";
    }

    @Test public void testLexerLabelRefs() throws Exception {
        String action = "$a $b.text $c $d.text";
    }

    @Test public void testSettingLexerRulePropertyRefs() throws Exception {
        String action = "$text $type=1 $line=1 $pos=1 $channel=1 $index";
    }

    @Test public void testArgsOnTokenInLexerRuleOfCombined() throws Exception {
    }
    @Test public void testMissingArgsOnTokenInLexerRuleOfCombined() throws Exception {
    }
    @Test public void testTokenLabelTreeProperty() throws Exception {
        String action = "$id.tree;";
    }

    @Test public void testTokenRefTreeProperty() throws Exception {
        String action = "$ID.tree;";
    }

    @Test public void testAmbiguousTokenRef() throws Exception {
        String action = "$ID;";
    }

    @Test public void testAmbiguousTokenRefWithProp() throws Exception {
        String action = "$ID.text;";
    }

    @Test public void testRuleRefWithDynamicScope() throws Exception {
        String action = "$field::x = $field.st;";
    }

    @Test public void testAssignToOwnRulenameAttr() throws Exception {
        String action = "$rule.tree = null;";
    }

    @Test public void testAssignToOwnParamAttr() throws Exception {
        String action = "$rule.i = 42; $i = 23;";
    }

    @Test public void testIllegalAssignToOwnRulenameAttr() throws Exception {
        String action = "$rule.stop = 0;";
    }

    @Test public void testIllegalAssignToLocalAttr() throws Exception {
        String action = "$tree = null; $st = null; $start = 0; $stop = 0; $text = 0;";
    }

    @Test public void testIllegalAssignRuleRefAttr() throws Exception {
        String action = "$other.tree = null;";
    }

    @Test public void testIllegalAssignTokenRefAttr() throws Exception {
        String action = "$ID.text = \"test\";";
    }

    @Test public void testAssignToTreeNodeAttribute() throws Exception {
        String action = "$tree.scope = localScope;";
    }

    @Test public void testDoNotTranslateAttributeCompare() throws Exception {
        String action = "$a.line == $b.line";
    }

    @Test public void testDoNotTranslateScopeAttributeCompare() throws Exception {
        String action = "if ($rule::foo == \"foo\" || 1) { System.out.println(\"ouch\"); }";
    }

    @Test public void testTreeRuleStopAttributeIsInvalid() throws Exception {
        String action = "$r.x; $r.start; $r.stop";
    }

    @Test public void testRefToTextAttributeForCurrentTreeRule() throws Exception {
        String action = "$text";
    }

    @Test public void testTypeOfGuardedAttributeRefIsCorrect() throws Exception {
        String action = "int x = $b::n;";
    }

	@Test public void testBracketArgParsing() throws Exception {
	}

	@Test public void testStringArgParsing() throws Exception {
		String action = "34, '{', \"it's<\", '\"', \"\\\"\", 19";
	}
	@Test public void testComplicatedSingleArgParsing() throws Exception {
		String action = "(*a).foo(21,33,\",\")";
	}
	@Test public void testArgWithLT() throws Exception {
		String action = "34<50";
	}
	@Test public void testGenericsAsArgumentDefinition() throws Exception {
		String action = "$foo.get(\"ick\");";
	}
	@Test public void testGenericsAsArgumentDefinition2() throws Exception {
		String action = "$foo.get(\"ick\"); x=3;";
	}
	@Test public void testGenericsAsReturnValue() throws Exception {
	}
*/
	// TODO: nonlocal $rule::x
}
