#!/bin/sh

# Build a .NET 3.5 compatible DLL using mono
# This step can be done by the `dotnet` cli once https://github.com/Microsoft/msbuild/issues/1333 is resolved.
echo "Step 1: Building .NET 3.5 DLL"
xbuild /p:Configuration=Release Antlr4.mono.sln

# Build a .NET core DLL using the `dotnet` cli from microsoft
echo "Step 2: Building .NET Core DLL"
dotnet restore Antlr4.dotnet.sln
dotnet build -c Release -f netstandard1.3 Antlr4.dotnet.sln

echo "Step 3: Packaging both DLLs into a single nuget package"
nuget pack Package.nuspec
