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
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    /// <summary>This class stores information about a configuration conflict.</summary>
    /// <remarks>This class stores information about a configuration conflict.</remarks>
    /// <author>Sam Harwell</author>
    public class ConflictInfo
    {
        private readonly BitSet conflictedAlts;

        private readonly bool exact;

        public ConflictInfo(BitSet conflictedAlts, bool exact)
        {
            this.conflictedAlts = conflictedAlts;
            this.exact = exact;
        }

        /// <summary>Gets the set of conflicting alternatives for the configuration set.</summary>
        /// <remarks>Gets the set of conflicting alternatives for the configuration set.</remarks>
        public BitSet ConflictedAlts
        {
            get
            {
                return conflictedAlts;
            }
        }

        /// <summary>Gets whether or not the configuration conflict is an exact conflict.</summary>
        /// <remarks>
        /// Gets whether or not the configuration conflict is an exact conflict.
        /// An exact conflict occurs when the prediction algorithm determines that
        /// the represented alternatives for a particular configuration set cannot be
        /// further reduced by consuming additional input. After reaching an exact
        /// conflict during an SLL prediction, only switch to full-context prediction
        /// could reduce the set of viable alternatives. In LL prediction, an exact
        /// conflict indicates a true ambiguity in the input.
        /// <p>
        /// For the
        /// <see cref="PredictionMode.LlExactAmbigDetection"/>
        /// prediction mode,
        /// accept states are conflicting but not exact are treated as non-accept
        /// states.</p>
        /// </remarks>
        public bool IsExact
        {
            get
            {
                return exact;
            }
        }

        public override bool Equals(object obj)
        {
            if (obj == this)
            {
                return true;
            }
            else
            {
                if (!(obj is Antlr4.Runtime.Atn.ConflictInfo))
                {
                    return false;
                }
            }
            Antlr4.Runtime.Atn.ConflictInfo other = (Antlr4.Runtime.Atn.ConflictInfo)obj;
            return IsExact == other.IsExact && Utils.Equals(ConflictedAlts, other.ConflictedAlts);
        }

        public override int GetHashCode()
        {
            return ConflictedAlts.GetHashCode();
        }
    }
}
