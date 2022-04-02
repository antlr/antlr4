/**
 * Map a predicate to a predicted alternative.
 */
export default class PredPrediction {
    constructor(pred, alt) {
        this.alt = alt;
        this.pred = pred;
    }

    toString() {
        return "(" + this.pred + ", " + this.alt + ")";
    }
}
