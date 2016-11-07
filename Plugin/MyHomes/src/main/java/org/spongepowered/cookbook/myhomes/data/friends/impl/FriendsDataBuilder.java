package org.spongepowered.cookbook.myhomes.data.friends.impl;

import org.spongepowered.cookbook.myhomes.data.Keys;
import org.spongepowered.cookbook.myhomes.data.friends.FriendsData;
import org.spongepowered.cookbook.myhomes.data.friends.ImmutableFriendsData;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FriendsDataBuilder extends AbstractDataBuilder<FriendsData> implements DataManipulatorBuilder<FriendsData, ImmutableFriendsData> {

    public static final int CONTENT_VERSION = 1;

    public FriendsDataBuilder() {
        super(FriendsData.class, CONTENT_VERSION);
    }

    @Override
    public FriendsDataImpl create() {
        return new FriendsDataImpl();
    }

    @Override
    public Optional<FriendsData> createFrom(DataHolder dataHolder) {
        return create().fill(dataHolder);
    }

    @Override
    protected Optional<FriendsData> buildContent(DataView container) throws InvalidDataException {
        if (container.contains(Keys.FRIENDS)) {
            List<UUID> friends = container.getObjectList(Keys.FRIENDS.getQuery(), UUID.class).get();
            return Optional.of(new FriendsDataImpl(friends));
        }

        return Optional.empty();
    }
}
