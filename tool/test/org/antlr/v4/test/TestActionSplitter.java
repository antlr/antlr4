package org.antlr.v4.test;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;
import org.antlr.v4.parse.ActionSplitter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestActionSplitter extends BaseTest {
    static String[] exprs = {
        "foo",		"['foo'<29>]",
        "$x",		"['$x'<20>]",
        "\\$x",		"['\\$'<6>, 'x'<29>]",
        "$x.y",		"['$x.y'<11>]",
        "$ID.text",		"['$ID.text'<11>]",
        "$ID",		"['$ID'<20>]",
        "$ID.getText()",		"['$ID'<20>, '.getText()'<29>]",
        "$ID.text = \"test\";",		"['$ID.text = \"test\";'<10>]",
        "$a.line == $b.line",		"['$a.line'<11>, ' == '<29>, '$b.line'<11>]",
        "$r.tree",		"['$r.tree'<11>]",
        "foo $a::n bar",		"['foo '<29>, '$a::n'<13>, ' bar'<29>]",
        "$Symbols[-1]::names.add($id.text);",		"['$Symbols[-1]::names'<16>, '.add('<29>, '$id.text'<11>, ');'<29>]",
        "$Symbols[0]::names.add($id.text);",		"['$Symbols[0]::names'<18>, '.add('<29>, '$id.text'<11>, ');'<29>]",
        "$Symbols::x;",		"['$Symbols::x'<13>, ';'<29>]",
        "$Symbols.size()>0",		"['$Symbols'<20>, '.size()>0'<29>]",
        "$field::x = $field.st;",		"['$field::x = $field.st;'<12>]",
        "$foo.get(\"ick\");",		"['$foo'<20>, '.get(\"ick\");'<29>]",
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
