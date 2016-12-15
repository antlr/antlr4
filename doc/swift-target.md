# ANTLR4 Language Target, Runtime for Swift

## Install ANTLR4

Make sure you have the ANTLR
installed.[The getting started guide](getting-started.md) should get
you started.

## Create a Swift lexer or parser 
This is pretty much the same as creating a Java lexer or parser, 
except you need to specify the language target, for example:

``` 
$ antlr4 -Dlanguage=Swift MyGrammar.g4 
``` 
For a full list of antlr4 tool options, please visit the
[tool documentation page](tool-options.md).

## Build your Swift project with ANTLR runtime

The following instructions are assuming Xcode as the IDE:

* __Add parser/lexer to project__. Make sure the parsers/lexers
generated in __step 2__ are added to the project. To do this, you can
drag the generated files from Finder to the Xcode IDE. Remember to
check __Copy items if needed__ to make sure the files are actually
moved into the project folder instead of symbolic links (see the
screenshot below). After moving you will be able to see your files in
the project navigator. But when you open one of the files, you will
see Xcode complaining the module "Antlr4" could not be found at the
import statement. This is expected, since we still need the ANTLR
Swift runtime for those missing symbols.

<img src=images/dragfile.png style="width: 200px;">

* __Download ANTLR runtime__. Due to unstable ABI of Swift language,
there will not be a single "library" for the Swift ANTLR runtime for
now.  To get Swift ANTLR runtime, clone the ANTLR repository. Open it
in finder. From the root directory of the repo, go to runtime/Swift
folder.  You will see the Xcode project manifest file:
__Antlr4.xcodeproj__.

* __Import ANTLR Swift runtime into project__. Drag Antlr4.xcodeproj
into your project, after this is done, your Xcode project navigator
will be something like the screenshot below. In this case, your own
project is "Smalltalk", and you will be able to see the
Antlr4.xcodeproj shown as a contained project. The error message will
still be there, that's because we still need to tell Xcode how to find
the runtime.

<img src=images/xcodenav.png style="width: 200px;">

* __Build ANTLR runtime__. By expanding the "Products" folder in the
inner project (Antlr4.xcodeproj), you will see two Antlr4.framework
files. ".framework" file is the swift version of ".jar", ".a" as in
JAVA, C/C++ Initially those two files should be red, that's because
they are not built. To build, click the "target selection" button
right next to your Xcode run button. And in the drop down select the
target you want to build. And you will see the two Antlr4.framework
files are for iOS and OSX, as shown below. After target selection,
press "CMD+B", and Xcode will build the framework for you. Then you
will see one of the frameworks become black.

<img src=images/targetselection.png style="width: 200px;">

* __Add dependencies__. Simply adding ANTLR Swift runtime and build
the artifact is not enough. You still need to specify
dependencies. Click your own project (Smalltalk), and you will see
project setting page. Go to "Build Phase", and inside it make sure
your ANTLR Swift runtime framework is added to both "__Target
Dependencies__" and "__Link Binary With Libraries__" sections, as
shown below. After correctly added dependencies, the error message for
importing library will be gone.

<img src=images/xcodedep.png>

## Example playground

The Swift runtime includes an Xcode playground to get started with.

First go to the ANTLR4 repository, and open
`runtime/Swift/Antlr4.xcworkspace` in Xcode.  Select "Antlr4 OSX > My
Mac" as the build target, and build the project as normal. The
playground should then be active.

The playground includes a simple grammar called "Hello", and an
example for walking the parse tree.  You should see in the playground
output that it is printing messages for each node in the parse tree as
it walks.

The grammar is defined in the playground's `Resources/Hello.g4`.  The
parser was generated from the grammar using ANTLR like this:

``` 
antlr4 -Dlanguage=Swift -visitor -o ../Sources/Autogen Hello.g4
```

The example tree walker is in Sources/HelloWalker.swift.
 
