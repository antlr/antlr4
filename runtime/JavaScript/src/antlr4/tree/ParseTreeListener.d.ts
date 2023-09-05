/* Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import ParserRuleContext from "../context/ParserRuleContext.js";
import ErrorNode from "./ErrorNode.js";
import TerminalNode from "./TerminalNode.js";

export declare abstract class ParseTreeListener {
    visitTerminal(node: TerminalNode): void;
    visitErrorNode(node: ErrorNode): void;
    enterEveryRule(ctx: ParserRuleContext): void;
    exitEveryRule(ctx: ParserRuleContext): void;
}

export default ParseTreeListener;
