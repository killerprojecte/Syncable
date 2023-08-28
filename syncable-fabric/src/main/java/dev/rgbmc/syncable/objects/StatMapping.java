package dev.rgbmc.syncable.objects;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;

public enum StatMapping {
  DAMAGE_DEALT(Stats.DAMAGE_DEALT),
  DAMAGE_TAKEN(Stats.DAMAGE_TAKEN),
  DEATHS(Stats.DEATHS),
  MOB_KILLS(Stats.MOB_KILLS),
  PLAYER_KILLS(Stats.PLAYER_KILLS),
  FISH_CAUGHT(Stats.FISH_CAUGHT),
  ANIMALS_BRED(Stats.ANIMALS_BRED),
  LEAVE_GAME(Stats.LEAVE_GAME),
  JUMP(Stats.JUMP),
  DROP_COUNT(Stats.DROP),
  DROP(new Identifier("dropped")),
  PICKUP(new Identifier("picked_up")),
  PLAY_ONE_MINUTE(Stats.PLAY_TIME),
  TOTAL_WORLD_TIME(Stats.TOTAL_WORLD_TIME),
  WALK_ONE_CM(Stats.WALK_ONE_CM),
  WALK_ON_WATER_ONE_CM(Stats.WALK_ON_WATER_ONE_CM),
  FALL_ONE_CM(Stats.FALL_ONE_CM),
  SNEAK_TIME(Stats.SNEAK_TIME),
  CLIMB_ONE_CM(Stats.CLIMB_ONE_CM),
  FLY_ONE_CM(Stats.FLY_ONE_CM),
  WALK_UNDER_WATER_ONE_CM(Stats.WALK_UNDER_WATER_ONE_CM),
  MINECART_ONE_CM(Stats.MINECART_ONE_CM),
  BOAT_ONE_CM(Stats.BOAT_ONE_CM),
  PIG_ONE_CM(Stats.PIG_ONE_CM),
  HORSE_ONE_CM(Stats.HORSE_ONE_CM),
  SPRINT_ONE_CM(Stats.SPRINT_ONE_CM),
  CROUCH_ONE_CM(Stats.CROUCH_ONE_CM),
  AVIATE_ONE_CM(Stats.AVIATE_ONE_CM),
  MINE_BLOCK(new Identifier("mined")),
  USE_ITEM(new Identifier("used")),
  BREAK_ITEM(new Identifier("broken")),
  CRAFT_ITEM(new Identifier("crafted")),
  KILL_ENTITY(new Identifier("killed")),
  ENTITY_KILLED_BY(new Identifier("killed_by")),
  TIME_SINCE_DEATH(Stats.TIME_SINCE_DEATH),
  TALKED_TO_VILLAGER(Stats.TALKED_TO_VILLAGER),
  TRADED_WITH_VILLAGER(Stats.TRADED_WITH_VILLAGER),
  CAKE_SLICES_EATEN(Stats.EAT_CAKE_SLICE),
  CAULDRON_FILLED(Stats.FILL_CAULDRON),
  CAULDRON_USED(Stats.USE_CAULDRON),
  ARMOR_CLEANED(Stats.CLEAN_ARMOR),
  BANNER_CLEANED(Stats.CLEAN_BANNER),
  BREWINGSTAND_INTERACTION(Stats.INTERACT_WITH_BREWINGSTAND),
  BEACON_INTERACTION(Stats.INTERACT_WITH_BEACON),
  DROPPER_INSPECTED(Stats.INSPECT_DROPPER),
  HOPPER_INSPECTED(Stats.INSPECT_HOPPER),
  DISPENSER_INSPECTED(Stats.INSPECT_DISPENSER),
  NOTEBLOCK_PLAYED(Stats.PLAY_NOTEBLOCK),
  NOTEBLOCK_TUNED(Stats.TUNE_NOTEBLOCK),
  FLOWER_POTTED(Stats.POT_FLOWER),
  TRAPPED_CHEST_TRIGGERED(Stats.TRIGGER_TRAPPED_CHEST),
  ENDERCHEST_OPENED(Stats.OPEN_ENDERCHEST),
  ITEM_ENCHANTED(Stats.ENCHANT_ITEM),
  RECORD_PLAYED(Stats.PLAY_RECORD),
  FURNACE_INTERACTION(Stats.INTERACT_WITH_FURNACE),
  CRAFTING_TABLE_INTERACTION(Stats.INTERACT_WITH_CRAFTING_TABLE),
  CHEST_OPENED(Stats.OPEN_CHEST),
  SLEEP_IN_BED(Stats.SLEEP_IN_BED),
  SHULKER_BOX_OPENED(Stats.OPEN_SHULKER_BOX),
  TIME_SINCE_REST(Stats.TIME_SINCE_REST),
  SWIM_ONE_CM(Stats.SWIM_ONE_CM),
  DAMAGE_DEALT_ABSORBED(Stats.DAMAGE_DEALT_ABSORBED),
  DAMAGE_DEALT_RESISTED(Stats.DAMAGE_DEALT_RESISTED),
  DAMAGE_BLOCKED_BY_SHIELD(Stats.DAMAGE_BLOCKED_BY_SHIELD),
  DAMAGE_ABSORBED(Stats.DAMAGE_ABSORBED),
  DAMAGE_RESISTED(Stats.DAMAGE_RESISTED),
  CLEAN_SHULKER_BOX(Stats.CLEAN_SHULKER_BOX),
  OPEN_BARREL(Stats.OPEN_BARREL),
  INTERACT_WITH_BLAST_FURNACE(Stats.INTERACT_WITH_BLAST_FURNACE),
  INTERACT_WITH_SMOKER(Stats.INTERACT_WITH_SMOKER),
  INTERACT_WITH_LECTERN(Stats.INTERACT_WITH_LECTERN),
  INTERACT_WITH_CAMPFIRE(Stats.INTERACT_WITH_CAMPFIRE),
  INTERACT_WITH_CARTOGRAPHY_TABLE(Stats.INTERACT_WITH_CARTOGRAPHY_TABLE),
  INTERACT_WITH_LOOM(Stats.INTERACT_WITH_LOOM),
  INTERACT_WITH_STONECUTTER(Stats.INTERACT_WITH_STONECUTTER),
  BELL_RING(Stats.BELL_RING),
  RAID_TRIGGER(Stats.RAID_TRIGGER),
  RAID_WIN(Stats.RAID_WIN),
  INTERACT_WITH_ANVIL(Stats.INTERACT_WITH_ANVIL),
  INTERACT_WITH_GRINDSTONE(Stats.INTERACT_WITH_GRINDSTONE),
  TARGET_HIT(Stats.TARGET_HIT),
  INTERACT_WITH_SMITHING_TABLE(Stats.INTERACT_WITH_SMITHING_TABLE),
  STRIDER_ONE_CM(Stats.STRIDER_ONE_CM);
  private static final BiMap<Identifier, BukkitStatistic> statistics;

