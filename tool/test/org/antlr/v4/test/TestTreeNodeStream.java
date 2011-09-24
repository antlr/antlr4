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
import org.antlr.runtime.tree.*;
import org.junit.Test;

/** Test the tree node stream. */
public class TestTreeNodeStream extends BaseTest {

	/** Build new stream; let's us override to test other streams. */
	public TreeNodeStream newStream(Object t) {
		return new CommonTreeNodeStream(t);
	}

    public String toTokenTypeString(TreeNodeStream stream) {
        return ((CommonTreeNodeStream)stream).toTokenTypeString();
    }

	@Test public void testSingleNode() throws Exception {
		Tree t = new CommonTree(new CommonToken(101));

		TreeNodeStream stream = newStream(t);
		String expecting = " 101";
		String found = toNodesOnlyString(stream);
		assertEquals(expecting, found);

		expecting = " 101";
		found = toTokenTypeString(stream);
		assertEquals(expecting, found);
	}

	@Test public void test4Nodes() throws Exception {
		// ^(101 ^(102 103) 104)
		Tree t = new CommonTree(new CommonToken(101));
		t.addChild(new CommonTree(new CommonToken(102)));
		t.getChild(0).addChild(new CommonTree(new CommonToken(103)));
		t.addChild(new CommonTree(new CommonToken(104)));

		TreeNodeStream stream = newStream(t);
		String expecting = " 101 102 103 104";
		String found = toNodesOnlyString(stream);
		assertEquals(expecting, found);

		expecting = " 101 2 102 2 103 3 104 3";
		found = toTokenTypeString(stream);
		assertEquals(expecting, found);
	}

	@Test public void testList() throws Exception {
		Tree root = new CommonTree((Token)null);

		Tree t = new CommonTree(new CommonToken(101));
		t.addChild(new CommonTree(new CommonToken(102)));
		t.getChild(0).addChild(new CommonTree(new CommonToken(103)));
		t.addChild(new CommonTree(new CommonToken(104)));

		Tree u = new CommonTree(new CommonToken(105));

		root.addChild(t);
		root.addChild(u);

		TreeNodeStream stream = newStream(root);
		String expecting = " 101 102 103 104 105";
		String found = toNodesOnlyString(stream);
		assertEquals(expecting, found);

		expecting = " 101 2 102 2 103 3 104 3 105";
		found = toTokenTypeString(stream);
		assertEquals(expecting, found);
	}

	@Test public void testFlatList() throws Exception {
		Tree root = new CommonTree((Token)null);

		root.addChild(new CommonTree(new CommonToken(101)));
		root.addChild(new CommonTree(new CommonToken(102)));
		root.addChild(new CommonTree(new CommonToken(103)));

		TreeNodeStream stream = newStream(root);
		String expecting = " 101 102 103";
		String found = toNodesOnlyString(stream);
		assertEquals(expecting, found);

		expecting = " 101 102 103";
		found = toTokenTypeString(stream);
		assertEquals(expecting, found);
	}

	@Test public void testListWithOneNode() throws Exception {
		Tree root = new CommonTree((Token)null);

		root.addChild(new CommonTree(new CommonToken(101)));

		TreeNodeStream stream = newStream(root);
		String expecting = " 101";
		String found = toNodesOnlyString(stream);
		assertEquals(expecting, found);

		expecting = " 101";
		found = toTokenTypeString(stream);
		assertEquals(expecting, found);
	}

	@Test public void testAoverB() throws Exception {
		Tree t = new CommonTree(new CommonToken(101));
		t.addChild(new CommonTree(new CommonToken(102)));

		TreeNodeStream stream = newStream(t);
		String expecting = " 101 102";
		String found = toNodesOnlyString(stream);
		assertEquals(expecting, found);

		expecting = " 101 2 102 3";
		found = toTokenTypeString(stream);
		assertEquals(expecting, found);
	}

	@Test public void testLT() throws Exception {
		// ^(101 ^(102 103) 104)
		Tree t = new CommonTree(new CommonToken(101));
		t.addChild(new CommonTree(new CommonToken(102)));
		t.getChild(0).addChild(new CommonTree(new CommonToken(103)));
		t.addChild(new CommonTree(new CommonToken(104)));

		TreeNodeStream stream = newStream(t);
		assertEquals(101, ((Tree)stream.LT(1)).getType());
		assertEquals(Token.DOWN, ((Tree)stream.LT(2)).getType());
		assertEquals(102, ((Tree)stream.LT(3)).getType());
		assertEquals(Token.DOWN, ((Tree)stream.LT(4)).getType());
		assertEquals(103, ((Tree)stream.LT(5)).getType());
		assertEquals(Token.UP, ((Tree)stream.LT(6)).getType());
		assertEquals(104, ((Tree)stream.LT(7)).getType());
		assertEquals(Token.UP, ((Tree)stream.LT(8)).getType());
		assertEquals(Token.EOF, ((Tree)stream.LT(9)).getType());
		// check way ahead
		assertEquals(Token.EOF, ((Tree)stream.LT(100)).getType());
	}

