dotnet build runtime/CSharp/src/Antlr4.csproj -c Release
dotnet pack runtime/CSharp/src/Antlr4.csproj -c Release
cd runtime-testsuite
export MAVEN_OPTS="-Xmx8g"
mvn -Dparallel=classes -DthreadCount=2 -Dtest=csharp.** test
cd ..
