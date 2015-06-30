#
# [The "BSD license"]
#  Copyright (c) 2012 Terence Parr
#  Copyright (c) 2012 Sam Harwell
#  Copyright (c) 2014 Eric Vergnaud
#  All rights reserved.
#
#  Redistribution and use in source and binary forms, with or without
#  modification, are permitted provided that the following conditions
#  are met:
#
#  1. Redistributions of source code must retain the above copyright
#     notice, this list of conditions and the following disclaimer.
#  2. Redistributions in binary form must reproduce the above copyright
#     notice, this list of conditions and the following disclaimer in the
#     documentation and/or other materials provided with the distribution.
#  3. The name of the author may not be used to endorse or promote products
#     derived from this software without specific prior written permission.
#
#  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
#  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
#  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
#  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
#  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
#  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
#  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
#  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
#  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
#  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#
from __builtin__ import unicode
from antlr4.Token import Token
from antlr4.error.ErrorListener import ProxyErrorListener, ConsoleErrorListener

class Recognizer(object):

    tokenTypeMapCache = dict()
    ruleIndexMapCache = dict()

    def __init__(self):
        self._listeners = [ ConsoleErrorListener.INSTANCE ]
        self._interp = None
        self._stateNumber = -1

    def extractVersion(self, version):
        pos = version.find(".")
        major = version[0:pos]
        version = version[pos+1:]
        pos = version.find(".")
        if pos==-1:
            pos = version.find("-")
        if pos==-1:
            pos = len(version)
        minor = version[0:pos]
        return major, minor

    def checkVersion(self, toolVersion):
        runtimeVersion = "4.5.2"
        rvmajor, rvminor = self.extractVersion(runtimeVersion)
        tvmajor, tvminor = self.extractVersion(toolVersion)
        if rvmajor!=tvmajor or rvminor!=tvminor:
            print("ANTLR runtime and generated code versions disagree: "+runtimeVersion+"!="+toolVersion)

    def addErrorListener(self, listener):
        self._listeners.append(listener)

    def removeErrorListener(self, listener):
        self._listeners.remove(listener)
        
    def removeErrorListeners(self):
        self._listeners = []
    
    def getTokenTypeMap(self):
        tokenNames = self.getTokenNames()
        if tokenNames is None:
            from antlr4.error.Errors import UnsupportedOperationException
            raise UnsupportedOperationException("The current recognizer does not provide a list of token names.")
        result = self.tokenTypeMapCache.get(tokenNames, None)
        if result is None:
            result = zip( tokenNames, range(0, len(tokenNames)))
            result["EOF"] = Token.EOF
            self.tokenTypeMapCache[tokenNames] = result
        return result

    # Get a map from rule names to rule indexes.
    #
    # <p>Used for XPath and tree pattern compilation.</p>
    #
    def getRuleIndexMap(self):
        ruleNames = self.getRuleNames()
        if ruleNames is None:
            from antlr4.error.Errors import UnsupportedOperationException
            raise UnsupportedOperationException("The current recognizer does not provide a list of rule names.")
        result = self.ruleIndexMapCache.get(ruleNames, None)
        if result is None:
            result = zip( ruleNames, range(0, len(ruleNames)))
            self.ruleIndexMapCache[ruleNames] = result
        return result

    def getTokenType(self, tokenName):
        ttype = self.getTokenTypeMap().get(tokenName, None)
        if ttype is not None:
            return ttype
        else:
            return Token.INVALID_TYPE


    # What is the error header, normally line/character position information?#
    def getErrorHeader(self, e):
        line = e.getOffendingToken().line
        column = e.getOffendingToken().column
        return u"line " + unicode(line) + u":" + unicode(column)


    # How should a token be displayed in an error message? The default
    #  is to display just the text, but during development you might
    #  want to have a lot of information spit out.  Override in that case
    #  to use t.toString() (which, for CommonToken, dumps everything about
    #  the token). This is better than forcing you to override a method in
    #  your token objects because you don't have to go modify your lexer
    #  so that it creates a new Java type.
    #
    # @deprecated This method is not called by the ANTLR 4 Runtime. Specific
    # implementations of {@link ANTLRErrorStrategy} may provide a similar
    # feature when necessary. For example, see
    # {@link DefaultErrorStrategy#getTokenErrorDisplay}.
    #
    def getTokenErrorDisplay(self, t):
        if t is None:
            return u"<no token>"
        s = t.text
        if s is None:
            if t.type==Token.EOF:
                s = u"<EOF>"
            else:
                s = u"<" + unicode(t.type) + u">"
        s = s.replace(u"\n",u"\\n")
        s = s.replace(u"\r",u"\\r")
        s = s.replace(u"\t",u"\\t")
        return u"'" + s + u"'"

    def getErrorListenerDispatch(self):
        return ProxyErrorListener(self._listeners)

    # subclass needs to override these if there are sempreds or actions
    # that the ATN interp needs to execute
    def sempred(self, localctx, ruleIndex, actionIndex):
        return True

    def precpred(self, localctx , precedence):
        return True

    @property
    def state(self):
        return self._stateNumber

    # Indicate that the recognizer has changed internal state that is
    #  consistent with the ATN state passed in.  This way we always know
    #  where we are in the ATN as the parser goes along. The rule
    #  context objects form a stack that lets us see the stack of
    #  invoking rules. Combine this and we have complete ATN
    #  configuration information.

    @state.setter
    def state(self, atnState):
        self._stateNumber = atnState
