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

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.TreeAdaptor;
import org.antlr.runtime.tree.TreeWizard;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TestTreeWizard extends BaseTest {
	protected static final String[] tokens =
		new String[] {"", "", "", "", "", "A", "B", "C", "D", "E", "ID", "VAR"};
	protected static final TreeAdaptor adaptor = new CommonTreeAdaptor();

	@Test public void testSingleNode() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("ID");
		String found = t.toStringTree();
		String expecting = "ID";
		assertEquals(expecting, found);
	}

	@Test public void testSingleNodeWithArg() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("ID[foo]");
		String found = t.toStringTree();
		String expecting = "foo";
		assertEquals(expecting, found);
	}

	@Test public void testSingleNodeTree() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("(A)");
		String found = t.toStringTree();
		String expecting = "A";
		assertEquals(expecting, found);
	}

	@Test public void testSingleLevelTree() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("(A B C D)");
		String found = t.toStringTree();
		String expecting = "(A B C D)";
		assertEquals(expecting, found);
	}

	@Test public void testListTree() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("(nil A B C)");
		String found = t.toStringTree();
		String expecting = "A B C";
		assertEquals(expecting, found);
	}

	@Test public void testInvalidListTree() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("A B C");
		assertTrue(t==null);
	}

	@Test public void testDoubleLevelTree() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("(A (B C) (B D) E)");
		String found = t.toStringTree();
		String expecting = "(A (B C) (B D) E)";
		assertEquals(expecting, found);
	}

	@Test public void testSingleNodeIndex() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("ID");
		Map m = wiz.index(t);
		String found = m.toString();
		String expecting = "{10=[ID]}";
		assertEquals(expecting, found);
	}

	@Test public void testNoRepeatsIndex() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("(A B C D)");
		Map m = wiz.index(t);
		String found = sortMapToString(m);
        String expecting = "{5=[A], 6=[B], 7=[C], 8=[D]}";
		assertEquals(expecting, found);
	}

	@Test public void testRepeatsIndex() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("(A B (A C B) B D D)");
		Map m = wiz.index(t);
		String found =  sortMapToString(m);
        String expecting = "{5=[A, A], 6=[B, B, B], 7=[C], 8=[D, D]}";
		assertEquals(expecting, found);
	}

	@Test public void testNoRepeatsVisit() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("(A B C D)");
		final List elements = new ArrayList();
		wiz.visit(t, wiz.getTokenType("B"), new TreeWizard.Visitor() {
			public void visit(Object t) {
				elements.add(t);
			}
		});
		String found = elements.toString();
		String expecting = "[B]";
		assertEquals(expecting, found);
	}

	@Test public void testNoRepeatsVisit2() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("(A B (A C B) B D D)");
		final List elements = new ArrayList();
		wiz.visit(t, wiz.getTokenType("C"),
					   new TreeWizard.Visitor() {
							public void visit(Object t) {
								elements.add(t);
							}
					   });
		String found = elements.toString();
		String expecting = "[C]";
		assertEquals(expecting, found);
	}

	@Test public void testRepeatsVisit() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("(A B (A C B) B D D)");
		final List elements = new ArrayList();
		wiz.visit(t, wiz.getTokenType("B"),
					   new TreeWizard.Visitor() {
							public void visit(Object t) {
								elements.add(t);
							}
					   });
		String found = elements.toString();
		String expecting = "[B, B, B]";
		assertEquals(expecting, found);
	}

	@Test public void testRepeatsVisit2() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("(A B (A C B) B D D)");
		final List elements = new ArrayList();
		wiz.visit(t, wiz.getTokenType("A"),
					   new TreeWizard.Visitor() {
							public void visit(Object t) {
								elements.add(t);
							}
					   });
		String found = elements.toString();
		String expecting = "[A, A]";
		assertEquals(expecting, found);
	}

	@Test public void testRepeatsVisitWithContext() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("(A B (A C B) B D D)");
		final List elements = new ArrayList();
		wiz.visit(t, wiz.getTokenType("B"),
		   new TreeWizard.ContextVisitor() {
			   public void visit(Object t, Object parent, int childIndex, Map labels) {
				   elements.add(adaptor.getText(t)+"@"+
								(parent!=null?adaptor.getText(parent):"nil")+
								"["+childIndex+"]");
			   }
		   });
		String found = elements.toString();
		String expecting = "[B@A[0], B@A[1], B@A[2]]";
		assertEquals(expecting, found);
	}

	@Test public void testRepeatsVisitWithNullParentAndContext() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("(A B (A C B) B D D)");
		final List elements = new ArrayList();
		wiz.visit(t, wiz.getTokenType("A"),
		   new TreeWizard.ContextVisitor() {
			   public void visit(Object t, Object parent, int childIndex, Map labels) {
				   elements.add(adaptor.getText(t)+"@"+
								(parent!=null?adaptor.getText(parent):"nil")+
								"["+childIndex+"]");
			   }
		   });
		String found = elements.toString();
		String expecting = "[A@nil[0], A@A[1]]";
		assertEquals(expecting, found);
	}

	@Test public void testVisitPattern() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("(A B C (A B) D)");
		final List elements = new ArrayList();
		wiz.visit(t, "(A B)",
					   new TreeWizard.Visitor() {
							public void visit(Object t) {
								elements.add(t);
							}
					   });
		String found = elements.toString();
		String expecting = "[A]"; // shouldn't match overall root, just (A B)
		assertEquals(expecting, found);
	}

	@Test public void testVisitPatternMultiple() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("(A B C (A B) (D (A B)))");
		final List elements = new ArrayList();
		wiz.visit(t, "(A B)",
					   new TreeWizard.ContextVisitor() {
						   public void visit(Object t, Object parent, int childIndex, Map labels) {
							   elements.add(adaptor.getText(t)+"@"+
											(parent!=null?adaptor.getText(parent):"nil")+
											"["+childIndex+"]");
						   }
					   });
		String found = elements.toString();
		String expecting = "[A@A[2], A@D[0]]"; // shouldn't match overall root, just (A B)
		assertEquals(expecting, found);
	}

	@Test public void testVisitPatternMultipleWithLabels() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("(A B C (A[foo] B[bar]) (D (A[big] B[dog])))");
		final List elements = new ArrayList();
		wiz.visit(t, "(%a:A %b:B)",
					   new TreeWizard.ContextVisitor() {
						   public void visit(Object t, Object parent, int childIndex, Map labels) {
							   elements.add(adaptor.getText(t)+"@"+
											(parent!=null?adaptor.getText(parent):"nil")+
											"["+childIndex+"]"+labels.get("a")+"&"+labels.get("b"));
						   }
					   });
		String found = elements.toString();
		String expecting = "[foo@A[2]foo&bar, big@D[0]big&dog]";
		assertEquals(expecting, found);
	}

	@Test public void testParse() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("(A B C)");
		boolean valid = wiz.parse(t, "(A B C)");
		assertTrue(valid);
	}

	@Test public void testParseSingleNode() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("A");
		boolean valid = wiz.parse(t, "A");
		assertTrue(valid);
	}

	@Test public void testParseFlatTree() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("(nil A B C)");
		boolean valid = wiz.parse(t, "(nil A B C)");
		assertTrue(valid);
	}

	@Test public void testWildcard() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("(A B C)");
		boolean valid = wiz.parse(t, "(A . .)");
		assertTrue(valid);
	}

	@Test public void testParseWithText() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("(A B[foo] C[bar])");
		// C pattern has no text arg so despite [bar] in t, no need
		// to match text--check structure only.
		boolean valid = wiz.parse(t, "(A B[foo] C)");
		assertTrue(valid);
	}

	@Test public void testParseWithText2() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("(A B[T__32] (C (D E[a])))");
		// C pattern has no text arg so despite [bar] in t, no need
		// to match text--check structure only.
		boolean valid = wiz.parse(t, "(A B[foo] C)");
		assertEquals("(A T__32 (C (D a)))", t.toStringTree());
	}

	@Test public void testParseWithTextFails() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("(A B C)");
		boolean valid = wiz.parse(t, "(A[foo] B C)");
		assertTrue(!valid); // fails
	}

	@Test public void testParseLabels() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("(A B C)");
		Map labels = new HashMap();
		boolean valid = wiz.parse(t, "(%a:A %b:B %c:C)", labels);
		assertTrue(valid);
		assertEquals("A", labels.get("a").toString());
		assertEquals("B", labels.get("b").toString());
		assertEquals("C", labels.get("c").toString());
	}

	@Test public void testParseWithWildcardLabels() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("(A B C)");
		Map labels = new HashMap();
		boolean valid = wiz.parse(t, "(A %b:. %c:.)", labels);
		assertTrue(valid);
		assertEquals("B", labels.get("b").toString());
		assertEquals("C", labels.get("c").toString());
	}

	@Test public void testParseLabelsAndTestText() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("(A B[foo] C)");
		Map labels = new HashMap();
		boolean valid = wiz.parse(t, "(%a:A %b:B[foo] %c:C)", labels);
		assertTrue(valid);
		assertEquals("A", labels.get("a").toString());
		assertEquals("foo", labels.get("b").toString());
		assertEquals("C", labels.get("c").toString());
	}

	@Test public void testParseLabelsInNestedTree() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("(A (B C) (D E))");
		Map labels = new HashMap();
		boolean valid = wiz.parse(t, "(%a:A (%b:B %c:C) (%d:D %e:E) )", labels);
		assertTrue(valid);
		assertEquals("A", labels.get("a").toString());
		assertEquals("B", labels.get("b").toString());
		assertEquals("C", labels.get("c").toString());
		assertEquals("D", labels.get("d").toString());
		assertEquals("E", labels.get("e").toString());
	}

	@Test public void testEquals() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t1 = (CommonTree)wiz.create("(A B C)");
		CommonTree t2 = (CommonTree)wiz.create("(A B C)");
		boolean same = TreeWizard.equals(t1, t2, adaptor);
		assertTrue(same);
	}

	@Test public void testEqualsWithText() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t1 = (CommonTree)wiz.create("(A B[foo] C)");
		CommonTree t2 = (CommonTree)wiz.create("(A B[foo] C)");
		boolean same = TreeWizard.equals(t1, t2, adaptor);
		assertTrue(same);
	}
	
	@Test public void testEqualsWithMismatchedText() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t1 = (CommonTree)wiz.create("(A B[foo] C)");
		CommonTree t2 = (CommonTree)wiz.create("(A B C)");
		boolean same = TreeWizard.equals(t1, t2, adaptor);
		assertTrue(!same);
	}

	@Test public void testFindPattern() throws Exception {
		TreeWizard wiz = new TreeWizard(adaptor, tokens);
		CommonTree t = (CommonTree)wiz.create("(A B C (A[foo] B[bar]) (D (A[big] B[dog])))");
		final List subtrees = wiz.find(t, "(A B)");
		List elements = subtrees;
		String found = elements.toString();
		String expecting = "[foo, big]";
		assertEquals(expecting, found);
	}
	
}