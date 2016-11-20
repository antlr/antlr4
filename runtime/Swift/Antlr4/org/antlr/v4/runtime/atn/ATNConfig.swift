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

/** A tuple: (ATN state, predicted alt, syntactic, semantic context).
 *  The syntactic context is a graph-structured stack node whose
 *  path(s) to the root is the rule invocation(s)
 *  chain used to arrive at the state.  The semantic context is
 *  the tree of semantic predicates encountered before reaching
 *  an ATN state.
 */
 

public class ATNConfig: Hashable, CustomStringConvertible {
    /**
     * This field stores the bit mask for implementing the
     * {@link #isPrecedenceFilterSuppressed} property as a bit within the
     * existing {@link #reachesIntoOuterContext} field.
     */
    private final let SUPPRESS_PRECEDENCE_FILTER: Int = 0x40000000

    /** The ATN state associated with this configuration */
    public final let state: ATNState

    /** What alt (or lexer rule) is predicted by this configuration */
    public final let alt: Int

    /** The stack of invoking states leading to the rule/states associated
     *  with this config.  We track only those contexts pushed during
     *  execution of the ATN simulator.
     */
    public final var context: PredictionContext?

    /**
     * We cannot execute predicates dependent upon local context unless
     * we know for sure we are in the correct context. Because there is
     * no way to do this efficiently, we simply cannot evaluate
     * dependent predicates unless we are in the rule that initially
     * invokes the ATN simulator.
     *
     * <p>
     * closure() tracks the depth of how far we dip into the outer context:
     * depth &gt; 0.  Note that it may not be totally accurate depth since I
     * don't ever decrement. TODO: make it a boolean then</p>
     *
     * <p>
     * For memory efficiency, the {@link #isPrecedenceFilterSuppressed} method
     * is also backed by this field. Since the field is publicly accessible, the
     * highest bit which would not cause the value to become negative is used to
     * store this field. This choice minimizes the risk that code which only
     * compares this value to 0 would be affected by the new purpose of the
     * flag. It also ensures the performance of the existing {@link org.antlr.v4.runtime.atn.ATNConfig}
     * constructors as well as certain operations like
     * {@link org.antlr.v4.runtime.atn.ATNConfigSet#add(org.antlr.v4.runtime.atn.ATNConfig, DoubleKeyMap)} method are
     * <em>completely</em> unaffected by the change.</p>
     */
    public final var reachesIntoOuterContext: Int = 0
    //=0 intital by janyou


    public final let semanticContext: SemanticContext

    public init(_ old: ATNConfig) {
        // dup
        self.state = old.state
        self.alt = old.alt
        self.context = old.context
        self.semanticContext = old.semanticContext
        self.reachesIntoOuterContext = old.reachesIntoOuterContext
    }

    public convenience init(_ state: ATNState,
                            _ alt: Int,
                            _ context: PredictionContext?) {
        self.init(state, alt, context, SemanticContext.NONE)
    }

    public init(_ state: ATNState,
                _ alt: Int,
                _ context: PredictionContext?,
                _ semanticContext: SemanticContext) {
        self.state = state
        self.alt = alt
        self.context = context
        self.semanticContext = semanticContext
    }

    public convenience init(_ c: ATNConfig, _ state: ATNState) {
        self.init(c, state, c.context, c.semanticContext)
    }

    public convenience init(_ c: ATNConfig, _ state: ATNState,
                            _ semanticContext: SemanticContext) {
        self.init(c, state, c.context, semanticContext)
    }

    public convenience init(_ c: ATNConfig,
                            _ semanticContext: SemanticContext) {
        self.init(c, c.state, c.context, semanticContext)
    }

    public convenience init(_ c: ATNConfig, _ state: ATNState,
                            _ context: PredictionContext?) {
        self.init(c, state, context, c.semanticContext)
    }

