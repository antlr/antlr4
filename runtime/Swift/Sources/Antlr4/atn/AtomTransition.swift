/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


/// TODO: make all transitions sets? no, should remove set edges

public final class AtomTransition: Transition, CustomStringConvertible {
    /// The token type or character value; or, signifies special label.
    public let label: Int

    public init(_ target: ATNState, _ label: Int) {

        self.label = label
        super.init(target)
    }

    override
    public func getSerializationType() -> Int {
        return Transition.ATOM
    }

    override
    public func labelIntervalSet() throws -> IntervalSet? {
        return try IntervalSet.of(label)
    }

    override
    public func matches(_ symbol: Int, _ minVocabSymbol: Int, _ maxVocabSymbol: Int) -> Bool {
        return label == symbol
    }


    public var description: String {
        return String(label)
    }
}
