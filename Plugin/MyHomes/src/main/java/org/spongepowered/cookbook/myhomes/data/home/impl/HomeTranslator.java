package org.spongepowered.cookbook.myhomes.data.home.impl;

import org.spongepowered.cookbook.myhomes.data.home.Home;
import com.flowpowered.math.vector.Vector3d;
import com.google.common.reflect.TypeToken;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.Queries;
import org.spongepowered.api.data.persistence.DataTranslator;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.World;

import java.util.UUID;

public class HomeTranslator implements DataTranslator<Home> {

    public static final int CONTENT_VERSION = 2;

    @Override
    public TypeToken<Home> getToken() {
        return TypeToken.of(Home.class);
    }

    @Override
    public Home translate(final DataView content) throws InvalidDataException {
        content.getInt(Queries.CONTENT_VERSION).ifPresent(version -> {
            if (version != CONTENT_VERSION) {
                throw new InvalidDataException("Version incompatible: " + version);
            }
        });

        if (!content.contains(Home.WORLD_QUERY, Home.POSITION_QUERY, Home.ROTATION_QUERY, Home.NAME_QUERY)) {
            throw new InvalidDataException("Incomplete data");
        }

        World world = Sponge.getServer().getWorld(content.getObject(Home.WORLD_QUERY, UUID.class).get())
                .orElseThrow(InvalidDataException::new);
        Vector3d position = content.getObject(Home.POSITION_QUERY, Vector3d.class).get();
        Vector3d rotation = content.getObject(Home.ROTATION_QUERY, Vector3d.class).get();
        String name = content.getString(Home.NAME_QUERY).get();

        Transform<World> transform = new Transform<>(world, position, rotation);
        return new Home(transform, name);
    }

    @Override
    public DataContainer translate(Home home) throws InvalidDataException {
        return DataContainer.createNew()
                .set(Home.WORLD_QUERY, home.getTransform().getExtent().getUniqueId())
                .set(Home.POSITION_QUERY, home.getTransform().getPosition())
                .set(Home.ROTATION_QUERY, home.getTransform().getRotation())
                .set(Home.NAME_QUERY, home.getName())
                .set(Queries.CONTENT_VERSION, CONTENT_VERSION);
    }

    @Override
    public String getId() {
        return "myhomes:home_translator";
    }

    @Override
    public String getName() {
        return "Home Translator";
    }
}
