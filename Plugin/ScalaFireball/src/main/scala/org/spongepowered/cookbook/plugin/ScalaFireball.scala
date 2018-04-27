/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered.org <http://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.spongepowered.cookbook.plugin

import java.util.Optional

import scala.annotation.tailrec

import com.flowpowered.math.vector.Vector3d
import com.google.inject.Inject
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.`type`.HandTypes
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.EntityTypes
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.projectile.explosive.fireball.LargeFireball
import org.spongepowered.api.entity.projectile.{Projectile, Snowball}
import org.spongepowered.api.event.block.InteractBlockEvent
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.event.game.state.GameStartingServerEvent
import org.spongepowered.api.event.{Listener, Order}
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.plugin.{Plugin, PluginContainer}
import org.spongepowered.api.text.Text
import scala.collection.mutable

// In Scala we don't have static. Instead we have singleton objects. When one
// wants something similar to static, one places a class and an object together.
// The object is then called the companion object and gets special treatment.
object ScalaFireball {

  private def getVelocity(player: Player, multiplier: Double): Vector3d = {
    // When using Scala, you don't have to specify the types for values, or
    // use semicolons at the end of statements. It's still recommended to
    // put the type annotation for public values though.

    val yaw = (player.getRotation.getY + 90) % 360
    val pitch = player.getRotation.getX * -1
    val rotYCos = Math.cos(Math.toRadians(pitch))
    val rotYSin = Math.sin(Math.toRadians(pitch))
    val rotXCos = Math.cos(Math.toRadians(yaw))
    val rotXSin = Math.sin(Math.toRadians(yaw))
    new Vector3d((multiplier * rotYCos) * rotXCos, multiplier * rotYSin, (multiplier * rotYCos) * rotXSin)
  }

  // In scala you can define extra methods on an existing class using what's
  // called implicit classes. Here we define a method on Optional to convert
  // it into Scala's own type for representing a value that might be missing
  implicit class RichOptional[A](private val optional: Optional[A]) extends AnyVal {
    def asOption: Option[A] = optional.map(Option.apply(_)).orElse(None)
  }

  // Calling Text.of with a bunch of arguments is so tedious. Let's make it so
  // we can use a String interpolator instead.
  implicit class TextSyntax(private val sc: StringContext) extends AnyVal {

    def t(args: Any*): Text = {
      sc.checkLengths(args)

      // Tail recursive functions are Scala's equivalent of while loops
      @tailrec
      def inner(partsLeft: Seq[String], argsLeft: Seq[Any], res: Seq[AnyRef]): Seq[AnyRef] =
        if (argsLeft == Nil) res
        else {
          inner(partsLeft.tail, argsLeft.tail, (res :+ argsLeft.head.asInstanceOf[AnyRef]) :+ partsLeft.head)
        }

      Text.of(inner(sc.parts.tail, args, Seq(sc.parts.head)): _*)
    }
  }

  // Let's use our new string interpolator. We also import all colors here for nicer
  // syntax.
  import org.spongepowered.api.text.format.TextColors._
  val NoPermission: Text = t"${RED}Hey! you don't have permission for large fireballs... NOOB!!"

}

@Plugin(id = "scalafireballs", name = "ScalaFireballs", version = "1.1")
class ScalaFireball {

  // To use our implicit class we need to import it
  import ScalaFireball.RichOptional

  // Scala has it's own collection library, filled with many collections
  // similar to the ones found in java, in addition to many new ones.
  private val fireballMap: mutable.WeakHashMap[Projectile, Vector3d] = mutable.WeakHashMap.empty[Projectile, Vector3d]

  // In scala when defining a missing value, a missing value (null, 0, and so on)
  // a underscore can be used instead
  @Inject
  private var container: PluginContainer = _

  @Listener
  def onStarting(event: GameStartingServerEvent): Unit = {
    Sponge.getScheduler.createTaskBuilder
      .execute(new FireballUpdater)
      .intervalTicks(1)
      .submit(this.container)
  }

