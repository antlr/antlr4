/* Copyright (c) 2012-2022 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import Tree from "./Tree.js";

export default class SyntaxTree extends Tree {

    leafContextWithToken(token) {
        for(let i=0; i < this.getChildCount(); i++) {
            const child = this.getChild(i);
            const tokens = child.getSourceInterval();
            // skip empty interval
            if(tokens.stop < tokens.start)
                continue;
            // have we gone past token ?
            if(tokens.start > token.tokenIndex)
                break;
            if (tokens.stop >= token.tokenIndex) {
                const context = child.leafContextWithToken(token);
                if(context!=null)
                    return context;
            }
        }
        if(this.ruleContext)
            return this.ruleContext;
        else
            return null;
    }
}
