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
# A pattern like {@code <ID> = <expr>;} converted to a {@link ParseTree} by
# {@link ParseTreePatternMatcher#compile(String, int)}.
#
from antlr4.xpath.XPath import XPath


class ParseTreePattern(object):

    # Construct a new instance of the {@link ParseTreePattern} class.
    #
    # @param matcher The {@link ParseTreePatternMatcher} which created this
    # tree pattern.
    # @param pattern The tree pattern in concrete syntax form.
    # @param patternRuleIndex The parser rule which serves as the root of the
    # tree pattern.
    # @param patternTree The tree pattern in {@link ParseTree} form.
    #
    def __init__(self, matcher, pattern, patternRuleIndex , patternTree):
        self.matcher = matcher
        self.patternRuleIndex = patternRuleIndex
        self.pattern = pattern
        self.patternTree = patternTree

    #
    # Match a specific parse tree against this tree pattern.
    #
    # @param tree The parse tree to match against this tree pattern.
    # @return A {@link ParseTreeMatch} object describing the result of the
    # match operation. The {@link ParseTreeMatch#succeeded()} method can be
    # used to determine whether or not the match was successful.
    #
    def match(self, tree):
        return self.matcher.match(tree, self)

    #
    # Determine whether or not a parse tree matches this tree pattern.
    #
    # @param tree The parse tree to match against this tree pattern.
    # @return {@code true} if {@code tree} is a match for the current tree
    # pattern; otherwise, {@code false}.
    #
    def matches(self, tree):
        return self.matcher.match(tree, self).succeeded()

    # Find all nodes using XPath and then try to match those subtrees against
    # this tree pattern.
    #
    # @param tree The {@link ParseTree} to match against this pattern.
    # @param xpath An expression matching the nodes
    #
    # @return A collection of {@link ParseTreeMatch} objects describing the
    # successful matches. Unsuccessful matches are omitted from the result,
    # regardless of the reason for the failure.
    #
    def findAll(self, tree, xpath):
        subtrees = XPath.findAll(tree, xpath, self.matcher.parser)
        matches = list()
        for t in subtrees:
            match = self.match(t)
            if match.succeeded():
                matches.append(match)
        return matches
