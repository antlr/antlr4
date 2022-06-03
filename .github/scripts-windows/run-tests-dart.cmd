C:\ProgramData\chocolatey\bin\choco.exe -y install dart-sdk
set PATH=%PATH%;C:\tools\dart-sdk\bin

cd runtime-testsuite
mvn -Dtest=dart.** test
cd ..
