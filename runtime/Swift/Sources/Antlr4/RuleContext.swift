/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


/// A rule context is a record of a single rule invocation.
/// 
/// We form a stack of these context objects using the parent
/// pointer. A parent pointer of null indicates that the current
/// context is the bottom of the stack. The ParserRuleContext subclass
/// as a children list so that we can turn this data structure into a
/// tree.
/// 
/// The root node always has a null pointer and invokingState of -1.
/// 
/// Upon entry to parsing, the first invoked rule function creates a
/// context object (asubclass specialized for that rule such as
/// SContext) and makes it the root of a parse tree, recorded by field
/// Parser._ctx.
/// 
/// public final SContext s() throws RecognitionException {
/// SContext _localctx = new SContext(_ctx, getState()); <-- create new node
/// enterRule(_localctx, 0, RULE_s);                     <-- push it
/// ...
/// exitRule();                                          <-- pop back to _localctx
/// return _localctx;
/// }
/// 
/// A subsequent rule invocation of r from the start rule s pushes a
/// new context object for r whose parent points at s and use invoking
/// state is the state with r emanating as edge label.
/// 
/// The invokingState fields from a context object to the root
/// together form a stack of rule indication states where the root
/// (bottom of the stack) has a -1 sentinel value. If we invoke start
/// symbol s then call r1, which calls r2, the  would look like
/// this:
/// 
/// SContext[-1]   <- root node (bottom of the stack)
/// R1Context[p]   <- p in rule s called r1
/// R2Context[q]   <- q in rule r1 called r2
/// 
/// So the top of the stack, _ctx, represents a call to the current
/// rule and it holds the return address from another rule that invoke
/// to this rule. To invoke a rule, we must always have a current context.
/// 
/// The parent contexts are useful for computing lookahead sets and
/// getting error information.
/// 
/// These objects are used during parsing and prediction.
/// For the special case of parsers, we use the subclass
/// ParserRuleContext.
/// 
/// - SeeAlso: org.antlr.v4.runtime.ParserRuleContext
/// 

open class RuleContext: RuleNode {
    public static let EMPTY = ParserRuleContext()

    /// What context invoked this rule?
    public weak var parent: RuleContext?

    /// What state invoked the rule associated with this context?
    /// The "return address" is the followState of invokingState
    /// If parent is null, this should be -1 this context object represents
    /// the start rule.
    /// 
    public var invokingState = -1

    override
    public init() {
        super.init()
    }

    public init(_ parent: RuleContext?, _ invokingState: Int) {
        self.parent = parent
        //if ( parent!=null ) { print("invoke "+stateNumber+" from "+parent)}
        self.invokingState = invokingState
    }

    open func depth() -> Int {
        var n = 0
        var p: RuleContext? = self
        while let pWrap = p {
            p = pWrap.parent
            n += 1
        }
        return n
    }

    /// A context is empty if there is no invoking state; meaning nobody called
    /// current context.
    /// 
    open func isEmpty() -> Bool {
        return invokingState == -1
    }

    // satisfy the ParseTree / SyntaxTree interface

    override
    open func getSourceInterval() -> Interval {
        return Interval.INVALID
    }

    override
    open func getRuleContext() -> RuleContext {
        return self
    }

    override
    open func getParent() -> Tree? {
        return parent
    }

    override
    open func getPayload() -> AnyObject {
        return self
    }

    /// Return the combined text of all child nodes. This method only considers
    /// tokens which have been added to the parse tree.
    /// 
    /// Since tokens on hidden channels (e.g. whitespace or comments) are not
    /// added to the parse trees, they will not appear in the output of this
    /// method.
    /// 

    open override func getText() -> String {
        let length = getChildCount()
        if length == 0 {
            return ""
        }

        let builder = StringBuilder()
        for i in 0..<length {
            builder.append((getChild(i) as! ParseTree).getText())
        }

        return builder.toString()
    }

    open func getRuleIndex() -> Int {
        return -1
    }

    open func getAltNumber() -> Int { return ATN.INVALID_ALT_NUMBER }
    open func setAltNumber(_ altNumber: Int) { }

    open override func getChild(_ i: Int) -> Tree? {
        return nil
    }


    open override func getChildCount() -> Int {
        return 0
    }

    open override func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
        return visitor.visitChildren(self)
    }

    /// Print out a whole tree, not just a node, in LISP format
    /// (root child1 .. childN). Print just a node if this is a leaf.
    /// We have to know the recognizer so we can get rule names.
    ///
    open override func toStringTree(_ recog: Parser) -> String {
        return Trees.toStringTree(self, recog)
    }

    /// Print out a whole tree, not just a node, in LISP format
    /// (root child1 .. childN). Print just a node if this is a leaf.
    /// 
    public func toStringTree(_ ruleNames: [String]?) -> String {
        return Trees.toStringTree(self, ruleNames)
    }

    open override func toStringTree() -> String {
        return toStringTree(nil)
    }

    open override var description: String {
        return toString(nil, nil)
    }

     open override var debugDescription: String {
         return description
    }

    public final func toString<T>(_ recog: Recognizer<T>) -> String {
        return toString(recog, ParserRuleContext.EMPTY)
    }

    public final func toString(_ ruleNames: [String]) -> String {
        return toString(ruleNames, nil)
    }

    // recog null unless ParserRuleContext, in which case we use subclass toString(...)
    open func toString<T>(_ recog: Recognizer<T>?, _ stop: RuleContext) -> String {
        let ruleNames = recog?.getRuleNames()
        return toString(ruleNames, stop)
    }

    open func toString(_ ruleNames: [String]?, _ stop: RuleContext?) -> String {
        let buf = StringBuilder()
        var p: RuleContext? = self
        buf.append("[")
        while let pWrap = p, pWrap !== stop {
            if let ruleNames = ruleNames {
                let ruleIndex = pWrap.getRuleIndex()
                let ruleIndexInRange = (ruleIndex >= 0 && ruleIndex < ruleNames.count)
                let ruleName = (ruleIndexInRange ? ruleNames[ruleIndex] : String(ruleIndex))
                buf.append(ruleName)
            }
            else {
                if !pWrap.isEmpty() {
                    buf.append(pWrap.invokingState)
                }
            }

            if pWrap.parent != nil && (ruleNames != nil || !pWrap.parent!.isEmpty()) {
                buf.append(" ")
            }

            p = pWrap.parent
        }

        buf.append("]")
        return buf.toString()
    }

    open func castdown<T>(_ subType: T.Type) -> T {
        return self as! T
    }

}
