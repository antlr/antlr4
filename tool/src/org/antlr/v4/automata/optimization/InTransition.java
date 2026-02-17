package org.antlr.v4.automata.optimization;

import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.Transition;

import java.util.Objects;

/**
 * Helper class used in ATN optimizers
 */
class InTransition {
	public final Transition transition;
	public final ATNState previousState;
	public final boolean isFollowState;

	public InTransition(Transition transition, ATNState previousState, boolean isFollowState) {
		this.transition = transition;
		this.previousState = previousState;
		this.isFollowState = isFollowState;
	}

	@Override
	public String toString() {
		return previousState + " -> " + transition.target;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		InTransition that = (InTransition) o;
		return isFollowState == that.isFollowState && Objects.equals(transition, that.transition) && Objects.equals(previousState, that.previousState);
	}

	@Override
	public int hashCode() {
		return Objects.hash(transition, previousState, isFollowState);
	}
}
