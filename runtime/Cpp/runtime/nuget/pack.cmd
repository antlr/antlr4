echo off
rem echo Usage:
rem echo ------
rem echo pack vsvers version [pre]      // pack 2019 4.9.1 -beta
rem echo ------
setlocal enableextensions enabledelayedexpansion

if "%1"=="" goto usage
if "%2"=="" goto usage
set PRE=%3
set PLATFORM=Win32

rem -version ^^[16.0^^,17.0^^)
set VS_VERSION=vs%1
rem  should be set "VSWHERE='%ProgramFiles(x86)%\Microsoft Visual Studio\Installer\vswhere.exe  -property installationPath -version ^[16.0^,17.0^)'"
if %VS_VERSION%==vs2019 (
  set "VSWHERE='C:\PROGRA~2\"Microsoft Visual Studio"\Installer\vswhere.exe  -latest -property installationPath -version ^[16.0^,17.0^)'"
) else (
if %VS_VERSION%==vs2022 (
  set "VSWHERE='C:\PROGRA~2\"Microsoft Visual Studio"\Installer\vswhere.exe  -latest -property installationPath -version ^[17.0^,18.0^)'"
)
)
for /f " delims=" %%a in (%VSWHERE%) do @set "VSCOMNTOOLS=%%a"

echo ============= %VSCOMNTOOLS% =============

if %VS_VERSION%==vs2019 (
  set VS_VARSALL=..\..\VC\Auxiliary\Build\vcvarsall.bat
  set "VS160COMNTOOLS=%VSCOMNTOOLS%\Common7\Tools\"
) else (
  if %VS_VERSION%==vs2022 (
    set VS_VARSALL=..\..\VC\Auxiliary\Build\vcvarsall.bat
    set "VS170COMNTOOLS=%VSCOMNTOOLS%\Common7\Tools\"
  ) else (
    set VS_VARSALL=..\..\VC\vcvarsall.bat
  )
)

if not defined VCINSTALLDIR (
  if %VS_VERSION%==vs2019 (
    if %PLATFORM%==x64 (
      call "%VS160COMNTOOLS%%VS_VARSALL%" x86_amd64 8.1
    ) else (
      call "%VS160COMNTOOLS%%VS_VARSALL%" x86 8.1
    )
  ) else (
    if %VS_VERSION%==vs2022 (
      if %PLATFORM%==x64 (
        call "%VS170COMNTOOLS%%VS_VARSALL%" x86_amd64 8.1
      ) else (
        call "%VS170COMNTOOLS%%VS_VARSALL%" x86 8.1
      )
    )
  )
)

if not defined VSINSTALLDIR (
  echo Error: No Visual cpp environment found.
  echo Please run this script from a Visual Studio Command Prompt
  echo or run "%%VSnnCOMNTOOLS%%\vsvars32.bat" first.
  goto :buildfailed
)


pushd ..\
call msbuild antlr4cpp-vs%1.vcxproj -t:rebuild -p:Platform=Win32 	-p:Configuration="Debug DLL"
call msbuild antlr4cpp-vs%1.vcxproj -t:rebuild -p:Platform=Win32 	-p:Configuration="Release DLL"
call msbuild antlr4cpp-vs%1.vcxproj -t:rebuild -p:Platform=Win32 	-p:Configuration="Debug Static"
call msbuild antlr4cpp-vs%1.vcxproj -t:rebuild -p:Platform=Win32 	-p:Configuration="Release Static"
call msbuild antlr4cpp-vs%1.vcxproj -t:rebuild -p:Platform=x64 		-p:Configuration="Debug DLL"
call msbuild antlr4cpp-vs%1.vcxproj -t:rebuild -p:Platform=x64 		-p:Configuration="Release DLL"
call msbuild antlr4cpp-vs%1.vcxproj -t:rebuild -p:Platform=x64 		-p:Configuration="Debug Static"
call msbuild antlr4cpp-vs%1.vcxproj -t:rebuild -p:Platform=x64 		-p:Configuration="Release Static"
popd

del *nupkg
echo nuget pack ANTLR4.Runtime.cpp.noarch.nuspec 				-p vs=%1 -p version=%2 -p pre=%pre%
call nuget pack ANTLR4.Runtime.cpp.noarch.nuspec 				-p vs=%1 -p version=%2 -p pre=%pre%
echo nuget pack ANTLR4.Runtime.cpp.shared.nuspec 	-symbols 	-p vs=%1 -p version=%2 -p pre=%pre%
call nuget pack ANTLR4.Runtime.cpp.shared.nuspec 	-symbols 	-p vs=%1 -p version=%2 -p pre=%pre%
echo nuget pack ANTLR4.Runtime.cpp.static.nuspec 	-symbols 	-p vs=%1 -p version=%2 -p pre=%pre%
call nuget pack ANTLR4.Runtime.cpp.static.nuspec 	-symbols 	-p vs=%1 -p version=%2 -p pre=%pre%

goto exit
:usage
echo Usage:
echo ------
echo "pack vsvers version [pre]"      // pack 2019 4.9.1 -beta
echo ------
:exit
:buildfailed
endlocal
rem echo on