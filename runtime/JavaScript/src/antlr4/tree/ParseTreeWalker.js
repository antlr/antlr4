/* Copyright (c) 2012-2022 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import TerminalNode from "./TerminalNode.js";
import ErrorNode from "./ErrorNode.js";

export default class ParseTreeWalker {

    /**
     * Performs a walk on the given parse tree starting at the root and going down recursively
     * with depth-first search. On each node, {@link ParseTreeWalker//enterRule} is called before
     * recursively walking down into child nodes, then
     * {@link ParseTreeWalker//exitRule} is called after the recursive call to wind up.
     * @param listener The listener used by the walker to process grammar rules
     * @param t The parse tree to be walked on
     */
    walk(listener, t) {
        const errorNode = t instanceof ErrorNode ||
            (t.isErrorNode !== undefined && t.isErrorNode());
        if (errorNode) {
            listener.visitErrorNode(t);
        } else if (t instanceof TerminalNode) {
            listener.visitTerminal(t);
        } else {
            this.enterRule(listener, t);
            for (let i = 0; i < t.getChildCount(); i++) {
                const child = t.getChild(i);
                this.walk(listener, child);
            }
            this.exitRule(listener, t);
        }
    }

    /**
     * Enters a grammar rule by first triggering the generic event {@link ParseTreeListener//enterEveryRule}
     * then by triggering the event specific to the given parse tree node
     * @param listener The listener responding to the trigger events
     * @param r The grammar rule containing the rule context
     */
    enterRule(listener, r) {
        const ctx = r.ruleContext;
        listener.enterEveryRule(ctx);
        ctx.enterRule(listener);
    }

    /**
     * Exits a grammar rule by first triggering the event specific to the given parse tree node
     * then by triggering the generic event {@link ParseTreeListener//exitEveryRule}
     * @param listener The listener responding to the trigger events
     * @param r The grammar rule containing the rule context
     */
    exitRule(listener, r) {
        const ctx = r.ruleContext;
        ctx.exitRule(listener);
        listener.exitEveryRule(ctx);
    }
}

ParseTreeWalker.DEFAULT = new ParseTreeWalker();
