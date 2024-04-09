package org.antlr.v4.runtime;

import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.misc.Pair;

public class CommonTokenWithStates extends CommonToken implements TokenWithStates {

	int _previousState = ATNState.INVALID_STATE_NUMBER;
	int _followState = ATNState.INVALID_STATE_NUMBER;

	public CommonTokenWithStates(Pair<TokenSource, CharStream> source, int type, int channel, int start, int stop) {
		super(source, type, channel, start, stop);
	}

	public CommonTokenWithStates(int type, String text) {
		super(type, text);
	}

	@Override
	public void setPreviousState(int state) {
		this._previousState = state;
	}

	@Override
	public int getPreviousState() {
		return _previousState;
	}

	public void setFollowState(int state) {
		this._followState = state;
	}

	public int getFollowState() {
		return _followState;
	}
}
