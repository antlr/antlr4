/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { Parser } from "../Parser"
import { Tree, ParseTree } from "./Tree"

export namespace Trees {
    export function toStringTree(tree: Tree): string
    export function toStringTree(tree: Tree, ruleNames: Array<string>): string
    export function toStringTree(
        tree: Tree, ruleNames: undefined, recog: Parser
    ): string

    export function getNodeText(t: Tree, ruleNames: Array<string>): string
    export function getNodeText(
        t: Tree, ruleNames: undefined, recog: Parser
    ): string

    export function getChildren(t: Tree): Array<Tree>
    export function getAncestors(t: Tree): Array<Tree>
    export function findAllTokenNodes(
        t: ParseTree, ttype: number
    ): Array<ParseTree>
    export function findAllRuleNodes(
        t: ParseTree, ruleIndex: number
    ): Array<ParseTree>
    export function findAllNodes(
        t: ParseTree, index: number, findTokens: boolean
    ): Array<ParseTree>
    export function _findAllNodes(
        t: ParseTree,
        index: number,
        findTokens: boolean,
        nodes: Array<ParseTree>
    ): void
    export function descendants(t: ParseTree): Array<ParseTree>
}
