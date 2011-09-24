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
package org.antlr.test;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeAdaptor;
import org.junit.Test;

public class TestTrees extends BaseTest {
	TreeAdaptor adaptor = new CommonTreeAdaptor();
	protected boolean debug = false;

	static class V extends CommonTree {
		public int x;
		public V(Token t) { this.token = t;}
		public V(int ttype, int x) { this.x=x; token=new CommonToken(ttype); }
		public V(int ttype, Token t, int x) { token=t; this.x=x;}
		public String toString() { return (token!=null?token.getText():"")+"<V>";}
	}

	@Test public void testSingleNode() throws Exception {
		CommonTree t = new CommonTree(new CommonToken(101));
		assertNull(t.parent);
		assertEquals(-1, t.childIndex);
	}

	@Test public void testTwoChildrenOfNilRoot() throws Exception {
		CommonTree root_0 = (CommonTree)adaptor.nil();
		CommonTree t = new V(101, 2);
		CommonTree u = new V(new CommonToken(102,"102"));
		adaptor.addChild(root_0, t);
		adaptor.addChild(root_0, u);
		assertNull(root_0.parent);
		assertEquals(-1, root_0.childIndex);
		assertEquals(0, t.childIndex);
		assertEquals(1, u.childIndex);
	}

	@Test public void test4Nodes() throws Exception {
		// ^(101 ^(102 103) 104)
		CommonTree r0 = new CommonTree(new CommonToken(101));
		r0.addChild(new CommonTree(new CommonToken(102)));
		r0.getChild(0).addChild(new CommonTree(new CommonToken(103)));
		r0.addChild(new CommonTree(new CommonToken(104)));

		assertNull(r0.parent);
		assertEquals(-1, r0.childIndex);
	}

	@Test public void testList() throws Exception {
		// ^(nil 101 102 103)
		CommonTree r0 = new CommonTree((Token)null);
		CommonTree c0, c1, c2;
		r0.addChild(c0=new CommonTree(new CommonToken(101)));
		r0.addChild(c1=new CommonTree(new CommonToken(102)));
		r0.addChild(c2=new CommonTree(new CommonToken(103)));

		assertNull(r0.parent);
		assertEquals(-1, r0.childIndex);
		assertEquals(r0, c0.parent);
		assertEquals(0, c0.childIndex);
		assertEquals(r0, c1.parent);
		assertEquals(1, c1.childIndex);		
		assertEquals(r0, c2.parent);
		assertEquals(2, c2.childIndex);
	}

	@Test public void testList2() throws Exception {
		// Add child ^(nil 101 102 103) to root 5
		// should pull 101 102 103 directly to become 5's child list
		CommonTree root = new CommonTree(new CommonToken(5));

		// child tree
		CommonTree r0 = new CommonTree((Token)null);
		CommonTree c0, c1, c2;
		r0.addChild(c0=new CommonTree(new CommonToken(101)));
		r0.addChild(c1=new CommonTree(new CommonToken(102)));
		r0.addChild(c2=new CommonTree(new CommonToken(103)));

		root.addChild(r0);

		assertNull(root.parent);
		assertEquals(-1, root.childIndex);
		// check children of root all point at root
		assertEquals(root, c0.parent);
		assertEquals(0, c0.childIndex);
		assertEquals(root, c0.parent);
		assertEquals(1, c1.childIndex);
		assertEquals(root, c0.parent);
		assertEquals(2, c2.childIndex);
	}

	@Test public void testAddListToExistChildren() throws Exception {
		// Add child ^(nil 101 102 103) to root ^(5 6)
		// should add 101 102 103 to end of 5's child list
		CommonTree root = new CommonTree(new CommonToken(5));
		root.addChild(new CommonTree(new CommonToken(6)));

		// child tree
		CommonTree r0 = new CommonTree((Token)null);
		CommonTree c0, c1, c2;
		r0.addChild(c0=new CommonTree(new CommonToken(101)));
		r0.addChild(c1=new CommonTree(new CommonToken(102)));
		r0.addChild(c2=new CommonTree(new CommonToken(103)));

		root.addChild(r0);

		assertNull(root.parent);
		assertEquals(-1, root.childIndex);
		// check children of root all point at root
		assertEquals(root, c0.parent);
		assertEquals(1, c0.childIndex);
		assertEquals(root, c0.parent);
		assertEquals(2, c1.childIndex);
		assertEquals(root, c0.parent);
		assertEquals(3, c2.childIndex);
	}

