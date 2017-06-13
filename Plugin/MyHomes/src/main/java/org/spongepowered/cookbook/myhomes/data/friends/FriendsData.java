package org.spongepowered.cookbook.myhomes.data.friends;

import org.spongepowered.api.data.manipulator.mutable.ListData;
import org.spongepowered.api.data.value.mutable.ListValue;

import java.util.UUID;

public interface FriendsData extends ListData<UUID, FriendsData, ImmutableFriendsData> {

    ListValue<UUID> friends();

}
