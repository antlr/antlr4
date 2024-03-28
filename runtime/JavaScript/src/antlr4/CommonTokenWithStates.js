import CommonToken from "./CommonToken.js";
import ATNState from "./state/ATNState.js";

export default class CommonTokenWithStates extends CommonToken {

    previousState =  ATNState.INVALID_STATE_NUMBER;
    followState = ATNState.INVALID_STATE_NUMBER;

}
