cd runtime-testsuite
export MAVEN_OPTS="-Xmx8g"
mvn -Dparallel=classes -DthreadCount=2 -Dantlr-python3-python="C:\Python310\python.exe" -Dtest=python3.** test
cd ..
