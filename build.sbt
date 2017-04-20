scalaVersion in ThisBuild := "2.12.1"

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies += "ch.epfl.scala" %% "collection-strawman" % "0.2.0-SNAPSHOT"

testOptions in ThisBuild += Tests.Argument(TestFrameworks.JUnit, "-q", "-v", "-s", "-a")

val streams =
  project.in(file("."))
    .settings(
      name := "stream-benchmarks",
      libraryDependencies ++= Seq(
        "com.novocode" % "junit-interface" % "0.11" % Test
      )
    )

enablePlugins(JmhPlugin)

javaOptions in run ++= Seq("-Xms3g",
    "-Xmx3g",
    "-XX:+CMSClassUnloadingEnabled",
    "-XX:ReservedCodeCacheSize=256m",
    "-XX:-TieredCompilation")
