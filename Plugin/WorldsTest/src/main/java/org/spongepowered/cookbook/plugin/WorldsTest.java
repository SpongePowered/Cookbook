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
                .seed(42)
                .dimensionType(DimensionTypes.NETHER)
                .generator(GeneratorTypes.DEFAULT)
                .usesMapFeatures(true)
                .gameMode(GameModes.CREATIVE)
                .usesMapFeatures(true)
                .build();
        if (!myNether.isPresent()) {
            logger.error("Creation of my custom nether failed, implementation issue!");
        }
    }

    private static class CommandTPWorld implements CommandCallable {
        private final Game game;

        public CommandTPWorld(Game game) {
            this.game = game;
        }

        @Override
        public boolean call(CommandSource source, String arguments, List<String> parents) throws CommandException {
            if (source instanceof Player) {
                final String[] args;
                if (arguments.isEmpty()) {
                    args = new String[0];
                } else {
                    args = arguments.split(" ");
                }
                switch (args.length) {
                    case 0:
                        break;
                    case 1:
                        final String potentialWorldName = args[0];
                        final Optional<World> optWorld = game.getServer().getWorld(potentialWorldName);
                        if (optWorld.isPresent()) {
                            ((Player) source).transferToWorld(optWorld.get().getName(), optWorld.get().getProperties().getSpawnPosition()
                                    .toDouble());
                        }
                        break;
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
                final String[] args;
                if (arguments.isEmpty()) {
                    args = new String[0];
                } else {
                    args = arguments.split(" ");
                }
                Vector3i spawnCoordinates;

                switch (args.length) {
                    // (Player) /spawn
                    case 0:
                        spawnCoordinates = ((Player) source).getWorld().getProperties().getSpawnPosition();
                        ((Player) source).setLocation(new Location(((Player) source).getWorld(), spawnCoordinates.toDouble()));
                        break;
                    case 1:
                        final String arg = args[0];
                        // (Player) /spawn -info
                        switch (arg) {
                            case "-info":
                                if (args.length > 2) {
                                    spawnCoordinates = null;
                                } else {
                                    spawnCoordinates = ((Player) source).getWorld().getProperties().getSpawnPosition();
                                }
                                source.sendMessage(Texts.of("World [" + ((Player) source).getWorld().getName() + "] -> x [" + spawnCoordinates.getX
                                        () + "] | y [" + spawnCoordinates.getY() + "] | z [" + spawnCoordinates.getZ() + "]."));
                                break;  
                            case "-set":
                            // (Player) /spawn -set x y z                              
                                if (args.length == 4) {
                                    int x = Integer.parseInt(args[1]);
                                    int y = Integer.parseInt(args[2]);
                                    int z = Integer.parseInt(args[3]);

                                    ((Player) source).getWorld().getProperties().setSpawnPosition(new Vector3i(x, y, z));
                            // (Player) /spawn -set <no args>                                 
                                } else {
                                    ((Player) source).getWorld().getProperties().setSpawnPosition(((Player) source).getLocation().getBlockPosition());
                                }

                        }
                        break;
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
            return null;
        }

        @Override
        public Text getHelp(CommandSource source) {
            return null;
        }

        @Override
        public String getUsage(CommandSource source) {
            return null;
        }

        @Override
        public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
            return null;
        }
    }
}
