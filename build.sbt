name := "simulator"
version := "0.1"
scalaVersion := "2.11.7"
compileOrder := CompileOrder.JavaThenScala
EclipseKeys.withSource := true

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.12"
)

enablePlugins(JavaAppPackaging)
mainClass in Compile := Some("simulator.actors.Simulator")
