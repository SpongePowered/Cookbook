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

import java.io.IOException;
import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ninja.leaping.configurate.ConfigurationNode;

public class DatabaseUtils {

    public static void getConnection(ConfigurationNode config) {
        Connection mycon;
        mycon = null;
        ConfigDatabase.getLogger().info(String.format("[%s/DB]: Got called with a %s",
                ConfigDatabase.NAME,config.getClass().getName()));
        /* try {
			int port = getConfigManager()
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */
        ConfigDatabase.getLogger().info("[%s/DB]: Children list: %s",
                ConfigDatabase.NAME, config.getNode("DB").getValue());
    }
}

