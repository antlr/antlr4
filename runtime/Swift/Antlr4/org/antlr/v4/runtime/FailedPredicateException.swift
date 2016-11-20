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
 

/** A semantic predicate failed during validation.  Validation of predicates
 *  occurs when normally parsing the alternative just like matching a token.
 *  Disambiguating predicate evaluation occurs when we test a predicate during
 *  prediction.
 */
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
