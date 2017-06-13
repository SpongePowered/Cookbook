package org.spongepowered.cookbook.myhomes.data.home.impl;

import org.spongepowered.cookbook.myhomes.data.home.Home;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.DataContentUpdater;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Optional;
import java.util.UUID;

public class HomeBuilder extends AbstractDataBuilder<Home> {

    public static final int CONTENT_VERSION = 2;

    public HomeBuilder() {
        super(Home.class, CONTENT_VERSION);
    }

    @Override
    protected Optional<Home> buildContent(DataView content) throws InvalidDataException {
        if (!content.contains(Home.WORLD_QUERY, Home.POSITION_QUERY, Home.ROTATION_QUERY, Home.NAME_QUERY)) {
            return Optional.empty();
        }

        World world = Sponge.getServer().getWorld(content.getObject(Home.WORLD_QUERY, UUID.class).get()).orElseThrow(InvalidDataException::new);
        Vector3d position = content.getObject(Home.POSITION_QUERY, Vector3d.class).get();
        Vector3d rotation = content.getObject(Home.ROTATION_QUERY, Vector3d.class).get();
        String name = content.getString(Home.NAME_QUERY).get();

        Transform<World> transform = new Transform<>(world, position, rotation);
        return Optional.of(new Home(transform, name));
    }

    public static class NameUpdater implements DataContentUpdater {
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
            if (!content.contains(Home.WORLD_QUERY, Home.POSITION_QUERY)) {
                throw new InvalidDataException("Invalid data for a home!");
            }
            UUID uuid = content.getObject(Home.WORLD_QUERY, UUID.class).get();
            Vector3d pos = content.getObject(Home.POSITION_QUERY, Vector3d.class).get();
            String name = Sponge.getServer().getWorldProperties(uuid)
                    .map(WorldProperties::getWorldName)
                    .orElse(uuid.toString().substring(0, 9));

            name = String.format("%s-%d,%d,%d", name, pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());

            content.set(Home.NAME_QUERY, name);

            return content;
        }
    }
}
