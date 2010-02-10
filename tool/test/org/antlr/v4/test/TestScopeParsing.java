package org.antlr.v4.test;

import org.antlr.v4.parse.ScopeParser;
import org.junit.Test;

public class TestScopeParsing extends BaseTest {
    String[] argPairs = {
        "int i",                            "{i=int i}",
        "int[] i, int j[]",                 "{i=int[] i, j=int [] j}",
        "Map<A\\,B>[] i, int j[]",          "{i=Map<A,B>[] i, j=int [] j}",
        "int i = 34+a[3], int j[] = new int[34]",
                                            "{i=int i= 34+a[3], j=int [] j= new int[34]}",
        "char *foo32[3] = {1\\,2\\,3}",     "{3=char *foo32[] 3= {1,2,3}}",

        // python/ruby style
        "i",                                "{i=null i}",
        "i,j",                              "{i=null i, j=null j}",
        "i,j, k",                           "{i=null i, j=null j, k=null k}",
    };

    String[] scopePairs = {
        "int i;",                           "{i=int i}",
        "int[] i; int j[];",                "{i=int[] i, j=int [] j}",
        "Map<A,B>[] i; int j[];",           "{i=Map<A,B>[] i, j=int [] j}",
        "int i = 34+a[3]; int j[] = new int[34];",
                                            "{i=int i= 34+a[3], j=int [] j= new int[34]}",
        "char *foo32[] = {1,2,3};",         "{foo32=char *[] foo32= {1,2,3}}",

        // python/ruby style
        "i",                                "{i=null i}",
        "i; j;",                            "{i=null i, j=null j}",
        "i; j; k;",                         "{i=null i, j=null j, k=null k}",
    };

    public void testInputOutputPairs(String[] pairs) {
        for (int i = 0; i < pairs.length; i+=2) {
            String input = pairs[i];
            String expected = pairs[i+1];
            String actual = ScopeParser.parseTypeList(input).attributes.toString();
            assertEquals(expected, actual);
        }
    }

    @Test public void testArgs() {
        for (int i = 0; i < argPairs.length; i+=2) {
            String input = argPairs[i];
            String expected = argPairs[i+1];
            String actual = ScopeParser.parseTypeList(input).attributes.toString();
            assertEquals(expected, actual);
        }
    }

    @Test public void testScopes() {
        for (int i = 0; i < scopePairs.length; i+=2) {
            String input = scopePairs[i];
            String expected = scopePairs[i+1];
            String actual = ScopeParser.parseDynamicScope(input).attributes.toString();
            assertEquals(expected, actual);
        }
    }
}
