/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.dfa;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

/**
 * Uses {@code VarHandle} (Java 9+) for acquire/release array element access,
 * accessed entirely through reflection and {@code MethodHandle} so this class
 * compiles with {@code --release 8}.
 */
final class VarHandleEdgeElementAccessor implements EdgeElementAccessor {

	private final MethodHandle getAcquire;
	private final MethodHandle setRelease;

	VarHandleEdgeElementAccessor() throws Throwable {
		Method arrayElementVarHandle = MethodHandles.class
			.getMethod("arrayElementVarHandle", Class.class);
		Object varHandle = arrayElementVarHandle.invoke(null, DFAState[].class);

		Class<?> varHandleClass = Class.forName("java.lang.invoke.VarHandle");
		MethodHandles.Lookup lookup = MethodHandles.lookup();

		getAcquire = lookup.findVirtual(varHandleClass, "getAcquire",
			MethodType.methodType(Object.class, DFAState[].class, int.class))
			.bindTo(varHandle);

		setRelease = lookup.findVirtual(varHandleClass, "setRelease",
			MethodType.methodType(void.class, DFAState[].class, int.class, DFAState.class))
			.bindTo(varHandle);
	}

	@Override
	public DFAState getAcquire(DFAState[] edges, int index) {
		try {
			return (DFAState) this.getAcquire.invoke(edges, index);
		} catch (Throwable t) {
			throw new Error(t);
		}
	}

	@Override
	public void setRelease(DFAState[] edges, int index, DFAState value) {
		try {
			this.setRelease.invoke(edges, index, value);
		} catch (Throwable t) {
			throw new Error(t);
		}
	}
}