/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import 'dart:developer';

import 'package:logging/logging.dart';

import '../../atn/atn.dart';
import '../../interval_set.dart';
import '../../misc/pair.dart';
import '../../parser.dart';
import '../../parser_rule_context.dart';
import '../../rule_context.dart';
import '../../token.dart';
import '../../tree/tree.dart';
import 'errors.dart';

/// The interface for defining strategies to deal with syntax errors encountered
/// during a parse by ANTLR-generated parsers. We distinguish between three
/// different kinds of errors:
///
/// <ul>
/// <li>The parser could not figure out which path to take in the ATN (none of
/// the available alternatives could possibly match)</li>
/// <li>The current input does not match what we were looking for</li>
/// <li>A predicate evaluated to false</li>
/// </ul>
///
/// Implementations of this interface report syntax errors by calling
/// {@link Parser#notifyErrorListeners}.
///
/// <p>TODO: what to do about lexers</p>
abstract class ErrorStrategy {
  /// Reset the error handler state for the specified [recognizer].
  /// @param recognizer the parser instance
  void reset(Parser recognizer);

  /// This method is called when an unexpected symbol is encountered during an
  /// inline match operation, such as {@link Parser#match}. If the error
  /// strategy successfully recovers from the match failure, this method
  /// returns the [Token] instance which should be treated as the
  /// successful result of the match.
  ///
  /// <p>This method handles the consumption of any tokens - the caller should
  /// <b>not</b> call {@link Parser#consume} after a successful recovery.</p>
  ///
  /// <p>Note that the calling code will not report an error if this method
  /// returns successfully. The error strategy implementation is responsible
  /// for calling {@link Parser#notifyErrorListeners} as appropriate.</p>
  ///
  /// @param recognizer the parser instance
  /// @ if the error strategy was not able to
  /// recover from the unexpected input symbol
  Token recoverInline(Parser recognizer);

  /// This method is called to recover from exception [e]. This method is
  /// called after {@link #reportError} by the default exception handler
  /// generated for a rule method.
  ///
  /// @see #reportError
  ///
  /// @param recognizer the parser instance
  /// @param e the recognition exception to recover from
  /// @ if the error strategy could not recover from
  /// the recognition exception
  void recover(Parser recognizer, RecognitionException e);

  /// This method provides the error handler with an opportunity to handle
  /// syntactic or semantic errors in the input stream before they result in a
  /// [RecognitionException].
  ///
  /// <p>The generated code currently contains calls to {@link #sync} after
  /// entering the decision state of a closure block ({@code (...)*} or
  /// {@code (...)+}).</p>
  ///
  /// <p>For an implementation based on Jim Idle's "magic sync" mechanism, see
  /// {@link DefaultErrorStrategy#sync}.</p>
  ///
  /// @see DefaultErrorStrategy#sync
  ///
  /// @param recognizer the parser instance
  /// @ if an error is detected by the error
  /// strategy but cannot be automatically recovered at the current state in
  /// the parsing process
  void sync(Parser recognizer);

  /// Tests whether or not [recognizer] is in the process of recovering
  /// from an error. In error recovery mode, {@link Parser#consume} adds
  /// symbols to the parse tree by calling
  /// {@link Parser#createErrorNode(ParserRuleContext, Token)} then
  /// {@link ParserRuleContext#addErrorNode(ErrorNode)} instead of
  /// {@link Parser#createTerminalNode(ParserRuleContext, Token)}.
  ///
  /// @param recognizer the parser instance
  /// @return [true] if the parser is currently recovering from a parse
  /// error, otherwise [false]
  bool inErrorRecoveryMode(Parser recognizer);

  /// This method is called by when the parser successfully matches an input
  /// symbol.
  ///
  /// @param recognizer the parser instance
  void reportMatch(Parser recognizer);

  /// Report any kind of [RecognitionException]. This method is called by
  /// the default exception handler generated for a rule method.
  ///
  /// @param recognizer the parser instance
  /// @param e the recognition exception to report
  void reportError(Parser recognizer, RecognitionException e);
}

/// This is the default implementation of [ANTLRErrorStrategy] used for
/// error reporting and recovery in ANTLR parsers.
class DefaultErrorStrategy implements ErrorStrategy {
  /// Indicates whether the error strategy is currently "recovering from an
  /// error". This is used to suppress reporting multiple error messages while
  /// attempting to recover from a detected syntax error.
  ///
  /// @see #inErrorRecoveryMode
  bool errorRecoveryMode = false;

