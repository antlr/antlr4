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
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;
using IEnumerable = System.Collections.IEnumerable;
using IEnumerator = System.Collections.IEnumerator;

namespace Antlr4.Runtime.Atn
{
    /// <author>Sam Harwell</author>
    public class ATNConfigSet : IEnumerable<ATNConfig>
    {
        /// <summary>
        /// This maps (state, alt) -&gt; merged
        /// <see cref="ATNConfig"/>
        /// . The key does not account for
        /// the
        /// <see cref="ATNConfig.SemanticContext()"/>
        /// of the value, which is only a problem if a single
        /// <c>ATNConfigSet</c>
        /// contains two configs with the same state and alternative
        /// but different semantic contexts. When this case arises, the first config
        /// added to this map stays, and the remaining configs are placed in
        /// <see cref="unmerged"/>
        /// .
        /// <p/>
        /// This map is only used for optimizing the process of adding configs to the set,
        /// and is
        /// <see langword="null"/>
        /// for read-only sets stored in the DFA.
        /// </summary>
        private readonly Dictionary<long, ATNConfig> mergedConfigs;

        /// <summary>
        /// This is an "overflow" list holding configs which cannot be merged with one
        /// of the configs in
        /// <see cref="mergedConfigs"/>
        /// but have a colliding key. This
        /// occurs when two configs in the set have the same state and alternative but
        /// different semantic contexts.
        /// <p/>
        /// This list is only used for optimizing the process of adding configs to the set,
        /// and is
        /// <see langword="null"/>
        /// for read-only sets stored in the DFA.
        /// </summary>
        private readonly List<ATNConfig> unmerged;

        /// <summary>This is a list of all configs in this set.</summary>
        /// <remarks>This is a list of all configs in this set.</remarks>
        private readonly List<ATNConfig> configs;

        private int uniqueAlt;

        private ConflictInfo conflictInfo;

        private bool hasSemanticContext;

        private bool dipsIntoOuterContext;

        /// <summary>
        /// When
        /// <see langword="true"/>
        /// , this config set represents configurations where the entire
        /// outer context has been consumed by the ATN interpreter. This prevents the
        /// <see cref="ParserATNSimulator.Closure(ATNConfigSet, ATNConfigSet, bool, bool, PredictionContextCache, bool)"/>
        /// from pursuing the global FOLLOW when a
        /// rule stop state is reached with an empty prediction context.
        /// <p/>
        /// Note:
        /// <c>outermostConfigSet</c>
        /// and
        /// <see cref="dipsIntoOuterContext"/>
        /// should never
        /// be true at the same time.
        /// </summary>
        private bool outermostConfigSet;

        private int cachedHashCode = -1;

        public ATNConfigSet()
        {
            // Used in parser and lexer. In lexer, it indicates we hit a pred
            // while computing a closure operation.  Don't make a DFA state from this.
            this.mergedConfigs = new Dictionary<long, ATNConfig>();
            this.unmerged = new List<ATNConfig>();
            this.configs = new List<ATNConfig>();
            this.uniqueAlt = ATN.InvalidAltNumber;
        }

        protected internal ATNConfigSet(Antlr4.Runtime.Atn.ATNConfigSet set, bool @readonly)
        {
            if (@readonly)
            {
                this.mergedConfigs = null;
                this.unmerged = null;
            }
            else
            {
                if (!set.IsReadOnly)
                {
                    this.mergedConfigs = new Dictionary<long, ATNConfig>(set.mergedConfigs);
                    this.unmerged = new List<ATNConfig>(set.unmerged);
                }
                else
                {
                    this.mergedConfigs = new Dictionary<long, ATNConfig>(set.configs.Count);
                    this.unmerged = new List<ATNConfig>();
                }
            }
            this.configs = new List<ATNConfig>(set.configs);
            this.dipsIntoOuterContext = set.dipsIntoOuterContext;
            this.hasSemanticContext = set.hasSemanticContext;
            this.outermostConfigSet = set.outermostConfigSet;
            if (@readonly || !set.IsReadOnly)
            {
                this.uniqueAlt = set.uniqueAlt;
                this.conflictInfo = set.conflictInfo;
            }
        }

