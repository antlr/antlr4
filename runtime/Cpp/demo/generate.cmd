@echo off
:: Created 2016, Mike Lischke (public domain)

:: This script is used to generate source files from the test grammars in the same folder. The generated files are placed
:: into a subfolder "generated" which the demo project uses to compile a demo binary.

set LOCATION=antlr4-4.5.4-SNAPSHOT.jar
java -jar %LOCATION% -Dlanguage=Cpp -listener -visitor -o generated/ -package antlrcpptest TLexer.g4 TParser.g4
::java -jar %LOCATION% -Dlanguage=Cpp -listener -visitor -o generated/ -package antlrcpptest -XdbgST TLexer.g4 TParser.g4
::java -jar %LOCATION% -Dlanguage=Java -listener -visitor -o generated/ -package antlrcpptest TLexer.g4 TParser.g4

