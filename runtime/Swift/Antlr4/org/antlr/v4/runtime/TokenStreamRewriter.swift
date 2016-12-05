/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


/**
* Useful for rewriting out a buffered input token stream after doing some
* augmentation or other manipulations on it.
*
* <p>
* You can insert stuff, replace, and delete chunks. Note that the operations
* are done lazily--only if you convert the buffer to a {@link String} with
* {@link org.antlr.v4.runtime.TokenStream#getText()}. This is very efficient because you are not
* moving data around all the time. As the buffer of tokens is converted to
* strings, the {@link #getText()} method(s) scan the input token stream and
* check to see if there is an operation at the current index. If so, the
* operation is done and then normal {@link String} rendering continues on the
* buffer. This is like having multiple Turing machine instruction streams
* (programs) operating on a single input tape. :)</p>
*
* <p>
* This rewriter makes no modifications to the token stream. It does not ask the
* stream to fill itself up nor does it advance the input cursor. The token
* stream {@link org.antlr.v4.runtime.TokenStream#index()} will return the same value before and
* after any {@link #getText()} call.</p>
*
* <p>
* The rewriter only works on tokens that you have in the buffer and ignores the
* current input cursor. If you are buffering tokens on-demand, calling
* {@link #getText()} halfway through the input will only do rewrites for those
* tokens in the first half of the file.</p>
*
* <p>
* Since the operations are done lazily at {@link #getText}-time, operations do
* not screw up the token index values. That is, an insert operation at token
* index {@code i} does not change the index values for tokens
* {@code i}+1..n-1.</p>
*
* <p>
* Because operations never actually alter the buffer, you may always get the
* original token stream back without undoing anything. Since the instructions
* are queued up, you can easily simulate transactions and roll back any changes
* if there is an error just by removing instructions. For example,</p>
*
* <pre>
* CharStream input = new ANTLRFileStream("input");
* TLexer lex = new TLexer(input);
* CommonTokenStream tokens = new CommonTokenStream(lex);
* T parser = new T(tokens);
* TokenStreamRewriter rewriter = new TokenStreamRewriter(tokens);
* parser.startRule();
* </pre>
*
* <p>
* Then in the rules, you can execute (assuming rewriter is visible):</p>
*
* <pre>
* Token t,u;
* ...
* rewriter.insertAfter(t, "text to put after t");}
* rewriter.insertAfter(u, "text after u");}
* System.out.println(rewriter.getText());
* </pre>
*
* <p>
* You can also have multiple "instruction streams" and get multiple rewrites
* from a single pass over the input. Just name the instruction streams and use
* that name again when printing the buffer. This could be useful for generating
* a C file and also its header file--all from the same buffer:</p>
*
* <pre>
* rewriter.insertAfter("pass1", t, "text to put after t");}
* rewriter.insertAfter("pass2", u, "text after u");}
* System.out.println(rewriter.getText("pass1"));
* System.out.println(rewriter.getText("pass2"));
* </pre>
*
* <p>
* If you don't use named rewrite streams, a "default" stream is used as the
* first example shows.</p>
*/

import Foundation

public class TokenStreamRewriter {
    public let DEFAULT_PROGRAM_NAME: String = "default"
    public static let PROGRAM_INIT_SIZE: Int = 100
    public static let MIN_TOKEN_INDEX: Int = 0

    // Define the rewrite operation hierarchy
    public class RewriteOperation: CustomStringConvertible {
        /** What index into rewrites List are we? */
        internal var instructionIndex: Int = 0
        /** Token buffer index. */
        internal var index: Int
        internal var text: String?
        weak var  tokens: TokenStream!
        internal var lastIndex: Int = 0
        init(_ index: Int, _ tokens: TokenStream) {
            self.index = index
            self.tokens = tokens
        }
        //_ tokens : TokenStream  ,_ tokens : TokenStream
        init(_ index: Int, _ text: String?, _ tokens: TokenStream) {
            self.index = index
            self.text = text
            self.tokens = tokens
        }
        /** Execute the rewrite operation by possibly adding to the buffer.
         *  Return the index of the next token to operate on.
         */
        public func execute(_ buf: StringBuilder) throws -> Int {
            return index
        }

        public var description: String {
            let opName: String = NSStringFromClass(RewriteOperation.self)
            //  var index : Int = opName.indexOf("$");
            //  opName =    opName.substring(   index+1);
            return "<\(opName) @ \(try? tokens.get(index)):\\\(text)\">"
        }

    }

