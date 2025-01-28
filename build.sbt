lazy val Scala212 = "2.12.20"
lazy val scalatestVersion = SettingKey[String]("scalatestVersion")

lazy val root = (project in file(".")).settings(
  organization := "org.scalatra.scalate",
  name := "scalamd",
  version := "1.8.0",
  scalaVersion := Scala212,
  crossScalaVersions := Seq(Scala212, "2.11.12", "2.10.7", "2.13.16", "3.3.5"),
  transitiveClassifiers in Global := Seq(Artifact.SourceClassifier),
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
  scalatestVersion := "3.2.19",
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest-wordspec" % scalatestVersion.value % Test,
    "org.scalatest" %% "scalatest-shouldmatchers" % scalatestVersion.value % Test,
  ),
  publishMavenStyle := true,
  publishTo := sonatypePublishTo.value,
  pomIncludeRepository := { x => false },
  pomExtra := <url>https://github.com/scalatra/scalamd/</url>
  <licenses>
    <license>
      <name>Apache License, Version 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:scalatra/scalamd.git</url>
    <connection>scm:git:git@github.com:scalatra/scalamd.git</connection>
  </scm>
  <developers>
    <developer>
      <id>rossabaker</id>
      <name>Ross A. Baker</name>
      <url>http://www.rossabaker.com/</url>
    </developer>
    <developer>
      <id>seratch</id>
      <name>Kazuhiro Sera</name>
      <url>https://github.com/seratch</url>
    </developer>
  </developers>
)