  /// The index into the input stream where the last error occurred.
  /// 	This is used to prevent infinite loops where an error is found
  ///  but no token is consumed during recovery...another error is found,
  ///  ad nauseum.  This is a failsafe mechanism to guarantee that at least
  ///  one token/tree node is consumed for two errors.
  int lastErrorIndex = -1;

  IntervalSet? lastErrorStates;

  /// This field is used to propagate information about the lookahead following
  /// the previous match. Since prediction prefers completing the current rule
  /// to error recovery efforts, error reporting may occur later than the
  /// original point where it was discoverable. The original context is used to
  /// compute the true expected sets as though the reporting occurred as early
  /// as possible.
  ParserRuleContext? nextTokensContext;

  /// @see #nextTokensContext
  int? nextTokensState;

  /// {@inheritDoc}
  ///
  /// <p>The default implementation simply calls {@link #endErrorCondition} to
  /// ensure that the handler is not in error recovery mode.</p>

  @override
  void reset(Parser recognizer) {
    endErrorCondition(recognizer);
  }

  /// This method is called to enter error recovery mode when a recognition
  /// exception is reported.
  ///
  /// @param recognizer the parser instance
  void beginErrorCondition(Parser recognizer) {
    errorRecoveryMode = true;
  }

  /// {@inheritDoc}

  @override
  bool inErrorRecoveryMode(Parser recognizer) {
    return errorRecoveryMode;
  }

  /// This method is called to leave error recovery mode after recovering from
  /// a recognition exception.
  ///
  /// @param recognizer
  void endErrorCondition(Parser recognizer) {
    errorRecoveryMode = false;
    lastErrorStates = null;
    lastErrorIndex = -1;
  }

  /// {@inheritDoc}
  ///
  /// <p>The default implementation simply calls {@link #endErrorCondition}.</p>

  @override
  void reportMatch(Parser recognizer) {
    endErrorCondition(recognizer);
  }

  /// {@inheritDoc}
  ///
  /// <p>The default implementation returns immediately if the handler is already
  /// in error recovery mode. Otherwise, it calls {@link #beginErrorCondition}
  /// and dispatches the reporting task based on the runtime type of [e]
  /// according to the following table.</p>
  ///
  /// <ul>
  /// <li>[NoViableAltException]: Dispatches the call to
  /// {@link #reportNoViableAlternative}</li>
  /// <li>[InputMismatchException]: Dispatches the call to
  /// {@link #reportInputMismatch}</li>
  /// <li>[FailedPredicateException]: Dispatches the call to
  /// {@link #reportFailedPredicate}</li>
  /// <li>All other types: calls {@link Parser#notifyErrorListeners} to report
  /// the exception</li>
  /// </ul>

  @override
  void reportError(Parser recognizer, RecognitionException e) {
    // if we've already reported an error and have not matched a token
    // yet successfully, don't report any errors.
    if (inErrorRecoveryMode(recognizer)) {
//			System.err.print("[SPURIOUS] ");
      return; // don't report spurious errors
    }
    beginErrorCondition(recognizer);
    if (e is NoViableAltException) {
      reportNoViableAlternative(recognizer, e);
    } else if (e is InputMismatchException) {
      reportInputMismatch(recognizer, e);
    } else if (e is FailedPredicateException) {
      reportFailedPredicate(recognizer, e);
    } else {
      log('unknown recognition error type: ${e.runtimeType}',
          level: Level.SEVERE.value);
      recognizer.notifyErrorListeners(e.message, e.offendingToken, e);
    }
  }

  /// {@inheritDoc}
  ///
  /// <p>The default implementation resynchronizes the parser by consuming tokens
  /// until we find one in the resynchronization set--loosely the set of tokens
  /// that can follow the current rule.</p>

