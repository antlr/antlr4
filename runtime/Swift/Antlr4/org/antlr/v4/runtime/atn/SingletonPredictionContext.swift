/*
* [The "BSD license"]
*  Copyright (c) 2012 Terence Parr
*  Copyright (c) 2012 Sam Harwell
*  Copyright (c) 2015 Janyou
*  All rights reserved.
*
*  Redistribution and use in source and binary forms, with or without
*  modification, are permitted provided that the following conditions
*  are met:
*
*  1. Redistributions of source code must retain the above copyright
*     notice, this list of conditions and the following disclaimer.
*  2. Redistributions in binary form must reproduce the above copyright
*     notice, this list of conditions and the following disclaimer in the
*     documentation and/or other materials provided with the distribution.
*  3. The name of the author may not be used to endorse or promote products
*     derived from this software without specific prior written permission.
*
*  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
*  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
*  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
*  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
*  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
*  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
*  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
*  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
*  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
*  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/



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
        let up: String = parent != nil ? parent!.description : ""
        if up.length == 0 {
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


