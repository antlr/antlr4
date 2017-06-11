/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include "ErrorNodeWithHidden.h"

using namespace antlr4;
using namespace antlr4::tree;

ErrorNodeWithHidden::ErrorNodeWithHidden(BufferedTokenStream *tokens, int channel, Token *symbol)
: TerminalNodeWithHidden(tokens, channel, symbol) {
};
