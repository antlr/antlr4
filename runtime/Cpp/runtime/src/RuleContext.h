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

#include "tree/RuleNode.h"

namespace antlr4 {

  /** A rule context is a record of a single rule invocation.
   *
   *  We form a stack of these context objects using the parent
   *  pointer. A parent pointer of null indicates that the current
   *  context is the bottom of the stack. The ParserRuleContext subclass
   *  as a children list so that we can turn this data structure into a
   *  tree.
   *
   *  The root node always has a null pointer and invokingState of -1.
   *
   *  Upon entry to parsing, the first invoked rule function creates a
   *  context object (asubclass specialized for that rule such as
   *  SContext) and makes it the root of a parse tree, recorded by field
   *  Parser._ctx.
   *
   *  public final SContext s() throws RecognitionException {
   *      SContext _localctx = new SContext(_ctx, getState()); <-- create new node
   *      enterRule(_localctx, 0, RULE_s);                     <-- push it
   *      ...
   *      exitRule();                                          <-- pop back to _localctx
   *      return _localctx;
   *  }
   *
   *  A subsequent rule invocation of r from the start rule s pushes a
   *  new context object for r whose parent points at s and use invoking
   *  state is the state with r emanating as edge label.
   *
   *  The invokingState fields from a context object to the root
   *  together form a stack of rule indication states where the root
   *  (bottom of the stack) has a -1 sentinel value. If we invoke start
   *  symbol s then call r1, which calls r2, the  would look like
   *  this:
   *
   *     SContext[-1]   <- root node (bottom of the stack)
   *     R1Context[p]   <- p in rule s called r1
   *     R2Context[q]   <- q in rule r1 called r2
   *
   *  So the top of the stack, _ctx, represents a call to the current
   *  rule and it holds the return address from another rule that invoke
   *  to this rule. To invoke a rule, we must always have a current context.
   *
   *  The parent contexts are useful for computing lookahead sets and
   *  getting error information.
   *
   *  These objects are used during parsing and prediction.
   *  For the special case of parsers, we use the subclass
   *  ParserRuleContext.
   *
   *  @see ParserRuleContext
   */
  class ANTLR4CPP_PUBLIC RuleContext : public tree::RuleNode, public std::enable_shared_from_this<RuleContext> {
  public:
    /// What context invoked this rule?
    std::weak_ptr<RuleContext> parent;

    /// What state invoked the rule associated with this context?
    /// The "return address" is the followState of invokingState
    /// If parent is null, this should be -1 and this context object represents the start rule.
    int invokingState;

    RuleContext();

    RuleContext(std::weak_ptr<RuleContext> parent, int invokingState);

    virtual int depth();

    /// A context is empty if there is no invoking state; meaning nobody called current context.
    virtual bool isEmpty();

    // satisfy the ParseTree / SyntaxTree interface

    virtual misc::Interval getSourceInterval() override;

    virtual Ref<RuleContext> getRuleContext() override;
    virtual std::string getText() override;

    virtual ssize_t getRuleIndex() const;

    /** For rule associated with this parse tree internal node, return
     *  the outer alternative number used to match the input. Default
     *  implementation does not compute nor store this alt num. Create
     *  a subclass of ParserRuleContext with backing field and set
     *  option contextSuperClass.
     *  to set it.
     *
     *  @since 4.5.3
     */
    virtual int getAltNumber() const;

    /** Set the outer alternative number for this context node. Default
     *  implementation does nothing to avoid backing field overhead for
     *  trees that don't need it.  Create
     *  a subclass of ParserRuleContext with backing field and set
     *  option contextSuperClass.
     *
     *  @since 4.5.3
     */
    virtual void setAltNumber(int altNumber);

    virtual std::size_t getChildCount() override;

    template<typename T, typename T1>
    T accept(tree::ParseTreeVisitor<T1> *visitor)  {
      return visitor->visitChildren(this);
    }

    /// <summary>
    /// Print out a whole tree, not just a node, in LISP format
    ///  (root child1 .. childN). Print just a node if this is a leaf.
    ///  We have to know the recognizer so we can get rule names.
    /// </summary>
    virtual std::string toStringTree(Parser *recog) override;

    /// <summary>
    /// Print out a whole tree, not just a node, in LISP format
    ///  (root child1 .. childN). Print just a node if this is a leaf.
    /// </summary>
    virtual std::string toStringTree(std::vector<std::string> &ruleNames);

    virtual std::string toStringTree() override;
    virtual std::string toString() override;
    std::string toString(Recognizer *recog);
    std::string toString(const std::vector<std::string> &ruleNames);

    // recog null unless ParserRuleContext, in which case we use subclass toString(...)
    std::string toString(Recognizer *recog, Ref<RuleContext> const& stop);

    virtual std::string toString(const std::vector<std::string> &ruleNames, Ref<RuleContext> const& stop);

    bool operator == (const RuleContext &other) { return this == &other; } // Simple address comparison.
    
  protected:
    virtual std::weak_ptr<Tree> getParentReference() override;
    virtual Ref<Tree> getChildReference(size_t i) override;

  private:
    void InitializeInstanceFields();
  };

} // namespace antlr4
