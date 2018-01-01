package org.spongepowered.cookbook.myhomes;

import org.spongepowered.api.Sponge;
import org.spongepowered.cookbook.myhomes.data.Keys;
import org.spongepowered.cookbook.myhomes.data.friends.impl.FriendsDataBuilder;
import org.spongepowered.cookbook.myhomes.data.friends.FriendsData;
import org.spongepowered.cookbook.myhomes.data.friends.ImmutableFriendsData;
import org.spongepowered.cookbook.myhomes.data.home.Home;
import org.spongepowered.cookbook.myhomes.data.home.HomeData;
import org.spongepowered.cookbook.myhomes.data.home.ImmutableHomeData;
import org.spongepowered.cookbook.myhomes.data.home.impl.HomeBuilder;
import org.spongepowered.cookbook.myhomes.data.home.impl.HomeDataBuilder;
import org.spongepowered.api.data.DataManager;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextStyles;

import com.google.inject.Inject;

@Plugin(id = "myhomes")
public class MyHomes {

    @Inject
    public PluginContainer container;

    @Listener
    public void onGameInitialization(GameInitializationEvent event) {
        DataManager dm = Sponge.getDataManager();

        // FriendsData
        DataRegistration.builder()
                .dataClass(FriendsData.class)
                .immutableClass(ImmutableFriendsData.class)
                .builder(new FriendsDataBuilder())
                .manipulatorId("friends")
                .dataName("Friends")
                .buildAndRegister(container);

        // Home
        dm.registerBuilder(Home.class, new HomeBuilder());
        dm.registerContentUpdater(Home.class, new HomeBuilder.NameUpdater());
        // Or, we could use a translator instead of implementing DataSerializable (only ONE is required)
        // dm.registerTranslator(Home.class, new HomeTranslator());

        // Home Data
        DataRegistration.builder()
                .dataClass(HomeData.class)
                .immutableClass(ImmutableHomeData.class)
                .builder(new HomeDataBuilder())
                .manipulatorId("homes")
                .dataName("Homes")
                .buildAndRegister(container);
        dm.registerContentUpdater(HomeData.class, new HomeDataBuilder.HomesUpdater());
    }

    @Listener
    public void onClientConnectionJoin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
        player.get(Keys.DEFAULT_HOME).ifPresent(home -> {
            player.setTransform(home.getTransform());
            player.sendMessage(ChatTypes.ACTION_BAR,
                    Text.of("Teleported to home - ", TextStyles.BOLD, home.getName()));
        });
    }
}
