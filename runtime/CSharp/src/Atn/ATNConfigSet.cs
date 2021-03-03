/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
	public class ATNConfigSet
	{


		/** Indicates that the set of configurations is read-only. Do not
		 *  allow any code to manipulate the set; DFA states will point at
		 *  the sets and they must not change. This does not protect the other
		 *  fields; in particular, conflictingAlts is set after
		 *  we've made this readonly.
		 */
		protected bool readOnly = false;

		/**
		 * All configs but hashed by (s, i, _, pi) not including context. Wiped out
		 * when we go readonly as this set becomes a DFA state.
		 */
		public ConfigHashSet configLookup;

		/** Track the elements as they are added to the set; supports get(i) */
		public ArrayList<ATNConfig> configs = new ArrayList<ATNConfig>(7);

		// TODO: these fields make me pretty uncomfortable but nice to pack up info together, saves recomputation
		// TODO: can we track conflicts as they are added to save scanning configs later?
		public int uniqueAlt;
		/** Currently this is only used when we detect SLL conflict; this does
		 *  not necessarily represent the ambiguous alternatives. In fact,
		 *  I should also point out that this seems to include predicated alternatives
		 *  that have predicates that evaluate to false. Computed in computeTargetState().
		 */
		public BitSet conflictingAlts;

		// Used in parser and lexer. In lexer, it indicates we hit a pred
		// while computing a closure operation.  Don't make a DFA state from this.
		public bool hasSemanticContext;
		public bool dipsIntoOuterContext;

		/** Indicates that this configuration set is part of a full context
		 *  LL prediction. It will be used to determine how to merge $. With SLL
		 *  it's a wildcard whereas it is not for LL context merge.
		 */
		public readonly bool fullCtx;

		private int cachedHashCode = -1;

		public ATNConfigSet(bool fullCtx)
		{
			configLookup = new ConfigHashSet();
			this.fullCtx = fullCtx;
		}

		public ATNConfigSet()
		: this(true)
		{
		}

		public ATNConfigSet(ATNConfigSet old)
			: this(old.fullCtx)
		{
			AddAll(old.configs);
			this.uniqueAlt = old.uniqueAlt;
			this.conflictingAlts = old.conflictingAlts;
			this.hasSemanticContext = old.hasSemanticContext;
			this.dipsIntoOuterContext = old.dipsIntoOuterContext;
		}

		public bool Add(ATNConfig config)
		{
			return Add(config, null);
		}

		/**
		 * Adding a new config means merging contexts with existing configs for
		 * {@code (s, i, pi, _)}, where {@code s} is the
		 * {@link ATNConfig#state}, {@code i} is the {@link ATNConfig#alt}, and
		 * {@code pi} is the {@link ATNConfig#semanticContext}. We use
		 * {@code (s,i,pi)} as key.
		 *
		 * <p>This method updates {@link #dipsIntoOuterContext} and
		 * {@link #hasSemanticContext} when necessary.</p>
		 */
		public bool Add(ATNConfig config, MergeCache mergeCache)
		{
			if (readOnly)
				throw new Exception("This set is readonly");
			if (config.semanticContext != SemanticContext.NONE)
			{
				hasSemanticContext = true;
			}
			if (config.OuterContextDepth > 0)
			{
				dipsIntoOuterContext = true;
			}
			ATNConfig existing = configLookup.GetOrAdd(config);
			if (existing == config)
			{ // we added this new one
				cachedHashCode = -1;
				configs.Add(config);  // track order here
				return true;
			}
			// a previous (s,i,pi,_), merge with it and save result
			bool rootIsWildcard = !fullCtx;
			PredictionContext merged = PredictionContext.Merge(existing.context, config.context, rootIsWildcard, mergeCache);
			 // no need to check for existing.context, config.context in cache
			 // since only way to create new graphs is "call rule" and here. We
			 // cache at both places.
			existing.reachesIntoOuterContext = Math.Max(existing.reachesIntoOuterContext, config.reachesIntoOuterContext);

			// make sure to preserve the precedence filter suppression during the merge
			if (config.IsPrecedenceFilterSuppressed)
			{
				existing.SetPrecedenceFilterSuppressed(true);
			}

			existing.context = merged; // replace context; no need to alt mapping
			return true;
		}

		/** Return a List holding list of configs */
		public List<ATNConfig> Elements
		{
			get
			{
				return configs;
			}
		}

		public HashSet<ATNState> GetStates()
		{
			HashSet<ATNState> states = new HashSet<ATNState>();
			foreach (ATNConfig c in configs)
			{
				states.Add(c.state);
			}
			return states;
		}

		/**
		 * Gets the complete set of represented alternatives for the configuration
		 * set.
		 *
		 * @return the set of represented alternatives in this configuration set
		 *
		 * @since 4.3
		 */

		public BitSet GetAlts()
		{
			BitSet alts = new BitSet();
			foreach (ATNConfig config in configs)
			{
				alts.Set(config.alt);
			}
			return alts;
		}

		public List<SemanticContext> GetPredicates()
		{
			List<SemanticContext> preds = new List<SemanticContext>();
			foreach (ATNConfig c in configs)
			{
				if (c.semanticContext != SemanticContext.NONE)
				{
					preds.Add(c.semanticContext);
				}
			}
			return preds;
		}

		public ATNConfig Get(int i) { return configs[i]; }

		public void OptimizeConfigs(ATNSimulator interpreter)
		{
			if (readOnly)
				throw new Exception("This set is readonly");
			if (configLookup.Count == 0)
				return;

			foreach (ATNConfig config in configs)
			{
				//			int before = PredictionContext.getAllContextNodes(config.context).size();
				config.context = interpreter.getCachedContext(config.context);
				//			int after = PredictionContext.getAllContextNodes(config.context).size();
				//			System.out.println("configs "+before+"->"+after);
			}
		}

		public bool AddAll(ICollection<ATNConfig> coll)
		{
			foreach (ATNConfig c in coll) Add(c);
			return false;
		}

		public override bool Equals(Object o)
		{
			if (o == this)
			{
				return true;
			}
			else if (!(o is ATNConfigSet))
			{
				return false;
			}

			//		System.out.print("equals " + this + ", " + o+" = ");
			ATNConfigSet other = (ATNConfigSet)o;
			bool same = configs != null &&
				configs.Equals(other.configs) &&  // includes stack context
				this.fullCtx == other.fullCtx &&
				this.uniqueAlt == other.uniqueAlt &&
				this.conflictingAlts == other.conflictingAlts &&
				this.hasSemanticContext == other.hasSemanticContext &&
				this.dipsIntoOuterContext == other.dipsIntoOuterContext;

			//		System.out.println(same);
			return same;
		}


		public override int GetHashCode()
		{
			if (IsReadOnly)
			{
				if (cachedHashCode == -1)
				{
					cachedHashCode = configs.GetHashCode();
				}

				return cachedHashCode;
			}

			return configs.GetHashCode();
		}

		public int Count
		{
			get
			{
				return configs.Count;
			}
		}

		public bool Empty
		{
			get
			{

				return configs.Count == 0;
			}
		}

		public bool Contains(Object o)
		{
			if (configLookup == null)
			{
				throw new Exception("This method is not implemented for readonly sets.");
			}

			return configLookup.ContainsKey((ATNConfig)o);
		}


		public void Clear()
		{
			if (readOnly)
				throw new Exception("This set is readonly");
			configs.Clear();
			cachedHashCode = -1;
			configLookup.Clear();
		}

		public bool IsReadOnly
		{
			get
			{
				return readOnly;
			}
			set
			{
				this.readOnly = value;
				configLookup = null; // can't mod, no need for lookup cache
			}
		}

		public override String ToString()
		{
			StringBuilder buf = new StringBuilder();
			buf.Append('[');
			List<ATNConfig> cfgs = Elements;
			if (cfgs.Count > 0)
			{
				foreach (ATNConfig c in cfgs)
				{
					buf.Append(c.ToString());
					buf.Append(", ");
				}
				buf.Length = buf.Length - 2;
			}
			buf.Append(']');
			if (hasSemanticContext)
				buf.Append(",hasSemanticContext=")
				   .Append(hasSemanticContext);
			if (uniqueAlt != ATN.INVALID_ALT_NUMBER)
				buf.Append(",uniqueAlt=")
				   .Append(uniqueAlt);
			if (conflictingAlts != null)
				buf.Append(",conflictingAlts=")
				   .Append(conflictingAlts);
			if (dipsIntoOuterContext)
				buf.Append(",dipsIntoOuterContext");
			return buf.ToString();
		}


	}

	public class OrderedATNConfigSet : ATNConfigSet
	{

		public OrderedATNConfigSet()
		{
			this.configLookup = new LexerConfigHashSet();
		}

		public class LexerConfigHashSet : ConfigHashSet
		{
			public LexerConfigHashSet()
				: base(new ObjectEqualityComparator())
			{
			}
		}
	}

	public class ObjectEqualityComparator : IEqualityComparer<ATNConfig>
	{


		public int GetHashCode(ATNConfig o)
		{
			if (o == null)
				return 0;
			else
				return o.GetHashCode();
		}

		public bool Equals(ATNConfig a, ATNConfig b)
		{
			if (a == b) return true;
			if (a == null || b == null) return false;
			return a.Equals(b);
		}
	}

	/**
	* The reason that we need this is because we don't want the hash map to use
	* the standard hash code and equals. We need all configurations with the same
	* {@code (s,i,_,semctx)} to be equal. Unfortunately, this key effectively doubles
	* the number of objects associated with ATNConfigs. The other solution is to
	* use a hash table that lets us specify the equals/hashcode operation.
	*/
	public class ConfigHashSet : Dictionary<ATNConfig, ATNConfig>
	{
		public ConfigHashSet(IEqualityComparer<ATNConfig> comparer)
			: base(comparer)
		{
		}


		public ConfigHashSet()
			: base(new ConfigEqualityComparator())
		{
		}

		public ATNConfig GetOrAdd(ATNConfig config)
		{
			ATNConfig existing;
			if (this.TryGetValue(config, out existing))
				return existing;
			else
			{
				this.Put(config, config);
				return config;
			}
		}

	}

	public class ConfigEqualityComparator : IEqualityComparer<ATNConfig>
	{


		public int GetHashCode(ATNConfig o)
		{
			int hashCode = 7;
			hashCode = 31 * hashCode + o.state.stateNumber;
			hashCode = 31 * hashCode + o.alt;
			hashCode = 31 * hashCode + o.semanticContext.GetHashCode();
			return hashCode;
		}

		public bool Equals(ATNConfig a, ATNConfig b)
		{
			if (a == b) return true;
			if (a == null || b == null) return false;
			return a.state.stateNumber == b.state.stateNumber
				&& a.alt == b.alt
				&& a.semanticContext.Equals(b.semanticContext);
		}
	}

}
