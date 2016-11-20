/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  Copyright (c) 2015 Janyou
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


/** A rule invocation record for parsing.
 *
 *  Contains all of the information about the current rule not stored in the
 *  RuleContext. It handles parse tree children list, Any ATN state
 *  tracing, and the default values available for rule invocations:
 *  start, stop, rule index, current alt number.
 *
 *  Subclasses made for each rule and grammar track the parameters,
 *  return values, locals, and labels specific to that rule. These
 *  are the objects that are returned from rules.
 *
 *  Note text is not an actual field of a rule return value; it is computed
 *  from start and stop using the input stream's toString() method.  I
 *  could add a ctor to this so that we can pass in and store the input
 *  stream, but I'm not sure we want to do that.  It would seem to be undefined
 *  to get the .text property anyway if the rule matches tokens from multiple
 *  input streams.
 *
 *  I do not use getters for fields of objects that are used simply to
 *  group values such as this aggregate.  The getters/setters are there to
 *  satisfy the superclass interface.
 */

open class ParserRuleContext: RuleContext {
    public var visited = false
    /** If we are debugging or building a parse tree for a visitor,
     *  we need to track all of the tokens and rule invocations associated
     *  with this rule's context. This is empty for parsing w/o tree constr.
     *  operation because we don't the need to track the details about
     *  how we parse this rule.
     */
    public var children: Array<ParseTree>?

    /** For debugging/tracing purposes, we want to track all of the nodes in
     *  the ATN traversed by the parser for a particular rule.
     *  This list indicates the sequence of ATN nodes used to match
     *  the elements of the children list. This list does not include
     *  ATN nodes and other rules used to match rule invocations. It
     *  traces the rule invocation node itself but nothing inside that
     *  other rule's ATN submachine.
     *
     *  There is NOT a one-to-one correspondence between the children and
     *  states list. There are typically many nodes in the ATN traversed
     *  for each element in the children list. For example, for a rule
     *  invocation there is the invoking state and the following state.
     *
     *  The parser setState() method updates field s and adds it to this list
     *  if we are debugging/tracing.
     *
     *  This does not trace states visited during prediction.
     */
//	public List<Integer> states;

    public var start: Token?, stop: Token?

    /**
     * The exception that forced this rule to return. If the rule successfully
     * completed, this is {@code null}.
     */
    public var exception: AnyObject!
    //RecognitionException<ATNSimulator>!;

    public override init() {
        super.init()
    }

    /** COPY a ctx (I'm deliberately not using copy constructor) to avoid
     *  confusion with creating node with parent. Does not copy children.
     */
    open func copyFrom(_ ctx: ParserRuleContext) {
        self.parent = ctx.parent
        self.invokingState = ctx.invokingState

        self.start = ctx.start
        self.stop = ctx.stop
    }

    public init(_ parent: ParserRuleContext?, _ invokingStateNumber: Int) {
        super.init(parent, invokingStateNumber)
    }

    // Double dispatch methods for listeners

    open func enterRule(_ listener: ParseTreeListener) {
    }

    open func exitRule(_ listener: ParseTreeListener) {
    }

    /** Does not set parent link; other add methods do that */
    @discardableResult
    open func addChild(_ t: TerminalNode) -> TerminalNode {
        if children == nil {
            children = Array<ParseTree>()
        }
        children!.append(t)
        return t
    }
    @discardableResult
    open func addChild(_ ruleInvocation: RuleContext) -> RuleContext {
        if children == nil {
            children = Array<ParseTree>()
        }
        children!.append(ruleInvocation)
        return ruleInvocation
    }

    /** Used by enterOuterAlt to toss out a RuleContext previously added as
     *  we entered a rule. If we have # label, we will need to remove
     *  generic ruleContext object.
      */
    open func removeLastChild() {
            children?.removeLast()
            //children.remove(children.size()-1);
    }

//	public void trace(int s) {
//		if ( states==null ) states = new ArrayList<Integer>();
//		states.add(s);
//	}

    open func addChild(_ matchedToken: Token) -> TerminalNode {
        let t: TerminalNodeImpl = TerminalNodeImpl(matchedToken)
        addChild(t)
        t.parent = self
        return t
    }
    @discardableResult
    open func addErrorNode(_ badToken: Token) -> ErrorNode {
        let t: ErrorNode = ErrorNode(badToken)
        addChild(t)
        t.parent = self
        return t
    }

    override
    /** Override to make type more specific */
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

    /**
     * Get the initial token in this context.
     * Note that the range from start to stop is inclusive, so for rules that do not consume anything
     * (for example, zero length or error productions) this token may exceed stop.
     */
    open func getStart() -> Token? {
        return start
    }
    /**
     * Get the final token in this context.
     * Note that the range from start to stop is inclusive, so for rules that do not consume anything
     * (for example, zero length or error productions) this token may precede start.
     */
    open func getStop() -> Token? {
        return stop
    }

    /** Used for rule context info debugging during parse-time, not so much for ATN debugging */
    open func toInfoString(_ recognizer: Parser) -> String {
        var rules: Array<String> = recognizer.getRuleInvocationStack(self)
        // Collections.reverse(rules);
        rules = rules.reversed()
        return "ParserRuleContext\(rules){start= + \(start), stop=\(stop)}"

    }
}
