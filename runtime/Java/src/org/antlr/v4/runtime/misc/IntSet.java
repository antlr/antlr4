/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.antlr.v4.runtime.misc;


import java.util.List;

/** A generic set of ints.
 *
 *  @see IntervalSet
 */
public interface IntSet {
    /** Add an element to the set */
    void add(int el);

    /** Add all elements from incoming set to this set.  Can limit
     *  to set of its own type. Return "this" so we can chain calls.
     */
    IntSet addAll(IntSet set);

    /** Return the intersection of this set with the argument, creating
     *  a new set.
     */
    IntSet and(IntSet a);

    IntSet complement(IntSet elements);

    IntSet or(IntSet a);

    IntSet subtract(IntSet a);

    /** Return the size of this set (not the underlying implementation's
     *  allocated memory size, for example).
     */
    int size();

    boolean isNil();

    @Override
    boolean equals(Object obj);

    int getSingleElement();

    boolean contains(int el);

    /** remove this element from this set */
    void remove(int el);

    List<Integer> toList();

    @Override
    String toString();
}
