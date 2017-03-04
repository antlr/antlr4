/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


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
/// <p>I use a set of ATNConfig objects not simple states.  An ATNConfig
/// is both a state (ala normal conversion) and a RuleContext describing
/// the chain of rules (if any) followed to arrive at that state.</p>
/// 
/// <p>A DFA state may have multiple references to a particular state,
/// but with different ATN contexts (with same or different alts)
/// meaning that state was reached via a different set of rule invocations.</p>

public class DFAState: Hashable, CustomStringConvertible {
    public var stateNumber: Int = -1


    public var configs: ATNConfigSet = ATNConfigSet()

    /// {@code edges[symbol]} points to target of symbol. Shift up by 1 so (-1)
    /// {@link org.antlr.v4.runtime.Token#EOF} maps to {@code edges[0]}.

    public var edges: [DFAState?]!

    public var isAcceptState: Bool = false

    /// if accept state, what ttype do we match or alt do we predict?
    /// This is set to {@link org.antlr.v4.runtime.atn.ATN#INVALID_ALT_NUMBER} when {@link #predicates}{@code !=null} or
    /// {@link #requiresFullContext}.
    public var prediction: Int! = 0

    public var lexerActionExecutor: LexerActionExecutor!

    /// Indicates that this state was created during SLL prediction that
    /// discovered a conflict between the configurations in the state. Future
    /// {@link org.antlr.v4.runtime.atn.ParserATNSimulator#execATN} invocations immediately jumped doing
    /// full context prediction if this field is true.
    public var requiresFullContext: Bool = false

    /// During SLL parsing, this is a list of predicates associated with the
    /// ATN configurations of the DFA state. When we have predicates,
    /// {@link #requiresFullContext} is {@code false} since full context prediction evaluates predicates
    /// on-the-fly. If this is not null, then {@link #prediction} is
    /// {@link org.antlr.v4.runtime.atn.ATN#INVALID_ALT_NUMBER}.
    /// 
    /// <p>We only use these for non-{@link #requiresFullContext} but conflicting states. That
    /// means we know from the context (it's $ or we don't dip into outer
    /// context) that it's an ambiguity not a conflict.</p>
    /// 
    /// <p>This list is computed by {@link org.antlr.v4.runtime.atn.ParserATNSimulator#predicateDFAState}.</p>

    public var predicates: [PredPrediction]?

    /// Map a predicate to a predicted alternative.

    public class PredPrediction: CustomStringConvertible {

        public final var pred: SemanticContext
        // never null; at least SemanticContext.NONE
        public final var alt: Int
        public init(_ pred: SemanticContext, _ alt: Int) {
            self.alt = alt
            self.pred = pred
        }


        public var description: String {

            return "(\(pred),\(alt))"

        }
    }

    public init() {
    }

    public init(_ stateNumber: Int) {
        self.stateNumber = stateNumber
    }

    public init(_ configs: ATNConfigSet) {
        self.configs = configs
    }

    /// Get the set of all alts mentioned by all ATN configurations in this
    /// DFA state.
    public func getAltSet() -> Set<Int>? {

        let alts = configs.getAltSet()

        return alts
    }


    public var hashValue: Int {
        var hash: Int = MurmurHash.initialize(7)
        hash = MurmurHash.update(hash, configs.hashValue)
        hash = MurmurHash.finish(hash, 1)
        return hash
    }

    /// Two {@link org.antlr.v4.runtime.dfa.DFAState} instances are equal if their ATN configuration sets
    /// are the same. This method is used to see if a state already exists.
    /// 
    /// <p>Because the number of alternatives and number of ATN configurations are
    /// finite, there is a finite number of DFA states that can be processed.
    /// This is necessary to show that the algorithm terminates.</p>
    /// 
    /// <p>Cannot test the DFA state numbers here because in
    /// {@link org.antlr.v4.runtime.atn.ParserATNSimulator#addDFAState} we need to know if any other state
    /// exists that has this exact set of ATN configurations. The
    /// {@link #stateNumber} is irrelevant.</p>

    public var description: String {
        let buf: StringBuilder = StringBuilder()
        buf.append(stateNumber).append(":").append(configs)
        if isAcceptState {
            buf.append("=>")
            if let predicates = predicates {
                buf.append(predicates.map({ $0.description }))
            } else {
                buf.append(prediction)
            }
        }
        return buf.toString()
    }


}

public func ==(lhs: DFAState, rhs: DFAState) -> Bool {

    if lhs === rhs {
        return true
    }
    let sameSet: Bool = lhs.configs == rhs.configs
    return sameSet
}
