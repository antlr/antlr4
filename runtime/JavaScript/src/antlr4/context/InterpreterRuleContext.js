import ParserRuleContext from "./ParserRuleContext.js";

export default class InterpreterRuleContext extends ParserRuleContext {
    constructor(parent, invokingStateNumber, ruleIndex) {
        super(parent, invokingStateNumber);
        this.ruleIndex = ruleIndex;
    }
}
