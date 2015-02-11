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
package org.spongepowered.cookbook.plugin.configdatabase;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.util.event.Subscribe;
import org.spongepowered.api.service.config.DefaultConfig;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import com.google.inject.Inject;


@Plugin(id = ConfigDatabase.NAME, name = "SpongePlots", version = "0,1")
public class ConfigDatabase {

    public static final String NAME = "ConfigDatabase";
    private Game game;
    private ConfigurationNode config = null;
    private Optional<PluginContainer> pluginContainer;
    private static Logger logger;
    public ConfigDatabase() {
    }

    public static Logger getLogger() {
        return logger;
    }

    @Inject
    @DefaultConfig(sharedRoot = true)
    private File defaultConfig;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader configManager;

    public File getDefaultConfig() {
        return defaultConfig;
    }

    public ConfigurationLoader getConfigManager() {
        return configManager;
    }

    @Subscribe
    public void onPreInitialization(PreInitializationEvent event) {

        game = event.getGame();
        pluginContainer = game.getPluginManager().getPlugin(ConfigDatabase.NAME);
        logger = game.getPluginManager().getLogger(pluginContainer.get());

        ConfigDatabase.getLogger().info(String.format("[%s]: Starting up da SpongePlots.", ConfigDatabase.NAME));
        try {
            if (!getDefaultConfig().exists()) {
                getDefaultConfig().createNewFile();
                config = getConfigManager().load();

                config.getNode("ConfigVersion").setValue(1);
                config.getNode("DB", "Host").setValue("127.0.0.1");
                config.getNode("DB", "Port").setValue(3306);
                config.getNode("DB", "Username").setValue("SpongePlots");
                config.getNode("DB", "Password").setValue("YouReallyShouldChangeMe");
                config.getNode("DB", "Configured").setValue(0);
                getConfigManager().save(config);
				getLogger().info("[SpongePlots]: Created default configuration, SpongePlots will not run until you have edited this file!");
           }
           config = getConfigManager().load();
        } catch (IOException exception) {
            getLogger().error("Couldn't create default configuration file!");
        }
        int version = config.getNode("version").getInt();
        getLogger().info("Configfile version is now " + version + ".");
    }

    @Subscribe
    public void onServerStart(ServerStartedEvent event) {
        // Here it is!
        getLogger().info(String.format("[%s]: Up and running.", ConfigDatabase.NAME));
        DatabaseUtils.getConnection(config);
        getLogger().info(String.format("[%s]: I called DB for a connection.", ConfigDatabase.NAME));
    }
}


