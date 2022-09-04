import RuleNode from "../tree/RuleNode";

export default class RuleContext extends RuleNode {
    parentCtx: RuleContext | undefined;

    getRuleContext() : RuleContext;
}
