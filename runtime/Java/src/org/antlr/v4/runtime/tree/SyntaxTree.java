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

package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;

/** A tree that knows about an interval in a token stream
 *  is some kind of syntax tree. Subinterfaces distinguish
 *  between parse trees and other kinds of syntax trees we might want to create.
 */
public interface SyntaxTree extends Tree {
	/**
	 * Return an {@link Interval} indicating the index in the
	 * {@link TokenStream} of the first and last token associated with this
	 * subtree. If this node is a leaf, then the interval represents a single
	 * token and has interval i..i for token index i.
	 *
	 * <p>An interval of i..i-1 indicates an empty interval at position
	 * i in the input stream, where 0 &lt;= i &lt;= the size of the input
	 * token stream.  Currently, the code base can only have i=0..n-1 but
	 * in concept one could have an empty interval after EOF. </p>
	 *
	 * <p>If source interval is unknown, this returns {@link Interval#INVALID}.</p>
	 *
	 * <p>As a weird special case, the source interval for rules matched after
	 * EOF is unspecified.</p>
	 */
	Interval getSourceInterval();
}
