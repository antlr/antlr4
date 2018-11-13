/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


/// 
/// 
/// The following images show the relation of states and
/// _org.antlr.v4.runtime.atn.ATNState#transitions_ for various grammar constructs.
/// 
/// 
/// * Solid edges marked with an &#0949; indicate a required
/// _org.antlr.v4.runtime.atn.EpsilonTransition_.
/// 
/// * Dashed edges indicate locations where any transition derived from
/// _org.antlr.v4.runtime.atn.Transition_ might appear.
/// 
/// * Dashed nodes are place holders for either a sequence of linked
/// _org.antlr.v4.runtime.atn.BasicState_ states or the inclusion of a block representing a nested
/// construct in one of the forms below.
/// 
/// * Nodes showing multiple outgoing alternatives with a `...` support
/// any number of alternatives (one or more). Nodes without the `...` only
/// support the exact number of alternatives shown in the diagram.
/// 
/// 
/// ## Basic Blocks
/// 
/// ### Rule
/// 
/// 
/// 
/// ## Block of 1 or more alternatives
/// 
/// 
/// 
/// ## Greedy Loops
/// 
/// ### Greedy Closure: `(...)*`
/// 
/// 
/// 
/// ### Greedy Positive Closure: `(...)+`
/// 
/// 
/// 
/// ### Greedy Optional: `(...)?`
/// 
/// 
/// 
/// ## Non-Greedy Loops
/// 
/// ### Non-Greedy Closure: `(...)*?`
/// 
/// 
/// 
/// ### Non-Greedy Positive Closure: `(...)+?`
/// 
/// 
/// 
/// ### Non-Greedy Optional: `(...)??`
/// 
/// 
/// 
/// 
public class ATNState: Hashable, CustomStringConvertible {
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

    /// 
    /// Which ATN are we in?
    /// 
    public final var atn: ATN? = nil

    public internal(set) final var stateNumber: Int = INVALID_STATE_NUMBER

    public internal(set) final var ruleIndex: Int?
    // at runtime, we don't have Rule objects

    public private(set) final var epsilonOnlyTransitions: Bool = false

    /// 
    /// Track the transitions emanating from this ATN state.
    /// 
    internal private(set) final var transitions = [Transition]()

    /// 
    /// Used to cache lookahead during parsing, not used during construction
    /// 
    public internal(set) final var nextTokenWithinRule: IntervalSet?


    public var hashValue: Int {
        return stateNumber
    }


    public func isNonGreedyExitState() -> Bool {
        return false
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
        if transitions.isEmpty {
            epsilonOnlyTransitions = e.isEpsilon()
        }
        else if epsilonOnlyTransitions != e.isEpsilon() {
            print("ATN state %d has both epsilon and non-epsilon transitions.\n", String(stateNumber))
            epsilonOnlyTransitions = false
        }

        var alreadyPresent = false
        for t in transitions {
            if t.target.stateNumber == e.target.stateNumber {
                if let tLabel = t.labelIntervalSet(), let eLabel = e.labelIntervalSet(), tLabel == eLabel {
//                    print("Repeated transition upon \(eLabel) from \(stateNumber)->\(t.target.stateNumber)")
                    alreadyPresent = true
                    break
                }
                else if t.isEpsilon() && e.isEpsilon() {
//                    print("Repeated epsilon transition from \(stateNumber)->\(t.target.stateNumber)")
                    alreadyPresent = true
                    break
                }
            }
        }

        if !alreadyPresent {
            transitions.append(e)
        }
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
        fatalError(#function + " must be overridden")
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

