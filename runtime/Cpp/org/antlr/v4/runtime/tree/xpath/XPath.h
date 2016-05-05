#pragma once

#include "XPathElement.h"
#include "Declarations.h"
#include "XPathLexer.h"

#include <string>
#include <vector>
#include "set"

/*
 * [The "BSD license"]
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

// TODO:  Come back to this after the base runtime works.
#if 0

namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {

                namespace tree {
                    namespace xpath {



                        /// <summary>
                        /// Represent a subset of XPath XML path syntax for use in identifying nodes in
                        ///  parse trees.
                        /// 
                        ///  Split path into words and separators / and // via ANTLR itself then walk
                        ///  path elements from left to right.  At each separator-word pair, find set
                        ///  of nodes. Next stage uses those as work list.
                        /// 
                        ///  The basic interface is ParseTree.findAll(parser, pathString). But that is
                        ///  just shorthand for:
                        /// 
                        ///  XPath p = new XPath(parser, xpath);
                        ///  return p.evaluate(this);
                        /// 
                        ///  See {@code org.antlr.v4.test.TestXPath} for descriptions. In short, this allows
                        ///  operators:
                        /// 
                        ///      /         root
                        ///      //        anywhere
                        ///      !         invert; this must appear directly after root or anywhere operator
                        /// 
                        ///  and path elements:
                        /// 
                        ///      ID        token name
                        ///      'string'  any string literal token from the grammar
                        ///      expr      rule name
                        ///      *         wildcard matching any node
                        /// 
                        ///  Whitespace is not allowed.
                        /// </summary>
                        class XPath {
                        public:
                            static const std::wstring WILDCARD; // word not operator/separator
                            static const std::wstring NOT; // word for invert operator

                        protected:
                            std::wstring path;
//JAVA TO C++ CONVERTER WARNING: Since the array size is not known in this declaration, Java to C++ Converter has converted this array to a pointer.  You will need to call 'delete[]' where appropriate:
//ORIGINAL LINE: protected XPathElement[] elements;
                            XPathElement *elements;
                            Parser *parser;

                        public:
                            XPath(Parser *parser, const std::wstring &path);

                            // TODO: check for invalid token/rule names, bad syntax

                            virtual XPathElement *split(const std::wstring &path);

                        private:
                            class XPathLexerAnonymousInnerClassHelper : public XPathLexer {
                            private:
                                XPath *const outerInstance;

                            public:
                                XPathLexerAnonymousInnerClassHelper(XPath *outerInstance, ANTLRInputStream *in_Renamed);

                                virtual void recover(LexerNoViableAltException *e);
                            };

                            /// <summary>
                            /// Convert word like * or ID or expr to a path element. anywhere is true
                            ///  if // precedes the word.
                            /// </summary>
                        protected:
                            virtual XPathElement *getXPathElement(Token *wordToken, bool anywhere);


                        public:
                            static std::vector<ParseTree*> *findAll(ParseTree *tree, const std::wstring &xpath, Parser *parser);

                            /// <summary>
                            /// Return a list of all nodes starting at t as root that satisfy the path.
                            ///  The root / is relative to the node passed to evaluate().
                            /// </summary>
                            virtual std::set<ParseTree*> *evaluate(ParseTree *const t);

                        private:
                            class ArrayListAnonymousInnerClassHelper : public std::vector<ParseTree*> {
                            private:
                                XPath *const outerInstance;

                                ParseTree *t;

                            public:
                                ArrayListAnonymousInnerClassHelper(XPath *outerInstance, ParseTree *t);
                            };
                        };

                    }
                }
            }
        }
    }
}

#endif
