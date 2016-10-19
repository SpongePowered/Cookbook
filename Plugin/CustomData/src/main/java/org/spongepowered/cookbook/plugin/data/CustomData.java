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

import static org.spongepowered.api.data.DataQuery.of;
import static org.spongepowered.api.data.key.KeyFactory.makeSingleKey;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Optional;
import java.util.UUID;

public class CustomData extends AbstractData<CustomData, ImmutableCustomData> {

    // TypeTokens needed for creating Keys (can be created inline too)
    private static TypeToken<UUID> TT_UUID = new TypeToken<UUID>() {};
    private static TypeToken<Value<UUID>> TTV_UUID = new TypeToken<Value<UUID>>() {};
    private static TypeToken<String> TT_String = new TypeToken<String>() {};
    private static TypeToken<Value<String>> TTV_String = new TypeToken<Value<String>>() {};

    // Keys for this custom data
    public static Key<Value<UUID>> MY_ID = makeSingleKey(TT_UUID, TTV_UUID, of("ID"), "cookbook:customdata:id", "ID");
    public static Key<Value<String>> MY_STRING = makeSingleKey(TT_String, TTV_String, of("String"), "cookbook:customdata:string", "String");

    // Live Data in this instance
    private UUID myID;
    private String myString;

    // For DataBuilder and personal use
    public CustomData() {
    }

    public CustomData(UUID myID, String myString) {
        this.myID = myID;
        this.myString = myString;
        registerGettersAndSetters();
    }

    @Override
    protected void registerGettersAndSetters() {
        // Getter, Setter and ValueGetter for MY_ID
        registerFieldGetter(MY_ID, CustomData.this::getMyID);
        registerFieldSetter(MY_ID, CustomData.this::setMyID);
        registerKeyValue(MY_ID, CustomData.this::myData);
        // Getter, Setter and ValueGetter for MY_STRING
        registerFieldGetter(MY_STRING, CustomData.this::getMyString);
        registerFieldSetter(MY_STRING, CustomData.this::setMyString);
        registerKeyValue(MY_STRING, CustomData.this::myString);
    }

    // Create immutable version of this
    @Override
    public ImmutableCustomData asImmutable() {
        return new ImmutableCustomData(this.myID, this.myString);
    }

    // Fill data using DataHolder and MergeFunction
    @Override
    public Optional<CustomData> fill(DataHolder dataHolder, MergeFunction overlap) {
        Optional<UUID> myID = dataHolder.get(MY_ID);
        Optional<String> myString = dataHolder.get(MY_STRING);
        // Only apply if the custom data is present
        if (myID.isPresent() && myString.isPresent()) {
            CustomData data = this.copy();
            data.myID = myID.get();
            data.myString = myString.get();

            // merge data
            data = overlap.merge(this, data);
            if (data != this) {
                this.myID = data.myID;
                this.myString = data.myString;
            }

            return Optional.of(this);
        }
        return Optional.empty();
    }

    // Fill data using DataContainer
    @Override
    public Optional<CustomData> from(DataContainer container) {
        Optional<UUID> myID = container.getObject(MY_ID.getQuery(), UUID.class);
        Optional<String> myString = container.getString(MY_STRING.getQuery());
        // Only apply if the custom data is present
        if (myID.isPresent() && myString.isPresent()) {
            this.myID = myID.get();
            this.myString = myString.get();
            return Optional.of(this);
        }
        return Optional.empty();
    }

    // Create copy of this
    @Override
    public CustomData copy() {
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
        return this.myString == null ? "" : this.myString;
    }

    // Setters

    private void setMyID(UUID myID) {
        this.myID = myID;
    }

    private void setMyString(String myString) {
        this.myString = myString;
    }

    // ValueGetters

    private Value<UUID> myData() {
        return Sponge.getRegistry().getValueFactory().createValue(MY_ID, this.myID);
    }

    private Value<String> myString() {
        return Sponge.getRegistry().getValueFactory().createValue(MY_STRING, getMyString());
    }
}
