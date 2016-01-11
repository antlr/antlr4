// Generated from T.g4 by ANTLR 4.5.1
package parser // T

import "antlr4"

// A complete listener for a parse tree produced by TParser

type TListener interface {
    antlr4.ParseTreeListener

    EnterS(*SContext)
    ExitS(*SContext)

    EnterA(*AContext)
    ExitA(*AContext)

}