    public final class InsertBeforeOp: RewriteOperation {
        public override init(_ index: Int, _ text: String?, _ tokens: TokenStream) {
            super.init(index, text, tokens)
        }

        override
        public func execute(_ buf: StringBuilder) throws -> Int {
            if text != nil {
                buf.append(text!)
            }
            if try tokens.get(index).getType() != CommonToken.EOF {
                buf.append(try tokens.get(index).getText()!)
            }
            return index + 1
        }
    }

    /** I'm going to try replacing range from x..y with (y-x)+1 ReplaceOp
     *  instructions.
     */

    public final class ReplaceOp: RewriteOperation {

        public init(_ from: Int, _ to: Int, _ text: String?, _ tokens: TokenStream) {
            super.init(from, text, tokens)
            lastIndex = to
        }

        override
        public func execute(_ buf: StringBuilder) -> Int {
            if text != nil {
                buf.append(text!)
            }
            return lastIndex + 1
        }
        override
        public var description: String {
            if text == nil {
                return "<DeleteOp@\(try? tokens.get(index))..\(try? tokens.get(lastIndex))>"
            }
            return "<ReplaceOp@\(try? tokens.get(index))..\(try? tokens.get(lastIndex)):\\\(text)>"
        }

    }

    public class RewriteOperationArray{
        private final var rewrites: Array<RewriteOperation?> = Array<RewriteOperation?>()
        public init(){
            rewrites.reserveCapacity(TokenStreamRewriter.PROGRAM_INIT_SIZE)
        }
        final func append(_ op: RewriteOperation){
            op.instructionIndex = rewrites.count
            rewrites.append(op)
        }

