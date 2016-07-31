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
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.EntityUniverse;

import java.util.Optional;

@Plugin(id = "ca.sapon.smite",
        name = "Smite",
        version = "1.0",
        description = "Use me on your enemies")
public class Smite {

    private static final int MAX_DISTANCE = 100;
    @Inject
    private PluginContainer container;

    @Listener(order = Order.POST)
    public void onInteract(InteractBlockEvent.Primary event, @First Player player) {
        // Check if the player is hitting with an ender eye and has the permissions
        final Optional<ItemStack> optionalStack = player.getItemInHand(HandTypes.MAIN_HAND);
        if (!optionalStack.isPresent() || optionalStack.get().getItem().getType() != ItemTypes.ENDER_EYE ||
                !player.hasPermission("smite.use")) {
            return;
        }
        // Get the first block hit
        final BlockRay.BlockRayBuilder<World> blockRay = BlockRay.from(player);
        final Optional<BlockRayHit<World>> optionalBlockHit = blockRay
                .filter(BlockRay.maxDistanceFilter(blockRay.position(), MAX_DISTANCE))
                .filter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1))
                .end();
        // Get the first entity hit
        final Optional<EntityUniverse.EntityHit> optionalEntityHit = player.getWorld()
                .getIntersectingEntities(player, MAX_DISTANCE, hit -> hit.getEntity() != player)
                .stream().reduce((a, b) -> a.getDistance() < b.getDistance() ? a : b);
        // Find the closest of the block or entity hit (if we had any hit at all)
        final Vector3d closest;
        if (optionalBlockHit.isPresent() && optionalBlockHit.get().mapBlock(World::getBlockType) != BlockTypes.AIR) {
            if (optionalEntityHit.isPresent()) {
                final Vector3d blockHitPosition = optionalBlockHit.get().getPosition();
                if (blockHitPosition.distance(blockRay.position()) < optionalEntityHit.get().getDistance()) {
                    closest = blockHitPosition;
                } else {
                    closest = optionalEntityHit.get().getIntersection();
                }
            } else {
                closest = optionalBlockHit.get().getPosition();
            }
        } else {
            if (optionalEntityHit.isPresent()) {
                closest = optionalEntityHit.get().getIntersection();
            } else {
                return;
            }
        }
        // Smite!
        final Optional<Entity> optionalEntity = player.getWorld().createEntity(EntityTypes.LIGHTNING, closest);
        optionalEntity.ifPresent(entity -> player.getWorld().spawnEntity(entity,
                Cause.source(EntitySpawnCause.builder().entity(entity).type(SpawnTypes.PLUGIN).build()).build().merge(event.getCause())));
    }

}
