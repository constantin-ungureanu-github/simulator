sbt clean

// Run simulator for 100 ticks with 1.000 cells and 100.000 subscribers
sbt "run 100 1000 100000"

sbt eclipse:clean eclipse

sbt gen-idea:clean gen-idea

sbt stage universal:packageBin

./target/universal/stage/bin/simulator 100 1000 100000
