package dev.rgbmc.syncable.objects;

import org.jetbrains.annotations.NotNull;

public enum BukkitStatistic {
  DAMAGE_DEALT,
  DAMAGE_TAKEN,
  DEATHS,
  MOB_KILLS,
  PLAYER_KILLS,
  FISH_CAUGHT,
  ANIMALS_BRED,
  LEAVE_GAME,
  JUMP,
  DROP_COUNT,
  DROP(Type.ITEM),
  PICKUP(Type.ITEM),
  PLAY_ONE_MINUTE,
  TOTAL_WORLD_TIME,
  WALK_ONE_CM,
  WALK_ON_WATER_ONE_CM,
  FALL_ONE_CM,
  SNEAK_TIME,
  CLIMB_ONE_CM,
  FLY_ONE_CM,
  WALK_UNDER_WATER_ONE_CM,
  MINECART_ONE_CM,
  BOAT_ONE_CM,
  PIG_ONE_CM,
  HORSE_ONE_CM,
  SPRINT_ONE_CM,
  CROUCH_ONE_CM,
  AVIATE_ONE_CM,
  MINE_BLOCK(Type.BLOCK),
  USE_ITEM(Type.ITEM),
  BREAK_ITEM(Type.ITEM),
  CRAFT_ITEM(Type.ITEM),
  KILL_ENTITY(Type.ENTITY),
  ENTITY_KILLED_BY(Type.ENTITY),
  TIME_SINCE_DEATH,
  TALKED_TO_VILLAGER,
  TRADED_WITH_VILLAGER,
  CAKE_SLICES_EATEN,
  CAULDRON_FILLED,
  CAULDRON_USED,
  ARMOR_CLEANED,
  BANNER_CLEANED,
  BREWINGSTAND_INTERACTION,
  BEACON_INTERACTION,
  DROPPER_INSPECTED,
  HOPPER_INSPECTED,
  DISPENSER_INSPECTED,
  NOTEBLOCK_PLAYED,
  NOTEBLOCK_TUNED,
  FLOWER_POTTED,
  TRAPPED_CHEST_TRIGGERED,
  ENDERCHEST_OPENED,
  ITEM_ENCHANTED,
  RECORD_PLAYED,
  FURNACE_INTERACTION,
  CRAFTING_TABLE_INTERACTION,
  CHEST_OPENED,
  SLEEP_IN_BED,
  SHULKER_BOX_OPENED,
  TIME_SINCE_REST,
  SWIM_ONE_CM,
  DAMAGE_DEALT_ABSORBED,
  DAMAGE_DEALT_RESISTED,
  DAMAGE_BLOCKED_BY_SHIELD,
  DAMAGE_ABSORBED,
  DAMAGE_RESISTED,
  CLEAN_SHULKER_BOX,
  OPEN_BARREL,
  INTERACT_WITH_BLAST_FURNACE,
  INTERACT_WITH_SMOKER,
  INTERACT_WITH_LECTERN,
  INTERACT_WITH_CAMPFIRE,
  INTERACT_WITH_CARTOGRAPHY_TABLE,
  INTERACT_WITH_LOOM,
  INTERACT_WITH_STONECUTTER,
  BELL_RING,
  RAID_TRIGGER,
  RAID_WIN,
  INTERACT_WITH_ANVIL,
  INTERACT_WITH_GRINDSTONE,
  TARGET_HIT,
  INTERACT_WITH_SMITHING_TABLE,
  STRIDER_ONE_CM;

  private final Type type;

  BukkitStatistic() {
    this(Type.UNTYPED);
  }

  BukkitStatistic(/*@NotNull*/ Type type) {
    this.type = type;
  }

  @NotNull
  public Type getType() {
    return type;
  }

  public boolean isSubstatistic() {
    return type != Type.UNTYPED;
  }

  public boolean isBlock() {
    return type == Type.BLOCK;
  }

  public enum Type {
    UNTYPED,
    ITEM,
    BLOCK,
    ENTITY
  }
}
