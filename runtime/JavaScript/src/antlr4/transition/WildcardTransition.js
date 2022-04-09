/* Copyright (c) 2012-2022 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import Transition from "./Transition.js";

export default class WildcardTransition extends Transition {
    constructor(target) {
        super(target);
        this.serializationType = Transition.WILDCARD;
    }

    matches(symbol, minVocabSymbol, maxVocabSymbol) {
        return symbol >= minVocabSymbol && symbol <= maxVocabSymbol;
    }

    toString() {
        return ".";
    }
}
