/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


public final class RangeTransition: Transition, CustomStringConvertible {
    public let from: Int
    public let to: Int

    public init(_ target: ATNState, _ from: Int, _ to: Int) {

        self.from = from
        self.to = to
        super.init(target)
    }

    override
    public func getSerializationType() -> Int {
        return Transition.RANGE
    }

    override
    //old label()
    public func labelIntervalSet() throws -> IntervalSet {
        return try IntervalSet.of(from, to)
    }

    override
    public func matches(_ symbol: Int, _ minVocabSymbol: Int, _ maxVocabSymbol: Int) -> Bool {
        return symbol >= from && symbol <= to
    }


    public func toString() -> String {
        return description
    }

    public var description: String {
        return "'" + String(from) + "'..'" + String(to) + "'"

    }
}
