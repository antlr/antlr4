package org.antlr.v4.test;

import org.antlr.runtime.RecognitionException;
import org.antlr.v4.automata.LexerNFAFactory;
import org.antlr.v4.automata.NFAFactory;
import org.antlr.v4.automata.ParserNFAFactory;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.semantics.SemanticPipeline;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.Test;
import org.stringtemplate.v4.ST;

/** */
public class TestActionTranslation extends BaseTest {
	String attributeTemplate =
		"parser grammar A;\n"+
		"@members {#members#<members>#end-members#}\n" +
		"a[int x] returns [int y]\n" +
		"@init {#init#<init>#end-init#}\n" +
		"    :   id=ID ids+=ID lab=b[34] {\n" +
		"		 #inline#<inline>#end-inline#\n" +
		"		 }\n" +
		"		 c\n" +
		"    ;\n" +
		"    finally {#finally#<finally>#end-finally#}\n" +
		"b[int d] returns [int e]\n" +
		"    :   {#inline2#<inline2>#end-inline2#}\n" +
		"    ;\n" +
		"c   :   ;\n" +
		"d	 :   ;\n";

	String scopeTemplate =
		"parser grammar A;\n"+
		"@members {\n" +
		"<members>\n" +
		"}\n" +
		"scope S { int i; }\n" +
		"a[int x] returns [int y]\n" +
		"scope { int z; }\n" +
		"scope S;\n" +
		"@init {<init>}\n" +
		"    :   lab=b[34] {\n" +
		"		 <inline>" +
		"		 }\n" +
		"    ;\n" +
		"    finally {<finally>}\n" +
		"b[int d] returns [int e]\n" +
		"scope { int f; }\n" +
		"    :   {<inline2>}\n" +
		"    ;\n" +
		"c   :   ;";
	
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
    }
    @Test public void testArguments() throws Exception {
        String action = "$i; $i.x; $u; $u.x";
    }
    @Test public void testComplicatedArgParsing() throws Exception {
        String action = "x, (*a).foo(21,33), 3.2+1, '\\n', "+
                        "\"a,oo\\nick\", {bl, \"fdkj\"eck}";
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
    @Test public void testComplicatedArgParsingWithTranslation() throws Exception {
        String action = "x, $A.text+\"3242\", (*$A).foo(21,33), 3.2+1, '\\n', "+
                        "\"a,oo\\nick\", {bl, \"fdkj\"eck}";
    }
    @Test public void testRefToReturnValueBeforeRefToPredefinedAttr() throws Exception {
        String action = "$x.foo";
    }
    @Test public void testRuleLabelBeforeRefToPredefinedAttr() throws Exception {
        String action = "$x.text";
    }
    @Test public void testInvalidArguments() throws Exception {
        String action = "$x";
    }
    @Test public void testReturnValue() throws Exception {
        String action = "$x.i";
    }
    @Test public void testReturnValueWithNumber() throws Exception {
        String action = "$x.i1";
    }
    @Test public void testReturnValues() throws Exception {
        String action = "$i; $i.x; $u; $u.x";
    }
    @Test public void testReturnWithMultipleRuleRefs() throws Exception {
        String action1 = "$obj = $rule2.obj;";
        String action2 = "$obj = $rule3.obj;";
        String expecting1 = "obj = rule21;";
        String expecting2 = "obj = rule32;";
        String action = action1;
    }
    @Test public void testInvalidReturnValues() throws Exception {
        String action = "$x";
    }
    @Test public void testTokenLabels() throws Exception {
        String action = "$id; $f; $id.text; $id.getText(); $id.dork " +
                        "$id.type; $id.line; $id.pos; " +
                        "$id.channel; $id.index;";
    }
    @Test public void testRuleLabels() throws Exception {
        String action = "$r.x; $r.start;\n $r.stop;\n $r.tree; $a.x; $a.stop;";
    }
    @Test public void testAmbiguRuleRef() throws Exception {
    }
    @Test public void testRuleLabelsWithSpecialToken() throws Exception {
        String action = "$r.x; $r.start; $r.stop; $r.tree; $a.x; $a.stop;";
    }
    @Test public void testForwardRefRuleLabels() throws Exception {
        String action = "$r.x; $r.start; $r.stop; $r.tree; $a.x; $a.tree;";
    }
    @Test public void testInvalidRuleLabelAccessesParameter() throws Exception {
        String action = "$r.z";
    }
    @Test public void testInvalidRuleLabelAccessesScopeAttribute() throws Exception {
        String action = "$r.n";
    }
    @Test public void testInvalidRuleAttribute() throws Exception {
        String action = "$r.blort";
    }
    @Test public void testMissingRuleAttribute() throws Exception {
        String action = "$r";
    }
    @Test public void testMissingUnlabeledRuleAttribute() throws Exception {
        String action = "$a";
    }
    @Test public void testNonDynamicAttributeOutsideRule() throws Exception {
        String action = "public void foo() { $x; }";
    }
    @Test public void testNonDynamicAttributeOutsideRule2() throws Exception {
        String action = "public void foo() { $x.y; }";
    }
    @Test public void testBasicGlobalScope() throws Exception {
        String action = "$Symbols::names.add($id.text);";
    }
    @Test public void testUnknownGlobalScope() throws Exception {
        String action = "$Symbols::names.add($id.text);";
    }
    @Test public void testIndexedGlobalScope() throws Exception {
        String action = "$Symbols[-1]::names.add($id.text);";
    }
    @Test public void test0IndexedGlobalScope() throws Exception {
        String action = "$Symbols[0]::names.add($id.text);";
    }
    @Test public void testAbsoluteIndexedGlobalScope() throws Exception {
        String action = "$Symbols[3]::names.add($id.text);";
    }
    @Test public void testScopeAndAttributeWithUnderscore() throws Exception {
        String action = "$foo_bar::a_b;";
    }
    @Test public void testSharedGlobalScope() throws Exception {
        String action = "$Symbols::x;";
    }
    @Test public void testGlobalScopeOutsideRule() throws Exception {
        String action = "public void foo() {$Symbols::names.add('foo');}";
    }
    @Test public void testRuleScopeOutsideRule() throws Exception {
        String action = "public void foo() {$a::name;}";
    }
    @Test public void testBasicRuleScope() throws Exception {
        String action = "$a::n;";
    }
    @Test public void testUnqualifiedRuleScopeAccessInsideRule() throws Exception {
        String action = "$n;";
    }
    @Test public void testIsolatedDynamicRuleScopeRef() throws Exception {
        String action = "$a;"; // refers to stack not top of stack
    }
    @Test public void testDynamicRuleScopeRefInSubrule() throws Exception {
        String action = "$a::n;";
    }
    @Test public void testIsolatedGlobalScopeRef() throws Exception {
        String action = "$Symbols;";
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
    @Test public void testRefToTextAttributeForCurrentRule() throws Exception {
        String action = "$text";
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
    @Test public void testUnknownDynamicAttribute() throws Exception {
        String action = "$a::x";
    }

    @Test public void testUnknownGlobalDynamicAttribute() throws Exception {
        String action = "$Symbols::x";
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

	public void testActions(String template, String actionName, String action, String expected) {
		ST st = new ST(template);
		st.add(actionName, action);
		String grammar = st.render();
		try {
			ErrorQueue equeue = new ErrorQueue();
			Grammar g = new Grammar(grammar);
			if ( g.ast!=null && !g.ast.hasErrors ) {
				SemanticPipeline sem = new SemanticPipeline(g);
				sem.process();

				NFAFactory factory = new ParserNFAFactory(g);
				if ( g.isLexer() ) factory = new LexerNFAFactory((LexerGrammar)g);
				g.nfa = factory.createNFA();
						
				CodeGenerator gen = new CodeGenerator(g);
				ST outputFileST = gen.generate();
				String output = outputFileST.render();
				System.out.println(output);
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
