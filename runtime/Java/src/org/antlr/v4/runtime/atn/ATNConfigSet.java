/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/** Specialized OrderedHashSet that can track info about the set.
 *  Might be able to optimize later w/o affecting code that uses this set.

 histogram of lexer DFA configset size:

 206 30  <- 206 sets with size 30
  47 1
  17 31
  12 2
  10 3
   7 32
   4 4
   3 35
   2 9
   2 6
   2 5
   2 34
   1 7
   1 33
   1 29
   1 12
   1 119 <- max size

 322 set size for SLL parser java.*

 888 1
 411 54
 365 88
 304 56
 206 80
 182 16
 167 86
 166 78
 158 84
 131 2
 121 20
 120 8
 119 112
  82 10
  73 6
  53 174
  47 90
  45 4
  39 12
  38 122
 37 89
 37 62
 34 3
 34 18
 32 81
 31 87
 28 45
 27 144
 25 41
 24 132
 22 91
 22 7
 21 82
 21 28
 21 27
 17 9
 16 29
 16 155
 15 51
 15 118
 14 146
 14 114
 13 5
 13 38
 12 48
 11 64
 11 50
 11 22
 11 134
 11 131
 10 79
 10 76
 10 59
 10 58
 10 55
 10 39
 10 116
  9 74
  9 47
  9 310
   ...

 javalr, java.* configs with # preds histogram:

 4569 0
   57 1
   27 27
    5 76
    4 28
    3 72
    3 38
    3 30
    2 6
    2 32
    1 9
    1 2

 */
public class ATNConfigSet implements Set<ATNConfig> {
	// TODO: convert to long like Sam? use list and map from config to ctx?
	/*
	The reason that we need this is because we don't want the hash map to use
	the standard hash code and equals. We need all configurations with the same
	(s,i,_,semctx) to be equal. Unfortunately, this key effectively doubles
	the number of objects associated with ATNConfigs. The other solution is to
	use a hash table that lets us specify the equals/hashcode operation.
	 */
	public static class Key {
		ATNState state;
		int alt;
		SemanticContext semanticContext;

		public Key(ATNState state, int alt, SemanticContext semanticContext) {
			this.state = state;
			this.alt = alt;
			this.semanticContext = semanticContext;
		}

		public Key(ATNConfig c) {
			this(c.state, c.alt, c.semanticContext);
		}

		@Override
		public boolean equals(Object obj) {
			if ( obj==this ) return true;
			if ( this.hashCode() != obj.hashCode() ) return false;
			if ( !(obj instanceof Key) ) return false;
			Key key = (Key)obj;
			return this.state.stateNumber==key.state.stateNumber
				&& this.alt==key.alt
				&& this.semanticContext.equals(key.semanticContext);
		}

		@Override
		public int hashCode() {
			int hashCode = 7;
			hashCode = 5 * hashCode + state.stateNumber;
			hashCode = 5 * hashCode + alt;
			hashCode = 5 * hashCode + semanticContext.hashCode();
	        return hashCode;
		}
	}

	/** Indicates that the set of configurations is read-only. Do not
	 *  allow any code to manipulate the set; DFA states will point at
	 *  the sets and they must not change. This does not protect the other
	 *  fields; in particular, conflictingAlts is set after
	 *  we've made this readonly.
 	 */
	protected boolean readonly = false;

	/** Track every config we add */
	public final LinkedHashMap<Key,ATNConfig> configToContext;

	/** Track the elements as they are added to the set; supports get(i) */
	// too hard to keep in sync
//	public final ArrayList<ATNConfig> configs = new ArrayList<ATNConfig>();

	// TODO: these fields make me pretty uncomfortable but nice to pack up info together, saves recomputation
	// TODO: can we track conflicts as they are added to save scanning configs later?
	public int uniqueAlt;
	protected IntervalSet conflictingAlts;
	// Used in parser and lexer. In lexer, it indicates we hit a pred
	// while computing a closure operation.  Don't make a DFA state from this.
	public boolean hasSemanticContext;
	public boolean dipsIntoOuterContext;

	/** Indicates that this configuration set is part of a full context
	 *  LL prediction. It will be used to determine how to merge $. With SLL
	 *  it's a wildcard whereas it is not for LL context merge.
	 */
	public final boolean fullCtx;

	public ATNConfigSet(boolean fullCtx) {
		configToContext = new LinkedHashMap<Key, ATNConfig>();
		this.fullCtx = fullCtx;
	}
	public ATNConfigSet() { this(true); }

	public ATNConfigSet(ATNConfigSet old, PredictionContextCache contextCache) {
		configToContext = new LinkedHashMap<Key, ATNConfig>(old.configToContext);
		this.fullCtx = old.fullCtx;
		this.uniqueAlt = old.uniqueAlt;
		this.conflictingAlts = old.conflictingAlts;
		this.hasSemanticContext = old.hasSemanticContext;
		this.dipsIntoOuterContext = old.dipsIntoOuterContext;
	}

	@Override
	public boolean add(ATNConfig e) {
		return add(e, null);
	}

