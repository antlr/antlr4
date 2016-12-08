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


public class DFA: CustomStringConvertible {
    /** A set of all DFA states. Use {@link java.util.Map} so we can get old state back
     *  ({@link java.util.Set} only allows you to see if it's there).
     */

    public final var states: HashMap<DFAState, DFAState?> = HashMap<DFAState, DFAState?>()

    public /*volatile*/ var s0: DFAState?

    public final var decision: Int

    /** From which ATN state did we create this DFA? */

    public let atnStartState: DecisionState

    /**
     * {@code true} if this DFA is for a precedence decision; otherwise,
     * {@code false}. This is the backing field for {@link #isPrecedenceDfa}.
     */
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

    /**
     * Gets whether this DFA is a precedence DFA. Precedence DFAs use a special
     * start state {@link #s0} which is not stored in {@link #states}. The
     * {@link org.antlr.v4.runtime.dfa.DFAState#edges} array for this start state contains outgoing edges
     * supplying individual start states corresponding to specific precedence
     * values.
     *
     * @return {@code true} if this is a precedence DFA; otherwise,
     * {@code false}.
     * @see org.antlr.v4.runtime.Parser#getPrecedence()
     */
    public final func isPrecedenceDfa() -> Bool {
        return precedenceDfa
    }

    /**
     * Get the start state for a specific precedence value.
     *
     * @param precedence The current precedence.
     * @return The start state corresponding to the specified precedence, or
     * {@code null} if no start state exists for the specified precedence.
     *
     * @throws IllegalStateException if this is not a precedence DFA.
     * @see #isPrecedenceDfa()
     */
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

    /**
     * Set the start state for a specific precedence value.
     *
     * @param precedence The current precedence.
     * @param startState The start state corresponding to the specified
     * precedence.
     *
     * @throws IllegalStateException if this is not a precedence DFA.
     * @see #isPrecedenceDfa()
     */
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

    /**
     * Sets whether this is a precedence DFA.
     *
     * @param precedenceDfa {@code true} if this is a precedence DFA; otherwise,
     * {@code false}
     *
     * @throws UnsupportedOperationException if {@code precedenceDfa} does not
     * match the value of {@link #isPrecedenceDfa} for the current DFA.
     *
     * @deprecated This method no longer performs any action.
     */
    ////@Deprecated
    public final func setPrecedenceDfa(_ precedenceDfa: Bool) throws {
        if precedenceDfa != isPrecedenceDfa() {
            throw ANTLRError.unsupportedOperation(msg: "The precedenceDfa field cannot change after a DFA is constructed.")

        }
    }

    /**
     * Return a list of all states in this DFA, ordered by state number.
     */

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

    /**
     * @deprecated Use {@link #toString(org.antlr.v4.runtime.Vocabulary)} instead.
     */
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
