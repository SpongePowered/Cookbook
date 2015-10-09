package org.spongepowered.cookbook

import com.google.inject.Inject
import org.slf4j.Logger
import org.spongepowered.api.Game
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.EventListener
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent
import org.spongepowered.api.event.network.ClientConnectionEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.text.action.TextActions
import org.spongepowered.api.text.title.Titles
import org.spongepowered.api.util.command.CommandResult
import org.spongepowered.api.util.command.spec.CommandSpec
import java.net.URL

// Plugins are declared in basically the same way as Java.
@Plugin(id = "HelloFromKotlin", name = "Hello From Kotlin!", version = "1.0.0")
public class HelloFromKotlin {

    // In Kotlin, types are non-nullable by default. Normally, you would have
    // declared this value as "var game: Game?" to indicate nullability, which
    // is a bit of a pain when going to use the type -- you have to include
    // nullability checks even if the variable is injected. This nullable
    // type used to be necessary to allow a field with no value set and no
    // setter methods. In Kotlin M13, they introduced the "lateinit" keyword
    // to indicate that it's a non-nullable type, but has a value not set at
    // compile time or with a setter -- for instance, by dependency injection.
    @Inject lateinit var game: Game
    @Inject lateinit var logger: Logger

    // Kotlin has a keyword for a function, "fun". Could have guessed that.
    fun getVersion(): String {

        return javaClass.annotations
                .filterIsInstance(Plugin::class.java)
                .first()
                .version
        // Let's break this down.
        //
        // "javaClass" is a special property pointing to the object's class as
        // its Java reperesentation. Classes in Kotlin are actually KClasses.
        // This is also the "java" in "Plugin::class.java".
        //
        // "annotations" is a virtual property created from "getAnnotations()".
        // It functions exactly the same, just shorter and cleaner.
        //
        // Since annotations is a List, filter out objects which are Plugins,
        // then grab the first one, then use the virtual property "version" to
        // call "getVersion()".

    }

    // Listeners are still painless.
    @Listener fun serverStarting(event: GameAboutToStartServerEvent) {

        // Note the semicolons! (Oh wait, the compiler knows when you don't
        // need them.)
        logger.info("Hello from Kotlin <3 (This is version ${getVersion()}" +
                " running on ${game.platform.name}" +
                " for ${game.platform.minecraftVersion.name})")
        // Template strings are cool.

        game.eventManager.registerListener(this,
                ClientConnectionEvent.Join::class.java,
                HelloListener())

        game.commandDispatcher.register(this, CommandSpec.builder()
                .description("Tasty snacks.".reset())
                // Note the easy lambda use. If the final parameter of a method
                // is a function type, you can omit parens. This allows for
                // awesome DSL creation, similar to what you might find in
                // Gradle.
                .executor { source, context ->
                    // "is" replaces "instanceof".
                    if (source is Player) {
                        // Notice the lack of a cast -- Kotlin is really smart.
                        source.sendTitle(
                                Titles.builder()
                                        .subtitle("Kotlin".gold()
                                                + " says hi, ${source.name}")
                                        .stay(80).build())
                        CommandResult.success()
                    }
                    // Inside a lambda function or class, (similar to how Ruby
                    // works) the last statement is the lambda's return value.
                    CommandResult.empty()
                }.build(), "kotlin")

    }

}

// Multiple public classes in one file? Yep.
public class HelloListener : EventListener<ClientConnectionEvent.Join> {

    // override is a necessary keyword.
    override fun handle(event: ClientConnectionEvent.Join) {

        event.targetEntity.sendMessage("Hello from ".green()
                + "Kotlin"
                .gold()
                .bold()
                .hover(TextActions.showText("https://github.com/SpongePowered/Cookbook/tree/master/Plugin/HelloFromKotlin"
                        .blue().underline()))
                .click(TextActions.openUrl(URL("https://github.com/SpongePowered/Cookbook/tree/master/Plugin/HelloFromKotlin")))
                + "!".green())

    }

}
