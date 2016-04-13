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
#include "CPPUtils.h"

namespace org {
namespace antlr {
namespace v4 {
namespace runtime {
namespace atn {

  /// A tree structure used to record the semantic context in which
  ///  an ATN configuration is valid.  It's either a single predicate,
  ///  a conjunction "p1 && p2", or a sum of products "p1||p2".
  ///
  ///  I have scoped the AND, OR, and Predicate subclasses of
  ///  SemanticContext within the scope of this outer class.
  class SemanticContext {
  public:
    //SemanticContext *parent;
    static const SemanticContextRef NONE;

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
    virtual bool eval(Recognizer *parser, RuleContextRef outerContext) = 0;

    static SemanticContextRef And(SemanticContextRef a, SemanticContextRef b);

    /// See also: ParserATNSimulator::getPredsForAmbigAlts.
    static SemanticContextRef Or(SemanticContextRef a, SemanticContextRef b);

    class Predicate;
    class PrecedencePredicate;
    class AND;
    class OR;

  private:
    template<typename T1> // where T1 : SemanticContextRef
    static std::vector<std::shared_ptr<PrecedencePredicate>> filterPrecedencePredicates(const std::vector<T1> &collection) {
      std::vector<std::shared_ptr<PrecedencePredicate>> result;
      for (auto context : collection) {
        if (antlrcpp::is<PrecedencePredicate>(context)) {
          result.push_back(std::dynamic_pointer_cast<PrecedencePredicate>(context));
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

    virtual bool eval(Recognizer *parser, RuleContextRef outerContext) override {
      RuleContextRef localctx;
      if (isCtxDependent)
        localctx = outerContext;
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

    virtual bool eval(Recognizer *parser, RuleContextRef outerContext) override {
      return parser->precpred(outerContext, precedence);
    }

    virtual int compareTo(PrecedencePredicate *o);
    virtual size_t hashCode() override;
    virtual bool operator == (const SemanticContext &other) const override;
    virtual std::wstring toString() const override;
  };

  class SemanticContext::AND : public SemanticContext {
  public:
    std::vector<SemanticContextRef> opnds;

    AND(SemanticContextRef a, SemanticContextRef b);

    virtual bool operator == (const SemanticContext &other) const override;
    virtual size_t hashCode() override;

    virtual bool eval(Recognizer *parser, RuleContextRef outerContext) override {
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
    std::vector<SemanticContextRef> opnds;

    OR(SemanticContextRef a, SemanticContextRef b);

    virtual bool operator == (const SemanticContext &other) const override;
    virtual size_t hashCode() override;

    virtual bool eval(Recognizer *parser, RuleContextRef outerContext) override {
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
