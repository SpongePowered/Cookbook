/*
 * This file is part of BlankCanvas, licensed under the BSD 3-Clause License
 *
 * Copyright (c) SiboRoc <http://siboroc.net>
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.sibomots.plugin.blankcanvas;

import com.google.inject.Inject;
import com.sibomots.plugin.blankcanvas.exceptions.ConfigurationLoadingException;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.config.DefaultConfig;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.sibomots.plugin.blankcanvas.BlankCanvas.LogMessage;

/**
 * <p>Blank Canvas Plugin</p>
 * 
 * <p>Sample plugin for loading default configuration file.</p>
 */
public class Configuration {

    // File description
    private static final String CONFIGFILE = "SampleConfiguration.conf";

    // Nodes
    public static final String CONFIGURATION_VERSION_NODE = "ConfigurationVersion";

    private ConfigurationNode config = null;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path defaultConfig = Paths.get(CONFIGFILE);

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    // Singleton
    private static Configuration ourInstance = new Configuration();
    public static Configuration getInstance() {
        return ourInstance;
    }

    private Configuration() {
    }

    // Methods
    public Path getDefaultConfig() {
        return this.defaultConfig;
    }

    public ConfigurationLoader<CommentedConfigurationNode> getConfigManager() {
        return this.configManager;
    }

    private void dumpValues() {
        try {
            this.config = this.getConfigManager().load();
        } catch (IOException e) {
            LogMessage("Unable to get configuration manager");
        }
    }


    public void invalidate() throws ConfigurationLoadingException {
        File realConfigFile = this.getDefaultConfig().toFile();
        boolean realConfigFileExists = realConfigFile.exists();

        try {
            if (realConfigFileExists) {
                // maybe the file is bogus. We're looking for markers to see if the file
                // is legitimate

                this.config = this.getConfigManager().load();
                String version = this.config.getNode(Configuration.CONFIGURATION_VERSION_NODE).getString();

                if (version == null) {
                    // likely this is not a valid config file. assume it isn't.
                    realConfigFile.delete();
                    realConfigFileExists = false;
                }
            }
        } catch (IOException e) {
            LogMessage("Failure trying to load existing configuration file.");
            throw new ConfigurationLoadingException("The configuration file was not loaded. Cannot test validity of existing configuration file.");
        }

        try {
            // Is there a real configuration file present in config dir?
            if ( !realConfigFileExists ) {
                // No? Make the file
                realConfigFile.createNewFile();

                // Since this file is new and we have a default in the plugin, let's transfer values from
                // the default (plugin file version) into the real file we just created.
                URL jarConfigFile = this.getClass().getResource(CONFIGFILE);

                // The jar-file version (the defaults) -- make a loader for it.
                ConfigurationLoader<CommentedConfigurationNode>
                        defaultLoader = HoconConfigurationLoader.builder().setURL(jarConfigFile).build();

                // load it.
                ConfigurationNode defaultNode = defaultLoader.load();

                // Meanwhile, load the configuration bound on the real file
                this.config = this.getConfigManager().load();

                // Save the defaults into the real config file bound to the real file's loader
                this.getConfigManager().save(defaultNode);

            }

            this.config = this.getConfigManager().load();
        } catch (IOException e) {
            LogMessage("Could not create default configuration");
            throw new ConfigurationLoadingException("The plugin failed to create and initialize a new configuration file on first run.");
        }

        int version = this.config.getNode("ConfigVersion").getInt();
        LogMessage("Config version: " + version );
        LogMessage("onPostInit");
    }
}
