/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

/**
 * Represents the type of recognizer an ATN applies to.
 *
 * @author Sam Harwell
 */
public enum ATNType {

	/**
	 * A lexer grammar.
	 */
	LEXER,

	/**
	 * A parser grammar.
	 */
	PARSER,

}
