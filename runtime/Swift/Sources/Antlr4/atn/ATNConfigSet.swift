/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


/// 
/// Specialized _java.util.Set_`<`_org.antlr.v4.runtime.atn.ATNConfig_`>` that can track
/// info about the set, with support for combining similar configurations using a
/// graph-structured stack.
///
public class ATNConfigSet: Hashable, CustomStringConvertible {
    /// 
    /// The reason that we need this is because we don't want the hash map to use
    /// the standard hash code and equals. We need all configurations with the same
    /// `(s,i,_,semctx)` to be equal. Unfortunately, this key effectively doubles
    /// the number of objects associated with ATNConfigs. The other solution is to
    /// use a hash table that lets us specify the equals/hashcode operation.
    /// 


    ///
    /// Indicates that the set of configurations is read-only. Do not
    /// allow any code to manipulate the set; DFA states will point at
    /// the sets and they must not change. This does not protect the other
    /// fields; in particular, conflictingAlts is set after
    /// we've made this readonly.
    ///
    internal final var readonly = false

    /// 
    /// All configs but hashed by (s, i, _, pi) not including context. Wiped out
    /// when we go readonly as this set becomes a DFA state.
    /// 
    public final var configLookup: LookupDictionary

    /// 
    /// Track the elements as they are added to the set; supports get(i)
    /// 
    public final var configs = [ATNConfig]()

    // TODO: these fields make me pretty uncomfortable but nice to pack up info together, saves recomputation
    // TODO: can we track conflicts as they are added to save scanning configs later?
    public final var uniqueAlt = 0
    //TODO no default
    /// 
    /// Currently this is only used when we detect SLL conflict; this does
    /// not necessarily represent the ambiguous alternatives. In fact,
    /// I should also point out that this seems to include predicated alternatives
    /// that have predicates that evaluate to false. Computed in computeTargetState().
    /// 
    internal final var conflictingAlts: BitSet?

    // Used in parser and lexer. In lexer, it indicates we hit a pred
    // while computing a closure operation.  Don't make a DFA state from this.
    public final var hasSemanticContext = false
    //TODO no default
    public final var dipsIntoOuterContext = false
    //TODO no default

    /// 
    /// Indicates that this configuration set is part of a full context
    /// LL prediction. It will be used to determine how to merge $. With SLL
    /// it's a wildcard whereas it is not for LL context merge.
    /// 
    public final var fullCtx: Bool

    private var cachedHashCode = -1

    public init(_ fullCtx: Bool = true) {
        configLookup = LookupDictionary()
        self.fullCtx = fullCtx
    }

