package org.spongepowered.cookbook.myhomes.data.friends.impl;

import org.spongepowered.cookbook.myhomes.data.Keys;
import org.spongepowered.cookbook.myhomes.data.friends.FriendsData;
import org.spongepowered.cookbook.myhomes.data.friends.ImmutableFriendsData;
import com.google.common.collect.ImmutableList;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractListData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.ListValue;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FriendsDataImpl extends AbstractListData<UUID, FriendsData, ImmutableFriendsData> implements FriendsData {

    public FriendsDataImpl(List<UUID> value) {
        super(value, Keys.FRIENDS);
    }

    public FriendsDataImpl() {
        this(ImmutableList.of());
    }

    @Override
    public ListValue<UUID> friends() {
        return getListValue();
    }

    @Override
    public ImmutableFriendsDataImpl asImmutable() {
        return new ImmutableFriendsDataImpl(getValue());
    }

    @Override
    public Optional<FriendsData> fill(DataHolder dataHolder, MergeFunction overlap) {
        FriendsData merged = overlap.merge(this, dataHolder.get(FriendsData.class).orElse(null));
        setValue(merged.friends().get());

        return Optional.of(this);
    }

    @Override
    public Optional<FriendsData> from(DataContainer container) {
        if (container.contains(Keys.FRIENDS)) {
            List<UUID> friends = container.getObjectList(Keys.FRIENDS.getQuery(), UUID.class).get();
            return Optional.of(setValue(friends));
        }

        return Optional.empty();
    }

    @Override
    public FriendsData copy() {
        return new FriendsDataImpl(getValue());
    }

    @Override
    public int getContentVersion() {
        return FriendsDataBuilder.CONTENT_VERSION;
    }
}
