import Antlr4

public class HelloWalker: HelloBaseListener {
    public override func enterR(_ ctx: HelloParser.RContext) {
        print("enterR: \(ctx.IDText())")
    }

    public override func exitR(_ ctx: HelloParser.RContext) {
        print("exitR: \(ctx.IDText())")
    }
}


fileprivate extension HelloParser.RContext {
    fileprivate func IDText() -> String {
        return ID()?.getText() ?? ""
    }
}
