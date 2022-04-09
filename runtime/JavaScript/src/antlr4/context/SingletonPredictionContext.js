/* Copyright (c) 2012-2022 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import PredictionContext from './PredictionContext.js';
import HashCode from "../misc/HashCode.js";

export default class SingletonPredictionContext extends PredictionContext {

    constructor(parent, returnState) {
        let hashCode = 0;
        const hash = new HashCode();
        if(parent !== null) {
            hash.update(parent, returnState);
        } else {
            hash.update(1);
        }
        hashCode = hash.finish();
        super(hashCode);
        this.parentCtx = parent;
        this.returnState = returnState;
    }

    getParent(index) {
        return this.parentCtx;
    }

    getReturnState(index) {
        return this.returnState;
    }

    equals(other) {
        if (this === other) {
            return true;
        } else if (!(other instanceof SingletonPredictionContext)) {
            return false;
        } else if (this.hashCode() !== other.hashCode()) {
            return false; // can't be same if hash is different
        } else {
            if(this.returnState !== other.returnState)
                return false;
            else if(this.parentCtx==null)
                return other.parentCtx==null
            else
                return this.parentCtx.equals(other.parentCtx);
        }
    }

    toString() {
        const up = this.parentCtx === null ? "" : this.parentCtx.toString();
        if (up.length === 0) {
            if (this.returnState === PredictionContext.EMPTY_RETURN_STATE) {
                return "$";
            } else {
                return "" + this.returnState;
            }
        } else {
            return "" + this.returnState + " " + up;
        }
    }

    get length(){
        return 1;
    }

    static create(parent, returnState) {
        if (returnState === PredictionContext.EMPTY_RETURN_STATE && parent === null) {
            // someone can pass in the bits of an array ctx that mean $
            return PredictionContext.EMPTY;
        } else {
            return new SingletonPredictionContext(parent, returnState);
        }
    }
}

