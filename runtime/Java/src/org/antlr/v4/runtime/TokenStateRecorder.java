package org.antlr.v4.runtime;

import org.antlr.v4.runtime.atn.ATNState;

/* to simplify suggestions computations, we register with each token the previous and following ATNStates */
/* we take advantage of the fact that each successful match is followed by a setState(int state) */
/* however there may be numerous setState following a match, so we only register the first state change */

public class TokenStateRecorder implements StateListener {

	final TokenStream _tokenStream;
	public TokenStateRecorder(TokenStream tokenStream) {
		this._tokenStream = tokenStream;
	}

	@Override
	public void stateChanged(int oldState, int newState) {
		// fetch last consumed token
		Token t = this._tokenStream.LT(-1);
		if(t instanceof TokenWithStates) {
			// ensure this is the first time we set follow state for this token
			if(((TokenWithStates) t).getFollowState() == ATNState.INVALID_STATE_NUMBER)
				((TokenWithStates) t).setFollowState(newState);
		}
		// fetch current token
		t = this._tokenStream.LT(1);
		if(t instanceof TokenWithStates) {
			// ensure this is the first time we set previous state for this token
			if(((TokenWithStates) t).getPreviousState() == ATNState.INVALID_STATE_NUMBER)
				((TokenWithStates) t).setPreviousState(oldState);
		}
	}
}
