/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


public class EmptyPredictionContext: SingletonPredictionContext {
    public init() {
        super.init(nil, PredictionContext.EMPTY_RETURN_STATE)
    }

    override
    public func isEmpty() -> Bool {
        true
    }

    override
    public func size() -> Int {
        1
    }

    override
    public func getParent(_ index: Int) -> PredictionContext? {
        nil
    }

    override
    public func getReturnState(_ index: Int) -> Int {
        returnState
    }


    override
    public var description: String {
        "$"
    }
}


public func ==(lhs: EmptyPredictionContext, rhs: EmptyPredictionContext) -> Bool {
    if lhs === rhs {
        return true
    }

    return false
}
