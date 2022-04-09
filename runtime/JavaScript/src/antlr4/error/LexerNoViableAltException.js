/* Copyright (c) 2012-2022 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import Interval from "../misc/Interval.js";
import RecognitionException from "./RecognitionException.js";

export default class LexerNoViableAltException extends RecognitionException {
    constructor(lexer, input, startIndex, deadEndConfigs) {
        super({message: "", recognizer: lexer, input: input, ctx: null});
        this.startIndex = startIndex;
        this.deadEndConfigs = deadEndConfigs;
    }

    toString() {
        let symbol = "";
        if (this.startIndex >= 0 && this.startIndex < this.input.size) {
            symbol = this.input.getText(new Interval(this.startIndex,this.startIndex));
        }
        return "LexerNoViableAltException" + symbol;
    }
}
