/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


final public class WildcardTransition: Transition, CustomStringConvertible {
    public override init(_ target: ATNState) {
        super.init(target)
    }

    override
    public func getSerializationType() -> Int {
        return Transition.WILDCARD
    }

    override
    public func matches(_ symbol: Int, _ minVocabSymbol: Int, _ maxVocabSymbol: Int) -> Bool {
        return symbol >= minVocabSymbol && symbol <= maxVocabSymbol
    }

    public var description: String {

        return "."
    }


}
