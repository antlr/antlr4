/*
 * Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include "tree/TerminalNodeImpl.h"

namespace antlr4 {
namespace tree {

/** Track text of hidden channel tokens to left and right of terminal node
 *  according to the rules borrowed from Roslyn on trivia:
 *
 "In general, a token owns any trivia after it on the same line up to
 the next token. Any trivia after that line is associated with the
 following token. The first token in the source file gets all the
 initial trivia, and the last sequence of trivia in the file is
 tacked onto the end-of-file token, which otherwise has zero width."

 *  These rules are implemented by in {@link #collectHiddenTokens}. It
 *  deviates from Roslyn rules in that final whitespace is added to
 *  last real token not the EOF token.
 *
 *  ErrorNodes act just like regular token leaves except that tokens
 *  conjured up during error recovery for missing tokens never have
 *  leading/trailing hidden tokens. Hidden tokens are associated
 *  with real tokens on either side of imaginary token injected
 *  for the missing token.
 *
 *  An empty input is a special case. If the start rule has a reference to EOF,
 *  then the tree will also have a terminal node for that. If the input
 *  is empty except for whitespace or comments, then the EOF terminal node
 *  has these as hiddenLeft as a special case.
 *
 *  If there is no reference to EOF, then the parse tree is a single internal
 *  node for the start rule. Consequently there would be no place to stick the
 *  whitespace or comments; in effect, those will not get added to the tree.
 *
 *  To use this class, override these methods to create
 *  TerminalNodeWithHidden nodes:
 *  {@link org.antlr.v4.runtime.Parser#createErrorNode(ParserRuleContext, Token)} and
 *  {@link org.antlr.v4.runtime.Parser#createTerminalNode(ParserRuleContext, Token)}.
 *
 *  Example:
 *
 * class MyCalcParser extends CalcParser(tokens) {
 *    public CalcParser(TokenStream tokens) { super(tokens); }
 *    @Override
 *    public TerminalNode createTerminalNode(ParserRuleContext parent, Token t) {
 *        return new TerminalNodeWithHidden(tokens, -1, t);
 *    }
 *    @Override
 *    public ErrorNode createErrorNode(ParserRuleContext parent, Token t) {
 *	      return new ErrorNodeWithHidden(tokens, -1, t);
 *    }
 *  };
 *
 * @since 4.7
 */
  class ANTLR4CPP_PUBLIC  TerminalNodeWithHidden : public TerminalNodeImpl {
  public:
    /** Construct a node with left/right hidden tokens on a channel,
     *  or all hidden tokens if channel==-1.
     */
    TerminalNodeWithHidden(BufferedTokenStream *tokens, int channel, Token *symbol);

  protected:
    /** Hidden tokens before this node's token.  leading[0]
     *  is the furthest token from this node's token.
     */
    std::vector<Token *> _leading;

    /** Hidden tokens after this node's token.  trailing[0]
     *  is the first token after this node's token.
     */
    std::vector<Token *> _trailing;

    void collectHiddenTokens(BufferedTokenStream *tokens, int channel, Token *symbol);

    std::vector<Token *> getLeadingHidden() const;
    std::vector<Token *> getTrailingHidden() const;
    void setLeadingHidden(std::vector<Token *> const& hiddenLeft);
    void setTrailingHidden(std::vector<Token *> const& hiddenRight);

    virtual std::string getText() const override;
  };

} // namespace tree
} // namespace antlr4
