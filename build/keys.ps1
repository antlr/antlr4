# Note: these values may only change during minor release
$Keys = @{
	'net20' = '7983ae52036899ac'
	'net30' = '7671200403f6656a'
	'net35-cf' = '770a97458f51159e'
	'net35-client' = '4307381ae04f9aa7'
	'net40-client' = 'bb1075973a9370c4'
	'net45' = 'edc21c04cf562012'
	'netcore45' = 'e4e9019902d0b6e2'
	'portable-net40' = '90bf14da8e1462b4'
	'portable-net45' = '3d23c8e77559f391'
}

function Resolve-FullPath() {
	param([string]$Path)
	[System.IO.Path]::GetFullPath((Join-Path (pwd) $Path))
}
