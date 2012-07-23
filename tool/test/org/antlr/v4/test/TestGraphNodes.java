package org.antlr.v4.test;

import junit.framework.TestCase;
import org.antlr.v4.runtime.atn.ArrayPredictionContext;
import org.antlr.v4.runtime.atn.PredictionContext;
import org.antlr.v4.runtime.atn.SingletonPredictionContext;
import org.junit.Test;

public class TestGraphNodes extends TestCase {
	@Override
	protected void setUp() throws Exception {
		PredictionContext.globalNodeCount = 1;
	}

	public boolean rootIsWildcard() { return true; }
	public boolean fullCtx() { return false; }

	@Test public void test_$_$() {
		PredictionContext r = PredictionContext.merge(PredictionContext.EMPTY,
													  PredictionContext.EMPTY,
													  rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_$_$_fullctx() {
		PredictionContext r = PredictionContext.merge(PredictionContext.EMPTY,
													  PredictionContext.EMPTY,
													  fullCtx());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_x_$() {
		PredictionContext r = PredictionContext.merge(x(), PredictionContext.EMPTY, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_x_$_fullctx() {
		PredictionContext r = PredictionContext.merge(x(), PredictionContext.EMPTY, fullCtx());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s2 [shape=box, label=\"[$, 9]\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s2->s0 [label=\"parent[1]\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_$_x() {
		PredictionContext r = PredictionContext.merge(PredictionContext.EMPTY, x(), rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_$_x_fullctx() {
		PredictionContext r = PredictionContext.merge(PredictionContext.EMPTY, x(), fullCtx());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s2 [shape=box, label=\"[$, 9]\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s2->s0 [label=\"parent[1]\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_a_a() {
		PredictionContext r = PredictionContext.merge(a(), a(), rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s1 [label=\"1\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s1->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_a$_ax() {
		PredictionContext a1 = a();
		PredictionContext x = x();
		PredictionContext a2 = createSingleton(x, 1);
		PredictionContext r = PredictionContext.merge(a1, a2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s1 [label=\"1\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s1->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_a$_ax_fullctx() {
		PredictionContext a1 = a();
		PredictionContext x = x();
		PredictionContext a2 = createSingleton(x, 1);
		PredictionContext r = PredictionContext.merge(a1, a2, fullCtx());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s5 [label=\"1\"];\n" +
			"  s4 [shape=box, label=\"[$, 9]\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s5->s4;\n" +
			"  s4->s0 [label=\"parent[1]\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_ax$_a$() {
		PredictionContext x = x();
		PredictionContext a1 = createSingleton(x, 1);
		PredictionContext a2 = a();
		PredictionContext r = PredictionContext.merge(a1, a2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s3 [label=\"1\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s3->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_ax$_a$_fullctx() {
		PredictionContext x = x();
		PredictionContext a1 = createSingleton(x, 1);
		PredictionContext a2 = a();
		PredictionContext r = PredictionContext.merge(a1, a2, fullCtx());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s5 [label=\"1\"];\n" +
			"  s4 [shape=box, label=\"[$, 9]\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s5->s4;\n" +
			"  s4->s0 [label=\"parent[1]\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_a_b() {
		PredictionContext r = PredictionContext.merge(a(), b(), rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s3 [shape=box, label=\"[1, 2]\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s3->s0 [label=\"parent[0]\"];\n" +
			"  s3->s0 [label=\"parent[1]\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_ax_ax_same() {
		PredictionContext x = x();
		PredictionContext a1 = createSingleton(x, 1);
		PredictionContext a2 = createSingleton(x, 1);
		PredictionContext r = PredictionContext.merge(a1, a2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s2 [label=\"1\"];\n" +
			"  s1 [label=\"9\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s2->s1;\n" +
			"  s1->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_ax_ax() {
		PredictionContext x1 = x();
		PredictionContext x2 = x();
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
			"  s3->s1;\n" +
			"  s1->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_abx_abx() {
		PredictionContext x1 = x();
		PredictionContext x2 = x();
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
			"  s5->s3;\n" +
			"  s3->s1;\n" +
			"  s1->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_abx_acx() {
		PredictionContext x1 = x();
		PredictionContext x2 = x();
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
			"  s1 [label=\"9\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s8->s7;\n" +
			"  s7->s1 [label=\"parent[0]\"];\n" +
			"  s7->s1 [label=\"parent[1]\"];\n" +
			"  s1->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_ax_bx_same() {
		PredictionContext x = x();
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
			"  s1->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_ax_bx() {
		PredictionContext x1 = x();
		PredictionContext x2 = x();
		PredictionContext a = createSingleton(x1, 1);
		PredictionContext b = createSingleton(x2, 2);
		PredictionContext r = PredictionContext.merge(a, b, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s5 [shape=box, label=\"[1, 2]\"];\n" +
			"  s1 [label=\"9\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s5->s1 [label=\"parent[0]\"];\n" +
			"  s5->s1 [label=\"parent[1]\"];\n" +
			"  s1->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_ax_by() {
		PredictionContext a = createSingleton(x(), 1);
		PredictionContext b = createSingleton(y(), 2);
		PredictionContext r = PredictionContext.merge(a, b, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s5 [shape=box, label=\"[1, 2]\"];\n" +
			"  s3 [label=\"10\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s1 [label=\"9\"];\n" +
			"  s5->s1 [label=\"parent[0]\"];\n" +
			"  s5->s3 [label=\"parent[1]\"];\n" +
			"  s3->s0;\n" +
			"  s1->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_a$_bx() {
		PredictionContext x2 = x();
		PredictionContext a = a();
		PredictionContext b = createSingleton(x2, 2);
		PredictionContext r = PredictionContext.merge(a, b, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s4 [shape=box, label=\"[1, 2]\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s4->s0 [label=\"parent[0]\"];\n" +
			"  s4->s0 [label=\"parent[1]\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_a$_bx_fullctx() {
		PredictionContext x2 = x();
		PredictionContext a = a();
		PredictionContext b = createSingleton(x2, 2);
		PredictionContext r = PredictionContext.merge(a, b, fullCtx());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s4 [shape=box, label=\"[1, 2]\"];\n" +
			"  s1 [label=\"9\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s4->s0 [label=\"parent[0]\"];\n" +
			"  s4->s1 [label=\"parent[1]\"];\n" +
			"  s1->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_aex_bfx() {
		PredictionContext x1 = x();
		PredictionContext x2 = x();
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
			"  s4->s2;\n" +
			"  s2->s0;\n" +
			"  s3->s1;\n" +
			"  s1->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	// Array merges

	@Test public void test_A$_A$_fullctx() {
		ArrayPredictionContext A1 = array(PredictionContext.EMPTY);
		ArrayPredictionContext A2 = array(PredictionContext.EMPTY);
		PredictionContext r = PredictionContext.merge(A1, A2, fullCtx());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s1 [shape=box, label=\"[-2]\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_Aab_Ac() { // a,b + c
		SingletonPredictionContext a = a();
		SingletonPredictionContext b = b();
		SingletonPredictionContext c = c();
		ArrayPredictionContext A1 = array(a, b);
		ArrayPredictionContext A2 = array(c);
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
		SingletonPredictionContext a1 = a();
		SingletonPredictionContext a2 = a();
		ArrayPredictionContext A1 = array(a1);
		ArrayPredictionContext A2 = array(a2);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s3 [shape=box, label=\"[1]\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s3->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_Aa_Abc() { // a + b,c
		SingletonPredictionContext a = a();
		SingletonPredictionContext b = b();
		SingletonPredictionContext c = c();
		ArrayPredictionContext A1 = array(a);
		ArrayPredictionContext A2 = array(b, c);
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

	@Test public void test_Aac_Ab() { // a,c + b
		SingletonPredictionContext a = a();
		SingletonPredictionContext b = b();
		SingletonPredictionContext c = c();
		ArrayPredictionContext A1 = array(a, c);
		ArrayPredictionContext A2 = array(b);
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

	@Test public void test_Aab_Aa() { // a,b + a
		ArrayPredictionContext A1 = array(a(), b());
		ArrayPredictionContext A2 = array(a());
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s3 [shape=box, label=\"[1, 2]\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s3->s0 [label=\"parent[0]\"];\n" +
			"  s3->s0 [label=\"parent[1]\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_Aab_Ab() { // a,b + b
		ArrayPredictionContext A1 = array(a(), b());
		ArrayPredictionContext A2 = array(b());
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s3 [shape=box, label=\"[1, 2]\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s3->s0 [label=\"parent[0]\"];\n" +
			"  s3->s0 [label=\"parent[1]\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_Aax_Aby() { // ax + by but in arrays
		SingletonPredictionContext a = createSingleton(x(), 1);
		SingletonPredictionContext b = createSingleton(y(), 2);
		ArrayPredictionContext A1 = array(a);
		ArrayPredictionContext A2 = array(b);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s7 [shape=box, label=\"[1, 2]\"];\n" +
			"  s3 [label=\"10\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s1 [label=\"9\"];\n" +
			"  s7->s1 [label=\"parent[0]\"];\n" +
			"  s7->s3 [label=\"parent[1]\"];\n" +
			"  s3->s0;\n" +
			"  s1->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_Aax_Aay() { // ax + ay -> merged singleton a, array parent
		SingletonPredictionContext a1 = createSingleton(x(), 1);
		SingletonPredictionContext a2 = createSingleton(y(), 1);
		ArrayPredictionContext A1 = array(a1);
		ArrayPredictionContext A2 = array(a2);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s8 [label=\"1\"];\n" +
			"  s7 [shape=box, label=\"[9, 10]\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s8->s7;\n" +
			"  s7->s0 [label=\"parent[0]\"];\n" +
			"  s7->s0 [label=\"parent[1]\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_Aaxc_Aayd() { // ax,c + ay,d -> merged a, array parent
		SingletonPredictionContext a1 = createSingleton(x(), 1);
		SingletonPredictionContext a2 = createSingleton(y(), 1);
		ArrayPredictionContext A1 = array(a1, c());
		ArrayPredictionContext A2 = array(a2, d());
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s10 [shape=box, label=\"[1, 3, 4]\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s9 [shape=box, label=\"[9, 10]\"];\n" +
			"  s10->s9 [label=\"parent[0]\"];\n" +
			"  s10->s0 [label=\"parent[1]\"];\n" +
			"  s10->s0 [label=\"parent[2]\"];\n" +
			"  s9->s0 [label=\"parent[0]\"];\n" +
			"  s9->s0 [label=\"parent[1]\"];\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_Aaubv_Acwdx() { // au,bv + cw,dx -> [a,b,c,d]->[u,v,w,x]
		SingletonPredictionContext a = createSingleton(u(), 1);
		SingletonPredictionContext b = createSingleton(v(), 2);
		SingletonPredictionContext c = createSingleton(w(), 3);
		SingletonPredictionContext d = createSingleton(x(), 4);
		ArrayPredictionContext A1 = array(a, b);
		ArrayPredictionContext A2 = array(c, d);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s11 [shape=box, label=\"[1, 2, 3, 4]\"];\n" +
			"  s7 [label=\"9\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s5 [label=\"8\"];\n" +
			"  s3 [label=\"7\"];\n" +
			"  s1 [label=\"6\"];\n" +
			"  s11->s1 [label=\"parent[0]\"];\n" +
			"  s11->s3 [label=\"parent[1]\"];\n" +
			"  s11->s5 [label=\"parent[2]\"];\n" +
			"  s11->s7 [label=\"parent[3]\"];\n" +
			"  s7->s0;\n" +
			"  s5->s0;\n" +
			"  s3->s0;\n" +
			"  s1->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_Aaubv_Abvdx() { // au,bv + bv,dx -> [a,b,d]->[u,v,x]
		SingletonPredictionContext a = createSingleton(u(), 1);
		SingletonPredictionContext b1 = createSingleton(v(), 2);
		SingletonPredictionContext b2 = createSingleton(v(), 2);
		SingletonPredictionContext d = createSingleton(x(), 4);
		ArrayPredictionContext A1 = array(a, b1);
		ArrayPredictionContext A2 = array(b2, d);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s11 [shape=box, label=\"[1, 2, 4]\"];\n" +
			"  s7 [label=\"9\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s3 [label=\"7\"];\n" +
			"  s1 [label=\"6\"];\n" +
			"  s11->s1 [label=\"parent[0]\"];\n" +
			"  s11->s3 [label=\"parent[1]\"];\n" +
			"  s11->s7 [label=\"parent[2]\"];\n" +
			"  s7->s0;\n" +
			"  s3->s0;\n" +
			"  s1->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_Aaubv_Abwdx() { // au,bv + bw,dx -> [a,b,d]->[u,[v,w],x]
		SingletonPredictionContext a = createSingleton(u(), 1);
		SingletonPredictionContext b1 = createSingleton(v(), 2);
		SingletonPredictionContext b2 = createSingleton(w(), 2);
		SingletonPredictionContext d = createSingleton(x(), 4);
		ArrayPredictionContext A1 = array(a, b1);
		ArrayPredictionContext A2 = array(b2, d);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s12 [shape=box, label=\"[1, 2, 4]\"];\n" +
			"  s7 [label=\"9\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s11 [shape=box, label=\"[7, 8]\"];\n" +
			"  s1 [label=\"6\"];\n" +
			"  s12->s1 [label=\"parent[0]\"];\n" +
			"  s12->s11 [label=\"parent[1]\"];\n" +
			"  s12->s7 [label=\"parent[2]\"];\n" +
			"  s7->s0;\n" +
			"  s11->s0 [label=\"parent[0]\"];\n" +
			"  s11->s0 [label=\"parent[1]\"];\n" +
			"  s1->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_Aaubv_Abvdu() { // au,bv + bv,du -> [a,b,d]->[u,v,u]; u,v shared
		SingletonPredictionContext a = createSingleton(u(), 1);
		SingletonPredictionContext b1 = createSingleton(v(), 2);
		SingletonPredictionContext b2 = createSingleton(v(), 2);
		SingletonPredictionContext d = createSingleton(u(), 4);
		ArrayPredictionContext A1 = array(a, b1);
		ArrayPredictionContext A2 = array(b2, d);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s11 [shape=box, label=\"[1, 2, 4]\"];\n" +
			"  s3 [label=\"7\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s1 [label=\"6\"];\n" +
			"  s11->s1 [label=\"parent[0]\"];\n" +
			"  s11->s3 [label=\"parent[1]\"];\n" +
			"  s11->s1 [label=\"parent[2]\"];\n" +
			"  s3->s0;\n" +
			"  s1->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}

	@Test public void test_Aaubu_Acudu() { // au,bu + cu,du -> [a,b,c,d]->[u,u,u,u]
		SingletonPredictionContext a = createSingleton(u(), 1);
		SingletonPredictionContext b = createSingleton(u(), 2);
		SingletonPredictionContext c = createSingleton(u(), 3);
		SingletonPredictionContext d = createSingleton(u(), 4);
		ArrayPredictionContext A1 = array(a, b);
		ArrayPredictionContext A2 = array(c, d);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard());
		System.out.println(PredictionContext.toDotString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s11 [shape=box, label=\"[1, 2, 3, 4]\"];\n" +
			"  s1 [label=\"6\"];\n" +
			"  s0 [label=\"$\"];\n" +
			"  s11->s1 [label=\"parent[0]\"];\n" +
			"  s11->s1 [label=\"parent[1]\"];\n" +
			"  s11->s1 [label=\"parent[2]\"];\n" +
			"  s11->s1 [label=\"parent[3]\"];\n" +
			"  s1->s0;\n" +
			"}\n";
		assertEquals(expecting, PredictionContext.toDotString(r));
	}


	// ------------ SUPPORT -------------------------

	protected SingletonPredictionContext a() {
		return createSingleton(PredictionContext.EMPTY, 1);
	}

	private SingletonPredictionContext b() {
		return createSingleton(PredictionContext.EMPTY, 2);
	}

	private SingletonPredictionContext c() {
		return createSingleton(PredictionContext.EMPTY, 3);
	}

	private SingletonPredictionContext d() {
		return createSingleton(PredictionContext.EMPTY, 4);
	}

	private SingletonPredictionContext u() {
		return createSingleton(PredictionContext.EMPTY, 6);
	}

	private SingletonPredictionContext v() {
		return createSingleton(PredictionContext.EMPTY, 7);
	}

	private SingletonPredictionContext w() {
		return createSingleton(PredictionContext.EMPTY, 8);
	}

	private SingletonPredictionContext x() {
		return createSingleton(PredictionContext.EMPTY, 9);
	}

	private SingletonPredictionContext y() {
		return createSingleton(PredictionContext.EMPTY, 10);
	}

	public SingletonPredictionContext createSingleton(PredictionContext parent, int payload) {
		SingletonPredictionContext a = new SingletonPredictionContext(parent, payload);
		return a;
	}

	public ArrayPredictionContext array(SingletonPredictionContext... nodes) {
		PredictionContext[] parents = new PredictionContext[nodes.length];
		int[] invokingStates = new int[nodes.length];
		for (int i=0; i<nodes.length; i++) {
			parents[i] = nodes[i].parent;
			invokingStates[i] = nodes[i].invokingState;
		}
		return new ArrayPredictionContext(parents, invokingStates);
	}
}
