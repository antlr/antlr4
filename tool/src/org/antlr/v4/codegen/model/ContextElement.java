/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

/** A rule or token referenced from the alternatives that share a context. It
 *  gets a getter in that context.
 */
public class ContextElement {
	/** The name of the rule or token. */
	public final String name;
	public final boolean isRule;
	/** Whether an alternative can match more than one of the rule or token. */
	public final boolean needsList;
	/** Whether an alternative can match without the rule or token. */
	public final boolean optional;

	public ContextElement(String name, boolean isRule, boolean needsList, boolean optional) {
		this.name = name;
		this.isRule = isRule;
		this.needsList = needsList;
		this.optional = optional;
	}
}
