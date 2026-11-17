import {ParseTreeVisitor, RuleNode} from "../tree/index.js";
import { Parser } from "../Parser.js";

export declare class RuleContext extends RuleNode {
    parentCtx: RuleContext | undefined;
    invokingState: number;

    accept<T>(visitor: ParseTreeVisitor<T>): T;
    get ruleContext() : RuleContext;
    toStringTree(ruleNames: string[] | null, recog: Parser) : string;
}
