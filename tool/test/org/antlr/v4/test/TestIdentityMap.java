package org.antlr.v4.test;

import org.antlr.v4.runtime.tree.IdentityMap;
import org.junit.Test;

import java.util.*;

public class TestIdentityMap extends BaseTest {
	static class Node {
		int x;
		public Node(int x) {this.x=x;}

		@Override
		public boolean equals(Object obj) {
			if ( !(obj instanceof Node) ) return false;
			return this==obj || this.x == ((Node)obj).x;
		}

		@Override
		public String toString() {
			return String.valueOf(x);
		}
	}

	@Test public void testBasic() {
		IdentityMap<String, Integer> m = new IdentityMap<String, Integer>();
		m.put("a", 1);
		m.put("b", 2);
		m.put("c", 3);
		m.put("d", 4);
		assertEquals(1, (int)m.get("a"));
		assertEquals(2, (int)m.get("b"));
		assertEquals(3, (int)m.get("c"));
		assertEquals(4, (int)m.get("d"));
		assertEquals(IdentityMap.INITIAL_CAPACITY, m.getNumberOfBuckets());
	}

	@Test public void testAllowNullValue() {
		IdentityMap<String, Integer> m = new IdentityMap<String, Integer>();
		m.put("a", null);
		assertEquals("{a:null}", m.toString());
		assertEquals(IdentityMap.INITIAL_CAPACITY, m.getNumberOfBuckets());
	}

	@Test public void testUpdateValue() {
		IdentityMap<String, Integer> m = new IdentityMap<String, Integer>();
		m.put("a", 34);
		m.put("a", 99);
		assertEquals(99, (int) m.get("a"));
		assertEquals(IdentityMap.INITIAL_CAPACITY, m.getNumberOfBuckets());
	}

	@Test public void testIdentityNotEquals() {
		IdentityMap<Node, Integer> m = new IdentityMap<Node, Integer>();
		Node a = new Node(1);
		Node b = new Node(1);
		m.put(a, 34);
		m.put(b, 99);
		assertEquals(34, (int) m.get(a));
		assertEquals(99, (int) m.get(b));
		assertEquals(IdentityMap.INITIAL_CAPACITY, m.getNumberOfBuckets());
	}

	@Test public void testKeySet() {
		IdentityMap<String, Integer> m = new IdentityMap<String, Integer>();
		m.put("a", 1);
		m.put("b", 2);
		m.put("c", 3);
		m.put("d", 4);
		List<String> keys = new ArrayList<String>();
		keys.addAll(m.keySet());
		Collections.sort(keys);
		assertEquals("[a, b, c, d]", keys.toString());
		assertEquals(IdentityMap.INITIAL_CAPACITY, m.getNumberOfBuckets());
	}

	@Test public void testValues() {
		IdentityMap<String, Integer> m = new IdentityMap<String, Integer>();
		m.put("a", 1);
		m.put("b", 2);
		m.put("c", 3);
		m.put("d", 4);
		List<Integer> keys = new ArrayList<Integer>();
		keys.addAll(m.values());
		Collections.sort(keys);
		assertEquals("[1, 2, 3, 4]", keys.toString());
		assertEquals(IdentityMap.INITIAL_CAPACITY, m.getNumberOfBuckets());
	}

	@Test public void testRemove() {
		IdentityMap<String, Integer> m = new IdentityMap<String, Integer>();
		m.put("a", 1);
		m.put("b", 2);
		m.put("c", 3);
		m.put("d", 4);
		m.remove("c");
		m.remove("a");
		List<Integer> keys = new ArrayList<Integer>();
		keys.addAll(m.values());
		Collections.sort(keys);
		assertEquals("[2, 4]", keys.toString());
		assertEquals(IdentityMap.INITIAL_CAPACITY, m.getNumberOfBuckets());
	}

	@Test public void testContainsKey() {
		IdentityMap<String, Integer> m = new IdentityMap<String, Integer>();
		m.put("a", 1);
		m.put("b", 2);
		m.put("c", 3);
		m.put("d", 4);
		assertTrue(m.containsKey("a"));
		assertTrue(m.containsKey("b"));
		assertTrue(m.containsKey("c"));
		assertTrue(m.containsKey("d"));
		assertEquals(IdentityMap.INITIAL_CAPACITY, m.getNumberOfBuckets());
	}

	@Test public void testPutAll() {
		IdentityMap<String, Integer> m = new IdentityMap<String, Integer>();
		m.put("a", 1);
		m.put("b", 2);
		m.put("c", 3);
		m.put("d", 4);

		IdentityMap<String, Integer> m2 = new IdentityMap<String, Integer>();
		m2.putAll(m);

		List<String> keys = new ArrayList<String>();
		keys.addAll(m.keySet());
		Collections.sort(keys);
		assertEquals("[a, b, c, d]", keys.toString());
		assertEquals(IdentityMap.INITIAL_CAPACITY, m.getNumberOfBuckets());
	}

	@Test public void testRehash() {
		IdentityMap<String, Integer> m = new IdentityMap<String, Integer>(3);
		assertEquals(4, m.getNumberOfBuckets());

		m.put("a", 1);
		m.put("bob", 2);
		m.put("c", 3); // 3 out of 4 is .75 load factor
		m.put("d", 4); // should force rehash on 4/4
		assertEquals(8, m.getNumberOfBuckets());

		m.put("e", 5);
		m.put("f", 6); // no rehash
		m.put("g", 7); // rehash
		assertEquals(16, m.getNumberOfBuckets());

		assertEquals(1, (int)m.get("a"));
		assertEquals(2, (int)m.get("bob"));
		assertEquals(3, (int)m.get("c"));
		assertEquals(4, (int)m.get("d"));
		assertEquals(5, (int)m.get("e"));
		assertEquals(6, (int)m.get("f"));
		assertEquals(7, (int)m.get("g"));
		assertEquals(16, m.getNumberOfBuckets());
	}

}
