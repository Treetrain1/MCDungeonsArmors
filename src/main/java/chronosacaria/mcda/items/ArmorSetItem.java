package chronosacaria.mcda.items;

import chronosacaria.mcda.Mcda;
import chronosacaria.mcda.config.ArmorStats;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class ArmorSetItem extends ArmorItem {

    protected static final UUID[] ARMOR_MODIFIERS = new UUID[] {
            UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
            UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
            UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
            UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

    protected final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;
    protected final ArmorSets set;

    public ArmorSetItem(ArmorSets set, EquipmentSlot slot) {
        super(set, slot, new Item.Settings().group(Mcda.ARMORS_GROUP));
        this.set = set;

        int protection = set.getProtectionAmount(slot);
        float toughness = set.getToughness();

        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        UUID uuid = ARMOR_MODIFIERS[slot.getEntitySlotId()];
        builder.put(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(uuid, "Armor modifier",
                protection, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new EntityAttributeModifier(uuid, "Armor toughness",
                toughness, EntityAttributeModifier.Operation.ADDITION));
        if (this.knockbackResistance > 0) {
            builder.put(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, new EntityAttributeModifier(uuid, "Armor knockback resistance",
                    this.knockbackResistance, EntityAttributeModifier.Operation.ADDITION));
        }

        ArmorStats armorStats = Mcda.CONFIG.mcdaArmorStatsConfig.armorStats.get(set);

        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(uuid,
                "Armor attack damage boost",
                armorStats.attackDamageBoost,
                EntityAttributeModifier.Operation.MULTIPLY_BASE));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(uuid,
                "Armor attack speed boost",
                armorStats.attackSpeedBoost, EntityAttributeModifier.Operation.MULTIPLY_BASE));
        builder.put(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier(uuid,
                "Armor movement speed boost",
                armorStats.movementSpeedBoost, EntityAttributeModifier.Operation.MULTIPLY_BASE));

        this.attributeModifiers = builder.build();
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == this.slot ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }

    @Override
    public Rarity getRarity(ItemStack itemStack) {
        return set.getRarity();
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        super.appendTooltip(itemStack, world, tooltip, tooltipContext);

        String setId = switch (set) {
            case MYSTERY, BLUE_MYSTERY, GREEN_MYSTERY, PURPLE_MYSTERY, RED_MYSTERY -> "mystery_armor";
            default -> set.getSetName();
        };

        String translationKey = String.format("item.mcda.%s.tooltip_", setId);
        int i = 1;

        while (I18n.hasTranslation(translationKey + i)) {
            tooltip.add(Text.translatable(translationKey + i).formatted(Formatting.ITALIC));
            i++;
        }

        if (Mcda.CONFIG.mcdaArmorStatsConfig.setBonusTooltips) {
            translationKey = String.format("item.mcda.%s.effect.tooltip_", setId);
            i = 1;

            while (I18n.hasTranslation(translationKey + i)) {
                tooltip.add(Text.translatable(translationKey + i).formatted(
                        Mcda.CONFIG.mcdaArmorStatsConfig.setBonusTooltipColors ?
                                switch (set) {
                                    case MYSTERY -> Formatting.WHITE;
                                    case BLUE_MYSTERY, FROST, PHANTOM, FROST_BITE, NIMBLE_TURTLE, GLOW_SQUID -> Formatting.DARK_AQUA;
                                    case GREEN_MYSTERY, HIGHLAND, CAVE_CRAWLER, HERO, OPULENT, VERDANT -> Formatting.GREEN;
                                    case PURPLE_MYSTERY, CURIOUS, THIEF -> Formatting.LIGHT_PURPLE;
                                    case RED_MYSTERY, LIVING_VINES, SPROUT, GHOST_KINDLER, GOURDIAN, BLACK_WOLF, RENEGADE, STALWART_MAIL, WITHER -> Formatting.RED;
                                    case STURDY_SHULKER, SPIDER, SOULDANCER -> i == 1 ? Formatting.LIGHT_PURPLE : Formatting.GRAY;
                                    case SHADOW_WALKER -> i == 1 ? Formatting.GREEN : Formatting.LIGHT_PURPLE;
                                    case CAULDRON, TITAN, SPLENDID, TROUBADOUR -> Formatting.BOLD;
                                    default -> Formatting.GRAY;
                                } : Formatting.GRAY
                ));
                i++;
            }


            if (slot == EquipmentSlot.FEET && (set == ArmorSets.RUGGED_CLIMBING_GEAR || set == ArmorSets.SNOW || set == ArmorSets.GOAT)) {
                tooltip.add(Text.translatable("Lightfooted").formatted(Mcda.CONFIG.mcdaArmorStatsConfig.setBonusTooltipColors ? Formatting.AQUA : Formatting.GRAY));
            }
        }

    }
}
