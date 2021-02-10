/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
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