  @override
  void recover(Parser recognizer, RecognitionException e) {
//		System.out.println("recover in "+recognizer.getRuleInvocationStack()+
//						   " index="+recognizer.inputStream.index()+
//						   ", lastErrorIndex="+
//						   lastErrorIndex+
//						   ", states="+lastErrorStates);
    if (lastErrorIndex == recognizer.inputStream.index &&
        lastErrorStates != null &&
        lastErrorStates!.contains(recognizer.state)) {
      // uh oh, another error at same token index and previously-visited
      // state in ATN; must be a case where LT(1) is in the recovery
      // token set so nothing got consumed. Consume a single token
      // at least to prevent an infinite loop; this is a failsafe.
//			log("seen error condition before index=, level: Level.SEVERE.value"+
//							   lastErrorIndex+", states="+lastErrorStates);
//			log("FAILSAFE consumes "+recognizer.getTokenNames()[recognizer.inputStream.LA(1)], level: Level.SEVERE.value);
      recognizer.consume();
    }
    lastErrorIndex = recognizer.inputStream.index;
    lastErrorStates ??= IntervalSet();
    lastErrorStates!.addOne(recognizer.state);
    final followSet = getErrorRecoverySet(recognizer);
    consumeUntil(recognizer, followSet);
  }

  /// The default implementation of {@link ANTLRErrorStrategy#sync} makes sure
  /// that the current lookahead symbol is consistent with what were expecting
  /// at this point in the ATN. You can call this anytime but ANTLR only
  /// generates code to check before subrules/loops and each iteration.
  ///
  /// <p>Implements Jim Idle's magic sync mechanism in closures and optional
  /// subrules. E.g.,</p>
  ///
  /// <pre>
  /// a : sync ( stuff sync )* ;
  /// sync : {consume to what can follow sync} ;
  /// </pre>
  ///
  /// At the start of a sub rule upon error, {@link #sync} performs single
  /// token deletion, if possible. If it can't do that, it bails on the current
  /// rule and uses the default error recovery, which consumes until the
  /// resynchronization set of the current rule.
  ///
  /// <p>If the sub rule is optional ({@code (...)?}, {@code (...)*}, or block
  /// with an empty alternative), then the expected set includes what follows
  /// the subrule.</p>
  ///
  /// <p>During loop iteration, it consumes until it sees a token that can start a
  /// sub rule or what follows loop. Yes, that is pretty aggressive. We opt to
  /// stay in the loop as long as possible.</p>
  ///
  /// <p><strong>ORIGINS</strong></p>
  ///
  /// <p>Previous versions of ANTLR did a poor job of their recovery within loops.
  /// A single mismatch token or missing token would force the parser to bail
  /// out of the entire rules surrounding the loop. So, for rule</p>
  ///
  /// <pre>
  /// classDef : 'class' ID '{' member* '}'
  /// </pre>
  ///
  /// input with an extra token between members would force the parser to
  /// consume until it found the next class definition rather than the next
  /// member definition of the current class.
  ///
  /// <p>This functionality cost a little bit of effort because the parser has to
  /// compare token set at the start of the loop and at each iteration. If for
  /// some reason speed is suffering for you, you can turn off this
  /// functionality by simply overriding this method as a blank { }.</p>

  @override
  void sync(Parser recognizer) {
    final s = recognizer.interpreter!.atn.states[recognizer.state]!;
//		log("sync @ "+s.stateNumber+"="+s.getClass().getSimpleName(), level: Level.SEVERE.value);
    // If already recovering, don't try to sync
    if (inErrorRecoveryMode(recognizer)) {
      return;
    }

    final tokens = recognizer.inputStream;
    final la = tokens.LA(1)!;

    // try cheaper subset first; might get lucky. seems to shave a wee bit off
    final nextTokens = recognizer.getATN().nextTokens(s);
    if (nextTokens.contains(la)) {
      // We are sure the token matches
      nextTokensContext = null;
      nextTokensState = ATNState.INVALID_STATE_NUMBER;
      return;
    }

    if (nextTokens.contains(Token.EPSILON)) {
      if (nextTokensContext == null) {
        // It's possible the next token won't match; information tracked
        // by sync is restricted for performance.
        nextTokensContext = recognizer.context;
        nextTokensState = recognizer.state;
      }
      return;
    }

    switch (s.stateType) {
      case StateType.BLOCK_START:
      case StateType.STAR_BLOCK_START:
      case StateType.PLUS_BLOCK_START:
      case StateType.STAR_LOOP_ENTRY:
        // report error and recover if possible
        if (singleTokenDeletion(recognizer) != null) {
          return;
        }

        throw InputMismatchException(recognizer);

      case StateType.PLUS_LOOP_BACK:
      case StateType.STAR_LOOP_BACK:
//			log("at loop back: "+s.getClass().getSimpleName(), level: Level.SEVERE.value);
        reportUnwantedToken(recognizer);
        final expecting = recognizer.expectedTokens;
        final whatFollowsLoopIterationOrRule =
            expecting | getErrorRecoverySet(recognizer);
        consumeUntil(recognizer, whatFollowsLoopIterationOrRule);
        break;

      default:
        // do nothing if we can't identify the exact kind of ATN state
        break;
    }
  }

