/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

const ATN = require('./ATN');
const Utils = require('./../Utils');
const {SemanticContext} = require('./SemanticContext');
const {merge} = require('./../PredictionContext');

function hashATNConfig(c) {
	return c.hashCodeForConfigSet();
}

function equalATNConfigs(a, b) {
	if ( a===b ) {
		return true;
	} else if ( a===null || b===null ) {
		return false;
	} else
       return a.equalsForConfigSet(b);
 }

/**
 * Specialized {@link Set}{@code <}{@link ATNConfig}{@code >} that can track
 * info about the set, with support for combining similar configurations using a
 * graph-structured stack
 */
class ATNConfigSet {
	constructor(fullCtx) {
		/**
		 * The reason that we need this is because we don't want the hash map to use
		 * the standard hash code and equals. We need all configurations with the
		 * same
		 * {@code (s,i,_,semctx)} to be equal. Unfortunately, this key effectively
		 * doubles
		 * the number of objects associated with ATNConfigs. The other solution is
		 * to
		 * use a hash table that lets us specify the equals/hashcode operation.
		 * All configs but hashed by (s, i, _, pi) not including context. Wiped out
		 * when we go readonly as this set becomes a DFA state
		 */
		this.configLookup = new Utils.Set(hashATNConfig, equalATNConfigs);
		/**
		 * Indicates that this configuration set is part of a full context
		 * LL prediction. It will be used to determine how to merge $. With SLL
		 * it's a wildcard whereas it is not for LL context merge
		 */
		this.fullCtx = fullCtx === undefined ? true : fullCtx;
		/**
		 * Indicates that the set of configurations is read-only. Do not
		 * allow any code to manipulate the set; DFA states will point at
		 * the sets and they must not change. This does not protect the other
		 * fields; in particular, conflictingAlts is set after
		 * we've made this readonly
		 */
		this.readOnly = false;
		// Track the elements as they are added to the set; supports get(i)///
		this.configs = [];

		// TODO: these fields make me pretty uncomfortable but nice to pack up info
		// together, saves recomputation
		// TODO: can we track conflicts as they are added to save scanning configs
		// later?
		this.uniqueAlt = 0;
		this.conflictingAlts = null;

		/**
		 * Used in parser and lexer. In lexer, it indicates we hit a pred
		 * while computing a closure operation. Don't make a DFA state from this
		 */
		this.hasSemanticContext = false;
		this.dipsIntoOuterContext = false;

		this.cachedHashCode = -1;
	}

	/**
	 * Adding a new config means merging contexts with existing configs for
	 * {@code (s, i, pi, _)}, where {@code s} is the
	 * {@link ATNConfig//state}, {@code i} is the {@link ATNConfig//alt}, and
	 * {@code pi} is the {@link ATNConfig//semanticContext}. We use
	 * {@code (s,i,pi)} as key.
	 *
	 * <p>This method updates {@link //dipsIntoOuterContext} and
	 * {@link //hasSemanticContext} when necessary.</p>
	 */
	add(config, mergeCache) {
		if (mergeCache === undefined) {
			mergeCache = null;
		}
		if (this.readOnly) {
			throw "This set is readonly";
		}
		if (config.semanticContext !== SemanticContext.NONE) {
			this.hasSemanticContext = true;
		}
		if (config.reachesIntoOuterContext > 0) {
			this.dipsIntoOuterContext = true;
		}
		const existing = this.configLookup.add(config);
		if (existing === config) {
			this.cachedHashCode = -1;
			this.configs.push(config); // track order here
			return true;
		}
		// a previous (s,i,pi,_), merge with it and save result
		const rootIsWildcard = !this.fullCtx;
		const merged = merge(existing.context, config.context, rootIsWildcard, mergeCache);
		/**
		 * no need to check for existing.context, config.context in cache
		 * since only way to create new graphs is "call rule" and here. We
		 * cache at both places
		 */
		existing.reachesIntoOuterContext = Math.max( existing.reachesIntoOuterContext, config.reachesIntoOuterContext);
		// make sure to preserve the precedence filter suppression during the merge
		if (config.precedenceFilterSuppressed) {
			existing.precedenceFilterSuppressed = true;
		}
		existing.context = merged; // replace context; no need to alt mapping
		return true;
	}

	getStates() {
		const states = new Utils.Set();
		for (let i = 0; i < this.configs.length; i++) {
			states.add(this.configs[i].state);
		}
		return states;
	}

	getPredicates() {
		const preds = [];
		for (let i = 0; i < this.configs.length; i++) {
			const c = this.configs[i].semanticContext;
			if (c !== SemanticContext.NONE) {
				preds.push(c.semanticContext);
			}
		}
		return preds;
	}

	optimizeConfigs(interpreter) {
		if (this.readOnly) {
			throw "This set is readonly";
		}
		if (this.configLookup.length === 0) {
			return;
		}
		for (let i = 0; i < this.configs.length; i++) {
			const config = this.configs[i];
			config.context = interpreter.getCachedContext(config.context);
		}
	}

	addAll(coll) {
		for (let i = 0; i < coll.length; i++) {
			this.add(coll[i]);
		}
		return false;
	}

	equals(other) {
		return this === other ||
			(other instanceof ATNConfigSet &&
			Utils.equalArrays(this.configs, other.configs) &&
			this.fullCtx === other.fullCtx &&
			this.uniqueAlt === other.uniqueAlt &&
			this.conflictingAlts === other.conflictingAlts &&
			this.hasSemanticContext === other.hasSemanticContext &&
			this.dipsIntoOuterContext === other.dipsIntoOuterContext);
	}

	hashCode() {
		const hash = new Utils.Hash();
		hash.update(this.configs);
		return hash.finish();
	}

	updateHashCode(hash) {
		if (this.readOnly) {
			if (this.cachedHashCode === -1) {
				this.cachedHashCode = this.hashCode();
			}
			hash.update(this.cachedHashCode);
		} else {
			hash.update(this.hashCode());
		}
	}

	isEmpty() {
		return this.configs.length === 0;
	}

	contains(item) {
		if (this.configLookup === null) {
			throw "This method is not implemented for readonly sets.";
		}
		return this.configLookup.contains(item);
	}

	containsFast(item) {
		if (this.configLookup === null) {
			throw "This method is not implemented for readonly sets.";
		}
		return this.configLookup.containsFast(item);
	}

	clear() {
		if (this.readOnly) {
			throw "This set is readonly";
		}
		this.configs = [];
		this.cachedHashCode = -1;
		this.configLookup = new Utils.Set();
	}

	setReadonly(readOnly) {
		this.readOnly = readOnly;
		if (readOnly) {
			this.configLookup = null; // can't mod, no need for lookup cache
		}
	}

	toString() {
		return Utils.arrayToString(this.configs) +
			(this.hasSemanticContext ? ",hasSemanticContext=" + this.hasSemanticContext : "") +
			(this.uniqueAlt !== ATN.INVALID_ALT_NUMBER ? ",uniqueAlt=" + this.uniqueAlt : "") +
			(this.conflictingAlts !== null ? ",conflictingAlts=" + this.conflictingAlts : "") +
			(this.dipsIntoOuterContext ? ",dipsIntoOuterContext" : "");
	}

	get items(){
		return this.configs;
	}

	get length(){
		return this.configs.length;
	}
}


class OrderedATNConfigSet extends ATNConfigSet {
	constructor() {
		super();
		this.configLookup = new Utils.Set();
	}
}

module.exports = {
	ATNConfigSet,
	OrderedATNConfigSet
}
