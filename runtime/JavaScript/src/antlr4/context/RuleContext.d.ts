import RuleNode from "../tree/RuleNode";
import Parser from "../Parser";

export default class RuleContext extends RuleNode {
    parentCtx: RuleContext | undefined;
    invokingState: number;

    get ruleContext() : RuleContext;
    toStringTree(ruleNames: string[] | null, recog: Parser) : string;
}
