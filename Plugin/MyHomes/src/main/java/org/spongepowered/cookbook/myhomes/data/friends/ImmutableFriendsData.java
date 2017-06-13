package org.spongepowered.cookbook.myhomes.data.friends;

import org.spongepowered.api.data.manipulator.immutable.ImmutableListData;
import org.spongepowered.api.data.value.immutable.ImmutableListValue;

import java.util.UUID;

public interface ImmutableFriendsData extends ImmutableListData<UUID, ImmutableFriendsData, FriendsData> {

    ImmutableListValue<UUID> friends();

}
