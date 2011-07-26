java -Xmx500M -cp "/usr/local/lib/antlr4-complete.jar:$CLASSPATH" org.antlr.v4.Tool JavaLexer.g
java -Xmx500M -cp "/usr/local/lib/antlr4-complete.jar:$CLASSPATH" org.antlr.v4.Tool JavaParser.g
javac -cp .:/usr/local/lib/antlr4-complete.jar *.java
time java -Xmx400M -cp .:/usr/local/lib/antlr4-complete.jar TestJava .

# parsing /Users/parrt/antlr/code/antlr4/main/tool/playground/./input
# parsing /Users/parrt/antlr/code/antlr4/main/tool/playground/./JavaLexer.java
# parsing /Users/parrt/antlr/code/antlr4/main/tool/playground/./JavaParser.java
# parsing /Users/parrt/antlr/code/antlr4/main/tool/playground/./L.java
# parsing /Users/parrt/antlr/code/antlr4/main/tool/playground/./MLexer.java
# parsing /Users/parrt/antlr/code/antlr4/main/tool/playground/./MParser.java
# parsing /Users/parrt/antlr/code/antlr4/main/tool/playground/./T.java
# parsing /Users/parrt/antlr/code/antlr4/main/tool/playground/./TestJava.java
# parsing /Users/parrt/antlr/code/antlr4/main/tool/playground/./TestL.java
# parsing /Users/parrt/antlr/code/antlr4/main/tool/playground/./TestT.java
# parsing /Users/parrt/antlr/code/antlr4/main/tool/playground/./TestYang.java
# parsing /Users/parrt/antlr/code/antlr4/main/tool/playground/./TLexer.java
# parsing /Users/parrt/antlr/code/antlr4/main/tool/playground/./TParser.java
# parsing /Users/parrt/antlr/code/antlr4/main/tool/playground/./U.java
# parsing /Users/parrt/antlr/code/antlr4/main/tool/playground/./UParser.java
# parsing /Users/parrt/antlr/code/antlr4/main/tool/playground/./YangJavaLexer.java
# parsing /Users/parrt/antlr/code/antlr4/main/tool/playground/./YangJavaParser.java
# Lexer total time 182ms.
# Total time 603ms.
# finished parsing OK
# 726 lexer failovers
# 104048 lexer match calls
# 220 parser failovers
# 107785 parser predict calls
