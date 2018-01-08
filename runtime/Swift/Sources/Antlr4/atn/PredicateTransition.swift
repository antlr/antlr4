/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 



/// 
/// TODO: this is old comment:
/// A tree of semantic predicates from the grammar AST if label==SEMPRED.
/// In the ATN, labels will always be exactly one predicate, but the DFA
/// may have to combine a bunch of them as it collects predicates from
/// multiple ATN configurations into a single DFA state.
/// 

public final class PredicateTransition: AbstractPredicateTransition, CustomStringConvertible {
    public let ruleIndex: Int
    public let predIndex: Int
    public let isCtxDependent: Bool
    // e.g., $i ref in pred

    public init(_ target: ATNState, _ ruleIndex: Int, _ predIndex: Int, _ isCtxDependent: Bool) {

        self.ruleIndex = ruleIndex
        self.predIndex = predIndex
        self.isCtxDependent = isCtxDependent
        super.init(target)
    }

    override
    public func getSerializationType() -> Int {
        return PredicateTransition.PREDICATE
    }

    override
    public func isEpsilon() -> Bool {
        return true
    }

    override
    public func matches(_ symbol: Int, _ minVocabSymbol: Int, _ maxVocabSymbol: Int) -> Bool {
        return false
    }

    public func getPredicate() -> SemanticContext.Predicate {
        return SemanticContext.Predicate(ruleIndex, predIndex, isCtxDependent)
    }

    public var description: String {
        return "pred_\(ruleIndex):\(predIndex)"
    }
}
