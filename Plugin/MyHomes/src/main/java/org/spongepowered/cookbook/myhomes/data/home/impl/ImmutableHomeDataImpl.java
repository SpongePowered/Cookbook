package org.spongepowered.cookbook.myhomes.data.home.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.cookbook.myhomes.data.Keys;
import org.spongepowered.cookbook.myhomes.data.home.Home;
import org.spongepowered.cookbook.myhomes.data.home.HomeData;
import org.spongepowered.cookbook.myhomes.data.home.ImmutableHomeData;
import com.google.common.collect.ImmutableMap;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.immutable.ImmutableMapValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

import java.util.Map;

public class ImmutableHomeDataImpl extends AbstractImmutableData<ImmutableHomeData, HomeData> implements ImmutableHomeData {

    private final Home defaultHome;
    private final ImmutableMap<String, Home> homes;

    private final ImmutableValue<Home> defaultHomeValue;
    private final ImmutableMapValue<String, Home> homesValue;

    public ImmutableHomeDataImpl() {
        this(null, ImmutableMap.of());
    }

    public ImmutableHomeDataImpl(Home defaultHome, Map<String, Home> homes) {
        this.defaultHome = checkNotNull(defaultHome);
        this.homes = ImmutableMap.copyOf(checkNotNull(homes));

        this.defaultHomeValue = Sponge.getRegistry().getValueFactory()
                .createValue(Keys.DEFAULT_HOME, defaultHome)
                .asImmutable();

        this.homesValue = Sponge.getRegistry().getValueFactory()
                .createMapValue(Keys.HOMES, homes, ImmutableMap.of())
                .asImmutable();
    }

    // Override if you have a separate interface
    @Override
    public ImmutableValue<Home> defaultHome() {
        return this.defaultHomeValue;
    }

    // Override if you have a separate interface
    @Override
    public ImmutableMapValue<String, Home> homes() {
        return this.homesValue;
    }

    private Home getDefaultHome() {
        return this.defaultHome;
    }

    private Map<String, Home> getHomes() {
        return this.homes;
    }

    @Override
    protected void registerGetters() {
        registerKeyValue(Keys.DEFAULT_HOME, this::defaultHome);
        registerKeyValue(Keys.HOMES, this::homes);

        registerFieldGetter(Keys.DEFAULT_HOME, this::getDefaultHome);
        registerFieldGetter(Keys.HOMES, this::getHomes);
    }

    @Override
    public int getContentVersion() {
        // Update whenever the serialization format changes
        return HomeDataBuilder.CONTENT_VERSION;
    }

    @Override
    public HomeDataImpl asMutable() {
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