    public convenience init(_ old: ATNConfigSet) {
        self.init(old.fullCtx)
        try! addAll(old)
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

    /// 
    /// Adding a new config means merging contexts with existing configs for
    /// `(s, i, pi, _)`, where `s` is the
    /// _org.antlr.v4.runtime.atn.ATNConfig#state_, `i` is the _org.antlr.v4.runtime.atn.ATNConfig#alt_, and
    /// `pi` is the _org.antlr.v4.runtime.atn.ATNConfig#semanticContext_. We use
    /// `(s,i,pi)` as key.
    /// 
    /// This method updates _#dipsIntoOuterContext_ and
    /// _#hasSemanticContext_ when necessary.
    /// 
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
            let rootIsWildcard = !fullCtx

            let merged = PredictionContext.merge(existing.context!, config.context!, rootIsWildcard, &mergeCache)

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


    /// 
    /// Return a List holding list of configs
    /// 
    public final func elements() -> [ATNConfig] {
        return configs
    }

    public final func getStates() -> Set<ATNState> {
        var states = Set<ATNState>(minimumCapacity: configs.count)
        for config in configs {
            states.insert(config.state)
        }
        return states
    }

    /// 
    /// Gets the complete set of represented alternatives for the configuration
    /// set.
    /// 
    /// - returns: the set of represented alternatives in this configuration set
    /// 
    /// - since: 4.3
    /// 
    public final func getAlts() -> BitSet {
        let alts = BitSet()
        for config in configs {
            try! alts.set(config.alt)
        }
        return alts
    }

    public final func getPredicates() -> [SemanticContext] {
        var preds = [SemanticContext]()
        for config in configs {
            if config.semanticContext != SemanticContext.NONE {
                preds.append(config.semanticContext)
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
        for config in configs {
            config.context = interpreter.getCachedContext(config.context!)

        }
    }

    @discardableResult
    public final func addAll(_ coll: ATNConfigSet) throws -> Bool {
        for c in coll.configs {
            try add(c)
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
            hashCode = hashCode &* 3 &+ item.hashValue
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
        var buf = ""
        buf += String(describing: elements())
        if hasSemanticContext {
            buf += ",hasSemanticContext=true"
        }
        if uniqueAlt != ATN.INVALID_ALT_NUMBER {
            buf += ",uniqueAlt=\(uniqueAlt)"
        }
        if let conflictingAlts = conflictingAlts {
            buf += ",conflictingAlts=\(conflictingAlts)"
        }
        if dipsIntoOuterContext {
            buf += ",dipsIntoOuterContext"
        }
        return buf
    }

    /// 
    /// override
    /// public <T> func toArray(a : [T]) -> [T] {
    /// return configLookup.toArray(a);
    /// 
    private final func configHash(_ stateNumber: Int,_ context: PredictionContext?) -> Int{
        var hashCode = MurmurHash.initialize(7)
        hashCode = MurmurHash.update(hashCode, stateNumber)
        hashCode = MurmurHash.update(hashCode, context)
        return MurmurHash.finish(hashCode, 2)
    }

    public final func getConflictingAltSubsets() -> [BitSet] {
        let length = configs.count
        var configToAlts = [Int: BitSet]()

        for i in 0..<length {
            let hash = configHash(configs[i].state.stateNumber, configs[i].context)
            var alts: BitSet
            if let configToAlt = configToAlts[hash] {
                alts = configToAlt
            } else {
                alts = BitSet()
                configToAlts[hash] = alts
            }

            try! alts.set(configs[i].alt)
        }

        return Array(configToAlts.values)
    }

    public final func getStateToAltMap() -> [ATNState: BitSet] {
        let length = configs.count
        var m = [ATNState: BitSet]()

        for i in 0..<length {
            var alts: BitSet
            if let mAlts =  m[configs[i].state] {
                alts = mAlts
            } else {
                alts = BitSet()
                m[configs[i].state] = alts
            }

            try! alts.set(configs[i].alt)
        }
        return m
    }

    //for DFAState
    public final func getAltSet() -> Set<Int>?  {
        if configs.isEmpty {
            return nil
        }
        var alts = Set<Int>()
        for config in configs {
            alts.insert(config.alt)
        }
        return alts
    }

    //for DiagnosticErrorListener
    public final func getAltBitSet() -> BitSet  {
        let result = BitSet()
        for config in configs {
            try! result.set(config.alt)
        }
        return result
    }

    //LexerATNSimulator
    public final var firstConfigWithRuleStopState: ATNConfig? {
        for config in configs {
            if config.state is RuleStopState {
                return config
            }
        }

        return nil
    }

    //ParserATNSimulator

    public final func getUniqueAlt() -> Int {
        var alt = ATN.INVALID_ALT_NUMBER
        for config in configs {
            if alt == ATN.INVALID_ALT_NUMBER {
                alt = config.alt // found first alt
            } else if config.alt != alt {
                return ATN.INVALID_ALT_NUMBER
            }
        }
        return alt
    }

    public final func removeAllConfigsNotInRuleStopState(_ mergeCache: inout DoubleKeyMap<PredictionContext, PredictionContext, PredictionContext>?,_ lookToEndOfRule: Bool,_ atn: ATN) -> ATNConfigSet {
        if PredictionMode.allConfigsInRuleStopStates(self) {
            return self
        }

        let result = ATNConfigSet(fullCtx)
        for config in configs {
            if config.state is RuleStopState {
                try! result.add(config, &mergeCache)
                continue
            }

            if lookToEndOfRule && config.state.onlyHasEpsilonTransitions() {
                let nextTokens = atn.nextTokens(config.state)
                if nextTokens.contains(CommonToken.EPSILON) {
                    let endOfRuleState = atn.ruleToStopState[config.state.ruleIndex!]
                    try! result.add(ATNConfig(config, endOfRuleState), &mergeCache)
                }
            }
        }

        return result
    }

    public final func applyPrecedenceFilter(_ mergeCache: inout DoubleKeyMap<PredictionContext, PredictionContext, PredictionContext>?,_ parser: Parser,_ _outerContext: ParserRuleContext!) throws -> ATNConfigSet {

        let configSet = ATNConfigSet(fullCtx)
        var statesFromAlt1 = [Int: PredictionContext]()
        for config in configs {
            // handle alt 1 first
            if config.alt != 1 {
                continue
            }

            let updatedContext = try config.semanticContext.evalPrecedence(parser, _outerContext)
            if updatedContext == nil {
                // the configuration was eliminated
                continue
            }

            statesFromAlt1[config.state.stateNumber] = config.context
            if updatedContext != config.semanticContext {
                try! configSet.add(ATNConfig(config, updatedContext!), &mergeCache)
            } else {
                try! configSet.add(config, &mergeCache)
            }
        }

        for config in configs {
            if config.alt == 1 {
                // already handled
                continue
            }

            if !config.isPrecedenceFilterSuppressed() {
                /// 
                /// In the future, this elimination step could be updated to also
                /// filter the prediction context for alternatives predicting alt>1
                /// (basically a graph subtraction algorithm).
                /// 
                let context = statesFromAlt1[config.state.stateNumber]
                if context != nil && context == config.context {
                    // eliminated
                    continue
                }
            }

            try! configSet.add(config, &mergeCache)
        }

        return configSet
    }

    internal func getPredsForAmbigAlts(_ ambigAlts: BitSet, _ nalts: Int) -> [SemanticContext?]? {
        var altToPred = [SemanticContext?](repeating: nil, count: nalts + 1)
        for config in configs {
            if try! ambigAlts.get(config.alt) {
                altToPred[config.alt] = SemanticContext.or(altToPred[config.alt], config.semanticContext)
            }
        }
        var nPredAlts = 0
        for i in 1...nalts {
            if altToPred[i] == nil {
                altToPred[i] = SemanticContext.NONE
            }
            else if altToPred[i] != SemanticContext.NONE {
                nPredAlts += 1
            }
        }

        //		// Optimize away p||p and p&&p TODO: optimize() was a no-op
        //		for (int i = 0; i < altToPred.length; i++) {
        //			altToPred[i] = altToPred[i].optimize();
        //		}

        // nonambig alts are null in altToPred
        return (nPredAlts == 0 ? nil : altToPred)
    }

    public final func getAltThatFinishedDecisionEntryRule() -> Int {
        let alts = IntervalSet()
        for config in configs {
            if config.getOuterContextDepth() > 0 ||
                (config.state is RuleStopState &&
                    config.context!.hasEmptyPath()) {
                try! alts.add(config.alt)
            }
        }
        if alts.size() == 0 {
            return ATN.INVALID_ALT_NUMBER
        }
        return alts.getMinElement()
    }

    /// 
    /// Walk the list of configurations and split them according to
    /// those that have preds evaluating to true/false.  If no pred, assume
    /// true pred and include in succeeded set.  Returns Pair of sets.
    /// 
    /// Create a new set so as not to alter the incoming parameter.
    /// 
    /// Assumption: the input stream has been restored to the starting point
    /// prediction, which is where predicates need to evaluate.
    /// 
    public final func splitAccordingToSemanticValidity(
        _ outerContext: ParserRuleContext,
        _ evalSemanticContext: (SemanticContext, ParserRuleContext, Int, Bool) throws -> Bool) rethrows -> (ATNConfigSet, ATNConfigSet) {
        let succeeded = ATNConfigSet(fullCtx)
        let failed = ATNConfigSet(fullCtx)
        for config in configs {
            if config.semanticContext != SemanticContext.NONE {
                let predicateEvaluationResult = try evalSemanticContext(config.semanticContext, outerContext, config.alt,fullCtx)
                if predicateEvaluationResult {
                    try! succeeded.add(config)
                } else {
                    try! failed.add(config)
                }
            } else {
                try! succeeded.add(config)
            }
        }
        return (succeeded, failed)
    }

    public final func dupConfigsWithoutSemanticPredicates() -> ATNConfigSet {
        let dup = ATNConfigSet()
        for config in configs {
            let c = ATNConfig(config, SemanticContext.NONE)
            try! dup.add(c)
        }
        return dup
    }

    public final var hasConfigInRuleStopState: Bool {
        return configs.contains(where: { $0.state is RuleStopState })
    }

    public final var allConfigsInRuleStopStates: Bool {
        return !configs.contains(where: { !($0.state is RuleStopState) })
    }
}


public func ==(lhs: ATNConfigSet, rhs: ATNConfigSet) -> Bool {
    if lhs === rhs {
        return true
    }

    return
        lhs.configs == rhs.configs && // includes stack context
        lhs.fullCtx == rhs.fullCtx &&
        lhs.uniqueAlt == rhs.uniqueAlt &&
        lhs.conflictingAlts == rhs.conflictingAlts &&
        lhs.hasSemanticContext == rhs.hasSemanticContext &&
        lhs.dipsIntoOuterContext == rhs.dipsIntoOuterContext
}
