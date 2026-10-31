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

    public func hash(into hasher: inout Hasher) {
        fatalError(#function + " must be overridden")
    }

    public var description: String {
        fatalError(#function + " must be overridden")
    }

    public class Empty: SemanticContext {
        //
        /// The default _org.antlr.v4.runtime.atn.SemanticContext_, which is semantically equivalent to
        /// a predicate of the form `{true?}.
        ///
        public static let Instance: Empty = Empty()

        public override func hash(into hasher: inout Hasher) {
        }

        override
        public var description: String {
            return "{true}?"
        }
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

        public override func hash(into hasher: inout Hasher) {
            hasher.combine(ruleIndex)
            hasher.combine(predIndex)
            hasher.combine(isCtxDependent)
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
                return SemanticContext.Empty.Instance
            } else {
                return nil
            }
        }


        public override func hash(into hasher: inout Hasher) {
            hasher.combine(precedence)
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

        ///
        /// Orders contexts by kind and then by their fields, so an operator holds
        /// its operands in one order whichever order they were combined in.
        ///
        static func compare(_ a: SemanticContext, _ b: SemanticContext) -> Int {
            let kinds = compareInts(kind(a), kind(b))
            if kinds != 0 {
                return kinds
            }
            switch (a, b) {
            case let (p as Predicate, q as Predicate):
                if p.ruleIndex != q.ruleIndex {
                    return compareInts(p.ruleIndex, q.ruleIndex)
                }
                if p.predIndex != q.predIndex {
                    return compareInts(p.predIndex, q.predIndex)
                }
                return compareInts(p.isCtxDependent ? 1 : 0, q.isCtxDependent ? 1 : 0)
            case let (p as PrecedencePredicate, q as PrecedencePredicate):
                return compareInts(p.precedence, q.precedence)
            case let (x as AND, y as AND):
                return compareOperands(x.opnds, y.opnds)
            case let (x as OR, y as OR):
                return compareOperands(x.opnds, y.opnds)
            default:
                return compareInts(a.hashValue, b.hashValue)
            }
        }

        ///
        /// Merges two operand arrays, each already in `compare` order, into
        /// one array in that order without duplicates.
        ///
        static func merge(_ x: [SemanticContext], _ y: [SemanticContext]) -> [SemanticContext] {
            var merged = [SemanticContext]()
            merged.reserveCapacity(x.count + y.count)
            var i = 0
            var j = 0
            while i < x.count && j < y.count {
                let c = compare(x[i], y[j])
                // compare orders contexts of other kinds by hash value, so it can tie two unequal ones
                if c < 0 || c == 0 && x[i] != y[j] {
                    merged.append(x[i])
                    i += 1
                } else if c > 0 {
                    merged.append(y[j])
                    j += 1
                } else {
                    merged.append(x[i])
                    i += 1
                    j += 1
                }
            }
            merged.append(contentsOf: x[i...])
            merged.append(contentsOf: y[j...])
            return merged
        }

        ///
        /// Removes all precedence predicates but the one with the lowest or highest
        /// precedence. They sort next to each other, by precedence.
        ///
        static func keepOnePrecedencePredicate(_ operands: inout [SemanticContext], lowest: Bool) {
            guard let first = operands.firstIndex(where: { $0 is PrecedencePredicate }),
                  let last = operands.lastIndex(where: { $0 is PrecedencePredicate }) else {
                return
            }
            let kept = operands[lowest ? first : last]
            operands.replaceSubrange(first...last, with: [kept])
        }

        private static func kind(_ context: SemanticContext) -> Int {
            switch context {
            case is Predicate: return 0
            case is PrecedencePredicate: return 1
            case is AND: return 2
            case is OR: return 3
            default: return 4
            }
        }

        private static func compareOperands(_ x: [SemanticContext], _ y: [SemanticContext]) -> Int {
            if x.count != y.count {
                return compareInts(x.count, y.count)
            }
            for (p, q) in zip(x, y) {
                let c = compare(p, q)
                if c != 0 {
                    return c
                }
            }
            return 0
        }

        private static func compareInts(_ x: Int, _ y: Int) -> Int {
            return x < y ? -1 : (x > y ? 1 : 0)
        }
    }

    ///
    /// A semantic context which is true whenever none of the contained contexts
    /// is false.
    ///

    public class AND: Operator {
        public let opnds: [SemanticContext]

        public init(_ a: SemanticContext, _ b: SemanticContext) {
            var operands = Operator.merge((a as? AND)?.opnds ?? [a], (b as? AND)?.opnds ?? [b])
            // interested in the transition with the lowest precedence
            Operator.keepOnePrecedencePredicate(&operands, lowest: true)
            opnds = operands
        }

        override
        public func getOperands() -> [SemanticContext] {
            return opnds
        }


        public override func hash(into hasher: inout Hasher) {
            hasher.combine(opnds)
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
                else if evaluated != SemanticContext.Empty.Instance {
                    // Reduce the result by skipping true elements
                    operands.append(evaluated!)
                }
            }

            if !differs {
                return self
            }

            return operands.reduce(SemanticContext.Empty.Instance, SemanticContext.and)
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
            var operands = Operator.merge((a as? OR)?.opnds ?? [a], (b as? OR)?.opnds ?? [b])
            // interested in the transition with the highest precedence
            Operator.keepOnePrecedencePredicate(&operands, lowest: false)
            self.opnds = operands
        }

        override
        public func getOperands() -> [SemanticContext] {
            return opnds
        }

        public override func hash(into hasher: inout Hasher) {
            hasher.combine(opnds)
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
                if evaluated == SemanticContext.Empty.Instance {
                    // The OR context is true if any element is true
                    return SemanticContext.Empty.Instance
                }
                else if let evaluated = evaluated {
                    // Reduce the result by skipping false elements
                    operands.append(evaluated)
                }
            }

            if !differs {
                return self
            }

            return operands.reduce(nil, SemanticContext.or)
        }

        override
        public var description: String {
            return opnds.map({ $0.description }).joined(separator: "||")

        }
    }

    public static func and(_ a: SemanticContext?, _ b: SemanticContext?) -> SemanticContext {
        if a == nil || a == SemanticContext.Empty.Instance {
            return b!
        }
        if b == nil || b == SemanticContext.Empty.Instance {
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
        if a == SemanticContext.Empty.Instance || b == SemanticContext.Empty.Instance {
            return SemanticContext.Empty.Instance
        }
        let result: OR = OR(a!, b!)
        if result.opnds.count == 1 {
            return result.opnds[0]
        }

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
