import {RuleNode} from "../tree/index.js";
import {Parser} from "../Parser.js";

export declare class RuleContext extends RuleNode {
    parentCtx: RuleContext | undefined;
    invokingState: number;

    get ruleContext() : RuleContext;
    toStringTree(ruleNames: string[] | null, recog: Parser) : string;
}
