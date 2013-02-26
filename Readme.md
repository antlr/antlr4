# C# target for ANTLR 4

## Introduction

This document gives a basic overview of using the C# target for ANTLR 4
with C# projects in Visual Studio.

### Available Targets

Currently the only target provided by this package is the `CSharp` target,
which is specified by passing the `-Dlanguage=CSharp` option to the
ANTLR 4 tool when the parsers are generated. This target is build against
the *.NET Framework version 4.5*, and due to its use of
[`IReadOnlyList<T>`](http://msdn.microsoft.com/en-us/library/hh192385.aspx)
is not currently compatible with earlier releases.

### Visual Studio Support for ANTLR 4 Grammars

Currently there is no Visual Studio extension specifically designed for editing
ANTLR 4 grammars. However, now that a C# target for ANTLR 4 is available an
editor should be available in the "reasonably near" future. If this feature is
particularly important to your team,
[Tunnel Vision Labs' Sponsored Development Program](http://tunnelvisionlabs.com/SponsoredDevelopment.pdf)
may be a cost effective option for reducing the release timeframe.

### Base Project Layout

    C:\dev\CoolTool\
      CoolProject\
        CoolProject.csproj
      CoolTool.sln

### Adding ANTLR to the Project Structure

1. Download the current C# release from the following location: **TODO**
2. Extract the files to `C:\dev\CoolTool\Reference\Antlr4`

After these steps, your folder should resemble the following.

    C:\dev\CoolTool\
      CoolProject\...
      Reference\
        Antlr4\
          Antlr4.targets
          Antlr4BuildTasks.dll
          antlr4-csharp-[version]-complete.jar
      CoolTool.sln

### MSBuild Support for ANTLR

Since the steps include manual modification of the Visual Studio project files,
I *very strongly* recommend you back up your project (or commit it to source control)
before attempting these steps.

1. Open the `CoolTool.sln` solution in Visual Studio
2. Unload the `CoolProject` project by right-clicking the project in Solution
   Explorer and selecting Unload Project
3. Open `CoolProject.csproj` for editing by right-clicking the unloaded project
   in Solution Explorer and selecting Edit CoolProject.csproj
4. For reference, locate the following line

        <Import Project="$(MSBuildBinPath)\Microsoft.CSharp.targets"/>

   Note: the line appears as follows in some projects

        <Import Project="$(MSBuildToolsPath)\Microsoft.CSharp.targets"/>

5. After the line in step 4, add the following code

        <PropertyGroup>
          <!-- Folder containing Antlr4BuildTasks.dll -->
          <AntlrBuildTaskPath>$(ProjectDir)..\Reference\Antlr4</AntlrBuildTaskPath>
          <!-- Path to the ANTLR Tool itself. -->
          <AntlrToolPath>$(ProjectDir)..\Reference\Antlr4\antlr4-csharp-4.0.1-SNAPSHOT-complete.jar</AntlrToolPath>
        </PropertyGroup>
        <Import Project="$(ProjectDir)..\Reference\Antlr4\Antlr4.targets" /> 

6. Save and close `CoolProject.csproj`
7. Reload the CoolProject project by right-clicking the project in Solution
   Explorer and selecting Reload Project

### Adding a Reference to the C# Runtime

In the CoolProject project, add a reference to `Antlr4.Runtime.dll`,  which is
located at `C:\dev\CoolTool\Reference\Antlr4\Antlr4.Runtime.dll`.

## Grammars

*TODO*

## Custom Token Specifications (*.tokens)

*TODO*

## Generated Code

*TODO*

## Extra Features in the C# Target

*TODO*

## Example Grammars

*TODO*

