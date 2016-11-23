/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
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

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.AbstractEqualityComparator;
import org.antlr.v4.runtime.misc.Array2DHashSet;
import org.antlr.v4.runtime.misc.DoubleKeyMap;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Specialized {@link Set}{@code <}{@link ATNConfig}{@code >} that can track
 * info about the set, with support for combining similar configurations using a
 * graph-structured stack.
 */
public class ATNConfigSet implements Set<ATNConfig> {
	/**
	 * The reason that we need this is because we don't want the hash map to use
	 * the standard hash code and equals. We need all configurations with the same
	 * {@code (s,i,_,semctx)} to be equal. Unfortunately, this key effectively doubles
	 * the number of objects associated with ATNConfigs. The other solution is to
	 * use a hash table that lets us specify the equals/hashcode operation.
	 */
	public static class ConfigHashSet extends AbstractConfigHashSet {
		public ConfigHashSet() {
			super(ConfigEqualityComparator.INSTANCE);
		}
	}

	public static final class ConfigEqualityComparator extends AbstractEqualityComparator<ATNConfig> {
		public static final ConfigEqualityComparator INSTANCE = new ConfigEqualityComparator();

		private ConfigEqualityComparator() {
		}

		@Override
		public int hashCode(ATNConfig o) {
			int hashCode = 7;
			hashCode = 31 * hashCode + o.state.stateNumber;
			hashCode = 31 * hashCode + o.alt;
			hashCode = 31 * hashCode + o.semanticContext.hashCode();
	        return hashCode;
		}

		@Override
		public boolean equals(ATNConfig a, ATNConfig b) {
			if ( a==b ) return true;
			if ( a==null || b==null ) return false;
			return a.state.stateNumber==b.state.stateNumber
				&& a.alt==b.alt
				&& a.semanticContext.equals(b.semanticContext);
		}
	}

	/** Indicates that the set of configurations is read-only. Do not
	 *  allow any code to manipulate the set; DFA states will point at
	 *  the sets and they must not change. This does not protect the other
	 *  fields; in particular, conflictingAlts is set after
	 *  we've made this readonly.
 	 */
	protected boolean readonly = false;

	/**
	 * All configs but hashed by (s, i, _, pi) not including context. Wiped out
	 * when we go readonly as this set becomes a DFA state.
	 */
	public AbstractConfigHashSet configLookup;

	/** Track the elements as they are added to the set; supports get(i) */
	public final ArrayList<ATNConfig> configs = new ArrayList<ATNConfig>(7);

	// TODO: these fields make me pretty uncomfortable but nice to pack up info together, saves recomputation
	// TODO: can we track conflicts as they are added to save scanning configs later?
	public int uniqueAlt;
	/** Currently this is only used when we detect SLL conflict; this does
	 *  not necessarily represent the ambiguous alternatives. In fact,
	 *  I should also point out that this seems to include predicated alternatives
	 *  that have predicates that evaluate to false. Computed in computeTargetState().
 	 */
	protected BitSet conflictingAlts;

	// Used in parser and lexer. In lexer, it indicates we hit a pred
	// while computing a closure operation.  Don't make a DFA state from this.
	public boolean hasSemanticContext;
	public boolean dipsIntoOuterContext;

	/** Indicates that this configuration set is part of a full context
	 *  LL prediction. It will be used to determine how to merge $. With SLL
	 *  it's a wildcard whereas it is not for LL context merge.
	 */
	public final boolean fullCtx;

	private int cachedHashCode = -1;

	public ATNConfigSet(boolean fullCtx) {
		configLookup = new ConfigHashSet();
		this.fullCtx = fullCtx;
	}
	public ATNConfigSet() { this(true); }

	public ATNConfigSet(ATNConfigSet old) {
		this(old.fullCtx);
		addAll(old);
		this.uniqueAlt = old.uniqueAlt;
		this.conflictingAlts = old.conflictingAlts;
		this.hasSemanticContext = old.hasSemanticContext;
		this.dipsIntoOuterContext = old.dipsIntoOuterContext;
	}

