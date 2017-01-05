/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


/** The default mechanism for creating tokens. It's used by default in Lexer and
 *  the error handling strategy (to create missing tokens).  Notifying the parser
 *  of a new factory means that it notifies it's token source and error strategy.
 */

public protocol TokenFactory {

    //typealias Symbol
    /** This is the method used to create tokens in the lexer and in the
     *  error handling strategy. If text!=null, than the start and stop positions
     *  are wiped to -1 in the text override is set in the CommonToken.
     */
    func create(_ source: (TokenSource?, CharStream?), _ type: Int, _ text: String?,
                _ channel: Int, _ start: Int, _ stop: Int,
                _ line: Int, _ charPositionInLine: Int) -> Token
    /** Generically useful */
    func create(_ type: Int, _ text: String) -> Token

}
