cd runtime-testsuite
export MAVEN_OPTS="-Xmx8g"
mvn -Dparallel=classes -DthreadCount=2 -Dtest=go.** test
cd ..
