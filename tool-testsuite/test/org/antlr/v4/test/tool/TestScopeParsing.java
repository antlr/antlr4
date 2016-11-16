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

import org.antlr.v4.misc.Utils;
import org.antlr.v4.parse.ScopeParser;
import org.antlr.v4.test.runtime.java.BaseJavaTest;
import org.antlr.v4.tool.Attribute;
import org.antlr.v4.tool.Grammar;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TestScopeParsing extends BaseJavaTest {
    static String[] argPairs = {
        "",                                 "",
        " ",                                "",
        "int i",                            "i:int",
        "int[] i, int j[]",                 "i:int[], j:int []",
		"Map<A,B>[] i, int j[]",          	"i:Map<A,B>[], j:int []",
		"Map<A,List<B>>[] i",	          	"i:Map<A,List<B>>[]",
        "int i = 34+a[3], int j[] = new int[34]",
                                            "i:int=34+a[3], j:int []=new int[34]",
        "char *[3] foo = {1,2,3}",     	    "foo:char *[3]={1,2,3}", // not valid C really, C is "type name" however so this is cool (this was broken in 4.5 anyway)
		"String[] headers",					"headers:String[]",

        // python/ruby style
        "i",                                "i",
        "i,j",                              "i, j",
        "i\t,j, k",                         "i, j, k",

	    // swift style
	    "x: int",                           "x:int",
	    "x :int",                           "x:int",
	    "x:int",                            "x:int",
	    "x:int=3",                          "x:int=3",
	    "r:Rectangle=Rectangle(fromLength: 6, fromBreadth: 12)", "r:Rectangle=Rectangle(fromLength: 6, fromBreadth: 12)",
	    "p:pointer to int",                 "p:pointer to int",
	    "a: array[3] of int",               "a:array[3] of int",
	    "a \t:\tfunc(array[3] of int)",     "a:func(array[3] of int)",
	    "x:int, y:float",                   "x:int, y:float",
	    "x:T?, f:func(array[3] of int), y:int", "x:T?, f:func(array[3] of int), y:int",

	    // go is postfix type notation like "x int" but must use either "int x" or "x:int" in [...] actions
	    "float64 x = 3",                    "x:float64=3",
	    "map[string]int x",                 "x:map[string]int",
    };

    String input;
	String output;

	public TestScopeParsing(String input, String output) {
		this.input = input;
		this.output = output;
	}

	@Before
	@Override
	public void testSetUp() throws Exception {
		super.testSetUp();
	}

    @Test
    public void testArgs() throws Exception {
	    Grammar dummy = new Grammar("grammar T; a:'a';");

	    LinkedHashMap<String, Attribute> attributes = ScopeParser.parseTypedArgList(null, input, dummy).attributes;
	    List<String> out = new ArrayList<>();
	    for (String arg : attributes.keySet()) {
		    Attribute attr = attributes.get(arg);
		    out.add(attr.toString());
	    }
	    String actual = Utils.join(out.toArray(), ", ");
	    assertEquals(output, actual);
    }

	@Parameterized.Parameters(name="{0}")
	public static Collection<Object[]> getAllTestDescriptors() {
		List<Object[]> tests = new ArrayList<>();
		for (int i = 0; i < argPairs.length; i+=2) {
			String arg = argPairs[i];
			String output = argPairs[i+1];
			tests.add(new Object[]{arg,output});
		}
		return tests;
	}
}
