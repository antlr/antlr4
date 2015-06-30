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
# A {@link Token} object representing a token of a particular type; e.g.,
# {@code <ID>}. These tokens are created for {@link TagChunk} chunks where the
# tag corresponds to a lexer rule or token type.
#
from antlr4.Token import CommonToken


class TokenTagToken(CommonToken):

    # Constructs a new instance of {@link TokenTagToken} with the specified
    # token name, type, and label.
    #
    # @param tokenName The token name.
    # @param type The token type.
    # @param label The label associated with the token tag, or {@code null} if
    # the token tag is unlabeled.
    #
    def __init__(self, tokenName:str, type:int, label:str=None):
        super().__init__(type=type)
        self.tokenName = tokenName
        self.label = label
        self._text = self.getText()

    #
    # {@inheritDoc}
    #
    # <p>The implementation for {@link TokenTagToken} returns the token tag
    # formatted with {@code <} and {@code >} delimiters.</p>
    #
    def getText(self):
        if self.label is None:
            return "<" + self.tokenName + ">"
        else:
            return "<" + self.label + ":" + self.tokenName + ">"

    # <p>The implementation for {@link TokenTagToken} returns a string of the form
    # {@code tokenName:type}.</p>
    #
    def __str__(self):
        return self.tokenName + ":" + str(self.type)
