package org.antlr.v4.runtime;

import org.antlr.v4.runtime.atn.ATNState;

public class RecordingErrorStrategy extends DefaultErrorStrategy {


	@Override
	public void sync(Parser recognizer) {
		this.recordState(recognizer);
		super.sync(recognizer);
	}

	@Override
	public void reportMatch(Parser recognizer) throws RecognitionException {
		this.recordState(recognizer);
		super.reportMatch(recognizer);
	}

	@Override
	public void reportError(Parser recognizer, RecognitionException e) {
		this.recordState(recognizer);
		super.reportError(recognizer, e);
	}

	private void recordState(Parser recognizer) {
		if (!inErrorRecoveryMode(recognizer)) {
			// use the opportunity to populate tokens previous and follow state
			ATNState s = recognizer.getInterpreter().atn.states.get(recognizer.getState());
			TokenStream tokens = recognizer.getInputStream();
			Token t = tokens.LT(-1);
			if (t instanceof TokenWithStates) {
				// only record the 1st encounter of follow state
				if(((TokenWithStates) t).getFollowState() == ATNState.INVALID_STATE_NUMBER)
					((TokenWithStates) t).setFollowState(s.stateNumber);
			}
			t = tokens.LT(1);
			if (t instanceof TokenWithStates) {
				// when it encounters EOF, the parser skips optional token types, leading to an incomplete set of expected tokens
				if(t.getType() != Token.EOF || ((TokenWithStates) t).getPreviousState() == ATNState.INVALID_STATE_NUMBER)
					((TokenWithStates) t).setPreviousState(s.stateNumber);
			}
		}
	}

}
