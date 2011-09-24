/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.antlr.v4.test;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.junit.Test;

public class TestTrees extends BaseTest {
	ASTAdaptor adaptor = new CommonASTAdaptor();
	protected boolean debug = false;

	static class V extends CommonAST {
		public int x;
		public V(Token t) { this.token = t;}
		public V(int ttype, int x) { this.x=x; token=new CommonToken(ttype); }
		public V(int ttype, Token t, int x) { token=t; this.x=x;}
		public String toString() { return (token!=null?token.getText():"")+"<V>";}
	}

	@Test public void testSingleNode() throws Exception {
		CommonAST t = new CommonAST(new CommonToken(101));
		assertNull(t.parent);
		assertEquals(-1, t.childIndex);
	}

	@Test public void testTwoChildrenOfNilRoot() throws Exception {
		CommonAST root_0 = (CommonAST)adaptor.nil();
		CommonAST t = new V(101, 2);
		CommonAST u = new V(new CommonToken(102,"102"));
		adaptor.addChild(root_0, t);
		adaptor.addChild(root_0, u);
		assertNull(root_0.parent);
		assertEquals(-1, root_0.childIndex);
		assertEquals(0, t.childIndex);
		assertEquals(1, u.childIndex);
	}

	@Test public void test4Nodes() throws Exception {
		// ^(101 ^(102 103) 104)
		CommonAST r0 = new CommonAST(new CommonToken(101));
		r0.addChild(new CommonAST(new CommonToken(102)));
		r0.getChild(0).addChild(new CommonAST(new CommonToken(103)));
		r0.addChild(new CommonAST(new CommonToken(104)));

		assertNull(r0.parent);
		assertEquals(-1, r0.childIndex);
	}

	@Test public void testList() throws Exception {
		// ^(nil 101 102 103)
		CommonAST r0 = new CommonAST((Token)null);
		CommonAST c0, c1, c2;
		r0.addChild(c0=new CommonAST(new CommonToken(101)));
		r0.addChild(c1=new CommonAST(new CommonToken(102)));
		r0.addChild(c2=new CommonAST(new CommonToken(103)));

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
		CommonAST root = new CommonAST(new CommonToken(5));

		// child tree
		CommonAST r0 = new CommonAST((Token)null);
		CommonAST c0, c1, c2;
		r0.addChild(c0=new CommonAST(new CommonToken(101)));
		r0.addChild(c1=new CommonAST(new CommonToken(102)));
		r0.addChild(c2=new CommonAST(new CommonToken(103)));

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
		CommonAST root = new CommonAST(new CommonToken(5));
		root.addChild(new CommonAST(new CommonToken(6)));

		// child tree
		CommonAST r0 = new CommonAST((Token)null);
		CommonAST c0, c1, c2;
		r0.addChild(c0=new CommonAST(new CommonToken(101)));
		r0.addChild(c1=new CommonAST(new CommonToken(102)));
		r0.addChild(c2=new CommonAST(new CommonToken(103)));

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
		CommonAST r0 = new CommonAST(new CommonToken(101));
		CommonAST r1 = new CommonAST(new CommonToken(102));
		r0.addChild(r1);
		r1.addChild(new CommonAST(new CommonToken(103)));
		BaseAST r2 = new CommonAST(new CommonToken(106));
		r2.addChild(new CommonAST(new CommonToken(107)));
		r1.addChild(r2);
		r0.addChild(new CommonAST(new CommonToken(104)));
		r0.addChild(new CommonAST(new CommonToken(105)));

		CommonAST dup = (CommonAST)(new CommonASTAdaptor()).dupTree(r0);

		assertNull(dup.parent);
		assertEquals(-1, dup.childIndex);
		Trees.sanityCheckParentAndChildIndexes(dup);
	}

	@Test public void testBecomeRoot() throws Exception {
		// 5 becomes new root of ^(nil 101 102 103)
		CommonAST newRoot = new CommonAST(new CommonToken(5));

		CommonAST oldRoot = new CommonAST((Token)null);
		oldRoot.addChild(new CommonAST(new CommonToken(101)));
		oldRoot.addChild(new CommonAST(new CommonToken(102)));
		oldRoot.addChild(new CommonAST(new CommonToken(103)));

		ASTAdaptor adaptor = new CommonASTAdaptor();
		adaptor.becomeRoot(newRoot, oldRoot);
		Trees.sanityCheckParentAndChildIndexes(newRoot);
	}

	@Test public void testBecomeRoot2() throws Exception {
		// 5 becomes new root of ^(101 102 103)
		CommonAST newRoot = new CommonAST(new CommonToken(5));

		CommonAST oldRoot = new CommonAST(new CommonToken(101));
		oldRoot.addChild(new CommonAST(new CommonToken(102)));
		oldRoot.addChild(new CommonAST(new CommonToken(103)));

		ASTAdaptor adaptor = new CommonASTAdaptor();
		adaptor.becomeRoot(newRoot, oldRoot);
		Trees.sanityCheckParentAndChildIndexes(newRoot);
	}

