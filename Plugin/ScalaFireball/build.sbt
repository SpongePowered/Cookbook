name := "ScalaFireball"
version := "1.2"
organization := "org.spongepowered.cookbook.plugin"

scalaVersion := "2.11.8"

resolvers += "SpongePowered" at "https://repo.spongepowered.org/maven"
libraryDependencies += "org.spongepowered" % "spongeapi" % "5.0.0-SNAPSHOT" % Provided

assemblyShadeRules in assembly := Seq(
	ShadeRule.rename("scala.**" -> "org.spongepowered.cookbook.plugin.shade.scala.@1").inAll
)