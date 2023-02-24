# TypeScript

Antlr4 TypeScript runtime uses the JavaScript runtime and adds type files to it.
This guarantees the same behaviour and performance across both target languages.
Generated lexers, parsers, listeners and visitors are generated in TypeScript.

The runtime is built using TypeScript v4.8.3, node 16.17 and webpack 5.66.
It may work with older versions but they have not been tested and they will not be supported.


## How to create a TypeScript lexer or parser?

This is pretty much the same as creating a Java lexer or parser, except you need to specify the language target, for example:

```bash
$ antlr4 -Dlanguage=TypeScript MyGrammar.g4
```

For a full list of antlr4 tool options, please visit the [tool documentation page](tool-options.md).

## Where can I get the runtime?

Once you've generated the lexer and/or parser code, you need to download the runtime from [npm](https://www.npmjs.com/package/antlr4).

We will not document here how to refer to the runtime from your project, since this would differ a lot depending on your project type and IDE. 

## How do I get the runtime in my browser?

The runtime is webpacked and sits in the dist folder. A .map file is also provided.

## How do I run the generated lexer and/or parser?

Let's suppose that your grammar is named, as above, "MyGrammar". Let's suppose this parser comprises a rule named "MyStartRule". The tool will have generated for you the following files:

*   MyGrammarLexer.ts
*   MyGrammarParser.ts
*   MyGrammarListener.ts (if you have not activated the -no-listener option)
*   MyGrammarVisitor.ts (if you have activated the -visitor option)
   
There is no listener or visitor interface generated, instead the generated listener and visitor class methods are implemented using lambdas.

Now a fully functioning script might look like the following:

```typescript
import { CharStream, CommonTokenStream }  from 'antlr4';
import MyGrammarLexer from './MyGrammarLexer';
import MyGrammarParser from './MyGrammarParser';

const input = "your text to parse here"
const chars = new CharStream(input); // replace this with a FileStream as required
const lexer = new MyGrammarLexer(chars);
const tokens = new CommonTokenStream(lexer);
const parser = new MyGrammarParser(tokens);
const tree = parser.MyStartRule();

```

Tha above program will work. But it won't be useful unless you do one of the following:

* you visit the parse tree using a custom listener
* you visit the parse tree using a custom visitor
* your grammar contains production code (like AntLR3)
 
(please note that production code is target specific, so you can't have multi target grammars that include production code)
 
## How do I create and run a visitor?

You need to create a custom visitor and use it to visit the parse tree, as follows:
```typescript

import { ParserRuleContext } from 'antlr4';
import MyGrammarVisitor from './MyGrammarVisitor';

class CustomVisitor extends MyGrammarVisitor {

  visitChildren(ctx: ParserRuleContext) {
    if (!ctx) {
      return;
    }
    if (ctx.children) {
      return ctx.children.map(child => {
        if (child.children && child.children.length != 0) {
          return child.accept(this);
        } else {
          return child.getText();
        }
      });
    }
  }
}

tree.accept(new CustomVisitor());
````

## How do I create and run a custom listener?

You need to create a custom listener and use it to visit the parse tree, as follows:

```typescript

import { ParseTreeWalker } from 'antlr4';
import MyGrammarListener from './MyGrammarListener';

class MyTreeWalker extends MyGrammarListener {

    exitMyStartRule = (ctx: MyStartRuleContext) => {
        console.log("In MyStartRule");
    };
    
}

const walker = new MyTreeWalker();
ParseTreeWalker.DEFAULT.walk(walker, tree);

```

## How do I integrate my parser with ACE editor?

This specific task is described in this [dedicated page](ace-javascript-target.md).
 
## How can I learn more about ANTLR?

Further information can be found from  "The definitive ANTLR 4 reference" book.

The TypeScript implementation of ANTLR is based on the JavaScript implementation, which is as close as possible to the Java one, so you shouldn't find it difficult to adapt the book's examples to TypeScript.