        /// <summary>
        /// Get the set of all alternatives represented by configurations in this
        /// set.
        /// </summary>
        /// <remarks>
        /// Get the set of all alternatives represented by configurations in this
        /// set.
        /// </remarks>
        [NotNull]
        public virtual BitSet RepresentedAlternatives
        {
            get
            {
                // if (!readonly && set.isReadOnly()) -> addAll is called from clone()
                if (conflictInfo != null)
                {
                    return (BitSet)conflictInfo.ConflictedAlts.Clone();
                }
                BitSet alts = new BitSet();
                foreach (ATNConfig config in this)
                {
                    alts.Set(config.Alt);
                }
                return alts;
            }
        }

        public bool IsReadOnly
        {
            get
            {
                return mergedConfigs == null;
            }
        }

        public virtual bool IsOutermostConfigSet
        {
            get
            {
                return outermostConfigSet;
            }
            set
            {
                bool outermostConfigSet = value;
                if (this.outermostConfigSet && !outermostConfigSet)
                {
                    throw new InvalidOperationException();
                }
                System.Diagnostics.Debug.Assert(!outermostConfigSet || !dipsIntoOuterContext);
                this.outermostConfigSet = outermostConfigSet;
            }
        }

        public virtual HashSet<ATNState> States
        {
            get
            {
                HashSet<ATNState> states = new HashSet<ATNState>();
                foreach (ATNConfig c in this.configs)
                {
                    states.Add(c.State);
                }
                return states;
            }
        }

        public virtual void OptimizeConfigs(ATNSimulator interpreter)
        {
            if (configs.Count == 0)
            {
                return;
            }
            for (int i = 0; i < configs.Count; i++)
            {
                ATNConfig config = configs[i];
                config.Context = interpreter.atn.GetCachedContext(config.Context);
            }
        }

        public virtual Antlr4.Runtime.Atn.ATNConfigSet Clone(bool @readonly)
        {
            Antlr4.Runtime.Atn.ATNConfigSet copy = new Antlr4.Runtime.Atn.ATNConfigSet(this, @readonly);
            if (!@readonly && this.IsReadOnly)
            {
                copy.AddAll(configs);
            }
            return copy;
        }

        public virtual int Count
        {
            get
            {
                return configs.Count;
            }
        }

        public virtual bool IsEmpty()
        {
            return configs.Count == 0;
        }

        public virtual bool Contains(object o)
        {
            if (!(o is ATNConfig))
            {
                return false;
            }
            ATNConfig config = (ATNConfig)o;
            long configKey = GetKey(config);
            ATNConfig mergedConfig;
            if (mergedConfigs.TryGetValue(configKey, out mergedConfig) && CanMerge(config, configKey, mergedConfig))
            {
                return mergedConfig.Contains(config);
            }
            foreach (ATNConfig c in unmerged)
            {
                if (c.Contains(config))
                {
                    return true;
                }
            }
            return false;
        }

        public virtual IEnumerator<ATNConfig> GetEnumerator()
        {
            return configs.GetEnumerator();
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            return GetEnumerator();
        }

        public virtual object[] ToArray()
        {
            return configs.ToArray();
        }

        public virtual bool Add(ATNConfig e)
        {
            return Add(e, null);
        }

        public virtual bool Add(ATNConfig e, PredictionContextCache contextCache)
        {
            EnsureWritable();
            System.Diagnostics.Debug.Assert(!outermostConfigSet || !e.ReachesIntoOuterContext);
            if (contextCache == null)
            {
                contextCache = PredictionContextCache.Uncached;
            }
            bool addKey;
            long key = GetKey(e);
            ATNConfig mergedConfig;
            addKey = !mergedConfigs.TryGetValue(key, out mergedConfig);
            if (mergedConfig != null && CanMerge(e, key, mergedConfig))
            {
                mergedConfig.OuterContextDepth = Math.Max(mergedConfig.OuterContextDepth, e.OuterContextDepth);
                if (e.PrecedenceFilterSuppressed)
                {
                    mergedConfig.PrecedenceFilterSuppressed = true;
                }
                PredictionContext joined = PredictionContext.Join(mergedConfig.Context, e.Context, contextCache);
                UpdatePropertiesForMergedConfig(e);
                if (mergedConfig.Context == joined)
                {
                    return false;
                }
                mergedConfig.Context = joined;
                return true;
            }
            for (int i = 0; i < unmerged.Count; i++)
            {
                ATNConfig unmergedConfig = unmerged[i];
                if (CanMerge(e, key, unmergedConfig))
                {
                    unmergedConfig.OuterContextDepth = Math.Max(unmergedConfig.OuterContextDepth, e.OuterContextDepth);
                    if (e.PrecedenceFilterSuppressed)
                    {
                        unmergedConfig.PrecedenceFilterSuppressed = true;
                    }
                    PredictionContext joined = PredictionContext.Join(unmergedConfig.Context, e.Context, contextCache);
                    UpdatePropertiesForMergedConfig(e);
                    if (unmergedConfig.Context == joined)
                    {
                        return false;
                    }
                    unmergedConfig.Context = joined;
                    if (addKey)
                    {
                        mergedConfigs[key] = unmergedConfig;
                        unmerged.RemoveAt(i);
                    }
                    return true;
                }
            }
            configs.Add(e);
            if (addKey)
            {
                mergedConfigs[key] = e;
            }
            else
            {
                unmerged.Add(e);
            }
            UpdatePropertiesForAddedConfig(e);
            return true;
        }

