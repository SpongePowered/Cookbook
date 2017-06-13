package org.spongepowered.cookbook.myhomes.data.home.impl;

import org.spongepowered.cookbook.myhomes.data.Keys;
import org.spongepowered.cookbook.myhomes.data.home.Home;
import org.spongepowered.cookbook.myhomes.data.home.HomeData;
import org.spongepowered.cookbook.myhomes.data.home.ImmutableHomeData;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.MapValue;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

public class HomeDataImpl extends AbstractData<HomeData, ImmutableHomeData> implements HomeData {

    private Home defaultHome;
    private Map<String, Home> homes;

    public HomeDataImpl(Home defaultHome, Map<String, Home> homes) {
        this.defaultHome = defaultHome;
        this.homes = homes;
    }

    // It's best to provide an empty constructor with "default" values
    public HomeDataImpl() {
        this(null, ImmutableMap.of());
    }

    // Override if you have a separate interface
    @Override
    public Value<Home> defaultHome() {
        return Sponge.getRegistry().getValueFactory()
                .createValue(Keys.DEFAULT_HOME, defaultHome, null);
    }

    // Override if you have a separate interface
    @Override
    public MapValue<String, Home> homes() {
        return Sponge.getRegistry().getValueFactory()
                .createMapValue(Keys.HOMES, homes, ImmutableMap.of());
    }

    private Home getDefaultHome() {
        return this.defaultHome;
    }

    private Map<String, Home> getHomes() {
        return Maps.newHashMap(this.homes);
    }

    private void setDefaultHome(@Nullable Home defaultHome) {
        this.defaultHome = defaultHome;
    }

    private void setHomes(Map<String, Home> homes) {
        Preconditions.checkNotNull(homes);
        this.homes = Maps.newHashMap(homes);
    }

    @Override
    protected void registerGettersAndSetters() {
        registerKeyValue(Keys.DEFAULT_HOME, this::defaultHome);
        registerKeyValue(Keys.HOMES, this::homes);

        registerFieldGetter(Keys.DEFAULT_HOME, this::getDefaultHome);
        registerFieldGetter(Keys.HOMES, this::getHomes);

        registerFieldSetter(Keys.DEFAULT_HOME, this::setDefaultHome);
        registerFieldSetter(Keys.HOMES, this::setHomes);
    }

    @Override
    public int getContentVersion() {
        // Update whenever the serialization format changes
        return HomeDataBuilder.CONTENT_VERSION;
    }

    @Override
    public ImmutableHomeData asImmutable() {
        return new ImmutableHomeDataImpl(this.defaultHome, this.homes);
    }

    // Only required on mutable implementations
    @Override
    public Optional<HomeData> fill(DataHolder dataHolder, MergeFunction overlap) {
        HomeData merged = overlap.merge(this, dataHolder.get(HomeData.class).orElse(null));
        this.defaultHome = merged.defaultHome().get();
        this.homes = merged.homes().get();

        return Optional.of(this);
    }

    // Only required on mutable implementations
    @Override
    public Optional<HomeData> from(DataContainer container) {
        if (!container.contains(Keys.DEFAULT_HOME, Keys.HOMES)) {
            return Optional.empty();
        }
        // Loads the structure defined in toContainer
        this.defaultHome = container.getSerializable(Keys.DEFAULT_HOME.getQuery(), Home.class).get();

        // Loads the map of homes
        this.homes = Maps.newHashMap();
        DataView homes = container.getView(Keys.HOMES.getQuery()).get();
        for (DataQuery homeQuery : homes.getKeys(false)) {
            homes.getSerializable(homeQuery, Home.class)
                    .ifPresent(home -> this.homes.put(homeQuery.toString(), home));
        }

        return Optional.of(this);
    }

    @Override
    public HomeData copy() {
        return new HomeDataImpl(this.defaultHome, this.homes);
    }

    @Override
    public DataContainer toContainer() {
        DataContainer container = super.toContainer();
        // This is the simplest, but use whatever structure you want!
        if(this.defaultHome != null) {
            container.set(Keys.DEFAULT_HOME, this.defaultHome);
        }
        container.set(Keys.HOMES, this.homes);

        return container;
    }
}
