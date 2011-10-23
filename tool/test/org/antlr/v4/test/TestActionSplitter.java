package org.antlr.v4.test;

import org.antlr.runtime.*;
import org.antlr.v4.parse.ActionSplitter;
import org.antlr.v4.semantics.BlankActionSplitterListener;
import org.junit.Test;

import java.util.*;

public class TestActionSplitter extends BaseTest {
    static String[] exprs = {
        "foo",		"['foo'<22>]",
        "$x",		"['$x'<6>]",
        "\\$x",		"['\\$x'<22>]",
        "$x.y",		"['$x.y'<13>]",
        "$ID.text",		"['$ID.text'<13>]",
        "$ID",		"['$ID'<6>]",
        "$ID.getText()",		"['$ID'<6>, '.getText()'<22>]",
        "$ID.text = \"test\";",		"['$ID.text = \"test\";'<19>]",
        "$a.line == $b.line",		"['$a.line'<13>, ' == '<22>, '$b.line'<13>]",
        "$r.tree",		"['$r.tree'<13>]",
        "foo $a::n bar",		"['foo '<22>, '$a::n'<12>, ' bar'<22>]",
        "$rule::x;",		"['$rule::x'<12>, ';'<22>]",
        "$field::x = $field.st;",		"['$field::x = $field.st;'<17>]",
        "$foo.get(\"ick\");",		"['$foo'<6>, '.get(\"ick\");'<22>]",
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
