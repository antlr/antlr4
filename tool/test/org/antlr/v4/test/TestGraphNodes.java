package org.antlr.v4.test;

import junit.framework.TestCase;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.atn.PredictionContext;
import org.junit.Test;

public class TestGraphNodes extends TestCase {
	PredictionContextCache contextCache;

	@Override
	protected void setUp() throws Exception {
		contextCache = new PredictionContextCache();
	}

	public boolean rootIsWildcard() { return true; }
	public boolean fullCtx() { return false; }

	@Test public void test_$_$() {
		PredictionContext r = contextCache.join(PredictionContext.EMPTY_LOCAL,
													  PredictionContext.EMPTY_LOCAL);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_$_$_fullctx() {
		PredictionContext r = contextCache.join(PredictionContext.EMPTY_FULL,
													  PredictionContext.EMPTY_FULL);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_x_$() {
		PredictionContext r = contextCache.join(x(false), PredictionContext.EMPTY_LOCAL);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_x_$_fullctx() {
		PredictionContext r = contextCache.join(x(true), PredictionContext.EMPTY_FULL);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s2 [shape=box, label=\"[$, 9]\"];\n" +
			"  s2->s0 [label=\"parent[1]\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_$_x() {
		PredictionContext r = contextCache.join(PredictionContext.EMPTY_LOCAL, x(false));
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_$_x_fullctx() {
		PredictionContext r = contextCache.join(PredictionContext.EMPTY_FULL, x(true));
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s2 [shape=box, label=\"[$, 9]\"];\n" +
			"  s2->s0 [label=\"parent[1]\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_a_a() {
		PredictionContext r = contextCache.join(a(false), a(false));
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s1 [label=\"1\"];\n" +
			"  s1->s0;\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_a$_ax() {
		PredictionContext a1 = a(false);
		PredictionContext x = x(false);
		PredictionContext a2 = createSingleton(x, 1);
		PredictionContext r = contextCache.join(a1, a2);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s1 [label=\"1\"];\n" +
			"  s1->s0;\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_a$_ax_fullctx() {
		PredictionContext a1 = a(true);
		PredictionContext x = x(true);
		PredictionContext a2 = createSingleton(x, 1);
		PredictionContext r = contextCache.join(a1, a2);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s4 [shape=box, label=\"[$, 9]\"];\n" +
			"  s5 [label=\"1\"];\n" +
			"  s4->s0 [label=\"parent[1]\"];\n" +
			"  s5->s4;\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_ax$_a$() {
		PredictionContext x = x(false);
		PredictionContext a1 = createSingleton(x, 1);
		PredictionContext a2 = a(false);
		PredictionContext r = contextCache.join(a1, a2);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s3 [label=\"1\"];\n" +
			"  s3->s0;\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_ax$_a$_fullctx() {
		PredictionContext x = x(true);
		PredictionContext a1 = createSingleton(x, 1);
		PredictionContext a2 = a(true);
		PredictionContext r = contextCache.join(a1, a2);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s4 [shape=box, label=\"[$, 9]\"];\n" +
			"  s5 [label=\"1\"];\n" +
			"  s4->s0 [label=\"parent[1]\"];\n" +
			"  s5->s4;\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_a_b() {
		PredictionContext r = contextCache.join(a(false), b(false));
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s3 [shape=box, label=\"[1, 2]\"];\n" +
			"  s3->s0 [label=\"parent[0]\"];\n" +
			"  s3->s0 [label=\"parent[1]\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_ax_ax_same() {
		PredictionContext x = x(false);
		PredictionContext a1 = createSingleton(x, 1);
		PredictionContext a2 = createSingleton(x, 1);
		PredictionContext r = contextCache.join(a1, a2);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s1 [label=\"9\"];\n" +
			"  s2 [label=\"1\"];\n" +
			"  s1->s0;\n" +
			"  s2->s1;\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_ax_ax() {
		PredictionContext x1 = x(false);
		PredictionContext x2 = x(false);
		PredictionContext a1 = createSingleton(x1, 1);
		PredictionContext a2 = createSingleton(x2, 1);
		PredictionContext r = contextCache.join(a1, a2);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s1 [label=\"9\"];\n" +
			"  s3 [label=\"1\"];\n" +
			"  s1->s0;\n" +
			"  s3->s1;\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_abx_abx() {
		PredictionContext x1 = x(false);
		PredictionContext x2 = x(false);
		PredictionContext b1 = createSingleton(x1, 2);
		PredictionContext b2 = createSingleton(x2, 2);
		PredictionContext a1 = createSingleton(b1, 1);
		PredictionContext a2 = createSingleton(b2, 1);
		PredictionContext r = contextCache.join(a1, a2);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s1 [label=\"9\"];\n" +
			"  s3 [label=\"2\"];\n" +
			"  s5 [label=\"1\"];\n" +
			"  s1->s0;\n" +
			"  s3->s1;\n" +
			"  s5->s3;\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_abx_acx() {
		PredictionContext x1 = x(false);
		PredictionContext x2 = x(false);
		PredictionContext b = createSingleton(x1, 2);
		PredictionContext c = createSingleton(x2, 3);
		PredictionContext a1 = createSingleton(b, 1);
		PredictionContext a2 = createSingleton(c, 1);
		PredictionContext r = contextCache.join(a1, a2);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s1 [label=\"9\"];\n" +
			"  s7 [shape=box, label=\"[2, 3]\"];\n" +
			"  s8 [label=\"1\"];\n" +
			"  s1->s0;\n" +
			"  s7->s1 [label=\"parent[0]\"];\n" +
			"  s7->s1 [label=\"parent[1]\"];\n" +
			"  s8->s7;\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_ax_bx_same() {
		PredictionContext x = x(false);
		PredictionContext a = createSingleton(x, 1);
		PredictionContext b = createSingleton(x, 2);
		PredictionContext r = contextCache.join(a, b);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s1 [label=\"9\"];\n" +
			"  s4 [shape=box, label=\"[1, 2]\"];\n" +
			"  s1->s0;\n" +
			"  s4->s1 [label=\"parent[0]\"];\n" +
			"  s4->s1 [label=\"parent[1]\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_ax_bx() {
		PredictionContext x1 = x(false);
		PredictionContext x2 = x(false);
		PredictionContext a = createSingleton(x1, 1);
		PredictionContext b = createSingleton(x2, 2);
		PredictionContext r = contextCache.join(a, b);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s1 [label=\"9\"];\n" +
			"  s5 [shape=box, label=\"[1, 2]\"];\n" +
			"  s1->s0;\n" +
			"  s5->s1 [label=\"parent[0]\"];\n" +
			"  s5->s1 [label=\"parent[1]\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_ax_by() {
		PredictionContext a = createSingleton(x(false), 1);
		PredictionContext b = createSingleton(y(false), 2);
		PredictionContext r = contextCache.join(a, b);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s1 [label=\"9\"];\n" +
			"  s3 [label=\"10\"];\n" +
			"  s5 [shape=box, label=\"[1, 2]\"];\n" +
			"  s1->s0;\n" +
			"  s3->s0;\n" +
			"  s5->s1 [label=\"parent[0]\"];\n" +
			"  s5->s3 [label=\"parent[1]\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_a$_bx() {
		PredictionContext x2 = x(false);
		PredictionContext a = a(false);
		PredictionContext b = createSingleton(x2, 2);
		PredictionContext r = contextCache.join(a, b);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s4 [shape=box, label=\"[1, 2]\"];\n" +
			"  s4->s0 [label=\"parent[0]\"];\n" +
			"  s4->s0 [label=\"parent[1]\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_a$_bx_fullctx() {
		PredictionContext x2 = x(true);
		PredictionContext a = a(true);
		PredictionContext b = createSingleton(x2, 2);
		PredictionContext r = contextCache.join(a, b);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s1 [label=\"9\"];\n" +
			"  s4 [shape=box, label=\"[1, 2]\"];\n" +
			"  s1->s0;\n" +
			"  s4->s0 [label=\"parent[0]\"];\n" +
			"  s4->s1 [label=\"parent[1]\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_aex_bfx() {
		PredictionContext x1 = x(false);
		PredictionContext x2 = x(false);
		PredictionContext e = createSingleton(x1, 5);
		PredictionContext f = createSingleton(x2, 6);
		PredictionContext a = createSingleton(e, 1);
		PredictionContext b = createSingleton(f, 2);
		PredictionContext r = contextCache.join(a, b);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s1 [label=\"9\"];\n" +
			"  s2 [label=\"9\"];\n" +
			"  s3 [label=\"5\"];\n" +
			"  s4 [label=\"6\"];\n" +
			"  s7 [shape=box, label=\"[1, 2]\"];\n" +
			"  s1->s0;\n" +
			"  s2->s0;\n" +
			"  s3->s1;\n" +
			"  s4->s2;\n" +
			"  s7->s3 [label=\"parent[0]\"];\n" +
			"  s7->s4 [label=\"parent[1]\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	// Array merges

	@Test public void test_A$_A$_fullctx() {
		PredictionContext A1 = array(PredictionContext.EMPTY_FULL);
		PredictionContext A2 = array(PredictionContext.EMPTY_FULL);
		PredictionContext r = contextCache.join(A1, A2);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s1 [shape=box, label=\"[-2]\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_Aab_Ac() { // a,b + c
		PredictionContext a = a(false);
		PredictionContext b = b(false);
		PredictionContext c = c(false);
		PredictionContext A1 = array(a, b);
		PredictionContext A2 = array(c);
		PredictionContext r = contextCache.join(A1, A2);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s6 [shape=box, label=\"[1, 2, 3]\"];\n" +
			"  s6->s0 [label=\"parent[0]\"];\n" +
			"  s6->s0 [label=\"parent[1]\"];\n" +
			"  s6->s0 [label=\"parent[2]\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_Aa_Aa() {
		PredictionContext a1 = a(false);
		PredictionContext a2 = a(false);
		PredictionContext A1 = array(a1);
		PredictionContext A2 = array(a2);
		PredictionContext r = contextCache.join(A1, A2);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s3 [shape=box, label=\"[1]\"];\n" +
			"  s3->s0;\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_Aa_Abc() { // a + b,c
		PredictionContext a = a(false);
		PredictionContext b = b(false);
		PredictionContext c = c(false);
		PredictionContext A1 = array(a);
		PredictionContext A2 = array(b, c);
		PredictionContext r = contextCache.join(A1, A2);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s6 [shape=box, label=\"[1, 2, 3]\"];\n" +
			"  s6->s0 [label=\"parent[0]\"];\n" +
			"  s6->s0 [label=\"parent[1]\"];\n" +
			"  s6->s0 [label=\"parent[2]\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_Aac_Ab() { // a,c + b
		PredictionContext a = a(false);
		PredictionContext b = b(false);
		PredictionContext c = c(false);
		PredictionContext A1 = array(a, c);
		PredictionContext A2 = array(b);
		PredictionContext r = contextCache.join(A1, A2);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s6 [shape=box, label=\"[1, 2, 3]\"];\n" +
			"  s6->s0 [label=\"parent[0]\"];\n" +
			"  s6->s0 [label=\"parent[1]\"];\n" +
			"  s6->s0 [label=\"parent[2]\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_Aab_Aa() { // a,b + a
		PredictionContext A1 = array(a(false), b(false));
		PredictionContext A2 = array(a(false));
		PredictionContext r = contextCache.join(A1, A2);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s3 [shape=box, label=\"[1, 2]\"];\n" +
			"  s3->s0 [label=\"parent[0]\"];\n" +
			"  s3->s0 [label=\"parent[1]\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_Aab_Ab() { // a,b + b
		PredictionContext A1 = array(a(false), b(false));
		PredictionContext A2 = array(b(false));
		PredictionContext r = contextCache.join(A1, A2);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s3 [shape=box, label=\"[1, 2]\"];\n" +
			"  s3->s0 [label=\"parent[0]\"];\n" +
			"  s3->s0 [label=\"parent[1]\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_Aax_Aby() { // ax + by but in arrays
		PredictionContext a = createSingleton(x(false), 1);
		PredictionContext b = createSingleton(y(false), 2);
		PredictionContext A1 = array(a);
		PredictionContext A2 = array(b);
		PredictionContext r = contextCache.join(A1, A2);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s1 [label=\"9\"];\n" +
			"  s3 [label=\"10\"];\n" +
			"  s7 [shape=box, label=\"[1, 2]\"];\n" +
			"  s1->s0;\n" +
			"  s3->s0;\n" +
			"  s7->s1 [label=\"parent[0]\"];\n" +
			"  s7->s3 [label=\"parent[1]\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_Aax_Aay() { // ax + ay -> merged singleton a, array parent
		PredictionContext a1 = createSingleton(x(false), 1);
		PredictionContext a2 = createSingleton(y(false), 1);
		PredictionContext A1 = array(a1);
		PredictionContext A2 = array(a2);
		PredictionContext r = contextCache.join(A1, A2);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s7 [shape=box, label=\"[9, 10]\"];\n" +
			"  s8 [label=\"1\"];\n" +
			"  s7->s0 [label=\"parent[0]\"];\n" +
			"  s7->s0 [label=\"parent[1]\"];\n" +
			"  s8->s7;\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_Aaxc_Aayd() { // ax,c + ay,d -> merged a, array parent
		PredictionContext a1 = createSingleton(x(false), 1);
		PredictionContext a2 = createSingleton(y(false), 1);
		PredictionContext A1 = array(a1, c(false));
		PredictionContext A2 = array(a2, d(false));
		PredictionContext r = contextCache.join(A1, A2);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s9 [shape=box, label=\"[9, 10]\"];\n" +
			"  s10 [shape=box, label=\"[1, 3, 4]\"];\n" +
			"  s9->s0 [label=\"parent[0]\"];\n" +
			"  s9->s0 [label=\"parent[1]\"];\n" +
			"  s10->s9 [label=\"parent[0]\"];\n" +
			"  s10->s0 [label=\"parent[1]\"];\n" +
			"  s10->s0 [label=\"parent[2]\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_Aaubv_Acwdx() { // au,bv + cw,dx -> [a,b,c,d]->[u,v,w,x]
		PredictionContext a = createSingleton(u(false), 1);
		PredictionContext b = createSingleton(v(false), 2);
		PredictionContext c = createSingleton(w(false), 3);
		PredictionContext d = createSingleton(x(false), 4);
		PredictionContext A1 = array(a, b);
		PredictionContext A2 = array(c, d);
		PredictionContext r = contextCache.join(A1, A2);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s1 [label=\"6\"];\n" +
			"  s3 [label=\"7\"];\n" +
			"  s5 [label=\"8\"];\n" +
			"  s7 [label=\"9\"];\n" +
			"  s11 [shape=box, label=\"[1, 2, 3, 4]\"];\n" +
			"  s1->s0;\n" +
			"  s3->s0;\n" +
			"  s5->s0;\n" +
			"  s7->s0;\n" +
			"  s11->s1 [label=\"parent[0]\"];\n" +
			"  s11->s3 [label=\"parent[1]\"];\n" +
			"  s11->s5 [label=\"parent[2]\"];\n" +
			"  s11->s7 [label=\"parent[3]\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_Aaubv_Abvdx() { // au,bv + bv,dx -> [a,b,d]->[u,v,x]
		PredictionContext a = createSingleton(u(false), 1);
		PredictionContext b1 = createSingleton(v(false), 2);
		PredictionContext b2 = createSingleton(v(false), 2);
		PredictionContext d = createSingleton(x(false), 4);
		PredictionContext A1 = array(a, b1);
		PredictionContext A2 = array(b2, d);
		PredictionContext r = contextCache.join(A1, A2);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s1 [label=\"6\"];\n" +
			"  s3 [label=\"7\"];\n" +
			"  s7 [label=\"9\"];\n" +
			"  s11 [shape=box, label=\"[1, 2, 4]\"];\n" +
			"  s1->s0;\n" +
			"  s3->s0;\n" +
			"  s7->s0;\n" +
			"  s11->s1 [label=\"parent[0]\"];\n" +
			"  s11->s3 [label=\"parent[1]\"];\n" +
			"  s11->s7 [label=\"parent[2]\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_Aaubv_Abwdx() { // au,bv + bw,dx -> [a,b,d]->[u,[v,w],x]
		PredictionContext a = createSingleton(u(false), 1);
		PredictionContext b1 = createSingleton(v(false), 2);
		PredictionContext b2 = createSingleton(w(false), 2);
		PredictionContext d = createSingleton(x(false), 4);
		PredictionContext A1 = array(a, b1);
		PredictionContext A2 = array(b2, d);
		PredictionContext r = contextCache.join(A1, A2);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s1 [label=\"6\"];\n" +
			"  s7 [label=\"9\"];\n" +
			"  s11 [shape=box, label=\"[7, 8]\"];\n" +
			"  s12 [shape=box, label=\"[1, 2, 4]\"];\n" +
			"  s1->s0;\n" +
			"  s7->s0;\n" +
			"  s11->s0 [label=\"parent[0]\"];\n" +
			"  s11->s0 [label=\"parent[1]\"];\n" +
			"  s12->s1 [label=\"parent[0]\"];\n" +
			"  s12->s11 [label=\"parent[1]\"];\n" +
			"  s12->s7 [label=\"parent[2]\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_Aaubv_Abvdu() { // au,bv + bv,du -> [a,b,d]->[u,v,u]; u,v shared
		PredictionContext a = createSingleton(u(false), 1);
		PredictionContext b1 = createSingleton(v(false), 2);
		PredictionContext b2 = createSingleton(v(false), 2);
		PredictionContext d = createSingleton(u(false), 4);
		PredictionContext A1 = array(a, b1);
		PredictionContext A2 = array(b2, d);
		PredictionContext r = contextCache.join(A1, A2);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s1 [label=\"6\"];\n" +
			"  s3 [label=\"7\"];\n" +
			"  s11 [shape=box, label=\"[1, 2, 4]\"];\n" +
			"  s1->s0;\n" +
			"  s3->s0;\n" +
			"  s11->s1 [label=\"parent[0]\"];\n" +
			"  s11->s3 [label=\"parent[1]\"];\n" +
			"  s11->s1 [label=\"parent[2]\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}

	@Test public void test_Aaubu_Acudu() { // au,bu + cu,du -> [a,b,c,d]->[u,u,u,u]
		PredictionContext a = createSingleton(u(false), 1);
		PredictionContext b = createSingleton(u(false), 2);
		PredictionContext c = createSingleton(u(false), 3);
		PredictionContext d = createSingleton(u(false), 4);
		PredictionContext A1 = array(a, b);
		PredictionContext A2 = array(c, d);
		PredictionContext r = contextCache.join(A1, A2);
		System.out.println(toDOTString(r));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0 [label=\"$\"];\n" +
			"  s1 [label=\"6\"];\n" +
			"  s11 [shape=box, label=\"[1, 2, 3, 4]\"];\n" +
			"  s1->s0;\n" +
			"  s11->s1 [label=\"parent[0]\"];\n" +
			"  s11->s1 [label=\"parent[1]\"];\n" +
			"  s11->s1 [label=\"parent[2]\"];\n" +
			"  s11->s1 [label=\"parent[3]\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r));
	}


	// ------------ SUPPORT -------------------------

	protected PredictionContext a(boolean fullContext) {
		return createSingleton(fullContext ? PredictionContext.EMPTY_FULL : PredictionContext.EMPTY_LOCAL, 1);
	}

	private PredictionContext b(boolean fullContext) {
		return createSingleton(fullContext ? PredictionContext.EMPTY_FULL : PredictionContext.EMPTY_LOCAL, 2);
	}

	private PredictionContext c(boolean fullContext) {
		return createSingleton(fullContext ? PredictionContext.EMPTY_FULL : PredictionContext.EMPTY_LOCAL, 3);
	}

	private PredictionContext d(boolean fullContext) {
		return createSingleton(fullContext ? PredictionContext.EMPTY_FULL : PredictionContext.EMPTY_LOCAL, 4);
	}

	private PredictionContext u(boolean fullContext) {
		return createSingleton(fullContext ? PredictionContext.EMPTY_FULL : PredictionContext.EMPTY_LOCAL, 6);
	}

	private PredictionContext v(boolean fullContext) {
		return createSingleton(fullContext ? PredictionContext.EMPTY_FULL : PredictionContext.EMPTY_LOCAL, 7);
	}

	private PredictionContext w(boolean fullContext) {
		return createSingleton(fullContext ? PredictionContext.EMPTY_FULL : PredictionContext.EMPTY_LOCAL, 8);
	}

	private PredictionContext x(boolean fullContext) {
		return createSingleton(fullContext ? PredictionContext.EMPTY_FULL : PredictionContext.EMPTY_LOCAL, 9);
	}

	private PredictionContext y(boolean fullContext) {
		return createSingleton(fullContext ? PredictionContext.EMPTY_FULL : PredictionContext.EMPTY_LOCAL, 10);
	}

	public PredictionContext createSingleton(PredictionContext parent, int payload) {
		PredictionContext a = contextCache.getChild(parent, payload);
		return a;
	}

	public PredictionContext array(PredictionContext... nodes) {
		PredictionContext result = nodes[0];
		for (int i = 1; i < nodes.length; i++) {
			result = contextCache.join(result, nodes[i]);
		}

		return result;
	}

	private static String toDOTString(PredictionContext context) {
		return "";
	}
}
