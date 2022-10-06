import RecognitionException from "./RecognitionException";
import {ATNConfigSet} from "../atn";
import Recognizer from "../Recognizer";

export default class NoViableAltException extends RecognitionException {

    deadEndConfigs: ATNConfigSet;

    constructor(recognizer: Recognizer<any>);

}
