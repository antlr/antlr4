import RuleContext from "./RuleContext";
import Token from "../Token";
import RecognitionException from "../error/RecognitionException";
import ParseTree from "../tree/ParseTree";
import TerminalNode from "../tree/TerminalNode";

export default class ParserRuleContext extends RuleContext {
    start: Token;
    stop: Token | undefined;
    parentCtx: ParserRuleContext | undefined;
    exception?: RecognitionException;

    constructor();
    constructor(parent: ParserRuleContext | undefined, invokingStateNumber: number | undefined)
    copyFrom(ctx: ParserRuleContext): void;
    getChildCount() : number;
    getChild(i: number) : ParseTree;
    getToken(ttype: number, i: number): TerminalNode;
    getTypedRuleContext<T extends ParserRuleContext>(ctxType: { new (parent: ParserRuleContext, invokingState: number) : T}, i: number): T;
    getTypedRuleContexts<T extends ParserRuleContext>(ctxType: { new (parent: ParserRuleContext, invokingState: number) : T}): T[];
}
