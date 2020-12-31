/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System.Collections.Generic;
using System.Text;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
	public abstract class PredictionContext
	{
		public static readonly int EMPTY_RETURN_STATE = int.MaxValue;

		public static readonly EmptyPredictionContext EMPTY = new EmptyPredictionContext();

		private static readonly int INITIAL_HASH = 1;

		protected internal static int CalculateEmptyHashCode()
		{
			int hash = MurmurHash.Initialize(INITIAL_HASH);
			hash = MurmurHash.Finish(hash, 0);
			return hash;
		}

		protected internal static int CalculateHashCode(PredictionContext parent, int returnState)
		{
			int hash = MurmurHash.Initialize(INITIAL_HASH);
			hash = MurmurHash.Update(hash, parent);
			hash = MurmurHash.Update(hash, returnState);
			hash = MurmurHash.Finish(hash, 2);
			return hash;
		}

		protected internal static int CalculateHashCode(PredictionContext[] parents, int[] returnStates)
		{
			int hash = MurmurHash.Initialize(INITIAL_HASH);
			foreach (PredictionContext parent in parents)
			{
				hash = MurmurHash.Update(hash, parent);
			}
			foreach (int returnState in returnStates)
			{
				hash = MurmurHash.Update(hash, returnState);
			}
			hash = MurmurHash.Finish(hash, 2 * parents.Length);
			return hash;
		}

		private readonly int cachedHashCode;

		protected internal PredictionContext(int cachedHashCode)
		{
			this.cachedHashCode = cachedHashCode;
		}

		public static PredictionContext FromRuleContext(ATN atn, RuleContext outerContext)
		{
			if (outerContext == null)
				outerContext = ParserRuleContext.EMPTY;
			if (outerContext.Parent == null || outerContext == ParserRuleContext.EMPTY)
				return PredictionContext.EMPTY;
			PredictionContext parent = PredictionContext.FromRuleContext(atn, outerContext.Parent);
			ATNState state = atn.states[outerContext.invokingState];
			RuleTransition transition = (RuleTransition)state.Transition(0);
			return parent.GetChild(transition.followState.stateNumber);
		}

		public abstract int Size
		{
			get;
		}

		public abstract PredictionContext GetParent(int index);

		public abstract int GetReturnState(int index);

		public virtual bool IsEmpty
		{
			get
			{
				return this == EMPTY;
			}
		}

		public virtual bool HasEmptyPath
		{
			get
			{
				return GetReturnState(Size - 1) == EMPTY_RETURN_STATE;
			}
		}

		public sealed override int GetHashCode()
		{
			return cachedHashCode;
		}



		internal static PredictionContext Merge(PredictionContext a, PredictionContext b, bool rootIsWildcard, MergeCache mergeCache)
		{
			if (a == b || a.Equals(b))
			{
				return a;
			}
			if (a is SingletonPredictionContext && b is SingletonPredictionContext)
			{
				return MergeSingletons((SingletonPredictionContext)a,
									   (SingletonPredictionContext)b,
									   rootIsWildcard, mergeCache);
			}

			// At least one of a or b is array
			// If one is $ and rootIsWildcard, return $ as * wildcard
			if (rootIsWildcard)
			{
				if (a is EmptyPredictionContext)
					return a;
				if (b is EmptyPredictionContext)
					return b;
			}

			// convert singleton so both are arrays to normalize
			if (a is SingletonPredictionContext)
			{
				a = new ArrayPredictionContext((SingletonPredictionContext)a);
			}
			if (b is SingletonPredictionContext)
			{
				b = new ArrayPredictionContext((SingletonPredictionContext)b);
			}
			return MergeArrays((ArrayPredictionContext)a, (ArrayPredictionContext)b,
							   rootIsWildcard, mergeCache);
		}

		public static PredictionContext MergeSingletons(
	SingletonPredictionContext a,
	SingletonPredictionContext b,
	bool rootIsWildcard,
	MergeCache mergeCache)
		{
			if (mergeCache != null)
			{
				PredictionContext previous = mergeCache.Get(a, b);
				if (previous != null) return previous;
				previous = mergeCache.Get(b, a);
				if (previous != null) return previous;
			}

			PredictionContext rootMerge = MergeRoot(a, b, rootIsWildcard);
			if (rootMerge != null)
			{
				if (mergeCache != null) mergeCache.Put(a, b, rootMerge);
				return rootMerge;
			}

			if (a.returnState == b.returnState)
			{ // a == b
				PredictionContext parent = Merge(a.parent, b.parent, rootIsWildcard, mergeCache);
				// if parent is same as existing a or b parent or reduced to a parent, return it
				if (parent == a.parent) return a; // ax + bx = ax, if a=b
				if (parent == b.parent) return b; // ax + bx = bx, if a=b
												  // else: ax + ay = a'[x,y]
												  // merge parents x and y, giving array node with x,y then remainders
												  // of those graphs.  dup a, a' points at merged array
												  // new joined parent so create new singleton pointing to it, a'
				PredictionContext a_ = SingletonPredictionContext.Create(parent, a.returnState);
				if (mergeCache != null) mergeCache.Put(a, b, a_);
				return a_;
			}
			else { // a != b payloads differ
				   // see if we can collapse parents due to $+x parents if local ctx
				int[] payloads = new int[2];
				PredictionContext[] parents = new PredictionContext[2];
				PredictionContext pc;
				PredictionContext singleParent = null;
				if (a == b || (a.parent != null && a.parent.Equals(b.parent)))
				{ // ax + bx = [a,b]x
					singleParent = a.parent;
				}
				if (singleParent != null)
				{   // parents are same
					// sort payloads and use same parent
					if (a.returnState > b.returnState)
					{
						payloads[0] = b.returnState;
						payloads[1] = a.returnState;
					}
					else {
						payloads[0] = a.returnState;
						payloads[1] = b.returnState;
					}
					parents[0] = singleParent;
					parents[1] = singleParent;
					pc = new ArrayPredictionContext(parents, payloads);
					if (mergeCache != null)
						mergeCache.Put(a, b, pc);
					return pc;
				}
				// parents differ and can't merge them. Just pack together
				// into array; can't merge.
				// ax + by = [ax,by]
				// sort by payload
				if (a.returnState > b.returnState)
				{
					payloads[0] = b.returnState;
					payloads[1] = a.returnState;
					parents[0] = b.parent;
					parents[1] = a.parent;
				}
				else {
					payloads[0] = a.returnState;
					payloads[1] = b.returnState;
					parents[0] = a.parent;
					parents[1] = b.parent;
				}
				pc = new ArrayPredictionContext(parents, payloads);
				if (mergeCache != null)
					mergeCache.Put(a, b, pc);
				return pc;
			}
		}

		public static PredictionContext MergeArrays(
	ArrayPredictionContext a,
	ArrayPredictionContext b,
	bool rootIsWildcard,
	MergeCache mergeCache)
		{
			if (mergeCache != null)
			{
				PredictionContext previous = mergeCache.Get(a, b);
				if (previous != null)
					return previous;
				previous = mergeCache.Get(b, a);
				if (previous != null)
					return previous;
			}

			// merge sorted payloads a + b => M
			int i = 0; // walks a
			int j = 0; // walks b
			int k = 0; // walks target M array

			int[] mergedReturnStates =
				new int[a.returnStates.Length + b.returnStates.Length];
			PredictionContext[] mergedParents =
				new PredictionContext[a.returnStates.Length + b.returnStates.Length];
			// walk and merge to yield mergedParents, mergedReturnStates
			while (i < a.returnStates.Length && j < b.returnStates.Length)
			{
				PredictionContext a_parent = a.parents[i];
				PredictionContext b_parent = b.parents[j];
				if (a.returnStates[i] == b.returnStates[j])
				{
					// same payload (stack tops are equal), must yield merged singleton
					int payload = a.returnStates[i];
					// $+$ = $
					bool both_dollar = payload == EMPTY_RETURN_STATE &&
									a_parent == null && b_parent == null;
					bool ax_ax = (a_parent != null && b_parent != null) &&
									a_parent.Equals(b_parent); // ax+ax -> ax
					if (both_dollar || ax_ax ) {
						mergedParents[k] = a_parent; // choose left
						mergedReturnStates[k] = payload;
					}
				else { // ax+ay -> a'[x,y]
						PredictionContext mergedParent =
							Merge(a_parent, b_parent, rootIsWildcard, mergeCache);
						mergedParents[k] = mergedParent;
						mergedReturnStates[k] = payload;
					}
					i++; // hop over left one as usual
					j++; // but also skip one in right side since we merge
				}
				else if (a.returnStates[i] < b.returnStates[j])
				{ // copy a[i] to M
					mergedParents[k] = a_parent;
					mergedReturnStates[k] = a.returnStates[i];
					i++;
				}
				else { // b > a, copy b[j] to M
					mergedParents[k] = b_parent;
					mergedReturnStates[k] = b.returnStates[j];
					j++;
				}
				k++;
			}

			// copy over any payloads remaining in either array
			if (i < a.returnStates.Length)
			{
				for (int p = i; p < a.returnStates.Length; p++)
				{
					mergedParents[k] = a.parents[p];
					mergedReturnStates[k] = a.returnStates[p];
					k++;
				}
			}
			else {
				for (int p = j; p < b.returnStates.Length; p++)
				{
					mergedParents[k] = b.parents[p];
					mergedReturnStates[k] = b.returnStates[p];
					k++;
				}
			}

			// trim merged if we combined a few that had same stack tops
			if (k < mergedParents.Length)
			{ // write index < last position; trim
				if (k == 1)
				{ // for just one merged element, return singleton top
					PredictionContext a_ = SingletonPredictionContext.Create(mergedParents[0], mergedReturnStates[0]);
					if (mergeCache != null) mergeCache.Put(a, b, a_);
					return a_;
				}
				mergedParents = Arrays.CopyOf(mergedParents, k);
				mergedReturnStates = Arrays.CopyOf(mergedReturnStates, k);
			}

			PredictionContext M = new ArrayPredictionContext(mergedParents, mergedReturnStates);

			// if we created same array as a or b, return that instead
			// TODO: track whether this is possible above during merge sort for speed
			if (M.Equals(a))
			{
				if (mergeCache != null)
					mergeCache.Put(a, b, a);
				return a;
			}
			if (M.Equals(b))
			{
				if (mergeCache != null)
					mergeCache.Put(a, b, b);
				return b;
			}

			CombineCommonParents(mergedParents);

			if (mergeCache != null)
				mergeCache.Put(a, b, M);
			return M;
		}

		protected static void CombineCommonParents(PredictionContext[] parents)
		{
			Dictionary<PredictionContext, PredictionContext> uniqueParents = new Dictionary<PredictionContext, PredictionContext>();

			for (int p = 0; p < parents.Length; p++)
			{
				PredictionContext parent = parents[p];
				if (parent!=null && !uniqueParents.ContainsKey(parent))
				{ // don't replace
					uniqueParents.Put(parent, parent);
				}
			}

			for (int p = 0; p < parents.Length; p++)
			{
				PredictionContext parent = parents[p];
				if (parent!=null)
					parents[p] = uniqueParents.Get(parent);
			}
		}

		public static PredictionContext MergeRoot(SingletonPredictionContext a,
											  SingletonPredictionContext b,
											  bool rootIsWildcard)
		{
			if (rootIsWildcard)
			{
				if (a == PredictionContext.EMPTY)
					return PredictionContext.EMPTY;  // * + b = *
				if (b == PredictionContext.EMPTY)
					return PredictionContext.EMPTY;  // a + * = *
			}
			else {
				if (a == EMPTY && b == EMPTY) return EMPTY; // $ + $ = $
				if (a == EMPTY)
				{ // $ + x = [$,x]
					int[] payloads = { b.returnState, EMPTY_RETURN_STATE };
					PredictionContext[] parents = { b.parent, null };
					PredictionContext joined =
						new ArrayPredictionContext(parents, payloads);
					return joined;
				}
				if (b == EMPTY)
				{ // x + $ = [$,x] ($ is always first if present)
					int[] payloads = { a.returnState, EMPTY_RETURN_STATE };
					PredictionContext[] parents = { a.parent, null };
					PredictionContext joined =
						new ArrayPredictionContext(parents, payloads);
					return joined;
				}
			}
			return null;
		}


		public static PredictionContext GetCachedContext(PredictionContext context, PredictionContextCache contextCache, PredictionContext.IdentityHashMap visited)
		{
			if (context.IsEmpty)
			{
				return context;
			}

			PredictionContext existing = visited.Get(context);
			if (existing != null)
			{
				return existing;
			}

			existing = contextCache.Get(context);
			if (existing != null)
			{
				visited.Put(context, existing);
				return existing;
			}

			bool changed = false;
			PredictionContext[] parents = new PredictionContext[context.Size];
			for (int i = 0; i < parents.Length; i++)
			{
				PredictionContext parent = GetCachedContext(context.GetParent(i), contextCache, visited);
				if (changed || parent != context.GetParent(i))
				{
					if (!changed)
					{
						parents = new PredictionContext[context.Size];
						for (int j = 0; j < context.Size; j++)
						{
							parents[j] = context.GetParent(j);
						}

						changed = true;
					}

					parents[i] = parent;
				}
			}

			if (!changed)
			{
				contextCache.Add(context);
				visited.Put(context, context);
				return context;
			}

			PredictionContext updated;
			if (parents.Length == 0)
			{
				updated = EMPTY;
			}
			else if (parents.Length == 1)
			{
				updated = SingletonPredictionContext.Create(parents[0], context.GetReturnState(0));
			}
			else {
				ArrayPredictionContext arrayPredictionContext = (ArrayPredictionContext)context;
				updated = new ArrayPredictionContext(parents, arrayPredictionContext.returnStates);
			}

			contextCache.Add(updated);
			visited.Put(updated, updated);
			visited.Put(context, updated);

			return updated;
		}

		public virtual PredictionContext GetChild(int returnState)
		{
			return new SingletonPredictionContext(this, returnState);
		}


		public virtual string[] ToStrings(IRecognizer recognizer, int currentState)
		{
			return ToStrings(recognizer, PredictionContext.EMPTY, currentState);
		}

		public virtual string[] ToStrings(IRecognizer recognizer, PredictionContext stop, int currentState)
		{
			List<string> result = new List<string>();
			for (int perm = 0; ; perm++)
			{
				int offset = 0;
				bool last = true;
				PredictionContext p = this;
				int stateNumber = currentState;
				StringBuilder localBuffer = new StringBuilder();
				localBuffer.Append("[");
				while (!p.IsEmpty && p != stop)
				{
					int index = 0;
					if (p.Size > 0)
					{
						int bits = 1;
						while ((1 << bits) < p.Size)
						{
							bits++;
						}
						int mask = (1 << bits) - 1;
						index = (perm >> offset) & mask;
						last &= index >= p.Size - 1;
						if (index >= p.Size)
						{
							goto outer_continue;
						}
						offset += bits;
					}
					if (recognizer != null)
					{
						if (localBuffer.Length > 1)
						{
							// first char is '[', if more than that this isn't the first rule
							localBuffer.Append(' ');
						}
						ATN atn = recognizer.Atn;
						ATNState s = atn.states[stateNumber];
						string ruleName = recognizer.RuleNames[s.ruleIndex];
						localBuffer.Append(ruleName);
					}
					else
					{
						if (p.GetReturnState(index) != EMPTY_RETURN_STATE)
						{
							if (!p.IsEmpty)
							{
								if (localBuffer.Length > 1)
								{
									// first char is '[', if more than that this isn't the first rule
									localBuffer.Append(' ');
								}
								localBuffer.Append(p.GetReturnState(index));
							}
						}
					}
					stateNumber = p.GetReturnState(index);
					p = p.GetParent(index);
				}
				localBuffer.Append("]");
				result.Add(localBuffer.ToString());
				if (last)
				{
					break;
				}
			outer_continue:;
			}

			return result.ToArray();
		}

		public sealed class IdentityHashMap : Dictionary<PredictionContext, PredictionContext>
		{
			public IdentityHashMap()
				: base(PredictionContext.IdentityEqualityComparator.Instance)
			{
			}
		}

		public sealed class IdentityEqualityComparator : EqualityComparer<PredictionContext>
		{
			public static readonly PredictionContext.IdentityEqualityComparator Instance = new PredictionContext.IdentityEqualityComparator();

			private IdentityEqualityComparator()
			{
			}

			public override int GetHashCode(PredictionContext obj)
			{
				return obj.GetHashCode();
			}

			public override bool Equals(PredictionContext a, PredictionContext b)
			{
				return a == b;
			}
		}
	}
}
