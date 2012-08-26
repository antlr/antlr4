/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *      derived from this software without specific prior written permission.
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

import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.misc.Utils;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 *
 * @author Sam Harwell
 */
public class ATNConfigSet implements Set<ATNConfig> {

	/**
	 * This maps (state, alt) -> merged {@link ATNConfig}. The key does not account for
	 * the {@link ATNConfig#semanticContext} of the value, which is only a problem if a single
	 * {@code ATNConfigSet} contains two configs with the same state and alternative
	 * but different semantic contexts. When this case arises, the first config
	 * added to this map stays, and the remaining configs are placed in {@link #unmerged}.
	 * <p>
	 * This map is only used for optimizing the process of adding configs to the set,
	 * and is {@code null} for read-only sets stored in the DFA.
	 */
	private final HashMap<Long, ATNConfig> mergedConfigs;
	/**
	 * This is an "overflow" list holding configs which cannot be merged with one
	 * of the configs in {@link #mergedConfigs} but have a colliding key. This
	 * occurs when two configs in the set have the same state and alternative but
	 * different semantic contexts.
	 * <p>
	 * This list is only used for optimizing the process of adding configs to the set,
	 * and is {@code null} for read-only sets stored in the DFA.
	 */
	private final ArrayList<ATNConfig> unmerged;
	/**
	 * This is a list of all configs in this set.
	 */
	private final ArrayList<ATNConfig> configs;

	private int uniqueAlt;
	private BitSet conflictingAlts;
	// Used in parser and lexer. In lexer, it indicates we hit a pred
	// while computing a closure operation.  Don't make a DFA state from this.
	private boolean hasSemanticContext;
	private boolean dipsIntoOuterContext;
	/**
	 * When {@code true}, this config set represents configurations where the entire
	 * outer context has been consumed by the ATN interpreter. This prevents the
	 * {@link ParserATNSimulator#closure} from pursuing the global FOLLOW when a
	 * rule stop state is reached with an empty prediction context.
	 * <p>
	 * Note: {@code outermostConfigSet} and {@link #dipsIntoOuterContext} should never
	 * be true at the same time.
	 */
	private boolean outermostConfigSet;

	public ATNConfigSet() {
		this.mergedConfigs = new HashMap<Long, ATNConfig>();
		this.unmerged = new ArrayList<ATNConfig>();
		this.configs = new ArrayList<ATNConfig>();

		this.uniqueAlt = ATN.INVALID_ALT_NUMBER;
	}

	@SuppressWarnings("unchecked")
	private ATNConfigSet(ATNConfigSet set, boolean readonly) {
		if (readonly) {
			this.mergedConfigs = null;
			this.unmerged = null;
		} else if (!set.isReadOnly()) {
			this.mergedConfigs = (HashMap<Long, ATNConfig>)set.mergedConfigs.clone();
			this.unmerged = (ArrayList<ATNConfig>)set.unmerged.clone();
		} else {
			this.mergedConfigs = new HashMap<Long, ATNConfig>(set.configs.size());
			this.unmerged = new ArrayList<ATNConfig>();
		}

		this.configs = (ArrayList<ATNConfig>)set.configs.clone();

		this.dipsIntoOuterContext = set.dipsIntoOuterContext;
		this.hasSemanticContext = set.hasSemanticContext;
		this.outermostConfigSet = set.outermostConfigSet;

		if (readonly || !set.isReadOnly()) {
			this.uniqueAlt = set.uniqueAlt;
			this.conflictingAlts = set.conflictingAlts;
		}

		// if (!readonly && set.isReadOnly()) -> addAll is called from clone()
	}

	public boolean isReadOnly() {
		return mergedConfigs == null;
	}

	public final void stripHiddenConfigs() {
		ensureWritable();

		Iterator<Map.Entry<Long, ATNConfig>> iterator = mergedConfigs.entrySet().iterator();
		while (iterator.hasNext()) {
			if (iterator.next().getValue().isHidden()) {
				iterator.remove();
			}
		}

		ListIterator<ATNConfig> iterator2 = unmerged.listIterator();
		while (iterator2.hasNext()) {
			if (iterator2.next().isHidden()) {
				iterator2.remove();
			}
		}

		iterator2 = configs.listIterator();
		while (iterator2.hasNext()) {
			if (iterator2.next().isHidden()) {
				iterator2.remove();
			}
		}
	}

	public boolean isOutermostConfigSet() {
		return outermostConfigSet;
	}

	public void setOutermostConfigSet(boolean outermostConfigSet) {
		if (this.outermostConfigSet && !outermostConfigSet) {
			throw new IllegalStateException();
		}

		assert !outermostConfigSet || !dipsIntoOuterContext;
		this.outermostConfigSet = outermostConfigSet;
	}

