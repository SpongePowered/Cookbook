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

import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

@Plugin(id = ConfigDatabase.NAME,
        name = "ConfigDatabase",
        version = "0.4",
        description = "Shows how to use configurate to create sort of database.")
public class ConfigDatabase {

    public static final String NAME = "configdatabase";
    private ConfigurationNode config = null;
    @Inject
    private PluginContainer pluginContainer;


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
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    public File getDefaultConfig() {
        return this.defaultConfig;
    }

    public ConfigurationLoader<CommentedConfigurationNode> getConfigManager() {
        return this.configManager;
    }

    // This is where we begin initializing our plugin.
    // The PreInitialization phase is appropriate for reading in (or creating) our config files, but
    // the server is in an early state of starting up yet, so don't do more than that.
    // See https://docs.spongepowered.org/stable/en/plugin/lifecycle.html for more information on
    // what you can do (And what is appropriate to do) in each stage.
    //

    @Listener
    public void onPreInitialization(GamePreInitializationEvent event) {
        logger = this.pluginContainer.getLogger();
        ConfigDatabase.getLogger().info("Starting up da SpongePlots.");

        try {

            if (!getDefaultConfig().exists()) {

                getDefaultConfig().createNewFile();
                this.config = getConfigManager().load();

                // This is a simple configuration node living in the 'top' path of your config file.
                // Here, you can put global configuration variables, such as a version number (for detecting
                // whether you need to integrate new changes to your config file structure into an existing
                // config file)

                this.config.getNode("ConfigVersion").setValue(1);

                // This is a set of pathed config nodes, living in the DB 'folder' of the config file.
                // Splitting your config variables like this produces sections to the config file, making sure
                // all config variables relating to eachother can be grouped together.
                // Also useful for passing only parts of your config to other modules; see below.

                this.config.getNode("DB", "Host").setValue("127.0.0.1");
                this.config.getNode("DB", "Port").setValue(3306);
                this.config.getNode("DB", "Username").setValue("SpongePlots");
                this.config.getNode("DB", "Password").setValue("YouReallyShouldChangeMe");
                this.config.getNode("DB", "Configured").setValue(false);
                getConfigManager().save(this.config);
                getLogger().info("Created default configuration, ConfigDatabase will not run until you have edited this file!");

            }

            this.config = getConfigManager().load();

        } catch (IOException exception) {

            getLogger().error("Couldn't create default configuration file!");

        }

        // This is how you would perform in-place upgrades to your config file, populating fields that might not exist
        // in config files from previous versions of your plugin.
        // On the first run of this plugin, the config file would be generated (as version 1) and then
        // immediately upgraded to version 2. The next time you run the module, version 3 upgrades would be appplied.
        // After that, you should be getting 'Configuration file is current' messages on load.

        int version = this.config.getNode("ConfigVersion").getInt();
        getLogger().info("Configfile version is " + version + ".");

        switch (version) {

            case 1: {
                getLogger().info("Adding config file version 2 fields");
                this.config.getNode("version2", "foo").setValue("bar");
            }

            case 2: {
                getLogger().info("Upgrading config file to version 3.");
                this.config.getNode("version3", "baz").setValue("bar");
            }

            default: {
                this.config.getNode("ConfigVersion").setValue(3);
                getLogger().info("Configuration file is current.");

                try {
                    // Save any changes
                    getConfigManager().save(this.config);
                } catch (IOException e) {
                    getLogger().error("Failed to save config file!", e);
                }
            }
        }
    }

    // Here, we are further along in the server start process, and we can begin doing more initialization of our
    // plugin, such as connecting to our database backend, load any further assets we want, or initialize commands.
    // Again, see the plugin lifetime documentation on which events you should be subscribing to.

    @Listener
    public void onInitialization(GameInitializationEvent event) {
        Connection db;

        // This is a sanity check you should always do. Don't try to connect to an unconfigured database.

        if (this.config.getNode("DB", "Configured").getBoolean() != true) {
            getLogger().info("You haven't configured the database. Exiting.");

            // TODO We should probably do more 'formal' shutdown on the mod here.
            return;
        }

        getLogger().info("In InitializationEvent.");

        // This is an example of calling another method with only parts of the config, rather than passing
        // the entire config, of which maybe 3-4 parameters will be used. In this case, only the attributes
        // in the "DB" section of the config is passed to DB.getConnection.
        // Note that here, we do not actually assign a connection to the variable db, in case you are just
        // testing this. If you have a mySQL database handy, add "db =" in front of DB.getConnection.

        DatabaseUtils.getConnection(this.config.getNode("DB"));
        getLogger().info("I should now have a database connection.");

    }
}

