/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 

/// 
/// A tuple: (ATN state, predicted alt, syntactic, semantic context).
/// The syntactic context is a graph-structured stack node whose
/// path(s) to the root is the rule invocation(s)
/// chain used to arrive at the state.  The semantic context is
/// the tree of semantic predicates encountered before reaching
/// an ATN state.
/// 


public class ATNConfig: Hashable, CustomStringConvertible {
    /// 
    /// This field stores the bit mask for implementing the
    /// _#isPrecedenceFilterSuppressed_ property as a bit within the
    /// existing _#reachesIntoOuterContext_ field.
    /// 
    private final let SUPPRESS_PRECEDENCE_FILTER: Int = 0x40000000

    /// 
    /// The ATN state associated with this configuration
    /// 
    public final let state: ATNState

    /// 
    /// What alt (or lexer rule) is predicted by this configuration
    /// 
    public final let alt: Int

    /// 
    /// The stack of invoking states leading to the rule/states associated
    /// with this config.  We track only those contexts pushed during
    /// execution of the ATN simulator.
    /// 
    public internal(set) final var context: PredictionContext?

    /// 
    /// We cannot execute predicates dependent upon local context unless
    /// we know for sure we are in the correct context. Because there is
    /// no way to do this efficiently, we simply cannot evaluate
    /// dependent predicates unless we are in the rule that initially
    /// invokes the ATN simulator.
    /// 
    /// 
    /// closure() tracks the depth of how far we dip into the outer context:
    /// depth &gt; 0.  Note that it may not be totally accurate depth since I
    /// don't ever decrement. TODO: make it a boolean then
    /// 
    /// 
    /// For memory efficiency, the _#isPrecedenceFilterSuppressed_ method
    /// is also backed by this field. Since the field is publicly accessible, the
    /// highest bit which would not cause the value to become negative is used to
    /// store this field. This choice minimizes the risk that code which only
    /// compares this value to 0 would be affected by the new purpose of the
    /// flag. It also ensures the performance of the existing _org.antlr.v4.runtime.atn.ATNConfig_
    /// constructors as well as certain operations like
    /// _org.antlr.v4.runtime.atn.ATNConfigSet#add(org.antlr.v4.runtime.atn.ATNConfig, DoubleKeyMap)_ method are
    /// __completely__ unaffected by the change.
    /// 
    public internal(set) final var reachesIntoOuterContext: Int = 0

    public final let semanticContext: SemanticContext

    public init(_ state: ATNState,
                _ alt: Int,
                _ context: PredictionContext?,
                _ semanticContext: SemanticContext = SemanticContext.NONE) {
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

    /// 
    /// This method gets the value of the _#reachesIntoOuterContext_ field
    /// as it existed prior to the introduction of the
    /// _#isPrecedenceFilterSuppressed_ method.
    /// 
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

    public func hash(into hasher: inout Hasher) {
        hasher.combine(state.stateNumber)
        hasher.combine(alt)
        hasher.combine(context)
        hasher.combine(semanticContext)
    }

    public var description: String {
        //return "MyClass \(string)"
        return toString(nil, true)
    }
    public func toString<T>(_ recog: Recognizer<T>?, _ showAlt: Bool) -> String {
        var buf = "(\(state)"
        if showAlt {
            buf += ",\(alt)"
        }
        if let context = context {
            buf += ",[\(context)]"
        }
        if semanticContext != SemanticContext.NONE {
            buf += ",\(semanticContext)"
        }
        let outerDepth = getOuterContextDepth()
        if outerDepth > 0 {
            buf += ",up=\(outerDepth)"
        }
        buf += ")"
        return buf
    }
}

///
/// An ATN configuration is equal to another if both have
/// the same state, they predict the same alternative, and
/// syntactic/semantic contexts are the same.
///
public func ==(lhs: ATNConfig, rhs: ATNConfig) -> Bool {

    if lhs === rhs {
        return true
    }
    
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
