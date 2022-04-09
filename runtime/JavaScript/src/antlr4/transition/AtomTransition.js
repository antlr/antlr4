/* Copyright (c) 2012-2022 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import IntervalSet from "../misc/IntervalSet.js";
import Transition from "./Transition.js";

export default class AtomTransition extends Transition {
    constructor(target, label) {
        super(target);
        // The token type or character value; or, signifies special label.
        this.label_ = label;
        this.label = this.makeLabel();
        this.serializationType = Transition.ATOM;
    }

    makeLabel() {
        const s = new IntervalSet();
        s.addOne(this.label_);
        return s;
    }

    matches(symbol, minVocabSymbol, maxVocabSymbol) {
        return this.label_ === symbol;
    }

    toString() {
        return this.label_;
    }
}
