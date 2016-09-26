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

The runtime has also been extensively tested against Node.js 0.10.33. No issue was found.

## How to create a JavaScript lexer or parser?

This is pretty much the same as creating a Java lexer or parser, except you need to specify the language target, for example:

```bash
$ antlr4 -Dlanguage=JavaScript MyGrammar.g4
```

For a full list of antlr4 tool options, please visit the [tool documentation page](tool-options.md).


## How do I get the runtime in my browser?

The runtime is quite big and is currently maintained in the form of around 50 scripts, which follow the same structure as the runtimes for other targets (Java, C#, Python...).

This structure is key in keeping code maintainable and consistent across targets.

However, it would be a bit of a problem when it comes to get it into a browser. Nobody wants to write 50 times:

```
<script src='lib/myscript.js'>
```

In order to avoid having to do this, and also to have the exact same code for browsers and Node.js, we rely on a script which provides the equivalent of the Node.js 'require' function.

This script is provided by Torben Haase, and is NOT part of ANTLR JavaScript runtime, although the runtime heavily relies on it. Please note that syntax for 'require' in NodeJS is different from the one implemented by RequireJS and similar frameworks.  

So in short, assuming you have at the root of your web site, both the 'antlr4' directory and a 'lib' directory with 'require.js' inside it, all you need to put in your HTML header is the following:

```xml
<script src='lib/require.js'>
<script>
    var antlr4 = require('antlr4/index');
 </script>
```

This will load the runtime asynchronously.

## How do I get the runtime in Node.js?

### Use npm 

The npm package manager is the usual way to install node.js packages.   This will install the latest stable version from [npmjs.com](https://npmjs.com/package/antlr4), and save the dependency in your project's project.json :

```
$ npm install --save antlr4
```

### Alternatively... use npm to install the runtime from local sources

To install from local sources, first you need to create a link from the runtime's source directory (e.g. antlr4\runtime\JavaScript\src\antlr4), use this command:

```
$ npm link
```

Then in your target project's directory, issue this command to link to the local copy:

``` 
$ npm link antlr4
```

## How do I run the generated lexer and/or parser?

Let's suppose that your grammar is named, as above, "MyGrammar". Let's suppose this parser comprises a rule named "StartRule". The tool will have generated for you the following files:

*   MyGrammarLexer.js
*   MyGrammarParser.js
*   MyGrammarListener.js (if you have not activated the -no-listener option)
*   MyGrammarVisitor.js (if you have activated the -visitor option)
   
(Developers used to Java/C# ANTLR will notice that there is no base listener or visitor generated, this is because JavaScript having no support for interfaces, the generated listener and visitor are fully fledged classes)

Now a fully functioning script might look like the following:

```javascript
   var input = "your text to parse here"
   var chars = new antlr4.InputStream(input);
   var lexer = new MyGrammarLexer.MyGrammarLexer(chars);
   var tokens  = new antlr4.CommonTokenStream(lexer);
   var parser = new MyGrammarParser.MyGrammarParser(tokens);
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
    KeyPrinter = function() {
         MyGrammarListener.call(this); // inherit default listener
         return this;
    };
 
// inherit default listener
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
       tree = parser.StartRule() - only repeated here for reference
   var printer = new KeyPrinter();
 antlr4.tree.ParseTreeWalker.DEFAULT.walk(printer, tree);
```

## How do I integrate my parser with ACE editor?

This specific task is described in this [dedicated page](ace-javascript-target.md).
 
## How can I learn more about ANTLR?
 

Further information can be found from  "The definitive ANTLR 4 reference" book.

The JavaScript implementation of ANTLR is as close as possible to the Java one, so you shouldn't find it difficult to adapt the book's examples to JavaScript.
