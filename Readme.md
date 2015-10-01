// Clean and Compile

sbt clean compile

// Run simulator for 100 ticks with 1.000 cells and 100.000 subscribers

sbt "run 100 1000 100000" [Java Options]

// Build Eclipse Project

sbt eclipse:clean eclipse

// Build executable package

sbt stage universal:packageBin

// Run executable package

./target/universal/stage/bin/simulator 100 1000 100000  [Java Options]

// Options to JVM

-J-server -J-XX:+UseNUMA -J-XX:+UseCondCardMark -J-XX:-UseBiasedLocking -J-XX:+UseParallelGC -J-Xss4M -J-Xms16G -J-Xmx32G




TODO
v0.4
- separate devices and subscribers
- add movement
- add self scheduling

v0.5
- use futures and pipes

v0.6
- add network elements of the mobile network for GSM, UMTS, LTE
- rework input and initializations
