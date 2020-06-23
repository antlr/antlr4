/*
 * Copyright (c) 2012-2018 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.tree.TerminalNodeImpl;

import antlr.v4.runtime.InterfaceRecognizer;
import antlr.v4.runtime.RuleContext : RuleContext;
import antlr.v4.runtime.Token;
import antlr.v4.runtime.TokenConstantDefinition;
import antlr.v4.runtime.atn.StateNames;
import antlr.v4.runtime.misc.Interval;
import antlr.v4.runtime.tree.ParseTree;
import antlr.v4.runtime.tree.ParseTreeVisitor;
import antlr.v4.runtime.tree.TerminalNode;
import std.conv;
import std.variant;

/**
 * TODO add class description
 */
class TerminalNodeImpl : TerminalNode
{

    public Token symbol;

    public ParseTree parent;

    public this(Token symbol)
    {
        this.symbol = symbol;
    }

    public ParseTree getChild(int i)
    {
        return null;
    }

    public ParseTree getParent()
    {
        return parent;
    }

    public void setParent(RuleContext parent)
    {
        this.parent = parent;
    }

    public Token getSymbol()
    {
        return symbol;
    }

    public Object getPayload()
    {
        return cast(Object)symbol;
    }

    public Interval getSourceInterval()
    {
        if (symbol is null)
            return cast(Interval)Interval.INVALID;
        auto tokenIndex = symbol.getTokenIndex();
        return new Interval(to!int(tokenIndex), to!int(tokenIndex));

    }

    public int getChildCount()
    {
        return 0;
    }

    public Variant accept(ParseTreeVisitor visitor)
    {
        return visitor.visitTerminal(this);
    }

    public Variant getText()
    {
        return symbol.getText();
    }

    public string toStringTree(InterfaceRecognizer parser)
    {
        return toString();
    }

    /**
     * @uml
     * @override
     */
    public override string toString()
    {
        if (symbol.getType() == TokenConstantDefinition.EOF )
            return "<EOF>";
        return to!string(symbol.getText);
    }

    public string toStringTree()
    {
        return toString();
    }

}
