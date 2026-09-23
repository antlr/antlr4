/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.dfa;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

/**
 * Uses {@code sun.misc.Unsafe} (Java 8) for volatile array element access,
 * accessed entirely through reflection and {@code MethodHandle} so this class
 * compiles with {@code --release 8}.
 */
final class UnsafeEdgeElementAccessor implements EdgeElementAccessor {

	private static final long BASE;
	private static final int SHIFT;
	private static final MethodHandle GET_OBJECT_VOLATILE;
	private static final MethodHandle PUT_ORDERED_OBJECT;

	static {
		try {
			Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
			Field f = unsafeClass.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			Object unsafe = f.get(null);

			MethodHandles.Lookup lookup = MethodHandles.lookup();

			MethodHandle arrayBaseOffset = lookup.findVirtual(unsafeClass, "arrayBaseOffset",
				MethodType.methodType(int.class, Class.class))
				.bindTo(unsafe);
			MethodHandle arrayIndexScale = lookup.findVirtual(unsafeClass, "arrayIndexScale",
				MethodType.methodType(int.class, Class.class))
				.bindTo(unsafe);

			BASE = (int) arrayBaseOffset.invoke(DFAState[].class);
			int scale = (int) arrayIndexScale.invoke(DFAState[].class);
			if ((scale & (scale - 1)) != 0) {
				throw new Error("DFAState[] array index scale is not a power of two");
			}
			SHIFT = 31 - Integer.numberOfLeadingZeros(scale);

			GET_OBJECT_VOLATILE = lookup.findVirtual(unsafeClass, "getObjectVolatile",
				MethodType.methodType(Object.class, Object.class, long.class))
				.bindTo(unsafe);

			PUT_ORDERED_OBJECT = lookup.findVirtual(unsafeClass, "putOrderedObject",
				MethodType.methodType(void.class, Object.class, long.class, Object.class))
				.bindTo(unsafe);
		} catch (Throwable e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private static long byteOffset(int index) {
		return ((long) index << SHIFT) + BASE;
	}

	@Override
	public DFAState getAcquire(DFAState[] edges, int index) {
		try {
			return (DFAState) GET_OBJECT_VOLATILE.invoke(edges, byteOffset(index));
		} catch (Throwable t) {
			throw new Error(t);
		}
	}

	@Override
	public void setRelease(DFAState[] edges, int index, DFAState value) {
		try {
			PUT_ORDERED_OBJECT.invoke(edges, byteOffset(index), value);
		} catch (Throwable t) {
			throw new Error(t);
		}
	}
}