        final func rollback(_ instructionIndex: Int) {
            rewrites = Array(rewrites[TokenStreamRewriter.MIN_TOKEN_INDEX ..< instructionIndex])
        }
        final var count: Int{
            return rewrites.count
        }
        final var isEmpty: Bool{
            return rewrites.isEmpty
        }
        /** We need to combine operations and report invalid operations (like
         *  overlapping replaces that are not completed nested). Inserts to
         *  same index need to be combined etc...  Here are the cases:
         *
         *  I.i.u I.j.v                             leave alone, nonoverlapping
         *  I.i.u I.i.v                             combine: Iivu
         *
         *  R.i-j.u R.x-y.v | i-j in x-y            delete first R
         *  R.i-j.u R.i-j.v                         delete first R
         *  R.i-j.u R.x-y.v | x-y in i-j            ERROR
         *  R.i-j.u R.x-y.v | boundaries overlap    ERROR
         *
         *  Delete special case of replace (text==null):
         *  D.i-j.u D.x-y.v | boundaries overlap    combine to max(min)..max(right)
         *
         *  I.i.u R.x-y.v | i in (x+1)-y            delete I (since insert before
         *                                          we're not deleting i)
         *  I.i.u R.x-y.v | i not in (x+1)-y        leave alone, nonoverlapping
         *  R.x-y.v I.i.u | i in x-y                ERROR
         *  R.x-y.v I.x.u                           R.x-y.uv (combine, delete I)
         *  R.x-y.v I.i.u | i not in x-y            leave alone, nonoverlapping
         *
         *  I.i.u = insert u before op @ index i
         *  R.x-y.u = replace x-y indexed tokens with u
         *
         *  First we need to examine replaces. For any replace op:
         *
         *      1. wipe out any insertions before op within that range.
         *      2. Drop any replace op before that is contained completely within
         *   that range.
         *      3. Throw exception upon boundary overlap with any previous replace.
         *
         *  Then we can deal with inserts:
         *
         *      1. for any inserts to same index, combine even if not adjacent.
         *      2. for any prior replace with same left boundary, combine this
         *   insert with replace and delete this replace.
         *      3. throw exception if index in same range as previous replace
         *
         *  Don't actually delete; make op null in list. Easier to walk list.
         *  Later we can throw as we add to index &rarr; op map.
         *
         *  Note that I.2 R.2-2 will wipe out I.2 even though, technically, the
         *  inserted stuff would be before the replace range. But, if you
         *  add tokens in front of a method body '{' and then delete the method
         *  body, I think the stuff before the '{' you added should disappear too.
         *
         *  Return a map from token index to operation.
         */
        final func reduceToSingleOperationPerIndex() throws -> Dictionary<Int, RewriteOperation> {

            let rewritesCount = rewrites.count
            // WALK REPLACES
            for i in 0..<rewritesCount {
                guard let rewritesI = rewrites[i]  else {
                    continue
                }
                if !(rewritesI is ReplaceOp) {
                    continue
                }

                // Wipe prior inserts within range
                let iopIndexList =  getKindOfOps(&rewrites, InsertBeforeOp.self, i)
                for j in iopIndexList  {
                    if let rewritesJ = rewrites[j] {
                        if rewritesJ.index == rewritesI.index {
                            // E.g., insert before 2, delete 2..2; update replace
                            // text to include insert before, kill insert
                            rewrites[rewritesJ.instructionIndex] = nil

                            rewritesI.text = catOpText(rewritesJ.text, rewritesI.text)

                        } else if rewritesJ.index > rewritesI.index &&
                                 rewritesJ.index <= rewritesI.lastIndex {
                            // delete insert as it's a no-op.
                            rewrites[rewritesJ.instructionIndex] = nil
                            //print("set nil j:\(j)")

                        }
                    }
                }
                // Drop any prior replaces contained within
                let prevRopIndexList =  getKindOfOps(&rewrites, ReplaceOp.self, i)
                for j in prevRopIndexList  {
                    if let rewritesJ = rewrites[j] {
                        if rewritesJ.index >= rewritesI.index && rewritesJ.lastIndex <= rewritesI.lastIndex {
                            // delete replace as it's a no-op.
                            rewrites[rewritesJ.instructionIndex] = nil
                            continue
                        }
                        // throw exception unless disjoint or identical
                        let disjoint: Bool =
                            rewritesJ.lastIndex < rewritesI.index || rewritesJ.index > rewritesI.lastIndex
                        let same: Bool =
                            rewritesJ.index == rewritesI.index && rewritesJ.lastIndex == rewritesI.lastIndex
                        // Delete special case of replace (text==null):
                        // D.i-j.u D.x-y.v  | boundaries overlap    combine to max(min)..max(right)
                        if rewritesJ.text == nil && rewritesI.text == nil && !disjoint {
                            //System.out.println("overlapping deletes: "+rewritesJ+", "+rop);
                            rewrites[rewritesJ.instructionIndex] = nil // kill first delete
                            rewritesI.index = min(rewritesJ.index, rewritesI.index)
                            rewritesI.lastIndex = max(rewritesJ.lastIndex, rewritesI.lastIndex)
                            //print("new rop " + rewritesI.description)
                        } else if !disjoint && !same {
                            throw ANTLRError.illegalArgument(msg: "replace op boundaries of \(rewritesI.description) overlap with previous \(rewritesJ.description)")

                        }
                    }
                }
            }

            // WALK INSERTS
            for i in 0..<rewritesCount {
                guard let rewritesI = rewrites[i]  else {
                    continue
                }
                if !(rewritesI is InsertBeforeOp) {
                    continue
                }


                // combine current insert with prior if any at same index
                let prevIopIndexList = getKindOfOps(&rewrites, InsertBeforeOp.self, i)
                for j in prevIopIndexList {
                    if let rewritesJ = rewrites[j] {
                        if rewritesJ.index == rewritesI.index {
                            // combine objects
                            // convert to strings...we're in process of toString'ing
                            // whole token buffer so no lazy eval issue with any templates
                            rewritesI.text = catOpText(rewritesI.text, rewrites[j]?.text)
                            // delete redundant prior insert
                            rewrites[rewritesJ.instructionIndex] = nil
                        }
                    }
                }

                // look for replaces where iop.index is in range; error
                let ropIndexList = getKindOfOps(&rewrites, ReplaceOp.self, i)
                for j in ropIndexList  {
                    if let rewritesJ = rewrites[j] {
                        if rewritesI.index == rewritesJ.index {
                            rewritesJ.text = catOpText(rewritesI.text, rewritesJ.text)
                            rewrites[i] = nil    // delete current insert
                            continue
                        }
                        if rewritesI.index >= rewritesJ.index && rewritesI.index <= rewritesJ.lastIndex {
                            throw ANTLRError.illegalArgument(msg: "insert op \(rewritesI.description) within boundaries of previous \(rewritesJ.description)")

                        }
                    }
                }

            }

            var m: Dictionary<Int, RewriteOperation> = Dictionary<Int, RewriteOperation>()
            for i in 0..<rewritesCount {
                if let op: RewriteOperation = rewrites[i] {
                    if m[op.index] != nil {
                        throw ANTLRError.illegalArgument(msg: "should only be one op per index")
                    }
                    m[op.index] = op
                }
            }

            return m
        }

        final func catOpText(_ a: String?, _ b: String?) -> String {
            let x: String = a ?? ""
            let y: String = b ?? ""

            return x + y
        }

