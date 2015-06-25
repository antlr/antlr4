/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
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
using System;
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
#pragma warning disable 0659 // 'class' overrides Object.Equals(object o) but does not override Object.GetHashCode()
    public sealed class EmptyPredictionContext : PredictionContext
    {
        public static readonly Antlr4.Runtime.Atn.EmptyPredictionContext LocalContext = new Antlr4.Runtime.Atn.EmptyPredictionContext(false);

        public static readonly Antlr4.Runtime.Atn.EmptyPredictionContext FullContext = new Antlr4.Runtime.Atn.EmptyPredictionContext(true);

        private readonly bool fullContext;

        private EmptyPredictionContext(bool fullContext)
            : base(CalculateEmptyHashCode())
        {
            this.fullContext = fullContext;
        }

        public bool IsFullContext
        {
            get
            {
                return fullContext;
            }
        }

        protected internal override PredictionContext AddEmptyContext()
        {
            return this;
        }

        protected internal override PredictionContext RemoveEmptyContext()
        {
            throw new NotSupportedException("Cannot remove the empty context from itself.");
        }

        public override PredictionContext GetParent(int index)
        {
            throw new ArgumentOutOfRangeException();
        }

        public override int GetReturnState(int index)
        {
            throw new ArgumentOutOfRangeException();
        }

        public override int FindReturnState(int returnState)
        {
            return -1;
        }

        public override int Size
        {
            get
            {
                return 0;
            }
        }

        public override PredictionContext AppendContext(int returnContext, PredictionContextCache contextCache)
        {
            return contextCache.GetChild(this, returnContext);
        }

        public override PredictionContext AppendContext(PredictionContext suffix, PredictionContextCache contextCache)
        {
            return suffix;
        }

        public override bool IsEmpty
        {
            get
            {
                return true;
            }
        }

        public override bool HasEmpty
        {
            get
            {
                return true;
            }
        }

        public override bool Equals(object o)
        {
            return this == o;
        }

        public override string[] ToStrings(IRecognizer recognizer, int currentState)
        {
            return new string[] { "[]" };
        }

        public override string[] ToStrings(IRecognizer recognizer, PredictionContext stop, int currentState)
        {
            return new string[] { "[]" };
        }
    }
}
