/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


import Foundation

open class ATNSimulator {
    /// 
    /// -  Use _org.antlr.v4.runtime.atn.ATNDeserializer#SERIALIZED_VERSION_ instead.
    /// 
    public static let SERIALIZED_VERSION: Int = {
        return ATNDeserializer.SERIALIZED_VERSION
    }()


    /// 
    /// This is the current serialized UUID.
    /// -  Use _org.antlr.v4.runtime.atn.ATNDeserializer#checkCondition(boolean)_ instead.
    /// 
    public static let SERIALIZED_UUID: UUID = {
        return (ATNDeserializer.SERIALIZED_UUID as UUID)
    }()


    /// 
    /// Must distinguish between missing edge and edge we know leads nowhere
    /// 
    public static let ERROR: DFAState = {
        let error = DFAState(ATNConfigSet())
        error.stateNumber = Int.max
        return error
    }()

    public var atn: ATN

    /// 
    /// The context cache maps all PredictionContext objects that are equals()
    /// to a single cached copy. This cache is shared across all contexts
    /// in all ATNConfigs in all DFA states.  We rebuild each ATNConfigSet
    /// to use only cached nodes/graphs in addDFAState(). We don't want to
    /// fill this during closure() since there are lots of contexts that
    /// pop up but are not used ever again. It also greatly slows down closure().
    /// 
    /// This cache makes a huge difference in memory and a little bit in speed.
    /// For the Java grammar on java.*, it dropped the memory requirements
    /// at the end from 25M to 16M. We don't store any of the full context
    /// graphs in the DFA because they are limited to local context only,
    /// but apparently there's a lot of repetition there as well. We optimize
    /// the config contexts before storing the config set in the DFA states
    /// by literally rebuilding them with cached subgraphs only.
    /// 
    /// I tried a cache for use during closure operations, that was
    /// whacked after each adaptivePredict(). It cost a little bit
    /// more time I think and doesn't save on the overall footprint
    /// so it's not worth the complexity.
    /// 
    internal final var sharedContextCache: PredictionContextCache?

    public init(_ atn: ATN,
                _ sharedContextCache: PredictionContextCache) {

        self.atn = atn
        self.sharedContextCache = sharedContextCache
    }

    open func reset() {
        RuntimeException(" must overriden ")
    }

    /// 
    /// Clear the DFA cache used by the current instance. Since the DFA cache may
    /// be shared by multiple ATN simulators, this method may affect the
    /// performance (but not accuracy) of other parsers which are being used
    /// concurrently.
    /// 
    /// - throws: ANTLRError.unsupportedOperation if the current instance does not
    /// support clearing the DFA.
    /// 
    /// - since: 4.3
    /// 
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

    /// 
    /// - note: Use _org.antlr.v4.runtime.atn.ATNDeserializer#deserialize_ instead.
    /// 
    public static func deserialize(_ data: [Character]) throws -> ATN {
        return try ATNDeserializer().deserialize(data)
    }

    /// 
    /// - note: Use _org.antlr.v4.runtime.atn.ATNDeserializer#checkCondition(boolean)_ instead.
    /// 
    public static func checkCondition(_ condition: Bool) throws {
        try ATNDeserializer().checkCondition(condition)
    }

    /// 
    /// - note: Use _org.antlr.v4.runtime.atn.ATNDeserializer#checkCondition(boolean, String)_ instead.
    /// 
    public static func checkCondition(_ condition: Bool, _ message: String) throws {
        try ATNDeserializer().checkCondition(condition, message)
    }

    /// 
    /// - note: Use _org.antlr.v4.runtime.atn.ATNDeserializer#toInt_ instead.
    /// 
    public func toInt(_ c: Character) -> Int {
        return toInt(c)
    }

    /// 
    /// - note: Use _org.antlr.v4.runtime.atn.ATNDeserializer#toInt32_ instead.
    /// 
    public func toInt32(_ data: [Character], _ offset: Int) -> Int {
        return toInt32(data, offset)
    }

    /// 
    /// - note: Use _org.antlr.v4.runtime.atn.ATNDeserializer#toLong_ instead.
    /// 
    public func toLong(_ data: [Character], _ offset: Int) -> Int64 {
        return toLong(data, offset)
    }

    public static func edgeFactory(_ atn: ATN,
                                  _ type: Int, _ src: Int, _ trg: Int,
                                  _ arg1: Int, _ arg2: Int, _ arg3: Int,
                                  _ sets: Array<IntervalSet>) throws -> Transition {
        return try ATNDeserializer().edgeFactory(atn, type, src, trg, arg1, arg2, arg3, sets)
    }

    /// 
    /// - note: Use _org.antlr.v4.runtime.atn.ATNDeserializer#stateFactory_ instead.
    /// 
    public static func stateFactory(_ type: Int, _ ruleIndex: Int) throws -> ATNState {
        return try ATNDeserializer().stateFactory(type, ruleIndex)!
    }

}
