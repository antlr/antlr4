/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
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


import Foundation

open class ATNSimulator {
    /**
     * @deprecated Use {@link org.antlr.v4.runtime.atn.ATNDeserializer#SERIALIZED_VERSION} instead.
     */
    public static let SERIALIZED_VERSION: Int = {
        return ATNDeserializer.SERIALIZED_VERSION
    }()


    /**
     * This is the current serialized UUID.
     * @deprecated Use {@link org.antlr.v4.runtime.atn.ATNDeserializer#checkCondition(boolean)} instead.
     */
    public static let SERIALIZED_UUID: UUID = {
        return (ATNDeserializer.SERIALIZED_UUID as UUID)
    }()


    /** Must distinguish between missing edge and edge we know leads nowhere */

    public static let ERROR: DFAState = {
        let error = DFAState(ATNConfigSet())
        error.stateNumber = Int.max
        return error
    }()

    public var atn: ATN

    /** The context cache maps all PredictionContext objects that are equals()
     *  to a single cached copy. This cache is shared across all contexts
     *  in all ATNConfigs in all DFA states.  We rebuild each ATNConfigSet
     *  to use only cached nodes/graphs in addDFAState(). We don't want to
     *  fill this during closure() since there are lots of contexts that
     *  pop up but are not used ever again. It also greatly slows down closure().
     *
     *  <p>This cache makes a huge difference in memory and a little bit in speed.
     *  For the Java grammar on java.*, it dropped the memory requirements
     *  at the end from 25M to 16M. We don't store any of the full context
     *  graphs in the DFA because they are limited to local context only,
     *  but apparently there's a lot of repetition there as well. We optimize
     *  the config contexts before storing the config set in the DFA states
     *  by literally rebuilding them with cached subgraphs only.</p>
     *
     *  <p>I tried a cache for use during closure operations, that was
     *  whacked after each adaptivePredict(). It cost a little bit
     *  more time I think and doesn't save on the overall footprint
     *  so it's not worth the complexity.</p>
      */
    internal final var sharedContextCache: PredictionContextCache?

    //static; {
    //ERROR = DFAState(ATNConfigSet());
    // ERROR.stateNumber = Integer.MAX_VALUE;
    //}

    public init(_ atn: ATN,
                _ sharedContextCache: PredictionContextCache) {

        self.atn = atn
        self.sharedContextCache = sharedContextCache
    }

    open func reset() {
        RuntimeException(" must overriden ")
    }

    /**
     * Clear the DFA cache used by the current instance. Since the DFA cache may
     * be shared by multiple ATN simulators, this method may affect the
     * performance (but not accuracy) of other parsers which are being used
     * concurrently.
     *
     * @throws UnsupportedOperationException if the current instance does not
     * support clearing the DFA.
     *
     * @since 4.3
     */
    open func clearDFA() throws {
        throw ANTLRError.unsupportedOperation(msg: "This ATN simulator does not support clearing the DFA. ")
    }

    open func getSharedContextCache() -> PredictionContextCache? {
        return sharedContextCache
    }

    open func getCachedContext(_ context: PredictionContext) -> PredictionContext {
        if sharedContextCache == nil {
            return context
        }

        //TODO: synced (sharedContextCache!)
        //synced (sharedContextCache!) {
        let visited: HashMap<PredictionContext, PredictionContext> =
        HashMap<PredictionContext, PredictionContext>()

        return PredictionContext.getCachedContext(context,
                sharedContextCache!,
                visited)
        //}
    }

    /**
     * @deprecated Use {@link org.antlr.v4.runtime.atn.ATNDeserializer#deserialize} instead.
     */
    ////@Deprecated
    public static func deserialize(_ data: [Character]) throws -> ATN {
        return try ATNDeserializer().deserialize(data)
    }

    /**
     * @deprecated Use {@link org.antlr.v4.runtime.atn.ATNDeserializer#checkCondition(boolean)} instead.
     */
    ////@Deprecated
    public static func checkCondition(_ condition: Bool) throws {
        try ATNDeserializer().checkCondition(condition)
    }

    /**
     * @deprecated Use {@link org.antlr.v4.runtime.atn.ATNDeserializer#checkCondition(boolean, String)} instead.
     */
    ////@Deprecated
    public static func checkCondition(_ condition: Bool, _ message: String) throws {
        try ATNDeserializer().checkCondition(condition, message)
    }

    /**
     * @deprecated Use {@link org.antlr.v4.runtime.atn.ATNDeserializer#toInt} instead.
     */
    ////@Deprecated
    public func toInt(_ c: Character) -> Int {
        return toInt(c)
    }

    /**
     * @deprecated Use {@link org.antlr.v4.runtime.atn.ATNDeserializer#toInt32} instead.
     */
    ////@Deprecated
    public func toInt32(_ data: [Character], _ offset: Int) -> Int {
        return toInt32(data, offset)
    }

    /**
     * @deprecated Use {@link org.antlr.v4.runtime.atn.ATNDeserializer#toLong} instead.
     */
    ////@Deprecated
    public func toLong(_ data: [Character], _ offset: Int) -> Int64 {
        return toLong(data, offset)
    }

    /**
     * @deprecated Use {@link org.antlr.v4.runtime.atn.ATNDeserializer#toUUID} instead.
     */
    ////@Deprecated
    //public class func toUUID(data : [Character], _ offset : Int) -> NSUUID {
    //return ATNDeserializer.toUUID(data, offset);
    //}

    /**
     * @deprecated Use {@link org.antlr.v4.runtime.atn.ATNDeserializer#edgeFactory} instead.
     */
    ////@Deprecated

    public static func edgeFactory(_ atn: ATN,
                                  _ type: Int, _ src: Int, _ trg: Int,
                                  _ arg1: Int, _ arg2: Int, _ arg3: Int,
                                  _ sets: Array<IntervalSet>) throws -> Transition {
        return try ATNDeserializer().edgeFactory(atn, type, src, trg, arg1, arg2, arg3, sets)
    }

    /**
     * @deprecated Use {@link org.antlr.v4.runtime.atn.ATNDeserializer#stateFactory} instead.
     */
    ////@Deprecated
    public static func stateFactory(_ type: Int, _ ruleIndex: Int) throws -> ATNState {
        return try ATNDeserializer().stateFactory(type, ruleIndex)!
    }

}
