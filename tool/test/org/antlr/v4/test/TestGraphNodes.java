package org.antlr.v4.test;

import junit.framework.TestCase;
import org.antlr.v4.runtime.atn.ArrayPredictionContext;
import org.antlr.v4.runtime.atn.PredictionContext;
import org.antlr.v4.runtime.atn.SingletonPredictionContext;
import org.junit.Test;

import java.util.List;

public class TestGraphNodes extends TestCase {
	@Override
	protected void setUp() throws Exception {
		PredictionContext.globalNodeCount = 1;
	}

	public boolean rootIsWildcard() { return true; }

	@Test public void testBothEmpty() {
		PredictionContext a = PredictionContext.EMPTY;
		PredictionContext b = PredictionContext.EMPTY;
		PredictionContext r = PredictionContext.merge(a, b, rootIsWildcard());
		List<PredictionContext> nodes = PredictionContext.getAllNodes(r);
		assertEquals("[$]", nodes.toString());
	}

	@Test public void testLeftEmpty() {
		PredictionContext a = PredictionContext.EMPTY;
		PredictionContext b = createSingleton(PredictionContext.EMPTY, 2);
		PredictionContext r = PredictionContext.merge(a, b, rootIsWildcard());
		List<PredictionContext> nodes = PredictionContext.getAllNodes(r);
		assertEquals("[$]", nodes.toString());
	}

	@Test public void testRightEmpty() {
		PredictionContext a = PredictionContext.EMPTY;
		PredictionContext b = createSingleton(PredictionContext.EMPTY, 2);
		PredictionContext r = PredictionContext.merge(a, b, rootIsWildcard());
		List<PredictionContext> nodes = PredictionContext.getAllNodes(r);
		assertEquals("[$]", nodes.toString());
	}

	@Test public void testSameSingleTops() {
		PredictionContext a1 = createSingleton(PredictionContext.EMPTY, 1);
		PredictionContext a2 = createSingleton(PredictionContext.EMPTY, 1);
		PredictionContext r = PredictionContext.merge(a1, a2, rootIsWildcard());
		List<PredictionContext> nodes = PredictionContext.getAllNodes(r);
		assertEquals("[1 $, $]", nodes.toString());
	}

	@Test public void test_a$_ax$() {
		PredictionContext a1 = createSingleton(PredictionContext.EMPTY, 1);
		PredictionContext x = createSingleton(PredictionContext.EMPTY, 9);
		PredictionContext a2 = createSingleton(x, 1);
		PredictionContext r = PredictionContext.merge(a1, a2, rootIsWildcard());
		List<PredictionContext> nodes = PredictionContext.getAllNodes(r);
		assertEquals("[1 $, $]", nodes.toString());
	}

	@Test public void test_ax$_a$() {
		PredictionContext x = createSingleton(PredictionContext.EMPTY, 9);
		PredictionContext a1 = createSingleton(x, 1);
		PredictionContext a2 = createSingleton(PredictionContext.EMPTY, 1);
		PredictionContext r = PredictionContext.merge(a1, a2, rootIsWildcard());
		List<PredictionContext> nodes = PredictionContext.getAllNodes(r);
		assertEquals("[1 $, $]", nodes.toString());
	}

	@Test public void testDiffSingleTops() {
		PredictionContext a1 = createSingleton(PredictionContext.EMPTY, 1);
		PredictionContext a2 = createSingleton(PredictionContext.EMPTY, 2);
		PredictionContext r = PredictionContext.merge(a1, a2, rootIsWildcard());
		List<PredictionContext> nodes = PredictionContext.getAllNodes(r);
		assertEquals("[[1 $, 2 $], $]", nodes.toString());
	}

	@Test public void test_ax_ax() {
		PredictionContext x = createSingleton(PredictionContext.EMPTY, 9);
		PredictionContext a1 = createSingleton(x, 1);
		PredictionContext a2 = createSingleton(x, 1);
		PredictionContext r = PredictionContext.merge(a1, a2, rootIsWildcard());
		List<PredictionContext> nodes = PredictionContext.getAllNodes(r);
		assertEquals("[1 9 $, 9 $, $]", nodes.toString());
	}