    public init(_ c: ATNConfig, _ state: ATNState,
                _ context: PredictionContext?,
                _ semanticContext: SemanticContext) {
        self.state = state
        self.alt = c.alt
        self.context = context
        self.semanticContext = semanticContext
        self.reachesIntoOuterContext = c.reachesIntoOuterContext
    }

    /**
     * This method gets the value of the {@link #reachesIntoOuterContext} field
     * as it existed prior to the introduction of the
     * {@link #isPrecedenceFilterSuppressed} method.
     */
    public final func getOuterContextDepth() -> Int {
        return reachesIntoOuterContext & ~SUPPRESS_PRECEDENCE_FILTER
    }

    public final func isPrecedenceFilterSuppressed() -> Bool {
        return (reachesIntoOuterContext & SUPPRESS_PRECEDENCE_FILTER) != 0
    }

    public final func setPrecedenceFilterSuppressed(_ value: Bool) {
        if value {
            self.reachesIntoOuterContext |= 0x40000000
        } else {
            self.reachesIntoOuterContext &= ~SUPPRESS_PRECEDENCE_FILTER
        }
    }

    /** An ATN configuration is equal to another if both have
     *  the same state, they predict the same alternative, and
     *  syntactic/semantic contexts are the same.
     */

    public var hashValue: Int {
        var hashCode: Int = MurmurHash.initialize(7)
        hashCode = MurmurHash.update(hashCode, state.stateNumber)
        hashCode = MurmurHash.update(hashCode, alt)
        hashCode = MurmurHash.update(hashCode, context)
        hashCode = MurmurHash.update(hashCode, semanticContext)
        hashCode = MurmurHash.finish(hashCode, 4)
        return hashCode

    }

    public func toString() -> String {
        return description
    }
    public var description: String {
        //return "MyClass \(string)"
        return toString(nil, true)
    }
    public func toString<T:ATNSimulator>(_ recog: Recognizer<T>?, _ showAlt: Bool) -> String {
        let buf: StringBuilder = StringBuilder()
//		if ( state.ruleIndex>=0 ) {
//			if ( recog!=null ) buf.append(recog.getRuleNames()[state.ruleIndex]+":");
//			else buf.append(state.ruleIndex+":");
//		}
        buf.append("(")
        buf.append(state)
        if showAlt {
            buf.append(",")
            buf.append(alt)
        }
        //TODO: context can be nil ?
        if context != nil {
            buf.append(",[")
            buf.append(context!)
            buf.append("]")
        }
        //TODO: semanticContext can be nil ?
        //if ( semanticContext != nil && semanticContext != SemanticContext.NONE ) {
        if semanticContext != SemanticContext.NONE {
            buf.append(",")
            buf.append(semanticContext)
        }
        if getOuterContextDepth() > 0 {
            buf.append(",up=").append(getOuterContextDepth())
        }
        buf.append(")")
        return buf.toString()
    }
}

public func ==(lhs: ATNConfig, rhs: ATNConfig) -> Bool {

    if lhs === rhs {
        return true
    }
    //TODO : rhs nil?
    /*else { if (other == nil) {
        return false;
        } }*/
    if (lhs is LexerATNConfig) && (rhs is LexerATNConfig) {
        return (lhs as! LexerATNConfig) == (rhs as! LexerATNConfig)


    }

    if lhs.state.stateNumber != rhs.state.stateNumber {
        return false
    }
    if lhs.alt != rhs.alt {
        return false
    }

    if lhs.isPrecedenceFilterSuppressed() != rhs.isPrecedenceFilterSuppressed() {
        return false
    }

    var contextCompare = false

    if lhs.context == nil && rhs.context == nil {
        contextCompare = true
    } else if lhs.context == nil && rhs.context != nil {
        contextCompare = false
    } else if lhs.context != nil && rhs.context == nil {
        contextCompare = false
    } else {
        contextCompare = (lhs.context! == rhs.context!)
    }

    if !contextCompare {
        return false
    }

    return  lhs.semanticContext == rhs.semanticContext

}
