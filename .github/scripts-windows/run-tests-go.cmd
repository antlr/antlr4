cd runtime-testsuite
mvn -Dparallel=classes -DthreadCount=2 -Dtest=go.** test
cd ..
