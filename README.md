# sbt-npm-dependencies - NPM Dependencies for Scala.js [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.chrisdavenport/sbt-npm-dependencies_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.chrisdavenport/sbt-npm-dependencies_2.12) ![Code of Conduct](https://img.shields.io/badge/Code%20of%20Conduct-Scala-blue.svg)

## Quick Start

To use sbt-npm-dependencies in an existing SBT project with Scala 2.12 or a later version, add the following dependencies to your
`project/plugins.sbt` depending on your needs:

```scala
addSbtPlugin("io.chrisdavenport" %% "sbt-npm-dependencies" % "<version>")
```

In your `build.sbt`, enable it with `enablePlugins(NpmDependenciesPlugin)` and declare your dependencies with

```scala
Compile / npmDependencies ++= Seq(
  "npmPackageName" -> "version",
  "secondNpmPackageName" -> "version",
  ...
)
```

## Internals

(For integration with other build systems)

sbt-npm-dependencies will export a file called `NPM_DEPENDENCIES` to the jar containing the dependencies in the following format:

```js
{
  "compile-dependencies": [
    { "npmPackageName": "version" },
    { "secondNpmPackageName": "version" },
    ...
  ],
  "test-dependencies": [
    { "npmPackageName": "version" },
    { "secondNpmPackageName": "version" },
    ...
  ],
  "compile-devDependencies": [
    { "npmPackageName": "version" },
    { "secondNpmPackageName": "version" },
    ...
  ],
  "test-devDependencies": [
    { "npmPackageName": "version" },
    { "secondNpmPackageName": "version" },
    ...
  ]
}
```

You can access dependency data from upstream packages in your downstream build through `Compile / npmTransitiveDependencies` and `Compile / npmTransitiveDevDependencies`, or by using a bundler plugin like https://github.com/fiatjaf/sbt-esbuild.
