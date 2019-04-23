/*
 * Copyright 2019 The ANTLR Project. All rights reserved.
 * Licensed under the BSD-3-Clause license. See LICENSE file in the project root for license information.
 */
package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;

public class IncrementalParserRuleContext extends ParserRuleContext {
    /* Avoid having to recompute depth on every single depth call */
    private int cachedDepth;
    private RuleContext cachedParent;

    // This is an epoch number that can be used to tell which pieces were
    // modified during a given incremental parse. The incremental parser
    // adds the current epoch number to all rule contexts it creates.
    // The epoch number is incremented every time a new parser instance is created.
    public int epoch = -1;

    // The interval that stores the min/max token we touched during
    // lookahead/lookbehind
    private Interval _minMaxTokenIndex = Interval.of(Integer.MAX_VALUE, Integer.MIN_VALUE

    );

    /**
     * Get the minimum token index this rule touched.
     */
    public int getMinTokenIndex() {
        return _minMaxTokenIndex.a;
    }

    /**
     * Get the maximum token index this rule touched.
     */
    public int getMaxTokenIndex() {
        return _minMaxTokenIndex.b;
    }

    /**
     * Get the interval this rule touched.
     */
    public Interval getMinMaxTokenIndex() {
        return _minMaxTokenIndex;
    }

    public void setMinMaxTokenIndex(Interval index) {
        _minMaxTokenIndex = index;
    }

    /**
     * Compute the depth of this context in the parse tree.
     *
     * @note The incremental parser uses a caching implemntation.
     *
     */
    @Override
    public int depth() {
        if (cachedParent != null && cachedParent == this.parent) {
            return cachedDepth;
        }
        int n = 1;
        if (this.parent != null) {
            int parentDepth = this.parent.depth();
            this.cachedParent = this.parent;
            this.cachedDepth = n = parentDepth + 1;
        } else {
            this.cachedDepth = n = 1;
        }
        return n;
    }

    public IncrementalParserRuleContext() {
    }

    public IncrementalParserRuleContext(IncrementalParserRuleContext parent, int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }
}
