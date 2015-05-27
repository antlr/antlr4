# [The "BSD license"]
#  Copyright (c) 2013 Terence Parr
#  Copyright (c) 2013 Sam Harwell
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
#/


#  A rule context is a record of a single rule invocation. It knows
#  which context invoked it, if any. If there is no parent context, then
#  naturally the invoking state is not valid.  The parent link
#  provides a chain upwards from the current rule invocation to the root
#  of the invocation tree, forming a stack. We actually carry no
#  information about the rule associated with this context (except
#  when parsing). We keep only the state number of the invoking state from
#  the ATN submachine that invoked this. Contrast this with the s
#  pointer inside ParserRuleContext that tracks the current state
#  being "executed" for the current rule.
#
#  The parent contexts are useful for computing lookahead sets and
#  getting error information.
#
#  These objects are used during parsing and prediction.
#  For the special case of parsers, we use the subclass
#  ParserRuleContext.
#
#  @see ParserRuleContext
#/
from io import StringIO
from antlr4.tree.Tree import RuleNode, INVALID_INTERVAL, ParseTreeVisitor
from antlr4.tree.Trees import Trees

# need forward declarations
RuleContext = None
Parser = None

class RuleContext(RuleNode):

    EMPTY = None

    def __init__(self, parent:RuleContext=None, invokingState:int=-1):
        super().__init__()
        # What context invoked this rule?
        self.parentCtx = parent
        # What state invoked the rule associated with this context?
        #  The "return address" is the followState of invokingState
        #  If parent is null, this should be -1.
        self.invokingState = invokingState


    def depth(self):
        n = 0
        p = self
        while p is not None:
            p = p.parentCtx
            n += 1
        return n

    # A context is empty if there is no invoking state; meaning nobody call
    #  current context.
    def isEmpty(self):
        return self.invokingState == -1

    # satisfy the ParseTree / SyntaxTree interface

    def getSourceInterval(self):
        return INVALID_INTERVAL

    def getRuleContext(self):
        return self

    def getPayload(self):
        return self

   # Return the combined text of all child nodes. This method only considers
    #  tokens which have been added to the parse tree.
    #  <p>
    #  Since tokens on hidden channels (e.g. whitespace or comments) are not
    #  added to the parse trees, they will not appear in the output of this
    #  method.
    #/
    def getText(self):
        if self.getChildCount() == 0:
            return ""
        with StringIO() as builder:
            for child in self.getChildren():
                builder.write(child.getText())
            return builder.getvalue()

    def getRuleIndex(self):
        return -1

    def getChild(self, i:int):
        return None

    def getChildCount(self):
        return 0

    def getChildren(self):
        for c in []:
            yield c

    def accept(self, visitor:ParseTreeVisitor):
        return visitor.visitChildren(self)

   # # Call this method to view a parse tree in a dialog box visually.#/
   #  public Future<JDialog> inspect(@Nullable Parser parser) {
   #      List<String> ruleNames = parser != null ? Arrays.asList(parser.getRuleNames()) : null;
   #      return inspect(ruleNames);
   #  }
   #
   #  public Future<JDialog> inspect(@Nullable List<String> ruleNames) {
   #      TreeViewer viewer = new TreeViewer(ruleNames, this);
   #      return viewer.open();
   #  }
   #
   # # Save this tree in a postscript file#/
   #  public void save(@Nullable Parser parser, String fileName)
   #      throws IOException, PrintException
   #  {
   #      List<String> ruleNames = parser != null ? Arrays.asList(parser.getRuleNames()) : null;
   #      save(ruleNames, fileName);
   #  }
   #
   # # Save this tree in a postscript file using a particular font name and size#/
   #  public void save(@Nullable Parser parser, String fileName,
   #                   String fontName, int fontSize)
   #      throws IOException
   #  {
   #      List<String> ruleNames = parser != null ? Arrays.asList(parser.getRuleNames()) : null;
   #      save(ruleNames, fileName, fontName, fontSize);
   #  }
   #
   # # Save this tree in a postscript file#/
   #  public void save(@Nullable List<String> ruleNames, String fileName)
   #      throws IOException, PrintException
   #  {
   #      Trees.writePS(this, ruleNames, fileName);
   #  }
   #
   # # Save this tree in a postscript file using a particular font name and size#/
   #  public void save(@Nullable List<String> ruleNames, String fileName,
   #                   String fontName, int fontSize)
   #      throws IOException
   #  {
   #      Trees.writePS(this, ruleNames, fileName, fontName, fontSize);
   #  }
   #
   # # Print out a whole tree, not just a node, in LISP format
   #  #  (root child1 .. childN). Print just a node if this is a leaf.
   #  #  We have to know the recognizer so we can get rule names.
   #  #/
   #  @Override
   #  public String toStringTree(@Nullable Parser recog) {
   #      return Trees.toStringTree(this, recog);
   #  }
   #
   # Print out a whole tree, not just a node, in LISP format
   #  (root child1 .. childN). Print just a node if this is a leaf.
   #
    def toStringTree(self, ruleNames:list=None, recog:Parser=None):
        return Trees.toStringTree(self, ruleNames=ruleNames, recog=recog)
   #  }
   #
   #  @Override
   #  public String toStringTree() {
   #      return toStringTree((List<String>)null);
   #  }
   #
    def __str__(self):
        return self.toString(None, None)

   #  @Override
   #  public String toString() {
   #      return toString((List<String>)null, (RuleContext)null);
   #  }
   #
   #  public final String toString(@Nullable Recognizer<?,?> recog) {
   #      return toString(recog, ParserRuleContext.EMPTY);
   #  }
   #
   #  public final String toString(@Nullable List<String> ruleNames) {
   #      return toString(ruleNames, null);
   #  }
   #
   #  // recog null unless ParserRuleContext, in which case we use subclass toString(...)
   #  public String toString(@Nullable Recognizer<?,?> recog, @Nullable RuleContext stop) {
   #      String[] ruleNames = recog != null ? recog.getRuleNames() : null;
   #      List<String> ruleNamesList = ruleNames != null ? Arrays.asList(ruleNames) : null;
   #      return toString(ruleNamesList, stop);
   #  }

    def toString(self, ruleNames:list, stop:RuleContext)->str:
        with StringIO() as buf:
            p = self
            buf.write("[")
            while p is not None and p is not stop:
                if ruleNames is None:
                    if not p.isEmpty():
                        buf.write(str(p.invokingState))
                else:
                    ri = p.getRuleIndex()
                    ruleName = ruleNames[ri] if ri >= 0 and ri < len(ruleNames) else str(ri)
                    buf.write(ruleName)

                if p.parentCtx is not None and (ruleNames is not None or not p.parentCtx.isEmpty()):
                    buf.write(" ")

                p = p.parentCtx

            buf.write("]")
            return buf.getvalue()

