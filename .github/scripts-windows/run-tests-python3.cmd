cd runtime-testsuite
mvn -Dantlr-python3-python="C:\Python38\python.exe" -Dparallel=classes -DthreadCount=4 -Dtest=python3.** test
cd ..