	@Test public void testDupTree() throws Exception {
		// ^(101 ^(102 103 ^(106 107) ) 104 105)
		CommonTree r0 = new CommonTree(new CommonToken(101));
		CommonTree r1 = new CommonTree(new CommonToken(102));
		r0.addChild(r1);
		r1.addChild(new CommonTree(new CommonToken(103)));
		Tree r2 = new CommonTree(new CommonToken(106));
		r2.addChild(new CommonTree(new CommonToken(107)));
		r1.addChild(r2);
		r0.addChild(new CommonTree(new CommonToken(104)));
		r0.addChild(new CommonTree(new CommonToken(105)));

		CommonTree dup = (CommonTree)(new CommonTreeAdaptor()).dupTree(r0);

		assertNull(dup.parent);
		assertEquals(-1, dup.childIndex);
		dup.sanityCheckParentAndChildIndexes();
	}

	@Test public void testBecomeRoot() throws Exception {
		// 5 becomes new root of ^(nil 101 102 103)
		CommonTree newRoot = new CommonTree(new CommonToken(5));

		CommonTree oldRoot = new CommonTree((Token)null);
		oldRoot.addChild(new CommonTree(new CommonToken(101)));
		oldRoot.addChild(new CommonTree(new CommonToken(102)));
		oldRoot.addChild(new CommonTree(new CommonToken(103)));

		TreeAdaptor adaptor = new CommonTreeAdaptor();
		adaptor.becomeRoot(newRoot, oldRoot);
		newRoot.sanityCheckParentAndChildIndexes();
	}

	@Test public void testBecomeRoot2() throws Exception {
		// 5 becomes new root of ^(101 102 103)
		CommonTree newRoot = new CommonTree(new CommonToken(5));

		CommonTree oldRoot = new CommonTree(new CommonToken(101));
		oldRoot.addChild(new CommonTree(new CommonToken(102)));
		oldRoot.addChild(new CommonTree(new CommonToken(103)));

		TreeAdaptor adaptor = new CommonTreeAdaptor();
		adaptor.becomeRoot(newRoot, oldRoot);
		newRoot.sanityCheckParentAndChildIndexes();
	}

	@Test public void testBecomeRoot3() throws Exception {
		// ^(nil 5) becomes new root of ^(nil 101 102 103)
		CommonTree newRoot = new CommonTree((Token)null);
		newRoot.addChild(new CommonTree(new CommonToken(5)));

		CommonTree oldRoot = new CommonTree((Token)null);
		oldRoot.addChild(new CommonTree(new CommonToken(101)));
		oldRoot.addChild(new CommonTree(new CommonToken(102)));
		oldRoot.addChild(new CommonTree(new CommonToken(103)));

		TreeAdaptor adaptor = new CommonTreeAdaptor();
		adaptor.becomeRoot(newRoot, oldRoot);
		newRoot.sanityCheckParentAndChildIndexes();
	}

	@Test public void testBecomeRoot5() throws Exception {
		// ^(nil 5) becomes new root of ^(101 102 103)
		CommonTree newRoot = new CommonTree((Token)null);
		newRoot.addChild(new CommonTree(new CommonToken(5)));

		CommonTree oldRoot = new CommonTree(new CommonToken(101));
		oldRoot.addChild(new CommonTree(new CommonToken(102)));
		oldRoot.addChild(new CommonTree(new CommonToken(103)));

		TreeAdaptor adaptor = new CommonTreeAdaptor();
		adaptor.becomeRoot(newRoot, oldRoot);
		newRoot.sanityCheckParentAndChildIndexes();
	}

