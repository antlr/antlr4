/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
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
namespace Antlr4.Runtime.Test
{
    using System;
    using System.Collections.Generic;
    using System.Runtime.CompilerServices;
    using System.Text;
    using Antlr4.Runtime.Atn;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    [TestClass]
    public class TestGraphNodes
    {
        private PredictionContextCache _contextCache;

        [TestInitialize]
        public void SetUp()
        {
            _contextCache = new PredictionContextCache();
        }

        public bool RootIsWildcard
        {
            get
            {
                return true;
            }
        }

        public bool FullContext
        {
            get
            {
                return false;
            }
        }

        [TestMethod]
        public void Test_dollar_dollar()
        {
            PredictionContext r = _contextCache.Join(PredictionContext.EmptyLocal,
                                                          PredictionContext.EmptyLocal);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[label=\"*\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_dollar_dollar_fullctx()
        {
            PredictionContext r = _contextCache.Join(PredictionContext.EmptyFull,
                                                          PredictionContext.EmptyFull);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[label=\"$\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_x_dollar()
        {
            PredictionContext r = _contextCache.Join(X(false), PredictionContext.EmptyLocal);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[label=\"*\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_x_dollar_fullctx()
        {
            PredictionContext r = _contextCache.Join(X(true), PredictionContext.EmptyFull);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[shape=record, label=\"<p0>|<p1>$\"];" + Environment.NewLine +
                "  s1[label=\"$\"];" + Environment.NewLine +
                "  s0:p0->s1[label=\"9\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_dollar_x()
        {
            PredictionContext r = _contextCache.Join(PredictionContext.EmptyLocal, X(false));
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[label=\"*\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_dollar_x_fullctx()
        {
            PredictionContext r = _contextCache.Join(PredictionContext.EmptyFull, X(true));
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[shape=record, label=\"<p0>|<p1>$\"];" + Environment.NewLine +
                "  s1[label=\"$\"];" + Environment.NewLine +
                "  s0:p0->s1[label=\"9\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_a_a()
        {
            PredictionContext r = _contextCache.Join(A(false), A(false));
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[label=\"0\"];" + Environment.NewLine +
                "  s1[label=\"*\"];" + Environment.NewLine +
                "  s0->s1[label=\"1\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_adollar_ax()
        {
            PredictionContext a1 = A(false);
            PredictionContext x = X(false);
            PredictionContext a2 = CreateSingleton(x, 1);
            PredictionContext r = _contextCache.Join(a1, a2);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[label=\"0\"];" + Environment.NewLine +
                "  s1[label=\"*\"];" + Environment.NewLine +
                "  s0->s1[label=\"1\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_adollar_ax_fullctx()
        {
            PredictionContext a1 = A(true);
            PredictionContext x = X(true);
            PredictionContext a2 = CreateSingleton(x, 1);
            PredictionContext r = _contextCache.Join(a1, a2);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[label=\"0\"];" + Environment.NewLine +
                "  s1[shape=record, label=\"<p0>|<p1>$\"];" + Environment.NewLine +
                "  s2[label=\"$\"];" + Environment.NewLine +
                "  s0->s1[label=\"1\"];" + Environment.NewLine +
                "  s1:p0->s2[label=\"9\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_axdollar_adollar()
        {
            PredictionContext x = X(false);
            PredictionContext a1 = CreateSingleton(x, 1);
            PredictionContext a2 = A(false);
            PredictionContext r = _contextCache.Join(a1, a2);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[label=\"0\"];" + Environment.NewLine +
                "  s1[label=\"*\"];" + Environment.NewLine +
                "  s0->s1[label=\"1\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_aadollar_adollar_dollar_fullCtx()
        {
            PredictionContext empty = PredictionContext.EmptyFull;
            PredictionContext child1 = CreateSingleton(empty, 8);
            PredictionContext right = _contextCache.Join(empty, child1);
            PredictionContext left = CreateSingleton(right, 8);
            PredictionContext merged = _contextCache.Join(left, right);
            String actual = ToDotString(merged);
            Console.WriteLine(actual);
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[shape=record, label=\"<p0>|<p1>$\"];" + Environment.NewLine +
                "  s1[shape=record, label=\"<p0>|<p1>$\"];" + Environment.NewLine +
                "  s2[label=\"$\"];" + Environment.NewLine +
                "  s0:p0->s1[label=\"8\"];" + Environment.NewLine +
                "  s1:p0->s2[label=\"8\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, actual);
        }

        [TestMethod]
        public void Test_axdollar_adollar_fullctx()
        {
            PredictionContext x = X(true);
            PredictionContext a1 = CreateSingleton(x, 1);
            PredictionContext a2 = A(true);
            PredictionContext r = _contextCache.Join(a1, a2);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[label=\"0\"];" + Environment.NewLine +
                "  s1[shape=record, label=\"<p0>|<p1>$\"];" + Environment.NewLine +
                "  s2[label=\"$\"];" + Environment.NewLine +
                "  s0->s1[label=\"1\"];" + Environment.NewLine +
                "  s1:p0->s2[label=\"9\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_a_b()
        {
            PredictionContext r = _contextCache.Join(A(false), B(false));
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[shape=record, label=\"<p0>|<p1>\"];" + Environment.NewLine +
                "  s1[label=\"*\"];" + Environment.NewLine +
                "  s0:p0->s1[label=\"1\"];" + Environment.NewLine +
                "  s0:p1->s1[label=\"2\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_ax_ax_same()
        {
            PredictionContext x = X(false);
            PredictionContext a1 = CreateSingleton(x, 1);
            PredictionContext a2 = CreateSingleton(x, 1);
            PredictionContext r = _contextCache.Join(a1, a2);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[label=\"0\"];" + Environment.NewLine +
                "  s1[label=\"1\"];" + Environment.NewLine +
                "  s2[label=\"*\"];" + Environment.NewLine +
                "  s0->s1[label=\"1\"];" + Environment.NewLine +
                "  s1->s2[label=\"9\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_ax_ax()
        {
            PredictionContext x1 = X(false);
            PredictionContext x2 = X(false);
            PredictionContext a1 = CreateSingleton(x1, 1);
            PredictionContext a2 = CreateSingleton(x2, 1);
            PredictionContext r = _contextCache.Join(a1, a2);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[label=\"0\"];" + Environment.NewLine +
                "  s1[label=\"1\"];" + Environment.NewLine +
                "  s2[label=\"*\"];" + Environment.NewLine +
                "  s0->s1[label=\"1\"];" + Environment.NewLine +
                "  s1->s2[label=\"9\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_abx_abx()
        {
            PredictionContext x1 = X(false);
            PredictionContext x2 = X(false);
            PredictionContext b1 = CreateSingleton(x1, 2);
            PredictionContext b2 = CreateSingleton(x2, 2);
            PredictionContext a1 = CreateSingleton(b1, 1);
            PredictionContext a2 = CreateSingleton(b2, 1);
            PredictionContext r = _contextCache.Join(a1, a2);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[label=\"0\"];" + Environment.NewLine +
                "  s1[label=\"1\"];" + Environment.NewLine +
                "  s2[label=\"2\"];" + Environment.NewLine +
                "  s3[label=\"*\"];" + Environment.NewLine +
                "  s0->s1[label=\"1\"];" + Environment.NewLine +
                "  s1->s2[label=\"2\"];" + Environment.NewLine +
                "  s2->s3[label=\"9\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_abx_acx()
        {
            PredictionContext x1 = X(false);
            PredictionContext x2 = X(false);
            PredictionContext b = CreateSingleton(x1, 2);
            PredictionContext c = CreateSingleton(x2, 3);
            PredictionContext a1 = CreateSingleton(b, 1);
            PredictionContext a2 = CreateSingleton(c, 1);
            PredictionContext r = _contextCache.Join(a1, a2);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[label=\"0\"];" + Environment.NewLine +
                "  s1[shape=record, label=\"<p0>|<p1>\"];" + Environment.NewLine +
                "  s2[label=\"2\"];" + Environment.NewLine +
                "  s3[label=\"*\"];" + Environment.NewLine +
                "  s0->s1[label=\"1\"];" + Environment.NewLine +
                "  s1:p0->s2[label=\"2\"];" + Environment.NewLine +
                "  s1:p1->s2[label=\"3\"];" + Environment.NewLine +
                "  s2->s3[label=\"9\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_ax_bx_same()
        {
            PredictionContext x = X(false);
            PredictionContext a = CreateSingleton(x, 1);
            PredictionContext b = CreateSingleton(x, 2);
            PredictionContext r = _contextCache.Join(a, b);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[shape=record, label=\"<p0>|<p1>\"];" + Environment.NewLine +
                "  s1[label=\"1\"];" + Environment.NewLine +
                "  s2[label=\"*\"];" + Environment.NewLine +
                "  s0:p0->s1[label=\"1\"];" + Environment.NewLine +
                "  s0:p1->s1[label=\"2\"];" + Environment.NewLine +
                "  s1->s2[label=\"9\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_ax_bx()
        {
            PredictionContext x1 = X(false);
            PredictionContext x2 = X(false);
            PredictionContext a = CreateSingleton(x1, 1);
            PredictionContext b = CreateSingleton(x2, 2);
            PredictionContext r = _contextCache.Join(a, b);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[shape=record, label=\"<p0>|<p1>\"];" + Environment.NewLine +
                "  s1[label=\"1\"];" + Environment.NewLine +
                "  s2[label=\"*\"];" + Environment.NewLine +
                "  s0:p0->s1[label=\"1\"];" + Environment.NewLine +
                "  s0:p1->s1[label=\"2\"];" + Environment.NewLine +
                "  s1->s2[label=\"9\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_ax_by()
        {
            PredictionContext a = CreateSingleton(X(false), 1);
            PredictionContext b = CreateSingleton(Y(false), 2);
            PredictionContext r = _contextCache.Join(a, b);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[shape=record, label=\"<p0>|<p1>\"];" + Environment.NewLine +
                "  s2[label=\"2\"];" + Environment.NewLine +
                "  s3[label=\"*\"];" + Environment.NewLine +
                "  s1[label=\"1\"];" + Environment.NewLine +
                "  s0:p0->s1[label=\"1\"];" + Environment.NewLine +
                "  s0:p1->s2[label=\"2\"];" + Environment.NewLine +
                "  s2->s3[label=\"10\"];" + Environment.NewLine +
                "  s1->s3[label=\"9\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_adollar_bx()
        {
            PredictionContext x2 = X(false);
            PredictionContext a = A(false);
            PredictionContext b = CreateSingleton(x2, 2);
            PredictionContext r = _contextCache.Join(a, b);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[shape=record, label=\"<p0>|<p1>\"];" + Environment.NewLine +
                "  s2[label=\"2\"];" + Environment.NewLine +
                "  s1[label=\"*\"];" + Environment.NewLine +
                "  s0:p0->s1[label=\"1\"];" + Environment.NewLine +
                "  s0:p1->s2[label=\"2\"];" + Environment.NewLine +
                "  s2->s1[label=\"9\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_adollar_bx_fullctx()
        {
            PredictionContext x2 = X(true);
            PredictionContext a = A(true);
            PredictionContext b = CreateSingleton(x2, 2);
            PredictionContext r = _contextCache.Join(a, b);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[shape=record, label=\"<p0>|<p1>\"];" + Environment.NewLine +
                "  s2[label=\"2\"];" + Environment.NewLine +
                "  s1[label=\"$\"];" + Environment.NewLine +
                "  s0:p0->s1[label=\"1\"];" + Environment.NewLine +
                "  s0:p1->s2[label=\"2\"];" + Environment.NewLine +
                "  s2->s1[label=\"9\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_aex_bfx()
        {
            PredictionContext x1 = X(false);
            PredictionContext x2 = X(false);
            PredictionContext e = CreateSingleton(x1, 5);
            PredictionContext f = CreateSingleton(x2, 6);
            PredictionContext a = CreateSingleton(e, 1);
            PredictionContext b = CreateSingleton(f, 2);
            PredictionContext r = _contextCache.Join(a, b);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[shape=record, label=\"<p0>|<p1>\"];" + Environment.NewLine +
                "  s2[label=\"2\"];" + Environment.NewLine +
                "  s3[label=\"3\"];" + Environment.NewLine +
                "  s4[label=\"*\"];" + Environment.NewLine +
                "  s1[label=\"1\"];" + Environment.NewLine +
                "  s0:p0->s1[label=\"1\"];" + Environment.NewLine +
                "  s0:p1->s2[label=\"2\"];" + Environment.NewLine +
                "  s2->s3[label=\"6\"];" + Environment.NewLine +
                "  s3->s4[label=\"9\"];" + Environment.NewLine +
                "  s1->s3[label=\"5\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        // Array merges

        [TestMethod]
        public void Test_Adollar_Adollar_fullctx()
        {
            PredictionContext A1 = Array(PredictionContext.EmptyFull);
            PredictionContext A2 = Array(PredictionContext.EmptyFull);
            PredictionContext r = _contextCache.Join(A1, A2);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[label=\"$\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_Aab_Ac()
        { // a,b + c
            PredictionContext a = A(false);
            PredictionContext b = B(false);
            PredictionContext c = C(false);
            PredictionContext A1 = Array(a, b);
            PredictionContext A2 = Array(c);
            PredictionContext r = _contextCache.Join(A1, A2);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[shape=record, label=\"<p0>|<p1>|<p2>\"];" + Environment.NewLine +
                "  s1[label=\"*\"];" + Environment.NewLine +
                "  s0:p0->s1[label=\"1\"];" + Environment.NewLine +
                "  s0:p1->s1[label=\"2\"];" + Environment.NewLine +
                "  s0:p2->s1[label=\"3\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_Aa_Aa()
        {
            PredictionContext a1 = A(false);
            PredictionContext a2 = A(false);
            PredictionContext A1 = Array(a1);
            PredictionContext A2 = Array(a2);
            PredictionContext r = _contextCache.Join(A1, A2);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[label=\"0\"];" + Environment.NewLine +
                "  s1[label=\"*\"];" + Environment.NewLine +
                "  s0->s1[label=\"1\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_Aa_Abc()
        { // a + b,c
            PredictionContext a = A(false);
            PredictionContext b = B(false);
            PredictionContext c = C(false);
            PredictionContext A1 = Array(a);
            PredictionContext A2 = Array(b, c);
            PredictionContext r = _contextCache.Join(A1, A2);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[shape=record, label=\"<p0>|<p1>|<p2>\"];" + Environment.NewLine +
                "  s1[label=\"*\"];" + Environment.NewLine +
                "  s0:p0->s1[label=\"1\"];" + Environment.NewLine +
                "  s0:p1->s1[label=\"2\"];" + Environment.NewLine +
                "  s0:p2->s1[label=\"3\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_Aac_Ab()
        { // a,c + b
            PredictionContext a = A(false);
            PredictionContext b = B(false);
            PredictionContext c = C(false);
            PredictionContext A1 = Array(a, c);
            PredictionContext A2 = Array(b);
            PredictionContext r = _contextCache.Join(A1, A2);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[shape=record, label=\"<p0>|<p1>|<p2>\"];" + Environment.NewLine +
                "  s1[label=\"*\"];" + Environment.NewLine +
                "  s0:p0->s1[label=\"1\"];" + Environment.NewLine +
                "  s0:p1->s1[label=\"2\"];" + Environment.NewLine +
                "  s0:p2->s1[label=\"3\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_Aab_Aa()
        { // a,b + a
            PredictionContext A1 = Array(A(false), B(false));
            PredictionContext A2 = Array(A(false));
            PredictionContext r = _contextCache.Join(A1, A2);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[shape=record, label=\"<p0>|<p1>\"];" + Environment.NewLine +
                "  s1[label=\"*\"];" + Environment.NewLine +
                "  s0:p0->s1[label=\"1\"];" + Environment.NewLine +
                "  s0:p1->s1[label=\"2\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_Aab_Ab()
        { // a,b + b
            PredictionContext A1 = Array(A(false), B(false));
            PredictionContext A2 = Array(B(false));
            PredictionContext r = _contextCache.Join(A1, A2);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[shape=record, label=\"<p0>|<p1>\"];" + Environment.NewLine +
                "  s1[label=\"*\"];" + Environment.NewLine +
                "  s0:p0->s1[label=\"1\"];" + Environment.NewLine +
                "  s0:p1->s1[label=\"2\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_Aax_Aby()
        { // ax + by but in arrays
            PredictionContext a = CreateSingleton(X(false), 1);
            PredictionContext b = CreateSingleton(Y(false), 2);
            PredictionContext A1 = Array(a);
            PredictionContext A2 = Array(b);
            PredictionContext r = _contextCache.Join(A1, A2);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[shape=record, label=\"<p0>|<p1>\"];" + Environment.NewLine +
                "  s2[label=\"2\"];" + Environment.NewLine +
                "  s3[label=\"*\"];" + Environment.NewLine +
                "  s1[label=\"1\"];" + Environment.NewLine +
                "  s0:p0->s1[label=\"1\"];" + Environment.NewLine +
                "  s0:p1->s2[label=\"2\"];" + Environment.NewLine +
                "  s2->s3[label=\"10\"];" + Environment.NewLine +
                "  s1->s3[label=\"9\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_Aax_Aay()
        { // ax + ay -> merged singleton a, array parent
            PredictionContext a1 = CreateSingleton(X(false), 1);
            PredictionContext a2 = CreateSingleton(Y(false), 1);
            PredictionContext A1 = Array(a1);
            PredictionContext A2 = Array(a2);
            PredictionContext r = _contextCache.Join(A1, A2);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[label=\"0\"];" + Environment.NewLine +
                "  s1[shape=record, label=\"<p0>|<p1>\"];" + Environment.NewLine +
                "  s2[label=\"*\"];" + Environment.NewLine +
                "  s0->s1[label=\"1\"];" + Environment.NewLine +
                "  s1:p0->s2[label=\"9\"];" + Environment.NewLine +
                "  s1:p1->s2[label=\"10\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_Aaxc_Aayd()
        { // ax,c + ay,d -> merged a, array parent
            PredictionContext a1 = CreateSingleton(X(false), 1);
            PredictionContext a2 = CreateSingleton(Y(false), 1);
            PredictionContext A1 = Array(a1, C(false));
            PredictionContext A2 = Array(a2, D(false));
            PredictionContext r = _contextCache.Join(A1, A2);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[shape=record, label=\"<p0>|<p1>|<p2>\"];" + Environment.NewLine +
                "  s2[label=\"*\"];" + Environment.NewLine +
                "  s1[shape=record, label=\"<p0>|<p1>\"];" + Environment.NewLine +
                "  s0:p0->s1[label=\"1\"];" + Environment.NewLine +
                "  s0:p1->s2[label=\"3\"];" + Environment.NewLine +
                "  s0:p2->s2[label=\"4\"];" + Environment.NewLine +
                "  s1:p0->s2[label=\"9\"];" + Environment.NewLine +
                "  s1:p1->s2[label=\"10\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_Aaubv_Acwdx()
        { // au,bv + cw,dx -> [a,b,c,d]->[u,v,w,x]
            PredictionContext a = CreateSingleton(U(false), 1);
            PredictionContext b = CreateSingleton(V(false), 2);
            PredictionContext c = CreateSingleton(W(false), 3);
            PredictionContext d = CreateSingleton(X(false), 4);
            PredictionContext A1 = Array(a, b);
            PredictionContext A2 = Array(c, d);
            PredictionContext r = _contextCache.Join(A1, A2);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[shape=record, label=\"<p0>|<p1>|<p2>|<p3>\"];" + Environment.NewLine +
                "  s4[label=\"4\"];" + Environment.NewLine +
                "  s5[label=\"*\"];" + Environment.NewLine +
                "  s3[label=\"3\"];" + Environment.NewLine +
                "  s2[label=\"2\"];" + Environment.NewLine +
                "  s1[label=\"1\"];" + Environment.NewLine +
                "  s0:p0->s1[label=\"1\"];" + Environment.NewLine +
                "  s0:p1->s2[label=\"2\"];" + Environment.NewLine +
                "  s0:p2->s3[label=\"3\"];" + Environment.NewLine +
                "  s0:p3->s4[label=\"4\"];" + Environment.NewLine +
                "  s4->s5[label=\"9\"];" + Environment.NewLine +
                "  s3->s5[label=\"8\"];" + Environment.NewLine +
                "  s2->s5[label=\"7\"];" + Environment.NewLine +
                "  s1->s5[label=\"6\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_Aaubv_Abvdx()
        { // au,bv + bv,dx -> [a,b,d]->[u,v,x]
            PredictionContext a = CreateSingleton(U(false), 1);
            PredictionContext b1 = CreateSingleton(V(false), 2);
            PredictionContext b2 = CreateSingleton(V(false), 2);
            PredictionContext d = CreateSingleton(X(false), 4);
            PredictionContext A1 = Array(a, b1);
            PredictionContext A2 = Array(b2, d);
            PredictionContext r = _contextCache.Join(A1, A2);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[shape=record, label=\"<p0>|<p1>|<p2>\"];" + Environment.NewLine +
                "  s3[label=\"3\"];" + Environment.NewLine +
                "  s4[label=\"*\"];" + Environment.NewLine +
                "  s2[label=\"2\"];" + Environment.NewLine +
                "  s1[label=\"1\"];" + Environment.NewLine +
                "  s0:p0->s1[label=\"1\"];" + Environment.NewLine +
                "  s0:p1->s2[label=\"2\"];" + Environment.NewLine +
                "  s0:p2->s3[label=\"4\"];" + Environment.NewLine +
                "  s3->s4[label=\"9\"];" + Environment.NewLine +
                "  s2->s4[label=\"7\"];" + Environment.NewLine +
                "  s1->s4[label=\"6\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_Aaubv_Abwdx()
        { // au,bv + bw,dx -> [a,b,d]->[u,[v,w],x]
            PredictionContext a = CreateSingleton(U(false), 1);
            PredictionContext b1 = CreateSingleton(V(false), 2);
            PredictionContext b2 = CreateSingleton(W(false), 2);
            PredictionContext d = CreateSingleton(X(false), 4);
            PredictionContext A1 = Array(a, b1);
            PredictionContext A2 = Array(b2, d);
            PredictionContext r = _contextCache.Join(A1, A2);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[shape=record, label=\"<p0>|<p1>|<p2>\"];" + Environment.NewLine +
                "  s3[label=\"3\"];" + Environment.NewLine +
                "  s4[label=\"*\"];" + Environment.NewLine +
                "  s2[shape=record, label=\"<p0>|<p1>\"];" + Environment.NewLine +
                "  s1[label=\"1\"];" + Environment.NewLine +
                "  s0:p0->s1[label=\"1\"];" + Environment.NewLine +
                "  s0:p1->s2[label=\"2\"];" + Environment.NewLine +
                "  s0:p2->s3[label=\"4\"];" + Environment.NewLine +
                "  s3->s4[label=\"9\"];" + Environment.NewLine +
                "  s2:p0->s4[label=\"7\"];" + Environment.NewLine +
                "  s2:p1->s4[label=\"8\"];" + Environment.NewLine +
                "  s1->s4[label=\"6\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_Aaubv_Abvdu()
        { // au,bv + bv,du -> [a,b,d]->[u,v,u]; u,v shared
            PredictionContext a = CreateSingleton(U(false), 1);
            PredictionContext b1 = CreateSingleton(V(false), 2);
            PredictionContext b2 = CreateSingleton(V(false), 2);
            PredictionContext d = CreateSingleton(U(false), 4);
            PredictionContext A1 = Array(a, b1);
            PredictionContext A2 = Array(b2, d);
            PredictionContext r = _contextCache.Join(A1, A2);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[shape=record, label=\"<p0>|<p1>|<p2>\"];" + Environment.NewLine +
                "  s2[label=\"2\"];" + Environment.NewLine +
                "  s3[label=\"*\"];" + Environment.NewLine +
                "  s1[label=\"1\"];" + Environment.NewLine +
                "  s0:p0->s1[label=\"1\"];" + Environment.NewLine +
                "  s0:p1->s2[label=\"2\"];" + Environment.NewLine +
                "  s0:p2->s1[label=\"4\"];" + Environment.NewLine +
                "  s2->s3[label=\"7\"];" + Environment.NewLine +
                "  s1->s3[label=\"6\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }

        [TestMethod]
        public void Test_Aaubu_Acudu()
        { // au,bu + cu,du -> [a,b,c,d]->[u,u,u,u]
            PredictionContext a = CreateSingleton(U(false), 1);
            PredictionContext b = CreateSingleton(U(false), 2);
            PredictionContext c = CreateSingleton(U(false), 3);
            PredictionContext d = CreateSingleton(U(false), 4);
            PredictionContext A1 = Array(a, b);
            PredictionContext A2 = Array(c, d);
            PredictionContext r = _contextCache.Join(A1, A2);
            Console.WriteLine(ToDotString(r));
            String expecting =
                "digraph G {" + Environment.NewLine +
                "rankdir=LR;" + Environment.NewLine +
                "  s0[shape=record, label=\"<p0>|<p1>|<p2>|<p3>\"];" + Environment.NewLine +
                "  s1[label=\"1\"];" + Environment.NewLine +
                "  s2[label=\"*\"];" + Environment.NewLine +
                "  s0:p0->s1[label=\"1\"];" + Environment.NewLine +
                "  s0:p1->s1[label=\"2\"];" + Environment.NewLine +
                "  s0:p2->s1[label=\"3\"];" + Environment.NewLine +
                "  s0:p3->s1[label=\"4\"];" + Environment.NewLine +
                "  s1->s2[label=\"6\"];" + Environment.NewLine +
                "}" + Environment.NewLine;
            Assert.AreEqual(expecting, ToDotString(r));
        }


        // ------------ SUPPORT -------------------------

        protected PredictionContext A(bool fullContext)
        {
            return CreateSingleton(fullContext ? PredictionContext.EmptyFull : PredictionContext.EmptyLocal, 1);
        }

        private PredictionContext B(bool fullContext)
        {
            return CreateSingleton(fullContext ? PredictionContext.EmptyFull : PredictionContext.EmptyLocal, 2);
        }

        private PredictionContext C(bool fullContext)
        {
            return CreateSingleton(fullContext ? PredictionContext.EmptyFull : PredictionContext.EmptyLocal, 3);
        }

        private PredictionContext D(bool fullContext)
        {
            return CreateSingleton(fullContext ? PredictionContext.EmptyFull : PredictionContext.EmptyLocal, 4);
        }

        private PredictionContext U(bool fullContext)
        {
            return CreateSingleton(fullContext ? PredictionContext.EmptyFull : PredictionContext.EmptyLocal, 6);
        }

        private PredictionContext V(bool fullContext)
        {
            return CreateSingleton(fullContext ? PredictionContext.EmptyFull : PredictionContext.EmptyLocal, 7);
        }

        private PredictionContext W(bool fullContext)
        {
            return CreateSingleton(fullContext ? PredictionContext.EmptyFull : PredictionContext.EmptyLocal, 8);
        }

        private PredictionContext X(bool fullContext)
        {
            return CreateSingleton(fullContext ? PredictionContext.EmptyFull : PredictionContext.EmptyLocal, 9);
        }

        private PredictionContext Y(bool fullContext)
        {
            return CreateSingleton(fullContext ? PredictionContext.EmptyFull : PredictionContext.EmptyLocal, 10);
        }

        public PredictionContext CreateSingleton(PredictionContext parent, int payload)
        {
            PredictionContext a = _contextCache.GetChild(parent, payload);
            return a;
        }

        public PredictionContext Array(params PredictionContext[] nodes)
        {
            PredictionContext result = nodes[0];
            for (int i = 1; i < nodes.Length; i++)
            {
                result = _contextCache.Join(result, nodes[i]);
            }

            return result;
        }

        private static String ToDotString(PredictionContext context)
        {
            StringBuilder nodes = new StringBuilder();
            StringBuilder edges = new StringBuilder();
            IDictionary<PredictionContext, PredictionContext> visited = new IdentityHashMap<PredictionContext, PredictionContext>();
            IDictionary<PredictionContext, int> contextIds = new IdentityHashMap<PredictionContext, int>();
            Stack<PredictionContext> workList = new Stack<PredictionContext>();
            visited[context] = context;
            contextIds[context] = contextIds.Count;
            workList.Push(context);
            while (workList.Count > 0)
            {
                PredictionContext current = workList.Pop();
                nodes.Append("  s").Append(contextIds[current]).Append('[');

                if (current.Size > 1)
                {
                    nodes.Append("shape=record, ");
                }

                nodes.Append("label=\"");

                if (current.IsEmpty)
                {
                    nodes.Append(PredictionContext.IsEmptyLocal(current) ? '*' : '$');
                }
                else if (current.Size > 1)
                {
                    for (int i = 0; i < current.Size; i++)
                    {
                        if (i > 0)
                        {
                            nodes.Append('|');
                        }

                        nodes.Append("<p").Append(i).Append('>');
                        if (current.GetReturnState(i) == PredictionContext.EmptyFullStateKey)
                        {
                            nodes.Append('$');
                        }
                        else if (current.GetReturnState(i) == PredictionContext.EmptyLocalStateKey)
                        {
                            nodes.Append('*');
                        }
                    }
                }
                else
                {
                    nodes.Append(contextIds[current]);
                }

                nodes.AppendLine("\"];");

                for (int i = 0; i < current.Size; i++)
                {
                    if (current.GetReturnState(i) == PredictionContext.EmptyFullStateKey
                        || current.GetReturnState(i) == PredictionContext.EmptyLocalStateKey)
                    {
                        continue;
                    }

                    if (!visited.ContainsKey(current.GetParent(i)))
                    {
                        visited[current.GetParent(i)] = current.GetParent(i);
                        contextIds[current.GetParent(i)] = contextIds.Count;
                        workList.Push(current.GetParent(i));
                    }

                    edges.Append("  s").Append(contextIds[current]);
                    if (current.Size > 1)
                    {
                        edges.Append(":p").Append(i);
                    }

                    edges.Append("->");
                    edges.Append('s').Append(contextIds[current.GetParent(i)]);
                    edges.Append("[label=\"").Append(current.GetReturnState(i)).Append("\"]");
                    edges.AppendLine(";");
                }
            }

            StringBuilder builder = new StringBuilder();
            builder.AppendLine("digraph G {");
            builder.AppendLine("rankdir=LR;");
            builder.Append(nodes);
            builder.Append(edges);
            builder.AppendLine("}");
            return builder.ToString();
        }

        private class IdentityHashMap<TKey, TValue> : Dictionary<TKey, TValue>
            where TKey : class
        {
            public IdentityHashMap()
                : base(IdentityEqualityComparer.Default)
            {
            }
        }

        private class IdentityEqualityComparer : IEqualityComparer<object>
        {
            private static readonly IdentityEqualityComparer _default = new IdentityEqualityComparer();

            public static IdentityEqualityComparer Default
            {
                get
                {
                    return _default;
                }
            }

            public new bool Equals(object x, object y)
            {
                return x == y;
            }

            public int GetHashCode(object obj)
            {
                return RuntimeHelpers.GetHashCode(obj);
            }
        }
    }
}
