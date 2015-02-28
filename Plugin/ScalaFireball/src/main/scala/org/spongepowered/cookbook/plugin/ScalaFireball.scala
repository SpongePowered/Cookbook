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
import com.google.common.base.Optional
import com.google.inject.Inject
import org.spongepowered.api.entity.player.Player
import org.spongepowered.api.entity.projectile.fireball.LargeFireball
import org.spongepowered.api.entity.projectile.{Projectile, Snowball}
import org.spongepowered.api.entity.{Entity, EntityTypes}
import org.spongepowered.api.event.entity.living.player.PlayerInteractEvent
import org.spongepowered.api.event.state.ServerStartingEvent
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.plugin.{Plugin, PluginContainer}
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.message.Messages
import org.spongepowered.api.util.event.{Order, Subscribe}
import org.spongepowered.api.world.World

object ScalaFireball {
  private def getVelocity(player: Player, multiplier: Double): Vector3d = {
    val yaw: Double = (player.getRotation.getX + 90) % 360
    val pitch: Double = player.getRotation.getY * -1
    val rotYCos: Double = Math.cos(Math.toRadians(pitch))
    val rotYSin: Double = Math.sin(Math.toRadians(pitch))
    val rotXCos: Double = Math.cos(Math.toRadians(yaw))
    val rotXSin: Double = Math.sin(Math.toRadians(yaw))
    new Vector3d((multiplier * rotYCos) * rotXCos, multiplier * rotYSin, (multiplier * rotYCos) * rotXSin)
  }
}

@Plugin(id = "ScalaFireballs", name = "ScalaFireballs", version = "1.0")
class ScalaFireball {
  private final val fireballMap: java.util.WeakHashMap[Projectile, Vector3d] =
    new java.util.WeakHashMap[Projectile, Vector3d]
  @Inject
  private val container: PluginContainer = null

  @Subscribe
  def onStarting(event: ServerStartingEvent) {
    event.getGame.getSyncScheduler.runRepeatingTask(container, new
        FireballUpdater, 1)
  }

  @Subscribe(order = Order.POST)
  def onInteract(event: PlayerInteractEvent) {
    val player: Player = event.getPlayer
    val option: Optional[ItemStack] = player.getItemInHand
    if (option.isPresent) {
      option.get.getItem match {
        case ItemTypes.STICK => spawnFireball(player)
        case ItemTypes.BLAZE_ROD =>
          if (player.hasPermission("simplefireball.large"))
            spawnLargeFireball(player)
          else
            player.sendMessage(Messages.of("Hey! you don't have "
              + "permission for large fireballs... NOOB!!")
              .builder.color(TextColors.RED).build)
        case _ =>

      }
    }
  }

  private def spawnFireball(player: Player) {
    val world: World = player.getWorld
    val optional: Optional[Entity] = world.createEntity(EntityTypes.SNOWBALL,
      player.getLocation.getPosition
        .add(Math.cos((player.getRotation.getX - 90)
        % 360) * 0.2,
          1.8,
          Math.sin((player.getRotation.getX - 90) % 360) * 0.2))
    if (optional.isPresent) {
      val velocity: Vector3d = ScalaFireball.getVelocity(player, 1.5D)
      optional.get.setVelocity(velocity)
      val fireball: Snowball = optional.get.asInstanceOf[Snowball]
      fireball.setShooter(player)
      fireball.setDamage(4)
      world.spawnEntity(fireball)
      fireball.setFireTicks(100000)
    }
  }

  private def spawnLargeFireball(player: Player) {
    val world: World = player.getWorld
    val optional: Optional[Entity] = world.createEntity(EntityTypes.FIREBALL,
      player.getLocation.getPosition.add(0, 1.8, 0))
    if (optional.isPresent) {
      val velocity: Vector3d = ScalaFireball.getVelocity(player, 1.5D)
      optional.get.setVelocity(velocity)
      val fireball: LargeFireball = optional.get.asInstanceOf[LargeFireball]
      fireball.setShooter(player)
      fireball.setExplosionPower(3)
      fireball.setDamage(8)
      world.spawnEntity(fireball)
      fireballMap.put(fireball, velocity)
    }
  }

  class FireballUpdater extends Runnable {
    def run() {
      import scala.collection.JavaConversions._
      for (entry <- fireballMap.entrySet) {
        entry.getKey.setVelocity(entry.getValue)
      }
    }
  }
}