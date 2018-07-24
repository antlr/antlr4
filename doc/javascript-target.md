# JavaScript

## Which browsers are supported?

In theory, all browsers supporting ECMAScript 5.1.

In practice, this target has been extensively tested against:

* Firefox 34.0.5
* Safari 8.0.2
* Chrome 39.0.2171
* Explorer 11.0.3
 
The tests were conducted using Selenium. No issue was found, so you should find that the runtime works pretty much against any recent JavaScript engine.

## Is NodeJS supported?

The runtime has also been extensively tested against Node.js 0.12.7. No issue was found.

## How to create a JavaScript lexer or parser?

This is pretty much the same as creating a Java lexer or parser, except you need to specify the language target, for example:

```bash
$ antlr4 -Dlanguage=JavaScript MyGrammar.g4
```

For a full list of antlr4 tool options, please visit the [tool documentation page](tool-options.md).

## Where can I get the runtime?

Once you've generated the lexer and/or parser code, you need to download the runtime.

The JavaScript runtime is [available from npm](https://www.npmjs.com/package/antlr4).

If you can't use npm, the JavaScript runtime is also available from the ANTLR web site [download section](http://www.antlr.org/download/index.html). The runtime is provided in the form of source code, so no additional installation is required.

We will not document here how to refer to the runtime from your project, since this would differ a lot depending on your project type and IDE. 

## How do I get the runtime in my browser?

The runtime is quite big and is currently maintained in the form of around 50 scripts, which follow the same structure as the runtimes for other targets (Java, C#, Python...).

This structure is key in keeping code maintainable and consistent across targets.

However, it would be a bit of a problem when it comes to get it into a browser. Nobody wants to write 50 times:

```
<script src='lib/myscript.js'>
```

To avoid having doing this, the preferred approach is to bundle antlr4 with your parser code, using webpack.

You can get [information on webpack here](https://webpack.github.io).

The steps to create your parsing code are the following:
 - generate your lexer, parser, listener and visitor using the antlr tool
 - write your parse tree handling code by providig your custom listener or visitor, and associated code, using 'require' to load antlr.
 - create an index.js file with the entry point to your parsing code (or several if required).
 - test your parsing logic thoroughly using node.js
 
You are now ready to bundle your parsing code as follows:
 - following webpack specs, create a webpack.config file
 - in the webpack.config file, exclude node.js only modules using: node: { module: "empty", net: "empty", fs: "empty" }
 - from the cmd line, navigate to the directory containing webpack.config and type: webpack
 
This will produce a single js file containing all your parsing code. Easy to include in your web pages!

If you can't use webpack, you can use the lib/require.js script which implements the Node.js 'require' function in brwsers.

This script is provided by Torben Haase, and is NOT part of ANTLR JavaScript runtime.   

Assuming you have, at the root of your web site, both the 'antlr4' directory and a 'lib' directory with 'require.js' inside it, all you need to put in your HTML header is the following:

```xml
<script src='lib/require.js'>
<script>
    var antlr4 = require('antlr4/index');
 </script>
```

This will load the runtime asynchronously.

## How do I run the generated lexer and/or parser?

Let's suppose that your grammar is named, as above, "MyGrammar". Let's suppose this parser comprises a rule named "StartRule". The tool will have generated for you the following files:

*   MyGrammarLexer.js
*   MyGrammarParser.js
*   MyGrammarListener.js (if you have not activated the -no-listener option)
*   MyGrammarVisitor.js (if you have activated the -visitor option)
   
(Developers used to Java/C# ANTLR will notice that there is no base listener or visitor generated, this is because JavaScript having no support for interfaces, the generated listener and visitor are fully fledged classes)

Now a fully functioning script might look like the following:

```javascript
   var antlr4 = require('antlr4');
   var MyGrammarLexer = require('./MyGrammarLexer').MyGrammarLexer;
   var MyGrammarParser = require('./MyGrammarParser').MyGrammarParser;
   var MyGrammarListener = require('./MyGrammarListener').MyGrammarListener;

   var input = "your text to parse here"
   var chars = new antlr4.InputStream(input);
   var lexer = new MyGrammarLexer(chars);
   var tokens  = new antlr4.CommonTokenStream(lexer);
   var parser = new MyGrammarParser(tokens);
   parser.buildParseTrees = true;
   var tree = parser.MyStartRule();
```

This program will work. But it won't be useful unless you do one of the following:

* you visit the parse tree using a custom listener
* you visit the parse tree using a custom visitor
* your grammar comprises production code (like AntLR3)
 
(please note that production code is target specific, so you can't have multi target grammars that include production code)
 
## How do I create and run a custom listener?

Let's suppose your MyGrammar grammar comprises 2 rules: "key" and "value". The antlr4 tool will have generated the following listener: 

```javascript
   MyGrammarListener = function(ParseTreeListener) {
       // some code here
   }
   // some code here
   MyGrammarListener.prototype.enterKey = function(ctx) {};
   MyGrammarListener.prototype.exitKey = function(ctx) {};
   MyGrammarListener.prototype.enterValue = function(ctx) {};
   MyGrammarListener.prototype.exitValue = function(ctx) {};
```

In order to provide custom behavior, you might want to create the following class:

```javascript
var KeyPrinter = function() {
    MyGrammarListener.call(this); // inherit default listener
    return this;
};

// continue inheriting default listener
KeyPrinter.prototype = Object.create(MyGrammarListener.prototype);
KeyPrinter.prototype.constructor = KeyPrinter;

// override default listener behavior
KeyPrinter.prototype.exitKey = function(ctx) {
    console.log("Oh, a key!");
};
```

In order to execute this listener, you would simply add the following lines to the above code:

```javascript
    ...
    tree = parser.StartRule() // only repeated here for reference
var printer = new KeyPrinter();
antlr4.tree.ParseTreeWalker.DEFAULT.walk(printer, tree);
```

## What about TypeScript?

We currently do not have a TypeScript target, but Sam Harwell is [working on a port](https://github.com/tunnelvisionlabs/antlr4ts). [Here](https://github.com/ChuckJonas/extract-interface-ts) is Section 4.3 of [ANTLR 4 book](http://a.co/5jUJYmh) converted to typescript.

## How do I integrate my parser with ACE editor?

This specific task is described in this [dedicated page](ace-javascript-target.md).
 
## How can I learn more about ANTLR?
 

Further information can be found from  "The definitive ANTLR 4 reference" book.

The JavaScript implementation of ANTLR is as close as possible to the Java one, so you shouldn't find it difficult to adapt the book's examples to JavaScript.
