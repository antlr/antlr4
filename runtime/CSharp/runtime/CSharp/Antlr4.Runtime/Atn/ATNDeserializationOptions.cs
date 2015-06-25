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
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    /// <author>Sam Harwell</author>
    public class ATNDeserializationOptions
    {
        private static readonly Antlr4.Runtime.Atn.ATNDeserializationOptions defaultOptions;

        static ATNDeserializationOptions()
        {
            defaultOptions = new Antlr4.Runtime.Atn.ATNDeserializationOptions();
            defaultOptions.MakeReadOnly();
        }

        private bool readOnly;

        private bool verifyATN;

        private bool generateRuleBypassTransitions;

        private bool optimize;

        public ATNDeserializationOptions()
        {
            this.verifyATN = true;
            this.generateRuleBypassTransitions = false;
            this.optimize = true;
        }

        public ATNDeserializationOptions(Antlr4.Runtime.Atn.ATNDeserializationOptions options)
        {
            this.verifyATN = options.verifyATN;
            this.generateRuleBypassTransitions = options.generateRuleBypassTransitions;
            this.optimize = options.optimize;
        }

        [NotNull]
        public static Antlr4.Runtime.Atn.ATNDeserializationOptions Default
        {
            get
            {
                return defaultOptions;
            }
        }

        public bool IsReadOnly
        {
            get
            {
                return readOnly;
            }
        }

        public void MakeReadOnly()
        {
            readOnly = true;
        }

        public bool VerifyAtn
        {
            get
            {
                return verifyATN;
            }
            set
            {
                bool verifyATN = value;
                ThrowIfReadOnly();
                this.verifyATN = verifyATN;
            }
        }

        public bool GenerateRuleBypassTransitions
        {
            get
            {
                return generateRuleBypassTransitions;
            }
            set
            {
                bool generateRuleBypassTransitions = value;
                ThrowIfReadOnly();
                this.generateRuleBypassTransitions = generateRuleBypassTransitions;
            }
        }

        public bool Optimize
        {
            get
            {
                return optimize;
            }
            set
            {
                bool optimize = value;
                ThrowIfReadOnly();
                this.optimize = optimize;
            }
        }

        protected internal virtual void ThrowIfReadOnly()
        {
            if (IsReadOnly)
            {
                throw new InvalidOperationException("The object is read only.");
            }
        }
    }
}
