lazy val Scala212 = "2.12.21"
lazy val scalatestVersion = SettingKey[String]("scalatestVersion")

lazy val root = (project in file(".")).settings(
  organization := "org.scalatra.scalate",
  name := "scalamd",
  version := "1.8.0",
  scalaVersion := Scala212,
  crossScalaVersions := Seq(Scala212, "2.11.12", "2.10.7", "2.13.18", "3.3.8"),
  transitiveClassifiers in Global := Seq(Artifact.SourceClassifier),
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
  scalacOptions ++= {
    if (scalaVersion.value.startsWith("3.3.")) {
      Seq(
        "-release:11",
        "-Yfuture-lazy-vals",
      )
    } else {
      scalaBinaryVersion.value match {
        case "2.12" | "2.13" =>
          Seq("-release:8")
        case _ =>
          Nil
      }
    }
  },
  scalatestVersion := "3.2.20",
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
