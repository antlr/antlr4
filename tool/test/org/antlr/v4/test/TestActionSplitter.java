package org.antlr.v4.test;

import org.antlr.runtime.*;
import org.antlr.v4.parse.ActionSplitter;
import org.antlr.v4.semantics.BlankActionSplitterListener;
import org.junit.Test;

import java.util.*;

public class TestActionSplitter extends BaseTest {
    static String[] exprs = {
        "foo",		"['foo'<26>]",
        "$x",		"['$x'<6>]",
        "\\$x",		"['\\$x'<26>]",
        "$x.y",		"['$x.y'<15>]",
        "$ID.text",		"['$ID.text'<15>]",
        "$ID",		"['$ID'<6>]",
        "$ID.getText()",		"['$ID'<6>, '.getText()'<26>]",
        "$ID.text = \"test\";",		"['$ID.text = \"test\";'<23>]",
        "$a.line == $b.line",		"['$a.line'<15>, ' == '<26>, '$b.line'<15>]",
        "$r.tree",		"['$r.tree'<15>]",
        "foo $a::n bar",		"['foo '<26>, '$a::n'<11>, ' bar'<26>]",
        "$Symbols[-1]::names.add($id.text);",		"['$Symbols[-1]::names'<10>, '.add('<26>, '$id.text'<15>, ');'<26>]",
        "$Symbols[0]::names.add($id.text);",		"['$Symbols[0]::names'<9>, '.add('<26>, '$id.text'<15>, ');'<26>]",
        "$Symbols::x;",		"['$Symbols::x'<11>, ';'<26>]",
        "$Symbols.size()>0",		"['$Symbols'<6>, '.size()>0'<26>]",
        "$field::x = $field.st;",		"['$field::x = $field.st;'<21>]",
        "$foo.get(\"ick\");",		"['$foo'<6>, '.get(\"ick\");'<26>]",
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