  /// This is called by {@link #reportError} when the exception is a
  /// [NoViableAltException].
  ///
  /// @see #reportError
  ///
  /// @param recognizer the parser instance
  /// @param e the recognition exception
  void reportNoViableAlternative(Parser recognizer, NoViableAltException e) {
    final tokens = recognizer.inputStream;
    String input;

    if (e.startToken.type == Token.EOF) {
      input = '<EOF>';
    } else {
      input = tokens.getTextRange(e.startToken, e.offendingToken);
    }

    final msg = 'no viable alternative at input ' + escapeWSAndQuote(input);
    recognizer.notifyErrorListeners(msg, e.offendingToken, e);
  }

  /// This is called by {@link #reportError} when the exception is an
  /// [InputMismatchException].
  ///
  /// @see #reportError
  ///
  /// @param recognizer the parser instance
  /// @param e the recognition exception
  void reportInputMismatch(Parser recognizer, InputMismatchException e) {
    final msg = 'mismatched input ' +
        getTokenErrorDisplay(e.offendingToken) +
        ' expecting ' +
        e.expectedTokens!.toString(vocabulary: recognizer.vocabulary);
    recognizer.notifyErrorListeners(msg, e.offendingToken, e);
  }

  /// This is called by {@link #reportError} when the exception is a
  /// [FailedPredicateException].
  ///
  /// @see #reportError
  ///
  /// @param recognizer the parser instance
  /// @param e the recognition exception
  void reportFailedPredicate(Parser recognizer, FailedPredicateException e) {
    final ruleIndex = recognizer.context?.ruleIndex;
    final ruleName = ruleIndex != null ? recognizer.ruleNames[ruleIndex] : '';
    final msg = 'rule ' + ruleName + ' ' + e.message;
    recognizer.notifyErrorListeners(msg, e.offendingToken, e);
  }

  /// This method is called to report a syntax error which requires the removal
  /// of a token from the input stream. At the time this method is called, the
  /// erroneous symbol is current {@code LT(1)} symbol and has not yet been
  /// removed from the input stream. When this method returns,
  /// [recognizer] is in error recovery mode.
  ///
  /// <p>This method is called when {@link #singleTokenDeletion} identifies
  /// single-token deletion as a viable recovery strategy for a mismatched
  /// input error.</p>
  ///
  /// <p>The default implementation simply returns if the handler is already in
  /// error recovery mode. Otherwise, it calls {@link #beginErrorCondition} to
  /// enter error recovery mode, followed by calling
  /// {@link Parser#notifyErrorListeners}.</p>
  ///
  /// @param recognizer the parser instance
  void reportUnwantedToken(Parser recognizer) {
    if (inErrorRecoveryMode(recognizer)) {
      return;
    }

    beginErrorCondition(recognizer);

    final t = recognizer.currentToken;
    final tokenName = getTokenErrorDisplay(t);
    final expecting = getExpectedTokens(recognizer);
    final msg = 'extraneous input ' +
        tokenName +
        ' expecting ' +
        expecting.toString(vocabulary: recognizer.vocabulary);
    recognizer.notifyErrorListeners(msg, t, null);
  }

  /// This method is called to report a syntax error which requires the
  /// insertion of a missing token into the input stream. At the time this
  /// method is called, the missing token has not yet been inserted. When this
  /// method returns, [recognizer] is in error recovery mode.
  ///
  /// <p>This method is called when {@link #singleTokenInsertion} identifies
  /// single-token insertion as a viable recovery strategy for a mismatched
  /// input error.</p>
  ///
  /// <p>The default implementation simply returns if the handler is already in
  /// error recovery mode. Otherwise, it calls {@link #beginErrorCondition} to
  /// enter error recovery mode, followed by calling
  /// {@link Parser#notifyErrorListeners}.</p>
  ///
  /// @param recognizer the parser instance
  void reportMissingToken(Parser recognizer) {
    if (inErrorRecoveryMode(recognizer)) {
      return;
    }

    beginErrorCondition(recognizer);

    final t = recognizer.currentToken;
    final expecting = getExpectedTokens(recognizer);
    final msg = 'missing ' +
        expecting.toString(vocabulary: recognizer.vocabulary) +
        ' at ' +
        getTokenErrorDisplay(t);

    recognizer.notifyErrorListeners(msg, t, null);
  }

