import {RuleContext} from "./RuleContext";
import {ParseTree, TerminalNode} from "../tree";
import {RecognitionException} from "../error";
import {Token} from "../Token";
import {Parser} from "../Parser";

export declare class ParserRuleContext extends RuleContext {
    start: Token;
    stop: Token | undefined;
    children: ParseTree[] | null;
    parentCtx: ParserRuleContext | undefined;
    exception?: RecognitionException;
    parser?: Parser;

    constructor(parent?: ParserRuleContext, invokingStateNumber?: number);
    copyFrom(ctx: ParserRuleContext): void;
    getChildCount() : number;
    getChild(i: number) : ParseTree;
    getToken(ttype: number, i: number): TerminalNode;
    getTokens(ttype: number): TerminalNode[];
    getTypedRuleContext<T extends ParserRuleContext, P extends Parser>(ctxType: { new (parser?: P, parent?: ParserRuleContext, invokingState?: number, ...args: any[]) : T}, i: number): T;
    getTypedRuleContexts<T extends ParserRuleContext, P extends Parser>(ctxType: { new (parser?: P, parent?: ParserRuleContext, invokingState?: number, ...args: any[]) : T}): T[];
}
