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

import com.flowpowered.math.vector.Vector3d
import com.google.inject.Inject
import java.util.Optional
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.projectile.explosive.fireball.LargeFireball
import org.spongepowered.api.entity.projectile.{Projectile, Snowball}
import org.spongepowered.api.entity.{Entity, EntityTypes}
import org.spongepowered.api.event.{Order, Listener}
import org.spongepowered.api.event.block.InteractBlockEvent
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.game.state.GameStartingServerEvent
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.plugin.{Plugin, PluginContainer}
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.Text
import org.spongepowered.api.world.World

object ScalaFireball {
  val NoPermission = Text.of(TextColors.RED,
    "Hey! you don't have permission for large fireballs... NOOB!!")

  private def getVelocity(player: Player, multiplier: Double): Vector3d = {
    val yaw: Double = (player.getRotation.getY + 90) % 360
    val pitch: Double = player.getRotation.getX * -1
    val rotYCos: Double = Math.cos(Math.toRadians(pitch))
    val rotYSin: Double = Math.sin(Math.toRadians(pitch))
    val rotXCos: Double = Math.cos(Math.toRadians(yaw))
    val rotXSin: Double = Math.sin(Math.toRadians(yaw))
    new Vector3d((multiplier * rotYCos) * rotXCos, multiplier * rotYSin, (multiplier * rotYCos) * rotXSin)
  }
}

@Plugin(id = "ScalaFireballs", name = "ScalaFireballs", version = "1.1")
class ScalaFireball {
  private final val fireballMap: java.util.WeakHashMap[Projectile, Vector3d] =
    new java.util.WeakHashMap[Projectile, Vector3d]
  @Inject
  private val container: PluginContainer = null

  @Listener
  def onStarting(event: GameStartingServerEvent) {
    Sponge.getScheduler.createTaskBuilder
      .execute(new FireballUpdater)
      .intervalTicks(1)
      .submit(this.container)
  }

  @Listener(order = Order.POST)
  def onInteract(event: InteractBlockEvent, @First player: Player) {
    val option: Optional[ItemStack] = player.getItemInHand
    if (option.isPresent) {
      option.get.getItem match {
        case ItemTypes.STICK => spawnFireball(player)
        case ItemTypes.BLAZE_ROD =>
          if (player.hasPermission("simplefireball.large"))
            spawnLargeFireball(player)
          else
            player.sendMessage(ScalaFireball.NoPermission)
        case _ =>

      }
    }
  }

  private def spawnFireball(player: Player) {
    val world: World = player.getWorld
    val optional: Optional[Entity] = world.createEntity(EntityTypes.SNOWBALL,
      player.getLocation.getPosition
        .add(Math.cos((player.getRotation.getY - 90)
        % 360) * 0.2,
          1.8,
          Math.sin((player.getRotation.getY - 90) % 360) * 0.2))
    if (optional.isPresent) {
      val velocity: Vector3d = ScalaFireball.getVelocity(player, 1.5D)
      optional.get.offer(Keys.VELOCITY, velocity)
      val fireball: Snowball = optional.get.asInstanceOf[Snowball]
      fireball.setShooter(player)
      fireball.offer[java.lang.Double](Keys.ATTACK_DAMAGE, 4)
      world.spawnEntity(fireball, Cause.of(player))
      fireball.offer[java.lang.Integer](Keys.FIRE_TICKS, 100000)
    }
  }

  private def spawnLargeFireball(player: Player) {
    val world: World = player.getWorld
    val optional: Optional[Entity] = world.createEntity(EntityTypes.FIREBALL,
      player.getLocation.getPosition.add(0, 1.8, 0))
    if (optional.isPresent) {
      val velocity: Vector3d = ScalaFireball.getVelocity(player, 1.5D)
      optional.get.offer(Keys.VELOCITY, velocity)
      val fireball: LargeFireball = optional.get.asInstanceOf[LargeFireball]
      fireball.setShooter(player)
      // TODO This vanished after Data API was introduced
      // fireball.setExplosionPower(3)
      fireball.offer[java.lang.Double](Keys.ATTACK_DAMAGE, 8)
      world.spawnEntity(fireball, Cause.of(player))
      fireballMap.put(fireball, velocity)
    }
  }

  class FireballUpdater extends Runnable {
    def run() {
      import scala.collection.JavaConversions._
      for (entry <- fireballMap.entrySet) {
        entry.getKey.offer(Keys.VELOCITY, entry.getValue)
      }
    }
  }
}