  /// {@inheritDoc}
  ///
  /// <p>The default implementation attempts to recover from the mismatched input
  /// by using single token insertion and deletion as described below. If the
  /// recovery attempt fails, this method throws an
  /// [InputMismatchException].</p>
  ///
  /// <p><strong>EXTRA TOKEN</strong> (single token deletion)</p>
  ///
  /// <p>{@code LA(1)} is not what we are looking for. If {@code LA(2)} has the
  /// right token, however, then assume {@code LA(1)} is some extra spurious
  /// token and delete it. Then consume and return the next token (which was
  /// the {@code LA(2)} token) as the successful result of the match operation.</p>
  ///
  /// <p>This recovery strategy is implemented by {@link #singleTokenDeletion}.</p>
  ///
  /// <p><strong>MISSING TOKEN</strong> (single token insertion)</p>
  ///
  /// <p>If current token (at {@code LA(1)}) is consistent with what could come
  /// after the expected {@code LA(1)} token, then assume the token is missing
  /// and use the parser's [TokenFactory] to create it on the fly. The
  /// "insertion" is performed by returning the created token as the successful
  /// result of the match operation.</p>
  ///
  /// <p>This recovery strategy is implemented by {@link #singleTokenInsertion}.</p>
  ///
  /// <p><strong>EXAMPLE</strong></p>
  ///
  /// <p>For example, Input {@code i=(3;} is clearly missing the {@code ')'}. When
  /// the parser returns from the nested call to [expr], it will have
  /// call chain:</p>
  ///
  /// <pre>
  /// stat &rarr; expr &rarr; atom
  /// </pre>
  ///
  /// and it will be trying to match the {@code ')'} at this point in the
  /// derivation:
  ///
  /// <pre>
  /// =&gt; ID '=' '(' INT ')' ('+' atom)* ';'
  ///                    ^
  /// </pre>
  ///
  /// The attempt to match {@code ')'} will fail when it sees {@code ';'} and
  /// call {@link #recoverInline}. To recover, it sees that {@code LA(1)==';'}
  /// is in the set of tokens that can follow the {@code ')'} token reference
  /// in rule [atom]. It can assume that you forgot the {@code ')'}.

  @override
  Token recoverInline(Parser recognizer) {
// SINGLE TOKEN DELETION
    final matchedSymbol = singleTokenDeletion(recognizer);
    if (matchedSymbol != null) {
// we have deleted the extra token.
// now, move past ttype token as if all were ok
      recognizer.consume();
      return matchedSymbol;
    }

// SINGLE TOKEN INSERTION
    if (singleTokenInsertion(recognizer)) {
      return getMissingSymbol(recognizer);
    }

// even that didn't work; must throw the exception
    InputMismatchException e;
    if (nextTokensContext == null) {
      e = InputMismatchException(recognizer);
    } else {
      e = InputMismatchException(
        recognizer,
        nextTokensState!,
        nextTokensContext,
      );
    }

    throw e;
  }

  /// This method implements the single-token insertion inline error recovery
  /// strategy. It is called by {@link #recoverInline} if the single-token
  /// deletion strategy fails to recover from the mismatched input. If this
  /// method returns [true], [recognizer] will be in error recovery
  /// mode.
  ///
  /// <p>This method determines whether or not single-token insertion is viable by
  /// checking if the {@code LA(1)} input symbol could be successfully matched
  /// if it were instead the {@code LA(2)} symbol. If this method returns
  /// [true], the caller is responsible for creating and inserting a
  /// token with the correct type to produce this behavior.</p>
  ///
  /// @param recognizer the parser instance
  /// @return [true] if single-token insertion is a viable recovery
  /// strategy for the current mismatched input, otherwise [false]
  bool singleTokenInsertion(Parser recognizer) {
    final currentSymbolType = recognizer.inputStream.LA(1)!;
    // if current token is consistent with what could come after current
    // ATN state, then we know we're missing a token; error recovery
    // is free to conjure up and insert the missing token
    final currentState = recognizer.interpreter!.atn.states[recognizer.state]!;
    final next = currentState.transition(0).target;
    final atn = recognizer.interpreter!.atn;
    final expectingAtLL2 = atn.nextTokens(next, recognizer.context);
//		System.out.println("LT(2) set="+expectingAtLL2.toString(recognizer.getTokenNames()));
    if (expectingAtLL2.contains(currentSymbolType)) {
      reportMissingToken(recognizer);
      return true;
    }
    return false;
  }