	@Test public void testBecomeRoot3() throws Exception {
		// ^(nil 5) becomes new root of ^(nil 101 102 103)
		CommonAST newRoot = new CommonAST((Token)null);
		newRoot.addChild(new CommonAST(new CommonToken(5)));

		CommonAST oldRoot = new CommonAST((Token)null);
		oldRoot.addChild(new CommonAST(new CommonToken(101)));
		oldRoot.addChild(new CommonAST(new CommonToken(102)));
		oldRoot.addChild(new CommonAST(new CommonToken(103)));

		ASTAdaptor adaptor = new CommonASTAdaptor();
		adaptor.becomeRoot(newRoot, oldRoot);
		Trees.sanityCheckParentAndChildIndexes(newRoot);
	}

	@Test public void testBecomeRoot5() throws Exception {
		// ^(nil 5) becomes new root of ^(101 102 103)
		CommonAST newRoot = new CommonAST((Token)null);
		newRoot.addChild(new CommonAST(new CommonToken(5)));

		CommonAST oldRoot = new CommonAST(new CommonToken(101));
		oldRoot.addChild(new CommonAST(new CommonToken(102)));
		oldRoot.addChild(new CommonAST(new CommonToken(103)));

		ASTAdaptor adaptor = new CommonASTAdaptor();
		adaptor.becomeRoot(newRoot, oldRoot);
		Trees.sanityCheckParentAndChildIndexes(newRoot);
	}

	@Test public void testBecomeRoot6() throws Exception {
		// emulates construction of ^(5 6)
		CommonAST root_0 = (CommonAST)adaptor.nil();
		CommonAST root_1 = (CommonAST)adaptor.nil();
		root_1 = (CommonAST)adaptor.becomeRoot(new CommonAST(new CommonToken(5)), root_1);

		adaptor.addChild(root_1, new CommonAST(new CommonToken(6)));

		adaptor.addChild(root_0, root_1);

		Trees.sanityCheckParentAndChildIndexes(root_0);
	}

	// Test replaceChildren

	@Test public void testReplaceWithNoChildren() throws Exception {
		CommonAST t = new CommonAST(new CommonToken(101));
		CommonAST newChild = new CommonAST(new CommonToken(5));
		boolean error = false;
		try {
			Trees.replaceChildren(t, 0, 0, newChild);
		}
		catch (IllegalArgumentException iae) {
			error = true;
		}
		assertTrue(error);
	}

	@Test public void testReplaceWithOneChildren() throws Exception {
		// assume token type 99 and use text
		CommonAST t = new CommonAST(new CommonToken(99,"a"));
		CommonAST c0 = new CommonAST(new CommonToken(99, "b"));
		t.addChild(c0);

		CommonAST newChild = new CommonAST(new CommonToken(99, "c"));
		Trees.replaceChildren(t, 0, 0, newChild);
		String expecting = "(a c)";
		assertEquals(expecting, t.toStringTree());
		Trees.sanityCheckParentAndChildIndexes(t);
	}

	@Test public void testReplaceInMiddle() throws Exception {
		CommonAST t = new CommonAST(new CommonToken(99, "a"));
		t.addChild(new CommonAST(new CommonToken(99, "b")));
		t.addChild(new CommonAST(new CommonToken(99, "c"))); // index 1
		t.addChild(new CommonAST(new CommonToken(99, "d")));

		CommonAST newChild = new CommonAST(new CommonToken(99,"x"));
		Trees.replaceChildren(t, 1, 1, newChild);
		String expecting = "(a b x d)";
		assertEquals(expecting, t.toStringTree());
		Trees.sanityCheckParentAndChildIndexes(t);
	}

	@Test public void testReplaceAtLeft() throws Exception {
		CommonAST t = new CommonAST(new CommonToken(99, "a"));
		t.addChild(new CommonAST(new CommonToken(99, "b"))); // index 0
		t.addChild(new CommonAST(new CommonToken(99, "c")));
		t.addChild(new CommonAST(new CommonToken(99, "d")));

		CommonAST newChild = new CommonAST(new CommonToken(99,"x"));
		Trees.replaceChildren(t, 0, 0, newChild);
		String expecting = "(a x c d)";
		assertEquals(expecting, t.toStringTree());
		Trees.sanityCheckParentAndChildIndexes(t);
	}

	@Test public void testReplaceAtRight() throws Exception {
		CommonAST t = new CommonAST(new CommonToken(99, "a"));
		t.addChild(new CommonAST(new CommonToken(99, "b")));
		t.addChild(new CommonAST(new CommonToken(99, "c")));
		t.addChild(new CommonAST(new CommonToken(99, "d"))); // index 2

		CommonAST newChild = new CommonAST(new CommonToken(99,"x"));
		Trees.replaceChildren(t, 2, 2, newChild);
		String expecting = "(a b c x)";
		assertEquals(expecting, t.toStringTree());
		Trees.sanityCheckParentAndChildIndexes(t);
	}

