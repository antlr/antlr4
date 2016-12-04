/* Copyright (c) 2012 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    /// <author>Sam Harwell</author>
    public class OrderedATNConfigSet : ATNConfigSet
    {
        public OrderedATNConfigSet()
        {
        }

        public OrderedATNConfigSet(ATNConfigSet set, bool @readonly)
            : base(set, @readonly)
        {
        }

        public override ATNConfigSet Clone(bool @readonly)
        {
            Antlr4.Runtime.Atn.OrderedATNConfigSet copy = new Antlr4.Runtime.Atn.OrderedATNConfigSet(this, @readonly);
            if (!@readonly && this.IsReadOnly)
            {
                copy.AddAll(this);
            }
            return copy;
        }

        protected internal override long GetKey(ATNConfig e)
        {
            return e.GetHashCode();
        }

        protected internal override bool CanMerge(ATNConfig left, long leftKey, ATNConfig right)
        {
            return left.Equals(right);
        }
    }
}
