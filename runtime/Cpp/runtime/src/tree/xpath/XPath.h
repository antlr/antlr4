/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 *  Copyright (c) 2014 Sam Harwell
 *  Copyright (c) 2013 Terence Parr
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
namespace xpath {

  /// Represent a subset of XPath XML path syntax for use in identifying nodes in
  /// parse trees.
  ///
  /// <para>
  /// Split path into words and separators {@code /} and {@code //} via ANTLR
  /// itself then walk path elements from left to right. At each separator-word
  /// pair, find set of nodes. Next stage uses those as work list.</para>
  ///
  /// <para>
  /// The basic interface is
  /// <seealso cref="XPath#findAll ParseTree.findAll"/>{@code (tree, pathString, parser)}.
  /// But that is just shorthand for:</para>
  ///
  /// <pre>
  /// <seealso cref="XPath"/> p = new <seealso cref="XPath#XPath XPath"/>(parser, pathString);
  /// return p.<seealso cref="#evaluate evaluate"/>(tree);
  /// </pre>
  ///
  /// <para>
  /// See {@code org.antlr.v4.test.TestXPath} for descriptions. In short, this
  /// allows operators:</para>
  ///
  /// <dl>
  /// <dt>/</dt> <dd>root</dd>
  /// <dt>//</dt> <dd>anywhere</dd>
  /// <dt>!</dt> <dd>invert; this must appear directly after root or anywhere
  /// operator</dd>
  /// </dl>
  ///
  /// <para>
  /// and path elements:</para>
  ///
  /// <dl>
  /// <dt>ID</dt> <dd>token name</dd>
  /// <dt>'string'</dt> <dd>any string literal token from the grammar</dd>
  /// <dt>expr</dt> <dd>rule name</dd>
  /// <dt>*</dt> <dd>wildcard matching any node</dd>
  /// </dl>
  ///
  /// <para>
  /// Whitespace is not allowed.</para>

  class ANTLR4CPP_PUBLIC XPath {
  public:
    static const std::string WILDCARD; // word not operator/separator
    static const std::string NOT; // word for invert operator

    XPath(Parser *parser, const std::string &path);
    virtual ~XPath() {}

    // TO_DO: check for invalid token/rule names, bad syntax
    virtual std::vector<XPathElement> split(const std::string &path);

    static std::vector<Ref<ParseTree>> findAll(const Ref<ParseTree> &tree, const std::string &xpath, Parser *parser);

    /// Return a list of all nodes starting at {@code t} as root that satisfy the
    /// path. The root {@code /} is relative to the node passed to
    /// <seealso cref="#evaluate"/>.
    virtual std::vector<Ref<ParseTree>> evaluate(const Ref<ParseTree> &t);

  protected:
    std::string _path;
    std::vector<XPathElement> _elements;
    Parser *_parser;

    /// Convert word like {@code *} or {@code ID} or {@code expr} to a path
    /// element. {@code anywhere} is {@code true} if {@code //} precedes the
    /// word.
    virtual XPathElement getXPathElement(Token *wordToken, bool anywhere);
  };

} // namespace xpath
} // namespace tree
} // namespace antlr4
