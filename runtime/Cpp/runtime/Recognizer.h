#pragma once

#include <string>
#include <vector>
#include <map>
#include <list>
#include <mutex>
#include <algorithm> 

#include "TokenFactory.h"
#include "ATNSimulator.h"
#include "Declarations.h"
#include "ConsoleErrorListener.h"
#include "Token.h"
#include "StringBuilder.h"
#include "ProxyErrorListener.h"
#include "Strings.h"
#include "CPPUtils.h"
#include "RecognitionException.h"
#include "IRecognizer.h"

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

namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {
                template<typename Symbol, typename ATNInterpreter>
                class Recognizer : public IRecognizer<Symbol, ATNInterpreter> {
                public:
                  Recognizer();
                public:
                    static const int _EOF = -1;

                private:
                    static std::map<std::vector<std::wstring>, std::map<std::wstring, int>*> _tokenTypeMapCache;
                    static std::map<std::vector<std::wstring>, std::map<std::wstring, int>*> _ruleIndexMapCache;

                    std::vector<ANTLRErrorListener*> _listeners;
                    //Mutex to manage synchronized access for multithreading
                    std::mutex mtx;
                    
#ifdef TODO 
                    // Damn code isn't even being used, why is it here?
                private:
                    class CopyOnWriteArrayListAnonymousInnerClassHelper : public std::vector<ANTLRErrorListener*> {
                    public:
                        CopyOnWriteArrayListAnonymousInnerClassHelper();
                    };
#endif

                protected:
                  ATNInterpreter _interpreter; // Set and deleted in descentants.

                private:
                    int _stateNumber;

                    /// <summary>
                    /// Used to print out token names like ID during debugging and
                    ///  error reporting.  The generated parsers implement a method
                    ///  that overrides this to point to their generated token names list.
                    /// </summary>
                public:
                    virtual const std::vector<std::wstring>& getTokenNames() const = 0;

                    virtual const std::vector<std::wstring>& getRuleNames() const = 0;

                    /// <summary>
                    /// Get a map from token names to token types.
                    /// <p/>
                    /// Used for XPath and tree pattern compilation.
                    /// </summary>
                    virtual std::map<std::wstring, int> *getTokenTypeMap();

                    /// <summary>
                    /// Get a map from rule names to rule indexes.
                    /// <p/>
                    /// Used for XPath and tree pattern compilation.
                    /// </summary>
                    virtual std::map<std::wstring, int> *getRuleIndexMap();

                    virtual int getTokenType(const std::wstring &tokenName);

                    /// <summary>
                    /// If this recognizer was generated, it will have a serialized ATN
                    /// representation of the grammar.
                    /// <p/>
                    /// For interpreters, we don't know their serialized ATN despite having
                    /// created the interpreter from it.
                    /// </summary>
                    virtual std::wstring getSerializedATN() {
                        throw L"there is no serialized ATN";
                    }

                    /// <summary>
                    /// For debugging and other purposes, might want the grammar name.
                    ///  Have ANTLR generate an implementation for this method.
                    /// </summary>
                    virtual std::wstring getGrammarFileName() const = 0;

                    /// <summary>
                    /// Get the <seealso cref="ATN"/> used by the recognizer for prediction.
                    /// </summary>
                    /// <returns> The <seealso cref="ATN"/> used by the recognizer for prediction. </returns>
                    virtual atn::ATN *getATN() const = 0;

                    /// <summary>
                    /// Get the ATN interpreter used by the recognizer for prediction.
                    /// </summary>
                    /// <returns> The ATN interpreter used by the recognizer for prediction. </returns>
                    virtual ATNInterpreter getInterpreter() const {
                        return _interpreter;
                    }

                  // TODO: probably obsolete function. _interpreter is a protected member directly managed by descendants.
                    /// <summary>
                    /// Set the ATN interpreter used by the recognizer for prediction.
                    /// </summary>
                    /// <param name="interpreter"> The ATN interpreter used by the recognizer for
                    /// prediction. </param>
                    virtual void setInterpreter(ATNInterpreter interpreter) {
                        _interpreter = interpreter;
                    }

                    /// <summary>
                    /// What is the error header, normally line/character position information? </summary>
                    virtual std::wstring getErrorHeader(RecognitionException *e);

                    /// <summary>
                    /// How should a token be displayed in an error message? The default
                    ///  is to display just the text, but during development you might
                    ///  want to have a lot of information spit out.  Override in that case
                    ///  to use t.toString() (which, for CommonToken, dumps everything about
                    ///  the token). This is better than forcing you to override a method in
                    ///  your token objects because you don't have to go modify your lexer
                    ///  so that it creates a new Java type.
                    /// </summary>
                    virtual std::wstring getTokenErrorDisplay(Token *t);

                    /// <exception cref="NullPointerException"> if {@code listener} is {@code null}. </exception>
                    virtual void addErrorListener(ANTLRErrorListener *listener);

                    virtual void removeErrorListener(ANTLRErrorListener *listener);

                    virtual void removeErrorListeners();

                    virtual std::vector<ANTLRErrorListener *> *getErrorListeners() {
                        return &_listeners;
                    }

                    virtual ANTLRErrorListener *getErrorListenerDispatch();

                    // subclass needs to override these if there are sempreds or actions
                    // that the ATN interp needs to execute
                    virtual bool sempred(RuleContext *_localctx, int ruleIndex, int actionIndex);

                    virtual bool precpred(RuleContext *localctx, int precedence);

                    virtual void action(RuleContext *_localctx, int ruleIndex, int actionIndex);

                    int getState();

                    /// <summary>
                    /// Indicate that the recognizer has changed internal state that is
                    ///  consistent with the ATN state passed in.  This way we always know
                    ///  where we are in the ATN as the parser goes along. The rule
                    ///  context objects form a stack that lets us see the stack of
                    ///  invoking rules. Combine this and we have complete ATN
                    ///  configuration information.
                    /// </summary>
                    void setState(int atnState);

                    virtual IntStream *getInputStream() = 0;

                    virtual void setInputStream(IntStream *input) = 0;

                    virtual TokenFactory<CommonToken *> *getTokenFactory() = 0;

                    template<typename T1>
                    void setTokenFactory(TokenFactory<T1> *input);

                private:
                    void InitializeInstanceFields();

                };

            }
        }
    }
}

#include "Recognizer.inl"


