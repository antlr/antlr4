/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


public final class NotSetTransition: SetTransition {
//	public override init(_ target : ATNState, inout _ set : IntervalSet?) {
//		super.init(target, &set);
//	}

    override
    public func getSerializationType() -> Int {
        return Transition.NOT_SET
    }

    override
    public func matches(_ symbol: Int, _ minVocabSymbol: Int, _ maxVocabSymbol: Int) -> Bool {
        return symbol >= minVocabSymbol
                && symbol <= maxVocabSymbol
                && !super.matches(symbol, minVocabSymbol, maxVocabSymbol)
    }

    override
    public var description: String {
        return "~" + super.description
    }
}
