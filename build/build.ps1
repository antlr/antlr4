param (
	[switch]$Debug
)

# build the solutions
$SolutionPath = "..\Runtime\CSharp\Antlr4.sln"
$CF35SolutionPath = "..\Runtime\CSharp\Antlr4.VS2008.sln"

# make sure the script was run from the expected path
if (!(Test-Path $SolutionPath)) {
	echo "The script was run from an invalid working directory."
	exit 1
}

. .\version.ps1

If ($Debug) {
	$BuildConfig = 'Debug'
} Else {
	$BuildConfig = 'Release'
}

# this is configured here for path checking, but also in the .props and .targets files
[xml]$pom = Get-Content "..\tool\pom.xml"
$CSharpToolVersionNodeInfo = Select-Xml "/mvn:project/mvn:version" -Namespace @{mvn='http://maven.apache.org/POM/4.0.0'} $pom
$CSharpToolVersion = $CSharpToolVersionNodeInfo.Node.InnerText.trim()

# build the main project
$msbuild = "$env:windir\Microsoft.NET\Framework64\v4.0.30319\msbuild.exe"

&$msbuild '/nologo' '/m' '/nr:false' '/t:rebuild' "/p:Configuration=$BuildConfig" $SolutionPath
if ($LASTEXITCODE -ne 0) {
	echo "Build failed, aborting!"
	exit $p.ExitCode
}

# build the compact framework project
$msbuild = "$env:windir\Microsoft.NET\Framework\v4.0.30319\msbuild.exe"

&$msbuild '/nologo' '/m' '/nr:false' '/t:rebuild' "/p:Configuration=$BuildConfig" $CF35SolutionPath
if ($LASTEXITCODE -ne 0) {
	echo ".NET 3.5 Compact Framework Build failed, aborting!"
	exit $p.ExitCode
}

if (-not (Test-Path 'nuget')) {
	mkdir "nuget"
}

# TODO: Build the Java library using Maven

$JarPath = "..\tool\target\antlr4-csharp-$CSharpToolVersion-complete.jar"
if (!(Test-Path $JarPath)) {
	echo "Couldn't locate the complete jar used for building C# parsers: $JarPath"
	exit 1
}

$packages = @(
	'Antlr4.Runtime'
	'Antlr4'
	'Antlr4.VS2008')

$nuget = '..\runtime\CSharp\.nuget\NuGet.exe'
ForEach ($package in $packages) {
	If (-not (Test-Path ".\$package.nuspec")) {
		$host.ui.WriteErrorLine("Couldn't locate NuGet package specification: $package")
		exit 1
	}

	&$nuget 'pack' ".\$package.nuspec" '-OutputDirectory' 'nuget' '-Prop' "Configuration=$BuildConfig" '-Version' "$AntlrVersion" '-Prop' "M2_REPO=$M2_REPO" '-Prop' "CSharpToolVersion=$CSharpToolVersion" '-Symbols'
}
