package io.chrisdavenport.sbt.npmdependencies
package sbtplugin

import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.sbtplugin.{ScalaJSPlugin, Stage}
import sbt.Keys._
import sbt.{Def, _}

object NpmDependenciesPlugin extends AutoPlugin {

  val npmPackageDirectory = "npm-dependencies"

  override def trigger = noTrigger

  override def requires = org.scalajs.sbtplugin.ScalaJSPlugin  &&
    plugins.JvmPlugin

  object autoImport {
        /**
      * List of the NPM packages (name and version) your application depends on.
      * You can use [semver](https://docs.npmjs.com/misc/semver) versions:
      *
      * {{{
      *   npmDependencies in Compile += "uuid" -> "~3.0.0"
      * }}}
      *
      * Note that this key must be scoped by a `Configuration` (either `Compile` or `Test`).
      *
      * @group settings
      */
    val npmDependencies: SettingKey[Seq[(String, String)]] =
      settingKey[Seq[(String, String)]]("NPM dependencies (libraries that your program uses)")

    /** @group settings */
    val npmDevDependencies: SettingKey[Seq[(String, String)]] =
      settingKey[Seq[(String, String)]]("NPM dev dependencies (libraries that the build uses)")

    val npmTransitiveDependencies: TaskKey[Map[String, Seq[(String, String)]]] = 
      taskKey[Map[String, Seq[(String, String)]]]("Calculates all scala.js transitive dependencies")

    val npmTransitiveDevDependencies: TaskKey[Map[String, Seq[(String, String)]]] = 
      taskKey[Map[String, Seq[(String, String)]]]("Calculates all scala.js transitive dev dependencies")
  }
  import autoImport._

  override def globalSettings: Seq[Setting[_]] = Seq(
  )

  override def projectSettings: Seq[Setting[_]] = Seq(
    // Include the manifest in the produced artifact
    (Compile / products) := (Compile / products).dependsOn(scalaJSManifest).value,
  ) ++ 
    inConfig(Compile)(perConfigSettings ++ compileTransitiveSettings) ++
    inConfig(Test)(perConfigSettings ++ testSettings ++ testTransitiveSettings)


  override def buildSettings: Seq[Setting[_]] = Seq(
  )


  private lazy val perConfigSettings: Seq[Def.Setting[_]] =
    Def.settings(
      npmDependencies := Seq.empty,
      npmDevDependencies := Seq.empty,
      // Uncertain if this will pick up the right value, but trying it.
      
    )

  private lazy val compileTransitiveSettings: Seq[Setting[_]] = Def.settings(
    npmTransitiveDependencies := {
      val deps = NpmDependencies.collectFromClasspath(fullClasspath.value)
      deps.toList.map{ case (s, dep) => (s -> dep.compileDependencies)}.toMap
    },

    npmTransitiveDevDependencies := {
      val deps = NpmDependencies.collectFromClasspath(fullClasspath.value)
      deps.toList.map{ case (s, dep) => (s -> dep.compileDevDependencies)}.toMap
    }
  )

  private lazy val testTransitiveSettings: Seq[Setting[_]] = Def.settings(
    npmTransitiveDependencies := {
      val deps = NpmDependencies.collectFromClasspath(fullClasspath.value)
      deps.toList.map{ case (s, dep) => (s -> (dep.compileDependencies ++ dep.testDependencies))}.toMap
    },

    npmTransitiveDevDependencies := {
      val deps = NpmDependencies.collectFromClasspath(fullClasspath.value)
      deps.toList.map{ case (s, dep) => (s -> (dep.compileDevDependencies ++ dep.testDevDependencies))}.toMap
    }
  )

  private lazy val testSettings: Seq[Setting[_]] =
    Def.settings(
      npmDependencies ++= (Compile / npmDependencies).value,
      npmDevDependencies ++= (Compile / npmDevDependencies).value,
    )

  /**
  * Writes the scalajs manifest file.
  */
  private lazy val scalaJSManifest: Def.Initialize[Task[File]] =
    Def.task {
      NpmDependencies.writeManifest(
        NpmDependencies(
          (Compile / npmDependencies).value.to[List],
          (Test / npmDependencies).value.to[List],
          (Compile / npmDevDependencies).value.to[List],
          (Test / npmDevDependencies).value.to[List]
        ),
        (Compile / classDirectory).value
      )
    }
}