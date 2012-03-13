/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Sam Harwell
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
package org.antlr.v4.runtime.atn;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Sam Harwell
 */
public class PredictionContextCache {
    public static final PredictionContextCache UNCACHED = new PredictionContextCache(false);

    private final Map<PredictionContext, PredictionContext> contexts =
        new HashMap<PredictionContext, PredictionContext>();
    private final Map<ObjectAndInt<PredictionContext>, PredictionContext> childContexts =
        new HashMap<ObjectAndInt<PredictionContext>, PredictionContext>();
    private final Map<IdentityCommutativeOperands<PredictionContext>, PredictionContext> joinContexts =
        new HashMap<IdentityCommutativeOperands<PredictionContext>, PredictionContext>();

    private final boolean enableCache;

    public PredictionContextCache() {
        this(true);
    }

    private PredictionContextCache(boolean enableCache) {
        this.enableCache = enableCache;
    }

    public PredictionContext getAsCached(PredictionContext context) {
        if (!enableCache) {
            return context;
        }

        PredictionContext result = contexts.get(context);
        if (result == null) {
            result = context;
            contexts.put(context, context);
        }

        return result;
    }

    public PredictionContext getChild(PredictionContext context, int invokingState) {
        if (!enableCache) {
            return context.getChild(invokingState);
        }

        ObjectAndInt<PredictionContext> operands = new ObjectAndInt<PredictionContext>(context, invokingState);
        PredictionContext result = childContexts.get(operands);
        if (result == null) {
            result = context.getChild(invokingState);
            result = getAsCached(result);
            childContexts.put(operands, result);
        }

        return result;
    }

    public PredictionContext join(PredictionContext x, PredictionContext y) {
        if (!enableCache) {
            return PredictionContext.join(x, y, this);
        }

        IdentityCommutativeOperands<PredictionContext> operands = new IdentityCommutativeOperands<PredictionContext>(x, y);
        PredictionContext result = joinContexts.get(operands);
        if (result != null) {
            return result;
        }

        result = PredictionContext.join(x, y, this);
        result = getAsCached(result);
        joinContexts.put(operands, result);
        return result;
    }

    protected static class ObjectAndInt<T> {
        private final T obj;
        private final int value;

        public ObjectAndInt(T obj, int value) {
            this.obj = obj;
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ObjectAndInt<?>)) {
                return false;
            } else if (obj == this) {
                return true;
            }

            ObjectAndInt<?> other = (ObjectAndInt<?>)obj;
            return this.value == other.value
                && (this.obj == other.obj || (this.obj != null && this.obj.equals(other.obj)));
        }

        @Override
        public int hashCode() {
            int hashCode = 5;
            hashCode = 7 * hashCode + (obj != null ? obj.hashCode() : 0);
            hashCode = 7 * hashCode + value;
            return hashCode;
        }
    }

    protected static class IdentityCommutativeOperands<T> {

        private final T x;
        private final T y;
		private final int xHashCode;
		private final int yHashCode;

        public IdentityCommutativeOperands(T x, T y) {
            this.x = x;
            this.y = y;
			this.xHashCode = System.identityHashCode(x);
			this.yHashCode = System.identityHashCode(y);
        }

        public T getX() {
            return x;
        }

        public T getY() {
            return y;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IdentityCommutativeOperands<?>)) {
                return false;
            }
            else if (this == obj) {
                return true;
            }

            IdentityCommutativeOperands<?> other = (IdentityCommutativeOperands<?>)obj;
            return (this.x == other.x && this.y == other.y) || (this.x == other.y && this.y == other.x);
        }

        @Override
        public int hashCode() {
            return xHashCode ^ yHashCode;
        }
    }

}
