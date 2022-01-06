python3 --version
where python
where python3

cd runtime-testsuite
mvn -Dantlr-python3-python="C:\Python38\python.exe" -Dparallel=classes -DthreadCount=4 -Dtest=python3.** test -Dantlr-python3-python="C:\Program Files\Python310\python.exe"
cd ..
