cd runtime-testsuite
mvn -Dparallel=classes -DthreadCount=2 -Dtest=java.** test
cd ..
