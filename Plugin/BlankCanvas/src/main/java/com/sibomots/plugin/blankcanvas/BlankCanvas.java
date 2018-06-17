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
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <p> BlankCanvas Plugin </p>
 *
 *
 */
@Plugin(id = "blankcanvas",
        version = "1.0.0",
        name = "BlankCanvas",
        url = "http://siboroc.net",
        description = "BlankCanvas Time!",
        authors = {"sibomots"},
        dependencies = @Dependency(id = "otherplugin", optional = true))
public class BlankCanvas {
    public static BlankCanvas instance;
    public static final String MOD_ID = "BlankCanvas";

    @Inject
    public PluginContainer pluginContainer;

    public BlankCanvas() {
        instance = this;
    }

    @Inject private Game game;
    @Inject private Logger logger;

    // Event listeners

    @Listener
    public void onInitialization(GameInitializationEvent event) {
         // configuration
         this.setupPluginConfiguration();
    }

    @Listener
    public void onPostInitialization(GamePostInitializationEvent event) {
         // TODO
    }

    @Listener
    public void onAboutToStart(GameAboutToStartServerEvent event) {
        // TODO
    }

    @Listener
    public void onServerStarted(GameStartedServerEvent event) {
        // TODO
    }


    /// Utilities

    private void setupPluginConfiguration() {
        try {
            Configuration.getInstance().invalidate();
        } catch (ConfigurationLoadingException e) {
            LogMessage("The initialization of the plugin failed. Check the configuration now.");
        }
    }

    public static void LogMessage(String msg) {
        BlankCanvas.instance.logger.info(msg);
    }
}
