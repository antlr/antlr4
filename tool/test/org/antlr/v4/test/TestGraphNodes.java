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
		assertEquals("[$:0]", nodes.toString());
	}

	@Test public void testLeftEmpty() {
		PredictionContext a = PredictionContext.EMPTY;
		PredictionContext b = createSingleton(PredictionContext.EMPTY, "b");
		PredictionContext r = PredictionContext.merge(a, b, rootIsWildcard());
		List<PredictionContext> nodes = PredictionContext.getAllNodes(r);
		assertEquals("[$:0]", nodes.toString());
	}

	@Test public void testRightEmpty() {
		PredictionContext a = PredictionContext.EMPTY;
		PredictionContext b = createSingleton(PredictionContext.EMPTY, "b");
		PredictionContext r = PredictionContext.merge(a, b, rootIsWildcard());
		List<PredictionContext> nodes = PredictionContext.getAllNodes(r);
		assertEquals("[$:0]", nodes.toString());
	}

	@Test public void testSameSingleTops() {
		PredictionContext a1 = createSingleton(PredictionContext.EMPTY, "a");
		PredictionContext a2 = createSingleton(PredictionContext.EMPTY, "a");
		PredictionContext r = PredictionContext.merge(a1, a2, rootIsWildcard());
		List<PredictionContext> nodes = PredictionContext.getAllNodes(r);
		assertEquals("[a:1, $:0]", nodes.toString());
	}

	@Test public void test_a$_ax$() {
		PredictionContext a1 = createSingleton(PredictionContext.EMPTY, "a");
		PredictionContext x = createSingleton(PredictionContext.EMPTY, "x");
		PredictionContext a2 = createSingleton(x, "a");
		PredictionContext r = PredictionContext.merge(a1, a2, rootIsWildcard());
		List<PredictionContext> nodes = PredictionContext.getAllNodes(r);
		assertEquals("[a:1, $:0]", nodes.toString());
	}

	@Test public void test_ax$_a$() {
		PredictionContext x = createSingleton(PredictionContext.EMPTY, "x");
		PredictionContext a1 = createSingleton(x, "a");
		PredictionContext a2 = createSingleton(PredictionContext.EMPTY, "a");
		PredictionContext r = PredictionContext.merge(a1, a2, rootIsWildcard());
		List<PredictionContext> nodes = PredictionContext.getAllNodes(r);
		assertEquals("[a:3, $:0]", nodes.toString());
	}

	@Test public void testDiffSingleTops() {
		PredictionContext a1 = createSingleton(PredictionContext.EMPTY, "a");
		PredictionContext a2 = createSingleton(PredictionContext.EMPTY, "b");
		PredictionContext r = PredictionContext.merge(a1, a2, rootIsWildcard());
		List<PredictionContext> nodes = PredictionContext.getAllNodes(r);
		assertEquals("[[a, b]:3, $:0]", nodes.toString());
	}

	@Test public void test_ax_ax_id() {
		PredictionContext x = createSingleton(PredictionContext.EMPTY, "x");
		PredictionContext a1 = createSingleton(x, "a");
		PredictionContext a2 = createSingleton(x, "a");
		PredictionContext r = PredictionContext.merge(a1, a2, rootIsWildcard());
		List<PredictionContext> nodes = PredictionContext.getAllNodes(r);
		assertEquals("[a:2, x:1, $:0]", nodes.toString());
	}

	@Test public void test_ax_ax_eq() {
		PredictionContext x1 = createSingleton(PredictionContext.EMPTY, "x");
		PredictionContext x2 = createSingleton(PredictionContext.EMPTY, "x");
		PredictionContext a1 = createSingleton(x1, "a");
		PredictionContext a2 = createSingleton(x2, "a");
		PredictionContext r = PredictionContext.merge(a1, a2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s3 [label=\"a\"];\n" +
			"  s1 [label=\"x\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s3->s1;\n" +
			"  s1->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_abx_abx_id() {
		PredictionContext x1 = createSingleton(PredictionContext.EMPTY, "x");
		PredictionContext x2 = createSingleton(PredictionContext.EMPTY, "x");
		PredictionContext b1 = createSingleton(x1, "b");
		PredictionContext b2 = createSingleton(x2, "b");
		PredictionContext a1 = createSingleton(b1, "a");
		PredictionContext a2 = createSingleton(b2, "a");
		PredictionContext r = PredictionContext.merge(a1, a2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s5 [label=\"a\"];\n" +
			"  s3 [label=\"b\"];\n" +
			"  s1 [label=\"x\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s5->s3;\n" +
			"  s3->s1;\n" +
			"  s1->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_abx_acx_id() {
		PredictionContext x1 = createSingleton(PredictionContext.EMPTY, "x");
		PredictionContext x2 = createSingleton(PredictionContext.EMPTY, "x");
		PredictionContext b = createSingleton(x1, "b");
		PredictionContext c = createSingleton(x2, "c");
		PredictionContext a1 = createSingleton(b, "a");
		PredictionContext a2 = createSingleton(c, "a");
		PredictionContext r = PredictionContext.merge(a1, a2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s8 [label=\"a\"];\n" +
			"  s7 [label=\"[b, c]\"];\n" +
			"  s2 [label=\"x\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s1 [label=\"x\"];\n" +
			"  s8->s7;\n" +
			"  s7->s1;\n" +
			"  s7->s2;\n" +
			"  s2->s0;\n" +
			"  s1->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_ax_bx_id() {
		PredictionContext x = createSingleton(PredictionContext.EMPTY, "x");
		PredictionContext a = createSingleton(x, "a");
		PredictionContext b = createSingleton(x, "b");
		PredictionContext r = PredictionContext.merge(a, b, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s4 [label=\"[a, b]\"];\n" +
			"  s1 [label=\"x\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s4->s1;\n" +
			"  s4->s1;\n" +
			"  s1->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_aex_bfx_id() {
		PredictionContext x1 = createSingleton(PredictionContext.EMPTY, "x");
		PredictionContext x2 = createSingleton(PredictionContext.EMPTY, "x");
		PredictionContext e = createSingleton(x1, "e");
		PredictionContext f = createSingleton(x2, "f");
		PredictionContext a = createSingleton(e, "a");
		PredictionContext b = createSingleton(f, "b");
		PredictionContext r = PredictionContext.merge(a, b, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s7 [label=\"[a, b]\"];\n" +
			"  s4 [label=\"f\"];\n" +
			"  s2 [label=\"x\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s3 [label=\"e\"];\n" +
			"  s1 [label=\"x\"];\n" +
			"  s7->s3;\n" +
			"  s7->s4;\n" +
			"  s4->s2;\n" +
			"  s2->s0;\n" +
			"  s3->s1;\n" +
			"  s1->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	// Array merges

	@Test public void test_Aab_Ac() {
		SingletonPredictionContext a = createSingleton(PredictionContext.EMPTY, "a");
		SingletonPredictionContext b = createSingleton(PredictionContext.EMPTY, "b");
		SingletonPredictionContext c = createSingleton(PredictionContext.EMPTY, "c");
		ArrayPredictionContext A1 = new ArrayPredictionContext(a,b);
		ArrayPredictionContext A2 = new ArrayPredictionContext(c);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s6 [label=\"[a, b, c]\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s6->s0;\n" +
			"  s6->s0;\n" +
			"  s6->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_Aa_Aa() {
		SingletonPredictionContext a1 = createSingleton(PredictionContext.EMPTY, "a");
		SingletonPredictionContext a2 = createSingleton(PredictionContext.EMPTY, "a");
		ArrayPredictionContext A1 = new ArrayPredictionContext(a1);
		ArrayPredictionContext A2 = new ArrayPredictionContext(a2);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s8 [label=\"[a]\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s8->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_Aa_Abc() {
		SingletonPredictionContext a = createSingleton(PredictionContext.EMPTY, "a");
		SingletonPredictionContext b = createSingleton(PredictionContext.EMPTY, "b");
		SingletonPredictionContext c = createSingleton(PredictionContext.EMPTY, "c");
		ArrayPredictionContext A1 = new ArrayPredictionContext(a);
		ArrayPredictionContext A2 = new ArrayPredictionContext(b,c);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s6 [label=\"[a, b, c]\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s6->s0;\n" +
			"  s6->s0;\n" +
			"  s6->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_Aac_Ab() {
		SingletonPredictionContext a = createSingleton(PredictionContext.EMPTY, "a");
		SingletonPredictionContext b = createSingleton(PredictionContext.EMPTY, "b");
		SingletonPredictionContext c = createSingleton(PredictionContext.EMPTY, "c");
		ArrayPredictionContext A1 = new ArrayPredictionContext(a,c);
		ArrayPredictionContext A2 = new ArrayPredictionContext(b);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s6 [label=\"[a, b, c]\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s6->s0;\n" +
			"  s6->s0;\n" +
			"  s6->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_Aab_Aa() {
		SingletonPredictionContext a1 = createSingleton(PredictionContext.EMPTY, "a");
		SingletonPredictionContext b = createSingleton(PredictionContext.EMPTY, "b");
		SingletonPredictionContext a2 = createSingleton(PredictionContext.EMPTY, "a");
		ArrayPredictionContext A1 = new ArrayPredictionContext(a1,b);
		ArrayPredictionContext A2 = new ArrayPredictionContext(a2);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s9 [label=\"[a, b]\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s9->s0;\n" +
			"  s9->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_Aab_Ab() {
		SingletonPredictionContext a = createSingleton(PredictionContext.EMPTY, "a");
		SingletonPredictionContext b1 = createSingleton(PredictionContext.EMPTY, "b");
		SingletonPredictionContext b2 = createSingleton(PredictionContext.EMPTY, "b");
		ArrayPredictionContext A1 = new ArrayPredictionContext(a,b1);
		ArrayPredictionContext A2 = new ArrayPredictionContext(b2);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s9 [label=\"[a, b]\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s9->s0;\n" +
			"  s9->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_Aax_Aay() { // ax + ay -> merged a, array parent
		SingletonPredictionContext x = createSingleton(PredictionContext.EMPTY, "x");
		SingletonPredictionContext a1 = createSingleton(x, "a");
		SingletonPredictionContext y = createSingleton(PredictionContext.EMPTY, "y");
		SingletonPredictionContext a2 = createSingleton(y, "a");
		ArrayPredictionContext A1 = new ArrayPredictionContext(a1);
		ArrayPredictionContext A2 = new ArrayPredictionContext(a2);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s12 [label=\"[a]\"];\n" +
			"  s10 [label=\"[x, y]\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s12->s10;\n" +
			"  s10->s0;\n" +
			"  s10->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_Aaxc_Aayd() { // ax,c + ay,d -> merged a, array parent
		SingletonPredictionContext x = createSingleton(PredictionContext.EMPTY, "x");
		SingletonPredictionContext a1 = createSingleton(x, "a");
		SingletonPredictionContext c = createSingleton(PredictionContext.EMPTY, "c");
		SingletonPredictionContext y = createSingleton(PredictionContext.EMPTY, "y");
		SingletonPredictionContext a2 = createSingleton(y, "a");
		SingletonPredictionContext d = createSingleton(PredictionContext.EMPTY, "d");
		ArrayPredictionContext A1 = new ArrayPredictionContext(a1,c);
		ArrayPredictionContext A2 = new ArrayPredictionContext(a2,d);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s14 [label=\"[a, c, d]\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s12 [label=\"[x, y]\"];\n" +
			"  s14->s12;\n" +
			"  s14->s0;\n" +
			"  s14->s0;\n" +
			"  s12->s0;\n" +
			"  s12->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	public SingletonPredictionContext createSingleton(PredictionContext parent, String payload) {
//		SingletonPredictionContext a = new SingletonPredictionContext(parent, payload);
//		return a;
		return null;
	}
}
