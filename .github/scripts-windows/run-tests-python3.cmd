cd runtime-testsuite
mvn -Dparallel=classes -DthreadCount=2 -Dantlr-python3-python="C:\Python310\python.exe" -Dtest=python3.** test
cd ..
