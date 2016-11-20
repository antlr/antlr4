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


/** An ATN transition between any two ATN states.  Subclasses define
*  atom, set, epsilon, action, predicate, rule transitions.
*
*  <p>This is a one way link.  It emanates from a state (usually via a list of
*  transitions) and has a target state.</p>
*
*  <p>Since we never have to change the ATN transitions once we construct it,
*  we can fix these transitions as specific classes. The DFA transitions
*  on the other hand need to update the labels as it adds transitions to
*  the states. We'll use the term Edge for the DFA to distinguish them from
*  ATN transitions.</p>
*/

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


    /** The target of this transition. */

    public final var target: ATNState

    init(_ target: ATNState) {


        self.target = target
    }

    public func getSerializationType() -> Int {
        RuntimeException(#function + " must be overridden")
        fatalError()
    }

    /**
    * Determines if the transition is an "epsilon" transition.
    *
    * <p>The default implementation returns {@code false}.</p>
    *
    * @return {@code true} if traversing this transition in the ATN does not
    * consume an input symbol; otherwise, {@code false} if traversing this
    * transition consumes (matches) an input symbol.
    */
    public func isEpsilon() -> Bool {
        return false
    }


    public func labelIntervalSet() throws -> IntervalSet? {
        return nil
    }

    public func matches(_ symbol: Int, _ minVocabSymbol: Int, _ maxVocabSymbol: Int) -> Bool {
        RuntimeException(#function + " must be overridden")
        fatalError()
    }
}
