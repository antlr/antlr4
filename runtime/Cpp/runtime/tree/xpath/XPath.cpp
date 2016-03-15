#include "XPath.h"
#include "XPathLexerErrorListener.h"
#include "CommonTokenStream.h"
#include "XPathWildcardAnywhereElement.h"
#include "XPathWildcardElement.h"
#include "XPathTokenAnywhereElement.h"
#include "XPathTokenElement.h"
#include "XPathRuleAnywhereElement.h"
#include "XPathRuleElement.h"
#include "ParserRuleContext.h"
#include "Token.h"
#include "Exceptions.h"
#include "Strings.h"
#include "ANTLRInputStream.h"
#include "LexerNoViableAltException.h"
#include "XPathElement.h"
#include "Parser.h"

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

                        const std::wstring XPath::WILDCARD = L"*";
                        const std::wstring XPath::NOT = L"!";

                        XPath::XPath(Parser *parser, const std::wstring &path) {
                            this->parser = parser;
                            this->path = path;
                            elements = split(path);
                                                //		System.out.println(Arrays.toString(elements));
                        }

                        XPathElement *XPath::split(const std::wstring &path) {
                            ANTLRInputStream *in_Renamed;
                            try {
                                in_Renamed = new ANTLRInputStream(new StringReader(path));
                            } catch (IOException ioe) {
                                throw IllegalArgumentException(std::wstring(L"Could not read path: ") + path, ioe);
                            }
                            XPathLexer *lexer = new XPathLexerAnonymousInnerClassHelper(this, in_Renamed);
                            lexer->removeErrorListeners();
                            lexer->addErrorListener(new XPathLexerErrorListener());
                            CommonTokenStream *tokenStream = new CommonTokenStream(lexer);
                            try {
                                tokenStream->fill();
                            } catch (LexerNoViableAltException e) {
                                int pos = lexer->getCharPositionInLine();
                                std::wstring msg = std::wstring(L"Invalid tokens or characters at index ") + std::to_wstring(pos) + std::wstring(L" in path '") + path + std::wstring(L"'");
                                throw IllegalArgumentException(msg, e);
                            }

                            std::vector<Token*> tokens = tokenStream->getTokens();
                                                //		System.out.println("path="+path+"=>"+tokens);
                            std::vector<XPathElement*> elements = std::vector<XPathElement*>();
                            int n = tokens.size();
                            int i = 0;
                            while (i < n) {
                                Token *el = tokens[i];
                                Token *next = nullptr;
                                switch (el->getType()) {
                                    case XPathLexer::ROOT :
                                    case XPathLexer::ANYWHERE :
                                        bool anywhere = el->getType() == XPathLexer::ANYWHERE;
                                        i++;
                                        next = tokens[i];
                                        bool invert = next->getType() == XPathLexer::BANG;
                                        if (invert) {
                                            i++;
                                            next = tokens[i];
                                        }
                                        XPathElement *pathElement = getXPathElement(next, anywhere);
                                        pathElement->invert = invert;
                                        elements.push_back(pathElement);
                                        i++;
                                        break;

                                    case XPathLexer::TOKEN_REF :
                                    case XPathLexer::RULE_REF :
                                    case XPathLexer::WILDCARD :
                                        elements.push_back(getXPathElement(el, false));
                                        i++;
                                        break;

                                    case Token::_EOF :
                                        goto loopBreak;

                                    default :
                                        throw IllegalArgumentException(std::wstring(L"Unknowth path element ") + el);
                                }
                                                    loopContinue:
                            }
                                                loopBreak:
                            return elements.toArray(new XPathElement[0]);
                        }

                        XPath::XPathLexerAnonymousInnerClassHelper::XPathLexerAnonymousInnerClassHelper(XPath *outerInstance, ANTLRInputStream *in_Renamed) : XPathLexer(in_Renamed) {
                            this->outerInstance = outerInstance;
                        }

                        void XPath::XPathLexerAnonymousInnerClassHelper::recover(LexerNoViableAltException *e) {
                            throw e;
                        }

                        org::antlr::v4::runtime::tree::xpath::XPathElement *XPath::getXPathElement(Token *wordToken, bool anywhere) {
                            if (wordToken->getType() == Token::_EOF) {
                                throw IllegalArgumentException(L"Missing path element at end of path");
                            }
                            std::wstring word = wordToken->getText();
                            int ttype = parser->getTokenType(word);
                            int ruleIndex = parser->getRuleIndex(word);
                            switch (wordToken->getType()) {
                                case XPathLexer::WILDCARD :
                                    return anywhere ? new XPathWildcardAnywhereElement() : new XPathWildcardElement();
                                case XPathLexer::TOKEN_REF :
                                case XPathLexer::STRING :
                                    if (ttype == Token::INVALID_TYPE) {
                                        throw IllegalArgumentException(word + std::wstring(L" at index ") + std::to_wstring(wordToken->getStartIndex()) + std::wstring(L" isn't a valid token name"));
                                    }
                                    return anywhere ? new XPathTokenAnywhereElement(word, ttype) : new XPathTokenElement(word, ttype);
                                default :
                                    if (ruleIndex == -1) {
                                        throw IllegalArgumentException(word + std::wstring(L" at index ") + std::to_wstring(wordToken->getStartIndex()) + std::wstring(L" isn't a valid rule name"));
                                    }
                                    return anywhere ? new XPathRuleAnywhereElement(word, ruleIndex) : new XPathRuleElement(word, ruleIndex);
                            }
                        }

                        std::vector<ParseTree*> *XPath::findAll(ParseTree *tree, const std::wstring &xpath, Parser *parser) {
                            XPath *p = new XPath(parser, xpath);
                            return p->evaluate(tree);
                        }

                        set::vector<ParseTree*> *XPath::evaluate(ParseTree *const t) {
                            ParserRuleContext *dummyRoot = new ParserRuleContext();
                            dummyRoot->children = new ArrayListAnonymousInnerClassHelper(this, t); // don't set t's parent.

							set::vector<ParseTree*> *work = std::vector<ParseTree*>();
                            work->add(dummyRoot);

                            int i = 0;
                            while (i < elements->length) {
								set::vector<ParseTree*> *next = std::vector<ParseTree*>();
                                for (auto node : work) {
                                    if (node->getChildCount() > 0) {
                                        // only try to match next element if it has children
                                        // e.g., //func/*/stat might have a token node for which
                                        // we can't go looking for stat nodes.
//JAVA TO C++ CONVERTER TODO TASK: Java wildcard generics are not converted to C++:
//ORIGINAL LINE: java.util.Collection<? extends org.antlr.v4.runtime.tree.ParseTree> matching = elements[i].evaluate(node);
										set::vector<ParseTree *> *matching = elements[i]->evaluate(node);
                                        next->addAll(matching);
                                    }
                                }
                                i++;
                                work = next;
                            }

                            return work;
                        }

                        XPath::ArrayListAnonymousInnerClassHelper::ArrayListAnonymousInnerClassHelper(XPath *outerInstance, ParseTree *t) {
                            this->outerInstance = outerInstance;
                            this->t = t;
                        }
                        // Dan - this code erronously shows up in the header file, TODO which one is correct?
                        XPath::ArrayListAnonymousInnerClassHelper::ArrayListAnonymousInnerClassHelper(XPath *outerInstance, ParseTree *t);
                        {
                            this->add(t);
                        }
                    }
                }
            }
        }
    }
}
#endif