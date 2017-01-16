/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.



/// The following images show the relation of states and
/// {@link org.antlr.v4.runtime.atn.ATNState#transitions} for various grammar constructs.
/// 
/// <ul>
/// 
/// <li>Solid edges marked with an &#0949; indicate a required
/// {@link org.antlr.v4.runtime.atn.EpsilonTransition}.</li>
/// 
/// <li>Dashed edges indicate locations where any transition derived from
/// {@link org.antlr.v4.runtime.atn.Transition} might appear.</li>
/// 
/// <li>Dashed nodes are place holders for either a sequence of linked
/// {@link org.antlr.v4.runtime.atn.BasicState} states or the inclusion of a block representing a nested
/// construct in one of the forms below.</li>
/// 
/// <li>Nodes showing multiple outgoing alternatives with a {@code ...} support
/// any number of alternatives (one or more). Nodes without the {@code ...} only
/// support the exact number of alternatives shown in the diagram.</li>
/// 
/// </ul>
/// 
/// <h2>Basic Blocks</h2>
/// 
/// <h3>Rule</h3>
/// 
/// <embed src="images/Rule.svg" type="image/svg+xml"/>
/// 
/// <h3>Block of 1 or more alternatives</h3>
/// 
/// <embed src="images/Block.svg" type="image/svg+xml"/>
/// 
/// <h2>Greedy Loops</h2>
/// 
/// <h3>Greedy Closure: {@code (...)*}</h3>
/// 
/// <embed src="images/ClosureGreedy.svg" type="image/svg+xml"/>
/// 
/// <h3>Greedy Positive Closure: {@code (...)+}</h3>
/// 
/// <embed src="images/PositiveClosureGreedy.svg" type="image/svg+xml"/>
/// 
/// <h3>Greedy Optional: {@code (...)?}</h3>
/// 
/// <embed src="images/OptionalGreedy.svg" type="image/svg+xml"/>
/// 
/// <h2>Non-Greedy Loops</h2>
/// 
/// <h3>Non-Greedy Closure: {@code (...)*?}</h3>
/// 
/// <embed src="images/ClosureNonGreedy.svg" type="image/svg+xml"/>
/// 
/// <h3>Non-Greedy Positive Closure: {@code (...)+?}</h3>
/// 
/// <embed src="images/PositiveClosureNonGreedy.svg" type="image/svg+xml"/>
/// 
/// <h3>Non-Greedy Optional: {@code (...)??}</h3>
/// 
/// <embed src="images/OptionalNonGreedy.svg" type="image/svg+xml"/>

public class ATNState: Hashable, CustomStringConvertible {
    public static let INITIAL_NUM_TRANSITIONS: Int = 4

    // constants for serialization
    public static let INVALID_TYPE: Int = 0
    public static let BASIC: Int = 1
    public static let RULE_START: Int = 2
    public static let BLOCK_START: Int = 3
    public static let PLUS_BLOCK_START: Int = 4
    public static let STAR_BLOCK_START: Int = 5
    public static let TOKEN_START: Int = 6
    public static let RULE_STOP: Int = 7
    public static let BLOCK_END: Int = 8
    public static let STAR_LOOP_BACK: Int = 9
    public static let STAR_LOOP_ENTRY: Int = 10
    public static let PLUS_LOOP_BACK: Int = 11
    public static let LOOP_END: Int = 12

    public static let serializationNames: Array<String> =

    ["INVALID",
        "BASIC",
        "RULE_START",
        "BLOCK_START",
        "PLUS_BLOCK_START",
        "STAR_BLOCK_START",
        "TOKEN_START",
        "RULE_STOP",
        "BLOCK_END",
        "STAR_LOOP_BACK",
        "STAR_LOOP_ENTRY",
        "PLUS_LOOP_BACK",
        "LOOP_END"]


    public static let INVALID_STATE_NUMBER: Int = -1

    /// Which ATN are we in?
    public final var atn: ATN? = nil

    public final var stateNumber: Int = INVALID_STATE_NUMBER

    public final var ruleIndex: Int?
    // at runtime, we don't have Rule objects

    public final var epsilonOnlyTransitions: Bool = false

    /// Track the transitions emanating from this ATN state.
    internal final var transitions: Array<Transition> = Array<Transition>()
    //Array<Transition>(INITIAL_NUM_TRANSITIONS);

    /// Used to cache lookahead during parsing, not used during construction
    public final var nextTokenWithinRule: IntervalSet?


    public var hashValue: Int {
        return stateNumber
    }


    public func isNonGreedyExitState() -> Bool {
        return false
    }


    public func toString() -> String {
        return description
    }
    public var description: String {
        //return "MyClass \(string)"
        return String(stateNumber)
    }
    public final func getTransitions() -> [Transition] {
        return transitions
    }

    public final func getNumberOfTransitions() -> Int {
        return transitions.count
    }

    public final func addTransition(_ e: Transition) {
        addTransition(transitions.count, e)
    }

    public final func addTransition(_ index: Int, _ e: Transition) {
        if transitions.isEmpty {
            epsilonOnlyTransitions = e.isEpsilon()
        } else {
            if epsilonOnlyTransitions != e.isEpsilon() {

                print("ATN state %d has both epsilon and non-epsilon transitions.\n", String(stateNumber))
                epsilonOnlyTransitions = false
            }
        }
        transitions.insert(e, at: index)

    }

    public final func transition(_ i: Int) -> Transition {
        return transitions[i]
    }

    public final func setTransition(_ i: Int, _ e: Transition) {
        transitions[i] = e
    }

    public final func removeTransition(_ index: Int) -> Transition {

        return transitions.remove(at: index)
    }

    public func getStateType() -> Int {
        RuntimeException(#function + " must be overridden")
        return 0
    }

    public final func onlyHasEpsilonTransitions() -> Bool {
        return epsilonOnlyTransitions
    }

    public final func setRuleIndex(_ ruleIndex: Int) {
        self.ruleIndex = ruleIndex
    }
}

public func ==(lhs: ATNState, rhs: ATNState) -> Bool {
    if lhs === rhs {
        return true
    }
    // are these states same object?
    return lhs.stateNumber == rhs.stateNumber

}

