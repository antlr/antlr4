/* Copyright (c) 2012 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System.Collections.Generic;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    /// <summary>
    /// Used to cache
    /// <see cref="PredictionContext"/>
    /// objects. Its used for the shared
    /// context cash associated with contexts in DFA states. This cache
    /// can be used for both lexers and parsers.
    /// </summary>
    /// <author>Sam Harwell</author>
    public class PredictionContextCache
    {
        public static readonly Antlr4.Runtime.Atn.PredictionContextCache Uncached = new Antlr4.Runtime.Atn.PredictionContextCache(false);

        private readonly IDictionary<PredictionContext, PredictionContext> contexts = new Dictionary<PredictionContext, PredictionContext>();

        private readonly IDictionary<PredictionContextCache.PredictionContextAndInt, PredictionContext> childContexts = new Dictionary<PredictionContextCache.PredictionContextAndInt, PredictionContext>();

        private readonly IDictionary<PredictionContextCache.IdentityCommutativePredictionContextOperands, PredictionContext> joinContexts = new Dictionary<PredictionContextCache.IdentityCommutativePredictionContextOperands, PredictionContext>();

        private readonly bool enableCache;

        public PredictionContextCache()
            : this(true)
        {
        }

        private PredictionContextCache(bool enableCache)
        {
            this.enableCache = enableCache;
        }

        public virtual PredictionContext GetAsCached(PredictionContext context)
        {
            if (!enableCache)
            {
                return context;
            }
            PredictionContext result;
            if (!contexts.TryGetValue(context, out result))
            {
                result = context;
                contexts[context] = context;
            }
            return result;
        }

        public virtual PredictionContext GetChild(PredictionContext context, int invokingState)
        {
            if (!enableCache)
            {
                return context.GetChild(invokingState);
            }
            PredictionContextCache.PredictionContextAndInt operands = new PredictionContextCache.PredictionContextAndInt(context, invokingState);
            PredictionContext result;
            if (!childContexts.TryGetValue(operands, out result))
            {
                result = context.GetChild(invokingState);
                result = GetAsCached(result);
                childContexts[operands] = result;
            }
            return result;
        }

        public virtual PredictionContext Join(PredictionContext x, PredictionContext y)
        {
            if (!enableCache)
            {
                return PredictionContext.Join(x, y, this);
            }
            PredictionContextCache.IdentityCommutativePredictionContextOperands operands = new PredictionContextCache.IdentityCommutativePredictionContextOperands(x, y);
            PredictionContext result;
            if (joinContexts.TryGetValue(operands, out result))
            {
                return result;
            }
            result = PredictionContext.Join(x, y, this);
            result = GetAsCached(result);
            joinContexts[operands] = result;
            return result;
        }

        protected internal sealed class PredictionContextAndInt
        {
            private readonly PredictionContext obj;

            private readonly int value;

            public PredictionContextAndInt(PredictionContext obj, int value)
            {
                this.obj = obj;
                this.value = value;
            }

            public override bool Equals(object obj)
            {
                if (!(obj is PredictionContextCache.PredictionContextAndInt))
                {
                    return false;
                }
                else
                {
                    if (obj == this)
                    {
                        return true;
                    }
                }
                PredictionContextCache.PredictionContextAndInt other = (PredictionContextCache.PredictionContextAndInt)obj;
                return this.value == other.value && (this.obj == other.obj || (this.obj != null && this.obj.Equals(other.obj)));
            }

            public override int GetHashCode()
            {
                int hashCode = 5;
                hashCode = 7 * hashCode + (obj != null ? obj.GetHashCode() : 0);
                hashCode = 7 * hashCode + value;
                return hashCode;
            }
        }

        protected internal sealed class IdentityCommutativePredictionContextOperands
        {
            private readonly PredictionContext x;

            private readonly PredictionContext y;

            public IdentityCommutativePredictionContextOperands(PredictionContext x, PredictionContext y)
            {
                this.x = x;
                this.y = y;
            }

            public PredictionContext X
            {
                get
                {
                    return x;
                }
            }

            public PredictionContext Y
            {
                get
                {
                    return y;
                }
            }

            public override bool Equals(object obj)
            {
                if (!(obj is PredictionContextCache.IdentityCommutativePredictionContextOperands))
                {
                    return false;
                }
                else
                {
                    if (this == obj)
                    {
                        return true;
                    }
                }
                PredictionContextCache.IdentityCommutativePredictionContextOperands other = (PredictionContextCache.IdentityCommutativePredictionContextOperands)obj;
                return (this.x == other.x && this.y == other.y) || (this.x == other.y && this.y == other.x);
            }

            public override int GetHashCode()
            {
                return x.GetHashCode() ^ y.GetHashCode();
            }
        }
    }
}
