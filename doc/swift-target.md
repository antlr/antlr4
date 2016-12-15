# ANTLR4 Language Target, Runtime for Swift

### Usage

#### 1. Install ANTLR4

[The getting started guide](getting-started.md) should get you started.

#### 2. create a Swift lexer or parser
This is pretty much the same as creating a Java lexer or parser, except you need to specify the language target, for example:

```
$ antlr4 -Dlanguage=Swift MyGrammar.g4
```
For a full list of antlr4 tool options, please visit the [tool documentation page](tool-options.md).

#### 3. Get the Swift ANTLR runtime

Due to unstable ABI of Swift language, there will not be a single "library" for the Swift ANTLR runtime for now.

The following instructions are assuming Xcode as the IDE:

* __Create parser/lexer__. Make sure the parsers/lexers generated in __step 2__ are added to the project. To do this, you can drag
the generated files from Finder to the Xcode IDE. Remember to check __Copy items if needed__ to make sure
the files are actually moved into the project folder instead of symbolic links (see the screenshot below). After moving you will be
able to see your files in the project navigator. But when you open one of the files, you will see Xcode 
complaining the module "Antlr4" could not be found at the import statement. This is expected, since we still need the ANTLR Swift runtime for
those missing symbols.

<img src=images/dragfile.png>

* __Download ANTLR runtime__. Clone the ANTLR repository. Open it in finder. From the root directory of the repo, go to runtime/Swift folder. You will be
able to see the Xcode project manifest file: __Antlr4.xcodeproj__. 

* __Import ANTLR Swift runtime into project__. Drag Antlr4.xcodeproj into your project, after this is done, your Xcode project navigator will be something
like the screenshot below. In this case, your own project is "Smalltalk", and you will be able to see the Antlr4.xcodeproj
shown as a contained project. 

<img src=images/xcodenav.png>

* __Build ANTLR runtime__. And by opening the "Products" folder in the inner project (Antlr4.xcodeproj), you will see
two Antlr4.framework files. ".framework" file is the swift version of ".jar", ".a" as in JAVA, C/C++
Initially those two files should be red, that's because they are not built. To build, click the "target selection" button
right next to your Xcode run button. And in the drop down select the target you want to build. And you will see the two 
Antlr4.framework files are for iOS and OSX. As shown below. After target selection, press "CMD+B", and Xcode will build
the framework for you. Then you will see one of the frameworks become black.

<img src=images/targetselection.png>

** __Add dependencies__. Simply adding ANTLR Swift runtime and build the artifact is not enough. You still need to specify
dependencies. Click your own project (Smalltalk), and you will see project setting page. Go to "Build Phase", and inside it
make sure your ANTLR Swift runtime framework is added to both "__Target Dependencies__" and "__Link Binary With Libraries__"
sections. As shown below.

<img src=images/xcodedep.png>

#### 4. Example playground

The Swift runtime includes an Xcode playground to get started with.

In Xcode, open `antlr4/runtime/Swift/Antlr4.xcworkspace`.  Select
"Antlr4 OSX > My Mac" as the build target, and build the project as normal.
The playground should then be active.

The playground includes a simple grammar called "Hello", and an example for
walking the parse tree.  You should see in the playground output that it is
printing messages for each node in the parse tree as it walks.

The playground shows how to create a lexer, token stream, and parser, and
how to execute the parse.

The grammar is defined in the playground's `Resources/Hello.g4`.  The parser
was generated from the grammar using Antlr4 like this:

```
cd 'antlr4/runtime/Swift/Antlr4 playground.playground/Resources'
antlr4 -Dlanguage=Swift -visitor -o ../Sources/Autogen Hello.g4
```

The example tree walker is in `Sources/HelloWalker.swift`.
