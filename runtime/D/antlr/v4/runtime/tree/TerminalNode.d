/*
 * Copyright (c) 2012-2019 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.tree.TerminalNode;

import antlr.v4.runtime.tree.ParseTree;
import antlr.v4.runtime.Token;

/**
 * TODO add interface description
 */
interface TerminalNode : ParseTree
{

    public Token getSymbol();

}
