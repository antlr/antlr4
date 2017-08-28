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
    public var children: Array<ParseTree>?

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
    public var exception: AnyObject!

    public override init() {
        super.init()
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
        if  ctx.children != nil {
            self.children = Array<ParseTree>()
            // reset parent pointer for any error nodes
            for  child: ParseTree in ctx.children! {
                if  child is ErrorNode {
                    addChild(child as! ErrorNode)
                }
            }
        }
    }

    public init(_ parent: ParserRuleContext?, _ invokingStateNumber: Int) {
        super.init(parent, invokingStateNumber)
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
    @discardableResult
    open func addAnyChild<T: ParseTree>(_ t: T) -> T {
        if children == nil {
            children = [T]()
        }
        children!.append(t)
        return t
    }

    @discardableResult
    open func addChild(_ ruleInvocation: RuleContext) -> RuleContext {
        return addAnyChild(ruleInvocation)
    }

    /// Add a token leaf node child and force its parent to be this node.
    @discardableResult
    open func addChild(_ t: TerminalNode) -> TerminalNode {
        t.setParent(self)
        return addAnyChild(t)
    }

    /// Add an error node child and force its parent to be this node.
    /// 
    /// - Since: 4.7
    /// 
    @discardableResult
    open func addErrorNode(_ errorNode: ErrorNode) -> ErrorNode {
        errorNode.setParent(self)
        return addAnyChild(errorNode)
    }

    /// Add a child to this node based upon matchedToken. It
    /// creates a TerminalNodeImpl rather than using
    /// _Parser#createTerminalNode(ParserRuleContext, Token)_. I'm leaving this
    /// in for compatibility but the parser doesn't use this anymore.
    /// 
    @available(*, deprecated)
    open func addChild(_ matchedToken: Token) -> TerminalNode {
        let t: TerminalNodeImpl = TerminalNodeImpl(matchedToken)
        addAnyChild(t)
        t.setParent(self)
        return t
    }

    /// Add a child to this node based upon badToken.  It
    /// creates a ErrorNodeImpl rather than using
    /// _Parser#createErrorNode(ParserRuleContext, Token)_. I'm leaving this
    /// in for compatibility but the parser doesn't use this anymore.
    /// 
    @discardableResult
    @available(*, deprecated)
    open func addErrorNode(_ badToken: Token) -> ErrorNode {
        let t: ErrorNode = ErrorNode(badToken)
        addAnyChild(t)
        t.setParent(self)
        return t
    }

    //	public void trace(int s) {
    //		if ( states==null ) states = new ArrayList<Integer>();
    //		states.add(s);
    //	}

    /// Used by enterOuterAlt to toss out a RuleContext previously added as
    /// we entered a rule. If we have # label, we will need to remove
    /// generic ruleContext object.
    /// 
    open func removeLastChild() {
    	if children != nil {
            children!.remove(at: children!.count-1)
    	}
    }


    override
    /// 
    /// Override to make type more specific
    /// 
    open func getParent() -> Tree? {
        return super.getParent()
    }

    override
    open func getChild(_ i: Int) -> Tree? {
        guard let children = children , i >= 0 && i < children.count else {
            return nil
        }
        return children[i]
    }

    open func getChild<T:ParseTree>(_ ctxType: T.Type, i: Int) -> T? {
        guard let children = children , i >= 0 && i < children.count else {
            return nil
        }
        var j: Int = -1 // what element have we found with ctxType?
        for o: ParseTree in children {
            //if ( ctxType.isInstance(o) ) {
            if let o = o as? T {
                j += 1
                if j == i {
                    return o //ctxType.cast(o);
                }
            }
        }
        return nil
    }

    open func getToken(_ ttype: Int, _ i: Int) -> TerminalNode? {
        guard let children = children , i >= 0 && i < children.count else {
            return nil
        }
        var j: Int = -1 // what token with ttype have we found?
        for o: ParseTree in children{
            if let tnode = o as? TerminalNode {
                let symbol: Token = tnode.getSymbol()!
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

    open func getTokens(_ ttype: Int) -> Array<TerminalNode> {
        if children == nil {
            return Array<TerminalNode>()
        }

        var tokens: Array<TerminalNode>? = nil
        for o: ParseTree in children! {
            if o is TerminalNode {
                let tnode: TerminalNode = o as! TerminalNode
                let symbol: Token = tnode.getSymbol()!
                if symbol.getType() == ttype {
                    if tokens == nil {
                        tokens = Array<TerminalNode>()
                    }
                    tokens?.append(tnode)
                }
            }
        }

        if tokens == nil {
            return Array<TerminalNode>()
        }

        return tokens!
    }

    open func getRuleContext<T:ParserRuleContext>(_ ctxType: T.Type, _ i: Int) -> T? {

        return getChild(ctxType, i: i)
    }

    open func getRuleContexts<T:ParserRuleContext>(_ ctxType: T.Type) -> Array<T> {

        guard let children = children  else {
            return Array<T>()//Collections.emptyList();
        }
        var contexts = Array<T>()
        for o: ParseTree in children {
            if let o = o as? T {
                contexts.append(o)
                //contexts.(ctxType.cast(o));
            }
        }
        return contexts
    }

    override
    open func getChildCount() -> Int {
        return children != nil ? children!.count : 0
    }

    override
    open func getSourceInterval() -> Interval {
        guard let start = start,let stop = stop else {
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
        var rules: Array<String> = recognizer.getRuleInvocationStack(self)
        // Collections.reverse(rules);
        rules = rules.reversed()
        return "ParserRuleContext\(rules){start= + \(String(describing: start)), stop=\(String(describing: stop))}"

    }
}
