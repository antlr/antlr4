/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.



/// A tree structure used to record the semantic context in which
/// an ATN configuration is valid.  It's either a single predicate,
/// a conjunction {@code p1&&p2}, or a sum of products {@code p1||p2}.
/// 
/// <p>I have scoped the {@link org.antlr.v4.runtime.atn.SemanticContext.AND}, {@link org.antlr.v4.runtime.atn.SemanticContext.OR}, and {@link org.antlr.v4.runtime.atn.SemanticContext.Predicate} subclasses of
/// {@link org.antlr.v4.runtime.atn.SemanticContext} within the scope of this outer class.</p>

import Foundation

public class SemanticContext: Hashable, CustomStringConvertible {
    /// The default {@link org.antlr.v4.runtime.atn.SemanticContext}, which is semantically equivalent to
    /// a predicate of the form {@code {true}?}.
    public static let NONE: SemanticContext = Predicate()

    /// For context independent predicates, we evaluate them without a local
    /// context (i.e., null context). That way, we can evaluate them without
    /// having to create proper rule-specific context during prediction (as
    /// opposed to the parser, which creates them naturally). In a practical
    /// sense, this avoids a cast exception from RuleContext to myruleContext.
    /// 
    /// <p>For context dependent predicates, we must pass in a local context so that
    /// references such as $arg evaluate properly as _localctx.arg. We only
    /// capture context dependent predicates in the context in which we begin
    /// prediction, so we passed in the outer context here in case of context
    /// dependent predicate evaluation.</p>
    public func eval<T:ATNSimulator>(_ parser: Recognizer<T>, _ parserCallStack: RuleContext) throws -> Bool {
        RuntimeException(#function + " must be overridden")
        return false
    }

    /// Evaluate the precedence predicates for the context and reduce the result.
    /// 
    /// - parameter parser: The parser instance.
    /// - parameter parserCallStack:
    /// - returns: The simplified semantic context after precedence predicates are
    /// evaluated, which will be one of the following values.
    /// <ul>
    /// <li>{@link #NONE}: if the predicate simplifies to {@code true} after
    /// precedence predicates are evaluated.</li>
    /// <li>{@code null}: if the predicate simplifies to {@code false} after
    /// precedence predicates are evaluated.</li>
    /// <li>{@code this}: if the semantic context is not changed as a result of
    /// precedence predicate evaluation.</li>
    /// <li>A non-{@code null} {@link org.antlr.v4.runtime.atn.SemanticContext}: the new simplified
    /// semantic context after precedence predicates are evaluated.</li>
    /// </ul>
    public func evalPrecedence<T:ATNSimulator>(_ parser: Recognizer<T>, _ parserCallStack: RuleContext) throws -> SemanticContext? {
        return self
    }
    public var hashValue: Int {
        RuntimeException(#function + " must be overridden")
        return 0
    }
    public var description: String {
        RuntimeException(#function + " must be overridden")
        return ""
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
        public func eval<T:ATNSimulator>(_ parser: Recognizer<T>, _ parserCallStack: RuleContext) throws -> Bool {
            let localctx: RuleContext? = isCtxDependent ? parserCallStack : nil
            return try parser.sempred(localctx, ruleIndex, predIndex)
        }

        override
        public var hashValue: Int {
            var hashCode: Int = MurmurHash.initialize()
            hashCode = MurmurHash.update(hashCode, ruleIndex)
            hashCode = MurmurHash.update(hashCode, predIndex)
            hashCode = MurmurHash.update(hashCode, isCtxDependent ? 1 : 0)
            hashCode = MurmurHash.finish(hashCode, 3)
            return hashCode
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
        public func eval<T:ATNSimulator>(_ parser: Recognizer<T>, _ parserCallStack: RuleContext) throws -> Bool {
            return try parser.precpred(parserCallStack, precedence)
        }

        override
        public func evalPrecedence<T:ATNSimulator>(_ parser: Recognizer<T>, _ parserCallStack: RuleContext) throws -> SemanticContext? {
            if try parser.precpred(parserCallStack, precedence) {
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

    /// This is the base class for semantic context "operators", which operate on
    /// a collection of semantic context "operands".
    /// 
    /// -  4.3

    public class Operator: SemanticContext {
        /// Gets the operands for the semantic context operator.
        /// 
        /// - returns: a collection of {@link org.antlr.v4.runtime.atn.SemanticContext} operands for the
        /// operator.
        /// 
        /// -  4.3

        public func getOperands() -> Array<SemanticContext> {
            RuntimeException(" must overriden ")
            return Array<SemanticContext>()
        }
    }

    /// A semantic context which is true whenever none of the contained contexts
    /// is false.

    public class AND: Operator {
        public let opnds: [SemanticContext]

        public init(_ a: SemanticContext, _ b: SemanticContext) {
            var operands: Set<SemanticContext> = Set<SemanticContext>()
            if a is AND {
                operands.formUnion((a as! AND).opnds)
                //operands.addAll(Arrays.asList((a as AND).opnds));
            } else {
                operands.insert(a)
            }
            if b is AND {
                operands.formUnion((b as! AND).opnds)
                //operands.addAll(Arrays.asList((b as AND).opnds));
            } else {
                operands.insert(b)
            }

            let precedencePredicates: Array<PrecedencePredicate> =
            SemanticContext.filterPrecedencePredicates(&operands)
            if !precedencePredicates.isEmpty {
                // interested in the transition with the lowest precedence

                let reduced: PrecedencePredicate = precedencePredicates.sorted {
                    $0.precedence < $1.precedence
                }.first! //Collections.min(precedencePredicates);
                operands.insert(reduced)
            }

            opnds = Array(operands)   //.toArray(new, SemanticContext[operands.size()]);
        }

        override
        public func getOperands() -> Array<SemanticContext> {
            return opnds
        }


        override
        public var hashValue: Int {
            //MurmurHash.hashCode(opnds, AND.class.hashCode());
            let seed = 1554547125
            //NSStringFromClass(AND.self).hashValue
            return MurmurHash.hashCode(opnds, seed)
        }

        /// {@inheritDoc}
        /// 
        /// <p>
        /// The evaluation of predicates by this context is short-circuiting, but
        /// unordered.</p>
        override
        public func eval<T:ATNSimulator>(_ parser: Recognizer<T>, _ parserCallStack: RuleContext) throws -> Bool {
            for opnd: SemanticContext in opnds {
                if try !opnd.eval(parser, parserCallStack) {
                    return false
                }
            }
            return true
        }

        override
        public func evalPrecedence<T:ATNSimulator>(_ parser: Recognizer<T>, _ parserCallStack: RuleContext) throws -> SemanticContext? {
            var differs: Bool = false
            var operands: Array<SemanticContext> = Array<SemanticContext>()
            for context: SemanticContext in opnds {
                let evaluated: SemanticContext? = try context.evalPrecedence(parser, parserCallStack)
                //TODO differs |= (evaluated != context)
                //differs |= (evaluated != context);
                differs = differs || (evaluated != context)

                if evaluated == nil {
                    // The AND context is false if any element is false
                    return nil
                } else {
                    if evaluated != SemanticContext.NONE {
                        // Reduce the result by skipping true elements
                        operands.append(evaluated!)
                    }
                }
            }

            if !differs {
                return self
            }

            if operands.isEmpty {
                // all elements were true, so the AND context is true
                return SemanticContext.NONE
            }

            var result: SemanticContext = operands[0]
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

    /// A semantic context which is true whenever at least one of the contained
    /// contexts is true.

    public class OR: Operator {
        public final var opnds: [SemanticContext]

        public init(_ a: SemanticContext, _ b: SemanticContext) {
            var operands: Set<SemanticContext> = Set<SemanticContext>()
            if a is OR {
                operands.formUnion((a as! OR).opnds)
                // operands.addAll(Arrays.asList((a as OR).opnds));
            } else {
                operands.insert(a)
            }
            if b is OR {
                operands.formUnion((b as! OR).opnds)
                //operands.addAll(Arrays.asList((b as OR).opnds));
            } else {
                operands.insert(b)
            }

            let precedencePredicates: Array<PrecedencePredicate> = SemanticContext.filterPrecedencePredicates(&operands)
            if !precedencePredicates.isEmpty {
                // interested in the transition with the highest precedence
                let reduced: PrecedencePredicate = precedencePredicates.sorted {
                    $0.precedence > $1.precedence
                }.first!
                //var reduced : PrecedencePredicate = Collections.max(precedencePredicates);
                operands.insert(reduced)
            }

            self.opnds = Array(operands)  //operands.toArray(new, SemanticContext[operands.size()]);
        }

        override
        public func getOperands() -> Array<SemanticContext> {
            return opnds //Arrays.asList(opnds);
        }


        override
        public var hashValue: Int {

            return MurmurHash.hashCode(opnds, NSStringFromClass(OR.self).hashValue)
        }

        /// {@inheritDoc}
        /// 
        /// <p>
        /// The evaluation of predicates by this context is short-circuiting, but
        /// unordered.</p>
        override
        public func eval<T:ATNSimulator>(_ parser: Recognizer<T>, _ parserCallStack: RuleContext) throws -> Bool {
            for opnd: SemanticContext in opnds {
                if try opnd.eval(parser, parserCallStack) {
                    return true
                }
            }
            return false
        }

        override
        public func evalPrecedence<T:ATNSimulator>(_ parser: Recognizer<T>, _ parserCallStack: RuleContext) throws -> SemanticContext? {
            var differs: Bool = false
            var operands: Array<SemanticContext> = Array<SemanticContext>()
            for context: SemanticContext in opnds {
                let evaluated: SemanticContext? = try context.evalPrecedence(parser, parserCallStack)
                //differs |= (evaluated != context);
                differs = differs || (evaluated != context)
                if evaluated == SemanticContext.NONE {
                    // The OR context is true if any element is true
                    return SemanticContext.NONE
                } else {
                    if evaluated != nil {
                        // Reduce the result by skipping false elements
                        operands.append(evaluated!)
                        //operands.add(evaluated);
                    }
                }
            }

            if !differs {
                return self
            }

            if operands.isEmpty {
                // all elements were false, so the OR context is false
                return nil
            }

            var result: SemanticContext = operands[0]
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
    /// - seealso: org.antlr.v4.runtime.atn.ParserATNSimulator#getPredsForAmbigAlts
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

    private static func filterPrecedencePredicates(
            _ collection: inout Set<SemanticContext>) ->
            Array<PrecedencePredicate> {

        let result = collection.filter {
            $0 is PrecedencePredicate
        }
        collection = Set<SemanticContext>(collection.filter {
            !($0 is PrecedencePredicate)
        })
        //if (result == nil) {
        //return Array<PrecedencePredicate>();
        //}

        return (result as! Array<PrecedencePredicate>)
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
