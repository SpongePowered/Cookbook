// In scala we use sbt to build our stuff instead of gradle.

name := "ScalaFireball"
version := "1.2"
organization := "org.spongepowered"

scalaVersion := "2.12.4"

//We say we use the spongyinfo sbt plugin, in the project/plugins.sbt file, and then enable it here
enablePlugins(SpongePlugin)
spongeApiVersion := "7.0.0" //spongyinfo automatically adds the sponge api dependency

// The annotation processor which creates the mcmod.info file for java doesn't work for scala.
// As such we need to create it ourself. The spongyinfo plugin simplifies this by letting
// use set it's values in the sbt file, and then generates it for us.
// These values are set to reasonable defaults, but it's good practice to set them explicitly.
spongePluginInfo := PluginInfo(
  id = "scalafireballs",
  name = Some("ScalaFireballs"),
  version = Some("1.1")
)

// spongyinfo automatically includes the assembly plugin which we can use to shade scala
assemblyShadeRules := Seq(
  // It's good practice to relocate your shaded Scala library so it won't
  // conflict with other shaded Scala libraries
  ShadeRule.rename("scala.**" -> "org.spongepowered.cookbook.plugin.shadescala").inAll
)