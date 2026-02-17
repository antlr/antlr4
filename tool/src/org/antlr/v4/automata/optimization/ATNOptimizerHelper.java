package org.antlr.v4.automata.optimization;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.ast.GrammarAST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ATNOptimizerHelper {
	public final ATN atn;
	public final Grammar grammar;

	/**
	 * Replacement map that is used at the end of optimization process for updating AST nodes
	 * See {@link ATNOptimizerHelper#updateAstNodes) }
	 */
	private final HashMap<ATNState, ATNState> replacement = new HashMap<>();

	/**
	 * Helper collection for fast traversing
	 */
	private final HashMap<ATNState, HashSet<ATNState>> reverseReplacement = new HashMap<>();

	/**
	 * Collection of incoming transitions that useful for ATN optimizers
	 *
	 * I1           O1
	 *     \     /
	 * I2  -> X  -> O2
	 *     /     \
	 * I3           O3
	 *
	 * Incoming: I1 -> X, I2 -> X, I3 -> X
	 * Outgoing: (ordinary transitions): X -> O1, X -> O2, X -> O3
	 */
	private final HashMap<ATNState, List<InTransition>> inTransitions = new HashMap<>();

	/**
	  Removed states that is being updated after optimization step
	 */
	private final List<ATNState> removedStates = new ArrayList<>();

	public static ATNOptimizerHelper initialize(Grammar g, ATN atn) {
		ATNOptimizerHelper helper = new ATNOptimizerHelper(g, atn);
		helper.collectInTransitions();
		return helper;
	}

	private ATNOptimizerHelper(Grammar grammar, ATN atn) {
		this.grammar = grammar;
		this.atn = atn;
	}

	private void collectInTransitions() {
		HashSet<Transition> visitedTransitions = new HashSet<>();
		for (RuleStartState start : atn.ruleToStartState) {
			if (!inTransitions.containsKey(start)) {
				inTransitions.put(start, new ArrayList<>());
			}
			for (int i = 0; i < start.getNumberOfTransitions(); i++) {
				collectInTransitions(start.transition(i), start, visitedTransitions);
			}
		}
	}

	private void collectInTransitions(Transition inTransition, ATNState prevState, HashSet<Transition> visitedTransitions) {
		if (!visitedTransitions.add(inTransition)) {
			return;
		}

		addInTransition(inTransition, inTransition.target, prevState, false);

		if (inTransition instanceof RuleTransition) {
			ATNState followState = ((RuleTransition) inTransition).followState;
			addInTransition(inTransition, followState, prevState, true);
			for (int j = 0; j < followState.getNumberOfTransitions(); j++) {
				collectInTransitions(followState.transition(j), followState, visitedTransitions);
			}
		}

		ATNState target = inTransition.target;
		for (int i = 0; i < target.getNumberOfTransitions(); i++) {
			collectInTransitions(target.transition(i), target, visitedTransitions);
		}
	}

	private void addInTransition(Transition transition, ATNState state, ATNState prevState, boolean isFollowState) {
		List<InTransition> inTransitionsForState = inTransitions.computeIfAbsent(state, k -> new ArrayList<>());
		inTransitionsForState.add(new InTransition(transition, prevState, isFollowState));
	}

	public List<ATNState> getRemovedStates() {
		return removedStates;
	}

	public List<InTransition> getInTransitions(ATNState state) {
		return inTransitions.get(state);
	}

	public void removeInOutTransitions(ATNState state) {
		removeInTransitionsForOutStates(state);
		state.clearTransitions();

		List<InTransition> inTransitionsForState = inTransitions.get(state);
		for (InTransition inTransition : inTransitionsForState) {
			Transition transition = inTransition.previousState.getTransition(t -> t.target == state);
			assert transition != null : "Transition is missing";
			inTransition.previousState.removeTransition(transition);
		}
		inTransitionsForState.clear();
	}

	public void replaceTransition(InTransition inTransition, Transition newTransition) {
		replaceTransition(inTransition.previousState, inTransition.transition, newTransition);
	}

	public void replaceTransition(ATNState previousState, Transition oldTransition, Transition newTransition) {
		previousState.setTransition(previousState.getTransitionIndex(oldTransition), newTransition);

		getInTransitions(newTransition.target).add(new InTransition(newTransition, previousState, false));
		if (newTransition instanceof RuleTransition) {
			RuleTransition newRuleTransition = (RuleTransition) newTransition;
			getInTransitions(newRuleTransition.followState).add(new InTransition(newTransition, previousState, true));
		}
	}

	public void removeInTransitionsForOutStates(ATNState state) {
		for (Transition transition : state.getTransitions()) {
			boolean isRemoved = getInTransitions(transition.target).removeIf(t -> t.previousState == state);
			assert isRemoved : "Transition is missing";
			if (transition instanceof RuleTransition) {
				RuleTransition ruleTransition = (RuleTransition) transition;
				isRemoved = getInTransitions(ruleTransition.followState).removeIf(t -> t.previousState == state);
				assert isRemoved : "Transition is missing";
			}
		}
	}

	/**
	 * First iteration:
	 *     source = a
	 *     dest = b
	 *
	 * Result:
	 *     replacement[a] = b
	 *     reverseReplacement[b] = {a}
	 *
	 * Second iteration:
	 *     source = b
	 *     dest = c
	 *
	 * Result:
	 *     replacement[a] = c
	 *     replacement[b] = c
	 *     reverseReplacement[c] = {a, b}
	 *     reverseReplacement[b] = {}
	 */
	public void addReplacement(ATNState source, ATNState dest) {
		// source -> dest, dest -> newDest => source -> newDest
		HashSet<ATNState> existingSources = reverseReplacement.get(source);
		if (existingSources != null) {
			for (ATNState existingSource : existingSources) {
				replacement.put(existingSource, dest);
			}
			// Removing since intermediate node will be hidden
			reverseReplacement.remove(source);
		}
		else {
			existingSources = new HashSet<>();
		}
		replacement.put(source, dest);

		existingSources.add(source);
		HashSet<ATNState> newSources = reverseReplacement.computeIfAbsent(dest, k -> new HashSet<>());
		newSources.addAll(existingSources);

		removeState(source);
	}

	private void removeState(ATNState source) {
		ATNState removingState = atn.removeState(source);
		assert removingState != null : "Removing state doesn't exist in atn";
		List<InTransition> removingCollection = inTransitions.remove(source);
		assert removingCollection != null : "Removing state doesn't exist in inTransitions";
		removedStates.add(source);
	}

	public void updateAstNodes(GrammarAST ast) {
		ATNState dest = replacement.get(ast.atnState);
		if (dest != null) {
			ast.atnState = dest;
		}

		List<?> children = ast.getChildren();
		if (children != null) {
			for (Object child : children) {
				if (child instanceof GrammarAST) {
					updateAstNodes((GrammarAST) child);
				}
			}
		}
	}

	public void compressStates() {
		List<ATNState> compressed = new ArrayList<>(atn.states.size());
		int newNumber = 0;
		for (ATNState s : atn.states) {
			if (s != null) {
				compressed.add(s);
				s.stateNumber = newNumber++; // reset state number as we shift to new position
			}
		}
		atn.states.clear();
		atn.states.addAll(compressed);
	}
}
