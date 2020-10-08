/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


/// 
/// A DFA state represents a set of possible ATN configurations.
/// As Aho, Sethi, Ullman p. 117 says "The DFA uses its state
/// to keep track of all possible states the ATN can be in after
/// reading each input symbol.  That is to say, after reading
/// input a1a2..an, the DFA is in a state that represents the
/// subset T of the states of the ATN that are reachable from the
/// ATN's start state along some path labeled a1a2..an."
/// In conventional NFA&rarr;DFA conversion, therefore, the subset T
/// would be a bitset representing the set of states the
/// ATN could be in.  We need to track the alt predicted by each
/// state as well, however.  More importantly, we need to maintain
/// a stack of states, tracking the closure operations as they
/// jump from rule to rule, emulating rule invocations (method calls).
/// I have to add a stack to simulate the proper lookahead sequences for
/// the underlying LL grammar from which the ATN was derived.
/// 
/// I use a set of ATNConfig objects not simple states.  An ATNConfig
/// is both a state (ala normal conversion) and a RuleContext describing
/// the chain of rules (if any) followed to arrive at that state.
/// 
/// A DFA state may have multiple references to a particular state,
/// but with different ATN contexts (with same or different alts)
/// meaning that state was reached via a different set of rule invocations.
/// 

public final class DFAState: Hashable, CustomStringConvertible {
    public internal(set) var stateNumber = -1

    public internal(set) var configs: ATNConfigSet

    /// 
    /// `edges[symbol]` points to target of symbol. Shift up by 1 so (-1)
    /// _org.antlr.v4.runtime.Token#EOF_ maps to `edges[0]`.
    ///
    public internal(set) var edges: [DFAState?]!

    public internal(set) var isAcceptState = false

    /// 
    /// if accept state, what ttype do we match or alt do we predict?
    /// This is set to _org.antlr.v4.runtime.atn.ATN#INVALID_ALT_NUMBER_ when _#predicates_`!=null` or
    /// _#requiresFullContext_.
    /// 
    public internal(set) var prediction = 0

    public internal(set) var lexerActionExecutor: LexerActionExecutor?

    /// 
    /// Indicates that this state was created during SLL prediction that
    /// discovered a conflict between the configurations in the state. Future
    /// _org.antlr.v4.runtime.atn.ParserATNSimulator#execATN_ invocations immediately jumped doing
    /// full context prediction if this field is true.
    /// 
    public internal(set) var requiresFullContext = false

    /// 
    /// During SLL parsing, this is a list of predicates associated with the
    /// ATN configurations of the DFA state. When we have predicates,
    /// _#requiresFullContext_ is `false` since full context prediction evaluates predicates
    /// on-the-fly. If this is not null, then _#prediction_ is
    /// _org.antlr.v4.runtime.atn.ATN#INVALID_ALT_NUMBER_.
    /// 
    /// We only use these for non-_#requiresFullContext_ but conflicting states. That
    /// means we know from the context (it's $ or we don't dip into outer
    /// context) that it's an ambiguity not a conflict.
    /// 
    /// This list is computed by _org.antlr.v4.runtime.atn.ParserATNSimulator#predicateDFAState_.
    /// 

    public internal(set) var predicates: [PredPrediction]?

    /// 
    /// Map a predicate to a predicted alternative.
    /// 

    public final class PredPrediction: CustomStringConvertible {
        public let pred: SemanticContext
        // never null; at least SemanticContext.NONE
        public let alt: Int

        public init(_ pred: SemanticContext, _ alt: Int) {
            self.alt = alt
            self.pred = pred
        }

        public var description: String {
            return "(\(pred),\(alt))"
        }
    }

    public init(_ configs: ATNConfigSet) {
        self.configs = configs
    }

    /// 
    /// Get the set of all alts mentioned by all ATN configurations in this
    /// DFA state.
    /// 
    public func getAltSet() -> Set<Int>? {
        return configs.getAltSet()
    }


    public func hash(into hasher: inout Hasher) {
        hasher.combine(configs)
    }

    public var description: String {
        var buf = "\(stateNumber):\(configs)"
        if isAcceptState {
            buf += "=>"
            if let predicates = predicates {
                buf += String(describing: predicates)
            }
            else {
                buf += String(prediction)
            }
        }
        return buf
    }
}

///
/// Two _org.antlr.v4.runtime.dfa.DFAState_ instances are equal if their ATN configuration sets
/// are the same. This method is used to see if a state already exists.
///
/// Because the number of alternatives and number of ATN configurations are
/// finite, there is a finite number of DFA states that can be processed.
/// This is necessary to show that the algorithm terminates.
///
/// Cannot test the DFA state numbers here because in
/// _org.antlr.v4.runtime.atn.ParserATNSimulator#addDFAState_ we need to know if any other state
/// exists that has this exact set of ATN configurations. The
/// _#stateNumber_ is irrelevant.
///
public func ==(lhs: DFAState, rhs: DFAState) -> Bool {
    if lhs === rhs {
        return true
    }
    return (lhs.configs == rhs.configs)
}
