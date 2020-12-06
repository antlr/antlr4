/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

const RuleContext = require('./RuleContext');
const Tree = require('./tree/Tree');
const INVALID_INTERVAL = Tree.INVALID_INTERVAL;
const TerminalNode = Tree.TerminalNode;
const TerminalNodeImpl = Tree.TerminalNodeImpl;
const ErrorNodeImpl = Tree.ErrorNodeImpl;
const Interval = require("./IntervalSet").Interval;

/**
 * A rule invocation record for parsing.
 *
 *  Contains all of the information about the current rule not stored in the
 *  RuleContext. It handles parse tree children list, Any ATN state
 *  tracing, and the default values available for rule indications:
 *  start, stop, rule index, current alt number, current
 *  ATN state.
 *
 *  Subclasses made for each rule and grammar track the parameters,
 *  return values, locals, and labels specific to that rule. These
 *  are the objects that are returned from rules.
 *
 *  Note text is not an actual field of a rule return value; it is computed
 *  from start and stop using the input stream's toString() method.  I
 *  could add a ctor to this so that we can pass in and store the input
 *  stream, but I'm not sure we want to do that.  It would seem to be undefined
 *  to get the .text property anyway if the rule matches tokens from multiple
 *  input streams.
 *
 *  I do not use getters for fields of objects that are used simply to
 *  group values such as this aggregate.  The getters/setters are there to
 *  satisfy the superclass interface.
 */
class ParserRuleContext extends RuleContext {
	constructor(parent, invokingStateNumber) {
		parent = parent || null;
		invokingStateNumber = invokingStateNumber || null;
		super(parent, invokingStateNumber);
		this.ruleIndex = -1;
		/**
		 * If we are debugging or building a parse tree for a visitor,
		 * we need to track all of the tokens and rule invocations associated
		 * with this rule's context. This is empty for parsing w/o tree constr.
		 * operation because we don't the need to track the details about
		 * how we parse this rule.
		 */
		this.children = null;
		this.start = null;
		this.stop = null;
		/**
		 * The exception that forced this rule to return. If the rule successfully
		 * completed, this is {@code null}.
		 */
		this.exception = null;
	}

	// COPY a ctx (I'm deliberately not using copy constructor)
	copyFrom(ctx) {
		// from RuleContext
		this.parentCtx = ctx.parentCtx;
		this.invokingState = ctx.invokingState;
		this.children = null;
		this.start = ctx.start;
		this.stop = ctx.stop;
		// copy any error nodes to alt label node
		if(ctx.children) {
			this.children = [];
			// reset parent pointer for any error nodes
			ctx.children.map(function(child) {
				if (child instanceof ErrorNodeImpl) {
					this.children.push(child);
					child.parentCtx = this;
				}
			}, this);
		}
	}

	// Double dispatch methods for listeners
	enterRule(listener) {
	}

	exitRule(listener) {
	}

	// Does not set parent link; other add methods do that
	addChild(child) {
		if (this.children === null) {
			this.children = [];
		}
		this.children.push(child);
		return child;
	}

	/** Used by enterOuterAlt to toss out a RuleContext previously added as
	 * we entered a rule. If we have // label, we will need to remove
	 * generic ruleContext object.
	 */
	removeLastChild() {
		if (this.children !== null) {
			this.children.pop();
		}
	}

	addTokenNode(token) {
		const node = new TerminalNodeImpl(token);
		this.addChild(node);
		node.parentCtx = this;
		return node;
	}

	addErrorNode(badToken) {
		const node = new ErrorNodeImpl(badToken);
		this.addChild(node);
		node.parentCtx = this;
		return node;
	}

	getChild(i, type) {
		type = type || null;
		if (this.children === null || i < 0 || i >= this.children.length) {
			return null;
		}
		if (type === null) {
			return this.children[i];
		} else {
			for(let j=0; j<this.children.length; j++) {
				const child = this.children[j];
				if(child instanceof type) {
					if(i===0) {
						return child;
					} else {
						i -= 1;
					}
				}
			}
			return null;
		}
	}

	getToken(ttype, i) {
		if (this.children === null || i < 0 || i >= this.children.length) {
			return null;
		}
		for(let j=0; j<this.children.length; j++) {
			const child = this.children[j];
			if (child instanceof TerminalNode) {
				if (child.symbol.type === ttype) {
					if(i===0) {
						return child;
					} else {
						i -= 1;
					}
				}
			}
		}
		return null;
	}

	getTokens(ttype ) {
		if (this.children=== null) {
			return [];
		} else {
			const tokens = [];
			for(let j=0; j<this.children.length; j++) {
				const child = this.children[j];
				if (child instanceof TerminalNode) {
					if (child.symbol.type === ttype) {
						tokens.push(child);
					}
				}
			}
			return tokens;
		}
	}

	getTypedRuleContext(ctxType, i) {
		return this.getChild(i, ctxType);
	}

	getTypedRuleContexts(ctxType) {
		if (this.children=== null) {
			return [];
		} else {
			const contexts = [];
			for(let j=0; j<this.children.length; j++) {
				const child = this.children[j];
				if (child instanceof ctxType) {
					contexts.push(child);
				}
			}
			return contexts;
		}
	}

	getChildCount() {
		if (this.children=== null) {
			return 0;
		} else {
			return this.children.length;
		}
	}

	getSourceInterval() {
		if( this.start === null || this.stop === null) {
			return INVALID_INTERVAL;
		} else {
			return new Interval(this.start.tokenIndex, this.stop.tokenIndex);
		}
	}
}

RuleContext.EMPTY = new ParserRuleContext();

class InterpreterRuleContext extends ParserRuleContext {
	constructor(parent, invokingStateNumber, ruleIndex) {
		super(parent, invokingStateNumber);
		this.ruleIndex = ruleIndex;
	}
}

module.exports = ParserRuleContext;