  /// This method implements the single-token deletion inline error recovery
  /// strategy. It is called by {@link #recoverInline} to attempt to recover
  /// from mismatched input. If this method returns null, the parser and error
  /// handler state will not have changed. If this method returns non-null,
  /// [recognizer] will <em>not</em> be in error recovery mode since the
  /// returned token was a successful match.
  ///
  /// <p>If the single-token deletion is successful, this method calls
  /// {@link #reportUnwantedToken} to report the error, followed by
  /// {@link Parser#consume} to actually "delete" the extraneous token. Then,
  /// before returning {@link #reportMatch} is called to signal a successful
  /// match.</p>
  ///
  /// @param recognizer the parser instance
  /// @return the successfully matched [Token] instance if single-token
  /// deletion successfully recovers from the mismatched input, otherwise
  /// null
  Token? singleTokenDeletion(Parser recognizer) {
    final nextTokenType = recognizer.inputStream.LA(2)!;
    final expecting = getExpectedTokens(recognizer);
    if (expecting.contains(nextTokenType)) {
      reportUnwantedToken(recognizer);
      /*
			log("recoverFromMismatchedToken deleting , level: Level.SEVERE.value"+
							   ((TokenStream)recognizer.inputStream).LT(1)+
							   " since "+((TokenStream)recognizer.inputStream).LT(2)+
							   " is what we want");
			*/
      recognizer.consume(); // simply delete extra token
      // we want to return the token we're actually matching
      final matchedSymbol = recognizer.currentToken;
      reportMatch(recognizer); // we know current token is correct
      return matchedSymbol;
    }
    return null;
  }

  /// Conjure up a missing token during error recovery.
  ///
  ///  The recognizer attempts to recover from single missing
  ///  symbols. But, actions might refer to that missing symbol.
  ///  For example, x=ID {f($x);}. The action clearly assumes
  ///  that there has been an identifier matched previously and that
  ///  $x points at that token. If that token is missing, but
  ///  the next token in the stream is what we want we assume that
  ///  this token is missing and we keep going. Because we
  ///  have to return some token to replace the missing token,
  ///  we have to conjure one up. This method gives the user control
  ///  over the tokens returned for missing tokens. Mostly,
  ///  you will want to create something special for identifier
  ///  tokens. For literals such as '{' and ',', the default
  ///  action in the parser or tree parser works. It simply creates
  ///  a CommonToken of the appropriate type. The text will be the token.
  ///  If you change what tokens must be created by the lexer,
  ///  override this method to create the appropriate tokens.
  Token getMissingSymbol(Parser recognizer) {
    final currentSymbol = recognizer.currentToken;
    final expecting = getExpectedTokens(recognizer);
    var expectedTokenType = Token.INVALID_TYPE;
    if (!expecting.isNil) {
      expectedTokenType = expecting.minElement; // get any element
    }
    String tokenText;
    if (expectedTokenType == Token.EOF) {
      tokenText = '<missing EOF>';
    } else {
      tokenText = '<missing ' +
          recognizer.vocabulary.getDisplayName(expectedTokenType) +
          '>';
    }
    var current = currentSymbol;
    final lookback = recognizer.inputStream.LT(-1);
    if (current.type == Token.EOF && lookback != null) {
      current = lookback;
    }
    return recognizer.tokenFactory.create(
      expectedTokenType,
      tokenText,
      Pair(current.tokenSource, current.tokenSource?.inputStream),
      Token.DEFAULT_CHANNEL,
      -1,
      -1,
      current.line,
      current.charPositionInLine,
    );
  }

  IntervalSet getExpectedTokens(Parser recognizer) {
    return recognizer.expectedTokens;
  }

