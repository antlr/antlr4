/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen;

import org.antlr.v4.codegen.model.ContextElement;
import org.antlr.v4.codegen.model.RuleFunction;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.AltAST;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The names of the context getters of a grammar, picked so that no context
 * ends up with two methods of the same name.
 *
 * <p>A rule or token starts out with {@link Target#getContextGetterName}.
 * {@link Target#getContextGetterMethodNames} tells which methods that
 * generates, and where two elements of one context want the same method, one
 * of them is renamed with the suffix the target uses for reserved words.</p>
 *
 * <p>A name is picked once for the whole grammar: an element renamed for one
 * context is renamed in all of them, so a rule or token has the same getter
 * wherever it is referenced. Since a clash used to produce code that did not
 * compile, a grammar that worked before has no clash and keeps all its
 * names.</p>
 *
 * <p>Of two elements that clash, the one that keeps its name is, in order:
 * the one that has not been renamed yet, the token rather than the rule, and
 * the one whose name sorts first.</p>
 */
public class ContextGetterNames {
	/** Tokens come before rules, then by name. */
	private static final Comparator<ContextElement> KEEPS_NAME_FIRST =
		Comparator.<ContextElement, Boolean>comparing(element -> element.isRule).thenComparing(element -> element.name);

	private final Target target;
	/** The getter names of the elements referenced from any context. */
	private final Map<String, String> getterNames = new HashMap<>();
	private final Set<String> renamed = new HashSet<>();

	public ContextGetterNames(Target target, Grammar g) {
		this.target = target;

		List<List<ContextElement>> contexts = new ArrayList<>();
		for (Rule r : g.rules.values()) {
			for (List<AltAST> altASTs : getAltsByContext(r)) {
				List<ContextElement> elements = RuleFunction.getContextElements(g, altASTs);
				Collections.sort(elements, KEEPS_NAME_FIRST);
				for (ContextElement element : elements) {
					getterNames.put(element.name, target.getContextGetterName(element.name));
				}
				contexts.add(elements);
			}
		}

		// A rename that settles one context can clash in another, so repeat
		// until all of them are settled.
		boolean changed;
		do {
			changed = false;
			for (List<ContextElement> elements : contexts) {
				changed |= rename(elements);
			}
		} while (changed);
	}

	/** Returns the getter name for the rule or token {@code name}. */
	public String getName(String name) {
		return getterNames.get(name);
	}

	/** The alternatives of {@code r} grouped by the context they share: the
	 *  unlabeled ones, then those of each label.
	 */
	private static List<List<AltAST>> getAltsByContext(Rule r) {
		List<List<AltAST>> contexts = new ArrayList<>();
		List<AltAST> unlabeledAlts = r.getUnlabeledAltASTs();
		if (unlabeledAlts != null) {
			contexts.add(unlabeledAlts);
		}
		Map<String, List<Pair<Integer, AltAST>>> labels = r.getAltLabels();
		if (labels != null) {
			for (List<Pair<Integer, AltAST>> labeledAlts : labels.values()) {
				List<AltAST> alts = new ArrayList<>();
				for (Pair<Integer, AltAST> pair : labeledAlts) {
					alts.add(pair.b);
				}
				contexts.add(alts);
			}
		}
		return contexts;
	}

	/** Renames elements of one context until its methods are all distinct;
	 *  {@code elements} are ordered by {@link #KEEPS_NAME_FIRST}. Returns
	 *  whether any element was renamed.
	 */
	private boolean rename(List<ContextElement> elements) {
		Set<String> methodNames = new HashSet<>();
		// Elements that still have their own name claim it first, so that a
		// renamed element also stays clear of the names it has no claim to.
		List<ContextElement> pending = new ArrayList<>();
		for (ContextElement element : elements) {
			if (renamed.contains(element.name) || !claim(methodNames, element)) {
				pending.add(element);
			}
		}

		boolean changed = false;
		for (ContextElement element : pending) {
			while (!claim(methodNames, element)) {
				getterNames.put(element.name, target.escapeWord(getterNames.get(element.name)));
				renamed.add(element.name);
				changed = true;
			}
		}
		return changed;
	}

	/** Adds the methods of {@code element} to {@code methodNames} unless one
	 *  of them is taken. Returns whether they were added.
	 */
	private boolean claim(Set<String> methodNames, ContextElement element) {
		Collection<String> names =
			target.getContextGetterMethodNames(getterNames.get(element.name), element.needsList);
		if (!Collections.disjoint(methodNames, names)) {
			return false;
		}
		methodNames.addAll(names);
		return true;
	}
}
