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

#include "PredictionContext.h"
#include "SemanticContext.h"

namespace org {
namespace antlr {
namespace v4 {
namespace runtime {
namespace atn {

  /// <summary>
  /// A tuple: (ATN state, predicted alt, syntactic, semantic context).
  ///  The syntactic context is a graph-structured stack node whose
  ///  path(s) to the root is the rule invocation(s)
  ///  chain used to arrive at the state.  The semantic context is
  ///  the tree of semantic predicates encountered before reaching
  ///  an ATN state.
  /// </summary>
  class ATNConfig {
  public:
    typedef std::shared_ptr<ATNConfig> Ref;
    
    /// The ATN state associated with this configuration.
    ATNState * state;

    /// <summary>
    /// What alt (or lexer rule) is predicted by this configuration </summary>
    const int alt;

    /// The stack of invoking states leading to the rule/states associated
    /// with this config.  We track only those contexts pushed during
    /// execution of the ATN simulator.
    ///
    /// Can be shared between multiple ANTConfig instances.
    PredictionContext::Ref context;

    /**
     * We cannot execute predicates dependent upon local context unless
     * we know for sure we are in the correct context. Because there is
     * no way to do this efficiently, we simply cannot evaluate
     * dependent predicates unless we are in the rule that initially
     * invokes the ATN simulator.
     *
     * <p>
     * closure() tracks the depth of how far we dip into the outer context:
     * depth &gt; 0.  Note that it may not be totally accurate depth since I
     * don't ever decrement. TODO: make it a boolean then</p>
     *
     * <p>
     * For memory efficiency, the {@link #isPrecedenceFilterSuppressed} method
     * is also backed by this field. Since the field is publicly accessible, the
     * highest bit which would not cause the value to become negative is used to
     * store this field. This choice minimizes the risk that code which only
     * compares this value to 0 would be affected by the new purpose of the
     * flag. It also ensures the performance of the existing {@link ATNConfig}
     * constructors as well as certain operations like
     * {@link ATNConfigSet#add(ATNConfig, DoubleKeyMap)} method are
     * <em>completely</em> unaffected by the change.</p>
     */
    int reachesIntoOuterContext;

    /// Can be shared between multiple ATNConfig instances.
    SemanticContext::Ref semanticContext;

    ATNConfig(ATNState *state, int alt, PredictionContext::Ref context);
    ATNConfig(ATNState *state, int alt, PredictionContext::Ref context, SemanticContext::Ref semanticContext);

    ATNConfig(ATNConfig::Ref c); // dup
    ATNConfig(ATNConfig::Ref c, ATNState *state);
    ATNConfig(ATNConfig::Ref c, ATNState *state, SemanticContext::Ref semanticContext);
    ATNConfig(ATNConfig::Ref c, SemanticContext::Ref semanticContext);
    ATNConfig(ATNConfig::Ref c, ATNState *state, PredictionContext::Ref context);
    ATNConfig(ATNConfig::Ref c, ATNState *state, PredictionContext::Ref context, SemanticContext::Ref semanticContext);

    virtual size_t hashCode() const;

    struct ATNConfigHasher
    {
      size_t operator()(const ATNConfig &k) const {
        return k.hashCode();
      }
    };

    struct ATNConfigComparer {
      bool operator()(const ATNConfig &lhs, const ATNConfig &rhs) const
      {
        return lhs == rhs;
      }
    };

    /**
     * This method gets the value of the {@link #reachesIntoOuterContext} field
     * as it existed prior to the introduction of the
     * {@link #isPrecedenceFilterSuppressed} method.
     */
    int getOuterContextDepth() const ;
    bool isPrecedenceFilterSuppressed() const;
    void setPrecedenceFilterSuppressed(bool value);

    /// An ATN configuration is equal to another if both have
    /// the same state, they predict the same alternative, and
    /// syntactic/semantic contexts are the same.
    bool operator == (const ATNConfig &other) const;

    virtual std::wstring toString();
    std::wstring toString(bool showAlt);

  private:
    /**
     * This field stores the bit mask for implementing the
     * {@link #isPrecedenceFilterSuppressed} property as a bit within the
     * existing {@link #reachesIntoOuterContext} field.
     */
    static const size_t SUPPRESS_PRECEDENCE_FILTER;
  };

} // namespace atn
} // namespace runtime
} // namespace v4
} // namespace antlr
} // namespace org


// Hash function for ATNConfig.

namespace std {
    using org::antlr::v4::runtime::atn::ATNConfig;

    template <> struct hash<ATNConfig>
    {
        size_t operator() (const ATNConfig &x) const
        {
            return x.hashCode();
        }
    };
}
