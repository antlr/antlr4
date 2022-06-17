/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.runtime.atn.ArrayPredictionContext;
import org.antlr.v4.runtime.atn.EmptyPredictionContext;
import org.antlr.v4.runtime.atn.PredictionContext;
import org.antlr.v4.runtime.atn.SingletonPredictionContext;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestGraphNodes {
	public boolean rootIsWildcard() { return true; }
	public boolean fullCtx() { return false; }

	@Test public void test_$_$() {
		PredictionContext r = PredictionContext.merge(
				EmptyPredictionContext.Instance, EmptyPredictionContext.Instance, rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[label=\"*\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_$_$_fullctx() {
		PredictionContext r = PredictionContext.merge(
				EmptyPredictionContext.Instance, EmptyPredictionContext.Instance, fullCtx(), null);
//		System.out.println(toDOTString(r, fullCtx()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[label=\"$\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, fullCtx()));
	}

	@Test public void test_x_$() {
		PredictionContext r = PredictionContext.merge(x(), EmptyPredictionContext.Instance, rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[label=\"*\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_x_$_fullctx() {
		PredictionContext r = PredictionContext.merge(x(), EmptyPredictionContext.Instance, fullCtx(), null);
//		System.out.println(toDOTString(r, fullCtx()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[shape=record, label=\"<p0>|<p1>$\"];\n" +
			"  s1[label=\"$\"];\n" +
			"  s0:p0->s1[label=\"9\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, fullCtx()));
	}

	@Test public void test_$_x() {
		PredictionContext r = PredictionContext.merge(EmptyPredictionContext.Instance, x(), rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[label=\"*\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_$_x_fullctx() {
		PredictionContext r = PredictionContext.merge(EmptyPredictionContext.Instance, x(), fullCtx(), null);
//		System.out.println(toDOTString(r, fullCtx()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[shape=record, label=\"<p0>|<p1>$\"];\n" +
			"  s1[label=\"$\"];\n" +
			"  s0:p0->s1[label=\"9\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, fullCtx()));
	}

	@Test public void test_a_a() {
		PredictionContext r = PredictionContext.merge(a(), a(), rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[label=\"0\"];\n" +
			"  s1[label=\"*\"];\n" +
			"  s0->s1[label=\"1\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_a$_ax() {
		PredictionContext a1 = a();
		PredictionContext x = x();
		PredictionContext a2 = createSingleton(x, 1);
		PredictionContext r = PredictionContext.merge(a1, a2, rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[label=\"0\"];\n" +
			"  s1[label=\"*\"];\n" +
			"  s0->s1[label=\"1\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_a$_ax_fullctx() {
		PredictionContext a1 = a();
		PredictionContext x = x();
		PredictionContext a2 = createSingleton(x, 1);
		PredictionContext r = PredictionContext.merge(a1, a2, fullCtx(), null);
//		System.out.println(toDOTString(r, fullCtx()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[label=\"0\"];\n" +
			"  s1[shape=record, label=\"<p0>|<p1>$\"];\n" +
			"  s2[label=\"$\"];\n" +
			"  s0->s1[label=\"1\"];\n" +
			"  s1:p0->s2[label=\"9\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, fullCtx()));
	}

	@Test public void test_ax$_a$() {
		PredictionContext x = x();
		PredictionContext a1 = createSingleton(x, 1);
		PredictionContext a2 = a();
		PredictionContext r = PredictionContext.merge(a1, a2, rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[label=\"0\"];\n" +
			"  s1[label=\"*\"];\n" +
			"  s0->s1[label=\"1\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_aa$_a$_$_fullCtx() {
		PredictionContext empty = EmptyPredictionContext.Instance;
		PredictionContext child1 = createSingleton(empty, 8);
		PredictionContext right = PredictionContext.merge(empty, child1, false, null);
		PredictionContext left = createSingleton(right, 8);
		PredictionContext merged = PredictionContext.merge(left, right, false, null);
		String actual = toDOTString(merged, false);
//		System.out.println(actual);
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[shape=record, label=\"<p0>|<p1>$\"];\n" +
			"  s1[shape=record, label=\"<p0>|<p1>$\"];\n" +
			"  s2[label=\"$\"];\n" +
			"  s0:p0->s1[label=\"8\"];\n" +
			"  s1:p0->s2[label=\"8\"];\n" +
			"}\n";
		assertEquals(expecting, actual);
	}

	@Test public void test_ax$_a$_fullctx() {
		PredictionContext x = x();
		PredictionContext a1 = createSingleton(x, 1);
		PredictionContext a2 = a();
		PredictionContext r = PredictionContext.merge(a1, a2, fullCtx(), null);
//		System.out.println(toDOTString(r, fullCtx()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[label=\"0\"];\n" +
			"  s1[shape=record, label=\"<p0>|<p1>$\"];\n" +
			"  s2[label=\"$\"];\n" +
			"  s0->s1[label=\"1\"];\n" +
			"  s1:p0->s2[label=\"9\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, fullCtx()));
	}

	@Test public void test_a_b() {
		PredictionContext r = PredictionContext.merge(a(), b(), rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[shape=record, label=\"<p0>|<p1>\"];\n" +
			"  s1[label=\"*\"];\n" +
			"  s0:p0->s1[label=\"1\"];\n" +
			"  s0:p1->s1[label=\"2\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_ax_ax_same() {
		PredictionContext x = x();
		PredictionContext a1 = createSingleton(x, 1);
		PredictionContext a2 = createSingleton(x, 1);
		PredictionContext r = PredictionContext.merge(a1, a2, rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[label=\"0\"];\n" +
			"  s1[label=\"1\"];\n" +
			"  s2[label=\"*\"];\n" +
			"  s0->s1[label=\"1\"];\n" +
			"  s1->s2[label=\"9\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_ax_ax() {
		PredictionContext x1 = x();
		PredictionContext x2 = x();
		PredictionContext a1 = createSingleton(x1, 1);
		PredictionContext a2 = createSingleton(x2, 1);
		PredictionContext r = PredictionContext.merge(a1, a2, rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[label=\"0\"];\n" +
			"  s1[label=\"1\"];\n" +
			"  s2[label=\"*\"];\n" +
			"  s0->s1[label=\"1\"];\n" +
			"  s1->s2[label=\"9\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_abx_abx() {
		PredictionContext x1 = x();
		PredictionContext x2 = x();
		PredictionContext b1 = createSingleton(x1, 2);
		PredictionContext b2 = createSingleton(x2, 2);
		PredictionContext a1 = createSingleton(b1, 1);
		PredictionContext a2 = createSingleton(b2, 1);
		PredictionContext r = PredictionContext.merge(a1, a2, rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[label=\"0\"];\n" +
			"  s1[label=\"1\"];\n" +
			"  s2[label=\"2\"];\n" +
			"  s3[label=\"*\"];\n" +
			"  s0->s1[label=\"1\"];\n" +
			"  s1->s2[label=\"2\"];\n" +
			"  s2->s3[label=\"9\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_abx_acx() {
		PredictionContext x1 = x();
		PredictionContext x2 = x();
		PredictionContext b = createSingleton(x1, 2);
		PredictionContext c = createSingleton(x2, 3);
		PredictionContext a1 = createSingleton(b, 1);
		PredictionContext a2 = createSingleton(c, 1);
		PredictionContext r = PredictionContext.merge(a1, a2, rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[label=\"0\"];\n" +
			"  s1[shape=record, label=\"<p0>|<p1>\"];\n" +
			"  s2[label=\"2\"];\n" +
			"  s3[label=\"*\"];\n" +
			"  s0->s1[label=\"1\"];\n" +
			"  s1:p0->s2[label=\"2\"];\n" +
			"  s1:p1->s2[label=\"3\"];\n" +
			"  s2->s3[label=\"9\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_ax_bx_same() {
		PredictionContext x = x();
		PredictionContext a = createSingleton(x, 1);
		PredictionContext b = createSingleton(x, 2);
		PredictionContext r = PredictionContext.merge(a, b, rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[shape=record, label=\"<p0>|<p1>\"];\n" +
			"  s1[label=\"1\"];\n" +
			"  s2[label=\"*\"];\n" +
			"  s0:p0->s1[label=\"1\"];\n" +
			"  s0:p1->s1[label=\"2\"];\n" +
			"  s1->s2[label=\"9\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_ax_bx() {
		PredictionContext x1 = x();
		PredictionContext x2 = x();
		PredictionContext a = createSingleton(x1, 1);
		PredictionContext b = createSingleton(x2, 2);
		PredictionContext r = PredictionContext.merge(a, b, rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[shape=record, label=\"<p0>|<p1>\"];\n" +
			"  s1[label=\"1\"];\n" +
			"  s2[label=\"*\"];\n" +
			"  s0:p0->s1[label=\"1\"];\n" +
			"  s0:p1->s1[label=\"2\"];\n" +
			"  s1->s2[label=\"9\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_ax_by() {
		PredictionContext a = createSingleton(x(), 1);
		PredictionContext b = createSingleton(y(), 2);
		PredictionContext r = PredictionContext.merge(a, b, rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[shape=record, label=\"<p0>|<p1>\"];\n" +
			"  s2[label=\"2\"];\n" +
			"  s3[label=\"*\"];\n" +
			"  s1[label=\"1\"];\n" +
			"  s0:p0->s1[label=\"1\"];\n" +
			"  s0:p1->s2[label=\"2\"];\n" +
			"  s2->s3[label=\"10\"];\n" +
			"  s1->s3[label=\"9\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_a$_bx() {
		PredictionContext x2 = x();
		PredictionContext a = a();
		PredictionContext b = createSingleton(x2, 2);
		PredictionContext r = PredictionContext.merge(a, b, rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[shape=record, label=\"<p0>|<p1>\"];\n" +
			"  s2[label=\"2\"];\n" +
			"  s1[label=\"*\"];\n" +
			"  s0:p0->s1[label=\"1\"];\n" +
			"  s0:p1->s2[label=\"2\"];\n" +
			"  s2->s1[label=\"9\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_a$_bx_fullctx() {
		PredictionContext x2 = x();
		PredictionContext a = a();
		PredictionContext b = createSingleton(x2, 2);
		PredictionContext r = PredictionContext.merge(a, b, fullCtx(), null);
//		System.out.println(toDOTString(r, fullCtx()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[shape=record, label=\"<p0>|<p1>\"];\n" +
			"  s2[label=\"2\"];\n" +
			"  s1[label=\"$\"];\n" +
			"  s0:p0->s1[label=\"1\"];\n" +
			"  s0:p1->s2[label=\"2\"];\n" +
			"  s2->s1[label=\"9\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, fullCtx()));
	}

	@Disabled("Known inefficiency but deferring resolving the issue for now")
	@Test public void test_aex_bfx() {
		// TJP: this is inefficient as it leaves the top x nodes unmerged.
		PredictionContext x1 = x();
		PredictionContext x2 = x();
		PredictionContext e = createSingleton(x1, 5);
		PredictionContext f = createSingleton(x2, 6);
		PredictionContext a = createSingleton(e, 1);
		PredictionContext b = createSingleton(f, 2);
		PredictionContext r = PredictionContext.merge(a, b, rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[shape=record, label=\"<p0>|<p1>\"];\n" +
			"  s2[label=\"2\"];\n" +
			"  s3[label=\"3\"];\n" +
			"  s4[label=\"*\"];\n" +
			"  s1[label=\"1\"];\n" +
			"  s0:p0->s1[label=\"1\"];\n" +
			"  s0:p1->s2[label=\"2\"];\n" +
			"  s2->s3[label=\"6\"];\n" +
			"  s3->s4[label=\"9\"];\n" +
			"  s1->s3[label=\"5\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	// Array merges

	@Test public void test_A$_A$_fullctx() {
		ArrayPredictionContext A1 = array(EmptyPredictionContext.Instance);
		ArrayPredictionContext A2 = array(EmptyPredictionContext.Instance);
		PredictionContext r = PredictionContext.merge(A1, A2, fullCtx(), null);
//		System.out.println(toDOTString(r, fullCtx()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[label=\"$\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, fullCtx()));
	}

	@Test public void test_Aab_Ac() { // a,b + c
		SingletonPredictionContext a = a();
		SingletonPredictionContext b = b();
		SingletonPredictionContext c = c();
		ArrayPredictionContext A1 = array(a, b);
		ArrayPredictionContext A2 = array(c);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[shape=record, label=\"<p0>|<p1>|<p2>\"];\n" +
			"  s1[label=\"*\"];\n" +
			"  s0:p0->s1[label=\"1\"];\n" +
			"  s0:p1->s1[label=\"2\"];\n" +
			"  s0:p2->s1[label=\"3\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_Aa_Aa() {
		SingletonPredictionContext a1 = a();
		SingletonPredictionContext a2 = a();
		ArrayPredictionContext A1 = array(a1);
		ArrayPredictionContext A2 = array(a2);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[label=\"0\"];\n" +
			"  s1[label=\"*\"];\n" +
			"  s0->s1[label=\"1\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_Aa_Abc() { // a + b,c
		SingletonPredictionContext a = a();
		SingletonPredictionContext b = b();
		SingletonPredictionContext c = c();
		ArrayPredictionContext A1 = array(a);
		ArrayPredictionContext A2 = array(b, c);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[shape=record, label=\"<p0>|<p1>|<p2>\"];\n" +
			"  s1[label=\"*\"];\n" +
			"  s0:p0->s1[label=\"1\"];\n" +
			"  s0:p1->s1[label=\"2\"];\n" +
			"  s0:p2->s1[label=\"3\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_Aac_Ab() { // a,c + b
		SingletonPredictionContext a = a();
		SingletonPredictionContext b = b();
		SingletonPredictionContext c = c();
		ArrayPredictionContext A1 = array(a, c);
		ArrayPredictionContext A2 = array(b);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[shape=record, label=\"<p0>|<p1>|<p2>\"];\n" +
			"  s1[label=\"*\"];\n" +
			"  s0:p0->s1[label=\"1\"];\n" +
			"  s0:p1->s1[label=\"2\"];\n" +
			"  s0:p2->s1[label=\"3\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_Aab_Aa() { // a,b + a
		ArrayPredictionContext A1 = array(a(), b());
		ArrayPredictionContext A2 = array(a());
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[shape=record, label=\"<p0>|<p1>\"];\n" +
			"  s1[label=\"*\"];\n" +
			"  s0:p0->s1[label=\"1\"];\n" +
			"  s0:p1->s1[label=\"2\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_Aab_Ab() { // a,b + b
		ArrayPredictionContext A1 = array(a(), b());
		ArrayPredictionContext A2 = array(b());
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[shape=record, label=\"<p0>|<p1>\"];\n" +
			"  s1[label=\"*\"];\n" +
			"  s0:p0->s1[label=\"1\"];\n" +
			"  s0:p1->s1[label=\"2\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_Aax_Aby() { // ax + by but in arrays
		SingletonPredictionContext a = createSingleton(x(), 1);
		SingletonPredictionContext b = createSingleton(y(), 2);
		ArrayPredictionContext A1 = array(a);
		ArrayPredictionContext A2 = array(b);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[shape=record, label=\"<p0>|<p1>\"];\n" +
			"  s2[label=\"2\"];\n" +
			"  s3[label=\"*\"];\n" +
			"  s1[label=\"1\"];\n" +
			"  s0:p0->s1[label=\"1\"];\n" +
			"  s0:p1->s2[label=\"2\"];\n" +
			"  s2->s3[label=\"10\"];\n" +
			"  s1->s3[label=\"9\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_Aax_Aay() { // ax + ay -> merged singleton a, array parent
		SingletonPredictionContext a1 = createSingleton(x(), 1);
		SingletonPredictionContext a2 = createSingleton(y(), 1);
		ArrayPredictionContext A1 = array(a1);
		ArrayPredictionContext A2 = array(a2);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[label=\"0\"];\n" +
			"  s1[shape=record, label=\"<p0>|<p1>\"];\n" +
			"  s2[label=\"*\"];\n" +
			"  s0->s1[label=\"1\"];\n" +
			"  s1:p0->s2[label=\"9\"];\n" +
			"  s1:p1->s2[label=\"10\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_Aaxc_Aayd() { // ax,c + ay,d -> merged a, array parent
		SingletonPredictionContext a1 = createSingleton(x(), 1);
		SingletonPredictionContext a2 = createSingleton(y(), 1);
		ArrayPredictionContext A1 = array(a1, c());
		ArrayPredictionContext A2 = array(a2, d());
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[shape=record, label=\"<p0>|<p1>|<p2>\"];\n" +
			"  s2[label=\"*\"];\n" +
			"  s1[shape=record, label=\"<p0>|<p1>\"];\n" +
			"  s0:p0->s1[label=\"1\"];\n" +
			"  s0:p1->s2[label=\"3\"];\n" +
			"  s0:p2->s2[label=\"4\"];\n" +
			"  s1:p0->s2[label=\"9\"];\n" +
			"  s1:p1->s2[label=\"10\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_Aaubv_Acwdx() { // au,bv + cw,dx -> [a,b,c,d]->[u,v,w,x]
		SingletonPredictionContext a = createSingleton(u(), 1);
		SingletonPredictionContext b = createSingleton(v(), 2);
		SingletonPredictionContext c = createSingleton(w(), 3);
		SingletonPredictionContext d = createSingleton(x(), 4);
		ArrayPredictionContext A1 = array(a, b);
		ArrayPredictionContext A2 = array(c, d);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[shape=record, label=\"<p0>|<p1>|<p2>|<p3>\"];\n" +
			"  s4[label=\"4\"];\n" +
			"  s5[label=\"*\"];\n" +
			"  s3[label=\"3\"];\n" +
			"  s2[label=\"2\"];\n" +
			"  s1[label=\"1\"];\n" +
			"  s0:p0->s1[label=\"1\"];\n" +
			"  s0:p1->s2[label=\"2\"];\n" +
			"  s0:p2->s3[label=\"3\"];\n" +
			"  s0:p3->s4[label=\"4\"];\n" +
			"  s4->s5[label=\"9\"];\n" +
			"  s3->s5[label=\"8\"];\n" +
			"  s2->s5[label=\"7\"];\n" +
			"  s1->s5[label=\"6\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_Aaubv_Abvdx() { // au,bv + bv,dx -> [a,b,d]->[u,v,x]
		SingletonPredictionContext a = createSingleton(u(), 1);
		SingletonPredictionContext b1 = createSingleton(v(), 2);
		SingletonPredictionContext b2 = createSingleton(v(), 2);
		SingletonPredictionContext d = createSingleton(x(), 4);
		ArrayPredictionContext A1 = array(a, b1);
		ArrayPredictionContext A2 = array(b2, d);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[shape=record, label=\"<p0>|<p1>|<p2>\"];\n" +
			"  s3[label=\"3\"];\n" +
			"  s4[label=\"*\"];\n" +
			"  s2[label=\"2\"];\n" +
			"  s1[label=\"1\"];\n" +
			"  s0:p0->s1[label=\"1\"];\n" +
			"  s0:p1->s2[label=\"2\"];\n" +
			"  s0:p2->s3[label=\"4\"];\n" +
			"  s3->s4[label=\"9\"];\n" +
			"  s2->s4[label=\"7\"];\n" +
			"  s1->s4[label=\"6\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_Aaubv_Abwdx() { // au,bv + bw,dx -> [a,b,d]->[u,[v,w],x]
		SingletonPredictionContext a = createSingleton(u(), 1);
		SingletonPredictionContext b1 = createSingleton(v(), 2);
		SingletonPredictionContext b2 = createSingleton(w(), 2);
		SingletonPredictionContext d = createSingleton(x(), 4);
		ArrayPredictionContext A1 = array(a, b1);
		ArrayPredictionContext A2 = array(b2, d);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[shape=record, label=\"<p0>|<p1>|<p2>\"];\n" +
			"  s3[label=\"3\"];\n" +
			"  s4[label=\"*\"];\n" +
			"  s2[shape=record, label=\"<p0>|<p1>\"];\n" +
			"  s1[label=\"1\"];\n" +
			"  s0:p0->s1[label=\"1\"];\n" +
			"  s0:p1->s2[label=\"2\"];\n" +
			"  s0:p2->s3[label=\"4\"];\n" +
			"  s3->s4[label=\"9\"];\n" +
			"  s2:p0->s4[label=\"7\"];\n" +
			"  s2:p1->s4[label=\"8\"];\n" +
			"  s1->s4[label=\"6\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_Aaubv_Abvdu() { // au,bv + bv,du -> [a,b,d]->[u,v,u]; u,v shared
		SingletonPredictionContext a = createSingleton(u(), 1);
		SingletonPredictionContext b1 = createSingleton(v(), 2);
		SingletonPredictionContext b2 = createSingleton(v(), 2);
		SingletonPredictionContext d = createSingleton(u(), 4);
		ArrayPredictionContext A1 = array(a, b1);
		ArrayPredictionContext A2 = array(b2, d);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[shape=record, label=\"<p0>|<p1>|<p2>\"];\n" +
			"  s2[label=\"2\"];\n" +
			"  s3[label=\"*\"];\n" +
			"  s1[label=\"1\"];\n" +
			"  s0:p0->s1[label=\"1\"];\n" +
			"  s0:p1->s2[label=\"2\"];\n" +
			"  s0:p2->s1[label=\"4\"];\n" +
			"  s2->s3[label=\"7\"];\n" +
			"  s1->s3[label=\"6\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}

	@Test public void test_Aaubu_Acudu() { // au,bu + cu,du -> [a,b,c,d]->[u,u,u,u]
		SingletonPredictionContext a = createSingleton(u(), 1);
		SingletonPredictionContext b = createSingleton(u(), 2);
		SingletonPredictionContext c = createSingleton(u(), 3);
		SingletonPredictionContext d = createSingleton(u(), 4);
		ArrayPredictionContext A1 = array(a, b);
		ArrayPredictionContext A2 = array(c, d);
		PredictionContext r = PredictionContext.merge(A1, A2, rootIsWildcard(), null);
//		System.out.println(toDOTString(r, rootIsWildcard()));
		String expecting =
			"digraph G {\n" +
			"rankdir=LR;\n" +
			"  s0[shape=record, label=\"<p0>|<p1>|<p2>|<p3>\"];\n" +
			"  s1[label=\"1\"];\n" +
			"  s2[label=\"*\"];\n" +
			"  s0:p0->s1[label=\"1\"];\n" +
			"  s0:p1->s1[label=\"2\"];\n" +
			"  s0:p2->s1[label=\"3\"];\n" +
			"  s0:p3->s1[label=\"4\"];\n" +
			"  s1->s2[label=\"6\"];\n" +
			"}\n";
		assertEquals(expecting, toDOTString(r, rootIsWildcard()));
	}


	// ------------ SUPPORT -------------------------

	protected SingletonPredictionContext a() {
		return createSingleton(EmptyPredictionContext.Instance, 1);
	}

	private SingletonPredictionContext b() {
		return createSingleton(EmptyPredictionContext.Instance, 2);
	}

	private SingletonPredictionContext c() {
		return createSingleton(EmptyPredictionContext.Instance, 3);
	}

	private SingletonPredictionContext d() {
		return createSingleton(EmptyPredictionContext.Instance, 4);
	}

	private SingletonPredictionContext u() {
		return createSingleton(EmptyPredictionContext.Instance, 6);
	}

	private SingletonPredictionContext v() {
		return createSingleton(EmptyPredictionContext.Instance, 7);
	}

	private SingletonPredictionContext w() {
		return createSingleton(EmptyPredictionContext.Instance, 8);
	}

	private SingletonPredictionContext x() {
		return createSingleton(EmptyPredictionContext.Instance, 9);
	}

	private SingletonPredictionContext y() {
		return createSingleton(EmptyPredictionContext.Instance, 10);
	}

	public SingletonPredictionContext createSingleton(PredictionContext parent, int payload) {
		SingletonPredictionContext a = SingletonPredictionContext.create(parent, payload);
		return a;
	}

	public ArrayPredictionContext array(SingletonPredictionContext... nodes) {
		PredictionContext[] parents = new PredictionContext[nodes.length];
		int[] invokingStates = new int[nodes.length];
		for (int i=0; i<nodes.length; i++) {
			parents[i] = nodes[i].parent;
			invokingStates[i] = nodes[i].returnState;
		}
		return new ArrayPredictionContext(parents, invokingStates);
	}

	private static String toDOTString(PredictionContext context, boolean rootIsWildcard) {
		StringBuilder nodes = new StringBuilder();
		StringBuilder edges = new StringBuilder();
		Map<PredictionContext, PredictionContext> visited = new IdentityHashMap<PredictionContext, PredictionContext>();
		Map<PredictionContext, Integer> contextIds = new IdentityHashMap<PredictionContext, Integer>();
		Deque<PredictionContext> workList = new ArrayDeque<PredictionContext>();
		visited.put(context, context);
		contextIds.put(context, contextIds.size());
		workList.add(context);
		while (!workList.isEmpty()) {
			PredictionContext current = workList.pop();
			nodes.append("  s").append(contextIds.get(current)).append('[');

			if (current.size() > 1) {
				nodes.append("shape=record, ");
			}

			nodes.append("label=\"");

			if (current.isEmpty()) {
				nodes.append(rootIsWildcard ? '*' : '$');
			} else if (current.size() > 1) {
				for (int i = 0; i < current.size(); i++) {
					if (i > 0) {
						nodes.append('|');
					}

					nodes.append("<p").append(i).append('>');
					if (current.getReturnState(i) == PredictionContext.EMPTY_RETURN_STATE) {
						nodes.append(rootIsWildcard ? '*' : '$');
					}
				}
			} else {
				nodes.append(contextIds.get(current));
			}

			nodes.append("\"];\n");

			if (current.isEmpty()) {
				continue;
			}

			for (int i = 0; i < current.size(); i++) {
				if (current.getReturnState(i) == PredictionContext.EMPTY_RETURN_STATE) {
					continue;
				}

				if (visited.put(current.getParent(i), current.getParent(i)) == null) {
					contextIds.put(current.getParent(i), contextIds.size());
					workList.push(current.getParent(i));
				}

				edges.append("  s").append(contextIds.get(current));
				if (current.size() > 1) {
					edges.append(":p").append(i);
				}

				edges.append("->");
				edges.append('s').append(contextIds.get(current.getParent(i)));
				edges.append("[label=\"").append(current.getReturnState(i)).append("\"]");
				edges.append(";\n");
			}
		}

		StringBuilder builder = new StringBuilder();
		builder.append("digraph G {\n");
		builder.append("rankdir=LR;\n");
		builder.append(nodes);
		builder.append(edges);
		builder.append("}\n");
		return builder.toString();
	}
}
