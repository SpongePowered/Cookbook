package org.spongepowered.cookbook.myhomes.data.home.impl;

import org.spongepowered.cookbook.myhomes.data.Keys;
import org.spongepowered.cookbook.myhomes.data.home.Home;
import org.spongepowered.cookbook.myhomes.data.home.HomeData;
import org.spongepowered.cookbook.myhomes.data.home.ImmutableHomeData;
import com.google.common.collect.ImmutableMap;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.DataContentUpdater;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

public class HomeDataBuilder extends AbstractDataBuilder<HomeData> implements DataManipulatorBuilder<HomeData, ImmutableHomeData> {

    public static final int CONTENT_VERSION = 2;

    public HomeDataBuilder() {
        super(HomeData.class, CONTENT_VERSION);
    }

    @Override
    public HomeData create() {
        return new HomeDataImpl();
    }

    @Override
    public Optional<HomeData> createFrom(DataHolder dataHolder) {
        return create().fill(dataHolder);
    }

    @Override
    protected Optional<HomeData> buildContent(DataView container) throws InvalidDataException {
        if(!container.contains(Keys.HOMES)) return Optional.empty();

        HomeData data = new HomeDataImpl();

        container.getView(Keys.HOMES.getQuery())
                .get().getKeys(false).forEach(name -> data.homes().put(name.toString(), container.getSerializable(name, Home.class)
                .orElseThrow(InvalidDataException::new)));

        container.getSerializable(Keys.DEFAULT_HOME.getQuery(), Home.class).ifPresent(home -> {
            data.set(Keys.DEFAULT_HOME, home);
        });

        return Optional.of(data);
    }

    public static class HomesUpdater implements DataContentUpdater {

        @Override
        public int getInputVersion() {
            return 1;
        }

        @Override
        public int getOutputVersion() {
            return 2;
        }

        @Override
        public DataView update(DataView content) {
            content.set(Keys.HOMES, ImmutableMap.of());

            return content;
        }
    }
}