  /// How should a token be displayed in an error message? The default
  ///  is to display just the text, but during development you might
  ///  want to have a lot of information spit out.  Override in that case
  ///  to use t.toString() (which, for CommonToken, dumps everything about
  ///  the token). This is better than forcing you to override a method in
  ///  your token objects because you don't have to go modify your lexer
  ///  so that it creates a new Java type.
  String getTokenErrorDisplay(Token? t) {
    if (t == null) return '<no token>';
    var s = getSymbolText(t);
    if (s == null) {
      if (getSymbolType(t) == Token.EOF) {
        s = '<EOF>';
      } else {
        s = '<${getSymbolType(t)}>';
      }
    }
    return escapeWSAndQuote(s);
  }

  String? getSymbolText(Token symbol) {
    return symbol.text;
  }

  int getSymbolType(Token symbol) {
    return symbol.type;
  }

  String escapeWSAndQuote(String s) {
//		if ( s==null ) return s;
    s = s.replaceAll('\n', r'\n');
    s = s.replaceAll('\r', r'\r');
    s = s.replaceAll('\t', r'\t');
    return "'" + s + "'";
  }

/*  Compute the error recovery set for the current rule.  During
	 *  rule invocation, the parser pushes the set of tokens that can
	 *  follow that rule reference on the stack; this amounts to
	 *  computing FIRST of what follows the rule reference in the
	 *  enclosing rule. See LinearApproximator.FIRST().
	 *  This local follow set only includes tokens
	 *  from within the rule; i.e., the FIRST computation done by
	 *  ANTLR stops at the end of a rule.
	 *
	 *  EXAMPLE
	 *
	 *  When you find a "no viable alt exception", the input is not
	 *  consistent with any of the alternatives for rule r.  The best
	 *  thing to do is to consume tokens until you see something that
	 *  can legally follow a call to r *or* any rule that called r.
	 *  You don't want the exact set of viable next tokens because the
	 *  input might just be missing a token--you might consume the
	 *  rest of the input looking for one of the missing tokens.
	 *
	 *  Consider grammar:
	 *
	 *  a : '[' b ']'
	 *    | '(' b ')'
	 *    ;
	 *  b : c '^' INT ;
	 *  c : ID
	 *    | INT
	 *    ;
	 *
	 *  At each rule invocation, the set of tokens that could follow
	 *  that rule is pushed on a stack.  Here are the various
	 *  context-sensitive follow sets:
	 *
	 *  FOLLOW(b1_in_a) = FIRST(']') = ']'
	 *  FOLLOW(b2_in_a) = FIRST(')') = ')'
	 *  FOLLOW(c_in_b) = FIRST('^') = '^'
	 *
	 *  Upon erroneous input "[]", the call chain is
	 *
	 *  a -> b -> c
	 *
	 *  and, hence, the follow context stack is:
	 *
	 *  depth     follow set       start of rule execution
	 *    0         <EOF>                    a (from main())
	 *    1          ']'                     b
	 *    2          '^'                     c
	 *
	 *  Notice that ')' is not included, because b would have to have
	 *  been called from a different context in rule a for ')' to be
	 *  included.
	 *
	 *  For error recovery, we cannot consider FOLLOW(c)
	 *  (context-sensitive or otherwise).  We need the combined set of
	 *  all context-sensitive FOLLOW sets--the set of all tokens that
	 *  could follow any reference in the call chain.  We need to
	 *  resync to one of those tokens.  Note that FOLLOW(c)='^' and if
	 *  we resync'd to that token, we'd consume until EOF.  We need to
	 *  sync to context-sensitive FOLLOWs for a, b, and c: {']','^'}.
	 *  In this case, for input "[]", LA(1) is ']' and in the set, so we would
	 *  not consume anything. After printing an error, rule c would
	 *  return normally.  Rule b would not find the required '^' though.
	 *  At this point, it gets a mismatched token error and throws an
	 *  exception (since LA(1) is not in the viable following token
	 *  set).  The rule exception handler tries to recover, but finds
	 *  the same recovery set and doesn't consume anything.  Rule b
	 *  exits normally returning to rule a.  Now it finds the ']' (and
	 *  with the successful match exits errorRecovery mode).
	 *
	 *  So, you can see that the parser walks up the call chain looking
	 *  for the token that was a member of the recovery set.
	 *
	 *  Errors are not generated in errorRecovery mode.
	 *
	 *  ANTLR's error recovery mechanism is based upon original ideas:
	 *
	 *  "Algorithms + Data Structures = Programs" by Niklaus Wirth
	 *
	 *  and
	 *
	 *  "A note on error recovery in recursive descent parsers":
	 *  http://portal.acm.org/citation.cfm?id=947902.947905
	 *
	 *  Later, Josef Grosch had some good ideas:
	 *
	 *  "Efficient and Comfortable Error Recovery in Recursive Descent
	 *  Parsers":
	 *  ftp://www.cocolab.com/products/cocktail/doca4.ps/ell.ps.zip
	 *
	 *  Like Grosch I implement context-sensitive FOLLOW sets that are combined
	 *  at run-time upon error to avoid overhead during parsing.
	 */
  IntervalSet getErrorRecoverySet(Parser recognizer) {
    final atn = recognizer.interpreter!.atn;
    RuleContext? ctx = recognizer.context;
    final recoverSet = IntervalSet();
    while (ctx != null && ctx.invokingState >= 0) {
      // compute what follows who invoked us
      final invokingState = atn.states[ctx.invokingState]!;
      final rt = invokingState.transition(0) as RuleTransition;
      final follow = atn.nextTokens(rt.followState);
      recoverSet.addAll(follow);
      ctx = ctx.parent;
    }
    recoverSet.remove(Token.EPSILON);
    return recoverSet;
  }

