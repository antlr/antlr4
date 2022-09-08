import RuleContext from "./RuleContext";
import Token from "../Token";
import RecognitionException from "../error/RecognitionException";
import ParseTree from "../tree/ParseTree";
import TerminalNode from "../tree/TerminalNode";
import Parser from "../Parser";

export default class ParserRuleContext extends RuleContext {
    start: Token;
    stop: Token | undefined;
    parentCtx: ParserRuleContext | undefined;
    exception?: RecognitionException;
    parser?: Parser;

    constructor(parent?: ParserRuleContext, invokingStateNumber?: number);
    copyFrom(ctx: ParserRuleContext): void;
    getChildCount() : number;
    getChild(i: number) : ParseTree;
    getToken(ttype: number, i: number): TerminalNode;
    getTokens(ttype: number): TerminalNode[];
    getTypedRuleContext<T extends ParserRuleContext>(ctxType: { new (parser?: Parser, parent?: ParserRuleContext, invokingState?: number, ...args: any[]) : T}, i: number): T;
    getTypedRuleContexts<T extends ParserRuleContext>(ctxType: { new (parser?: Parser, parent?: ParserRuleContext, invokingState?: number, ...args: any[]) : T}): T[];
}
