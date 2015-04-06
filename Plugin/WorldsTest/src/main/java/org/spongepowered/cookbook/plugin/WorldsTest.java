package org.spongepowered.cookbook.plugin;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.entity.player.gamemode.GameModes;
import org.spongepowered.api.event.state.ServerStartingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.event.Subscribe;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.GeneratorTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;

@Plugin(id = "worldstest", name = "WorldsTest", version = "0.1")
public class WorldsTest {
    @Inject
    private Logger logger;
    @Inject
    private Game game;
    @Inject
    private PluginContainer instance;

    @Subscribe
    public void onServerStarting(ServerStartingEvent event) {

        game.getCommandDispatcher().register(instance, new CommandTPWorld(game), "tpworld");
        game.getCommandDispatcher().register(instance, new CommandSpawn(game), "spawn");

        Optional<World> myNether = game.getRegistry().getWorldBuilder()
                .name("my_nether")
                .enabled(true)
                .loadsOnStartup(true)
                .keepsSpawnLoaded(true)
                .seed(9001)
                .dimensionType(DimensionTypes.NETHER)
                .generator(GeneratorTypes.DEFAULT)
                .usesMapFeatures(true)
                .gameMode(GameModes.CREATIVE)
                .usesMapFeatures(true)
                .build();
        if (!myNether.isPresent()) {
            logger.error("Creation of my custom nether failed, implementation issue!");
        }

        game.getRegistry().getWorldBuilder()
                .name("my_end")
                .enabled(false)
                .loadsOnStartup(true)
                .keepsSpawnLoaded(true)
                .seed(1337)
                .dimensionType(DimensionTypes.END)
                .generator(GeneratorTypes.DEFAULT)
                .usesMapFeatures(true)
                .gameMode(GameModes.CREATIVE)
                .usesMapFeatures(true)
                .build();
    }

    private static class CommandTPWorld implements CommandCallable {
        private final Game game;

        public CommandTPWorld(Game game) {
            this.game = game;
        }

        @Override
        public boolean call(CommandSource source, String arguments, List<String> parents) throws CommandException {
            if (source instanceof Player) {
                if (arguments.isEmpty()) {
                    source.sendMessage(Texts.of("Cannot teleport, no world name provided."));
                    return true;
                }

                final String potentialWorldName = arguments.split(" ")[0];
                final Optional<World> optWorld = game.getServer().getWorld(potentialWorldName);
                if (optWorld.isPresent()) {
                    ((Player) source).transferToWorld(optWorld.get().getName(), optWorld.get().getProperties().getSpawnPosition()
                            .toDouble());
                } else {
                    source.sendMessage(Texts.of("World [", TextColors.AQUA, potentialWorldName, TextColors.WHITE, "] was not found."));
                }
            }
            return true;
        }

        @Override
        public boolean testPermission(CommandSource source) {
            return source.hasPermission("command.sponge.tpworld");
        }

        @Override
        public String getShortDescription(CommandSource source) {
            return "Used to teleport a player to a world";
        }

        @Override
        public Text getHelp(CommandSource source) {
            return Texts.of("usage: <player> <worldname>");
        }

        @Override
        public String getUsage(CommandSource source) {
            return "usage: <player> <worldname>";
        }

        @Override
        public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
            return null;
        }
    }

    private static class CommandSpawn implements CommandCallable {
        private final Game game;

        public CommandSpawn(Game game) {
            this.game = game;
        }

        @Override
        public boolean call(CommandSource source, String arguments, List<String> parents) throws CommandException {
            if (source instanceof Player) {
                final Player player = (Player) source;
                final String[] args = arguments.split(" ");
                World world;
                Vector3i spawnCoordinates;

                // (Player) /spawn
                if (arguments.isEmpty()) {
                    world = player.getWorld();
                    spawnCoordinates = player.getWorld().getProperties().getSpawnPosition();
                    player.setLocationSafely(new Location(world, spawnCoordinates.toDouble()));
                } else {
                    switch (args[0]) {
                        case "-i":
                            // (Player) /spawn -i
                            if (args.length == 1) {
                                world = player.getWorld();
                            // (Player) /spawn -i <world_name>
                            } else {
                                final Optional<World> optWorldCandidate = game.getServer().getWorld(args[1]);
                                if (optWorldCandidate.isPresent()) {
                                    world = optWorldCandidate.get();
                                } else {
                                    source.sendMessage(Texts.of("World [", TextColors.AQUA, args[1], TextColors.WHITE, "] was not found"));
                                    break;
                                }
                            }

                            spawnCoordinates = world.getProperties().getSpawnPosition();

                            source.sendMessage(Texts.of("World [", TextColors.AQUA, world.getName(), TextColors.WHITE,
                                    "]: spawn -> x [", TextColors.GREEN, spawnCoordinates.getX(), TextColors.WHITE, "] | y [", TextColors.GREEN,
                                    spawnCoordinates.getY(), TextColors.WHITE, "] | z [", TextColors.GREEN, spawnCoordinates.getZ(), TextColors
                                            .WHITE, "]."));
                            break;
                        case "-s":
                            world = player.getWorld();

                            // (Player) /spawn -s <no args>
                            if (args.length == 1) {
                                spawnCoordinates = player.getLocation().getBlockPosition();
                            // (Player) /spawn -s x y z
                            } else {
                                try {
                                    spawnCoordinates = new Vector3i(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                                } catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
                                    source.sendMessage(Texts.of("Invalid spawn coordinates, must be in numeric format. Ex. /spawn -s 0 0 0."));
                                    break;
                                }
                            }

                            world.getProperties().setSpawnPosition(spawnCoordinates);

                            source.sendMessage(Texts.of("World [", TextColors.AQUA, world.getName(), TextColors.WHITE,
                                    "]: spawn set to -> x [", TextColors.GREEN, spawnCoordinates.getX(), TextColors.WHITE, "] | y [", TextColors
                                            .GREEN,
                                    spawnCoordinates.getY(), TextColors.WHITE, "] | z [", TextColors.GREEN, spawnCoordinates.getZ(), TextColors
                                            .WHITE, "]."));
                            break;
                    }
                }
            }
            return true;
        }

        @Override
        public boolean testPermission(CommandSource source) {
            return source.hasPermission("command.sponge.spawn");
        }

        @Override
        public String getShortDescription(CommandSource source) {
            return "Used to get spawn information or set the point of a world";
        }

        @Override
        public Text getHelp(CommandSource source) {
            return Texts.of("usage: spawn -i <world_name> | -s <world_name> <x> <y> <z>");
        }

        @Override
        public String getUsage(CommandSource source) {
            return "usage: spawn -i <world_name> | -s <world_name> <x> <y> <z>";
        }

        @Override
        public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
            return null;
        }
    }
}