	@Test public void testReplaceOneWithTwoAtLeft() throws Exception {
		CommonAST t = new CommonAST(new CommonToken(99, "a"));
		t.addChild(new CommonAST(new CommonToken(99, "b")));
		t.addChild(new CommonAST(new CommonToken(99, "c")));
		t.addChild(new CommonAST(new CommonToken(99, "d")));

		CommonAST newChildren = (CommonAST)adaptor.nil();
		newChildren.addChild(new CommonAST(new CommonToken(99,"x")));
		newChildren.addChild(new CommonAST(new CommonToken(99,"y")));

		Trees.replaceChildren(t, 0, 0, newChildren);
		String expecting = "(a x y c d)";
		assertEquals(expecting, t.toStringTree());
		Trees.sanityCheckParentAndChildIndexes(t);
	}

	@Test public void testReplaceOneWithTwoAtRight() throws Exception {
		CommonAST t = new CommonAST(new CommonToken(99, "a"));
		t.addChild(new CommonAST(new CommonToken(99, "b")));
		t.addChild(new CommonAST(new CommonToken(99, "c")));
		t.addChild(new CommonAST(new CommonToken(99, "d")));

		CommonAST newChildren = (CommonAST)adaptor.nil();
		newChildren.addChild(new CommonAST(new CommonToken(99,"x")));
		newChildren.addChild(new CommonAST(new CommonToken(99,"y")));

		Trees.replaceChildren(t, 2, 2, newChildren);
		String expecting = "(a b c x y)";
		assertEquals(expecting, t.toStringTree());
		Trees.sanityCheckParentAndChildIndexes(t);
	}

	@Test public void testReplaceOneWithTwoInMiddle() throws Exception {
		CommonAST t = new CommonAST(new CommonToken(99, "a"));
		t.addChild(new CommonAST(new CommonToken(99, "b")));
		t.addChild(new CommonAST(new CommonToken(99, "c")));
		t.addChild(new CommonAST(new CommonToken(99, "d")));

		CommonAST newChildren = (CommonAST)adaptor.nil();
		newChildren.addChild(new CommonAST(new CommonToken(99,"x")));
		newChildren.addChild(new CommonAST(new CommonToken(99,"y")));

		Trees.replaceChildren(t, 1, 1, newChildren);
		String expecting = "(a b x y d)";
		assertEquals(expecting, t.toStringTree());
		Trees.sanityCheckParentAndChildIndexes(t);
	}

	@Test public void testReplaceTwoWithOneAtLeft() throws Exception {
		CommonAST t = new CommonAST(new CommonToken(99, "a"));
		t.addChild(new CommonAST(new CommonToken(99, "b")));
		t.addChild(new CommonAST(new CommonToken(99, "c")));
		t.addChild(new CommonAST(new CommonToken(99, "d")));

		CommonAST newChild = new CommonAST(new CommonToken(99,"x"));

		Trees.replaceChildren(t, 0, 1, newChild);
		String expecting = "(a x d)";
		assertEquals(expecting, t.toStringTree());
		Trees.sanityCheckParentAndChildIndexes(t);
	}

	@Test public void testReplaceTwoWithOneAtRight() throws Exception {
		CommonAST t = new CommonAST(new CommonToken(99, "a"));
		t.addChild(new CommonAST(new CommonToken(99, "b")));
		t.addChild(new CommonAST(new CommonToken(99, "c")));
		t.addChild(new CommonAST(new CommonToken(99, "d")));

		CommonAST newChild = new CommonAST(new CommonToken(99,"x"));

		Trees.replaceChildren(t, 1, 2, newChild);
		String expecting = "(a b x)";
		assertEquals(expecting, t.toStringTree());
		Trees.sanityCheckParentAndChildIndexes(t);
	}

	@Test public void testReplaceAllWithOne() throws Exception {
		CommonAST t = new CommonAST(new CommonToken(99, "a"));
		t.addChild(new CommonAST(new CommonToken(99, "b")));
		t.addChild(new CommonAST(new CommonToken(99, "c")));
		t.addChild(new CommonAST(new CommonToken(99, "d")));

		CommonAST newChild = new CommonAST(new CommonToken(99,"x"));

		Trees.replaceChildren(t, 0, 2, newChild);
		String expecting = "(a x)";
		assertEquals(expecting, t.toStringTree());
		Trees.sanityCheckParentAndChildIndexes(t);
	}

	@Test public void testReplaceAllWithTwo() throws Exception {
		CommonAST t = new CommonAST(new CommonToken(99, "a"));
		t.addChild(new CommonAST(new CommonToken(99, "b")));
		t.addChild(new CommonAST(new CommonToken(99, "c")));
		t.addChild(new CommonAST(new CommonToken(99, "d")));

		CommonAST newChildren = (CommonAST)adaptor.nil();
		newChildren.addChild(new CommonAST(new CommonToken(99,"x")));
		newChildren.addChild(new CommonAST(new CommonToken(99,"y")));

		Trees.replaceChildren(t, 0, 2, newChildren);
		String expecting = "(a x y)";
		assertEquals(expecting, t.toStringTree());
		Trees.sanityCheckParentAndChildIndexes(t);
	}
}
