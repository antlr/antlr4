set ChocolateyInstall=C:\tools\chocolatey
C:\ProgramData\chocolatey\bin\cinst.exe visualstudio2022-workload-vctools -y

cd runtime-testsuite
mvn -Dparallel=classes -DthreadCount=2 -Dtest=cpp.** test
cd ..
