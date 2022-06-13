set ChocolateyInstall=C:\tools\chocolatey
C:\ProgramData\chocolatey\bin\cinst.exe visualstudio2022-workload-vctools -y

cd runtime-testsuite
mvn -Dtest=cpp.** test
cd ..
