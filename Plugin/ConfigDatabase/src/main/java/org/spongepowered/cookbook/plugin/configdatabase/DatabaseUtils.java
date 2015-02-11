/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered.org <http://www.spongepowered.org>
 * Copyright (c) Kenneth Aalberg, other contributors
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

import java.io.IOException;
import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ninja.leaping.configurate.ConfigurationNode;

public class DatabaseUtils {

    public static void getConnection(ConfigurationNode config) {
        Connection mycon;
        mycon = null;
        /* This shows we have gotten the configuration variables we need to make a SQL connection from
         * the main class. Note that we don't actually do anything to connect to the database here,
         * and we don't want to return a null Connection, so this method has been made to return a void
         * instead. If you want to actually attach a mysql DB, change the method to 
         * public static Connection getConnection(ConfigurationNode config) above, and return "mycon"
         * instead of just return.
         */
        ConfigDatabase.getLogger().info("[ConfigDatabase/DB]: I got the following variables from my parent class: "+config.getChildrenMap());
				
				return;
    }
}

