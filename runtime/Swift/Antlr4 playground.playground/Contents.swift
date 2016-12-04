import Antlr4

var input = "hello world"

let lexer = HelloLexer(ANTLRInputStream(input))
let tokens = CommonTokenStream(lexer)

do {
    let parser = try HelloParser(tokens)

    let tree = try parser.r()
    let walker = ParseTreeWalker()
    try walker.walk(HelloWalker(), tree)
}
catch ANTLRException.cannotInvokeStartRule {
    print("Error: cannot invoke start rule.")
}
catch ANTLRException.recognition(let e) {
    print("Unrecoverable recognition error: \(e)")
}
catch {
    print("Unknown error: \(error)")
}
