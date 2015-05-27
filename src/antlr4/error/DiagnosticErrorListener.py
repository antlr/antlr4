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


#
# This implementation of {@link ANTLRErrorListener} can be used to identify
# certain potential correctness and performance problems in grammars. "Reports"
# are made by calling {@link Parser#notifyErrorListeners} with the appropriate
# message.
#
# <ul>
# <li><b>Ambiguities</b>: These are cases where more than one path through the
# grammar can match the input.</li>
# <li><b>Weak context sensitivity</b>: These are cases where full-context
# prediction resolved an SLL conflict to a unique alternative which equaled the
# minimum alternative of the SLL conflict.</li>
# <li><b>Strong (forced) context sensitivity</b>: These are cases where the
# full-context prediction resolved an SLL conflict to a unique alternative,
# <em>and</em> the minimum alternative of the SLL conflict was found to not be
# a truly viable alternative. Two-stage parsing cannot be used for inputs where
# this situation occurs.</li>
# </ul>

from io import StringIO
from antlr4 import Parser, DFA
from antlr4.atn.ATNConfigSet import ATNConfigSet
from antlr4.error.ErrorListener import ErrorListener

class DiagnosticErrorListener(ErrorListener):

    def __init__(self, exactOnly:bool=True):
        # whether all ambiguities or only exact ambiguities are reported.
        self.exactOnly = exactOnly

    def reportAmbiguity(self, recognizer:Parser, dfa:DFA, startIndex:int,
                       stopIndex:int, exact:bool, ambigAlts:set, configs:ATNConfigSet):
        if self.exactOnly and not exact:
            return

        with StringIO() as buf:
            buf.write("reportAmbiguity d=")
            buf.write(self.getDecisionDescription(recognizer, dfa))
            buf.write(": ambigAlts=")
            buf.write(str(self.getConflictingAlts(ambigAlts, configs)))
            buf.write(", input='")
            buf.write(recognizer.getTokenStream().getText((startIndex, stopIndex)))
            buf.write("'")
            recognizer.notifyErrorListeners(buf.getvalue())


    def reportAttemptingFullContext(self, recognizer:Parser, dfa:DFA, startIndex:int,
                       stopIndex:int, conflictingAlts:set, configs:ATNConfigSet):
        with StringIO() as buf:
            buf.write("reportAttemptingFullContext d=")
            buf.write(self.getDecisionDescription(recognizer, dfa))
            buf.write(", input='")
            buf.write(recognizer.getTokenStream().getText((startIndex, stopIndex)))
            buf.write("'")
            recognizer.notifyErrorListeners(buf.getvalue())

    def reportContextSensitivity(self, recognizer:Parser, dfa:DFA, startIndex:int,
                       stopIndex:int, prediction:int, configs:ATNConfigSet):
        with StringIO() as buf:
            buf.write("reportContextSensitivity d=")
            buf.write(self.getDecisionDescription(recognizer, dfa))
            buf.write(", input='")
            buf.write(recognizer.getTokenStream().getText((startIndex, stopIndex)))
            buf.write("'")
            recognizer.notifyErrorListeners(buf.getvalue())

    def getDecisionDescription(self, recognizer:Parser, dfa:DFA):
        decision = dfa.decision
        ruleIndex = dfa.atnStartState.ruleIndex

        ruleNames = recognizer.ruleNames
        if ruleIndex < 0 or ruleIndex >= len(ruleNames):
            return str(decision)

        ruleName = ruleNames[ruleIndex]
        if ruleName is None or len(ruleName)==0:
            return str(decision)

        return str(decision) + " (" + ruleName + ")"

    #
    # Computes the set of conflicting or ambiguous alternatives from a
    # configuration set, if that information was not already provided by the
    # parser.
    #
    # @param reportedAlts The set of conflicting or ambiguous alternatives, as
    # reported by the parser.
    # @param configs The conflicting or ambiguous configuration set.
    # @return Returns {@code reportedAlts} if it is not {@code null}, otherwise
    # returns the set of alternatives represented in {@code configs}.
    #
    def getConflictingAlts(self, reportedAlts:set, configs:ATNConfigSet):
        if reportedAlts is not None:
            return reportedAlts

        result = set()
        for config in configs:
            result.add(config.alt)

        return result
