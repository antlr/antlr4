/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.tool;

import org.antlr.v4.tool.ast.ActionAST;

/** Grammars, rules, and alternatives all have symbols visible to
 *  actions.  To evaluate attr exprs, ask action for its resolver
 *  then ask resolver to look up various symbols. Depending on the context,
 *  some symbols are available at some aren't.
 *
 *  Alternative level:
 *
 *  $x		Attribute: rule arguments, return values, predefined rule prop.
 * 			AttributeDict: references to tokens and token labels in the
 * 			current alt (including any elements within subrules contained
 * 			in that outermost alt). x can be rule with scope or a global scope.
 * 			List label: x is a token/rule list label.
 *  $x.y	Attribute: x is surrounding rule, rule/token/label ref
 *  $s::y	Attribute: s is any rule with scope or global scope; y is prop within
 *
 *  Rule level:
 *
 *  $x		Attribute: rule arguments, return values, predefined rule prop.
 * 			AttributeDict: references to token labels in *any* alt. x can
 * 			be any rule with scope or global scope.
 * 			List label: x is a token/rule list label.
 *  $x.y	Attribute: x is surrounding rule, label ref (in any alts)
 *  $s::y	Attribute: s is any rule with scope or global scope; y is prop within
 *
 *  Grammar level:
 *
 *  $s		AttributeDict: s is a global scope
 *  $s::y	Attribute: s is a global scope; y is prop within
 */
public interface AttributeResolver {
	public boolean resolvesToListLabel(String x, ActionAST node);
	public boolean resolvesToLabel(String x, ActionAST node);
	public boolean resolvesToAttributeDict(String x, ActionAST node);
	public boolean resolvesToToken(String x, ActionAST node);
	public Attribute resolveToAttribute(String x, ActionAST node);
	public Attribute resolveToAttribute(String x, String y, ActionAST node);
}
