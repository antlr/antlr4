import PredictionContext from "./PredictionContext.js";
import SingletonPredictionContext from "./SingletonPredictionContext.js";

export default class EmptyPredictionContext extends SingletonPredictionContext {

    constructor() {
        super(null, PredictionContext.EMPTY_RETURN_STATE);
    }

    isEmpty() {
        return true;
    }

    getParent(index) {
        return null;
    }

    getReturnState(index) {
        return this.returnState;
    }

    equals(other) {
        return this === other;
    }

    toString() {
        return "$";
    }
}


PredictionContext.EMPTY = new EmptyPredictionContext();
