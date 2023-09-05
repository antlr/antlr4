/* Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import RecognitionException from "./RecognitionException.js";
import Parser from "../Parser.js";
import Token from "../Token.js";

export declare class ErrorStrategy {
    reset(recognizer: Parser): void;
    sync(recognizer: Parser): void;
    recover(recognizer: Parser, e: RecognitionException): void;
    recoverInline(recognizer: Parser): Token;
    reportMatch(recognizer: Parser): void;
    reportError(recognizer: Parser, e: RecognitionException): void;
}

export default ErrorStrategy;