        private void UpdatePropertiesForMergedConfig(ATNConfig config)
        {
            // merged configs can't change the alt or semantic context
            dipsIntoOuterContext |= config.ReachesIntoOuterContext;
            System.Diagnostics.Debug.Assert(!outermostConfigSet || !dipsIntoOuterContext);
        }

        private void UpdatePropertiesForAddedConfig(ATNConfig config)
        {
            if (configs.Count == 1)
            {
                uniqueAlt = config.Alt;
            }
            else
            {
                if (uniqueAlt != config.Alt)
                {
                    uniqueAlt = ATN.InvalidAltNumber;
                }
            }
            hasSemanticContext |= !SemanticContext.None.Equals(config.SemanticContext);
            dipsIntoOuterContext |= config.ReachesIntoOuterContext;
            System.Diagnostics.Debug.Assert(!outermostConfigSet || !dipsIntoOuterContext);
        }

        protected internal virtual bool CanMerge(ATNConfig left, long leftKey, ATNConfig right)
        {
            if (left.State.stateNumber != right.State.stateNumber)
            {
                return false;
            }
            if (leftKey != GetKey(right))
            {
                return false;
            }
            return left.SemanticContext.Equals(right.SemanticContext);
        }

        protected internal virtual long GetKey(ATNConfig e)
        {
            long key = e.State.stateNumber;
            key = (key << 12) | (e.Alt & 0xFFFL);
            return key;
        }

        public virtual bool Remove(object o)
        {
            EnsureWritable();
            throw new NotSupportedException("Not supported yet.");
        }

        public virtual bool ContainsAll(IEnumerable<ATNConfig> c)
        {
            foreach (ATNConfig o in c)
            {
                if (!Contains(o))
                {
                    return false;
                }
            }
            return true;
        }

        public virtual bool AddAll(IEnumerable<ATNConfig> c)
        {
            return AddAll(c, null);
        }

        public virtual bool AddAll(IEnumerable<ATNConfig> c, PredictionContextCache contextCache)
        {
            EnsureWritable();
            bool changed = false;
            foreach (ATNConfig group in c)
            {
                changed |= Add(group, contextCache);
            }
            return changed;
        }

        public virtual bool RetainAll<_T0>(ICollection<_T0> c)
        {
            EnsureWritable();
            throw new NotSupportedException("Not supported yet.");
        }

        public virtual bool RemoveAll<_T0>(ICollection<_T0> c)
        {
            EnsureWritable();
            throw new NotSupportedException("Not supported yet.");
        }

        public virtual void Clear()
        {
            EnsureWritable();
            mergedConfigs.Clear();
            unmerged.Clear();
            configs.Clear();
            dipsIntoOuterContext = false;
            hasSemanticContext = false;
            uniqueAlt = ATN.InvalidAltNumber;
            conflictInfo = null;
        }

        public override bool Equals(object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (!(obj is Antlr4.Runtime.Atn.ATNConfigSet))
            {
                return false;
            }
            Antlr4.Runtime.Atn.ATNConfigSet other = (Antlr4.Runtime.Atn.ATNConfigSet)obj;
            return this.outermostConfigSet == other.outermostConfigSet && Utils.Equals(conflictInfo, other.conflictInfo) && configs.SequenceEqual(other.configs);
        }

