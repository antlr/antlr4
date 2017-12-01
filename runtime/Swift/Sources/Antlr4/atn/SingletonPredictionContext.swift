/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 



public class SingletonPredictionContext: PredictionContext {
    public final let parent: PredictionContext?
    public final let returnState: Int

    init(_ parent: PredictionContext?, _ returnState: Int) {

        //TODO assert
        //assert ( returnState=ATNState.INVALID_STATE_NUMBER,"Expected: returnState!/=ATNState.INVALID_STATE_NUMBER");
        self.parent = parent
        self.returnState = returnState


        super.init(parent != nil ? PredictionContext.calculateHashCode(parent!, returnState) : PredictionContext.calculateEmptyHashCode())
    }

    public static func create(_ parent: PredictionContext?, _ returnState: Int) -> SingletonPredictionContext {
        if returnState == PredictionContext.EMPTY_RETURN_STATE && parent == nil {
            // someone can pass in the bits of an array ctx that mean $
            return PredictionContext.EMPTY
        }
        return SingletonPredictionContext(parent, returnState)
    }

    override
    public func size() -> Int {
        return 1
    }

    override
    public func getParent(_ index: Int) -> PredictionContext? {
        assert(index == 0, "Expected: index==0")
        return parent
    }

    override
    public func getReturnState(_ index: Int) -> Int {
        assert(index == 0, "Expected: index==0")
        return returnState
    }


    override
    public var description: String {
        let up = parent?.description ?? ""
        if up.isEmpty {
            if returnState == PredictionContext.EMPTY_RETURN_STATE {
                return "$"
            }
            return String(returnState)
        }
        return String(returnState) + " " + up
    }
}


public func ==(lhs: SingletonPredictionContext, rhs: SingletonPredictionContext) -> Bool {
    if lhs === rhs {
        return true
    }
    if lhs.hashValue != rhs.hashValue {
        return false
    }
    if lhs.returnState != rhs.returnState {
        return false
    }
    var parentCompare = false
    if (lhs.parent == nil) && (rhs.parent == nil) {
        parentCompare = true
    } else if lhs.parent == nil || rhs.parent == nil  {
        parentCompare = false
    } else {
        parentCompare = (lhs.parent! == rhs.parent!)
    }

    return parentCompare
}


