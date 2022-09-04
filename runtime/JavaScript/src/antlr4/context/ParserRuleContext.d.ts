import RuleContext from "./RuleContext";
import Token from "../Token";
import {RecognitionException} from "../error";
import ParseTree from "../tree/ParseTree";

export default class ParserRuleContext extends RuleContext {
    _start: Token;
    _stop: Token | undefined;
    exception?: RecognitionException;

    constructor();
    constructor(parent: ParserRuleContext | undefined, invokingStateNumber: number | undefined)
    getChildCount() : number;
    getChild(i: number) : ParseTree;
}
