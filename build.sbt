// See README.md for license details.


ThisBuild / scalaVersion     := "2.13.14"
ThisBuild / version          := "3.2.0"


lazy val root = (project in file("."))
  .settings(
    name := "chiselwatt",
    addCompilerPlugin(
      "org.chipsalliance" % "chisel-plugin" % "6.4.3-tywaves-SNAPSHOT" cross CrossVersion.full
    ),
    libraryDependencies ++= Seq(
      "org.chipsalliance" %% "chisel" % "6.4.3-tywaves-SNAPSHOT",
      "com.github.rameloni" %% "tywaves-chisel-api" % "0.4.2-SNAPSHOT",
      "edu.berkeley.cs" %% "chiseltest" % "6.0.0" % "test"
    ),
    scalacOptions ++= Seq(
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit"
    ),
  )
