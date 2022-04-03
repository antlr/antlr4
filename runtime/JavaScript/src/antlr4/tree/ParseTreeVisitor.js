export default class ParseTreeVisitor {
    visit(ctx) {
        if (Array.isArray(ctx)) {
            return ctx.map(function(child) {
                return child.accept(this);
            }, this);
        } else {
            return ctx.accept(this);
        }
    }

    visitChildren(ctx) {
        if (ctx.children) {
            return this.visit(ctx.children);
        } else {
            return null;
        }
    }

    visitTerminal(node) {
    }

    visitErrorNode(node) {
    }
}

