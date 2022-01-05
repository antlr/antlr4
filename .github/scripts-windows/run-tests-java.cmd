cd runtime-testsuite
mvn -Dparallel=classes -DthreadCount=4 -Dtest=java.** test
cd ..
