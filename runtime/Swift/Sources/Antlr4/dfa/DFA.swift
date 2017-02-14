/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


public class DFA: CustomStringConvertible {
    /// A set of all DFA states. Use {@link java.util.Map} so we can get old state back
    /// ({@link java.util.Set} only allows you to see if it's there).

    public final var states: HashMap<DFAState, DFAState?> = HashMap<DFAState, DFAState?>()

    public /*volatile*/ var s0: DFAState?

    public final var decision: Int

    /// From which ATN state did we create this DFA?

    public let atnStartState: DecisionState

    /// {@code true} if this DFA is for a precedence decision; otherwise,
    /// {@code false}. This is the backing field for {@link #isPrecedenceDfa}.
    private final var precedenceDfa: Bool

    public convenience init(_ atnStartState: DecisionState) {
        self.init(atnStartState, 0)
    }

    public init(_ atnStartState: DecisionState, _ decision: Int) {
        self.atnStartState = atnStartState
        self.decision = decision

        var precedenceDfa: Bool = false
        if atnStartState is StarLoopEntryState {
            if (atnStartState as! StarLoopEntryState).precedenceRuleDecision {
                precedenceDfa = true
                let precedenceState: DFAState = DFAState(ATNConfigSet())
                precedenceState.edges = [DFAState]() //new DFAState[0];
                precedenceState.isAcceptState = false
                precedenceState.requiresFullContext = false
                self.s0 = precedenceState
            }
        }

        self.precedenceDfa = precedenceDfa
    }

    /// Gets whether this DFA is a precedence DFA. Precedence DFAs use a special
    /// start state {@link #s0} which is not stored in {@link #states}. The
    /// {@link org.antlr.v4.runtime.dfa.DFAState#edges} array for this start state contains outgoing edges
    /// supplying individual start states corresponding to specific precedence
    /// values.
    /// 
    /// - returns: {@code true} if this is a precedence DFA; otherwise,
    /// {@code false}.
    /// - seealso: org.antlr.v4.runtime.Parser#getPrecedence()
    public final func isPrecedenceDfa() -> Bool {
        return precedenceDfa
    }

    /// Get the start state for a specific precedence value.
    /// 
    /// - parameter precedence: The current precedence.
    /// - returns: The start state corresponding to the specified precedence, or
    /// {@code null} if no start state exists for the specified precedence.
    /// 
    /// -  IllegalStateException if this is not a precedence DFA.
    /// - seealso: #isPrecedenceDfa()
    ////@SuppressWarnings("null")
    public final func getPrecedenceStartState(_ precedence: Int) throws -> DFAState? {
        if !isPrecedenceDfa() {
            throw ANTLRError.illegalState(msg: "Only precedence DFAs may contain a precedence start state.")

        }

        // s0.edges is never null for a precedence DFA
        // if (precedence < 0 || precedence >= s0!.edges!.count) {
        if precedence < 0 || s0 == nil ||
                s0!.edges == nil || precedence >= s0!.edges!.count {
            return nil
        }

        return s0!.edges![precedence]
    }

    /// Set the start state for a specific precedence value.
    /// 
    /// - parameter precedence: The current precedence.
    /// - parameter startState: The start state corresponding to the specified
    /// precedence.
    /// 
    /// -  IllegalStateException if this is not a precedence DFA.
    /// - seealso: #isPrecedenceDfa()
    ////@SuppressWarnings({"SynchronizeOnNonFinalField", "null"})
    public final func setPrecedenceStartState(_ precedence: Int, _ startState: DFAState) throws {
        if !isPrecedenceDfa() {
            throw ANTLRError.illegalState(msg: "Only precedence DFAs may contain a precedence start state.")

        }

        guard let s0 = s0,let edges = s0.edges , precedence >= 0 else {
            return
        }
        // synchronization on s0 here is ok. when the DFA is turned into a
        // precedence DFA, s0 will be initialized once and not updated again
        synced(s0) {
            // s0.edges is never null for a precedence DFA
            if precedence >= edges.count {
                let increase = [DFAState?](repeating: nil, count: (precedence + 1 - edges.count))
                s0.edges = edges + increase
                //Array( self.s0!.edges![0..<precedence + 1])
                //s0.edges = Arrays.copyOf(s0.edges, precedence + 1);
            }

            s0.edges[precedence] = startState
        }
    }

    /// Sets whether this is a precedence DFA.
    /// 
    /// - parameter precedenceDfa: {@code true} if this is a precedence DFA; otherwise,
    /// {@code false}
    /// 
    /// -  UnsupportedOperationException if {@code precedenceDfa} does not
    /// match the value of {@link #isPrecedenceDfa} for the current DFA.
    /// 
    /// -  This method no longer performs any action.
    ////@Deprecated
    public final func setPrecedenceDfa(_ precedenceDfa: Bool) throws {
        if precedenceDfa != isPrecedenceDfa() {
            throw ANTLRError.unsupportedOperation(msg: "The precedenceDfa field cannot change after a DFA is constructed.")

        }
    }

    /// Return a list of all states in this DFA, ordered by state number.

    public func getStates() -> Array<DFAState> {
        var result: Array<DFAState> = Array<DFAState>(states.keys)

        result = result.sorted {
            $0.stateNumber < $1.stateNumber
        }

        return result
    }

    public var description: String {
        return toString(Vocabulary.EMPTY_VOCABULARY)
    }


    public func toString() -> String {
        return description
    }

    /// -  Use {@link #toString(org.antlr.v4.runtime.Vocabulary)} instead.
    ////@Deprecated
    public func toString(_ tokenNames: [String?]?) -> String {
        if s0 == nil {
            return ""
        }
        let serializer: DFASerializer = DFASerializer(self, tokenNames)
        return serializer.toString()
    }

    public func toString(_ vocabulary: Vocabulary) -> String {
        if s0 == nil {
            return ""
        }

        let serializer: DFASerializer = DFASerializer(self, vocabulary)
        return serializer.toString()
    }

    public func toLexerString() -> String {
        if s0 == nil {
            return ""
        }
        let serializer: DFASerializer = LexerDFASerializer(self)
        return serializer.toString()
    }

}
