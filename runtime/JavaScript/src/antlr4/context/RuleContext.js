/* Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import RuleNode from '../tree/RuleNode.js';
import Interval from '../misc/Interval.js';
import Trees from '../tree/Trees.js';

export default class RuleContext extends RuleNode {
    /** A rule context is a record of a single rule invocation. It knows
     * which context invoked it, if any. If there is no parent context, then
     * naturally the invoking state is not valid.  The parent link
     * provides a chain upwards from the current rule invocation to the root
     * of the invocation tree, forming a stack. We actually carry no
     * information about the rule associated with this context (except
     * when parsing). We keep only the state number of the invoking state from
     * the ATN submachine that invoked this. Contrast this with the s
     * pointer inside ParserRuleContext that tracks the current state
     * being "executed" for the current rule.
     *
     * The parent contexts are useful for computing lookahead sets and
     * getting error information.
     *
     * These objects are used during parsing and prediction.
     * For the special case of parsers, we use the subclass
     * ParserRuleContext.
     *
     * @see ParserRuleContext
     */
    constructor(parent, invokingState) {
        // What context invoked this rule?
        super();
        this.parentCtx = parent || null;
        /**
         * What state invoked the rule associated with this context?
         * The "return address" is the followState of invokingState
         * If parent is null, this should be -1.
         */
        this.invokingState = invokingState || -1;
    }

    depth() {
        let n = 0;
        let p = this;
        while (p !== null) {
            p = p.parentCtx;
            n += 1;
        }
        return n;
    }

    /**
     * A context is empty if there is no invoking state; meaning nobody call
     * current context.
     */
    isEmpty() {
        return this.invokingState === -1;
    }

// satisfy the ParseTree / SyntaxTree interface
    getSourceInterval() {
        return Interval.INVALID_INTERVAL;
    }

    getRuleContext() {
        return this;
    }

    getPayload() {
        return this;
    }

    /**
     * Return the combined text of all child nodes. This method only considers
     * tokens which have been added to the parse tree.
     * <p>
     * Since tokens on hidden channels (e.g. whitespace or comments) are not
     * added to the parse trees, they will not appear in the output of this
     * method.
     */
    getText() {
        if (this.getChildCount() === 0) {
            return "";
        } else {
            return this.children.map(function (child) {
                return child.getText();
            }).join("");
        }
    }

    /**
     * For rule associated with this parse tree internal node, return
     * the outer alternative number used to match the input. Default
     * implementation does not compute nor store this alt num. Create
     * a subclass of ParserRuleContext with backing field and set
     * option contextSuperClass.
     * to set it.
     */
    getAltNumber() {
        // use constant value of ATN.INVALID_ALT_NUMBER to avoid circular dependency
        return 0;
    }

    /**
     * Set the outer alternative number for this context node. Default
     * implementation does nothing to avoid backing field overhead for
     * trees that don't need it.  Create
     * a subclass of ParserRuleContext with backing field and set
     * option contextSuperClass.
     */
    setAltNumber(altNumber) {
    }

    getChild(i) {
        return null;
    }

    getChildCount() {
        return 0;
    }

    accept(visitor) {
        return visitor.visitChildren(this);
    }

    /**
     * Print out a whole tree, not just a node, in LISP format
     * (root child1 .. childN). Print just a node if this is a leaf.
     */
    toStringTree(ruleNames, recog) {
        return Trees.toStringTree(this, ruleNames, recog);
    }

    toString(ruleNames, stop) {
        ruleNames = ruleNames || null;
        stop = stop || null;
        let p = this;
        let s = "[";
        while (p !== null && p !== stop) {
            if (ruleNames === null) {
                if (!p.isEmpty()) {
                    s += p.invokingState;
                }
            } else {
                const ri = p.ruleIndex;
                const ruleName = (ri >= 0 && ri < ruleNames.length) ? ruleNames[ri]
                    : "" + ri;
                s += ruleName;
            }
            if (p.parentCtx !== null && (ruleNames !== null || !p.parentCtx.isEmpty())) {
                s += " ";
            }
            p = p.parentCtx;
        }
        s += "]";
        return s;
    }
}
