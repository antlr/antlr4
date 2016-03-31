/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Dan McLaughlin
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

#pragma once

#include "Recognizer.h"

namespace org {
namespace antlr {
namespace v4 {
namespace runtime {
namespace atn {

  /// <summary>
  /// A tree structure used to record the semantic context in which
  ///  an ATN configuration is valid.  It's either a single predicate,
  ///  a conjunction {@code p1&&p2}, or a sum of products {@code p1||p2}.
  /// <p/>
  ///  I have scoped the <seealso cref="AND"/>, <seealso cref="OR"/>, and <seealso cref="Predicate"/> subclasses of
  ///  <seealso cref="SemanticContext"/> within the scope of this outer class.
  /// </summary>
  class SemanticContext {
  public:
    SemanticContext *parent;
    static SemanticContext *const NONE;

    virtual size_t hashCode() = 0;
    virtual std::wstring toString() const = 0;
    virtual bool operator == (const SemanticContext &other) const = 0;

    /// <summary>
    /// For context independent predicates, we evaluate them without a local
    /// context (i.e., null context). That way, we can evaluate them without
    /// having to create proper rule-specific context during prediction (as
    /// opposed to the parser, which creates them naturally). In a practical
    /// sense, this avoids a cast exception from RuleContext to myruleContext.
    /// <p/>
    /// For context dependent predicates, we must pass in a local context so that
    /// references such as $arg evaluate properly as _localctx.arg. We only
    /// capture context dependent predicates in the context in which we begin
    /// prediction, so we passed in the outer context here in case of context
    /// dependent predicate evaluation.
    /// </summary>
    virtual bool eval(Recognizer *parser, RuleContext *outerContext) = 0;

    static SemanticContext *And(SemanticContext *a, SemanticContext *b);

    /// See also: ParserATNSimulator::getPredsForAmbigAlts.
    static SemanticContext *Or(SemanticContext *a, SemanticContext *b);

    class Predicate;
    class PrecedencePredicate;
    class AND;
    class OR;

  private:
    template<typename T1> // where T1 : SemanticContext
    static std::vector<PrecedencePredicate*> filterPrecedencePredicates(std::vector<T1> *collection) {
      std::vector<PrecedencePredicate*> result;
      for (std::vector<SemanticContext*>::const_iterator iterator = collection->begin(); iterator != collection->end(); ++iterator) {
        SemanticContext *context = *iterator;
        if ((PrecedencePredicate*)(context) != nullptr) {
          result.push_back((PrecedencePredicate*)context);
        }
      }

      return result;
    }

  };


  class SemanticContext::Predicate : public SemanticContext {
    friend class SemanticContext;

  public:
    const int ruleIndex;
    const int predIndex;
    const bool isCtxDependent; // e.g., $i ref in pred

  protected:
    Predicate();

  public:
    Predicate(int ruleIndex, int predIndex, bool isCtxDependent);

    virtual bool eval(Recognizer *parser, RuleContext *outerContext) override {
      RuleContext *localctx = isCtxDependent ? outerContext : nullptr;
      return parser->sempred(localctx, ruleIndex, predIndex);
    }

    virtual size_t hashCode() override;
    virtual bool operator == (const SemanticContext &other) const override;
    virtual std::wstring toString() const override;
  };

  class SemanticContext::PrecedencePredicate : public SemanticContext {
  public:
    const int precedence;

  protected:
    PrecedencePredicate();

  public:
    PrecedencePredicate(int precedence);

    virtual bool eval(Recognizer *parser, RuleContext *outerContext) override {
      return parser->precpred(outerContext, precedence);
    }

    virtual int compareTo(PrecedencePredicate *o);
    virtual size_t hashCode() override;
    virtual bool operator == (const SemanticContext &other) const override;
    virtual std::wstring toString() const override;

    static bool lessThan(const PrecedencePredicate &a, const PrecedencePredicate &b) {
      return a.precedence < b.precedence;
    }
    static bool greaterThan(const PrecedencePredicate &a,
                            const PrecedencePredicate &b) {
      return a.precedence > b.precedence;
    }
  };

  class SemanticContext::AND : public SemanticContext {
  public:
    std::vector<SemanticContext*> opnds;

    AND(SemanticContext *a, SemanticContext *b);

    virtual bool operator == (const SemanticContext &other) const override;
    virtual size_t hashCode() override;

    virtual bool eval(Recognizer *parser, RuleContext *outerContext) override {
      for (auto opnd : opnds) {
        if (!opnd->eval(parser, outerContext)) {
          return false;
        }
      }
      return true;
    }


    virtual std::wstring toString()  const override;
  };

  class SemanticContext::OR : public SemanticContext {
  public:
    std::vector<SemanticContext*> opnds;

    OR(SemanticContext *a, SemanticContext *b);

    virtual bool operator == (const SemanticContext &other) const override;
    virtual size_t hashCode() override;

    virtual bool eval(Recognizer *parser, RuleContext *outerContext) override {
      for (auto opnd : opnds) {
        if (opnd->eval(parser, outerContext)) {
          return true;
        }
      }
      return false;
    }

    virtual std::wstring toString() const override;
  };

} // namespace atn
} // namespace runtime
} // namespace v4
} // namespace antlr
} // namespace org

// Hash function for SemanticContext, used in the MurmurHash::update function

namespace std {
  using org::antlr::v4::runtime::atn::SemanticContext;

  template <> struct hash<SemanticContext>
  {
    size_t operator () ( SemanticContext &x) const
    {
      return x.hashCode();
    }
  };
}