  /// Consume tokens until one matches the given token set. */
  void consumeUntil(Parser recognizer, IntervalSet set) {
//		log("consumeUntil("+set.toString(recognizer.getTokenNames())+")", level: Level.SEVERE.value);
    var ttype = recognizer.inputStream.LA(1)!;
    while (ttype != Token.EOF && !set.contains(ttype)) {
      //System.out.println("consume during recover LA(1)="+getTokenNames()[input.LA(1)]);
//			recognizer.inputStream.consume();
      recognizer.consume();
      ttype = recognizer.inputStream.LA(1)!;
    }
  }
}

/// This implementation of [ANTLRErrorStrategy] responds to syntax errors
/// by immediately canceling the parse operation with a
/// [ParseCancellationException]. The implementation ensures that the
/// {@link ParserRuleContext#exception} field is set for all parse tree nodes
/// that were not completed prior to encountering the error.
///
/// <p>
/// This error strategy is useful in the following scenarios.</p>
///
/// <ul>
/// <li><strong>Two-stage parsing:</strong> This error strategy allows the first
/// stage of two-stage parsing to immediately terminate if an error is
/// encountered, and immediately fall back to the second stage. In addition to
/// avoiding wasted work by attempting to recover from errors here, the empty
/// implementation of {@link BailErrorStrategy#sync} improves the performance of
/// the first stage.</li>
/// <li><strong>Silent validation:</strong> When syntax errors are not being
/// reported or logged, and the parse result is simply ignored if errors occur,
/// the [BailErrorStrategy] avoids wasting work on recovering from errors
/// when the result will be ignored either way.</li>
/// </ul>
///
/// <p>
/// {@code myparser.setErrorHandler(new BailErrorStrategy());}</p>
///
/// @see Parser#setErrorHandler(ANTLRErrorStrategy)
class BailErrorStrategy extends DefaultErrorStrategy {
  /// Instead of recovering from exception [e], re-throw it wrapped
  ///  in a [ParseCancellationException] so it is not caught by the
  ///  rule function catches.  Use {@link Exception#getCause()} to get the
  ///  original [RecognitionException].

  @override
  void recover(Parser recognizer, RecognitionException e) {
    for (var context = recognizer.context;
        context != null;
        context = context.parent) {
      context.exception = e;
    }

    throw ParseCancellationException(e.message);
  }

  /// Make sure we don't attempt to recover inline; if the parser
  ///  successfully recovers, it won't throw an exception.

  @override
  Token recoverInline(Parser recognizer) {
    final e = InputMismatchException(recognizer);
    for (var context = recognizer.context;
        context != null;
        context = context.parent) {
      context.exception = e;
    }

    throw ParseCancellationException(e.message);
  }

  /// Make sure we don't attempt to recover from problems in subrules. */

  @override
  void sync(Parser recognizer) {}
}
