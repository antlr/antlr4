package org.antlr.v4.runtime;

/* a simple mean to remember which ATN state followed a token match */
public interface TokenWithStates extends Token {

	void setPreviousState(int state);
	int getPreviousState();
	void setFollowState(int state);
	int getFollowState();
}
