#
# [The "BSD license"]
# Copyright (c) 2013 Terence Parr
# Copyright (c) 2013 Sam Harwell
# Copyright (c) 2014 Eric Vergnaud
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions
# are met:
#
# 1. Redistributions of source code must retain the above copyright
#    notice, this list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright
#    notice, this list of conditions and the following disclaimer in the
#    documentation and/or other materials provided with the distribution.
# 3. The name of the author may not be used to endorse or promote products
#    derived from this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
# IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
# OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
# IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
# INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
# NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
# DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
# THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
# (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
# THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#

#
# A {@link Token} object representing an entire subtree matched by a parser
# rule; e.g., {@code <expr>}. These tokens are created for {@link TagChunk}
# chunks where the tag corresponds to a parser rule.
#
from antlr4.Token import Token


class RuleTagToken(Token):
    #
    # Constructs a new instance of {@link RuleTagToken} with the specified rule
    # name, bypass token type, and label.
    #
    # @param ruleName The name of the parser rule this rule tag matches.
    # @param bypassTokenType The bypass token type assigned to the parser rule.
    # @param label The label associated with the rule tag, or {@code null} if
    # the rule tag is unlabeled.
    #
    # @exception IllegalArgumentException if {@code ruleName} is {@code null}
    # or empty.

    def __init__(self, ruleName, bypassTokenType, label=None):
        if ruleName is None or len(ruleName)==0:
            raise Exception("ruleName cannot be null or empty.")
        self.source = None
        self.type = bypassTokenType # token type of the token
        self.channel = Token.DEFAULT_CHANNEL # The parser ignores everything not on DEFAULT_CHANNEL
        self.start = -1 # optional; return -1 if not implemented.
        self.stop = -1  # optional; return -1 if not implemented.
        self.tokenIndex = -1 # from 0..n-1 of the token object in the input stream
        self.line = 0 # line=1..n of the 1st character
        self.column = -1 # beginning of the line at which it occurs, 0..n-1
        self.label = label
        self._text = self.getText() # text of the token.

        self.ruleName = ruleName


    def getText(self):
        if self.label is None:
            return "<" + self.ruleName + ">"
        else:
            return "<" + self.label + ":" + self.ruleName + ">"
