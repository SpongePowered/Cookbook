package org.spongepowered.cookbook.myhomes.data;

import org.spongepowered.cookbook.myhomes.data.home.Home;
import com.google.common.reflect.TypeToken;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.data.value.mutable.MapValue;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Keys {

    public static final Key<Value<Home>> DEFAULT_HOME = KeyFactory.makeSingleKey(
            TypeToken.of(Home.class),
            new TypeToken<Value<Home>>() {
            },
            DataQuery.of("DefaultHome"), "myhomes:default_home", "Default Home");

    public static final Key<MapValue<String, Home>> HOMES = KeyFactory.makeMapKey(
            new TypeToken<Map<String, Home>>() {
            },
            new TypeToken<MapValue<String, Home>>() {
            },
            DataQuery.of("Homes"), "myhomes:homes", "Homes");

    public static final Key<ListValue<UUID>> FRIENDS = KeyFactory.makeListKey(
            new TypeToken<List<UUID>>() {
            },
            new TypeToken<ListValue<UUID>>() {
            },
            DataQuery.of("Friends"), "myhomes:friends", "Friends");
}
