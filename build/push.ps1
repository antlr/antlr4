. .\version.ps1

If ($AntlrVersion.EndsWith('-dev')) {
	$host.ui.WriteErrorLine("Cannot push development version '$AntlrVersion' to NuGet.")
	Exit 1
}

$packages = @(
	'Antlr4.Runtime'
	'Antlr4'
	'Antlr4.VS2008')

# Make sure all packages exist before pushing any packages
ForEach ($package in $packages) {
	If (-not (Test-Path ".\nuget\$package.$AntlrVersion.nupkg")) {
		$host.ui.WriteErrorLine("Couldn't locate NuGet package: $JarPath")
		exit 1
	}

	If (-not (Test-Path ".\nuget\$package.$AntlrVersion.symbols.nupkg")) {
		$host.ui.WriteErrorLine("Couldn't locate NuGet symbols package: $JarPath")
		exit 1
	}
}

$nuget = '..\runtime\CSharp\.nuget\NuGet.exe'
ForEach ($package in $packages) {
	&$nuget 'push' ".\nuget\$package.$AntlrVersion.nupkg"
}
