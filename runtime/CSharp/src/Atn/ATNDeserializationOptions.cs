/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using Antlr4.Runtime.Misc;

namespace Antlr4.Runtime.Atn
{
    /// <author>Sam Harwell</author>
    public class ATNDeserializationOptions
    {
        private static readonly ATNDeserializationOptions defaultOptions;

        static ATNDeserializationOptions()
        {
            defaultOptions = new ATNDeserializationOptions();
            defaultOptions.MakeReadOnly();
        }

        private bool readOnly;

        private bool verifyATN;

        private bool generateRuleBypassTransitions;

        private bool optimize;

        public ATNDeserializationOptions()
        {
            verifyATN = true;
            generateRuleBypassTransitions = false;
            optimize = true;
        }

        public ATNDeserializationOptions(ATNDeserializationOptions options)
        {
            verifyATN = options.verifyATN;
            generateRuleBypassTransitions = options.generateRuleBypassTransitions;
            optimize = options.optimize;
        }

        [NotNull]
        public static ATNDeserializationOptions Default => defaultOptions;

        public bool IsReadOnly => readOnly;

        public void MakeReadOnly()
        {
            readOnly = true;
        }

        public bool VerifyAtn
        {
            get => verifyATN;
            set
            {
                bool verifyATN = value;
                ThrowIfReadOnly();
                this.verifyATN = verifyATN;
            }
        }

        public bool GenerateRuleBypassTransitions
        {
            get => generateRuleBypassTransitions;
            set
            {
                bool generateRuleBypassTransitions = value;
                ThrowIfReadOnly();
                this.generateRuleBypassTransitions = generateRuleBypassTransitions;
            }
        }

        public bool Optimize
        {
            get => optimize;
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
