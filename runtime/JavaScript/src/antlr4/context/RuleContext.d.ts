import {RuleNode} from "../tree";
import {Parser} from "../Parser";

export declare class RuleContext extends RuleNode {
    parentCtx: RuleContext | undefined;
    invokingState: number;

    get ruleContext() : RuleContext;
    toStringTree(ruleNames: string[] | null, recog: Parser) : string;
}
