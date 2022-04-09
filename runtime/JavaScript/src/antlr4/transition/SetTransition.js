/* Copyright (c) 2012-2022 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
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