	public Set<ATNState> getStates() {
		Set<ATNState> states = new HashSet<ATNState>();
		for (ATNConfig c : this.configs) {
			states.add(c.getState());
		}

		return states;
	}

	public void optimizeConfigs(ATNSimulator interpreter) {
		if (configs.isEmpty()) {
			return;
		}

		for (int i = 0; i < configs.size(); i++) {
			ATNConfig config = configs.get(i);
			config.setContext(interpreter.getCachedContext(config.getContext()));
		}
	}

	public ATNConfigSet clone(boolean readonly) {
		ATNConfigSet copy = new ATNConfigSet(this, readonly);
		if (!readonly && this.isReadOnly()) {
			copy.addAll(this.configs);
		}

		return copy;
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
		if (!(o instanceof ATNConfig)) {
			return false;
		}

		ATNConfig config = (ATNConfig)o;
		ATNConfig mergedConfig = mergedConfigs.get(getKey(config));
		if (mergedConfig != null && canMerge(config, mergedConfig)) {
			return mergedConfig.contains(config);
		}

		for (ATNConfig c : unmerged) {
			if (c.contains(config)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Iterator<ATNConfig> iterator() {
		return new ATNConfigSetIterator();
	}

	@Override
	public Object[] toArray() {
		return configs.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return configs.toArray(a);
	}

	@Override
	public boolean add(ATNConfig e) {
		return add(e, null);
	}

	public boolean add(ATNConfig e, @Nullable PredictionContextCache contextCache) {
		ensureWritable();
		assert !outermostConfigSet || !e.getReachesIntoOuterContext();
		assert !e.isHidden();

		if (contextCache == null) {
			contextCache = PredictionContextCache.UNCACHED;
		}

		boolean addKey;
		long key = getKey(e);
		ATNConfig mergedConfig = mergedConfigs.get(key);
		addKey = (mergedConfig == null);
		if (mergedConfig != null && canMerge(e, key, mergedConfig)) {
			mergedConfig.setOuterContextDepth(Math.max(mergedConfig.getOuterContextDepth(), e.getOuterContextDepth()));

			PredictionContext joined = PredictionContext.join(mergedConfig.getContext(), e.getContext(), contextCache);
			updatePropertiesForMergedConfig(e);
			if (mergedConfig.getContext() == joined) {
				return false;
			}

			mergedConfig.setContext(joined);
			return true;
		}

		for (int i = 0; i < unmerged.size(); i++) {
			ATNConfig unmergedConfig = unmerged.get(i);
			if (canMerge(e, key, unmergedConfig)) {
				unmergedConfig.setOuterContextDepth(Math.max(unmergedConfig.getOuterContextDepth(), e.getOuterContextDepth()));

				PredictionContext joined = PredictionContext.join(unmergedConfig.getContext(), e.getContext(), contextCache);
				updatePropertiesForMergedConfig(e);
				if (unmergedConfig.getContext() == joined) {
					return false;
				}

				unmergedConfig.setContext(joined);

				if (addKey) {
					mergedConfigs.put(key, unmergedConfig);
					unmerged.remove(i);
				}

				return true;
			}
		}

		configs.add(e);
		if (addKey) {
			mergedConfigs.put(key, e);
		} else {
			unmerged.add(e);
		}

		updatePropertiesForAddedConfig(e);
		return true;
	}

	private void updatePropertiesForMergedConfig(ATNConfig config) {
		// merged configs can't change the alt or semantic context
		dipsIntoOuterContext |= config.getReachesIntoOuterContext();
		assert !outermostConfigSet || !dipsIntoOuterContext;
	}

	private void updatePropertiesForAddedConfig(ATNConfig config) {
		if (configs.size() == 1) {
			uniqueAlt = config.getAlt();
		} else if (uniqueAlt != config.getAlt()) {
			uniqueAlt = ATN.INVALID_ALT_NUMBER;
		}

		hasSemanticContext |= !SemanticContext.NONE.equals(config.getSemanticContext());
		dipsIntoOuterContext |= config.getReachesIntoOuterContext();
		assert !outermostConfigSet || !dipsIntoOuterContext;
	}

	private static boolean canMerge(ATNConfig left, ATNConfig right) {
		if (getKey(left) != getKey(right)) {
			return false;
		}

		return left.getSemanticContext().equals(right.getSemanticContext());
	}

	private static boolean canMerge(ATNConfig left, long leftKey, ATNConfig right) {
		if (left.getState().stateNumber != right.getState().stateNumber) {
			return false;
		}

		if (leftKey != getKey(right)) {
			return false;
		}

		return left.getSemanticContext().equals(right.getSemanticContext());
	}

	private static long getKey(ATNConfig e) {
		long key = ((long)e.getState().stateNumber << 32) + (e.getAlt() << 3);
		//key |= e.reachesIntoOuterContext != 0 ? 1 : 0;
		//key |= e.resolveWithPredicate ? 1 << 1 : 0;
		//key |= e.traversedPredicate ? 1 << 2 : 0;
		return key;
	}

	@Override
	public boolean remove(Object o) {
		ensureWritable();

		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!(o instanceof ATNConfig)) {
				return false;
			}

			if (!contains((ATNConfig)o)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean addAll(Collection<? extends ATNConfig> c) {
		return addAll(c, null);
	}

	public boolean addAll(Collection<? extends ATNConfig> c, PredictionContextCache contextCache) {
		ensureWritable();

		boolean changed = false;
		for (ATNConfig group : c) {
			changed |= add(group, contextCache);
		}

		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		ensureWritable();
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		ensureWritable();
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void clear() {
		ensureWritable();

		mergedConfigs.clear();
		unmerged.clear();
		configs.clear();

		dipsIntoOuterContext = false;
		hasSemanticContext = false;
		uniqueAlt = ATN.INVALID_ALT_NUMBER;
		conflictingAlts = null;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof ATNConfigSet)) {
			return false;
		}

		ATNConfigSet other = (ATNConfigSet)obj;
		return this.outermostConfigSet == other.outermostConfigSet
			&& Utils.equals(conflictingAlts, other.conflictingAlts)
			&& configs.equals(other.configs);
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		hashCode = 5 * hashCode ^ (outermostConfigSet ? 1 : 0);
		hashCode = 5 * hashCode ^ configs.hashCode();
		return hashCode;
	}

	@Override
	public String toString() {
		return toString(false);
	}

	public String toString(boolean showContext) {
		StringBuilder buf = new StringBuilder();
		List<ATNConfig> sortedConfigs = new ArrayList<ATNConfig>(configs);
		Collections.sort(sortedConfigs, new Comparator<ATNConfig>() {
			@Override
			public int compare(ATNConfig o1, ATNConfig o2) {
				if (o1.getAlt() != o2.getAlt()) {
					return o1.getAlt() - o2.getAlt();
				}
				else if (o1.getState().stateNumber != o2.getState().stateNumber) {
					return o1.getState().stateNumber - o2.getState().stateNumber;
				}
				else {
					return o1.getSemanticContext().toString().compareTo(o2.getSemanticContext().toString());
				}
			}
		});

		buf.append("[");
		for (int i = 0; i < sortedConfigs.size(); i++) {
			if (i > 0) {
				buf.append(", ");
			}
			buf.append(sortedConfigs.get(i).toString(null, true, showContext));
		}
		buf.append("]");

		if ( hasSemanticContext ) buf.append(",hasSemanticContext=").append(hasSemanticContext);
		if ( uniqueAlt!=ATN.INVALID_ALT_NUMBER ) buf.append(",uniqueAlt=").append(uniqueAlt);
		if ( conflictingAlts!=null ) buf.append(",conflictingAlts=").append(conflictingAlts);
		if ( dipsIntoOuterContext ) buf.append(",dipsIntoOuterContext");
		return buf.toString();
	}

	public int getUniqueAlt() {
		return uniqueAlt;
	}

	public boolean hasSemanticContext() {
		return hasSemanticContext;
	}

	public void markExplicitSemanticContext() {
		ensureWritable();
		hasSemanticContext = true;
	}

	public BitSet getConflictingAlts() {
		return conflictingAlts;
	}

	public void setConflictingAlts(BitSet conflictingAlts) {
		ensureWritable();
		this.conflictingAlts = conflictingAlts;
	}

	public boolean getDipsIntoOuterContext() {
		return dipsIntoOuterContext;
	}

	public ATNConfig get(int index) {
		return configs.get(index);
	}

	public void remove(int index) {
		ensureWritable();
		ATNConfig config = configs.get(index);
		configs.remove(config);
		long key = getKey(config);
		if (mergedConfigs.get(key) == config) {
			mergedConfigs.remove(key);
		} else {
			for (int i = 0; i < unmerged.size(); i++) {
				if (unmerged.get(i) == config) {
					unmerged.remove(i);
					return;
				}
			}
		}
	}

	protected final void ensureWritable() {
		if (isReadOnly()) {
			throw new IllegalStateException("This ATNConfigSet is read only.");
		}
	}

	private final class ATNConfigSetIterator implements Iterator<ATNConfig> {

		int index = -1;
		boolean removed = false;

		@Override
		public boolean hasNext() {
			return index + 1 < configs.size();
		}

		@Override
		public ATNConfig next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}

			index++;
			removed = false;
			return configs.get(index);
		}

		@Override
		public void remove() {
			if (removed || index < 0 || index >= configs.size()) {
				throw new IllegalStateException();
			}

			ATNConfigSet.this.remove(index);
			removed = true;
		}

	}
}
