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
package org.antlr.v4.test;

import org.antlr.v4.misc.Graph;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/** Test topo sort in GraphNode. */
public class TestTopologicalSort extends BaseTest {
    @Test
    public void testFairlyLargeGraph() throws Exception {
        Graph<String> g = new Graph<String>();
        g.addEdge("C", "F");
        g.addEdge("C", "G");
        g.addEdge("C", "A");
        g.addEdge("C", "B");
        g.addEdge("A", "D");
        g.addEdge("A", "E");
        g.addEdge("B", "E");
        g.addEdge("D", "E");
        g.addEdge("D", "F");
        g.addEdge("F", "H");
        g.addEdge("E", "F");

        String expecting = "[H, F, G, E, D, A, B, C]";
        List<String> nodes = g.sort();
        String result = nodes.toString();
        assertEquals(expecting, result);
    }

    @Test
    public void testCyclicGraph() throws Exception {
        Graph<String> g = new Graph<String>();
        g.addEdge("A", "B");
        g.addEdge("B", "C");
        g.addEdge("C", "A");
        g.addEdge("C", "D");

        String expecting = "[D, C, B, A]";
        List<String> nodes = g.sort();
        String result = nodes.toString();
        assertEquals(expecting, result);
    }

    @Test
    public void testRepeatedEdges() throws Exception {
        Graph<String> g = new Graph<String>();
        g.addEdge("A", "B");
        g.addEdge("B", "C");
        g.addEdge("A", "B"); // dup
        g.addEdge("C", "D");

        String expecting = "[D, C, B, A]";
        List<String> nodes = g.sort();
        String result = nodes.toString();
        assertEquals(expecting, result);
    }

    @Test
    public void testSimpleTokenDependence() throws Exception {
        Graph<String> g = new Graph<String>();
        g.addEdge("Java.g4", "MyJava.tokens"); // Java feeds off manual token file
        g.addEdge("Java.tokens", "Java.g4");
        g.addEdge("Def.g4", "Java.tokens");    // walkers feed off generated tokens
        g.addEdge("Ref.g4", "Java.tokens");

        String expecting = "[MyJava.tokens, Java.g4, Java.tokens, Def.g4, Ref.g4]";
        List<String> nodes = g.sort();
        String result = nodes.toString();
        assertEquals(expecting, result);
    }

    @Test
    public void testParserLexerCombo() throws Exception {
        Graph<String> g = new Graph<String>();
        g.addEdge("JavaLexer.tokens", "JavaLexer.g4");
        g.addEdge("JavaParser.g4", "JavaLexer.tokens");
        g.addEdge("Def.g4", "JavaLexer.tokens");
        g.addEdge("Ref.g4", "JavaLexer.tokens");

        String expecting = "[JavaLexer.g4, JavaLexer.tokens, JavaParser.g4, Def.g4, Ref.g4]";
        List<String> nodes = g.sort();
        String result = nodes.toString();
        assertEquals(expecting, result);
    }
}
