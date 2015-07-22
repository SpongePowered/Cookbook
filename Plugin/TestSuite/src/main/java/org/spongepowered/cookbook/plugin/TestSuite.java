package org.spongepowered.cookbook.plugin;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerStartingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.world.extent.ExtentBufferFactory;

import java.util.List;

@Plugin(id = "TestSuite", name = "TestSuite", version = "0.1")
public class TestSuite {

    public static ExtentBufferFactory EXTENT_BUFFER_FACTORY;
    @Inject private Logger logger;
    @Inject private Game game;
    @Inject private PluginContainer instance;

    @Subscribe
    public void onServerStarting(ServerStartingEvent event) {
        EXTENT_BUFFER_FACTORY = game.getRegistry().getExtentBufferFactory();
        game.getCommandDispatcher().register(instance, new Commands(), "testsuite");
        logger.info("Running test suite...");
        final Result result = runTests();
        logger.info("Ran {} test(s), ignored {} test(s), took {}", result.getRunCount(), result.getIgnoreCount(),
            DurationFormatUtils.formatDurationWords(result.getRunTime(), true, true));
        if (result.getFailureCount() > 0) {
            logger.warn("Failed {} test(s)", result.getFailureCount());
            for (Failure failure : result.getFailures()) {
                logger.warn(failure.toString());
                logger.warn(failure.getTrace());
            }
        }
        if (result.wasSuccessful()) {
            logger.info("Success");
        } else {
            logger.warn("Failure");
        }
    }

    private static Result runTests() {
        return JUnitCore.runClasses(BiomeBufferTest.class);
    }

    private class Commands implements CommandCallable {

        @Override
        public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
            return null;
        }

        @Override
        public boolean testPermission(CommandSource source) {
            return source.hasPermission("command.sponge.testsuite");
        }

        @Override
        public Optional<Text> getShortDescription(CommandSource source) {
            return Optional.of((Text) Texts.of("Runs the Sponge test suite"));
        }

        @Override
        public Optional<Text> getHelp(CommandSource source) {
            return Optional.of(getUsage(source));
        }

        @Override
        public Text getUsage(CommandSource source) {
            return Texts.of("testsuite");
        }

        @Override
        public CommandResult process(CommandSource source, String arguments) throws CommandException {
            source.sendMessage(Texts.of("Running test suite..."));
            final Result result = runTests();
            source.sendMessage(Texts.of("Ran ", result.getRunCount(), " test(s), ignored ", result.getIgnoreCount(), " test(s), took ",
                DurationFormatUtils.formatDurationWords(result.getRunTime(), true, true)));
            if (result.getFailureCount() > 0) {
                source.sendMessage(Texts.of(TextColors.DARK_RED, "Failed ", result.getFailureCount(), " test(s)"));
                source.sendMessage(Texts.of(TextColors.DARK_RED, "See console for stack traces"));
                for (Failure failure : result.getFailures()) {
                    source.sendMessage(Texts.of(TextColors.DARK_RED, failure.toString()));
                    logger.warn(failure.getTrace());
                }
            }
            if (result.wasSuccessful()) {
                source.sendMessage(Texts.of("Success"));
            } else {
                source.sendMessage(Texts.of(TextColors.DARK_RED, "Failure"));
            }
            return CommandResult.success();
        }

    }

}
