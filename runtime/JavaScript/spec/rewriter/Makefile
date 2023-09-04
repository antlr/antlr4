ANTLR_VERSION = 4.13.0
ANTLR_JAR = .antlr/antlr-$(ANTLR_VERSION)-complete.jar

.antlr:
	mkdir .antlr

$(ANTLR_JAR): .antlr
	curl https://www.antlr.org/download/antlr-$(ANTLR_VERSION)-complete.jar -o $(ANTLR_JAR)

abc: abc.g4 $(ANTLR_JAR)
	java -jar $(ANTLR_JAR) -Dlanguage=JavaScript -no-listener abc.g4 -o generatedCode/
	sed -i.bak "s/import antlr4 from 'antlr4'/import antlr4 from '..\/..\/..\/src\/antlr4\/index.node.js'/" generatedCode/abc.js

calc: calc.g4 $(ANTLR_JAR)
	java -jar $(ANTLR_JAR) -Dlanguage=JavaScript -no-listener calc.g4 -o generatedCode/
	sed -i.bak "s/import antlr4 from 'antlr4'/import antlr4 from '..\/..\/..\/src\/antlr4\/index.node.js'/" generatedCode/calc.js