	/** Adding a new config means merging contexts with existing configs for
	 *  (s, i, pi, _)
	 *  We use (s,i,pi) as key
	 */
	public boolean add(ATNConfig config, @Nullable PredictionContextCache contextCache) {
		if ( readonly ) throw new IllegalStateException("This set is readonly");
		contextCache = null; // TODO: costs time to cache and saves essentially no RAM
		if ( config.semanticContext!=SemanticContext.NONE ) {
			hasSemanticContext = true;
		}
		Key key = new Key(config);
		ATNConfig existing = configToContext.get(key);
		if ( existing==null ) { // nothing there yet; easy, just add
			configToContext.put(key, config);
			return true;
		}
		// a previous (s,i,pi,_), merge with it and save result
		boolean rootIsWildcard = !fullCtx;
		PredictionContext merged =
			PredictionContext.merge(existing.context, config.context, contextCache, rootIsWildcard);
		// no need to check for existing.context, config.context in cache
		// since only way to create new graphs is "call rule" and here. We
		// cache at both places.
		if ( contextCache!=null ) merged = contextCache.add(merged);
		existing.reachesIntoOuterContext =
			Math.max(existing.reachesIntoOuterContext, config.reachesIntoOuterContext);
		existing.context = merged; // replace context; no need to alt mapping
		return true;
	}

	/** Return a List holding list of configs */
    public List<ATNConfig> elements() {
		List<ATNConfig> configs = new ArrayList<ATNConfig>();
		configs.addAll(configToContext.values());
		return configs;
    }

	public Set<ATNState> getStates() {
		Set<ATNState> states = new HashSet<ATNState>();
		for (Key key : this.configToContext.keySet()) {
			states.add(key.state);
		}
		return states;
	}

	public List<SemanticContext> getPredicates() {
		List<SemanticContext> preds = new ArrayList<SemanticContext>();
		for (ATNConfig c : configToContext.values()) {
			if ( c.semanticContext!=SemanticContext.NONE ) {
				preds.add(c.semanticContext);
			}
		}
		return preds;
	}

	// TODO: very expensive, used in lexer to kill after wildcard config
	public ATNConfig get(int i) {
		int j = 0;
		for (ATNConfig c : configToContext.values()) {
			if ( j == i ) return c;
			j++;
		}
		throw new IndexOutOfBoundsException("config set index "+i+" not in 0.."+size());
	}

	public void remove(int i) {
		if ( readonly ) throw new IllegalStateException("This set is readonly");
		ATNConfig c = elements().get(i);
		configToContext.remove(new Key(c));
	}

	public void optimizeConfigs(ATNSimulator interpreter) {
		if ( readonly ) throw new IllegalStateException("This set is readonly");
		if ( configToContext.isEmpty() ) return;

		for (ATNConfig config : configToContext.values()) {
//			int before = PredictionContext.getAllContextNodes(config.context).size();
			config.context = interpreter.getCachedContext(config.context);
//			int after = PredictionContext.getAllContextNodes(config.context).size();
//			System.out.println("configs "+before+"->"+after);
		}
	}

	@Override
	public boolean addAll(Collection<? extends ATNConfig> c) {
		return addAll(c, null);
	}

	public boolean addAll(Collection<? extends ATNConfig> coll,
						  PredictionContextCache contextCache)
	{
		for (ATNConfig c : coll) {
			add(c, contextCache);
		}
		return false;
	}

	@Override
	public boolean equals(Object o) {
//		System.out.print("equals " + this + ", " + o+" = ");
		ATNConfigSet other = (ATNConfigSet)o;
		boolean same = configToContext!=null &&
			configToContext.equals(other.configToContext) &&
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
		return configToContext.hashCode();
	}

	@Override
	public int size() {
		return configToContext.size();
	}

	@Override
	public boolean isEmpty() {
		return configToContext.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		if ( o instanceof ATNConfig ) {
			return configToContext.containsKey(new Key((ATNConfig)o));
		}
		return false;
	}

	@Override
	public Iterator<ATNConfig> iterator() {
		return configToContext.values().iterator();
	}

	@Override
	public void clear() {
		if ( readonly ) throw new IllegalStateException("This set is readonly");
		configToContext.clear();
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(elements().toString());
//		buf.append(super.toString());
		if ( hasSemanticContext ) buf.append(",hasSemanticContext=").append(hasSemanticContext);
		if ( uniqueAlt!=ATN.INVALID_ALT_NUMBER ) buf.append(",uniqueAlt=").append(uniqueAlt);
		if ( conflictingAlts!=null ) buf.append(",conflictingAlts=").append(conflictingAlts);
		if ( dipsIntoOuterContext ) buf.append(",dipsIntoOuterContext");
		return buf.toString();
	}

	// satisfy interface

	@Override
	public Object[] toArray() {
		ATNConfig[] configs = new ATNConfig[configToContext.size()];
		int i = 0;
		for (ATNConfig c : configToContext.values()) configs[i++] = c;
		return configs;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		int i = 0;
		for (ATNConfig c : configToContext.values()) a[i++] = (T)c;
		return a;
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
}
