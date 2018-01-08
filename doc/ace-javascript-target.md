# Integrating ANTLR JavaScript parsers with ACE editor

Having the ability to parse code other than JavaScript is great, but nowadays users expect to be able to edit code with nice edit features such as keyword highlighting, indentation and brace matching, and advanced ones such as syntax checking.

I have been through the process of integrating an ANTLR parser with ACE, the dominant code editor for web based code editing. Information about ACE can be found on their web site. 

This page describes my experience, and humbly aims to help you get started. It is not however a reference guide, and no support is provided.

## Architecture

The ACE editor is organized as follows

1. The editor itself is a <div> which once initialized comprises a number of elements. This UI element is responsible for the display, and the generation of edit events.
1. The editor relies on a Session, which manages events and configuration.
1. The code itself is stored in a Document. Any insertion or deletion of text is reflected in the Document.
1. Keyword highlighting, indentation and brace matching are delegated to a mode. There is no direct equivalent of an ACE mode in ANTLR. While keywords are the equivalent of ANTLR lexer tokens, indentation and brace matching are edit tasks, not parsing ones. A given ACE editor can only have one mode, which corresponds to the language being edited. There is no need for ANTLR integration to support keyword highlighting, indentation and brace matching.
1. Syntax checking is delegated to a worker. This is where ANTLR integration is needed. If syntax checking is enabled, ACE asks the mode to create a worker. In JavaScript, workers run in complete isolation i.e. they don't share code or variables with other workers, or with the HTML page itself.
1. The below diagram describes how the whole system works. In green are the components *you* need to provide. You'll notice that there is no need to load ANTLR in the HTML page itself. You'll also notice that ACE maintains a document in each thread. This is done through low level events sent by the ACE session to the worker which describe the delta. Once applied to the worker document, a high level event is triggered, which is easy to handle since at this point the worker document is a perfect copy of the UI document.  

<img src=images/ACE-Architecture.001.png>

## Step-by-step guide

The first thing to do is to create an editor in your html page. This is thoroughly described in the ACE documentation, so we'll just sum it up here:

```xml
<script src="../js/ace/ace.js" type="text/javascript" charset="utf-8"></script>
<script>
    var editor = ace.edit("editor");
</script>
```

This should give you a working editor. You may want to control its sizing using CSS. I personally load the editor in an iframe and set its style to position: absolute, top: 0, left: 0 etc... but I'm sure you know better than me how to achieve results. 

The second thing to do is to configure the ACE editor to use your mode i.e. language configuration. A good place to start is to inherit from the built-in TextMode. The following is a very simple example, which only caters for comments, literals, and a limited subset of separators and keywords :

```javascript
ace.define('ace/mode/my-mode',["require","exports","module","ace/lib/oop","ace/mode/text","ace/mode/text_highlight_rules", "ace/worker/worker_client" ], function(require, exports, module) {
    var oop = require("ace/lib/oop");
    var TextMode = require("ace/mode/text").Mode;
    var TextHighlightRules = require("ace/mode/text_highlight_rules").TextHighlightRules;

    var MyHighlightRules = function() {
        var keywordMapper = this.createKeywordMapper({
            "keyword.control": "if|then|else",
            "keyword.operator": "and|or|not",
            "keyword.other": "class",
            "storage.type": "int|float|text",
            "storage.modifier": "private|public",
            "support.function": "print|sort",
            "constant.language": "true|false"
  }, "identifier");
        this.$rules = {
            "start": [
                { token : "comment", regex : "//" },
                { token : "string",  regex : '["](?:(?:\\\\.)|(?:[^"\\\\]))*?["]' },
                { token : "constant.numeric", regex : "0[xX][0-9a-fA-F]+\\b" },
                { token : "constant.numeric", regex: "[+-]?\\d+(?:(?:\\.\\d*)?(?:[eE][+-]?\\d+)?)?\\b" },
                { token : "keyword.operator", regex : "!|%|\\\\|/|\\*|\\-|\\+|~=|==|<>|!=|<=|>=|=|<|>|&&|\\|\\|" },
                { token : "punctuation.operator", regex : "\\?|\\:|\\,|\\;|\\." },
                { token : "paren.lparen", regex : "[[({]" },
                { token : "paren.rparen", regex : "[\\])}]" },
                { token : "text", regex : "\\s+" },
                { token: keywordMapper, regex: "[a-zA-Z_$][a-zA-Z0-9_$]*\\b" }
            ]
        };
    };
    oop.inherits(MyHighlightRules, TextHighlightRules);

    var MyMode = function() {
        this.HighlightRules = MyHighlightRules;
    };
    oop.inherits(MyMode, TextMode);

    (function() {

        this.$id = "ace/mode/my-mode";

    }).call(MyMode.prototype);

    exports.Mode = MyMode;
});
```

