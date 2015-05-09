package org.spongepowered.cookbook.plugin;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerStartingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.PositionOutOfBoundsException;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.extent.Extent;

import java.util.List;

@Plugin(id = "worldeditingtest", name = "WorldEditingTest", version = "0.1")
public class WorldEditingTest {

    @Inject private Logger logger;
    @Inject private Game game;
    @Inject private PluginContainer instance;

    @Subscribe
    public void onServerStarting(ServerStartingEvent event) {
        game.getCommandDispatcher().register(instance, new Commands(), "edit");
    }

    private class Commands implements CommandCallable {

        @Override
        public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
            return null;
        }

        @Override
        public boolean testPermission(CommandSource source) {
            return source.hasPermission("command.sponge.edit");
        }

        @Override
        public Optional<Text> getShortDescription(CommandSource source) {
            return Optional.of((Text) Texts.of("Gets a summary of the contents of chunk"));
        }

        @Override
        public Optional<Text> getHelp(CommandSource source) {
            return Optional.of(getUsage(source));
        }

        @Override
        public Text getUsage(CommandSource source) {
            return Texts.of("edit [c|w] [<x> <y> <z>]");
        }

        @Override
        public Optional<CommandResult> process(CommandSource source, String arguments) throws CommandException {
            if (source instanceof Player) {
                final String[] split = arguments.split(" ");
                final boolean useChunk;
                final int index;
                if (split[0].equals("c")) {
                    useChunk = true;
                    index = 1;
                } else if (split[0].equals("w")) {
                    useChunk = false;
                    index = 1;
                } else {
                    useChunk = false;
                    index = 0;
                }
                final Player player = (Player) source;
                final World world = player.getWorld();
                final Vector3i blockPosition = getPosition(player, split, index);
                final Vector2i biomePosition = blockPosition.toVector2(true);
                final Extent extent = useChunk ? world.getChunk(game.getServer().getChunkLayout().toChunk(blockPosition).get()).get() : world;
                try {
                    player.sendMessage(Texts.of(extent.getBlockType(blockPosition).getName()));
                    player.sendMessage(Texts.of(extent.getBiome(biomePosition).getName()));
                    extent.setBiome(biomePosition, BiomeTypes.SWAMPLAND);
                    extent.setBlockType(blockPosition, BlockTypes.GRASS);
                } catch (PositionOutOfBoundsException exception) {
                    player.sendMessage(Texts.of("The position is out of bounds"));
                }
                return Optional.of(CommandResult.success());
            }
            throw new CommandException(Texts.of("This command can only be executed by a player"));
        }
    }

    private static Vector3i getPosition(Player player, String[] arguments, int index) throws CommandException {
        if (arguments.length - index <= 1) {
            return player.getLocation().getBlockPosition();
        }
        if (arguments.length - index != 3) {
            throw new CommandException(Texts.of("Expected 3 arguments"));
        }
        final int x, y, z;
        try {
            x = Integer.parseInt(arguments[index]);
            y = Integer.parseInt(arguments[index + 1]);
            z = Integer.parseInt(arguments[index + 2]);
        } catch (NumberFormatException exception) {
            throw new CommandException(Texts.of("Expected 3 ints"));
        }
        return new Vector3i(x, y, z);
    }
}
