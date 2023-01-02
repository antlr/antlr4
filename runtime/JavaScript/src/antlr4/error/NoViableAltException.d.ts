import ATNConfigSet from "../atn/ATNConfigSet";
import Recognizer from "../Recognizer";
import RecognitionException from "./RecognitionException";

declare class NoViableAltException extends RecognitionException {

    deadEndConfigs: ATNConfigSet;

    constructor(recognizer: Recognizer<any>);

}

export default NoViableAltException;