	@Test public void testMarkRewindEntire() throws Exception {
		// ^(101 ^(102 103 ^(106 107) ) 104 105)
		// stream has 7 real + 6 nav nodes
		// Sequence of types: 101 DN 102 DN 103 106 DN 107 UP UP 104 105 UP EOF
		Tree r0 = new CommonTree(new CommonToken(101));
		Tree r1 = new CommonTree(new CommonToken(102));
		r0.addChild(r1);
		r1.addChild(new CommonTree(new CommonToken(103)));
		Tree r2 = new CommonTree(new CommonToken(106));
		r2.addChild(new CommonTree(new CommonToken(107)));
		r1.addChild(r2);
		r0.addChild(new CommonTree(new CommonToken(104)));
		r0.addChild(new CommonTree(new CommonToken(105)));

		TreeNodeStream stream = newStream(r0);
		int m = stream.mark(); // MARK
		for (int k=1; k<=13; k++) { // consume til end
			stream.LT(1);
			stream.consume();
		}
		assertEquals(Token.EOF, ((Tree)stream.LT(1)).getType());
		stream.rewind(m);      // REWIND

		// consume til end again :)
		for (int k=1; k<=13; k++) { // consume til end
			stream.LT(1);
			stream.consume();
		}
		assertEquals(Token.EOF, ((Tree)stream.LT(1)).getType());
	}

	@Test public void testMarkRewindInMiddle() throws Exception {
		// ^(101 ^(102 103 ^(106 107) ) 104 105)
		// stream has 7 real + 6 nav nodes
		// Sequence of types: 101 DN 102 DN 103 106 DN 107 UP UP 104 105 UP EOF
		Tree r0 = new CommonTree(new CommonToken(101));
		Tree r1 = new CommonTree(new CommonToken(102));
		r0.addChild(r1);
		r1.addChild(new CommonTree(new CommonToken(103)));
		Tree r2 = new CommonTree(new CommonToken(106));
		r2.addChild(new CommonTree(new CommonToken(107)));
		r1.addChild(r2);
		r0.addChild(new CommonTree(new CommonToken(104)));
		r0.addChild(new CommonTree(new CommonToken(105)));

		TreeNodeStream stream = newStream(r0);
		for (int k=1; k<=7; k++) { // consume til middle
			//System.out.println(((Tree)stream.LT(1)).getType());
			stream.consume();
		}
		assertEquals(107, ((Tree)stream.LT(1)).getType());
		stream.mark(); // MARK
		stream.consume(); // consume 107
		stream.consume(); // consume UP
		stream.consume(); // consume UP
		stream.consume(); // consume 104
		stream.rewind(); // REWIND
        stream.mark();   // keep saving nodes though

		assertEquals(107, ((Tree)stream.LT(1)).getType());
		stream.consume();
		assertEquals(Token.UP, ((Tree)stream.LT(1)).getType());
		stream.consume();
		assertEquals(Token.UP, ((Tree)stream.LT(1)).getType());
		stream.consume();
		assertEquals(104, ((Tree)stream.LT(1)).getType());
		stream.consume();
		// now we're past rewind position
		assertEquals(105, ((Tree)stream.LT(1)).getType());
		stream.consume();
		assertEquals(Token.UP, ((Tree)stream.LT(1)).getType());
		stream.consume();
		assertEquals(Token.EOF, ((Tree)stream.LT(1)).getType());
		assertEquals(Token.UP, ((Tree)stream.LT(-1)).getType());
	}

	@Test public void testMarkRewindNested() throws Exception {
		// ^(101 ^(102 103 ^(106 107) ) 104 105)
		// stream has 7 real + 6 nav nodes
		// Sequence of types: 101 DN 102 DN 103 106 DN 107 UP UP 104 105 UP EOF
		Tree r0 = new CommonTree(new CommonToken(101));
		Tree r1 = new CommonTree(new CommonToken(102));
		r0.addChild(r1);
		r1.addChild(new CommonTree(new CommonToken(103)));
		Tree r2 = new CommonTree(new CommonToken(106));
		r2.addChild(new CommonTree(new CommonToken(107)));
		r1.addChild(r2);
		r0.addChild(new CommonTree(new CommonToken(104)));
		r0.addChild(new CommonTree(new CommonToken(105)));

		TreeNodeStream stream = newStream(r0);
		int m = stream.mark(); // MARK at start
		stream.consume(); // consume 101
		stream.consume(); // consume DN
		int m2 = stream.mark(); // MARK on 102
		stream.consume(); // consume 102
		stream.consume(); // consume DN
		stream.consume(); // consume 103
		stream.consume(); // consume 106
		stream.rewind(m2);      // REWIND to 102
		assertEquals(102, ((Tree)stream.LT(1)).getType());
		stream.consume();
		assertEquals(Token.DOWN, ((Tree)stream.LT(1)).getType());
		stream.consume();
		// stop at 103 and rewind to start
		stream.rewind(m); // REWIND to 101
		assertEquals(101, ((Tree)stream.LT(1)).getType());
		stream.consume();
		assertEquals(Token.DOWN, ((Tree)stream.LT(1)).getType());
		stream.consume();
		assertEquals(102, ((Tree)stream.LT(1)).getType());
		stream.consume();
		assertEquals(Token.DOWN, ((Tree)stream.LT(1)).getType());
	}

