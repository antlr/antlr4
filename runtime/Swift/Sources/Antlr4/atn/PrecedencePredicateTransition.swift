/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.



/// 
/// -  Sam Harwell

public final class PrecedencePredicateTransition: AbstractPredicateTransition, CustomStringConvertible {
    public final var precedence: Int

    public init(_ target: ATNState, _ precedence: Int) {

        self.precedence = precedence
        super.init(target)
    }

    override
    public func getSerializationType() -> Int {
        return Transition.PRECEDENCE
    }

    override
    public func isEpsilon() -> Bool {
        return true
    }

    override
    public func matches(_ symbol: Int, _ minVocabSymbol: Int, _ maxVocabSymbol: Int) -> Bool {
        return false
    }

    public func getPredicate() -> SemanticContext.PrecedencePredicate {
        return SemanticContext.PrecedencePredicate(precedence)
    }

    public var description: String {
        return "\(precedence)  >= _p"
    }

    public func toString() -> String {
        return description
    }

}
