/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


public class DFA: CustomStringConvertible {
    /// 
    /// A set of all DFA states.
    /// 
    public var states = [DFAState: DFAState]()

    public var s0: DFAState?

    public let decision: Int

    /// 
    /// From which ATN state did we create this DFA?
    ///
    public let atnStartState: DecisionState

    /// 
    /// `true` if this DFA is for a precedence decision; otherwise,
    /// `false`. This is the backing field for _#isPrecedenceDfa_.
    /// 
    private let precedenceDfa: Bool
    
    /// 
    /// mutex for DFAState changes.
    /// 
    private let dfaStateMutex = Mutex()

    public convenience init(_ atnStartState: DecisionState) {
        self.init(atnStartState, 0)
    }

    public init(_ atnStartState: DecisionState, _ decision: Int) {
        self.atnStartState = atnStartState
        self.decision = decision

        if let starLoopState = atnStartState as? StarLoopEntryState, starLoopState.precedenceRuleDecision {
            let precedenceState = DFAState(ATNConfigSet())
            precedenceState.edges = [DFAState]()
            precedenceState.isAcceptState = false
            precedenceState.requiresFullContext = false

            precedenceDfa = true
            s0 = precedenceState
        }
        else {
            precedenceDfa = false
            s0 = nil
        }
    }

    /// 
    /// Gets whether this DFA is a precedence DFA. Precedence DFAs use a special
    /// start state _#s0_ which is not stored in _#states_. The
    /// _org.antlr.v4.runtime.dfa.DFAState#edges_ array for this start state contains outgoing edges
    /// supplying individual start states corresponding to specific precedence
    /// values.
    /// 
    /// - returns: `true` if this is a precedence DFA; otherwise,
    /// `false`.
    /// - seealso: org.antlr.v4.runtime.Parser#getPrecedence()
    /// 
    public final func isPrecedenceDfa() -> Bool {
        return precedenceDfa
    }

    /// 
    /// Get the start state for a specific precedence value.
    /// 
    /// - parameter precedence: The current precedence.
    /// - returns: The start state corresponding to the specified precedence, or
    /// `null` if no start state exists for the specified precedence.
    /// 
    /// - throws: _ANTLRError.illegalState_ if this is not a precedence DFA.
    /// - seealso: #isPrecedenceDfa()
    /// 
    public final func getPrecedenceStartState(_ precedence: Int) throws -> DFAState? {
        if !isPrecedenceDfa() {
            throw ANTLRError.illegalState(msg: "Only precedence DFAs may contain a precedence start state.")

        }

        guard let s0 = s0, let edges = s0.edges, precedence >= 0, precedence < edges.count else {
            return nil
        }

        return edges[precedence]
    }

    /// 
    /// Set the start state for a specific precedence value.
    /// 
    /// - parameter precedence: The current precedence.
    /// - parameter startState: The start state corresponding to the specified
    /// precedence.
    /// 
    /// - throws: _ANTLRError.illegalState_ if this is not a precedence DFA.
    /// - seealso: #isPrecedenceDfa()
    /// 
    public final func setPrecedenceStartState(_ precedence: Int, _ startState: DFAState) throws {
        if !isPrecedenceDfa() {
            throw ANTLRError.illegalState(msg: "Only precedence DFAs may contain a precedence start state.")
        }

        guard let s0 = s0, let edges = s0.edges, precedence >= 0 else {
            return
        }

        // synchronization on s0 here is ok. when the DFA is turned into a
        // precedence DFA, s0 will be initialized once and not updated again
        dfaStateMutex.synchronized {
            // s0.edges is never null for a precedence DFA
            if precedence >= edges.count {
                let increase = [DFAState?](repeating: nil, count: (precedence + 1 - edges.count))
                s0.edges = edges + increase
            }

            s0.edges[precedence] = startState
        }
    }

    ///
    /// Return a list of all states in this DFA, ordered by state number.
    /// 
    public func getStates() -> [DFAState] {
        var result = [DFAState](states.keys)

        result = result.sorted {
            $0.stateNumber < $1.stateNumber
        }

        return result
    }

    public var description: String {
        return toString(Vocabulary.EMPTY_VOCABULARY)
    }

    public func toString(_ vocabulary: Vocabulary) -> String {
        if s0 == nil {
            return ""
        }

        let serializer = DFASerializer(self, vocabulary)
        return serializer.description
    }

    public func toLexerString() -> String {
        if s0 == nil {
            return ""
        }
        let serializer = LexerDFASerializer(self)
        return serializer.description
    }

}
