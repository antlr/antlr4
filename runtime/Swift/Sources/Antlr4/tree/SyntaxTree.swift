/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


/// A tree that knows about an interval in a token stream
/// is some kind of syntax tree. Subinterfaces distinguish
/// between parse trees and other kinds of syntax trees we might want to create.
/// 

public protocol SyntaxTree: Tree {
    /// 
    /// Return an _org.antlr.v4.runtime.misc.Interval_ indicating the index in the
    /// _org.antlr.v4.runtime.TokenStream_ of the first and last token associated with this
    /// subtree. If this node is a leaf, then the interval represents a single
    /// token.
    /// 
    /// If source interval is unknown, this returns _org.antlr.v4.runtime.misc.Interval#INVALID_.
    /// 

    func getSourceInterval() -> Interval
}
