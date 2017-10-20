/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


open class RuleNode: ParseTree {
    open func getRuleContext() -> RuleContext {
        fatalError(#function + " must be overridden")
    }
}
