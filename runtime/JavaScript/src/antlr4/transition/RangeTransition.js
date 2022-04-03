import IntervalSet from "../misc/IntervalSet.js";
import Transition from "./Transition.js";

export default class RangeTransition extends Transition {
    constructor(target, start, stop) {
        super(target);
        this.serializationType = Transition.RANGE;
        this.start = start;
        this.stop = stop;
        this.label = this.makeLabel();
    }

    makeLabel() {
        const s = new IntervalSet();
        s.addRange(this.start, this.stop);
        return s;
    }

    matches(symbol, minVocabSymbol, maxVocabSymbol) {
        return symbol >= this.start && symbol <= this.stop;
    }

    toString() {
        return "'" + String.fromCharCode(this.start) + "'..'" + String.fromCharCode(this.stop) + "'";
    }
}
