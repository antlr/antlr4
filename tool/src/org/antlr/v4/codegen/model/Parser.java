/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.runtime.misc.IntegerList;

import java.util.ArrayList;
import java.util.List;

public class Parser extends Recognizer {
	public final ParserFile file;

	@ModelElement public final List<RuleFunction> funcs = new ArrayList<RuleFunction>();

	public Parser(OutputModelFactory factory, ParserFile file, IntegerList atnData) {
		super(factory, atnData);
		this.file = file;
	}
}
