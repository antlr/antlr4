/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 



/// 
/// A transition containing a set of values.
/// 

public class SetTransition: Transition, CustomStringConvertible {
    public final var set: IntervalSet

    // TODO (sam): should we really allow null here?
    public init(_ target: ATNState, _ set: IntervalSet) {

        self.set = set
        super.init(target)
    }

    override
    public func getSerializationType() -> Int {
        return Transition.SET
    }

    override
    public func labelIntervalSet() -> IntervalSet? {
        return set
    }

    override
    public func matches(_ symbol: Int, _ minVocabSymbol: Int, _ maxVocabSymbol: Int) -> Bool {
        return set.contains(symbol)
    }

    public var description: String {
        return set.description
    }


}
