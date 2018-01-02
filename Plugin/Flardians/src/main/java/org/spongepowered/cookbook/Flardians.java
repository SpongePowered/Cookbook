package org.spongepowered.cookbook;

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.data.manipulator.mutable.entity.TradeOfferData;
import org.spongepowered.api.data.manipulator.mutable.item.LoreData;
import org.spongepowered.api.data.type.Careers;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.merchant.TradeOffer;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.List;
import java.util.Random;

@Plugin(id = "flardians", name = "Flardians", version = "0.5", description = "BUY FLARD HERE")
public class Flardians {

    private static final ItemType[] SELL_TYPES = new ItemType[]{ItemTypes.SLIME_BALL, ItemTypes.HARDENED_CLAY, ItemTypes.BLAZE_ROD, ItemTypes.APPLE,
            ItemTypes.GHAST_TEAR, ItemTypes.COBBLESTONE, ItemTypes.STICK, ItemTypes.EMERALD,};
    private static final List<ItemType> BUYING_TYPES = ImmutableList.of(ItemTypes.ACACIA_DOOR, ItemTypes.LEAVES2, ItemTypes.BOOKSHELF, ItemTypes.COAL,
            ItemTypes.COBBLESTONE, ItemTypes.ANVIL, ItemTypes.IRON_ORE, ItemTypes.APPLE,
            ItemTypes.WHEAT_SEEDS, ItemTypes.DIRT);
    private static final Text FLARDARIAN = Text.of(TextColors.DARK_AQUA, TextStyles.BOLD, TextStyles.ITALIC, "Flardarian");
    private static final Text ITEM_DISPLAY = Text.of(TextColors.YELLOW, TextStyles.BOLD, "[", TextColors.GREEN, TextStyles.ITALIC, "FLARD",
            TextStyles.RESET, TextColors.YELLOW, TextStyles.BOLD, "]");
    private static final Text LORE_FIRST = Text.of(TextColors.BLUE, TextStyles.ITALIC, "This is indeed a glorious day!");
    private static final Text LORE_SECOND = Text.of(TextColors.BLUE, TextStyles.ITALIC, "Shining sun makes the clouds flee");
    private static final Text LORE_THIRD = Text.of(TextColors.BLUE, TextStyles.ITALIC, "With State of ", TextColors.YELLOW, "Sponge",
            TextColors.BLUE, " again today");
    private static final Text LORE_FOURTH = Text.of(TextColors.BLUE, TextStyles.ITALIC, "Granting delights for you and me");
    private static final Text LORE_FIFTH = Text.of(TextColors.BLUE, TextStyles.ITALIC, "For ", TextColors.YELLOW, "Sponge", TextColors.BLUE,
            " is in a State of play");
    private static final Text LORE_SIXTH = Text.of(TextColors.BLUE, TextStyles.ITALIC, "Today, be happy as can be!");
    private static final ImmutableList<Text> LORE = ImmutableList.of(LORE_FIRST, LORE_SECOND, LORE_THIRD, LORE_FOURTH, LORE_FIFTH, LORE_SIXTH);
    private static final Random RANDOM = new Random();

    @Listener
    public void onSpawn(SpawnEntityEvent event) {
        event.getEntities().stream()
                .filter(entity1 -> entity1.getType().equals(EntityTypes.VILLAGER) && Math.random() > 0.7)
                .forEach(villager -> {
                    villager.offer(Keys.CAREER, Careers.CLERIC);
                    villager.offer(Keys.DISPLAY_NAME, FLARDARIAN);
                    villager.offer(Keys.CUSTOM_NAME_VISIBLE, true);
                    villager.offer(Keys.INVULNERABILITY_TICKS, 10000);
                    villager.offer(generateTradeOffer());
                });
    }

    private TradeOfferData generateTradeOffer() {
        final int rand = RANDOM.nextInt(7);
        final int itemRand = RANDOM.nextInt(BUYING_TYPES.size());

        final DisplayNameData itemName = Sponge.getDataManager().getManipulatorBuilder(DisplayNameData.class).get().create();
        itemName.set(Keys.DISPLAY_NAME, ITEM_DISPLAY);

        // Set up the lore data.
        final LoreData loreData = Sponge.getDataManager().getManipulatorBuilder(LoreData.class).get().create();
        final ListValue<Text> lore = loreData.lore();
        lore.addAll(LORE);
        loreData.set(lore);

        // Create the selling item
        final ItemStack selling = ItemStack.builder()
                .itemType(BUYING_TYPES.get(itemRand))
                .itemData(itemName)
                .itemData(loreData)
                .quantity(1)
                .build();

        // Create the buying item
        final ItemStack buying = ItemStack.builder()
                .itemType(SELL_TYPES[rand])
                .quantity(1)
                .build();

        final TradeOfferData tradeOfferData = Sponge.getDataManager().getManipulatorBuilder(TradeOfferData.class).get().create();
        tradeOfferData.set(tradeOfferData.tradeOffers()
                .add(TradeOffer.builder()
                        .firstBuyingItem(buying)
                        .maxUses(10000)
                        .sellingItem(selling)
                        .build()));
        return tradeOfferData;
    }

}
