import sbt.Keys._

name := "ScalaFireball"

version := "1.1"

name := "ScalaFireball"

organization := "org.spongepowered.cookbook.plugin"

scalaVersion := "2.11.1"

libraryDependencies += "org.spongepowered" % "spongeapi" % "3.0.0"

resolvers += "SpongePowered" at "http://repo.spongepowered.org/maven"

lazy val scalaFireball = Project( "ScalaFireball", file("."))


