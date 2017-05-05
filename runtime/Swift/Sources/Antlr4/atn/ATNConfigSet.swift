/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


/// Specialized {@link java.util.Set}{@code <}{@link org.antlr.v4.runtime.atn.ATNConfig}{@code >} that can track
/// info about the set, with support for combining similar configurations using a
/// graph-structured stack.
//:  Set<ATNConfig>

public class ATNConfigSet: Hashable, CustomStringConvertible {
    /// The reason that we need this is because we don't want the hash map to use
    /// the standard hash code and equals. We need all configurations with the same
    /// {@code (s,i,_,semctx)} to be equal. Unfortunately, this key effectively doubles
    /// the number of objects associated with ATNConfigs. The other solution is to
    /// use a hash table that lets us specify the equals/hashcode operation.


     /// Indicates that the set of configurations is read-only. Do not
    /// allow any code to manipulate the set; DFA states will point at
    /// the sets and they must not change. This does not protect the other
    /// fields; in particular, conflictingAlts is set after
    /// we've made this readonly.
    internal final var readonly: Bool = false

    /// All configs but hashed by (s, i, _, pi) not including context. Wiped out
    /// when we go readonly as this set becomes a DFA state.
    public final var configLookup: LookupDictionary

    /// Track the elements as they are added to the set; supports get(i)
    public final var configs: Array<ATNConfig> = Array<ATNConfig>()

    // TODO: these fields make me pretty uncomfortable but nice to pack up info together, saves recomputation
    // TODO: can we track conflicts as they are added to save scanning configs later?
    public final var uniqueAlt: Int = 0
    //TODO no default
    /// Currently this is only used when we detect SLL conflict; this does
    /// not necessarily represent the ambiguous alternatives. In fact,
    /// I should also point out that this seems to include predicated alternatives
    /// that have predicates that evaluate to false. Computed in computeTargetState().
    internal final var conflictingAlts: BitSet?

    // Used in parser and lexer. In lexer, it indicates we hit a pred
    // while computing a closure operation.  Don't make a DFA state from this.
    public final var hasSemanticContext: Bool = false
    //TODO no default
    public final var dipsIntoOuterContext: Bool = false
    //TODO no default

    /// Indicates that this configuration set is part of a full context
    /// LL prediction. It will be used to determine how to merge $. With SLL
    /// it's a wildcard whereas it is not for LL context merge.
    public final var fullCtx: Bool

    private var cachedHashCode: Int = -1

    public init(_ fullCtx: Bool) {
        configLookup = LookupDictionary()
        self.fullCtx = fullCtx
    }
    public convenience init() {
        self.init(true)
    }

    public convenience init(_ old: ATNConfigSet) throws {
        self.init(old.fullCtx)
        try addAll(old)
        self.uniqueAlt = old.uniqueAlt
        self.conflictingAlts = old.conflictingAlts
        self.hasSemanticContext = old.hasSemanticContext
        self.dipsIntoOuterContext = old.dipsIntoOuterContext
    }

    //override
    @discardableResult
    public final func add(_ config: ATNConfig) throws -> Bool {
        var mergeCache : DoubleKeyMap<PredictionContext, PredictionContext, PredictionContext>? = nil
        return try add(config, &mergeCache)
    }

    /// Adding a new config means merging contexts with existing configs for
    /// {@code (s, i, pi, _)}, where {@code s} is the
    /// {@link org.antlr.v4.runtime.atn.ATNConfig#state}, {@code i} is the {@link org.antlr.v4.runtime.atn.ATNConfig#alt}, and
    /// {@code pi} is the {@link org.antlr.v4.runtime.atn.ATNConfig#semanticContext}. We use
    /// {@code (s,i,pi)} as key.
    /// 
    /// <p>This method updates {@link #dipsIntoOuterContext} and
    /// {@link #hasSemanticContext} when necessary.</p>
    @discardableResult
    public final func add(
        _ config: ATNConfig,
        _ mergeCache: inout DoubleKeyMap<PredictionContext, PredictionContext, PredictionContext>?) throws -> Bool {
            if readonly {
                throw ANTLRError.illegalState(msg: "This set is readonly")

            }

            if config.semanticContext != SemanticContext.NONE {
                hasSemanticContext = true
            }
            if config.getOuterContextDepth() > 0 {
                dipsIntoOuterContext = true
            }
            let existing: ATNConfig = getOrAdd(config)
            if existing === config {
                // we added this new one
                cachedHashCode = -1
                configs.append(config)  // track order here
                return true
            }
            // a previous (s,i,pi,_), merge with it and save result
            let rootIsWildcard: Bool = !fullCtx

            let merged: PredictionContext =
            PredictionContext.merge(existing.context!, config.context!, rootIsWildcard, &mergeCache)

            // no need to check for existing.context, config.context in cache
            // since only way to create new graphs is "call rule" and here. We
            // cache at both places.
            existing.reachesIntoOuterContext =
                max(existing.reachesIntoOuterContext, config.reachesIntoOuterContext)

            // make sure to preserve the precedence filter suppression during the merge
            if config.isPrecedenceFilterSuppressed() {
                existing.setPrecedenceFilterSuppressed(true)
            }

            existing.context = merged // replace context; no need to alt mapping
            return true
    }

