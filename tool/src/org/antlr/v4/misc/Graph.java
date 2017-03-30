/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.misc;

import org.antlr.v4.runtime.misc.OrderedHashSet;

import java.util.ArrayList;
import java.util.Collections;
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
		public T payload;

		@SuppressWarnings("unchecked")
		public List<Node<T>> edges = Collections.EMPTY_LIST; // points at which nodes?

		public Node(T payload) { this.payload = payload; }

		public void addEdge(Node<T> n) {
			if ( edges==Collections.EMPTY_LIST ) edges = new ArrayList<Node<T>>();
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

	public Node<T> getNode(T a) {
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
