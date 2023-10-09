# TypeScript

Antlr4 TypeScript runtime uses the JavaScript runtime and adds type files to it.
This guarantees the same behaviour and performance across both target languages.
The target lexers, parsers, listeners, and visitors which are generated from your grammar will be TypeScript.

The runtime is built using TypeScript v4.8.3, node 16.17 and webpack 5.66.
It may work with older versions but they have not been tested and they will not be supported.


## How to create a TypeScript lexer or parser?

This is pretty much the same as creating a Java lexer or parser, except you need to specify the language target, for example:

```bash
$ antlr4 -Dlanguage=TypeScript MyLanguage.g4
```

For a full list of antlr4 tool options, please visit the [tool documentation page](tool-options.md).

## Where can I get the runtime?

Once you've generated the lexer and/or parser code, you need to download the runtime from [npm](https://www.npmjs.com/package/antlr4).

We will not document here how to refer to the runtime from your project, since this would differ a lot depending on your project type and IDE. 

## How do I get the runtime in my browser?

The runtime is webpacked and sits in the dist folder. A .map file is also provided.

## How do I run the generated lexer and/or parser?

Let's suppose that your grammar is named, as above, "MyLanguage". Let's suppose this parser comprises a rule named "myStartRule". The tool will have generated for you the following files:

*   MyLanguageLexer.ts
*   MyLanguageParser.ts
*   MyLanguageListener.ts (if you have not activated the -no-listener option)
*   MyLanguageVisitor.ts (if you have activated the -visitor option)
   
There is no listener or visitor interface generated, instead the generated listener and visitor class methods are implemented using lambdas.

A function to parse code according to the grammar in MyLanguage.g4 would be written like this:

```typescript
import { CharStream, CommonTokenStream } from "antlr4";
import MyLanguageParser, { MyStartRuleContext } from"./MyLanguageParser";
import MyLanguageLexer from "./MyLanguageLexer";

export function parseMyLanguage(codeToParse: string): MyStartRuleContext {
    const chars = new CharStream(codeToParse);
    const lexer = new MyLanguageLexer(chars);
    const tokens = new CommonTokenStream(lexer);
    const parser = new MyLanguageParser(tokens);
    return parser.myStartRule();
}
```

Tha above function will execute. But it won't be useful unless you do one of the following:

* visit the parse tree using a custom listener
* visit the parse tree using a custom visitor
* populate the grammar with production code (like AntLR3)
  * _This approach is discouraged in Antlr4, Production code is target specific, which would prevent a grammar being used by multiple target languages_
 
## How do I create and run a visitor?

You need to create an instance of custom visitor class and use it to visit the parse tree, as follows:

```typescript
import { ParseTreeVisitor } from "antlr4";
import { MyStartRuleContext} from "./MyLanguageParser";
import MyLanguageVisitor from "./MyLanguageVisitor";
import { parseMyLanguage } from "./typescript-parse";

type MyVisitResult = void;

class CustomVisitor
    extends ParseTreeVisitor<MyVisitResult>
    implements MyLanguageVisitor<MyVisitResult>
{

    visitMyStartRule(ctx: MyStartRuleContext): MyVisitResult {
        return this.visitChildren(ctx);
    }

}

function parseAndVisit(codeToParse: string) {
    const parseResult = parseMyLanguage(codeToParse);
    const visitor = new CustomVisitor();
    visitor.visit(parseResult);
}
````

## How do I create and run a custom listener?

You need to create an instance of a custom listener class and use it to visit the parse tree, as follows:

```typescript
import { ParseTreeWalker, ParseTreeListener } from "antlr4";
import { MyStartRuleContext } from "./MyLanguageParser";
import MyLanguageListener from "./MyLanguageListener";
import { parseMyLanguage } from "./typescript-parse";

class CustomListener
    extends ParseTreeListener
    implements MyLanguageListener
{
    enterMyStartRule(ctx: MyStartRuleContext) {
        console.log("Enter Node 'myStartRule'");
    }
    exitMyStartRule(ctx: MyStartRuleContext) {
        console.log("Exit Node 'myStartRule'");
    }

}

function parseAndRunListener(codeToParse: string) {
    const parseResult = parseMyLanguage(codeToParse);
    ParseTreeWalker.DEFAULT.walk(new CustomListener(), parseResult);
}
```

## How do I integrate my parser with ACE editor?

This specific task is described in this [dedicated page](ace-javascript-target.md).
 
## How can I learn more about ANTLR?

Further information can be found from  "The definitive ANTLR 4 reference" book.

The TypeScript implementation of ANTLR is based on the JavaScript implementation, which is as close as possible to the Java one, so you shouldn't find it difficult to adapt the book's examples to TypeScript.
