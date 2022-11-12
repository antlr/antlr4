# Run this so we get right jars before trying this script:
#  cd ANTLR-ROOT-DIR
#  mvn install -DskipTests=true
#  cd runtime-tests
#  mvn install jar:test-jar -DskipTests=true 
#
# Run script with 
#
#  traceatn.sh /tmp/JSON.g4 json /tmp/foo.json

export ANTLRJAR=/Users/parrt/.m2/repository/org/antlr/antlr4/4.11.2-SNAPSHOT/antlr4-4.11.2-SNAPSHOT-complete.jar
export TESTJAR=/Users/parrt/.m2/repository/org/antlr/antlr4-runtime-testsuite/4.11.2-SNAPSHOT/antlr4-runtime-testsuite-4.11.2-SNAPSHOT-tests.jar
java -classpath $ANTLRJAR:$TESTJAR org.antlr.v4.test.runtime.TraceATN $@
