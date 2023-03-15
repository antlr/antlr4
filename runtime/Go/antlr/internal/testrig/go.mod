module testrig

go 1.20

replace github.com/antlr/antlr4/runtime/Go/antlr/v4 => ../../v4

require (
	github.com/antlr/antlr4/runtime/Go/antlr/v4 v4.0.0-20230305170008-8188dc5388df
	github.com/pyroscope-io/client v0.6.0
)

require (
	github.com/pyroscope-io/godeltaprof v0.1.0 // indirect
	golang.org/x/exp v0.0.0-20220722155223-a9213eeb770e // indirect
)
