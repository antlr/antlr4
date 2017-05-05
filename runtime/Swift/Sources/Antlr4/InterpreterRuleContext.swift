/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


/// This class extends {@link org.antlr.v4.runtime.ParserRuleContext} by allowing the value of
/// {@link #getRuleIndex} to be explicitly set for the context.
/// 
/// <p>
/// {@link org.antlr.v4.runtime.ParserRuleContext} does not include field storage for the rule index
/// since the context classes created by the code generator override the
/// {@link #getRuleIndex} method to return the correct value for that context.
/// Since the parser interpreter does not use the context classes generated for a
/// parser, this class (with slightly more memory overhead per node) is used to
/// provide equivalent functionality.</p>

public class InterpreterRuleContext: ParserRuleContext {
    /// This is the backing field for {@link #getRuleIndex}.
    private var ruleIndex: Int = -1

    public override init() {
        super.init()
    }

    /// Constructs a new {@link org.antlr.v4.runtime.InterpreterRuleContext} with the specified
    /// parent, invoking state, and rule index.
    /// 
    /// - parameter parent: The parent context.
    /// - parameter invokingStateNumber: The invoking state number.
    /// - parameter ruleIndex: The rule index for the current context.
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

    /// Copy a {@link org.antlr.v4.runtime.ParserRuleContext} or {@link org.antlr.v4.runtime.InterpreterRuleContext}
    /// stack to a {@link org.antlr.v4.runtime.InterpreterRuleContext} tree.
    /// Return {@link null} if {@code ctx} is null.
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