	@Test public void testBecomeRoot6() throws Exception {
		// emulates construction of ^(5 6)
		CommonTree root_0 = (CommonTree)adaptor.nil();
		CommonTree root_1 = (CommonTree)adaptor.nil();
		root_1 = (CommonTree)adaptor.becomeRoot(new CommonTree(new CommonToken(5)), root_1);

		adaptor.addChild(root_1, new CommonTree(new CommonToken(6)));

		adaptor.addChild(root_0, root_1);

		root_0.sanityCheckParentAndChildIndexes();
	}

	// Test replaceChildren

	@Test public void testReplaceWithNoChildren() throws Exception {
		CommonTree t = new CommonTree(new CommonToken(101));
		CommonTree newChild = new CommonTree(new CommonToken(5));
		boolean error = false;
		try {
			t.replaceChildren(0, 0, newChild);
		}
		catch (IllegalArgumentException iae) {
			error = true;
		}
		assertTrue(error);
	}

	@Test public void testReplaceWithOneChildren() throws Exception {
		// assume token type 99 and use text
		CommonTree t = new CommonTree(new CommonToken(99,"a"));
		CommonTree c0 = new CommonTree(new CommonToken(99, "b"));
		t.addChild(c0);

		CommonTree newChild = new CommonTree(new CommonToken(99, "c"));
		t.replaceChildren(0, 0, newChild);
		String expecting = "(a c)";
		assertEquals(expecting, t.toStringTree());
		t.sanityCheckParentAndChildIndexes();
	}

	@Test public void testReplaceInMiddle() throws Exception {
		CommonTree t = new CommonTree(new CommonToken(99, "a"));
		t.addChild(new CommonTree(new CommonToken(99, "b")));
		t.addChild(new CommonTree(new CommonToken(99, "c"))); // index 1
		t.addChild(new CommonTree(new CommonToken(99, "d")));

		CommonTree newChild = new CommonTree(new CommonToken(99,"x"));
		t.replaceChildren(1, 1, newChild);
		String expecting = "(a b x d)";
		assertEquals(expecting, t.toStringTree());
		t.sanityCheckParentAndChildIndexes();
	}

	@Test public void testReplaceAtLeft() throws Exception {
		CommonTree t = new CommonTree(new CommonToken(99, "a"));
		t.addChild(new CommonTree(new CommonToken(99, "b"))); // index 0
		t.addChild(new CommonTree(new CommonToken(99, "c")));
		t.addChild(new CommonTree(new CommonToken(99, "d")));

		CommonTree newChild = new CommonTree(new CommonToken(99,"x"));
		t.replaceChildren(0, 0, newChild);
		String expecting = "(a x c d)";
		assertEquals(expecting, t.toStringTree());
		t.sanityCheckParentAndChildIndexes();
	}

	@Test public void testReplaceAtRight() throws Exception {
		CommonTree t = new CommonTree(new CommonToken(99, "a"));
		t.addChild(new CommonTree(new CommonToken(99, "b")));
		t.addChild(new CommonTree(new CommonToken(99, "c")));
		t.addChild(new CommonTree(new CommonToken(99, "d"))); // index 2

		CommonTree newChild = new CommonTree(new CommonToken(99,"x"));
		t.replaceChildren(2, 2, newChild);
		String expecting = "(a b c x)";
		assertEquals(expecting, t.toStringTree());
		t.sanityCheckParentAndChildIndexes();
	}

	@Test public void testReplaceOneWithTwoAtLeft() throws Exception {
		CommonTree t = new CommonTree(new CommonToken(99, "a"));
		t.addChild(new CommonTree(new CommonToken(99, "b")));
		t.addChild(new CommonTree(new CommonToken(99, "c")));
		t.addChild(new CommonTree(new CommonToken(99, "d")));

		CommonTree newChildren = (CommonTree)adaptor.nil();
		newChildren.addChild(new CommonTree(new CommonToken(99,"x")));
		newChildren.addChild(new CommonTree(new CommonToken(99,"y")));

		t.replaceChildren(0, 0, newChildren);
		String expecting = "(a x y c d)";
		assertEquals(expecting, t.toStringTree());
		t.sanityCheckParentAndChildIndexes();
	}

