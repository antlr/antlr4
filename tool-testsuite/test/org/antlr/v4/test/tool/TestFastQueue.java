/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.tool;

import org.antlr.runtime.misc.FastQueue;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestFastQueue {
    @Test public void testQueueNoRemove() throws Exception {
        FastQueue<String> q = new FastQueue<String>();
        q.add("a");
        q.add("b");
        q.add("c");
        q.add("d");
        q.add("e");
        String expecting = "a b c d e";
        String found = q.toString();
        assertEquals(expecting, found);
    }

    @Test public void testQueueThenRemoveAll() throws Exception {
        FastQueue<String> q = new FastQueue<String>();
        q.add("a");
        q.add("b");
        q.add("c");
        q.add("d");
        q.add("e");
        StringBuilder buf = new StringBuilder();
        while ( q.size()>0 ) {
            String o = q.remove();
            buf.append(o);
            if ( q.size()>0 ) buf.append(" ");
        }
        assertEquals(0, q.size(), "queue should be empty");
        String expecting = "a b c d e";
        String found = buf.toString();
        assertEquals(expecting, found);
    }

    @Test public void testQueueThenRemoveOneByOne() throws Exception {
        StringBuilder buf = new StringBuilder();
        FastQueue<String> q = new FastQueue<String>();
        q.add("a");
        buf.append(q.remove());
        q.add("b");
        buf.append(q.remove());
        q.add("c");
        buf.append(q.remove());
        q.add("d");
        buf.append(q.remove());
        q.add("e");
        buf.append(q.remove());
        assertEquals(0, q.size(), "queue should be empty");
        String expecting = "abcde";
        String found = buf.toString();
        assertEquals(expecting, found);
    }

    // E r r o r s

    @Test public void testGetFromEmptyQueue() throws Exception {
        FastQueue<String> q = new FastQueue<String>();
        String msg = null;
        try { q.remove(); }
        catch (NoSuchElementException nsee) {
            msg = nsee.getMessage();
        }
        String expecting = "queue index 0 > last index -1";
        String found = msg;
        assertEquals(expecting, found);
    }

    @Test public void testGetFromEmptyQueueAfterSomeAdds() throws Exception {
        FastQueue<String> q = new FastQueue<String>();
        q.add("a");
        q.add("b");
        q.remove();
        q.remove();
        String msg = null;
        try { q.remove(); }
        catch (NoSuchElementException nsee) {
            msg = nsee.getMessage();
        }
        String expecting = "queue index 0 > last index -1";
        String found = msg;
        assertEquals(expecting, found);
    }

    @Test public void testGetFromEmptyQueueAfterClear() throws Exception {
        FastQueue<String> q = new FastQueue<String>();
        q.add("a");
        q.add("b");
        q.clear();
        String msg = null;
        try { q.remove(); }
        catch (NoSuchElementException nsee) {
            msg = nsee.getMessage();
        }
        String expecting = "queue index 0 > last index -1";
        String found = msg;
        assertEquals(expecting, found);
    }
}
