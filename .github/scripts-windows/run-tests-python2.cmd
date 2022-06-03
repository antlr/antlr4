cd runtime-testsuite
mvn -Dparallel=classes -DthreadCount=2 -Dantlr-python2-exec="C:\Python27\python.exe" -Dtest=python2.** test
cd ..
