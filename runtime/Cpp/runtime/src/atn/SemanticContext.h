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
#include "support/CPPUtils.h"

namespace antlr4 {
namespace atn {

  /// A tree structure used to record the semantic context in which
  ///  an ATN configuration is valid.  It's either a single predicate,
  ///  a conjunction "p1 && p2", or a sum of products "p1||p2".
  ///
  ///  I have scoped the AND, OR, and Predicate subclasses of
  ///  SemanticContext within the scope of this outer class.
  class ANTLR4CPP_PUBLIC SemanticContext : public std::enable_shared_from_this<SemanticContext> {
  public:
    struct Hasher
    {
      size_t operator()(Ref<SemanticContext> const& k) const {
        return k->hashCode();
      }
    };

    struct Comparer {
      bool operator()(Ref<SemanticContext> const& lhs, Ref<SemanticContext> const& rhs) const {
        return *lhs == *rhs;
      }
    };


    using Set = std::unordered_set<Ref<SemanticContext>, Hasher, Comparer>;

    /**
     * The default {@link SemanticContext}, which is semantically equivalent to
     * a predicate of the form {@code {true}?}.
     */
    static const Ref<SemanticContext> NONE;

    virtual ~SemanticContext() {};
    
    virtual size_t hashCode() const = 0;
    virtual std::string toString() const = 0;
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
    virtual bool eval(Recognizer *parser, Ref<RuleContext> const& parserCallStack) = 0;

    /**
     * Evaluate the precedence predicates for the context and reduce the result.
     *
     * @param parser The parser instance.
     * @param parserCallStack
     * @return The simplified semantic context after precedence predicates are
     * evaluated, which will be one of the following values.
     * <ul>
     * <li>{@link #NONE}: if the predicate simplifies to {@code true} after
     * precedence predicates are evaluated.</li>
     * <li>{@code null}: if the predicate simplifies to {@code false} after
     * precedence predicates are evaluated.</li>
     * <li>{@code this}: if the semantic context is not changed as a result of
     * precedence predicate evaluation.</li>
     * <li>A non-{@code null} {@link SemanticContext}: the new simplified
     * semantic context after precedence predicates are evaluated.</li>
     * </ul>
     */
    virtual Ref<SemanticContext> evalPrecedence(Recognizer *parser, Ref<RuleContext> const& parserCallStack);

    static Ref<SemanticContext> And(Ref<SemanticContext> const& a, Ref<SemanticContext> const& b);

    /// See also: ParserATNSimulator::getPredsForAmbigAlts.
    static Ref<SemanticContext> Or(Ref<SemanticContext> const& a, Ref<SemanticContext> const& b);

    class Predicate;
    class PrecedencePredicate;
    class Operator;
    class AND;
    class OR;

  private:
    static std::vector<Ref<PrecedencePredicate>> filterPrecedencePredicates(const Set &collection);
  };

  class ANTLR4CPP_PUBLIC SemanticContext::Predicate : public SemanticContext {
  public:
    const int ruleIndex;
    const int predIndex;
    const bool isCtxDependent; // e.g., $i ref in pred

  protected:
    Predicate();

  public:
    Predicate(int ruleIndex, int predIndex, bool isCtxDependent);

    virtual bool eval(Recognizer *parser, Ref<RuleContext> const& parserCallStack) override;
    virtual size_t hashCode() const override;
    virtual bool operator == (const SemanticContext &other) const override;
    virtual std::string toString() const override;
  };
  
  class ANTLR4CPP_PUBLIC SemanticContext::PrecedencePredicate : public SemanticContext {
  public:
    const int precedence;

  protected:
    PrecedencePredicate();

  public:
    PrecedencePredicate(int precedence);

    virtual bool eval(Recognizer *parser, Ref<RuleContext> const& parserCallStack) override;
    virtual Ref<SemanticContext> evalPrecedence(Recognizer *parser, Ref<RuleContext> const& parserCallStack) override;
    virtual int compareTo(PrecedencePredicate *o);
    virtual size_t hashCode() const override;
    virtual bool operator == (const SemanticContext &other) const override;
    virtual std::string toString() const override;
  };

  /**
   * This is the base class for semantic context "operators", which operate on
   * a collection of semantic context "operands".
   *
   * @since 4.3
   */
  class ANTLR4CPP_PUBLIC SemanticContext::Operator : public SemanticContext {
  public:
    /**
     * Gets the operands for the semantic context operator.
     *
     * @return a collection of {@link SemanticContext} operands for the
     * operator.
     *
     * @since 4.3
     */

    virtual std::vector<Ref<SemanticContext>> getOperands() const = 0;
  };
  
  /**
   * A semantic context which is true whenever none of the contained contexts
   * is false.
   */
  class ANTLR4CPP_PUBLIC SemanticContext::AND : public SemanticContext::Operator {
  public:
    std::vector<Ref<SemanticContext>> opnds;

    AND(Ref<SemanticContext> const& a, Ref<SemanticContext> const& b) ;

    virtual std::vector<Ref<SemanticContext>> getOperands() const override;
    virtual bool operator == (const SemanticContext &other) const override;
    virtual size_t hashCode() const override;

    /**
     * The evaluation of predicates by this context is short-circuiting, but
     * unordered.</p>
     */
    virtual bool eval(Recognizer *parser, Ref<RuleContext> const& parserCallStack) override;
    virtual Ref<SemanticContext> evalPrecedence(Recognizer *parser, Ref<RuleContext> const& parserCallStack) override;
    virtual std::string toString() const override;
  };

  /**
   * A semantic context which is true whenever at least one of the contained
   * contexts is true.
   */
  class ANTLR4CPP_PUBLIC SemanticContext::OR : public SemanticContext::Operator {
  public:
    std::vector<Ref<SemanticContext>> opnds;

    OR(Ref<SemanticContext> const& a, Ref<SemanticContext> const& b);

    virtual std::vector<Ref<SemanticContext>> getOperands() const override;
    virtual bool operator == (const SemanticContext &other) const override;
    virtual size_t hashCode() const override;

    /**
     * The evaluation of predicates by this context is short-circuiting, but
     * unordered.
     */
    virtual bool eval(Recognizer *parser, Ref<RuleContext> const& parserCallStack) override;
    virtual Ref<SemanticContext> evalPrecedence(Recognizer *parser, Ref<RuleContext> const& parserCallStack) override;
    virtual std::string toString() const override;
  };
  
} // namespace atn
} // namespace antlr4

// Hash function for SemanticContext, used in the MurmurHash::update function

namespace std {
  using antlr4::atn::SemanticContext;

  template <> struct hash<SemanticContext>
  {
    size_t operator () (SemanticContext &x) const
    {
      return x.hashCode();
    }
  };
}