Now if you store the above in a file called "my-mode.js", setting the ACE Editor becomes straightforward:

```xml
<script src="../js/ace/ace.js" type="text/javascript" charset="utf-8"></script>
<script src="../js/my-mode.js" type="text/javascript" charset="utf-8"></script>
<script>
    var editor = ace.edit("editor");
    editor.getSession().setMode("ace/mode/my-mode");
</script>
```

At this point you should have a working editor, able to highlight keywords. You may wonder why you need to set the tokens when you have already done so in your ANTLR lexer grammar. First, ACE expects a classification (control, operator, type...) which does not exist in ANTLR. Second, there is no need for ANTLR to achieve this, since ACE comes with its own lexer.

Ok, now that we have a working editor comes the time where we need syntax validation. This is where the worker comes in the picture.

Creating the worker is the responsibility of the mode you provide. So you need to enhance it with something like the following:
 
```javascript
var WorkerClient = require("ace/worker/worker_client").WorkerClient;
this.createWorker = function(session) {
    this.$worker = new WorkerClient(["ace"], "ace/worker/my-worker", "MyWorker", "../js/my-worker.js");
    this.$worker.attachToDocument(session.getDocument());

    this.$worker.on("errors", function(e) {
        session.setAnnotations(e.data);
    });

    this.$worker.on("annotate", function(e) {
        session.setAnnotations(e.data);
    });

    this.$worker.on("terminate", function() {
        session.clearAnnotations();
    });

    return this.$worker;

};
```

The above code needs to be placed in the existing worker, after: 

```javascript
this.$id = "ace/mode/my-mode";
```

Please note that the mode code runs on the UI side, not the worker side. The event handlers here are for events sent by the worker, not to the worker.

Obviously the above won't work out of the box, because you need to provide the "my-worker.js" file.

Creating a worker from scratch is not something I've tried. Simply put, your worker needs to handle all messages sent by ACE using the WorkerClient created by the mode. This is not a simple task, and is better delegated to existing ACE code, so we can focus on tasks specific to our language.

What I did is I started from "mode-json.js", a rather simple worker which comes with ACE, stripped out all JSON validation related stuff out of it, and saved the remaining code in a file name "worker-base.js" which you can find [here](resources/worker-base.js). Once this done, I was able to create a simple worker, as follows:

```javascript
importScripts("worker-base.js");
ace.define('ace/worker/my-worker',["require","exports","module","ace/lib/oop","ace/worker/mirror"], function(require, exports, module) {
    "use strict";

    var oop = require("ace/lib/oop");
    var Mirror = require("ace/worker/mirror").Mirror;

    var MyWorker = function(sender) {
        Mirror.call(this, sender);
        this.setTimeout(200);
        this.$dialect = null;
    };

    oop.inherits(MyWorker, Mirror);

    (function() {

        this.onUpdate = function() {
            var value = this.doc.getValue();
            var annotations = validate(value);
            this.sender.emit("annotate", annotations);
        };

    }).call(MyWorker.prototype);

    exports.MyWorker = MyWorker;
});

var validate = function(input) {
    return [ { row: 0, column: 0, text: "MyMode says Hello!", type: "error" } ];
};
```

