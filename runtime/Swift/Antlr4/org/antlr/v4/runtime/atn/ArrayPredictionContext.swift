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


public class ArrayPredictionContext: PredictionContext {
    /** Parent can be null only if full ctx mode and we make an array
     *  from {@link #EMPTY} and non-empty. We merge {@link #EMPTY} by using null parent and
     *  returnState == {@link #EMPTY_RETURN_STATE}.
     */
    public final var parents: [PredictionContext?]
    
    /** Sorted for merge, no duplicates; if present,
     *  {@link #EMPTY_RETURN_STATE} is always last.
     */
    public final let returnStates: [Int]
    
    public convenience init(_ a: SingletonPredictionContext) {
//        if a.parent == nil {
//            // print("parent is nil")
//        }
        //self.init(new, PredictionContext[] {a.parent}, new, int[] {a.returnState});
        let parents = [a.parent]
        self.init(parents, [a.returnState])
    }
    
    public init(_ parents: [PredictionContext?], _ returnStates: [Int]) {
        
        self.parents = parents
        self.returnStates = returnStates
        super.init(PredictionContext.calculateHashCode(parents, returnStates))
    }
    
    override
    final public func isEmpty() -> Bool {
        // since EMPTY_RETURN_STATE can only appear in the last position, we
        // don't need to verify that size==1
        return returnStates[0] == PredictionContext.EMPTY_RETURN_STATE
    }
    
    override
    final public func size() -> Int {
        return returnStates.count
    }
    
    override
    final public func getParent(_ index: Int) -> PredictionContext? {
        return parents[index]
    }
    
    override
    final public func getReturnState(_ index: Int) -> Int {
        return returnStates[index]
    }
    
    //	@Override
    //	public int findReturnState(int returnState) {
    //		return Arrays.binarySearch(returnStates, returnState);
    //	}
    
    
    override
    public var description: String {
        if isEmpty() {
            return "[]"
        }
        let buf: StringBuilder = StringBuilder()
        buf.append("[")
        let length = returnStates.count
        
        for i in 0..<length {
            if i > 0 {
                buf.append(", ")
            }
            if returnStates[i] == PredictionContext.EMPTY_RETURN_STATE {
                buf.append("$")
                continue
            }
            buf.append(returnStates[i])
            if parents[i] != nil {
                buf.append(" ")
                buf.append(parents[i].debugDescription)
            } else {
                buf.append("null")
            }
        }
        buf.append("]")
        return buf.toString()
    }
    
    internal final func combineCommonParents() {

        let length = parents.count
        var uniqueParents: Dictionary<PredictionContext, PredictionContext> =
        Dictionary<PredictionContext, PredictionContext>()
        for p in 0..<length {
            if let parent: PredictionContext = parents[p] {
                // if !uniqueParents.keys.contains(parent) {
                if uniqueParents[parent] == nil {
                    uniqueParents[parent] = parent  // don't replace
                }
            }
        }
        
        for p in 0..<length {
            if let parent: PredictionContext = parents[p] {
                parents[p] = uniqueParents[parent]
            }
        }
        
    }
}


public func ==(lhs: ArrayPredictionContext, rhs: ArrayPredictionContext) -> Bool {
    if lhs === rhs {
        return true
    }
    if lhs.hashValue != rhs.hashValue {
        return false
    }
    
    // return lhs.returnStates == rhs.returnStates && lhs.parents == rhs.parents
    
    return ArrayEquals(lhs.returnStates, rhs.returnStates) && ArrayEquals(lhs.parents, rhs.parents)
}