	@Test public void testSeekFromStart() throws Exception {
		// ^(101 ^(102 103 ^(106 107) ) 104 105)
		// stream has 7 real + 6 nav nodes
		// Sequence of types: 101 DN 102 DN 103 106 DN 107 UP UP 104 105 UP EOF
		Tree r0 = new CommonTree(new CommonToken(101));
		Tree r1 = new CommonTree(new CommonToken(102));
		r0.addChild(r1);
		r1.addChild(new CommonTree(new CommonToken(103)));
		Tree r2 = new CommonTree(new CommonToken(106));
		r2.addChild(new CommonTree(new CommonToken(107)));
		r1.addChild(r2);
		r0.addChild(new CommonTree(new CommonToken(104)));
		r0.addChild(new CommonTree(new CommonToken(105)));

		TreeNodeStream stream = newStream(r0);
		stream.seek(7);   // seek to 107
		assertEquals(107, ((Tree)stream.LT(1)).getType());
		stream.consume(); // consume 107
		stream.consume(); // consume UP
		stream.consume(); // consume UP
		assertEquals(104, ((Tree)stream.LT(1)).getType());
	}

    @Test public void testReset() throws Exception {
        // ^(101 ^(102 103 ^(106 107) ) 104 105)
        // stream has 7 real + 6 nav nodes
        // Sequence of types: 101 DN 102 DN 103 106 DN 107 UP UP 104 105 UP EOF
        Tree r0 = new CommonTree(new CommonToken(101));
        Tree r1 = new CommonTree(new CommonToken(102));
        r0.addChild(r1);
        r1.addChild(new CommonTree(new CommonToken(103)));
        Tree r2 = new CommonTree(new CommonToken(106));
        r2.addChild(new CommonTree(new CommonToken(107)));
        r1.addChild(r2);
        r0.addChild(new CommonTree(new CommonToken(104)));
        r0.addChild(new CommonTree(new CommonToken(105)));

        TreeNodeStream stream = newStream(r0);
        String v = toNodesOnlyString(stream); // scan all
        stream.reset();
        String v2 = toNodesOnlyString(stream); // scan all
        assertEquals(v, v2);
    }

	@Test public void testDeepTree() throws Exception {
		// ^(10 100 101 ^(20 ^(30 40 (50 (60 70)))) (80 90)))
		// stream has 8 real + 10 nav nodes
		int n = 9;
		CommonTree[] nodes = new CommonTree[n];
		for (int i=0; i< n; i++) {
			nodes[i] = new CommonTree(new CommonToken((i+1)*10));
		}
		Tree g = nodes[0];
		Tree rules = nodes[1];
		Tree rule1 = nodes[2];
		Tree id = nodes[3];
		Tree block = nodes[4];
		Tree alt = nodes[5];
		Tree s = nodes[6];
		Tree rule2 = nodes[7];
		Tree id2 = nodes[8];
		g.addChild(new CommonTree(new CommonToken(100)));
		g.addChild(new CommonTree(new CommonToken(101)));
		g.addChild(rules);
		rules.addChild(rule1);
		rule1.addChild(id);
		rule1.addChild(block);
		block.addChild(alt);
		alt.addChild(s);
		rules.addChild(rule2);
		rule2.addChild(id2);

		TreeNodeStream stream = newStream(g);
		String expecting = " 10 2 100 101 20 2 30 2 40 50 2 60 2 70 3 3 3 80 2 90 3 3 3";
		String found = toTokenTypeString(stream);
		assertEquals(expecting, found);
	}

	public String toNodesOnlyString(TreeNodeStream nodes) {
        TreeAdaptor adaptor = nodes.getTreeAdaptor();
		StringBuffer buf = new StringBuffer();
        Object o = nodes.LT(1);
        int type = adaptor.getType(o);
        while ( o!=null && type!=Token.EOF ) {
			if ( !(type==Token.DOWN||type==Token.UP) ) {
				buf.append(" ");
				buf.append(type);
			}
            nodes.consume();
            o = nodes.LT(1);
            type = adaptor.getType(o);
		}
		return buf.toString();
	}
}
