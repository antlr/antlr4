#
# Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
# Use of this file is governed by the BSD 3-clause license that
# can be found in the LICENSE.txt file in the project root.
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
    def __init__(self, tokenName, type, label=None):
        super(TokenTagToken, self).__init__(type=type)
        self.tokenName = unicode(tokenName)
        self.label = unicode(label)
        self._text = self.getText()

    #
    # {@inheritDoc}
    #
    # <p>The implementation for {@link TokenTagToken} returns the token tag
    # formatted with {@code <} and {@code >} delimiters.</p>
    #
    def getText(self):
        if self.label is None:
            return u"<" + self.tokenName + u">"
        else:
            return u"<" + self.label + u":" + self.tokenName + u">"

    # <p>The implementation for {@link TokenTagToken} returns a string of the form
    # {@code tokenName:type}.</p>
    #
    def __unicode__(self):
        return self.tokenName + u":" + unicode(self.type)
