/*
 * [The "BSD license"]
 * Copyright (c) 2016 Mike Lischke
 * Copyright (c) 2013 Terence Parr
 * Copyright (c) 2013 Dan McLaughlin
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#pragma once

#include "Parser.h"
#include "atn/ATN.h"
#include "support/BitSet.h"
#include "atn/PredictionContext.h"
#include "Vocabulary.h"

namespace antlr4 {

  /// <summary>
  /// A parser simulator that mimics what ANTLR's generated
  ///  parser code does. A ParserATNSimulator is used to make
  ///  predictions via adaptivePredict but this class moves a pointer through the
  ///  ATN to simulate parsing. ParserATNSimulator just
  ///  makes us efficient rather than having to backtrack, for example.
  ///
  ///  This properly creates parse trees even for left recursive rules.
  ///
  ///  We rely on the left recursive rule invocation and special predicate
  ///  transitions to make left recursive rules work.
  ///
  ///  See TestParserInterpreter for examples.
  /// </summary>
  class ANTLR4CPP_PUBLIC ParserInterpreter : public Parser {
  public:
    // @deprecated
    ParserInterpreter(const std::string &grammarFileName, const std::vector<std::string>& tokenNames,
      const std::vector<std::string>& ruleNames, const atn::ATN &atn, TokenStream *input);
    ParserInterpreter(const std::string &grammarFileName, const dfa::Vocabulary &vocabulary,
                      const std::vector<std::string> &ruleNames, const atn::ATN &atn, TokenStream *input);
    ~ParserInterpreter();

    virtual void reset() override;
    
    virtual const atn::ATN& getATN() const override;

    // @deprecated
    virtual const std::vector<std::string>& getTokenNames() const override;

    virtual const dfa::Vocabulary& getVocabulary() const override;
    
    virtual const std::vector<std::string>& getRuleNames() const override;
    virtual std::string getGrammarFileName() const override;

    /// Begin parsing at startRuleIndex
    virtual Ref<ParserRuleContext> parse(int startRuleIndex);

    virtual void enterRecursionRule(Ref<ParserRuleContext> const& localctx, int state, int ruleIndex, int precedence) override;


    /** Override this parser interpreters normal decision-making process
     *  at a particular decision and input token index. Instead of
     *  allowing the adaptive prediction mechanism to choose the
     *  first alternative within a block that leads to a successful parse,
     *  force it to take the alternative, 1..n for n alternatives.
     *
     *  As an implementation limitation right now, you can only specify one
     *  override. This is sufficient to allow construction of different
     *  parse trees for ambiguous input. It means re-parsing the entire input
     *  in general because you're never sure where an ambiguous sequence would
     *  live in the various parse trees. For example, in one interpretation,
     *  an ambiguous input sequence would be matched completely in expression
     *  but in another it could match all the way back to the root.
     *
     *  s : e '!'? ;
     *  e : ID
     *    | ID '!'
     *    ;
     *
     *  Here, x! can be matched as (s (e ID) !) or (s (e ID !)). In the first
     *  case, the ambiguous sequence is fully contained only by the root.
     *  In the second case, the ambiguous sequences fully contained within just
     *  e, as in: (e ID !).
     *
     *  Rather than trying to optimize this and make
     *  some intelligent decisions for optimization purposes, I settled on
     *  just re-parsing the whole input and then using
     *  {link Trees#getRootOfSubtreeEnclosingRegion} to find the minimal
     *  subtree that contains the ambiguous sequence. I originally tried to
     *  record the call stack at the point the parser detected and ambiguity but
     *  left recursive rules create a parse tree stack that does not reflect
     *  the actual call stack. That impedance mismatch was enough to make
     *  it it challenging to restart the parser at a deeply nested rule
     *  invocation.
     *
     *  Only parser interpreters can override decisions so as to avoid inserting
     *  override checking code in the critical ALL(*) prediction execution path.
     *
     *  @since 4.5.1
     */
    void addDecisionOverride(int decision, int tokenIndex, int forcedAlt);
    
    Ref<InterpreterRuleContext> getOverrideDecisionRoot() const;
    
    /** Return the root of the parse, which can be useful if the parser
     *  bails out. You still can access the top node. Note that,
     *  because of the way left recursive rules add children, it's possible
     *  that the root will not have any children if the start rule immediately
     *  called and left recursive rule that fails.
     *
     * @since 4.5.1
     */
    Ref<InterpreterRuleContext> getRootContext();

  protected:
    const std::string _grammarFileName;
    std::vector<std::string> _tokenNames;
    const atn::ATN &_atn;

    std::vector<std::string> _ruleNames;

    std::vector<dfa::DFA> _decisionToDFA; // not shared like it is for generated parsers
    atn::PredictionContextCache _sharedContextCache;

    /** This stack corresponds to the _parentctx, _parentState pair of locals
     *  that would exist on call stack frames with a recursive descent parser;
     *  in the generated function for a left-recursive rule you'd see:
     *
     *  private EContext e(int _p) throws RecognitionException {
     *      ParserRuleContext _parentctx = _ctx;    // Pair.a
     *      int _parentState = getState();          // Pair.b
     *      ...
     *  }
     *
     *  Those values are used to create new recursive rule invocation contexts
     *  associated with left operand of an alt like "expr '*' expr".
     */
    std::stack<std::pair<Ref<ParserRuleContext>, int>> _parentContextStack;
    
    /** We need a map from (decision,inputIndex)->forced alt for computing ambiguous
     *  parse trees. For now, we allow exactly one override.
     */
    int _overrideDecision = -1;
    int _overrideDecisionInputIndex = -1;
    int _overrideDecisionAlt = -1;
    bool _overrideDecisionReached = false; // latch and only override once; error might trigger infinite loop

    /** What is the current context when we override a decision? This tells
     *  us what the root of the parse tree is when using override
     *  for an ambiguity/lookahead check.
     */
    Ref<InterpreterRuleContext> _overrideDecisionRoot;
    Ref<InterpreterRuleContext> _rootContext;
    
    virtual atn::ATNState *getATNState();
    virtual void visitState(atn::ATNState *p);

    /** Method visitDecisionState() is called when the interpreter reaches
     *  a decision state (instance of DecisionState). It gives an opportunity
     *  for subclasses to track interesting things.
     */
    int visitDecisionState(atn::DecisionState *p);

    /** Provide simple "factory" for InterpreterRuleContext's.
     *  @since 4.5.1
     */
    Ref<InterpreterRuleContext> createInterpreterRuleContext(std::weak_ptr<ParserRuleContext> parent, int invokingStateNumber,
                                                             int ruleIndex);

    virtual void visitRuleStopState(atn::ATNState *p);

    /** Rely on the error handler for this parser but, if no tokens are consumed
     *  to recover, add an error node. Otherwise, nothing is seen in the parse
     *  tree.
     */
    void recover(RecognitionException &e);
    Token* recoverInline();

  private:
    const dfa::Vocabulary &_vocabulary;
    std::unique_ptr<Token> _errorToken;
  };

} // namespace antlr4
