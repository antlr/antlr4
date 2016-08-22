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

#include "antlr4-common.h"

namespace antlr4 {
namespace tree {

  /// The basic notion of a tree has a parent, a payload, and a list of children.
  /// It is the most abstract interface for all the trees used by ANTLR.
  // ml: deviating from Java here. This class forms a tree? Then it should also manage parent + children.
  class ANTLR4CPP_PUBLIC Tree {
  public:
    virtual ~Tree() {};
    
    /// The parent of this node. If the return value is null, then this
    /// node is the root of the tree.
    std::weak_ptr<Tree> parent;

    /// If we are debugging or building a parse tree for a visitor,
    ///  we need to track all of the tokens and rule invocations associated
    ///  with this rule's context. This is empty for parsing w/o tree constr.
    ///  operation because we don't the need to track the details about
    ///  how we parse this rule.
    std::vector<Ref<Tree>> children;

    /// Print out a whole tree, not just a node, in LISP format
    /// {@code (root child1 .. childN)}. Print just a node if this is a leaf.
    virtual std::string toStringTree() = 0;
    virtual std::string toString() = 0;

    virtual bool operator == (const Tree &other) const;
  };

} // namespace tree
} // namespace antlr4
