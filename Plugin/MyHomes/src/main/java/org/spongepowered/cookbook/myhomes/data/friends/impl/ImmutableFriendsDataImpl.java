package org.spongepowered.cookbook.myhomes.data.friends.impl;

import org.spongepowered.cookbook.myhomes.data.Keys;
import org.spongepowered.cookbook.myhomes.data.friends.FriendsData;
import org.spongepowered.cookbook.myhomes.data.friends.ImmutableFriendsData;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableListData;
import org.spongepowered.api.data.value.immutable.ImmutableListValue;

import java.util.List;
import java.util.UUID;

public class ImmutableFriendsDataImpl extends AbstractImmutableListData<UUID, ImmutableFriendsData, FriendsData> implements ImmutableFriendsData {

    public ImmutableFriendsDataImpl(List<UUID> value) {
        super(value, Keys.FRIENDS);
    }

    @Override
    public ImmutableListValue<UUID> friends() {
        return getListValue();
    }

    @Override
    public FriendsDataImpl asMutable() {
        return new FriendsDataImpl(getValue());
    }

    @Override
    public int getContentVersion() {
        return FriendsDataBuilder.CONTENT_VERSION;
    }
}
