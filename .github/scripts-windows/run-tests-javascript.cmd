cd runtime-testsuite
mvn -Dparallel=classes -DthreadCount=2 -Dtest=javascript.** test
cd ..