    public final func getOrAdd(_ config: ATNConfig) -> ATNConfig {

        return configLookup.getOrAdd(config)
    }


    /// Return a List holding list of configs
    public final func elements() -> Array<ATNConfig> {
        return configs
    }

    public final func getStates() -> Set<ATNState> {

        let length = configs.count
        var states: Set<ATNState> = Set<ATNState>(minimumCapacity: length)
        for i in 0..<length {
            states.insert(configs[i].state)
        }
        return states
    }

    /// Gets the complete set of represented alternatives for the configuration
    /// set.
    /// 
    /// - returns: the set of represented alternatives in this configuration set
    /// 
    /// -  4.3

    public final func getAlts() throws -> BitSet {
        let alts: BitSet = BitSet()
        let length = configs.count
        for i in 0..<length {
            try alts.set(configs[i].alt)
        }
        return alts
    }

    public final func getPredicates() -> Array<SemanticContext> {
        var preds: Array<SemanticContext> = Array<SemanticContext>()
        let length = configs.count
        for i in 0..<length {
            if configs[i].semanticContext != SemanticContext.NONE {
                preds.append(configs[i].semanticContext)
            }
        }
        return preds
    }

    public final func get(_ i: Int) -> ATNConfig {
        return configs[i]
    }

    public final func optimizeConfigs(_ interpreter: ATNSimulator) throws {
        if readonly {
            throw ANTLRError.illegalState(msg: "This set is readonly")

        }
        if configLookup.isEmpty {
            return
        }
        let length = configs.count
        for i in 0..<length {
            configs[i].context = interpreter.getCachedContext(configs[i].context!)

        }
    }

    @discardableResult
    public final func addAll(_ coll: ATNConfigSet) throws -> Bool {
        for c: ATNConfig in coll.configs {
            try  add(c)
        }
        return false
    }

    public var hashValue: Int {
        if isReadonly() {
            if cachedHashCode == -1 {
                cachedHashCode = configsHashValue//configs.hashValue ;
            }

            return cachedHashCode
        }

        return configsHashValue // configs.hashValue;
    }

    private var configsHashValue: Int {
        var hashCode = 1
        for item in configs {
            hashCode = Int.multiplyWithOverflow(3, hashCode).0
            hashCode = Int.addWithOverflow(hashCode, item.hashValue).0

        }
        return hashCode

    }

    public final var count: Int {
        return configs.count
    }

    public final func size() -> Int {
        return configs.count
    }


    public final func isEmpty() -> Bool {
        return configs.isEmpty
    }


    public final func contains(_ o: ATNConfig) -> Bool {

        return configLookup.contains(o)
    }


    public final func clear() throws {
        if readonly {
            throw ANTLRError.illegalState(msg: "This set is readonly")

        }
        configs.removeAll()
        cachedHashCode = -1
        configLookup.removeAll()
    }

    public final func isReadonly() -> Bool {
        return readonly
    }

    public final func setReadonly(_ readonly: Bool) {
        self.readonly = readonly
        configLookup.removeAll()

    }

    public var description: String {
        let buf: StringBuilder = StringBuilder()
        buf.append(elements().map({ $0.description }))
        if hasSemanticContext {
            buf.append(",hasSemanticContext=")
            buf.append(hasSemanticContext)
        }
        if uniqueAlt != ATN.INVALID_ALT_NUMBER {
            buf.append(",uniqueAlt=")
            buf.append(uniqueAlt)
        }
        if let conflictingAlts = conflictingAlts {
            buf.append(",conflictingAlts=")
            buf.append(conflictingAlts.description)
        }
        if dipsIntoOuterContext {
            buf.append(",dipsIntoOuterContext")
        }
        return buf.toString()
    }
    public func toString() -> String {
        return description
    }

    // satisfy interface
    //	public func toArray() -> [ATNConfig] {
    //        return  Array( configLookup.map{$0.config}) ;
    //	}

