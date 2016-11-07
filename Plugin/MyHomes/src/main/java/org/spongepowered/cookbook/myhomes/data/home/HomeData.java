package org.spongepowered.cookbook.myhomes.data.home;

import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.value.mutable.MapValue;
import org.spongepowered.api.data.value.mutable.Value;

public interface HomeData extends DataManipulator<HomeData, ImmutableHomeData> {

    Value<Home> defaultHome();

    MapValue<String, Home> homes();

}
