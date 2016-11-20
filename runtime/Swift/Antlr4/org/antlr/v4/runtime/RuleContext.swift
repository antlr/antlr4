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


/** A rule context is a record of a single rule invocation.
*
*  We form a stack of these context objects using the parent
*  pointer. A parent pointer of null indicates that the current
*  context is the bottom of the stack. The ParserRuleContext subclass
*  as a children list so that we can turn this data structure into a
*  tree.
*
*  The root node always has a null pointer and invokingState of -1.
*
*  Upon entry to parsing, the first invoked rule function creates a
*  context object (asubclass specialized for that rule such as
*  SContext) and makes it the root of a parse tree, recorded by field
*  Parser._ctx.
*
*  public final SContext s() throws RecognitionException {
*      SContext _localctx = new SContext(_ctx, getState()); <-- create new node
*      enterRule(_localctx, 0, RULE_s);                     <-- push it
*      ...
*      exitRule();                                          <-- pop back to _localctx
*      return _localctx;
*  }
*
*  A subsequent rule invocation of r from the start rule s pushes a
*  new context object for r whose parent points at s and use invoking
*  state is the state with r emanating as edge label.
*
*  The invokingState fields from a context object to the root
*  together form a stack of rule indication states where the root
*  (bottom of the stack) has a -1 sentinel value. If we invoke start
*  symbol s then call r1, which calls r2, the  would look like
*  this:
*
*     SContext[-1]   <- root node (bottom of the stack)
*     R1Context[p]   <- p in rule s called r1
*     R2Context[q]   <- q in rule r1 called r2
*
*  So the top of the stack, _ctx, represents a call to the current
*  rule and it holds the return address from another rule that invoke
*  to this rule. To invoke a rule, we must always have a current context.
*
*  The parent contexts are useful for computing lookahead sets and
*  getting error information.
*
*  These objects are used during parsing and prediction.
*  For the special case of parsers, we use the subclass
*  ParserRuleContext.
*
*  @see org.antlr.v4.runtime.ParserRuleContext
*/

open class RuleContext: RuleNode {
    public static let EMPTY: ParserRuleContext = ParserRuleContext()

    /** What context invoked this rule? */
    public var parent: RuleContext?

    /** What state invoked the rule associated with this context?
     *  The "return address" is the followState of invokingState
     *  If parent is null, this should be -1 this context object represents
     *  the start rule.
     */
    public var invokingState: Int = -1
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
        var n: Int = 0
        var p: RuleContext? = self
        while let pWrap = p {
            p = pWrap.parent
            n += 1
        }
        return n
    }

    /** A context is empty if there is no invoking state; meaning nobody called
     *  current context.
     */
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

    /** Return the combined text of all child nodes. This method only considers
     *  tokens which have been added to the parse tree.
     *  <p>
     *  Since tokens on hidden channels (e.g. whitespace or comments) are not
     *  added to the parse trees, they will not appear in the output of this
     *  method.
     */

    open override func getText() -> String {
        let length = getChildCount()
        if length == 0 {
            return ""
        }

        let builder: StringBuilder = StringBuilder()
        for i in 0..<length {
            builder.append((getChild(i) as! ParseTree).getText())
        }

        return builder.toString()
    }

    open func getRuleIndex() -> Int {
        return -1
    }


    open override func getChild(_ i: Int) -> Tree? {
        return nil
    }


    open override func getChildCount() -> Int {
        return 0
    }

    open override func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
        return visitor.visitChildren(self)
    }

    /*
     /** Call this method to view a parse tree in a dialog box visually. */
     public func inspect(parser : Parser) -> Future<JDialog> {
         var ruleNames : Array<String> = parser != nil ? Arrays.asList(parser.getRuleNames()) : null;
         return inspect(ruleNames);
     }

     public func inspect(ruleNames : Array<String>) -> Future<JDialog> {
         var viewer : TreeViewer = TreeViewer(ruleNames, self);
         return viewer.open();
     }

     /** Save this tree in a postscript file */
     public func save(parser : Parser, _ fileName : String)
         throws; IOException, PrintException
     {
         var ruleNames : Array<String> = parser != nil ? Arrays.asList(parser.getRuleNames()) : null;
         save(ruleNames, fileName);
     }

     /** Save this tree in a postscript file using a particular font name and size */
     public func save(parser : Parser, _ fileName : String,
                      _ fontName : String, _ fontSize : Int)
         throws; IOException
     {
         var ruleNames : Array<String> = parser != nil ? Arrays.asList(parser.getRuleNames()) : null;
         save(ruleNames, fileName, fontName, fontSize);
     }

     /** Save this tree in a postscript file */
     public func save(ruleNames : Array<String>, _ fileName : String)
         throws; IOException, PrintException
     {
         Trees.writePS(self, ruleNames, fileName);
     }

     /** Save this tree in a postscript file using a particular font name and size */
     public func save(ruleNames : Array<String>, _ fileName : String,
                      _ fontName : String, _ fontSize : Int)
         throws; IOException
     {
         Trees.writePS(self, ruleNames, fileName, fontName, fontSize);
     }
 */
    /** Print out a whole tree, not just a node, in LISP format
     *  (root child1 .. childN). Print just a node if this is a leaf.
     *  We have to know the recognizer so we can get rule names.
     */

    open override func toStringTree(_ recog: Parser) -> String {
        return Trees.toStringTree(self, recog)
    }

    /** Print out a whole tree, not just a node, in LISP format
     *  (root child1 .. childN). Print just a node if this is a leaf.
     */
    public func toStringTree(_ ruleNames: Array<String>?) -> String {
        return Trees.toStringTree(self, ruleNames)
    }


    open override func toStringTree() -> String {
        let info: Array<String>? = nil
        return toStringTree(info)
    }
    open override var description: String {
        let p1: Array<String>? = nil
        let p2: RuleContext? = nil
        return toString(p1, p2)
    }
    
     open override var debugDescription: String {
         return description
    }

    public final func toString<T:ATNSimulator>(_ recog: Recognizer<T>) -> String {
        return toString(recog, ParserRuleContext.EMPTY)
    }

    public final func toString(_ ruleNames: Array<String>) -> String {
        return toString(ruleNames, nil)
    }

    // recog null unless ParserRuleContext, in which case we use subclass toString(...)
    open func toString<T:ATNSimulator>(_ recog: Recognizer<T>?, _ stop: RuleContext) -> String {
        let ruleNames: [String]? = recog != nil ? recog!.getRuleNames() : nil
        let ruleNamesList: Array<String>? = ruleNames ?? nil
        return toString(ruleNamesList, stop)
    }

    open func toString(_ ruleNames: Array<String>?, _ stop: RuleContext?) -> String {
        let buf: StringBuilder = StringBuilder()
        var p: RuleContext? = self
        buf.append("[")
        while let pWrap = p , pWrap !== stop {
            if ruleNames == nil {
                if !pWrap.isEmpty() {
                    buf.append(pWrap.invokingState)
                }
            } else {
                let ruleIndex: Int = pWrap.getRuleIndex()
                let ruleIndexInRange: Bool =  ruleIndex >= 0 && ruleIndex < ruleNames!.count
                let ruleName: String = ruleIndexInRange ? ruleNames![ruleIndex] : String(ruleIndex)
                buf.append(ruleName)
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
