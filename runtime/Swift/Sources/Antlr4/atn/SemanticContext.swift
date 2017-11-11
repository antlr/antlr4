/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 



/// 
/// A tree structure used to record the semantic context in which
/// an ATN configuration is valid.  It's either a single predicate,
/// a conjunction `p1&&p2`, or a sum of products `p1||p2`.
/// 
/// I have scoped the _org.antlr.v4.runtime.atn.SemanticContext.AND_, _org.antlr.v4.runtime.atn.SemanticContext.OR_, and _org.antlr.v4.runtime.atn.SemanticContext.Predicate_ subclasses of
/// _org.antlr.v4.runtime.atn.SemanticContext_ within the scope of this outer class.
/// 

import Foundation

public class SemanticContext: Hashable, CustomStringConvertible {
    /// 
    /// The default _org.antlr.v4.runtime.atn.SemanticContext_, which is semantically equivalent to
    /// a predicate of the form `{true`?}.
    /// 
    public static let NONE: SemanticContext = Predicate()

    /// 
    /// For context independent predicates, we evaluate them without a local
    /// context (i.e., null context). That way, we can evaluate them without
    /// having to create proper rule-specific context during prediction (as
    /// opposed to the parser, which creates them naturally). In a practical
    /// sense, this avoids a cast exception from RuleContext to myruleContext.
    /// 
    /// For context dependent predicates, we must pass in a local context so that
    /// references such as $arg evaluate properly as _localctx.arg. We only
    /// capture context dependent predicates in the context in which we begin
    /// prediction, so we passed in the outer context here in case of context
    /// dependent predicate evaluation.
    /// 
    public func eval<T>(_ parser: Recognizer<T>, _ parserCallStack: RuleContext) throws -> Bool {
        fatalError(#function + " must be overridden")
    }

    /// 
    /// Evaluate the precedence predicates for the context and reduce the result.
    /// 
    /// - parameter parser: The parser instance.
    /// - parameter parserCallStack:
    /// - returns: The simplified semantic context after precedence predicates are
    /// evaluated, which will be one of the following values.
    /// * _#NONE_: if the predicate simplifies to `true` after
    /// precedence predicates are evaluated.
    /// * `null`: if the predicate simplifies to `false` after
    /// precedence predicates are evaluated.
    /// * `this`: if the semantic context is not changed as a result of
    /// precedence predicate evaluation.
    /// * A non-`null` _org.antlr.v4.runtime.atn.SemanticContext_: the new simplified
    /// semantic context after precedence predicates are evaluated.
    /// 
    public func evalPrecedence<T>(_ parser: Recognizer<T>, _ parserCallStack: RuleContext) throws -> SemanticContext? {
        return self
    }

    public var hashValue: Int {
        fatalError(#function + " must be overridden")
    }

    public var description: String {
        fatalError(#function + " must be overridden")
    }

    public class Predicate: SemanticContext {
        public let ruleIndex: Int
        public let predIndex: Int
        public let isCtxDependent: Bool
        // e.g., $i ref in pred

        override
        public init() {
            self.ruleIndex = -1
            self.predIndex = -1
            self.isCtxDependent = false
        }

        public init(_ ruleIndex: Int, _ predIndex: Int, _ isCtxDependent: Bool) {
            self.ruleIndex = ruleIndex
            self.predIndex = predIndex
            self.isCtxDependent = isCtxDependent
        }

        override
        public func eval<T>(_ parser: Recognizer<T>, _ parserCallStack: RuleContext) throws -> Bool {
            let localctx = isCtxDependent ? parserCallStack : nil
            return try parser.sempred(localctx, ruleIndex, predIndex)
        }

        override
        public var hashValue: Int {
            var hashCode = MurmurHash.initialize()
            hashCode = MurmurHash.update(hashCode, ruleIndex)
            hashCode = MurmurHash.update(hashCode, predIndex)
            hashCode = MurmurHash.update(hashCode, isCtxDependent ? 1 : 0)
            return MurmurHash.finish(hashCode, 3)
        }


        override
        public var description: String {
            return "{\(ruleIndex):\(predIndex)}?"
        }

    }


    public class PrecedencePredicate: SemanticContext {
        public let precedence: Int
        override
        init() {
            self.precedence = 0
        }

        public init(_ precedence: Int) {
            self.precedence = precedence
        }

        override
        public func eval<T>(_ parser: Recognizer<T>, _ parserCallStack: RuleContext) throws -> Bool {
            return parser.precpred(parserCallStack, precedence)
        }

        override
        public func evalPrecedence<T>(_ parser: Recognizer<T>, _ parserCallStack: RuleContext) throws -> SemanticContext? {
            if parser.precpred(parserCallStack, precedence) {
                return SemanticContext.NONE
            } else {
                return nil
            }
        }


        override
        public var hashValue: Int {
            var hashCode: Int = 1
            hashCode = 31 * hashCode + precedence
            return hashCode
        }

        override
        public var description: String {
            return "{" + String(precedence) + ">=prec}?"

        }
    }

    /// 
    /// This is the base class for semantic context "operators", which operate on
    /// a collection of semantic context "operands".
    /// 
    /// -  4.3
    /// 

    public class Operator: SemanticContext {
        /// 
        /// Gets the operands for the semantic context operator.
        /// 
        /// - returns: a collection of _org.antlr.v4.runtime.atn.SemanticContext_ operands for the
        /// operator.
        /// 
        /// -  4.3
        /// 

        public func getOperands() -> Array<SemanticContext> {
            fatalError(#function + " must be overridden")
        }
    }

    /// 
    /// A semantic context which is true whenever none of the contained contexts
    /// is false.
    /// 

    public class AND: Operator {
        public let opnds: [SemanticContext]

        public init(_ a: SemanticContext, _ b: SemanticContext) {
            var operands = Set<SemanticContext>()
            if let aAnd = a as? AND {
                operands.formUnion(aAnd.opnds)
            } else {
                operands.insert(a)
            }
            if let bAnd = b as? AND {
                operands.formUnion(bAnd.opnds)
            } else {
                operands.insert(b)
            }

            let precedencePredicates = SemanticContext.filterPrecedencePredicates(&operands)
            if !precedencePredicates.isEmpty {
                // interested in the transition with the lowest precedence

                let reduced = precedencePredicates.sorted {
                    $0.precedence < $1.precedence
                }
                operands.insert(reduced[0])
            }

            opnds = Array(operands)
        }

        override
        public func getOperands() -> [SemanticContext] {
            return opnds
        }


        override
        public var hashValue: Int {
            //MurmurHash.hashCode(opnds, AND.class.hashCode());
            let seed = 1554547125
            //NSStringFromClass(AND.self).hashValue
            return MurmurHash.hashCode(opnds, seed)
        }

        /// 
        /// 
        /// 
        /// 
        /// The evaluation of predicates by this context is short-circuiting, but
        /// unordered.
        /// 
        override
        public func eval<T>(_ parser: Recognizer<T>, _ parserCallStack: RuleContext) throws -> Bool {
            for opnd in opnds {
                if try !opnd.eval(parser, parserCallStack) {
                    return false
                }
            }
            return true
        }

        override
        public func evalPrecedence<T>(_ parser: Recognizer<T>, _ parserCallStack: RuleContext) throws -> SemanticContext? {
            var differs = false
            var operands = [SemanticContext]()
            for context in opnds {
                let evaluated = try context.evalPrecedence(parser, parserCallStack)
                //TODO differs |= (evaluated != context)
                //differs |= (evaluated != context);
                differs = differs || (evaluated != context)

                if evaluated == nil {
                    // The AND context is false if any element is false
                    return nil
                }
                else if evaluated != SemanticContext.NONE {
                    // Reduce the result by skipping true elements
                    operands.append(evaluated!)
                }
            }

            if !differs {
                return self
            }

            if operands.isEmpty {
                // all elements were true, so the AND context is true
                return SemanticContext.NONE
            }

            var result = operands[0]
            let length = operands.count
            for i in 1..<length {
                result = SemanticContext.and(result, operands[i])
            }

            return result
        }

        public func toString() -> String {
            return description
        }

        override
        public var description: String {
            return opnds.map({ $0.description }).joined(separator: "&&")

        }
    }

    /// 
    /// A semantic context which is true whenever at least one of the contained
    /// contexts is true.
    /// 

    public class OR: Operator {
        public final var opnds: [SemanticContext]

        public init(_ a: SemanticContext, _ b: SemanticContext) {
            var operands: Set<SemanticContext> = Set<SemanticContext>()
            if let aOr = a as? OR {
                operands.formUnion(aOr.opnds)
            } else {
                operands.insert(a)
            }
            if let bOr = b as? OR {
                operands.formUnion(bOr.opnds)
            } else {
                operands.insert(b)
            }

            let precedencePredicates = SemanticContext.filterPrecedencePredicates(&operands)
            if !precedencePredicates.isEmpty {
                // interested in the transition with the highest precedence

                let reduced = precedencePredicates.sorted {
                    $0.precedence > $1.precedence
                }
                operands.insert(reduced[0])
            }

            self.opnds = Array(operands)
        }

        override
        public func getOperands() -> [SemanticContext] {
            return opnds
        }


        override
        public var hashValue: Int {

            return MurmurHash.hashCode(opnds, NSStringFromClass(OR.self).hashValue)
        }

        /// 
        /// 
        /// 
        /// 
        /// The evaluation of predicates by this context is short-circuiting, but
        /// unordered.
        /// 
        override
        public func eval<T>(_ parser: Recognizer<T>, _ parserCallStack: RuleContext) throws -> Bool {
            for opnd in opnds {
                if try opnd.eval(parser, parserCallStack) {
                    return true
                }
            }
            return false
        }

        override
        public func evalPrecedence<T>(_ parser: Recognizer<T>, _ parserCallStack: RuleContext) throws -> SemanticContext? {
            var differs = false
            var operands = [SemanticContext]()
            for context in opnds {
                let evaluated = try context.evalPrecedence(parser, parserCallStack)
                differs = differs || (evaluated != context)
                if evaluated == SemanticContext.NONE {
                    // The OR context is true if any element is true
                    return SemanticContext.NONE
                }
                else if let evaluated = evaluated {
                    // Reduce the result by skipping false elements
                    operands.append(evaluated)
                }
            }

            if !differs {
                return self
            }

            if operands.isEmpty {
                // all elements were false, so the OR context is false
                return nil
            }

            var result = operands[0]
            let length = operands.count
            for i in 1..<length {
                result = SemanticContext.or(result, operands[i])
            }

            return result
        }


        public func toString() -> String {
            return description
        }

        override
        public var description: String {
            return opnds.map({ $0.description }).joined(separator: "||")

        }
    }

    public static func and(_ a: SemanticContext?, _ b: SemanticContext?) -> SemanticContext {
        if a == nil || a == SemanticContext.NONE {
            return b!
        }
        if b == nil || b == SemanticContext.NONE {
            return a!
        }
        let result: AND = AND(a!, b!)
        if result.opnds.count == 1 {
            return result.opnds[0]
        }

        return result
    }

    /// 
    /// 
    /// - seealso: org.antlr.v4.runtime.atn.ParserATNSimulator#getPredsForAmbigAlts
    /// 
    public static func or(_ a: SemanticContext?, _ b: SemanticContext?) -> SemanticContext {
        if a == nil {
            return b!
        }
        if b == nil {
            return a!
        }
        if a == SemanticContext.NONE || b == SemanticContext.NONE {
            return SemanticContext.NONE
        }
        let result: OR = OR(a!, b!)
        if result.opnds.count == 1 {
            return result.opnds[0]
        }

        return result
    }

    private static func filterPrecedencePredicates(_ collection: inout Set<SemanticContext>) -> [PrecedencePredicate] {
        let result = collection.flatMap {
            $0 as? PrecedencePredicate
        }
        collection = Set<SemanticContext>(collection.filter {
            !($0 is PrecedencePredicate)
        })
        return result
    }
}

public func ==(lhs: SemanticContext, rhs: SemanticContext) -> Bool {
    if lhs === rhs {
        return true
    }

    if (lhs is SemanticContext.Predicate) && (rhs is SemanticContext.Predicate) {
        return (lhs as! SemanticContext.Predicate) == (rhs as! SemanticContext.Predicate)
    }

    if (lhs is SemanticContext.PrecedencePredicate) && (rhs is SemanticContext.PrecedencePredicate) {
        return (lhs as! SemanticContext.PrecedencePredicate) == (rhs as! SemanticContext.PrecedencePredicate)
    }

    if (lhs is SemanticContext.AND) && (rhs is SemanticContext.AND) {
        return (lhs as! SemanticContext.AND) == (rhs as! SemanticContext.AND)
    }

    if (lhs is SemanticContext.OR) && (rhs is SemanticContext.OR) {
        return (lhs as! SemanticContext.OR) == (rhs as! SemanticContext.OR)
    }


    return false
}

public func ==(lhs: SemanticContext.Predicate, rhs: SemanticContext.Predicate) -> Bool {
    if lhs === rhs {
        return true
    }
    return lhs.ruleIndex == rhs.ruleIndex &&
            lhs.predIndex == rhs.predIndex &&
            lhs.isCtxDependent == rhs.isCtxDependent
}

public func ==(lhs: SemanticContext.PrecedencePredicate, rhs: SemanticContext.PrecedencePredicate) -> Bool {
    if lhs === rhs {
        return true
    }
    return lhs.precedence == rhs.precedence
}


public func ==(lhs: SemanticContext.AND, rhs: SemanticContext.AND) -> Bool {
    if lhs === rhs {
        return true
    }
    return lhs.opnds == rhs.opnds
}

public func ==(lhs: SemanticContext.OR, rhs: SemanticContext.OR) -> Bool {
    if lhs === rhs {
        return true
    }
    return lhs.opnds == rhs.opnds
}
