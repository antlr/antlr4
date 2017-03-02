/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

public class TerminalNode: ParseTree {
    public func getSymbol() -> Token? {
        RuntimeException(" must overriden !")
        fatalError()

    }
    
    /** Set the parent for this leaf node.
     *
     *  Technically, this is not backward compatible as it changes
     *  the interface but no one was able to create custom
     *  TerminalNodes anyway so I'm adding as it improves internal
     *  code quality.
     *
     *  @since 4.7
     */
    public func setParent(_ parent: RuleContext) {
        RuntimeException(" must overriden !")
        fatalError()
    }
}