    /// override
    /// public <T> func toArray(a : [T]) -> [T] {
    /// return configLookup.toArray(a);
    private final func configHash(_ stateNumber: Int,_ context: PredictionContext?) -> Int{

        var hashCode: Int = MurmurHash.initialize(7)
        hashCode = MurmurHash.update(hashCode, stateNumber)
        hashCode = MurmurHash.update(hashCode, context)
        hashCode = MurmurHash.finish(hashCode, 2)

        return hashCode

    }
    public final func getConflictingAltSubsets() throws -> Array<BitSet> {
        let length = configs.count
        let configToAlts: HashMap<Int, BitSet> = HashMap<Int, BitSet>(count: length)

        for i in 0..<length {
            let hash = configHash(configs[i].state.stateNumber, configs[i].context)
            var alts: BitSet
            if let configToAlt = configToAlts[hash] {
                alts = configToAlt
            } else {
                alts = BitSet()
                configToAlts[hash] = alts
            }

            try alts.set(configs[i].alt)
        }


        return configToAlts.values
    }
    public final func getStateToAltMap() throws -> HashMap<ATNState, BitSet> {
        let length = configs.count
        let m: HashMap<ATNState, BitSet> = HashMap<ATNState, BitSet>(count: length) //minimumCapacity: length)

        for i in 0..<length {
            var alts: BitSet
            if let mAlts =  m[configs[i].state] {
                alts = mAlts
            } else {
                alts = BitSet()
                m[configs[i].state] = alts
            }

            try alts.set(configs[i].alt)
        }
        return m
    }

    //for DFAState
    public final func getAltSet() -> Set<Int>?  {
        var alts: Set<Int> = Set<Int>()
        let length = configs.count
        for i in 0..<length {
            alts.insert(configs[i].alt)
        }

        if alts.isEmpty {
            return nil
        }
        return alts
    }

    //for DiagnosticErrorListener
    public final func getAltBitSet() throws -> BitSet  {
        let result: BitSet = BitSet()
        let length = configs.count
        for i in 0..<length {
            try result.set(configs[i].alt)
        }

        return result
    }

    //LexerATNSimulator
    public final var firstConfigWithRuleStopState: ATNConfig?  {
        let length = configs.count
        for i in 0..<length {
            if configs[i].state is RuleStopState {
                return configs[i]
            }
        }

        return nil
    }

    //ParserATNSimulator

    public final  func getUniqueAlt() -> Int {
        var alt: Int = ATN.INVALID_ALT_NUMBER
        let length = configs.count
        for i in 0..<length {
            if alt == ATN.INVALID_ALT_NUMBER {
                alt = configs[i].alt // found first alt
            } else {
                if configs[i].alt != alt {
                    return ATN.INVALID_ALT_NUMBER
                }
            }
        }
        return alt
    }
    public final func removeAllConfigsNotInRuleStopState(_ mergeCache: inout DoubleKeyMap<PredictionContext, PredictionContext, PredictionContext>?,_ lookToEndOfRule: Bool,_ atn: ATN) throws -> ATNConfigSet {
        if PredictionMode.allConfigsInRuleStopStates(self) {
            return self
        }

        let result: ATNConfigSet = ATNConfigSet(fullCtx)
        let length = configs.count
        for i in 0..<length {
            if configs[i].state is RuleStopState {
                try result.add(configs[i],&mergeCache)
                continue
            }

            if lookToEndOfRule && configs[i].state.onlyHasEpsilonTransitions() {
                let nextTokens: IntervalSet = try  atn.nextTokens(configs[i].state)
                if nextTokens.contains(CommonToken.EPSILON) {
                    let endOfRuleState: ATNState = atn.ruleToStopState[configs[i].state.ruleIndex!]
                    try result.add(ATNConfig(configs[i], endOfRuleState), &mergeCache)
                }
            }
        }

        return result
    }
    public final func applyPrecedenceFilter(_ mergeCache: inout DoubleKeyMap<PredictionContext, PredictionContext, PredictionContext>?,_ parser: Parser,_ _outerContext: ParserRuleContext!) throws -> ATNConfigSet {

        let configSet: ATNConfigSet = ATNConfigSet(fullCtx)
        let length = configs.count
        let statesFromAlt1: HashMap<Int, PredictionContext> = HashMap<Int, PredictionContext>(count: length)
        for i in 0..<length {
            // handle alt 1 first
            if configs[i].alt != 1 {
                continue
            }

            let updatedContext: SemanticContext? = try configs[i].semanticContext.evalPrecedence(parser, _outerContext)
            if updatedContext == nil {
                // the configuration was eliminated
                continue
            }

            statesFromAlt1[configs[i].state.stateNumber] = configs[i].context
            if updatedContext != configs[i].semanticContext {
                try configSet.add(ATNConfig(configs[i], updatedContext!), &mergeCache)
            } else {
                try configSet.add(configs[i],&mergeCache)
            }
        }

        for i in 0..<length {
            if configs[i].alt == 1 {
                // already handled
                continue
            }

            if !configs[i].isPrecedenceFilterSuppressed() {
                /// In the future, this elimination step could be updated to also
                /// filter the prediction context for alternatives predicting alt>1
                /// (basically a graph subtraction algorithm).
                let context: PredictionContext? = statesFromAlt1[configs[i].state.stateNumber]
                if context != nil && context == configs[i].context {
                    // eliminated
                    continue
                }
            }

            try configSet.add(configs[i], &mergeCache)
        }

        return configSet
    }
    internal func getPredsForAmbigAlts(_ ambigAlts: BitSet,
        _ nalts: Int) throws -> [SemanticContext?]? {

            var altToPred: [SemanticContext?]? = [SemanticContext?](repeating: nil, count: nalts + 1) //new SemanticContext[nalts + 1];
            let length = configs.count
            for i in 0..<length {
                if try ambigAlts.get(configs[i].alt) {
                    altToPred![configs[i].alt] = SemanticContext.or(altToPred![configs[i].alt], configs[i].semanticContext)
                }
            }
            var nPredAlts: Int = 0
            for i in 1...nalts {
                if altToPred![i] == nil {
                    altToPred![i] = SemanticContext.NONE
                } else {
                    if altToPred![i] != SemanticContext.NONE {
                        nPredAlts += 1
                    }
                }
            }

            //		// Optimize away p||p and p&&p TODO: optimize() was a no-op
            //		for (int i = 0; i < altToPred.length; i++) {
            //			altToPred[i] = altToPred[i].optimize();
            //		}

            // nonambig alts are null in altToPred
            if nPredAlts == 0 {
                altToPred = nil
            }

            return altToPred

    }
    public final func getAltThatFinishedDecisionEntryRule() throws -> Int {
        let alts: IntervalSet = try IntervalSet()
        let length = configs.count
        for i in 0..<length {
            if configs[i].getOuterContextDepth() > 0 ||
                (configs[i].state is RuleStopState &&
                    configs[i].context!.hasEmptyPath()) {
                try alts.add(configs[i].alt)
            }
        }
        if alts.size() == 0 {
            return ATN.INVALID_ALT_NUMBER
        }
        return alts.getMinElement()
    }

