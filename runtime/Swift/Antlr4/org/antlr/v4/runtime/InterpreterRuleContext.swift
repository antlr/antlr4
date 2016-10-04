/*
 * [The "BSD license"]
 * Copyright (c) 2013 Terence Parr
 * Copyright (c) 2013 Sam Harwell
 * Copyright (c) 2015 Janyou
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


/**
 * This class extends {@link org.antlr.v4.runtime.ParserRuleContext} by allowing the value of
 * {@link #getRuleIndex} to be explicitly set for the context.
 *
 * <p>
 * {@link org.antlr.v4.runtime.ParserRuleContext} does not include field storage for the rule index
 * since the context classes created by the code generator override the
 * {@link #getRuleIndex} method to return the correct value for that context.
 * Since the parser interpreter does not use the context classes generated for a
 * parser, this class (with slightly more memory overhead per node) is used to
 * provide equivalent functionality.</p>
 */

public class InterpreterRuleContext: ParserRuleContext {
    /** This is the backing field for {@link #getRuleIndex}. */
    private var ruleIndex: Int = -1

    public override init() {
        super.init()
    }

    /**
     * Constructs a new {@link org.antlr.v4.runtime.InterpreterRuleContext} with the specified
     * parent, invoking state, and rule index.
     *
     * @param parent The parent context.
     * @param invokingStateNumber The invoking state number.
     * @param ruleIndex The rule index for the current context.
     */
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

    /** Copy a {@link org.antlr.v4.runtime.ParserRuleContext} or {@link org.antlr.v4.runtime.InterpreterRuleContext}
     *  stack to a {@link org.antlr.v4.runtime.InterpreterRuleContext} tree.
     *  Return {@link null} if {@code ctx} is null.
     */
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
