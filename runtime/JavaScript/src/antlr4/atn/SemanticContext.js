/* Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import equalArrays from "../utils/equalArrays.js";
import HashCode from "../misc/HashCode.js";

/**
 * A tree structure used to record the semantic context in which
 * an ATN configuration is valid.  It's either a single predicate,
 * a conjunction {@code p1&&p2}, or a sum of products {@code p1||p2}.
 *
 * <p>I have scoped the {@link AND}, {@link OR}, and {@link Predicate} subclasses of
 * {@link SemanticContext} within the scope of this outer class.</p>
 */
export default class SemanticContext {

	hashCode() {
		const hash = new HashCode();
		this.updateHashCode(hash);
		return hash.finish();
	}

	/**
	 * For context independent predicates, we evaluate them without a local
	 * context (i.e., null context). That way, we can evaluate them without
	 * having to create proper rule-specific context during prediction (as
	 * opposed to the parser, which creates them naturally). In a practical
	 * sense, this avoids a cast exception from RuleContext to myruleContext.
	 *
	 * <p>For context dependent predicates, we must pass in a local context so that
	 * references such as $arg evaluate properly as _localctx.arg. We only
	 * capture context dependent predicates in the context in which we begin
	 * prediction, so we passed in the outer context here in case of context
	 * dependent predicate evaluation.</p>
	 */
	evaluate(parser, outerContext) {}

	/**
	 * Evaluate the precedence predicates for the context and reduce the result.
	 *
	 * @param parser The parser instance.
	 * @param outerContext The current parser context object.
	 * @return The simplified semantic context after precedence predicates are
	 * evaluated, which will be one of the following values.
	 * <ul>
	 * <li>{@link //NONE}: if the predicate simplifies to {@code true} after
	 * precedence predicates are evaluated.</li>
	 * <li>{@code null}: if the predicate simplifies to {@code false} after
	 * precedence predicates are evaluated.</li>
	 * <li>{@code this}: if the semantic context is not changed as a result of
	 * precedence predicate evaluation.</li>
	 * <li>A non-{@code null} {@link SemanticContext}: the new simplified
	 * semantic context after precedence predicates are evaluated.</li>
	 * </ul>
	 */
	evalPrecedence(parser, outerContext) {
		return this;
	}

	static andContext(a, b) {
		if (a === null || a === SemanticContext.NONE) {
			return b;
		}
		if (b === null || b === SemanticContext.NONE) {
			return a;
		}
		const result = new AND(a, b);
		if (result.opnds.length === 1) {
			return result.opnds[0];
		} else {
			return result;
		}
	}

	static orContext(a, b) {
		if (a === null) {
			return b;
		}
		if (b === null) {
			return a;
		}
		if (a === SemanticContext.NONE || b === SemanticContext.NONE) {
			return SemanticContext.NONE;
		}
		const result = new OR(a, b);
		if (result.opnds.length === 1) {
			return result.opnds[0];
		} else {
			return result;
		}
	}
}



class AND extends SemanticContext {
	/**
	 * A semantic context which is true whenever none of the contained contexts
	 * is false
	 */
	constructor(a, b) {
		super();
		const operands = merge(
			a instanceof AND ? a.opnds : [a],
			b instanceof AND ? b.opnds : [b]);
		// interested in the transition with the lowest precedence
		keepOnePrecedencePredicate(operands, true);
		this.opnds = operands;
		this.cachedHashCode = HashCode.hashStuff(this.opnds, "AND");
	}

	equals(other) {
		if (this === other) {
			return true;
		} else if (!(other instanceof AND)) {
			return false;
		} else {
			return this.cachedHashCode === other.cachedHashCode && equalArrays(this.opnds, other.opnds);
		}
	}

	hashCode() {
		return this.cachedHashCode;
	}

	updateHashCode(hash) {
		hash.update(this.cachedHashCode);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The evaluation of predicates by this context is short-circuiting, but
	 * unordered.</p>
	 */
	evaluate(parser, outerContext) {
		for (let i = 0; i < this.opnds.length; i++) {
			if (!this.opnds[i].evaluate(parser, outerContext)) {
				return false;
			}
		}
		return true;
	}

	evalPrecedence(parser, outerContext) {
		let differs = false;
		const operands = [];
		for (let i = 0; i < this.opnds.length; i++) {
			const context = this.opnds[i];
			const evaluated = context.evalPrecedence(parser, outerContext);
			differs |= (evaluated !== context);
			if (evaluated === null) {
				// The AND context is false if any element is false
				return null;
			} else if (evaluated !== SemanticContext.NONE) {
				// Reduce the result by skipping true elements
				operands.push(evaluated);
			}
		}
		if (!differs) {
			return this;
		}
		if (operands.length === 0) {
			// all elements were true, so the AND context is true
			return SemanticContext.NONE;
		}
		let result = null;
		operands.map(function(o) {
			result = result === null ? o : SemanticContext.andContext(result, o);
		});
		return result;
	}

	toString() {
		const s = this.opnds.map(o => o.toString());
		return (s.length > 3 ? s.slice(3) : s).join("&&");
	}
}


class OR extends SemanticContext {
	/**
	 * A semantic context which is true whenever at least one of the contained
	 * contexts is true
	 */
	constructor(a, b) {
		super();
		const operands = merge(
			a instanceof OR ? a.opnds : [a],
			b instanceof OR ? b.opnds : [b]);
		// interested in the transition with the highest precedence
		keepOnePrecedencePredicate(operands, false);
		this.opnds = operands;
		this.cachedHashCode = HashCode.hashStuff(this.opnds, "OR");
	}

