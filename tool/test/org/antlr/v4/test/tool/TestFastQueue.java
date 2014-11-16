/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
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
package org.antlr.v4.test.tool;

import org.antlr.runtime.misc.FastQueue;
import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;

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
        assertEquals("queue should be empty", 0, q.size());
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
        assertEquals("queue should be empty", 0, q.size());
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
