import {ATNConfigSet} from "../atn";
import {Recognizer} from "../Recognizer";
import { Token } from "../Token";
import {RecognitionException} from "./RecognitionException";

export declare class LexerNoViableAltException extends RecognitionException {

    deadEndConfigs: ATNConfigSet;

    constructor(recognizer: Recognizer<any>);

    startIndex: number;
}
