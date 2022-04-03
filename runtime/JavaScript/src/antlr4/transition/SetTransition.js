// A transition containing a set of values.
import IntervalSet from "../misc/IntervalSet.js";
import Token from '../Token.js';
import Transition from "./Transition.js";

export default class SetTransition extends Transition {
    constructor(target, set) {
        super(target);
        this.serializationType = Transition.SET;
        if (set !==undefined && set !==null) {
            this.label = set;
        } else {
            this.label = new IntervalSet();
            this.label.addOne(Token.INVALID_TYPE);
        }
    }

    matches(symbol, minVocabSymbol, maxVocabSymbol) {
        return this.label.contains(symbol);
    }

    toString() {
        return this.label.toString();
    }
}
