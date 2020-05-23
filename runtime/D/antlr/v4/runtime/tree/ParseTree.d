/*
 * Copyright (c) 2012-2019 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.tree.ParseTree;

import antlr.v4.runtime.tree.ParseTreeVisitor;
import antlr.v4.runtime.InterfaceRecognizer;
import antlr.v4.runtime.RuleContext : RuleContext;
import antlr.v4.runtime.tree.SyntaxTree;
import std.variant;

/**
 * An interface to access the tree of {@link RuleContext} objects created
 *  *  during a parse that makes the data structure look like a simple parse tree.
 * This node represents both internal nodes, rule invocations,
 * and leaf nodes, token matches.
 *
 * <p>The payload is either a {@link Token} or a {@link RuleContext} object.</p>
 */
interface ParseTree : SyntaxTree
{

    /**
     * @uml
     * @override
     */
    public override ParseTree getParent();

    public void setParent(RuleContext parent);

    /**
     * @uml
     * @override
     */
    public override ParseTree getChild(int i);

    /**
     * The {@link ParseTreeVisitor} needs a double dispatch method.
     */
    public Variant accept(ParseTreeVisitor visitor);

    public Variant getText();

    /**
     * Specialize toStringTree so that it can print out more information
     * based upon the parser.
     */
    public string toStringTree(InterfaceRecognizer parser);

}
