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

import java.lang
import java.util.Optional

import scala.collection.mutable

import org.spongepowered.api.Sponge
import org.spongepowered.api.data.`type`.HandTypes
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.EntityTypes
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.projectile.explosive.fireball.LargeFireball
import org.spongepowered.api.entity.projectile.{Projectile, Snowball}
import org.spongepowered.api.event.block.InteractBlockEvent
import org.spongepowered.api.event.cause.entity.spawn.{SpawnCause, SpawnTypes}
import org.spongepowered.api.event.cause.{Cause, NamedCause}
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.event.game.state.GameStartingServerEvent
import org.spongepowered.api.event.{Listener, Order}
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.plugin.{Plugin, PluginContainer}
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.cookbook.plugin.ScalaFireball.optionalToOption

import com.flowpowered.math.vector.Vector3d
import com.google.inject.Inject

object ScalaFireball {
  val NoPermission = Text.of(TextColors.RED,
    "Hey! you don't have permission for large fireballs... NOOB!!")

  private def getVelocity(player: Player, multiplier: Double): Vector3d = {
    val yaw = (player.getRotation.getY + 90) % 360
    val pitch = player.getRotation.getX * -1

    val pitchRad = Math.toRadians(pitch)
    val yawRad = Math.toRadians(yaw)

    val rotYCos = Math.cos(pitchRad)
    val rotYSin = Math.sin(pitchRad)
    val rotXCos = Math.cos(yawRad)
    val rotXSin = Math.sin(yawRad)
    new Vector3d((multiplier * rotYCos) * rotXCos, multiplier * rotYSin,
      (multiplier * rotYCos) * rotXSin)
  }

  def optionalToOption[A](optional: Optional[A]): Option[A] =
    if(optional.isPresent) Some(optional.get) else None
}

@Plugin(id = "ScalaFireballs", name = "ScalaFireballs", version = "1.2")
class ScalaFireball {
  private val fireballMap = mutable.WeakHashMap[Projectile, Vector3d]()

  @Inject
  private val container: PluginContainer = null

  @Listener
  def onStarting(event: GameStartingServerEvent): Unit = {
    val runnable: Runnable = new Runnable {
      def run(): Unit = fireballMap.foreach {
        case (projectile, velocity) => projectile.offer(Keys.VELOCITY, velocity)
      }
    }

    Sponge.getScheduler.createTaskBuilder
      .execute(runnable)
      .intervalTicks(1)
      .submit(this.container)
  }

  @Listener(order = Order.POST)
  def onInteract(event: InteractBlockEvent, @First player: Player) {
    val heldOption = optionalToOption(player.getItemInHand(HandTypes.MAIN_HAND))
      .orElse(optionalToOption(player.getItemInHand(HandTypes.OFF_HAND)))

    heldOption.foreach { heldItem =>
      heldItem.getItem match {
        case ItemTypes.STICK => spawnFireball(player)
        case ItemTypes.BLAZE_ROD =>
          if (player.hasPermission("simplefireball.large"))
            spawnLargeFireball(player)
          else player.sendMessage(ScalaFireball.NoPermission)
        case _ =>
      }
    }
  }

  private def spawnFireball(player: Player): Unit = {
    val world = player.getWorld
    val location = player.getLocation.getPosition.add(
      Math.cos((player.getRotation.getY - 90) % 360) * 0.2,
      1.8,
      Math.sin((player.getRotation.getY - 90) % 360) * 0.2
    )

    world.createEntity(EntityTypes.SNOWBALL, location) match {
      case snowball: Snowball =>
        val velocity = ScalaFireball.getVelocity(player, 1.5D)
        snowball.offer(Keys.VELOCITY, velocity)

        snowball.setShooter(player)
        snowball.offer[lang.Double](Keys.ATTACK_DAMAGE, 4D)
        snowball.offer[lang.Integer](Keys.FIRE_TICKS, 100000)
        val spawnCause = SpawnCause.builder()
          .`type`(SpawnTypes.PROJECTILE)
          .build()
        val cause = Cause.builder()
          .suggestNamed(NamedCause.THROWER, player)
          .suggestNamed("SpawnCause", spawnCause)
          .build()
        world.spawnEntity(snowball, cause)
      case _ =>
    }
  }

  private def spawnLargeFireball(player: Player): Unit = {
    val world = player.getWorld
    val location = player.getLocation.getPosition.add(0D, 1.8D, 0D)

    world.createEntity(EntityTypes.FIREBALL, location) match {
      case fireball: LargeFireball =>
        val velocity = ScalaFireball.getVelocity(player, 1.5D)

        fireball.offer(Keys.VELOCITY, velocity)

        fireball.setShooter(player)
        fireball.offer(Keys.ATTACK_DAMAGE, 8D: lang.Double)
        fireball.offer[lang.Integer](Keys.FIRE_TICKS, 100000: lang.Integer)
        fireball.offer(Keys.EXPLOSION_RADIUS, Optional.of(3: lang.Integer))
        val spawnCause = SpawnCause.builder()
          .`type`(SpawnTypes.PROJECTILE)
          .build()
        val cause = Cause.builder()
          .suggestNamed(NamedCause.THROWER, player)
          .suggestNamed("SpawnCause", spawnCause)
          .build()
        world.spawnEntity(fireball, cause)
        fireballMap.put(fireball, velocity)
      case _ =>
    }
  }
}
