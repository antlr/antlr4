import {ATNConfigSet} from "../atn/index.js";
import {Recognizer} from "../Recognizer.js";
import { Token } from "../Token.js";
import {RecognitionException} from "./RecognitionException.js";

export declare class NoViableAltException extends RecognitionException {

    deadEndConfigs: ATNConfigSet;

    constructor(recognizer: Recognizer<any>);

    startToken: Token;
}
