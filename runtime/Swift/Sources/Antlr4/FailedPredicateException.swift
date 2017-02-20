/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


/// A semantic predicate failed during validation.  Validation of predicates
/// occurs when normally parsing the alternative just like matching a token.
/// Disambiguating predicate evaluation occurs when we test a predicate during
/// prediction.
public class FailedPredicateException: RecognitionException<ParserATNSimulator> {
	private final var ruleIndex: Int
	private final var predicateIndex: Int
	private final var predicate: String?

	public convenience init(_ recognizer: Parser) throws {
		try self.init(recognizer, nil)
	}

	public convenience init(_ recognizer: Parser, _ predicate: String?)throws {
		try self.init(recognizer, predicate, nil)
	}

	public   init(_ recognizer: Parser,
									_ predicate: String?,
									_ message: String?) throws
	{

		let s: ATNState  = recognizer.getInterpreter().atn.states[recognizer.getState()]!

		let trans: AbstractPredicateTransition = s.transition(0) as! AbstractPredicateTransition
		if trans is PredicateTransition {
			self.ruleIndex = (trans as! PredicateTransition).ruleIndex
			self.predicateIndex = (trans as! PredicateTransition).predIndex
		}
		else {
			self.ruleIndex = 0
			self.predicateIndex = 0
		}

		self.predicate = predicate

        super.init(FailedPredicateException.formatMessage(predicate!, message), recognizer  , recognizer.getInputStream()!, recognizer._ctx)

		try self.setOffendingToken(recognizer.getCurrentToken())
	}

	public func getRuleIndex() -> Int {
		return ruleIndex
	}

	public func getPredIndex() -> Int {
		return predicateIndex
	}


	public func getPredicate() -> String? {
		return predicate
	}


	private static func formatMessage(_ predicate: String, _ message: String?) -> String {
		if message != nil {
			return message!
		}

		return  "failed predicate: {predicate}?"   //String.format(Locale.getDefault(), "failed predicate: {%s}?", predicate);
	}
}
