package org.spongepowered.cookbook.plugin;

import com.google.inject.Inject;
import java.io.File;
import java.io.IOException;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.event.state.ServerStartingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.config.DefaultConfig;
import org.spongepowered.api.util.event.Subscribe;

@Plugin(id = "ConfigTest", name = "Test Plugin", version = "1.0")
public class ConfigTest {
    
    @Inject
    Logger logger;

    public Logger getLogger() {
        return logger;
    }
    
    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader configManager;
    
    @Inject
    @DefaultConfig(sharedRoot = true)
    private File defaultConfig;
    
    public File getDefaultConfig() {
        return defaultConfig;
    }

    public ConfigurationLoader getConfigManager() {
        return configManager;
    }
    
    @Subscribe
    public void handler(ServerStartingEvent evt) {
        System.out.println("On ServerStartedEvent");
        
        ConfigurationNode config = null;

        //Create config if it doesn't exist, and set defaults
        try {
            if (!getDefaultConfig().exists()) {
                getDefaultConfig().createNewFile();
                config = getConfigManager().load();

                config.getNode("version").setValue(1);
                config.getNode("doStuff").setValue(true);
                config.getNode("doMoreStuff").setValue(false);
                getConfigManager().save(config);
            }
            config = getConfigManager().load();

        } catch (IOException exception) {
            getLogger().error("The default configuration could not be loaded or created!");
        }
        
        //Get config value
        int version = config.getNode("version").getInt();
        
        //Set config value
        try {
            config.getNode("version").setValue(2);
            getConfigManager().save(config);
        } catch (IOException exception) {
            getLogger().error("The configuration edits could not be saved!");
        }
        
        System.out.println("Ending ServerStartedEvent");
        
    }
}
