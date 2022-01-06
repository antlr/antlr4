dotnet build runtime/CSharp/src/Antlr4.csproj -c Release
dotnet pack runtime/CSharp/src/Antlr4.csproj -c Release
cd runtime-testsuite
mvn -Dparallel=classes -DthreadCount=4 -Dtest=csharp.** test
cd ..
