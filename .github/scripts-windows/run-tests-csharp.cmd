dotnet build runtime/CSharp/src/Antlr4.csproj -c Release
dotnet pack runtime/CSharp/src/Antlr4.csproj -c Release
cd runtime-testsuite
mvn -Dparallel=classes -DthreadCount=2 -Dtest=csharp.** test
cd ..
