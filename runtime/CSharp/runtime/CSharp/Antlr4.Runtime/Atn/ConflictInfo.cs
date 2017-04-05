/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    /// <summary>This class stores information about a configuration conflict.</summary>
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
        /// <see cref="PredictionMode.LL_EXACT_AMBIG_DETECTION"/>
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
