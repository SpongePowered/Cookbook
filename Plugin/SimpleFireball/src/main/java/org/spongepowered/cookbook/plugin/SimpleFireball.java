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

import java.util.Map;
import java.util.WeakHashMap;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.Snowball;
import org.spongepowered.api.entity.projectile.explosive.fireball.LargeFireball;
import org.spongepowered.api.event.entity.player.PlayerInteractEvent;
import org.spongepowered.api.event.state.ServerStartingEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.event.Order;
import org.spongepowered.api.util.event.Subscribe;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Optional;
import com.google.inject.Inject;

@Plugin(id = "com.afterkraft.SimpleFireball",
        name = "SimpleFireball",
        version = "1.0")
public class SimpleFireball {

    private final WeakHashMap<Projectile, Vector3d> fireballMap = new
            WeakHashMap<Projectile, Vector3d>();
    @Inject
    private PluginContainer container;

    private static Vector3d getVelocity(Player player, double multiplier) {
        double yaw = ((player.getRotation().getX() + 90) % 360);
        double pitch = ((player.getRotation().getY()) * -1);
        double rotYCos = Math.cos(Math.toRadians(pitch));
        double rotYSin = Math.sin(Math.toRadians(pitch));
        double rotXCos = Math.cos(Math.toRadians(yaw));
        double rotXSin = Math.sin(Math.toRadians(yaw));
        return new Vector3d((multiplier * rotYCos) * rotXCos,
                multiplier * rotYSin, (multiplier * rotYCos) * rotXSin);
    }

    @Subscribe
    public void onStarting(ServerStartingEvent event) {
        event.getGame().getSyncScheduler().runRepeatingTask(container, new
                FireballUpdater(), 1);
    }

    private static final Text NO_PERMISSION = Texts.of(TextColors.RED,
            "Hey! you don't have permission for large fireballs... NOOB!!");

    @Subscribe(order = Order.POST)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
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
                                .getRotation().getX() - 90) % 360) * 0.2,
                        1.8, Math.sin((player
                                .getRotation().getX() - 90) % 360) * 0.2));

        if (optional.isPresent()) {
            Vector3d velocity = getVelocity(player, 1.5D);
            optional.get().setVelocity(velocity);
            Snowball fireball = (Snowball) optional.get();
            fireball.setShooter(player);
            fireball.setDamage(4);
            world.spawnEntity(fireball);
            fireball.setFireTicks(100000);
        }
    }

    private void spawnLargeFireball(Player player) {
        World world = player.getWorld();
        Optional<Entity> optional = world.createEntity(EntityTypes.FIREBALL,
                player.getLocation().getPosition().add(0, 1.8, 0));

        if (optional.isPresent()) {
            Vector3d velocity = getVelocity(player, 1.5D);
            optional.get().setVelocity(velocity);
            LargeFireball fireball = (LargeFireball) optional.get();
            fireball.setShooter(player);
            fireball.setExplosionPower(3);
            fireball.setDamage(8);
            world.spawnEntity(fireball);
            fireballMap.put(fireball, velocity);
        }
    }

    private class FireballUpdater implements Runnable {

        @Override
        public void run() {
            for (Map.Entry<Projectile, Vector3d> entry : fireballMap
                    .entrySet()) {
                entry.getKey().setVelocity(entry.getValue());
            }

        }
    }

}