        /** Get all operations before an index of a particular kind */

        final func getKindOfOps<T: RewriteOperation>(_ rewrites: inout Array<RewriteOperation?>, _ kind: T.Type, _ before: Int ) ->  Array<Int>  {

            let length = min(before,rewrites.count)
            var op = Array<Int>()
            op.reserveCapacity(length)
            for i in 0..<length {
                if rewrites[i] is T {
                    op.append(i)
                }
            }
            return op
        }

    }

    /** Our source stream */
    internal var tokens: TokenStream

    /** You may have multiple, named streams of rewrite operations.
     *  I'm calling these things "programs."
     *  Maps String (name) &rarr; rewrite (List)
     */
    internal var programs: Dictionary<String, RewriteOperationArray> //Array<RewriteOperation>

    /** Map String (program name) &rarr; Integer index */
    internal final var lastRewriteTokenIndexes: Dictionary<String, Int>

    public init(_ tokens: TokenStream) {
        self.tokens = tokens
        programs = Dictionary<String, RewriteOperationArray>()
        programs[DEFAULT_PROGRAM_NAME] = RewriteOperationArray()
        lastRewriteTokenIndexes = Dictionary<String, Int>()
    }

    public final func getTokenStream() -> TokenStream {
        return tokens
    }

    public func rollback(_ instructionIndex: Int) {
        rollback(DEFAULT_PROGRAM_NAME, instructionIndex)
    }

    /** Rollback the instruction stream for a program so that
     *  the indicated instruction (via instructionIndex) is no
     *  longer in the stream. UNTESTED!
     */
    public func rollback(_ programName: String, _ instructionIndex: Int) {
        if let program = programs[programName] {
            program.rollback(instructionIndex)
        }
    }

    public func deleteProgram() {
        deleteProgram(DEFAULT_PROGRAM_NAME)
    }

    /** Reset the program so that no instructions exist */
    public func deleteProgram(_ programName: String) {
        rollback(programName, TokenStreamRewriter.MIN_TOKEN_INDEX)
    }

    public func insertAfter(_ t: Token, _ text: String) {
        insertAfter(DEFAULT_PROGRAM_NAME, t, text)
    }

    public func insertAfter(_ index: Int, _ text: String) {
        insertAfter(DEFAULT_PROGRAM_NAME, index, text)
    }

    public func insertAfter(_ programName: String, _ t: Token, _ text: String) {
        insertAfter(programName, t.getTokenIndex(), text)
    }

    public func insertAfter(_ programName: String, _ index: Int, _ text: String) {
        // to insert after, just insert before next index (even if past end)
        insertBefore(programName, index + 1, text)
    }

    public func insertBefore(_ t: Token, _ text: String) {
        insertBefore(DEFAULT_PROGRAM_NAME, t, text)
    }

    public func insertBefore(_ index: Int, _ text: String) {
        insertBefore(DEFAULT_PROGRAM_NAME, index, text)
    }

    public func insertBefore(_ programName: String, _ t: Token, _ text: String) {
        insertBefore(programName, t.getTokenIndex(), text)
    }

    public func insertBefore(_ programName: String, _ index: Int, _ text: String) {
        let op: RewriteOperation = InsertBeforeOp(index, text, tokens)
        let rewritesArray: RewriteOperationArray = getProgram(programName)
        rewritesArray.append(op)
    }

    public func replace(_ index: Int, _ text: String) throws {
        try replace(DEFAULT_PROGRAM_NAME, index, index, text)
    }

    public func replace(_ from: Int, _ to: Int, _ text: String) throws {
        try replace(DEFAULT_PROGRAM_NAME, from, to, text)
    }

    public func replace(_ indexT: Token, _ text: String) throws {
        try replace(DEFAULT_PROGRAM_NAME, indexT, indexT, text)
    }

    public func replace(_ from: Token, _ to: Token, _ text: String) throws {
        try  replace(DEFAULT_PROGRAM_NAME, from, to, text)
    }

    public func replace(_ programName: String, _ from: Int, _ to: Int, _ text: String?) throws {
        if from > to || from < 0 || to < 0 || to >= tokens.size() {
            throw ANTLRError.illegalArgument(msg: "replace: range invalid: \(from)..\(to)(size=\(tokens.size()))")
        }
        let op: RewriteOperation = ReplaceOp(from, to, text, tokens)
        let rewritesArray: RewriteOperationArray = getProgram(programName)
        rewritesArray.append(op)

    }

