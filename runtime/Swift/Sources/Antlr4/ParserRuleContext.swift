/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


/// A rule invocation record for parsing.
/// 
/// Contains all of the information about the current rule not stored in the
/// RuleContext. It handles parse tree children list, Any ATN state
/// tracing, and the default values available for rule invocations:
/// start, stop, rule index, current alt number.
/// 
/// Subclasses made for each rule and grammar track the parameters,
/// return values, locals, and labels specific to that rule. These
/// are the objects that are returned from rules.
/// 
/// Note text is not an actual field of a rule return value; it is computed
/// from start and stop using the input stream's toString() method.  I
/// could add a ctor to this so that we can pass in and store the input
/// stream, but I'm not sure we want to do that.  It would seem to be undefined
/// to get the .text property anyway if the rule matches tokens from multiple
/// input streams.
/// 
/// I do not use getters for fields of objects that are used simply to
/// group values such as this aggregate.  The getters/setters are there to
/// satisfy the superclass interface.
/// 
open class ParserRuleContext: RuleContext {
    public var visited = false

    /// If we are debugging or building a parse tree for a visitor,
    /// we need to track all of the tokens and rule invocations associated
    /// with this rule's context. This is empty for parsing w/o tree constr.
    /// operation because we don't the need to track the details about
    /// how we parse this rule.
    /// 
    public var children: [ParseTree]?

    /// For debugging/tracing purposes, we want to track all of the nodes in
    /// the ATN traversed by the parser for a particular rule.
    /// This list indicates the sequence of ATN nodes used to match
    /// the elements of the children list. This list does not include
    /// ATN nodes and other rules used to match rule invocations. It
    /// traces the rule invocation node itself but nothing inside that
    /// other rule's ATN submachine.
    /// 
    /// There is NOT a one-to-one correspondence between the children and
    /// states list. There are typically many nodes in the ATN traversed
    /// for each element in the children list. For example, for a rule
    /// invocation there is the invoking state and the following state.
    /// 
    /// The parser setState() method updates field s and adds it to this list
    /// if we are debugging/tracing.
    /// 
    /// This does not trace states visited during prediction.
    /// 
    public var start: Token?, stop: Token?

    /// 
    /// The exception that forced this rule to return. If the rule successfully
    /// completed, this is `null`.
    /// 
    public var exception: RecognitionException?

    public override init() {
        super.init()
    }

    public init(_ parent: ParserRuleContext?, _ invokingStateNumber: Int) {
        super.init(parent, invokingStateNumber)
    }

    /// COPY a ctx (I'm deliberately not using copy constructor) to avoid
    /// confusion with creating node with parent. Does not copy children.
    /// 
    /// This is used in the generated parser code to flip a generic XContext
    /// node for rule X to a YContext for alt label Y. In that sense, it is
    /// not really a generic copy function.
    /// 
    /// If we do an error sync() at start of a rule, we might add error nodes
    /// to the generic XContext so this function must copy those nodes to
    /// the YContext as well else they are lost!
    /// 
    open func copyFrom(_ ctx: ParserRuleContext) {
        self.parent = ctx.parent
        self.invokingState = ctx.invokingState
        self.start = ctx.start
        self.stop = ctx.stop

        // copy any error nodes to alt label node
        if let ctxChildren = ctx.children {
            self.children = [ParseTree]()
            // reset parent pointer for any error nodes
            for child in ctxChildren {
                if let errNode = child as? ErrorNode {
                    addChild(errNode)
                }
            }
        }
    }

    // Double dispatch methods for listeners

    open func enterRule(_ listener: ParseTreeListener) {
    }

    open func exitRule(_ listener: ParseTreeListener) {
    }

    /// Add a parse tree node to this as a child.  Works for
    /// internal and leaf nodes. Does not set parent link;
    /// other add methods must do that. Other addChild methods
    /// call this.
    /// 
    /// We cannot set the parent pointer of the incoming node
    /// because the existing interfaces do not have a setParent()
    /// method and I don't want to break backward compatibility for this.
    /// 
    /// - Since: 4.7
    /// 
    open func addAnyChild(_ t: ParseTree) {
        if children == nil {
            children = [ParseTree]()
        }
        children!.append(t)
    }