        public override int GetHashCode()
        {
            if (IsReadOnly && cachedHashCode != -1)
            {
                return cachedHashCode;
            }
            int hashCode = 1;
            hashCode = 5 * hashCode ^ (outermostConfigSet ? 1 : 0);
            hashCode = 5 * hashCode ^ SequenceEqualityComparer<ATNConfig>.Default.GetHashCode(configs);
            if (IsReadOnly)
            {
                cachedHashCode = hashCode;
            }
            return hashCode;
        }

        public override string ToString()
        {
            return ToString(false);
        }

        public virtual string ToString(bool showContext)
        {
            StringBuilder buf = new StringBuilder();
            List<ATNConfig> sortedConfigs = new List<ATNConfig>(configs);
            sortedConfigs.Sort(new _IComparer_475());
            buf.Append("[");
            for (int i = 0; i < sortedConfigs.Count; i++)
            {
                if (i > 0)
                {
                    buf.Append(", ");
                }
                buf.Append(sortedConfigs[i].ToString(null, true, showContext));
            }
            buf.Append("]");
            if (hasSemanticContext)
            {
                buf.Append(",hasSemanticContext=").Append(hasSemanticContext);
            }
            if (uniqueAlt != ATN.InvalidAltNumber)
            {
                buf.Append(",uniqueAlt=").Append(uniqueAlt);
            }
            if (conflictInfo != null)
            {
                buf.Append(",conflictingAlts=").Append(conflictInfo.ConflictedAlts);
                if (!conflictInfo.IsExact)
                {
                    buf.Append("*");
                }
            }
            if (dipsIntoOuterContext)
            {
                buf.Append(",dipsIntoOuterContext");
            }
            return buf.ToString();
        }

        private sealed class _IComparer_475 : IComparer<ATNConfig>
        {
            public _IComparer_475()
            {
            }

            public int Compare(ATNConfig o1, ATNConfig o2)
            {
                if (o1.Alt != o2.Alt)
                {
                    return o1.Alt - o2.Alt;
                }
                else
                {
                    if (o1.State.stateNumber != o2.State.stateNumber)
                    {
                        return o1.State.stateNumber - o2.State.stateNumber;
                    }
                    else
                    {
                        return string.CompareOrdinal(o1.SemanticContext.ToString(), o2.SemanticContext.ToString());
                    }
                }
            }
        }

        public virtual int UniqueAlt
        {
            get
            {
                return uniqueAlt;
            }
        }

        public virtual bool HasSemanticContext
        {
            get
            {
                return hasSemanticContext;
            }
        }

        public virtual void ClearExplicitSemanticContext()
        {
            EnsureWritable();
            hasSemanticContext = false;
        }

        public virtual void MarkExplicitSemanticContext()
        {
            EnsureWritable();
            hasSemanticContext = true;
        }

        public virtual ConflictInfo ConflictInformation
        {
            get
            {
                return conflictInfo;
            }
            set
            {
                ConflictInfo conflictInfo = value;
                EnsureWritable();
                this.conflictInfo = conflictInfo;
            }
        }

        public virtual BitSet ConflictingAlts
        {
            get
            {
                if (conflictInfo == null)
                {
                    return null;
                }
                return conflictInfo.ConflictedAlts;
            }
        }

        public virtual bool IsExactConflict
        {
            get
            {
                if (conflictInfo == null)
                {
                    return false;
                }
                return conflictInfo.IsExact;
            }
        }

        public virtual bool DipsIntoOuterContext
        {
            get
            {
                return dipsIntoOuterContext;
            }
        }

        public virtual ATNConfig this[int index]
        {
            get
            {
                return configs[index];
            }
        }

        public virtual void Remove(int index)
        {
            EnsureWritable();
            ATNConfig config = configs[index];
            configs.Remove(config);
            long key = GetKey(config);
            ATNConfig existing;
            if (mergedConfigs.TryGetValue(key, out existing) && existing == config)
            {
                mergedConfigs.Remove(key);
            }
            else
            {
                for (int i = 0; i < unmerged.Count; i++)
                {
                    if (unmerged[i] == config)
                    {
                        unmerged.RemoveAt(i);
                        return;
                    }
                }
            }
        }

        protected internal void EnsureWritable()
        {
            if (IsReadOnly)
            {
                throw new InvalidOperationException("This ATNConfigSet is read only.");
            }
        }
    }
}
