import { Parser } from "../Parser.js";
import { ParseTree } from "./ParseTree.js";
import { Tree } from "./Tree.js";

export default Trees;
declare namespace Trees {
    function toStringTree(tree: Tree, ruleNames: string[], recog: Parser): string;
    function getNodeText(t: Tree, ruleNames: string[], recog: Parser): string;
    function getChildren(t: Tree): Tree[];
    function getAncestors(t: Tree): Tree[];
    function findAllTokenNodes(t: ParseTree, ttype: number): ParseTree[];
    function findAllRuleNodes(t: ParseTree, ruleIndex: number): ParseTree[];
    function findAllNodes(t: ParseTree, index: number, findTokens: boolean): ParseTree[];
    function descendants(t: ParseTree): ParseTree[];
}
