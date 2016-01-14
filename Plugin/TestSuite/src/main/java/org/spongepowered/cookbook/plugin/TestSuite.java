package org.spongepowered.cookbook.plugin;

import com.google.inject.Inject;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.extent.ExtentBufferFactory;

import java.util.List;
import java.util.Optional;

@Plugin(id = "TestSuite", name = "TestSuite", version = "0.3")
public class TestSuite {

    public static ExtentBufferFactory EXTENT_BUFFER_FACTORY;
    @Inject private Logger logger;
    @Inject private PluginContainer instance;


    @Listener
    public void onServerStarting(GameStartingServerEvent event) {
        EXTENT_BUFFER_FACTORY = Sponge.getRegistry().getExtentBufferFactory();
        Sponge.getCommandManager().register(this.instance, new Commands(), "testsuite");
        this.logger.info("Running test suite...");
        final Result result = runTests();
        this.logger.info("Ran {} test(s), ignored {} test(s), took {}", result.getRunCount(), result.getIgnoreCount(),
            DurationFormatUtils.formatDurationWords(result.getRunTime(), true, true));
        if (result.getFailureCount() > 0) {
            this.logger.warn("Failed {} test(s)", result.getFailureCount());
            for (Failure failure : result.getFailures()) {
                this.logger.warn(failure.toString());
                this.logger.warn(failure.getTrace());
            }
        }
        if (result.wasSuccessful()) {
            this.logger.info("Success");
        } else {
            this.logger.warn("Failure");
        }
    }

    private static Result runTests() {
        return JUnitCore.runClasses(BiomeBufferTest.class, BlockBufferTest.class, BiomeWorkerTest.class, BlockWorkerTest.class);
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
            return Optional.of((Text) Text.of("Runs the Sponge test suite"));
        }

        @Override
        public Optional<Text> getHelp(CommandSource source) {
            return Optional.of(getUsage(source));
        }

        @Override
        public Text getUsage(CommandSource source) {
            return Text.of("testsuite");
        }

        @Override
        public CommandResult process(CommandSource source, String arguments) throws CommandException {
            source.sendMessage(Text.of("Running test suite..."));
            final Result result = runTests();
            source.sendMessage(Text.of("Ran ", result.getRunCount(), " test(s), ignored ", result.getIgnoreCount(), " test(s), took ",
                DurationFormatUtils.formatDurationWords(result.getRunTime(), true, true)));
            if (result.getFailureCount() > 0) {
                source.sendMessage(Text.of(TextColors.DARK_RED, "Failed ", result.getFailureCount(), " test(s)"));
                source.sendMessage(Text.of(TextColors.DARK_RED, "See console for stack traces"));
                for (Failure failure : result.getFailures()) {
                    source.sendMessage(Text.of(TextColors.DARK_RED, failure.toString()));
                    TestSuite.this.logger.warn(failure.getTrace());
                }
            }
            if (result.wasSuccessful()) {
                source.sendMessage(Text.of("Success"));
            } else {
                source.sendMessage(Text.of(TextColors.DARK_RED, "Failure"));
            }
            return CommandResult.success();
        }

    }

}
