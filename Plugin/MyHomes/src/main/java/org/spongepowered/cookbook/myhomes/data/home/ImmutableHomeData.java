package org.spongepowered.cookbook.myhomes.data.home;

import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.value.immutable.ImmutableMapValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

public interface ImmutableHomeData extends ImmutableDataManipulator<ImmutableHomeData, HomeData> {

    ImmutableValue<Home> defaultHome();

    ImmutableMapValue<String, Home> homes();

}
