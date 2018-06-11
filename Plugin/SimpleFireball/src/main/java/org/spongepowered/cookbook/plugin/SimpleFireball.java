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
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
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

// The plugin annotation works as the main class of your plugin.
// You setup the various values of the plugin here.
@Plugin(id = "simplefireball",
        name = "SimpleFireball",
        version = "1.2",
        description = "A simple plugin showcasing the creation of fireballs.")
public class SimpleFireball {

    private final WeakHashMap<Projectile, Vector3d> fireballMap = new WeakHashMap<>();
    @Inject
    private PluginContainer container;

    private static Vector3d getVelocity(Player player, double multiplier) {
        double yaw = ((player.getRotation().getY() + 90) % 360);
        double pitch = ((player.getRotation().getX()) * -1);
        double rotYCos = Math.cos(Math.toRadians(pitch));
        double rotYSin = Math.sin(Math.toRadians(pitch));
        double rotXCos = Math.cos(Math.toRadians(yaw));
        double rotXSin = Math.sin(Math.toRadians(yaw));

        double x = (multiplier * rotYCos) * rotXCos;
        double y = multiplier * rotYSin;
        double z = (multiplier * rotYCos) * rotXSin;
        return new Vector3d(x, y, z);
    }

    // A lot of the interaction with Sponge happens when certain stuff happens.
    // These are called Events. If we create a method that takes a subclass of Event,
    // which specifies the specific thing we're waiting for, as the only parameter,
    // and annotate that parameter with @Listener, it will be called whenever that
    // event happens (is fired).
    //
    // Here we call some code whenever the game is starting up.
    @Listener
    public void onStarting(GameStartingServerEvent event) {

        // Here we use the scheduler to call some code later.
        // In this instance we use it to call the code in the execute block
        // every tick.
        // We could also have delayed execution by some amount of ticks,
        // or used real like time units instead.
        Sponge.getScheduler().createTaskBuilder()
                .execute(() -> {
                    for (Map.Entry<Projectile, Vector3d> entry : fireballMap.entrySet()) {
                        entry.getKey().offer(Keys.VELOCITY, entry.getValue());
                    }
                })
                // Tells it how often to call the code.
                // 1 is every tick, 2 is every 2 ticks, 3 is every 3 ticks,
                // and so on.
                .intervalTicks(1)
                // We submit the task, and give it our plugin instance
                .submit(this.container);
    }

    // There are many cases in minecraft where styled text is shown to the player.
    // The most common example is chat. Sponge handles this through the Text class.
    // The simplest way to create a Text object is to use the Text.of factory.
    // Just pass in the styles you want before the text to be styled that way.
    // You can use RESET to get back to the default.
    private static final Text NO_PERMISSION = Text.of(TextColors.RED,
            "Hey! you don't have permission for large fireballs... NOOB!!");

    @Listener(order = Order.POST)
    public void onInteract(InteractBlockEvent event, @First Player player) {
        // Sponge will never give you back a null. Instead if a value can be missing it will
        // give you back an Optional.
        // See https://docs.spongepowered.org/stable/en/plugin/optional/index.html
        // for more info on Optional.
        Optional<ItemStack> option = Optional
                .ofNullable(player.getItemInHand(HandTypes.MAIN_HAND)
                        .orElse(player.getItemInHand(HandTypes.OFF_HAND).orElse(null)));

        if (option.isPresent()) {
            ItemType itemType = option.get().getType();
            if (itemType.equals(ItemTypes.STICK)) {
                spawnFireball(player, false);
            } else if (itemType.equals(ItemTypes.BLAZE_ROD)) {
                if (player.hasPermission("simplefireball.large")) {
                    spawnFireball(player, true);
                } else {
                    player.sendMessage(NO_PERMISSION);
                }
            }
        }
    }

    private void spawnFireball(Player player, boolean isLarge) {
        World world = player.getWorld();
        Vector3d playerRot = player.getRotation();
        Vector3d playerLoc = player.getLocation().getPosition();
        Vector3d spawnLoc = playerLoc.add(Math.cos((playerRot.getY() - 90) % 360) * 0.2, 1.8,
                Math.sin((playerRot.getY() - 90) % 360) * 0.2);

        // Before you can create an entity you need to create it. You can
        // do this with the world.createEntity method. You need to pass in
        // the entity type you want to create.
        EntityType entityType = isLarge ? EntityTypes.FIREBALL : EntityTypes.SNOWBALL;
        Projectile fireball = (Projectile) world.createEntity(entityType, spawnLoc);

        // Once we have our entity we need to set a few values on it.
        // Sponge uses a system inspired by entity system. That means that data
        // and the objects are separated and that theoretically any object can
        // hold any data. To refer to these pieces of data, and set values like
        // we would with setX methods, we use the method offer, which will try
        // to set the value.
        //
        // If you want to see if setting the value succeeded, you can inspect
        // the value returned by offer.
        // We don't care in this case though.
        Vector3d velocity = getVelocity(player, 1.5D);
        double attackDamage = isLarge ? 8D : 4D;
        fireball.offer(Keys.VELOCITY, velocity);
        fireball.offer(Keys.ATTACK_DAMAGE, attackDamage);
        fireball.setShooter(player);

        if(isLarge) {
            fireball.offer(Keys.EXPLOSION_RADIUS, Optional.of(3));
            fireballMap.put(fireball, velocity);
        }
        else {
            fireball.offer(Keys.FIRE_TICKS, 100000);
        }

        // Once we have set up the fireball like we want to, we can spawn it.
        // Because we want the player to show up as the one that spawned the
        // fireball, we need to push the player as a part of the cause. Once
        // we have spawned the fireball, we can pop the player again.
        Sponge.getCauseStackManager().pushCause(player);
        world.spawnEntity(fireball);
        Sponge.getCauseStackManager().popCause();
    }
}