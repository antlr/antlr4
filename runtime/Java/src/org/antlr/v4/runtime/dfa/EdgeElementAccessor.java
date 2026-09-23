/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.dfa;

/**
 * Abstracts acquire/release access to elements of a {@link DFAState} array.
 *
 * <p>On Java 9+, the implementation uses {@code VarHandle} for
 * acquire/release semantics. On Java 8, it falls back to
 * {@code sun.misc.Unsafe}.</p>
 */
public interface EdgeElementAccessor {

	DFAState getAcquire(DFAState[] edges, int index);

	void setRelease(DFAState[] edges, int index, DFAState value);

	static EdgeElementAccessor create() {
		try {
			Class.forName("java.lang.invoke.VarHandle");
			return new VarHandleEdgeElementAccessor();
		} catch (Throwable e) {
			return new UnsafeEdgeElementAccessor();
		}
	}
}