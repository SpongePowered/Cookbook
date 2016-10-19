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

import com.google.inject.Inject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.cookbook.plugin.data.CustomData;
import org.spongepowered.cookbook.plugin.data.CustomDataBuilder;
import org.spongepowered.cookbook.plugin.data.ImmutableCustomData;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Plugin(id = "cookbook_customdata",
        name = "Custom Data",
        version = "1.0",
        description = "How to use custom data")
public class CustomDataPlugin {

    @Inject private Logger logger;

    @Listener
    public void init(GameInitializationEvent event) {
        // Register your Data, ImmutableData and DataBuilder in GameInitializationEvent
        Sponge.getDataManager().register(CustomData.class, ImmutableCustomData.class, new CustomDataBuilder());
        logger.info("Custom Data will happen!");
    }

    /**
     * Sets the last message as custom data on the Item in the players hand
     */
    @Listener
    public void onChat(MessageChannelEvent.Chat event, @First Player player) {
        // Get copy of item in hand
        Optional<ItemStack> item = player.getItemInHand(HandTypes.MAIN_HAND);
        if (item.isPresent()) {
            // if present check for data using the key
            Optional<String> msg = item.get().get(CustomData.MY_STRING);
            if (msg.isPresent()) { // if present show message:
                player.sendMessage(Text.of("Last chat-message while this item was in hand: " + msg.get()));
            }

            // finally offer last message as CustomData to ItemStack
            item.get().offer(new CustomData(UUID.randomUUID(), event.getRawMessage().toPlain()));
            // Don't forget to set item back on the player
            player.setItemInHand(HandTypes.MAIN_HAND, item.get());
            // Info for player:
            player.sendMessage(ChatTypes.ACTION_BAR, Text.of("Custom Data Saved"));
        }
    }
}
