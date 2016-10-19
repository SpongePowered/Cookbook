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

package org.spongepowered.cookbook.plugin.data;

import static org.spongepowered.cookbook.plugin.data.CustomData.MY_ID;
import static org.spongepowered.cookbook.plugin.data.CustomData.MY_STRING;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

import java.util.UUID;

public class ImmutableCustomData extends AbstractImmutableData<ImmutableCustomData, CustomData> {

    private UUID myID;
    private String myString;

    public ImmutableCustomData(UUID myID, String myString) {
        this.myID = myID;
        this.myString = myString;
    }

    @Override
    protected void registerGetters() {
        // Getter and ValueGetter for MY_ID
        registerFieldGetter(MY_ID, this::getMyID);
        registerKeyValue(MY_ID, this::myData);
        // Getter and ValueGetter for MY_STRING
        registerFieldGetter(MY_STRING, this::getMyString);
        registerKeyValue(MY_STRING, this::myString);
    }

    // Create mutable version of this
    @Override
    public CustomData asMutable() {
        return new CustomData(this.myID, this.myString);
    }

    // Content Version (may be used for updating custom data later)
    @Override
    public int getContentVersion() {
        return 1;
    }

    // !IMPORANT! Override toContainer and set your custom data
    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(MY_ID, getMyID()).set(MY_STRING, getMyString());
    }

    // Getters

    private UUID getMyID() {
        return this.myID;
    }

    private String getMyString() {
        return this.myString;
    }

    // Value Getters

    private ImmutableValue<UUID> myData() {
        return Sponge.getRegistry().getValueFactory().createValue(MY_ID, this.myID).asImmutable();
    }

    private ImmutableValue<String> myString() {
        return Sponge.getRegistry().getValueFactory().createValue(MY_STRING, this.myString).asImmutable();
    }
}