  @Listener(order = Order.POST)
  def onInteract(event: InteractBlockEvent, @First player: Player): Unit = {
    val optItem = player.getItemInHand(HandTypes.MAIN_HAND).asOption
      .orElse(player.getItemInHand(HandTypes.OFF_HAND).asOption)

    // In scala we almost never check if an option is defined. Instead we
    // either treat it like a list with a single element, or we do
    // use pattern matching on it.
    optItem.foreach { stack =>

      // In scala you can perform tests on a value and perform code based on
      // the result of that test. It's kind of like an if expression, except
      // that you're not limited to testing against a boolean expression.
      // You can test against the type, if it's a specific value (as we do here),
      // you can extract values as is common to do with Option, and more.
      stack.getType match {
        case ItemTypes.STICK => spawnFireball(player)
        // We can also combine these tests. Here we test if it's a specific
        // value, and then test if the player has the needed permissions.
        case ItemTypes.BLAZE_ROD if player.hasPermission("simplefireball.large") =>
          spawnLargeFireball(player)
        case ItemTypes.BLAZE_ROD => player.sendMessage(ScalaFireball.NoPermission)
        case _ =>
        // We need to add a base case at the end if we haven't covered all
        // possible values. If you don't do this you get an exception.
        // Don't worry though. The compiler will tell you if you
        // missed anything in some cases where it can tell what all the
        // possible values to match are.
      }
    }
  }

  // Having to remember to pop our stack frames is boring. Let's create e
  // helper method for that.

  def usingCauseStackFrame[A, B](cause: A)(use: => B): B = {
    // Here we use a call-by-name parameter, which means that it's not
    // evaluated until we use it. Think of it kind of like a Supplier.
    Sponge.getCauseStackManager.pushCause(cause)
    val res = use
    Sponge.getCauseStackManager.popCause()
    res
  }

  private def spawnFireball(player: Player): Unit = {
    val world = player.getWorld
    val entity = world.createEntity(
      EntityTypes.SNOWBALL,
      player.getLocation.getPosition.add(
        Math.cos((player.getRotation.getY - 90) % 360) * 0.2,
        1.8,
        Math.sin((player.getRotation.getY - 90) % 360) * 0.2
      )
    )

    val velocity = ScalaFireball.getVelocity(player, 1.5D)
    entity.offer(Keys.VELOCITY, velocity)

    // You normally don't do any casting in Scala. Instead you use pattern
    // matching on the values.
    entity match {
      case fireball: Snowball =>
        fireball.setShooter(player)
        fireball.offer(Keys.ATTACK_DAMAGE, Double.box(4D))
        usingCauseStackFrame(player) {
          world.spawnEntity(fireball)
        }
        fireball.offer(Keys.FIRE_TICKS, Int.box(100000))
      case _ =>
    }
  }

  private def spawnLargeFireball(player: Player) {
    val world = player.getWorld
    val entity = world.createEntity(EntityTypes.FIREBALL, player.getLocation.getPosition.add(0, 1.8, 0))
    val velocity = ScalaFireball.getVelocity(player, 1.5D)
    entity.offer(Keys.VELOCITY, velocity)

    entity match {
      case fireball: LargeFireball =>
        fireball.setShooter(player)
        fireball.offer(Keys.EXPLOSION_RADIUS, Optional.of(Int.box(3)))
        fireball.offer(Keys.ATTACK_DAMAGE, Double.box(8))
        usingCauseStackFrame(player) {
          world.spawnEntity(fireball)
        }
        fireballMap.put(fireball, velocity)
      case _ =>
    }
  }

  class FireballUpdater extends Runnable {
    def run(): Unit = {
      for ((projectile, speed) <- fireballMap) {
        projectile.offer(Keys.VELOCITY, speed)
      }
    }
  }
}