	@Test public void testReplaceOneWithTwoAtRight() throws Exception {
		CommonTree t = new CommonTree(new CommonToken(99, "a"));
		t.addChild(new CommonTree(new CommonToken(99, "b")));
		t.addChild(new CommonTree(new CommonToken(99, "c")));
		t.addChild(new CommonTree(new CommonToken(99, "d")));

		CommonTree newChildren = (CommonTree)adaptor.nil();
		newChildren.addChild(new CommonTree(new CommonToken(99,"x")));
		newChildren.addChild(new CommonTree(new CommonToken(99,"y")));

		t.replaceChildren(2, 2, newChildren);
		String expecting = "(a b c x y)";
		assertEquals(expecting, t.toStringTree());
		t.sanityCheckParentAndChildIndexes();
	}

	@Test public void testReplaceOneWithTwoInMiddle() throws Exception {
		CommonTree t = new CommonTree(new CommonToken(99, "a"));
		t.addChild(new CommonTree(new CommonToken(99, "b")));
		t.addChild(new CommonTree(new CommonToken(99, "c")));
		t.addChild(new CommonTree(new CommonToken(99, "d")));

		CommonTree newChildren = (CommonTree)adaptor.nil();
		newChildren.addChild(new CommonTree(new CommonToken(99,"x")));
		newChildren.addChild(new CommonTree(new CommonToken(99,"y")));

		t.replaceChildren(1, 1, newChildren);
		String expecting = "(a b x y d)";
		assertEquals(expecting, t.toStringTree());
		t.sanityCheckParentAndChildIndexes();
	}

	@Test public void testReplaceTwoWithOneAtLeft() throws Exception {
		CommonTree t = new CommonTree(new CommonToken(99, "a"));
		t.addChild(new CommonTree(new CommonToken(99, "b")));
		t.addChild(new CommonTree(new CommonToken(99, "c")));
		t.addChild(new CommonTree(new CommonToken(99, "d")));

		CommonTree newChild = new CommonTree(new CommonToken(99,"x"));

		t.replaceChildren(0, 1, newChild);
		String expecting = "(a x d)";
		assertEquals(expecting, t.toStringTree());
		t.sanityCheckParentAndChildIndexes();
	}

	@Test public void testReplaceTwoWithOneAtRight() throws Exception {
		CommonTree t = new CommonTree(new CommonToken(99, "a"));
		t.addChild(new CommonTree(new CommonToken(99, "b")));
		t.addChild(new CommonTree(new CommonToken(99, "c")));
		t.addChild(new CommonTree(new CommonToken(99, "d")));

		CommonTree newChild = new CommonTree(new CommonToken(99,"x"));

		t.replaceChildren(1, 2, newChild);
		String expecting = "(a b x)";
		assertEquals(expecting, t.toStringTree());
		t.sanityCheckParentAndChildIndexes();
	}

	@Test public void testReplaceAllWithOne() throws Exception {
		CommonTree t = new CommonTree(new CommonToken(99, "a"));
		t.addChild(new CommonTree(new CommonToken(99, "b")));
		t.addChild(new CommonTree(new CommonToken(99, "c")));
		t.addChild(new CommonTree(new CommonToken(99, "d")));

		CommonTree newChild = new CommonTree(new CommonToken(99,"x"));

		t.replaceChildren(0, 2, newChild);
		String expecting = "(a x)";
		assertEquals(expecting, t.toStringTree());
		t.sanityCheckParentAndChildIndexes();
	}

	@Test public void testReplaceAllWithTwo() throws Exception {
		CommonTree t = new CommonTree(new CommonToken(99, "a"));
		t.addChild(new CommonTree(new CommonToken(99, "b")));
		t.addChild(new CommonTree(new CommonToken(99, "c")));
		t.addChild(new CommonTree(new CommonToken(99, "d")));

		CommonTree newChildren = (CommonTree)adaptor.nil();
		newChildren.addChild(new CommonTree(new CommonToken(99,"x")));
		newChildren.addChild(new CommonTree(new CommonToken(99,"y")));

		t.replaceChildren(0, 2, newChildren);
		String expecting = "(a x y)";
		assertEquals(expecting, t.toStringTree());
		t.sanityCheckParentAndChildIndexes();
	}
}
