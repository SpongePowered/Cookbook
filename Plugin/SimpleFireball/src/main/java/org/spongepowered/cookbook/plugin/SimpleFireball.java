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

package org.spongepowered.cookbook.plugin;

import com.flowpowered.math.vector.Vector3d;
import com.google.inject.Inject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.Snowball;
import org.spongepowered.api.entity.projectile.explosive.fireball.LargeFireball;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

@Plugin(id = "com.afterkraft.SimpleFireball",
        name = "SimpleFireball",
        version = "1.1")
public class SimpleFireball {

    private final WeakHashMap<Projectile, Vector3d> fireballMap = new
            WeakHashMap<Projectile, Vector3d>();
    @Inject
    private PluginContainer container;

    private static Vector3d getVelocity(Player player, double multiplier) {
        double yaw = ((player.getRotation().getY() + 90) % 360);
        double pitch = ((player.getRotation().getX()) * -1);
        double rotYCos = Math.cos(Math.toRadians(pitch));
        double rotYSin = Math.sin(Math.toRadians(pitch));
        double rotXCos = Math.cos(Math.toRadians(yaw));
        double rotXSin = Math.sin(Math.toRadians(yaw));
        return new Vector3d((multiplier * rotYCos) * rotXCos,
                multiplier * rotYSin, (multiplier * rotYCos) * rotXSin);
    }

    @Listener
    public void onStarting(GameStartingServerEvent event) {
        Sponge.getScheduler().createTaskBuilder()
                .execute(new FireballUpdater())
                .intervalTicks(1)
                .submit(this.container);
    }

    private static final Text NO_PERMISSION = Text.of(TextColors.RED,
            "Hey! you don't have permission for large fireballs... NOOB!!");

    @Listener(order = Order.POST)
    public void onInteract(InteractBlockEvent event, @First Player player) {
        Optional<ItemStack> option = player.getItemInHand();

        if (option.isPresent()) {
            ItemType itemType = option.get().getItem();
            if (itemType.equals(ItemTypes.STICK)) {
                spawnFireball(player);
            } else if (itemType.equals(ItemTypes.BLAZE_ROD)) {
                if (player.hasPermission("simplefireball.large")) {
                    spawnLargeFireball(player);
                } else {
                    player.sendMessage(NO_PERMISSION);
                }
            }
        }
    }

    private void spawnFireball(Player player) {
        World world = player.getWorld();
        Optional<Entity> optional = world.createEntity(EntityTypes.SNOWBALL,
                player.getLocation().getPosition().add(Math.cos((player
                                .getRotation().getY() - 90) % 360) * 0.2,
                        1.8, Math.sin((player
                                .getRotation().getY() - 90) % 360) * 0.2));

        if (optional.isPresent()) {
            Vector3d velocity = getVelocity(player, 1.5D);
            optional.get().offer(Keys.VELOCITY, velocity);
            Snowball fireball = (Snowball) optional.get();
            fireball.setShooter(player);
            fireball.offer(Keys.ATTACK_DAMAGE, 4D);
            world.spawnEntity(fireball, Cause.of(player));
            fireball.offer(Keys.FIRE_TICKS, 100000);
        }
    }

    private void spawnLargeFireball(Player player) {
        World world = player.getWorld();
        Optional<Entity> optional = world.createEntity(EntityTypes.FIREBALL,
                player.getLocation().getPosition().add(0, 1.8, 0));

        if (optional.isPresent()) {
            Vector3d velocity = getVelocity(player, 1.5D);
            optional.get().offer(Keys.VELOCITY, velocity);
            LargeFireball fireball = (LargeFireball) optional.get();
            fireball.setShooter(player);
            // TODO This vanished after the Data API was introduced
            // fireball.setExplosionPower(3);
            fireball.offer(Keys.ATTACK_DAMAGE, 8D);
            world.spawnEntity(fireball, Cause.of(player));
            this.fireballMap.put(fireball, velocity);
        }
    }

    private class FireballUpdater implements Runnable {

        @Override
        public void run() {
            for (Map.Entry<Projectile, Vector3d> entry : SimpleFireball.this.fireballMap
                    .entrySet()) {
                entry.getKey().offer(Keys.VELOCITY, entry.getValue());
            }

        }
    }

}