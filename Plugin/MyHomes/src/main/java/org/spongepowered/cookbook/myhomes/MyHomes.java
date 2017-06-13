package org.spongepowered.cookbook.myhomes;

import org.spongepowered.api.Sponge;
import org.spongepowered.cookbook.myhomes.data.Keys;
import org.spongepowered.cookbook.myhomes.data.friends.impl.FriendsDataBuilder;
import org.spongepowered.cookbook.myhomes.data.friends.impl.FriendsDataImpl;
import org.spongepowered.cookbook.myhomes.data.friends.impl.ImmutableFriendsDataImpl;
import org.spongepowered.cookbook.myhomes.data.home.Home;
import org.spongepowered.cookbook.myhomes.data.home.HomeData;
import org.spongepowered.cookbook.myhomes.data.home.ImmutableHomeData;
import org.spongepowered.cookbook.myhomes.data.home.impl.HomeBuilder;
import org.spongepowered.cookbook.myhomes.data.home.impl.HomeDataBuilder;
import org.spongepowered.api.data.DataManager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextStyles;

@Plugin(id = "myhomes")
public class MyHomes {

    @Listener
    public void onGameInitialization(GameInitializationEvent event) {
        DataManager dm = Sponge.getDataManager();

        // FriendsData
        dm.register(FriendsDataImpl.class, ImmutableFriendsDataImpl.class, new FriendsDataBuilder());

        // Home
        dm.registerBuilder(Home.class, new HomeBuilder());
        dm.registerContentUpdater(Home.class, new HomeBuilder.NameUpdater());
        // Or, we could use a translator instead of implementing DataSerializable (only ONE is required)
        // dm.registerTranslator(Home.class, new HomeTranslator());

        // Home Data
        dm.register(HomeData.class, ImmutableHomeData.class, new HomeDataBuilder());
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
