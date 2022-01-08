cd runtime-testsuite
mvn -Dantlr-python2-python="C:\Python27\python.exe" -Dparallel=classes -DthreadCount=4 -Dtest=python2.** test 
cd ..