At this point, you should have an editor which displays an error icon next to the first line. When you hover over the error icon, it should display: MyMode says Hello!. Is that not a friendly worker? Yum.

What remains to be done is have our validate function actually validate the input. Finally ANTLR comes in the picture!

To start with, let's load ANTLR and your parser, listener etc.. 

The preferred approach for loading parser code is to bundle your parser, [as described here](javascript-target.md).
You can then load it as part of the importScripts instruction at the start of your worker code.

Another approach is to load it using 'require'. Easy, since you could write:

```js
var antlr4 = require('antlr4/index');
```

This may work, but it's actually unreliable. The reason is that the 'require' function that comes with ACE uses a different syntax than the 'require' function used by ANTLR, which follows the NodeJS 'require' convention. 
So we need to bring in a NodeJS compatible 'require' function that conforms to the NodeJS syntax. I personally use one that comes from Torben Haase's Honey project, which you can find in li/require.js. 
But hey, now we're going to have 2 'require' functions not compatible with each other! Indeed, this is why you need to take special care, as follows:

```js
// load nodejs compatible require
var ace_require = require;
require = undefined;
var Honey = { 'requirePath': ['..'] }; // walk up to js folder, see Honey docs
importScripts("../lib/require.js");
var antlr4_require = require;
require = ace_require;
```
Now it's safe to load antlr and the parsers generated for your language. 
Assuming that your language files (generated or hand-built) are in a folder with an index.js file that calls require for each file, your parser loading code can be as simple as follows:
```js
// load antlr4 and myLanguage
var antlr4, mylanguage;
try {
    require = antlr4_require;
    antlr4 = require('antlr4/index');
    mylanguage = require('mylanguage/index');
} finally {
    require = ace_require;
}
```
Please note the try-finally construct. ANTLR uses 'require' synchronously so it's perfectly safe to ignore the ACE 'require' while running ANTLR code. ACE itself does not guarantee synchronous execution, so you are much safer always switching 'require' back to 'ace_require'.

Now detecting deep syntax errors in your code is a task for your ANTLR listener or visitor or whatever piece of code you've delegated this to. We're not going to describe this here, since it would require some knowledge of your language. However, detecting grammar syntax errors is something ANTLR does beautifully (isn't that why you went for ANTLR in the first place?). So what we will illustrate here is how to report grammar syntax errors. I have no doubt that from there, you will be able to extend the validator to suit your specific needs.
Whenever ANTLR encounters an unexpected token, it fires an error. By default, the error is routed to an error listener which simply writes to the console.
What we need to do is replace this listener by our own listener, se we can route errors to the ACE editor. First, let's create such a listener:
```js
// class for gathering errors and posting them to ACE editor
var AnnotatingErrorListener = function(annotations) {
    antlr4.error.ErrorListener.call(this);
    this.annotations = annotations;
    return this;
};

AnnotatingErrorListener.prototype = Object.create(antlr4.error.ErrorListener.prototype);
AnnotatingErrorListener.prototype.constructor = AnnotatingErrorListener;

AnnotatingErrorListener.prototype.syntaxError = function(recognizer, offendingSymbol, line, column, msg, e) {
    this.annotations.push({
        row: line - 1,
        column: column,
        text: msg,
        type: "error"
 });
};
```
With this, all that remains to be done is plug the listener in when we parse the code. Here is how I do it:
```js
var validate = function(input) {
    var stream = CharStreams.fromString(input);
    var lexer = new mylanguage.MyLexer(stream);
    var tokens = new antlr4.CommonTokenStream(lexer);
    var parser = new mylanguage.MyParser(tokens);
    var annotations = [];
    var listener = new AnnotatingErrorListener(annotations)
    parser.removeErrorListeners();
    parser.addErrorListener(listener);
    parser.parseMyRule();
    return annotations;
};
```
You know what? That's it! You now have an ACE editor that does syntax validation using ANTLR! I hope you find this useful, and simple enough to get started.
Now wait, hey! How do you debug this? Well, as usual, using Chrome, since no other browser is able to debug worker code. What a shame...
