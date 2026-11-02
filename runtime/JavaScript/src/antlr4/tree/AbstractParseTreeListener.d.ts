import { ParserRuleContext } from "../context";
import { ErrorNode } from "./ErrorNode";
import { ParseTreeListener } from "./ParseTreeListener";
import { TerminalNode } from "./TerminalNode";

/* Copyright (c) 2012-2022 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
export declare abstract class AbstractParseTreeListener implements ParseTreeListener {
    visitTerminal(node: TerminalNode): void;
    visitErrorNode(node: ErrorNode): void;
    enterEveryRule(ctx: ParserRuleContext): void;
    exitEveryRule(ctx: ParserRuleContext): void;
}