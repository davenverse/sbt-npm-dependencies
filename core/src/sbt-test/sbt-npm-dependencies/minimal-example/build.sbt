enablePlugins(NpmDependenciesPlugin)

name := "minimal-example"

libraryDependencies += "org.http4s" %%% "http4s-ember-server" % "1.0.0-M24"

Test / test := {
  val out = (Compile / npmTransitiveDependencies).value
  println(out)
}