	equals(other) {
		if (this === other) {
			return true;
		} else if (!(other instanceof OR)) {
			return false;
		} else {
			return this.cachedHashCode === other.cachedHashCode && equalArrays(this.opnds, other.opnds);
		}
	}

	hashCode() {
		return this.cachedHashCode;
	}

	updateHashCode(hash) {
		hash.update(this.cachedHashCode);
	}

	/**
	 * <p>
	 * The evaluation of predicates by this context is short-circuiting, but
	 * unordered.</p>
	 */
	evaluate(parser, outerContext) {
		for (let i = 0; i < this.opnds.length; i++) {
			if (this.opnds[i].evaluate(parser, outerContext)) {
				return true;
			}
		}
		return false;
	}

	evalPrecedence(parser, outerContext) {
		let differs = false;
		const operands = [];
		for (let i = 0; i < this.opnds.length; i++) {
			const context = this.opnds[i];
			const evaluated = context.evalPrecedence(parser, outerContext);
			differs |= (evaluated !== context);
			if (evaluated === SemanticContext.NONE) {
				// The OR context is true if any element is true
				return SemanticContext.NONE;
			} else if (evaluated !== null) {
				// Reduce the result by skipping false elements
				operands.push(evaluated);
			}
		}
		if (!differs) {
			return this;
		}
		if (operands.length === 0) {
			// all elements were false, so the OR context is false
			return null;
		}
		const result = null;
		operands.map(function(o) {
			return result === null ? o : SemanticContext.orContext(result, o);
		});
		return result;
	}

	toString() {
		const s = this.opnds.map(o => o.toString());
		return (s.length > 3 ? s.slice(3) : s).join("||");
	}
}

/**
 * Orders contexts by kind and then by their fields, so an operator holds
 * its operands in one order whichever order they were combined in.
 */
function compare(a, b) {
	const k = kind(a) - kind(b);
	if (k !== 0) {
		return k;
	}
	if (a instanceof SemanticContext.Predicate) {
		if (a.ruleIndex !== b.ruleIndex) {
			return a.ruleIndex - b.ruleIndex;
		}
		if (a.predIndex !== b.predIndex) {
			return a.predIndex - b.predIndex;
		}
		return Number(a.isCtxDependent) - Number(b.isCtxDependent);
	}
	if (a instanceof SemanticContext.PrecedencePredicate) {
		return a.precedence - b.precedence;
	}
	if (a instanceof AND || a instanceof OR) {
		if (a.opnds.length !== b.opnds.length) {
			return a.opnds.length - b.opnds.length;
		}
		for (let i = 0; i < a.opnds.length; i++) {
			const c = compare(a.opnds[i], b.opnds[i]);
			if (c !== 0) {
				return c;
			}
		}
		return 0;
	}
	return a.hashCode() - b.hashCode();
}

/**
 * Merges two operand arrays, each already in {@link compare} order, into
 * one array in that order without duplicates.
 */
function merge(x, y) {
	const merged = [];
	let i = 0;
	let j = 0;
	while (i < x.length && j < y.length) {
		const c = compare(x[i], y[j]);
		// compare orders contexts of other kinds by hash code, so it can tie two unequal ones
		if (c < 0 || c === 0 && !x[i].equals(y[j])) {
			merged.push(x[i++]);
		} else if (c > 0) {
			merged.push(y[j++]);
		} else {
			merged.push(x[i++]);
			j++;
		}
	}
	return merged.concat(x.slice(i), y.slice(j));
}

/**
 * Removes all precedence predicates but the one with the lowest or highest
 * precedence. They sort next to each other, by precedence.
 */
function keepOnePrecedencePredicate(operands, lowest) {
	let first = -1;
	let last = -1;
	for (let i = 0; i < operands.length; i++) {
		if (operands[i] instanceof SemanticContext.PrecedencePredicate) {
			if (first < 0) {
				first = i;
			}
			last = i;
		}
	}
	if (first >= 0) {
		operands.splice(first, last - first + 1, operands[lowest ? first : last]);
	}
}

function kind(context) {
	if (context instanceof SemanticContext.Predicate) {
		return 0;
	} else if (context instanceof SemanticContext.PrecedencePredicate) {
		return 1;
	} else if (context instanceof AND) {
		return 2;
	} else if (context instanceof OR) {
		return 3;
	} else {
		return 4;
	}
}
