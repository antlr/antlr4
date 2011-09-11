package org.antlr.v4.test;

import org.antlr.runtime.RecognitionException;
import org.antlr.v4.automata.*;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.semantics.SemanticPipeline;
import org.antlr.v4.tool.*;
import org.junit.Test;
import org.stringtemplate.v4.*;

/** */
public class TestActionTranslation extends BaseTest {
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

	String scopeTemplate =
		"scopeTemplate(members,init,inline,finally,inline2) ::= <<\n" +
		"parser grammar A;\n"+
		"@members {\n" +
		"#members#<members>#end-members#\n" +
		"}\n" +
		"scope S { int i; }\n" +
		"a\n" +
		"scope { int z; }\n" +
		"scope S;\n" +
		"@init {#init#<init>#end-init#}\n" +
		"    :   {\n" +
		"		 #inline#<inline>#end-inline#" +
		"		 }\n" +
		"    ;\n" +
		"    finally {#finally#<finally>#end-finally#}\n" +
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
		String expected = "x, (_localctx._tID!=null?_localctx._tID.getText():null)+\"3242\"," +
						  " (*_localctx._tID).foo(21,33), 3.2+1, '\\n', \"a,oo\\nick\", {bl, \"fdkj\"eck}";
		testActions(attributeTemplate, "inline", action, expected);
	}

	@Test public void testArguments() throws Exception {
		String action = "$x; $a.x";
		String expected = "_localctx.x; _localctx.x";
		testActions(attributeTemplate, "inline", action, expected);
	}

	@Test public void testReturnValue() throws Exception {
		String action = "$x; $a.x";
		String expected = "_localctx.x; _localctx.x";
		testActions(attributeTemplate, "inline", action, expected);
	}

	@Test public void testReturnValueWithNumber() throws Exception {
		String action = "$a.x1";
		String expected = "_localctx.x1";
		testActions(attributeTemplate, "inline", action, expected);
	}

	@Test public void testReturnValuesCurrentRule() throws Exception {
		String action = "$y; $a.y;";
		String expected = "_localctx.y; _localctx.y;";
		testActions(attributeTemplate, "inline", action, expected);
	}

	@Test public void testReturnValues() throws Exception {
		String action = "$lab.e; $b.e;";
		String expected = "_localctx.lab.e; _localctx._rb.e;";
		testActions(attributeTemplate, "inline", action, expected);
	}

    @Test public void testReturnWithMultipleRuleRefs() throws Exception {
		String action = "$c.x; $c.y;";
		String expected = "_localctx._rc.x; _localctx._rc.y;";
		testActions(attributeTemplate, "inline", action, expected);
    }

    @Test public void testTokenRefs() throws Exception {
		String action = "$id; $ID; $id.text; $id.getText(); $id.line;";
		String expected = "_localctx.id; _localctx._tID; (_localctx.id!=null?_localctx.id.getText():null); _localctx.id.getText(); (_localctx.id!=null?_localctx.id.getLine():0);";
		testActions(attributeTemplate, "inline", action, expected);
    }

    @Test public void testRuleRefs() throws Exception {
        String action = "$lab.start; $c.tree;";
		String expected = "(_localctx.lab!=null?(_localctx.lab.start):null); (_localctx._rc!=null?((CommonAST)_localctx._rc.tree):null);";
		testActions(attributeTemplate, "inline", action, expected);
    }

	@Test public void testRefToTextAttributeForCurrentRule() throws Exception {
        String action = "$a.text; $text";
		String expected =
			"(_localctx._ra!=null?((TokenStream)_input).toString(_localctx._ra.start,_localctx._ra.stop):" +
			"null); ((TokenStream)_input).toString(_localctx.start, _input.LT(-1))";
		testActions(attributeTemplate, "init", action, expected);
		expected =
			"((TokenStream)_input).toString(_localctx.start, _input.LT(-1)); ((TokenStream)_input).toString(_localctx.start, _input.LT(-1))";
		testActions(attributeTemplate, "inline", action, expected);
		expected =
			"(_localctx._ra!=null?((TokenStream)_input).toString(_localctx._ra.start,_localctx._ra.stop):null);" +
			" ((TokenStream)_input).toString(_localctx.start, _input.LT(-1))";
		testActions(attributeTemplate, "finally", action, expected);
    }

    @Test public void testDynamicRuleScopeRefInSubrule() throws Exception {
        String action = "$a::n;";
    }
    @Test public void testRuleScopeFromAnotherRule() throws Exception {
        String action = "$a::n;"; // must be qualified
    }
    @Test public void testFullyQualifiedRefToCurrentRuleParameter() throws Exception {
        String action = "$a.i;";
    }
    @Test public void testFullyQualifiedRefToCurrentRuleRetVal() throws Exception {
        String action = "$a.i;";
    }
    @Test public void testSetFullyQualifiedRefToCurrentRuleRetVal() throws Exception {
        String action = "$a.i = 1;";
    }
    @Test public void testIsolatedRefToCurrentRule() throws Exception {
        String action = "$a;";
    }
    @Test public void testIsolatedRefToRule() throws Exception {
        String action = "$x;";
    }
    @Test public void testFullyQualifiedRefToLabelInCurrentRule() throws Exception {
        String action = "$a.x;";
    }
    @Test public void testFullyQualifiedRefToListLabelInCurrentRule() throws Exception {
        String action = "$a.x;"; // must be qualified
    }
    @Test public void testFullyQualifiedRefToTemplateAttributeInCurrentRule() throws Exception {
        String action = "$a.st;"; // can be qualified
    }
    @Test public void testRuleRefWhenRuleHasScope() throws Exception {
        String action = "$b.start;";
    }
    @Test public void testDynamicScopeRefOkEvenThoughRuleRefExists() throws Exception {
        String action = "$b::n;";
    }
    @Test public void testRefToTemplateAttributeForCurrentRule() throws Exception {
        String action = "$st=null;";
    }

    @Test public void testRefToStartAttributeForCurrentRule() throws Exception {
        String action = "$start;";
    }

    @Test public void testTokenLabelFromMultipleAlts() throws Exception {
        String action = "$ID.text;"; // must be qualified
    }
    @Test public void testRuleLabelFromMultipleAlts() throws Exception {
        String action = "$b.text;"; // must be qualified
    }
    @Test public void testUnqualifiedRuleScopeAttribute() throws Exception {
        String action = "$n;"; // must be qualified
    }
    @Test public void testRuleAndTokenLabelTypeMismatch() throws Exception {
    }
    @Test public void testListAndTokenLabelTypeMismatch() throws Exception {
    }
    @Test public void testListAndRuleLabelTypeMismatch() throws Exception {
    }
    @Test public void testArgReturnValueMismatch() throws Exception {
    }
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
    @Test public void testRuleLabelOnTwoDifferentRulesAST() throws Exception {
    }
    @Test public void testRuleLabelOnTwoDifferentRulesTemplate() throws Exception {
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

	// TODO: nonlocal $rule::x


	public void testActions(String templates, String actionName, String action, String expected) {
		int lp = templates.indexOf('(');
		String name = templates.substring(0, lp);
		STGroup group = new STGroupString(templates);
		ST st = group.getInstanceOf(name);
		st.add(actionName, action);
		String grammar = st.render();
		try {
			ErrorQueue equeue = new ErrorQueue();
			Grammar g = new Grammar(grammar);
			if ( g.ast!=null && !g.ast.hasErrors ) {
				SemanticPipeline sem = new SemanticPipeline(g);
				sem.process();

				ATNFactory factory = new ParserATNFactory(g);
				if ( g.isLexer() ) factory = new LexerATNFactory((LexerGrammar)g);
				g.atn = factory.createATN();

				CodeGenerator gen = new CodeGenerator(g);
				ST outputFileST = gen.generateParser();
				String output = outputFileST.render();
				//System.out.println(output);
				String b = "#" + actionName + "#";
				int start = output.indexOf(b);
				String e = "#end-" + actionName + "#";
				int end = output.indexOf(e);
				String snippet = output.substring(start+b.length(),end);
				assertEquals(expected, snippet);
			}
			if ( equeue.size()>0 ) {
				System.err.println(equeue.toString(g.tool));
			}
		}
		catch (RecognitionException re) {
			re.printStackTrace(System.err);
		}
	}
}
