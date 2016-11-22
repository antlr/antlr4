/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.test.tool;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;
import org.antlr.v4.parse.ActionSplitter;
import org.antlr.v4.semantics.BlankActionSplitterListener;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestActionSplitter extends BaseJavaToolTest {
	@Before
	@Override
	public void testSetUp() throws Exception {
		super.testSetUp();
	}

    static String[] exprs = {
        "foo",		"['foo'<" + ActionSplitter.TEXT + ">]",
        "$x",		"['$x'<" + ActionSplitter.ATTR + ">]",
        "\\$x",		"['\\$x'<" + ActionSplitter.TEXT + ">]",
        "$x.y",		"['$x.y'<" + ActionSplitter.QUALIFIED_ATTR + ">]",
        "$ID.text",		"['$ID.text'<" + ActionSplitter.QUALIFIED_ATTR + ">]",
        "$ID",		"['$ID'<" + ActionSplitter.ATTR + ">]",
        "$ID.getText()",		"['$ID'<" + ActionSplitter.ATTR + ">, '.getText()'<" + ActionSplitter.TEXT + ">]",
        "$ID.text = \"test\";",		"['$ID.text'<" + ActionSplitter.QUALIFIED_ATTR + ">, ' = \"test\";'<" + ActionSplitter.TEXT + ">]",
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
