package org.antlr.v4.test;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;
import org.antlr.v4.parse.ActionSplitter;
import org.junit.Test;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class TestActionSplitter extends BaseTest {
    static String[] exprs = {
        "foo",		"['foo'<26>]",
        "$x",		"['$x'<16>]",
        "\\$x",		"['\\$x'<26>]",
        "$x.y",		"['$x.y'<8>]",
        "$ID.text",		"['$ID.text'<8>]",
        "$ID",		"['$ID'<16>]",
        "$ID.getText()",		"['$ID'<16>, '.getText()'<26>]",
        "$ID.text = \"test\";",		"['$ID.text = \"test\";'<7>]",
        "$a.line == $b.line",		"['$a.line'<8>, ' == '<26>, '$b.line'<8>]",
        "$r.tree",		"['$r.tree'<8>]",
        "foo $a::n bar",		"['foo '<26>, '$a::n'<9>, ' bar'<26>]",
        "$Symbols[-1]::names.add($id.text);",		"['$Symbols[-1]::names'<12>, '.add('<26>, '$id.text'<8>, ');'<26>]",
        "$Symbols[0]::names.add($id.text);",		"['$Symbols[0]::names'<15>, '.add('<26>, '$id.text'<8>, ');'<26>]",
        "$Symbols::x;",		"['$Symbols::x'<9>, ';'<26>]",
        "$Symbols.size()>0",		"['$Symbols'<16>, '.size()>0'<26>]",
        "$field::x = $field.st;",		"['$field::x = $field.st;'<10>]",
        "$foo.get(\"ick\");",		"['$foo'<16>, '.get(\"ick\");'<26>]",
    };

    @Test public void testExprs() {
        for (int i = 0; i < exprs.length; i+=2) {
            String input = exprs[i];
            String expect = exprs[i+1];
            List<String> chunks = getActionChunks(input);
            assertEquals(expect, chunks.toString());
        }
    }

    public static List<String> getActionChunks(String a) {
        List<String> chunks = new ArrayList<String>();
        ActionSplitter splitter = new ActionSplitter(new ANTLRStringStream(a));
        Token t = splitter.nextToken();
        while ( t.getType()!=Token.EOF ) {
            chunks.add("'"+t.getText()+"'<"+t.getType()+">");
            t = splitter.nextToken();
        }
        return chunks;
    }
}
