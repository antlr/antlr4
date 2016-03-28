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
package org.antlr.v4.misc;

import org.antlr.v4.runtime.misc.OrderedHashSet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** A generic graph with edges; Each node as a single Object payload.
 *  This is only used to topologically sort a list of file dependencies
 *  at the moment.
 */
public class Graph<T> {

	public static class Node<T> {
		T payload;
		List<Node<T>> edges; // points at which nodes?

		public Node(T payload) { this.payload = payload; }

		public void addEdge(Node<T> n) {
			if ( edges==null ) edges = new ArrayList<Node<T>>();
			if ( !edges.contains(n) ) edges.add(n);
		}

		@Override
		public String toString() { return payload.toString(); }
	}

	/** Map from node payload to node containing it */
	protected Map<T,Node<T>> nodes = new LinkedHashMap<T,Node<T>>();

	public void addEdge(T a, T b) {
		//System.out.println("add edge "+a+" to "+b);
		Node<T> a_node = getNode(a);
		Node<T> b_node = getNode(b);
		a_node.addEdge(b_node);
	}

	protected Node<T> getNode(T a) {
		Node<T> existing = nodes.get(a);
		if ( existing!=null ) return existing;
		Node<T> n = new Node<T>(a);
		nodes.put(a, n);
		return n;
	}

	/** DFS-based topological sort.  A valid sort is the reverse of
	 *  the post-order DFA traversal.  Amazingly simple but true.
	 *  For sorting, I'm not following convention here since ANTLR
	 *  needs the opposite.  Here's what I assume for sorting:
	 *
	 *    If there exists an edge u -&gt; v then u depends on v and v
	 *    must happen before u.
	 *
	 *  So if this gives nonreversed postorder traversal, I get the order
	 *  I want.
	 */
	public List<T> sort() {
		Set<Node<T>> visited = new OrderedHashSet<Node<T>>();
		ArrayList<T> sorted = new ArrayList<T>();
		while ( visited.size() < nodes.size() ) {
			// pick any unvisited node, n
			Node<T> n = null;
			for (Node<T> tNode : nodes.values()) {
				n = tNode;
				if ( !visited.contains(n) ) break;
			}
			if (n!=null) { // if at least one unvisited
				DFS(n, visited, sorted);
			}
		}
		return sorted;
	}

	public void DFS(Node<T> n, Set<Node<T>> visited, ArrayList<T> sorted) {
		if ( visited.contains(n) ) return;
		visited.add(n);
		if ( n.edges!=null ) {
			for (Node<T> target : n.edges) {
				DFS(target, visited, sorted);
			}
		}
		sorted.add(n.payload);
	}
}
