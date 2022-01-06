cd runtime-testsuite
mvn -Dparallel=classes -DthreadCount=4 -Dtest=javascript.** test 
cd ..
