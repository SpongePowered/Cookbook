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
import org.spongepowered.api.event.state.InitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.util.event.Subscribe;
import org.spongepowered.api.service.config.DefaultConfig;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import com.google.inject.Inject;


@Plugin(id = ConfigDatabase.NAME, name = "ConfigDatabase", version = "0,1")
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

    /* This is where we begin initializing our plugin.
     * The PreInitialization phase is appropriate for reading in (or creating) our config files, but
     * the server is in an early state of starting up yet, so don't do more than that.
     * See https://docs.spongepowered.org/en/latest/plugins/plugin-lifecycle/ for more information on
     * what you can do (And what is appropriate to do) in each stage.
     */
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
								/* This is a simple configuration node living in the 'top' path of your config file.
								 * Here, you can put global configuration variables, such as a version number (for detecting
								 * whether you need to integrate new changes to your config file structure into an existing
								 * config file)
								 */
                config.getNode("ConfigVersion").setValue(1);
                /* This is a set of pathed config nodes, living in the DB 'folder' of the config file.
                 * Splitting your config variables like this produces sections to the config file, making sure
                 * all config variables relating to eachother can be grouped together.
                 * Also useful for passing only parts of your config to other modules; see below.
                 */
                config.getNode("DB", "Host").setValue("127.0.0.1");
                config.getNode("DB", "Port").setValue(3306);
                config.getNode("DB", "Username").setValue("SpongePlots");
                config.getNode("DB", "Password").setValue("YouReallyShouldChangeMe");
                config.getNode("DB", "Configured").setValue(0);
                getConfigManager().save(config);
				getLogger().info("[ConfigDatabase]: Created default configuration, ConfigDatabase will not run until you have edited this file!");
           }
           config = getConfigManager().load();
        } catch (IOException exception) {
            getLogger().error("[ConfigDatabase]: Couldn't create default configuration file!");
        }
        /* This is how you would perform in-place upgrades to your config file, populating fields that might not exist
         * in config files from previous versions of your plugin.
         * On the first run of this plugin, the config file would be generated (as version 1) and then
         * immediately upgraded to version 2. The next time you run the module, version 3 upgrades would be appplied.
         * After that, you should be getting 'Configuration file is current' messages on load.
         */
        int version = config.getNode("version").getInt();
        getLogger().info("[ConfigDatabase]: Configfile version is " + version + ".");
        switch(version){
        	case 1:
        		getLogger().info("[ConfigDatabase]: Adding config file version 2 fields");
        		config.getNode("version2","foo").setValue("bar");
        		break;
        	case 2:
        		getLogger().info("[ConfigDatabase]: Upgrading config file to version 3.");
        		config.getNode("version3","baz").setValue("bar");
        		break;
        	default:
        		getLogger().info("[ConfigDatabase]: Configuration file is current.");
        	}
        		
    }
		/*
		 * Here, we are further along in the server start process, and we can begin doing more initialization of our
		 * plugin, such as connecting to our database backend, load any further assets we want, or initialize commands.
		 * Again, see the plugin lifetime documentation on which events you should be subscribing to.
		 */
    @Subscribe
    public void onInitialization(InitializationEvent event) {
 			Connection db;
 			// This is a sanity check you should always do. Don't try to connect to an unconfigured database.
			if(config.getNode("DB","configured").getBoolean()!=true){
				getLogger().info("[ConfigDatabase] You haven't configured the database. Exiting.");
				return; /* We should probably do more 'formal' shutdown on the mod here. */
			}
			getLogger().info("[ConfigDatabase]: In InitializationEvent.");
			/* This is an example of calling another method with only parts of the config, rather than passing
			 * the entire config, of which maybe 3-4 parameters will be used. In this case, only the attributes
			 * in the "DB" section of the config is passed to DB.getConnection.
			 * Note that here, we do not actually assign a connection to the variable db, in case you are just
			 * testing this. If you have a mySQL database handy, add "db =" in front of DB.getConnection.
			 */
			DB.getConnection(config.getNode("DB"));
			getLogger().info("[ConfigDatabase]: I should now have a database connection.");
		}
}


