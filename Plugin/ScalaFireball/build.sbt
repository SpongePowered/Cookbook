// In scala we use sbt to build our stuff instead of gradle.

name := "ScalaFireball"
version := "1.2"
organization := "org.spongepowered"

scalaVersion := "2.12.4"

resolvers += "SpongePowered" at "http://repo.spongepowered.org/maven"
libraryDependencies += "org.spongepowered" % "spongeapi" % "7.0.0"

assemblyShadeRules := Seq(
  // It's good practice to relocate your shaded Scala library so it won't
  // conflict with other shaded Scala libraries
  ShadeRule.rename("scala.**" -> "org.spongepowered.cookbook.plugin.shadescala").inAll
)