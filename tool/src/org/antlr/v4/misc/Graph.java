/*
 * [The "BSD license"]
 *  Copyright (c) 2010 Terence Parr
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *      derived from this software without specific prior written permission.
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
package org.antlr.v4.misc;

import org.antlr.v4.runtime.misc.OrderedHashSet;

import java.util.*;

/** A generic graph with edges; Each node as a single Object payload.
 *  This is only used to topologically sort a list of file dependencies
 *  at the moment.
 */
public class Graph {

    public static class Node {
        Object payload;
        List<Node> edges; // points at which nodes?

        public Node(Object payload) { this.payload = payload; }

        public void addEdge(Node n) {
            if ( edges==null ) edges = new ArrayList<Node>();
            if ( !edges.contains(n) ) edges.add(n);
        }

        @Override
        public String toString() { return payload.toString(); }
    }

    /** Map from node payload to node containing it */
    protected Map<Object,Node> nodes = new HashMap<Object,Node>();

    public void addEdge(Object a, Object b) {
        //System.out.println("add edge "+a+" to "+b);
        Node a_node = getNode(a);
        Node b_node = getNode(b);
        a_node.addEdge(b_node);
    }

    protected Node getNode(Object a) {
        Node existing = nodes.get(a);
        if ( existing!=null ) return existing;
        Node n = new Node(a);
        nodes.put(a, n);
        return n;
    }

    /** DFS-based topological sort.  A valid sort is the reverse of
     *  the post-order DFA traversal.  Amazingly simple but true.
     *  For sorting, I'm not following convention here since ANTLR
     *  needs the opposite.  Here's what I assume for sorting:
     *
     *    If there exists an edge u -> v then u depends on v and v
     *    must happen before u.
     *
     *  So if this gives nonreversed postorder traversal, I get the order
     *  I want.
     */
    public List<Object> sort() {
        Set<Node> visited = new OrderedHashSet<Node>();
        ArrayList<Object> sorted = new ArrayList<Object>();
        while ( visited.size() < nodes.size() ) {
            // pick any unvisited node, n
            Node n = null;
            for (Iterator it = nodes.values().iterator(); it.hasNext();) {
                n = (Node)it.next();
                if ( !visited.contains(n) ) break;
            }
            DFS(n, visited, sorted);
        }
        return sorted;
    }

    public void DFS(Node n, Set<Node> visited, ArrayList<Object> sorted) {
        if ( visited.contains(n) ) return;
        visited.add(n);
        if ( n.edges!=null ) {
            for (Iterator it = n.edges.iterator(); it.hasNext();) {
                Node target = (Node) it.next();
                DFS(target, visited, sorted);
            }
        }
        sorted.add(n.payload);
    }
}