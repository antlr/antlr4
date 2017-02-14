/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


public protocol WritableToken: Token {
    func setText(_ text: String)

    func setType(_ ttype: Int)

    func setLine(_ line: Int)

    func setCharPositionInLine(_ pos: Int)

    func setChannel(_ channel: Int)

    func setTokenIndex(_ index: Int)
}
