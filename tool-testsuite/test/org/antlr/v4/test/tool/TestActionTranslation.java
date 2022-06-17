/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.analysis.AnalysisPipeline;
import org.antlr.v4.automata.ATNFactory;
import org.antlr.v4.automata.LexerATNFactory;
import org.antlr.v4.automata.ParserATNFactory;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.semantics.SemanticPipeline;
import org.antlr.v4.test.runtime.ErrorQueue;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.jupiter.api.Test;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupString;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** */
@SuppressWarnings("unused")
public class TestActionTranslation {
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

	private static void testActions(String templates, String actionName, String action, String expected) throws org.antlr.runtime.RecognitionException {
		int lp = templates.indexOf('(');
		String name = templates.substring(0, lp);
		STGroup group = new STGroupString(templates);
		ST st = group.getInstanceOf(name);
		st.add(actionName, action);
		String grammar = st.render();
		ErrorQueue equeue = new ErrorQueue();
		Grammar g = new Grammar(grammar, equeue);
		if ( g.ast!=null && !g.ast.hasErrors ) {
			SemanticPipeline sem = new SemanticPipeline(g);
			sem.process();

			ATNFactory factory = new ParserATNFactory(g);
			if ( g.isLexer() ) factory = new LexerATNFactory((LexerGrammar)g);
			g.atn = factory.createATN();

			AnalysisPipeline anal = new AnalysisPipeline(g);
			anal.process();

			CodeGenerator gen = CodeGenerator.create(g);
			ST outputFileST = gen.generateParser(false);
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
//			System.err.println(equeue.toString());
		}
	}
}
