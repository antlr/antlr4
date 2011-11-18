package org.antlr.v4.test;

import org.antlr.runtime.*;
import org.antlr.v4.parse.ActionSplitter;
import org.antlr.v4.semantics.BlankActionSplitterListener;
import org.junit.Test;

import java.util.*;

public class TestActionSplitter extends BaseTest {
    static String[] exprs = {
        "foo",		"['foo'<" + ActionSplitter.TEXT + ">]",
        "$x",		"['$x'<" + ActionSplitter.ATTR + ">]",
        "\\$x",		"['\\$x'<" + ActionSplitter.TEXT + ">]",
        "$x.y",		"['$x.y'<" + ActionSplitter.QUALIFIED_ATTR + ">]",
        "$ID.text",		"['$ID.text'<" + ActionSplitter.QUALIFIED_ATTR + ">]",
        "$ID",		"['$ID'<" + ActionSplitter.ATTR + ">]",
        "$ID.getText()",		"['$ID'<" + ActionSplitter.ATTR + ">, '.getText()'<" + ActionSplitter.TEXT + ">]",
        "$ID.text = \"test\";",		"['$ID.text = \"test\";'<" + ActionSplitter.SET_QUALIFIED_ATTR + ">]",
        "$a.line == $b.line",		"['$a.line'<" + ActionSplitter.QUALIFIED_ATTR + ">, ' == '<" + ActionSplitter.TEXT + ">, '$b.line'<" + ActionSplitter.QUALIFIED_ATTR + ">]",
        "$r.tree",		"['$r.tree'<" + ActionSplitter.QUALIFIED_ATTR + ">]",
        "foo $a::n bar",		"['foo '<" + ActionSplitter.TEXT + ">, '$a::n'<" + ActionSplitter.NONLOCAL_ATTR + ">, ' bar'<" + ActionSplitter.TEXT + ">]",
        "$rule::x;",		"['$rule::x'<" + ActionSplitter.NONLOCAL_ATTR + ">, ';'<" + ActionSplitter.TEXT + ">]",
        "$field::x = $field.st;",		"['$field::x = $field.st;'<" + ActionSplitter.SET_NONLOCAL_ATTR + ">]",
        "$foo.get(\"ick\");",		"['$foo'<" + ActionSplitter.ATTR + ">, '.get(\"ick\");'<" + ActionSplitter.TEXT + ">]",
    };

    @Test public void testExprs() {
        for (int i = 0; i < exprs.length; i+=2) {
            String input = exprs[i];
            String expect = exprs[i+1];
            List<String> chunks = getActionChunks(input);
            assertEquals("input: "+input, expect, chunks.toString());
        }
    }

    public static List<String> getActionChunks(String a) {
        List<String> chunks = new ArrayList<String>();
        ActionSplitter splitter = new ActionSplitter(new ANTLRStringStream(a),
													 new BlankActionSplitterListener());
        Token t = splitter.nextToken();
        while ( t.getType()!=Token.EOF ) {
            chunks.add("'"+t.getText()+"'<"+t.getType()+">");
            t = splitter.nextToken();
        }
        return chunks;
    }
}
