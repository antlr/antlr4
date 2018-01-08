/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


/// 
/// An ATN transition between any two ATN states.  Subclasses define
/// atom, set, epsilon, action, predicate, rule transitions.
/// 
/// This is a one way link.  It emanates from a state (usually via a list of
/// transitions) and has a target state.
/// 
/// Since we never have to change the ATN transitions once we construct it,
/// we can fix these transitions as specific classes. The DFA transitions
/// on the other hand need to update the labels as it adds transitions to
/// the states. We'll use the term Edge for the DFA to distinguish them from
/// ATN transitions.
/// 

import Foundation

public class Transition {
    // constants for serialization
    public static let EPSILON: Int = 1
    public static let RANGE: Int = 2
    public static let RULE: Int = 3
    public static let PREDICATE: Int = 4
    // e.g., {isType(input.LT(1))}?
    public static let ATOM: Int = 5
    public static let ACTION: Int = 6
    public static let SET: Int = 7
    // ~(A|B) or ~atom, wildcard, which convert to next 2
    public static let NOT_SET: Int = 8
    public static let WILDCARD: Int = 9
    public static let PRECEDENCE: Int = 10


    public let serializationNames: Array<String> =

    ["INVALID",
     "EPSILON",
     "RANGE",
     "RULE",
     "PREDICATE",
     "ATOM",
     "ACTION",
     "SET",
     "NOT_SET",
     "WILDCARD",
     "PRECEDENCE"]


    public static let serializationTypes: Dictionary<String, Int> = [

            NSStringFromClass(EpsilonTransition.self): EPSILON,
            NSStringFromClass(RangeTransition.self): RANGE,
            NSStringFromClass(RuleTransition.self): RULE,
            NSStringFromClass(PredicateTransition.self): PREDICATE,
            NSStringFromClass(AtomTransition.self): ATOM,
            NSStringFromClass(ActionTransition.self): ACTION,
            NSStringFromClass(SetTransition.self): SET,
            NSStringFromClass(NotSetTransition.self): NOT_SET,
            NSStringFromClass(WildcardTransition.self): WILDCARD,
            NSStringFromClass(PrecedencePredicateTransition.self): PRECEDENCE,


    ]


    /// 
    /// The target of this transition.
    /// 

    public final var target: ATNState

    init(_ target: ATNState) {


        self.target = target
    }

    public func getSerializationType() -> Int {
        fatalError(#function + " must be overridden")
    }

    /// 
    /// Determines if the transition is an "epsilon" transition.
    /// 
    /// The default implementation returns `false`.
    /// 
    /// - returns: `true` if traversing this transition in the ATN does not
    /// consume an input symbol; otherwise, `false` if traversing this
    /// transition consumes (matches) an input symbol.
    /// 
    public func isEpsilon() -> Bool {
        return false
    }


    public func labelIntervalSet() -> IntervalSet? {
        return nil
    }

    public func matches(_ symbol: Int, _ minVocabSymbol: Int, _ maxVocabSymbol: Int) -> Bool {
        fatalError(#function + " must be overridden")
    }
}
