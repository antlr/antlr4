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

import org.antlr.runtime.tree.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestTreeIterator {
    static final String[] tokens = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "A", "B", "C", "D", "E", "F", "G"
    };

    @Test public void testNode() {
        TreeAdaptor adaptor = new CommonTreeAdaptor();
        TreeWizard wiz = new TreeWizard(adaptor, tokens);
        CommonTree t = (CommonTree)wiz.create("A");
        TreeIterator it = new TreeIterator(t);
        StringBuffer buf = toString(it);
        String expecting = "A EOF";
        String found = buf.toString();
        assertEquals(expecting, found);
    }

    @Test public void testFlatAB() {
        TreeAdaptor adaptor = new CommonTreeAdaptor();
        TreeWizard wiz = new TreeWizard(adaptor, tokens);
        CommonTree t = (CommonTree)wiz.create("(nil A B)");
        TreeIterator it = new TreeIterator(t);
        StringBuffer buf = toString(it);
        String expecting = "nil DOWN A B UP EOF";
        String found = buf.toString();
        assertEquals(expecting, found);
    }

    @Test public void testAB() {
        TreeAdaptor adaptor = new CommonTreeAdaptor();
        TreeWizard wiz = new TreeWizard(adaptor, tokens);
        CommonTree t = (CommonTree)wiz.create("(A B)");
        TreeIterator it = new TreeIterator(t);
        StringBuffer buf = toString(it);
        String expecting = "A DOWN B UP EOF";
        String found = buf.toString();
        assertEquals(expecting, found);
    }

    @Test public void testABC() {
        TreeAdaptor adaptor = new CommonTreeAdaptor();
        TreeWizard wiz = new TreeWizard(adaptor, tokens);
        CommonTree t = (CommonTree)wiz.create("(A B C)");
        TreeIterator it = new TreeIterator(t);
        StringBuffer buf = toString(it);
        String expecting = "A DOWN B C UP EOF";
        String found = buf.toString();
        assertEquals(expecting, found);
    }

    @Test public void testVerticalList() {
        TreeAdaptor adaptor = new CommonTreeAdaptor();
        TreeWizard wiz = new TreeWizard(adaptor, tokens);
        CommonTree t = (CommonTree)wiz.create("(A (B C))");
        TreeIterator it = new TreeIterator(t);
        StringBuffer buf = toString(it);
        String expecting = "A DOWN B DOWN C UP UP EOF";
        String found = buf.toString();
        assertEquals(expecting, found);
    }

    @Test public void testComplex() {
        TreeAdaptor adaptor = new CommonTreeAdaptor();
        TreeWizard wiz = new TreeWizard(adaptor, tokens);
        CommonTree t = (CommonTree)wiz.create("(A (B (C D E) F) G)");
        TreeIterator it = new TreeIterator(t);
        StringBuffer buf = toString(it);
        String expecting = "A DOWN B DOWN C DOWN D E UP F UP G UP EOF";
        String found = buf.toString();
        assertEquals(expecting, found);
    }

    @Test public void testReset() {
        TreeAdaptor adaptor = new CommonTreeAdaptor();
        TreeWizard wiz = new TreeWizard(adaptor, tokens);
        CommonTree t = (CommonTree)wiz.create("(A (B (C D E) F) G)");
        TreeIterator it = new TreeIterator(t);
        StringBuffer buf = toString(it);
        String expecting = "A DOWN B DOWN C DOWN D E UP F UP G UP EOF";
        String found = buf.toString();
        assertEquals(expecting, found);

        it.reset();
        buf = toString(it);
        expecting = "A DOWN B DOWN C DOWN D E UP F UP G UP EOF";
        found = buf.toString();
        assertEquals(expecting, found);
    }

    protected static StringBuffer toString(TreeIterator it) {
        StringBuffer buf = new StringBuffer();
        while ( it.hasNext() ) {
            CommonTree n = (CommonTree)it.next();
            buf.append(n);
            if ( it.hasNext() ) buf.append(" ");
        }
        return buf;
    }
}
