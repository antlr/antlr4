cd runtime-testsuite
mvn -Dparallel=classes -DthreadCount=4 -Dtest=python3.** test -Dantlr-python3-python="C:\Python35\python.exe"
cd ..