    public func replace(_ programName: String, _ from: Token, _ to: Token, _ text: String?) throws {
        try replace(programName,
            from.getTokenIndex(),
            to.getTokenIndex(),
            text)
    }

    public func delete(_ index: Int) throws {
        try delete(DEFAULT_PROGRAM_NAME, index, index)
    }

    public func delete(_ from: Int, _ to: Int) throws {
        try delete(DEFAULT_PROGRAM_NAME, from, to)
    }

    public func delete(_ indexT: Token) throws {
        try delete(DEFAULT_PROGRAM_NAME, indexT, indexT)
    }

    public func delete(_ from: Token, _ to: Token) throws {
        try delete(DEFAULT_PROGRAM_NAME, from, to)
    }

    public func delete(_ programName: String, _ from: Int, _ to: Int) throws {
        try replace(programName, from, to, nil)
    }

    public func delete(_ programName: String, _ from: Token, _ to: Token) throws {
        try replace(programName, from, to, nil)
    }

    public func getLastRewriteTokenIndex() -> Int {
        return getLastRewriteTokenIndex(DEFAULT_PROGRAM_NAME)
    }

    internal func getLastRewriteTokenIndex(_ programName: String) -> Int {
        let I: Int? = lastRewriteTokenIndexes[programName]
        if I == nil {
            return -1
        }
        return I!
    }

    internal func setLastRewriteTokenIndex(_ programName: String, _ i: Int) {
        lastRewriteTokenIndexes[programName] = i
    }

    internal func getProgram(_ name: String) -> RewriteOperationArray
    {
        var program: RewriteOperationArray? = programs[name]
        if program == nil {
            program = initializeProgram(name)
        }
        return program!
    }

    private func initializeProgram(_ name: String) -> RewriteOperationArray
    {
        let program: RewriteOperationArray = RewriteOperationArray()

        programs[name] = program
        return program
    }

    /** Return the text from the original tokens altered per the
     *  instructions given to this rewriter.
     */
    public func getText() throws -> String {
        return try getText(DEFAULT_PROGRAM_NAME, Interval.of(0, tokens.size() - 1))
    }

    /** Return the text from the original tokens altered per the
     *  instructions given to this rewriter in programName.
     */
    public func getText(_ programName: String) throws -> String {
        return try getText(programName, Interval.of(0, tokens.size() - 1))
    }

    /** Return the text associated with the tokens in the interval from the
     *  original token stream but with the alterations given to this rewriter.
     *  The interval refers to the indexes in the original token stream.
     *  We do not alter the token stream in any way, so the indexes
     *  and intervals are still consistent. Includes any operations done
     *  to the first and last token in the interval. So, if you did an
     *  insertBefore on the first token, you would get that insertion.
     *  The same is true if you do an insertAfter the stop token.
     */
    public func getText(_ interval: Interval) throws -> String {
        return try getText(DEFAULT_PROGRAM_NAME, interval)
    }

    public func getText(_ programName: String, _ interval: Interval) throws -> String {
        var start: Int = interval.a
        var stop: Int = interval.b

        // ensure start/end are in range
        if stop > tokens.size() - 1 {
            stop = tokens.size() - 1
        }
        if start < 0 {
            start = 0
        }
        guard let rewrites = programs[programName] , !rewrites.isEmpty else {
             return try tokens.getText(interval) // no instructions to execute
        }

        let buf: StringBuilder = StringBuilder()

        // First, optimize instruction stream
        var indexToOp: Dictionary<Int, RewriteOperation> = try rewrites.reduceToSingleOperationPerIndex()

        // Walk buffer, executing instructions and emitting tokens
        var i: Int = start
        while i <= stop && i < tokens.size() {
            let op: RewriteOperation? = indexToOp[i]
            indexToOp.removeValue(forKey: i)
            //indexToOp.remove(i); // remove so any left have index size-1
            let t: Token = try tokens.get(i)
            if op == nil {
                // no operation at that index, just dump token
                if t.getType() != CommonToken.EOF {
                    buf.append(t.getText()!)
                }
                i += 1 // move to next token
            } else {
                i = try op!.execute(buf) // execute operation and skip
            }

        }

        // include stuff after end if it's last index in buffer
        // So, if they did an insertAfter(lastValidIndex, "foo"), include
        // foo if end==lastValidIndex.
        if stop == tokens.size() - 1 {
            // Scan any remaining operations after last token
            // should be included (they will be inserts).
            for op: RewriteOperation in indexToOp.values {
                if op.index >= tokens.size() - 1 {
                    buf.append(op.text!)
                }
            }
        }

        return buf.toString()
    }

}
