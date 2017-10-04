/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


/// 
/// This class extends _org.antlr.v4.runtime.ParserRuleContext_ by allowing the value of
/// _#getRuleIndex_ to be explicitly set for the context.
/// 
/// 
/// _org.antlr.v4.runtime.ParserRuleContext_ does not include field storage for the rule index
/// since the context classes created by the code generator override the
/// _#getRuleIndex_ method to return the correct value for that context.
/// Since the parser interpreter does not use the context classes generated for a
/// parser, this class (with slightly more memory overhead per node) is used to
/// provide equivalent functionality.
/// 

public class InterpreterRuleContext: ParserRuleContext {
    /// 
    /// This is the backing field for _#getRuleIndex_.
    /// 
    private var ruleIndex: Int = -1

    public override init() {
        super.init()
    }

    /// 
    /// Constructs a new _org.antlr.v4.runtime.InterpreterRuleContext_ with the specified
    /// parent, invoking state, and rule index.
    /// 
    /// - parameter parent: The parent context.
    /// - parameter invokingStateNumber: The invoking state number.
    /// - parameter ruleIndex: The rule index for the current context.
    /// 
    public init(_ parent: ParserRuleContext?,
                _ invokingStateNumber: Int,
                _ ruleIndex: Int) {
        self.ruleIndex = ruleIndex
        super.init(parent, invokingStateNumber)

    }

    override
    public func getRuleIndex() -> Int {
        return ruleIndex
    }

    /// 
    /// Copy a _org.antlr.v4.runtime.ParserRuleContext_ or _org.antlr.v4.runtime.InterpreterRuleContext_
    /// stack to a _org.antlr.v4.runtime.InterpreterRuleContext_ tree.
    /// Return _null_ if `ctx` is null.
    /// 
    public static func fromParserRuleContext(_ ctx: ParserRuleContext?) -> InterpreterRuleContext? {
        guard let ctx = ctx else {
             return nil
        }
        let dup: InterpreterRuleContext = InterpreterRuleContext()
        dup.copyFrom(ctx)
        dup.ruleIndex = ctx.getRuleIndex()
        dup.parent = fromParserRuleContext(ctx.getParent() as? ParserRuleContext)
        return dup
    }
}