	@Test public void test_ax_ax_eq() {
		PredictionContext x1 = createSingleton(PredictionContext.EMPTY, 9);
		PredictionContext x2 = createSingleton(PredictionContext.EMPTY, 9);
		PredictionContext a1 = createSingleton(x1, 1);
		PredictionContext a2 = createSingleton(x2, 1);
		PredictionContext r = PredictionContext.merge(a1, a2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s3 [label=\"1\"];\n" +
			"  s1 [label=\"9\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s3->s1 [label=\"parent[0]\"];\n" +
			"  s1->s0 [label=\"parent[0]\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_abx_abx() {
		PredictionContext x1 = createSingleton(PredictionContext.EMPTY, 9);
		PredictionContext x2 = createSingleton(PredictionContext.EMPTY, 9);
		PredictionContext b1 = createSingleton(x1, 2);
		PredictionContext b2 = createSingleton(x2, 2);
		PredictionContext a1 = createSingleton(b1, 1);
		PredictionContext a2 = createSingleton(b2, 1);
		PredictionContext r = PredictionContext.merge(a1, a2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s5 [label=\"1\"];\n" +
			"  s3 [label=\"2\"];\n" +
			"  s1 [label=\"9\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s5->s3 [label=\"parent[0]\"];\n" +
			"  s3->s1 [label=\"parent[0]\"];\n" +
			"  s1->s0 [label=\"parent[0]\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_abx_acx() {
		PredictionContext x1 = createSingleton(PredictionContext.EMPTY, 9);
		PredictionContext x2 = createSingleton(PredictionContext.EMPTY, 9);
		PredictionContext b = createSingleton(x1, 2);
		PredictionContext c = createSingleton(x2, 3);
		PredictionContext a1 = createSingleton(b, 1);
		PredictionContext a2 = createSingleton(c, 1);
		PredictionContext r = PredictionContext.merge(a1, a2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s8 [label=\"1\"];\n" +
			"  s7 [shape=box, label=\"[2, 3]\"];\n" +
			"  s2 [label=\"9\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s1 [label=\"9\"];\n" +
			"  s8->s7 [label=\"parent[0]\"];\n" +
			"  s7->s1 [label=\"parent[0]\"];\n" +
			"  s7->s2 [label=\"parent[1]\"];\n" +
			"  s2->s0 [label=\"parent[0]\"];\n" +
			"  s1->s0 [label=\"parent[0]\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_ax_bx() {
		PredictionContext x = createSingleton(PredictionContext.EMPTY, 9);
		PredictionContext a = createSingleton(x, 1);
		PredictionContext b = createSingleton(x, 2);
		PredictionContext r = PredictionContext.merge(a, b, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s4 [shape=box, label=\"[1, 2]\"];\n" +
			"  s1 [label=\"9\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s4->s1 [label=\"parent[0]\"];\n" +
			"  s4->s1 [label=\"parent[1]\"];\n" +
			"  s1->s0 [label=\"parent[0]\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_aex_bfx() {
		PredictionContext x1 = createSingleton(PredictionContext.EMPTY, 9);
		PredictionContext x2 = createSingleton(PredictionContext.EMPTY, 9);
		PredictionContext e = createSingleton(x1, 5);
		PredictionContext f = createSingleton(x2, 6);
		PredictionContext a = createSingleton(e, 1);
		PredictionContext b = createSingleton(f, 2);
		PredictionContext r = PredictionContext.merge(a, b, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s7 [shape=box, label=\"[1, 2]\"];\n" +
			"  s4 [label=\"6\"];\n" +
			"  s2 [label=\"9\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s3 [label=\"5\"];\n" +
			"  s1 [label=\"9\"];\n" +
			"  s7->s3 [label=\"parent[0]\"];\n" +
			"  s7->s4 [label=\"parent[1]\"];\n" +
			"  s4->s2 [label=\"parent[0]\"];\n" +
			"  s2->s0 [label=\"parent[0]\"];\n" +
			"  s3->s1 [label=\"parent[0]\"];\n" +
			"  s1->s0 [label=\"parent[0]\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	// Array merges

	@Test public void test_Aab_Ac() {
		SingletonPredictionContext a = createSingleton(PredictionContext.EMPTY, 1);
		SingletonPredictionContext b = createSingleton(PredictionContext.EMPTY, 2);
		SingletonPredictionContext c = createSingleton(PredictionContext.EMPTY, 3);
		ArrayPredictionContext A1 = create(a,b);
		ArrayPredictionContext A2 = create(c);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s6 [shape=box, label=\"[1, 2, 3]\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s6->s0 [label=\"parent[0]\"];\n" +
			"  s6->s0 [label=\"parent[1]\"];\n" +
			"  s6->s0 [label=\"parent[2]\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_Aa_Aa() {
		SingletonPredictionContext a1 = createSingleton(PredictionContext.EMPTY, 1);
		SingletonPredictionContext a2 = createSingleton(PredictionContext.EMPTY, 1);
		ArrayPredictionContext A1 = create(a1);
		ArrayPredictionContext A2 = create(a2);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s7 [shape=box, label=\"[1]\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s7->s0 [label=\"parent[0]\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_Aa_Abc() {
		SingletonPredictionContext a = createSingleton(PredictionContext.EMPTY, 1);
		SingletonPredictionContext b = createSingleton(PredictionContext.EMPTY, 2);
		SingletonPredictionContext c = createSingleton(PredictionContext.EMPTY, 3);
		ArrayPredictionContext A1 = create(a);
		ArrayPredictionContext A2 = create(b,c);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s6 [shape=box, label=\"[1, 2, 3]\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s6->s0 [label=\"parent[0]\"];\n" +
			"  s6->s0 [label=\"parent[1]\"];\n" +
			"  s6->s0 [label=\"parent[2]\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_Aac_Ab() {
		SingletonPredictionContext a = createSingleton(PredictionContext.EMPTY, 1);
		SingletonPredictionContext b = createSingleton(PredictionContext.EMPTY, 2);
		SingletonPredictionContext c = createSingleton(PredictionContext.EMPTY, 3);
		ArrayPredictionContext A1 = create(a,c);
		ArrayPredictionContext A2 = create(b);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s6 [shape=box, label=\"[1, 2, 3]\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s6->s0 [label=\"parent[0]\"];\n" +
			"  s6->s0 [label=\"parent[1]\"];\n" +
			"  s6->s0 [label=\"parent[2]\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_Aab_Aa() {
		SingletonPredictionContext a1 = createSingleton(PredictionContext.EMPTY, 1);
		SingletonPredictionContext b = createSingleton(PredictionContext.EMPTY, 2);
		SingletonPredictionContext a2 = createSingleton(PredictionContext.EMPTY, 1);
		ArrayPredictionContext A1 = create(a1,b);
		ArrayPredictionContext A2 = create(a2);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s8 [shape=box, label=\"[1, 2]\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s8->s0 [label=\"parent[0]\"];\n" +
			"  s8->s0 [label=\"parent[1]\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_Aab_Ab() {
		SingletonPredictionContext a = createSingleton(PredictionContext.EMPTY, 1);
		SingletonPredictionContext b1 = createSingleton(PredictionContext.EMPTY, 2);
		SingletonPredictionContext b2 = createSingleton(PredictionContext.EMPTY, 2);
		ArrayPredictionContext A1 = create(a,b1);
		ArrayPredictionContext A2 = create(b2);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s8 [shape=box, label=\"[1, 2]\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s8->s0 [label=\"parent[0]\"];\n" +
			"  s8->s0 [label=\"parent[1]\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_Aax_Aay() { // ax + ay -> merged a, array parent
		SingletonPredictionContext x = createSingleton(PredictionContext.EMPTY, 9);
		SingletonPredictionContext a1 = createSingleton(x, 1);
		SingletonPredictionContext y = createSingleton(PredictionContext.EMPTY, 10);
		SingletonPredictionContext a2 = createSingleton(y, 1);
		ArrayPredictionContext A1 = create(a1);
		ArrayPredictionContext A2 = create(a2);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s11 [shape=box, label=\"[1]\"];\n" +
			"  s9 [shape=box, label=\"[9, 10]\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s11->s9 [label=\"parent[0]\"];\n" +
			"  s9->s0 [label=\"parent[0]\"];\n" +
			"  s9->s0 [label=\"parent[1]\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_Aaxc_Aayd() { // ax,c + ay,d -> merged a, array parent
		SingletonPredictionContext x = createSingleton(PredictionContext.EMPTY, 9);
		SingletonPredictionContext a1 = createSingleton(x, 1);
		SingletonPredictionContext c = createSingleton(PredictionContext.EMPTY, 3);
		SingletonPredictionContext y = createSingleton(PredictionContext.EMPTY, 10);
		SingletonPredictionContext a2 = createSingleton(y, 1);
		SingletonPredictionContext d = createSingleton(PredictionContext.EMPTY, 4);
		ArrayPredictionContext A1 = create(a1,c);
		ArrayPredictionContext A2 = create(a2,d);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s13 [shape=box, label=\"[1, 3, 4]\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s11 [shape=box, label=\"[9, 10]\"];\n" +
			"  s13->s11 [label=\"parent[0]\"];\n" +
			"  s13->s0 [label=\"parent[1]\"];\n" +
			"  s13->s0 [label=\"parent[2]\"];\n" +
			"  s11->s0 [label=\"parent[0]\"];\n" +
			"  s11->s0 [label=\"parent[1]\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	public SingletonPredictionContext createSingleton(PredictionContext parent, int payload) {
		SingletonPredictionContext a = new SingletonPredictionContext(parent, payload);
		return a;
	}

	public ArrayPredictionContext create(SingletonPredictionContext... nodes) {
		PredictionContext[] parents = new PredictionContext[nodes.length];
		int[] invokingStates = new int[nodes.length];
		for (int i=0; i<nodes.length; i++) {
			parents[i] = nodes[i].parent;
			invokingStates[i] = nodes[i].invokingState;
		}
		return new ArrayPredictionContext(parents, invokingStates);
	}
}
