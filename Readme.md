sbt clean

// Run simulator for 100 ticks with 10.000 cells and 1.000.000 subscribers
sbt "run 100 10000 1000000"

sbt eclipse:clean eclipse

sbt gen-idea:clean gen-idea

sbt stage universal:packageBin

./target/universal/stage/bin/simulator 100 10000 1000000

