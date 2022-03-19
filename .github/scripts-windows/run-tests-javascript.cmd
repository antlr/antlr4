cd runtime-testsuite
export MAVEN_OPTS="-Xmx2g"
mvn -Dparallel=classes -DthreadCount=2 -Dtest=javascript.** test
cd ..
