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

 322 set size for SLL parser java.* in DFA states:

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

 javalr, java.* all atnconfigsets; max size = 322, num sets = 269088

 114186 1    <-- optimize
 35712 6
 28081 78
 15252 54
 14171 56
 13159 12
 11810 88
 6873 86
 6158 80
 5169 4
 3773 118
 2350 16
 1002 112
  915 28
  898 44
  734 2
  632 62
  575 8
  566 59
  474 20
  388 84
  343 48
  333 55
  328 47
  311 41
  306 38
  277 81
  263 79
  255 66
  245 90
  245 87
  234 50
  224 10
  220 60
  194 64
  186 32
  184 82
  150 18
  125 7
  121 132
  116 30
  103 51
   95 114
   84 36
   82 40
   78 22
   77 89
   55 9
   53 174
   48 152
   44 67
   44 5
   42 115
   41 58
   38 122
   37 134
   34 13
   34 116
   29 45
   29 3
   29 24
   27 144
   26 146
   25 91
   24 113
   20 27
   ...

 number with 1-9 elements:

 114186 1
 35712 6
 5169 4
  734 2
  575 8
  125 7
   55 9
   44 5
   29 3

 Can cover 60% of sizes with size up to 6
 Can cover 44% of sizes with size up to 4
 Can cover 42% of sizes with size up to 1
 */
public class ATNConfigSet implements Set<ATNConfig> {
	/*
	The reason that we need this is because we don't want the hash map to use
	the standard hash code and equals. We need all configurations with the same
	(s,i,_,semctx) to be equal. Unfortunately, this key effectively doubles
	the number of objects associated with ATNConfigs. The other solution is to
	use a hash table that lets us specify the equals/hashcode operation.
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

	/** All configs but hashed by (s, i, _, pi) not incl context.  Wiped out
	 *  when we go readonly as this set becomes a DFA state.
	 */
	public AbstractConfigHashSet configLookup;

	/** Track the elements as they are added to the set; supports get(i) */
	public final ArrayList<ATNConfig> configs = new ArrayList<ATNConfig>(7);

	// TODO: these fields make me pretty uncomfortable but nice to pack up info together, saves recomputation
	// TODO: can we track conflicts as they are added to save scanning configs later?
	public int uniqueAlt;
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

	/** Adding a new config means merging contexts with existing configs for
	 *  (s, i, pi, _)
	 *  We use (s,i,pi) as key
	 */
	public boolean add(
		ATNConfig config,
		DoubleKeyMap<PredictionContext,PredictionContext,PredictionContext> mergeCache)
	{
		if ( readonly ) throw new IllegalStateException("This set is readonly");
		if ( config.semanticContext!=SemanticContext.NONE ) {
			hasSemanticContext = true;
		}
		ATNConfig existing = configLookup.absorb(config);
		if ( existing==config ) { // we added this new one
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
		if ( o instanceof ATNConfig ) {
			return configLookup.contains(o);
		}
		return false;
	}

	@Override
	public Iterator<ATNConfig> iterator() {
		return configs.iterator();
	}

	@Override
	public void clear() {
		if ( readonly ) throw new IllegalStateException("This set is readonly");
		configs.clear();
		configLookup.clear();
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
	public Object[] toArray() {
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
	}
}