	@Override
	public boolean add(ATNConfig config) {
		return add(config, null);
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
	public boolean add(
		ATNConfig config,
		DoubleKeyMap<PredictionContext,PredictionContext,PredictionContext> mergeCache)
	{
		if ( readonly ) throw new IllegalStateException("This set is readonly");
		if ( config.semanticContext!=SemanticContext.NONE ) {
			hasSemanticContext = true;
		}
		if (config.getOuterContextDepth() > 0) {
			dipsIntoOuterContext = true;
		}
		ATNConfig existing = configLookup.getOrAdd(config);
		if ( existing==config ) { // we added this new one
			cachedHashCode = -1;
			configs.add(config);  // track order here
			return true;
		}
		// a previous (s,i,pi,_), merge with it and save result
		boolean rootIsWildcard = !fullCtx;
		PredictionContext merged =
			PredictionContext.merge(existing.context, config.context, rootIsWildcard, mergeCache);
		// no need to check for existing.context, config.context in cache
		// since only way to create new graphs is "call rule" and here. We
		// cache at both places.
		existing.reachesIntoOuterContext =
			Math.max(existing.reachesIntoOuterContext, config.reachesIntoOuterContext);

		// make sure to preserve the precedence filter suppression during the merge
		if (config.isPrecedenceFilterSuppressed()) {
			existing.setPrecedenceFilterSuppressed(true);
		}

		existing.context = merged; // replace context; no need to alt mapping
		return true;
	}

	/** Return a List holding list of configs */
    public List<ATNConfig> elements() { return configs; }

	public Set<ATNState> getStates() {
		Set<ATNState> states = new HashSet<ATNState>();
		for (ATNConfig c : configs) {
			states.add(c.state);
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

	public BitSet getAlts() {
		BitSet alts = new BitSet();
		for (ATNConfig config : configs) {
			alts.set(config.alt);
		}
		return alts;
	}

	public List<SemanticContext> getPredicates() {
		List<SemanticContext> preds = new ArrayList<SemanticContext>();
		for (ATNConfig c : configs) {
			if ( c.semanticContext!=SemanticContext.NONE ) {
				preds.add(c.semanticContext);
			}
		}
		return preds;
	}

	public ATNConfig get(int i) { return configs.get(i); }

	public void optimizeConfigs(ATNSimulator interpreter) {
		if ( readonly ) throw new IllegalStateException("This set is readonly");
		if ( configLookup.isEmpty() ) return;

		for (ATNConfig config : configs) {
//			int before = PredictionContext.getAllContextNodes(config.context).size();
			config.context = interpreter.getCachedContext(config.context);
//			int after = PredictionContext.getAllContextNodes(config.context).size();
//			System.out.println("configs "+before+"->"+after);
		}
	}

	@Override
	public boolean addAll(Collection<? extends ATNConfig> coll) {
		for (ATNConfig c : coll) add(c);
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		else if (!(o instanceof ATNConfigSet)) {
			return false;
		}

//		System.out.print("equals " + this + ", " + o+" = ");
		ATNConfigSet other = (ATNConfigSet)o;
		boolean same = configs!=null &&
			configs.equals(other.configs) &&  // includes stack context
			this.fullCtx == other.fullCtx &&
			this.uniqueAlt == other.uniqueAlt &&
			this.conflictingAlts == other.conflictingAlts &&
			this.hasSemanticContext == other.hasSemanticContext &&
			this.dipsIntoOuterContext == other.dipsIntoOuterContext;

//		System.out.println(same);
		return same;
	}

	@Override
	public int hashCode() {
		if (isReadonly()) {
			if (cachedHashCode == -1) {
				cachedHashCode = configs.hashCode();
			}

			return cachedHashCode;
		}

		return configs.hashCode();
	}

	@Override
	public int size() {
		return configs.size();
	}

	@Override
	public boolean isEmpty() {
		return configs.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		if (configLookup == null) {
			throw new UnsupportedOperationException("This method is not implemented for readonly sets.");
		}

		return configLookup.contains(o);
	}

	public boolean containsFast(ATNConfig obj) {
		if (configLookup == null) {
			throw new UnsupportedOperationException("This method is not implemented for readonly sets.");
		}

		return configLookup.containsFast(obj);
	}

	@Override
	public Iterator<ATNConfig> iterator() {
		return configs.iterator();
	}

	@Override
	public void clear() {
		if ( readonly ) throw new IllegalStateException("This set is readonly");
		configs.clear();
		cachedHashCode = -1;
		configLookup.clear();
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
		configLookup = null; // can't mod, no need for lookup cache
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(elements().toString());
		if ( hasSemanticContext ) buf.append(",hasSemanticContext=").append(hasSemanticContext);
		if ( uniqueAlt!=ATN.INVALID_ALT_NUMBER ) buf.append(",uniqueAlt=").append(uniqueAlt);
		if ( conflictingAlts!=null ) buf.append(",conflictingAlts=").append(conflictingAlts);
		if ( dipsIntoOuterContext ) buf.append(",dipsIntoOuterContext");
		return buf.toString();
	}

	// satisfy interface

	@Override
	public ATNConfig[] toArray() {
		return configLookup.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return configLookup.toArray(a);
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public static abstract class AbstractConfigHashSet extends Array2DHashSet<ATNConfig> {

		public AbstractConfigHashSet(AbstractEqualityComparator<? super ATNConfig> comparator) {
			this(comparator, 16, 2);
		}

		public AbstractConfigHashSet(AbstractEqualityComparator<? super ATNConfig> comparator, int initialCapacity, int initialBucketCapacity) {
			super(comparator, initialCapacity, initialBucketCapacity);
		}

		@Override
		protected final ATNConfig asElementType(Object o) {
			if (!(o instanceof ATNConfig)) {
				return null;
			}

			return (ATNConfig)o;
		}

		@Override
		protected final ATNConfig[][] createBuckets(int capacity) {
			return new ATNConfig[capacity][];
		}

		@Override
		protected final ATNConfig[] createBucket(int capacity) {
			return new ATNConfig[capacity];
		}

	}
}