    open func addChild(_ ruleInvocation: RuleContext) {
        addAnyChild(ruleInvocation)
    }

    /// Add a token leaf node child and force its parent to be this node.
    open func addChild(_ t: TerminalNode) {
        t.setParent(self)
        addAnyChild(t)
    }

    /// Add an error node child and force its parent to be this node.
    open func addErrorNode(_ errorNode: ErrorNode) {
        errorNode.setParent(self)
        addAnyChild(errorNode)
    }


    /// Used by enterOuterAlt to toss out a RuleContext previously added as
    /// we entered a rule. If we have # label, we will need to remove
    /// generic ruleContext object.
    /// 
    open func removeLastChild() {
        children?.removeLast()
    }


    override
    open func getChild(_ i: Int) -> Tree? {
        guard let children = children, i >= 0 && i < children.count else {
            return nil
        }
        return children[i]
    }

    open func getChild<T: ParseTree>(_ ctxType: T.Type, i: Int) -> T? {
        guard let children = children, i >= 0 && i < children.count else {
            return nil
        }
        var j = -1 // what element have we found with ctxType?
        for o in children {
            if let o = o as? T {
                j += 1
                if j == i {
                    return o
                }
            }
        }

        return nil
    }

    open func getToken(_ ttype: Int, _ i: Int) -> TerminalNode? {
        guard let children = children, i >= 0 && i < children.count else {
            return nil
        }
        var j = -1 // what token with ttype have we found?
        for o in children {
            if let tnode = o as? TerminalNode {
                let symbol = tnode.getSymbol()!
                if symbol.getType() == ttype {
                    j += 1
                    if j == i {
                        return tnode
                    }
                }
            }
        }

        return nil
    }

    open func getTokens(_ ttype: Int) -> [TerminalNode] {
        guard let children = children else {
            return [TerminalNode]()
        }

        return children.compactMap {
            if let tnode = $0 as? TerminalNode, let symbol = tnode.getSymbol(), symbol.getType() == ttype {
                return tnode
            }
            else {
                return nil
            }
        }
    }

    open func getRuleContext<T: ParserRuleContext>(_ ctxType: T.Type, _ i: Int) -> T? {
        return getChild(ctxType, i: i)
    }

    open func getRuleContexts<T: ParserRuleContext>(_ ctxType: T.Type) -> [T] {
        guard let children = children else {
            return [T]()
        }
        return children.compactMap { $0 as? T }
    }

    override
    open func getChildCount() -> Int {
        return children?.count ?? 0
    }

    override
    open subscript(index: Int) -> ParseTree {
        return children![index]
    }

    override
    open func getSourceInterval() -> Interval {
        guard let start = start, let stop = stop else {
             return Interval.INVALID
        }
        return Interval.of(start.getTokenIndex(), stop.getTokenIndex())
    }

    /// 
    /// Get the initial token in this context.
    /// Note that the range from start to stop is inclusive, so for rules that do not consume anything
    /// (for example, zero length or error productions) this token may exceed stop.
    /// 
    open func getStart() -> Token? {
        return start
    }
    /// 
    /// Get the final token in this context.
    /// Note that the range from start to stop is inclusive, so for rules that do not consume anything
    /// (for example, zero length or error productions) this token may precede start.
    /// 
    open func getStop() -> Token? {
        return stop
    }

    /// Used for rule context info debugging during parse-time, not so much for ATN debugging
    open func toInfoString(_ recognizer: Parser) -> String {
        let rules = Array(recognizer.getRuleInvocationStack(self).reversed())
        let startStr = start == nil ? "<unknown>" : start!.description
        let stopStr = stop == nil ? "<unknown>" : stop!.description
        return "ParserRuleContext\(rules){start=\(startStr)), stop=\(stopStr)}"
    }
}
