/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


public class EmptyPredictionContext: SingletonPredictionContext {
    public init() {
        super.init(nil, PredictionContext.EMPTY_RETURN_STATE)
    }

    override
    public func isEmpty() -> Bool {
        return true
    }

    override
    public func size() -> Int {
        return 1
    }

    override
    public func getParent(_ index: Int) -> PredictionContext? {
        return nil
    }

    override
    public func getReturnState(_ index: Int) -> Int {
        return returnState
    }


    override
    public var description: String {
        return "$"
    }
}


public func ==(lhs: EmptyPredictionContext, rhs: EmptyPredictionContext) -> Bool {
    if lhs === rhs {
        return true
    }

    return false
}
