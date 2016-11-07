package org.spongepowered.cookbook.myhomes.data.home;

import org.spongepowered.cookbook.myhomes.data.home.impl.HomeBuilder;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.Queries;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.World;

public class Home implements DataSerializable {

    public static final DataQuery WORLD_QUERY = DataQuery.of("WorldUUID");
    public static final DataQuery POSITION_QUERY = DataQuery.of("Position");
    public static final DataQuery ROTATION_QUERY = DataQuery.of("Rotation");
    public static final DataQuery NAME_QUERY = DataQuery.of("Name");

    private Transform<World> transform;

    private String name;

    public Home(Transform<World> transform, String name) {
        this.transform = transform;
        this.name = name;
    }

    @Override
    public int getContentVersion() {
        return HomeBuilder.CONTENT_VERSION;
    }

    public Transform<World> getTransform() {
        return this.transform;
    }

    public String getName() {
        return name;
    }

    @Override
    public DataContainer toContainer() {
        return new MemoryDataContainer()
                .set(WORLD_QUERY, this.transform.getExtent().getUniqueId())
                .set(POSITION_QUERY, this.transform.getPosition())
                .set(ROTATION_QUERY, this.transform.getRotation())
                .set(NAME_QUERY, this.name)
                .set(Queries.CONTENT_VERSION, HomeBuilder.CONTENT_VERSION);
    }

}
