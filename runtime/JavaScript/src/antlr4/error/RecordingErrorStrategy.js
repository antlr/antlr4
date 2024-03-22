import DefaultErrorStrategy from "./DefaultErrorStrategy.js";
import CommonTokenWithStates from "../CommonTokenWithStates.js";
import ATNState from "../state/ATNState.js";
import Token from "../Token.js";

export default class RecordingErrorStrategy extends DefaultErrorStrategy {

    sync(recognizer) {
        this.recordState(recognizer);
        super.sync(recognizer);
    }

    reportMatch(recognizer) {
        this.recordState(recognizer);
        super.reportMatch(recognizer);
    }

    reportError(recognizer, error) {
        this.recordState(recognizer);
        super.reportError(recognizer, error);
    }

    recordState(recognizer) {
        if (!this.inErrorRecoveryMode(recognizer)) {
            // use the opportunity to populate tokens previous and follow state
            const state = recognizer._interp.atn.states[recognizer._stateNumber];
            const tokens = recognizer._input;
            let token = tokens.LT(-1);
            if (token instanceof CommonTokenWithStates) {
                // only record the 1st encounter of follow state
                if(token.followState === ATNState.INVALID_STATE_NUMBER) {
                    token.followState = state.stateNumber;
                }
            }
            token = tokens.LT(1);
            if (token instanceof CommonTokenWithStates) {
                // when it encounters EOF, the parser skips optional token types, leading to an incomplete set of expected tokens
                if(token.type !== Token.EOF || token.previousState === ATNState.INVALID_STATE_NUMBER) {
                    token.previousState = state.stateNumber;
                }
            }
        }
    }

}