    /// Walk the list of configurations and split them according to
    /// those that have preds evaluating to true/false.  If no pred, assume
    /// true pred and include in succeeded set.  Returns Pair of sets.
    /// 
    /// Create a new set so as not to alter the incoming parameter.
    /// 
    /// Assumption: the input stream has been restored to the starting point
    /// prediction, which is where predicates need to evaluate.
    public final func splitAccordingToSemanticValidity(
        _ outerContext: ParserRuleContext,
        _ evalSemanticContext:( SemanticContext,ParserRuleContext,Int,Bool) throws -> Bool) throws -> (ATNConfigSet, ATNConfigSet) {
            let succeeded: ATNConfigSet = ATNConfigSet(fullCtx)
            let failed: ATNConfigSet = ATNConfigSet(fullCtx)
            let length = configs.count
            for i in 0..<length {
                if configs[i].semanticContext != SemanticContext.NONE {
                    let predicateEvaluationResult: Bool = try evalSemanticContext(configs[i].semanticContext, outerContext, configs[i].alt,fullCtx)
                    if predicateEvaluationResult {
                        try succeeded.add(configs[i])
                    } else {
                        try failed.add(configs[i])
                    }
                } else {
                    try succeeded.add(configs[i])
                }
            }
            return (succeeded, failed)
    }

    //public enum PredictionMode
    public final  func dupConfigsWithoutSemanticPredicates() throws -> ATNConfigSet {
        let dup: ATNConfigSet = ATNConfigSet()
        let length = configs.count
        for i in 0..<length {
            let c = ATNConfig(configs[i], SemanticContext.NONE)
            try dup.add(c)
        }
        return dup
    }
    public final var hasConfigInRuleStopState: Bool {
        let length = configs.count
        for i in 0..<length {
            if configs[i].state is RuleStopState {
                return true
            }
        }

        return false
    }

    public final var allConfigsInRuleStopStates: Bool {
        let length = configs.count
        for i in 0..<length {
            if !(configs[i].state is RuleStopState) {
                return false
            }
        }

        return true
    }
}


public func ==(lhs: ATNConfigSet, rhs: ATNConfigSet) -> Bool {

    if lhs === rhs {
        return true
    }

    let same: Bool =
    lhs.configs == rhs.configs && // includes stack context
        lhs.fullCtx == rhs.fullCtx &&
        lhs.uniqueAlt == rhs.uniqueAlt &&
        lhs.conflictingAlts == rhs.conflictingAlts &&
        lhs.hasSemanticContext == rhs.hasSemanticContext &&
        lhs.dipsIntoOuterContext == rhs.dipsIntoOuterContext


    return same

}
