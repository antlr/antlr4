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

import org.antlr.v4.parse.ScopeParser;
import org.antlr.v4.test.runtime.java.BaseTest;
import org.antlr.v4.tool.Grammar;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestScopeParsing extends BaseTest {
    String[] argPairs = {
        "",                                 "{}",
        " ",                                "{}",
        "int i",                            "{i=int i}",
        "int[] i, int j[]",                 "{i=int[] i, j=int [] j}",
		"Map<A,B>[] i, int j[]",          	"{i=Map<A,B>[] i, j=int [] j}",
		"Map<A,List<B>>[] i",	          	"{i=Map<A,List<B>>[] i}",
        "int i = 34+a[3], int j[] = new int[34]",
                                            "{i=int i= 34+a[3], j=int [] j= new int[34]}",
        "char *foo32[3] = {1,2,3}",     	"{3=char *foo32[] 3= {1,2,3}}",
		"String[] headers",					"{headers=String[] headers}",

        // python/ruby style
        "i",                                "{i=null i}",
        "i,j",                              "{i=null i, j=null j}",
        "i,j, k",                           "{i=null i, j=null j, k=null k}",
    };

    @Test public void testArgs() throws Exception {
        for (int i = 0; i < argPairs.length; i+=2) {
            String input = argPairs[i];
            String expected = argPairs[i+1];
			Grammar dummy = new Grammar("grammar T; a:'a';");
			String actual = ScopeParser.parseTypedArgList(null, input, dummy).attributes.toString();
            assertEquals(expected, actual);
        }
    }
}
