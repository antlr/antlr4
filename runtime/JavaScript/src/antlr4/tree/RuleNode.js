import ParseTree from "./ParseTree.js";

export default class RuleNode extends ParseTree {

    getRuleContext(){
        throw new Error("missing interface implementation")
    }
}
