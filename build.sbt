ThisBuild / tlBaseVersion := "0.1" // current series x.y

ThisBuild / organization := "io.chrisdavenport"
ThisBuild / organizationName := "Christopher Davenport"
ThisBuild / startYear := Some(2022)
ThisBuild / licenses := Seq(License.MIT)
ThisBuild / developers := List(
  tlGitHubDev("christopherdavenport", "Christopher Davenport")
)

ThisBuild / tlCiReleaseBranches := Seq()

// sbt plugins build against the sbt 1.x Scala version only.
ThisBuild / crossScalaVersions := Seq("2.12.20")
ThisBuild / scalaVersion := "2.12.20"

// Compiler settings DavenversePlugin injected globally; sbt-typelevel-ci-release
// does not supply them. -Ypartial-unification is needed for cats mapN on 2.12.
ThisBuild / scalacOptions += "-Ypartial-unification"
ThisBuild / libraryDependencies ++= Seq(
  compilerPlugin("org.typelevel" % "kind-projector" % "0.13.4" cross CrossVersion.full),
  compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
)

val scalaJSVersion = sys.env.getOrElse("SCALAJS_VERSION", "1.22.0")

ThisBuild / versionScheme := Some("early-semver")

ThisBuild / githubWorkflowBuild := Seq(
  WorkflowStep.Sbt(List("test"))
)

val catsV = "2.6.1"
val catsEffectV = "3.1.1"
val fs2V = "3.0.6"
val circeV = "0.14.1"


// Projects
lazy val `sbt-npm-dependencies` = project.in(file("."))
    .enablePlugins(NoPublishPlugin)
  .aggregate(core)

lazy val core = project.in(file("core"))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-npm-dependencies",
    scriptedBufferLog := false,
    scriptedLaunchOpts ++= Seq(
    "-Dplugin.version=" + version.value,
    s"-Dscalajs.version=$scalaJSVersion",
    "-Dsbt.execute.extrachecks=true" // Avoid any deadlocks.
    ),
    test := {
      (Test / test).value
      scripted.toTask("").value
    },
    addSbtPlugin("org.scala-js" % "sbt-scalajs" % scalaJSVersion),
    libraryDependencies ++= Seq(
      "io.circe"                    %% "circe-core"                 % circeV,
      "io.circe"                    %% "circe-parser"               % circeV,
    )
  )