  static {
    ImmutableBiMap.Builder<Identifier, BukkitStatistic> statisticBuilder = ImmutableBiMap.builder();
    for (StatMapping statistic : StatMapping.values()) {
      statisticBuilder.put(statistic.minecraftKey, statistic.bukkit);
    }

    statistics = statisticBuilder.build();
  }

  private final Identifier minecraftKey;
  private final BukkitStatistic bukkit;

  StatMapping(Identifier minecraftKey) {
    this.minecraftKey = minecraftKey;
    this.bukkit = BukkitStatistic.valueOf(this.name());
  }

  public static Stat getNMSStatistic(BukkitStatistic bukkit) {
    Preconditions.checkArgument(
        bukkit.getType() == BukkitStatistic.Type.UNTYPED,
        "This method only accepts untyped statistics");

    Stat<Identifier> nms = Stats.CUSTOM.getOrCreateStat(statistics.inverse().get(bukkit));
    Preconditions.checkArgument(nms != null, "NMS Statistic %s does not exist", bukkit);

    return nms;
  }

  public static Stat getMaterialStatistic(BukkitStatistic stat, Item item) {
    try {
      if (stat == BukkitStatistic.MINE_BLOCK) {
        return Stats.MINED.getOrCreateStat(Block.getBlockFromItem(item));
      }
      if (stat == BukkitStatistic.CRAFT_ITEM) {
        return Stats.CRAFTED.getOrCreateStat(item);
      }
      if (stat == BukkitStatistic.USE_ITEM) {
        return Stats.USED.getOrCreateStat(item);
      }
      if (stat == BukkitStatistic.BREAK_ITEM) {
        return Stats.BROKEN.getOrCreateStat(item);
      }
      if (stat == BukkitStatistic.PICKUP) {
        return Stats.PICKED_UP.getOrCreateStat(item);
      }
      if (stat == BukkitStatistic.DROP) {
        return Stats.DROPPED.getOrCreateStat(item);
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      return null;
    }
    return null;
  }

  public static Stat getEntityStatistic(BukkitStatistic stat, EntityType entityType) {
    if (stat == BukkitStatistic.KILL_ENTITY) {
      return Stats.KILLED.getOrCreateStat(entityType);
    }
    if (stat == BukkitStatistic.ENTITY_KILLED_BY) {
      return Stats.KILLED_BY.getOrCreateStat(entityType);
    }
    return null;
  }
}
