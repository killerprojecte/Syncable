package dev.rgbmc.syncable.utils;

import com.google.common.collect.Lists;
import dev.rgbmc.syncable.SyncableFabric;
import dev.rgbmc.syncable.objects.AdvancementData;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class AdvancementUtils {
  public static List<AdvancementData> getAdvancements(ServerPlayerEntity player) {
    MinecraftServer server = SyncableFabric.getServer();
    List<AdvancementData> advancements = new ArrayList<>();
    final Iterator<Advancement> advancementIterator =
        server.getAdvancementLoader().getAdvancements().iterator();
    advancementIterator.forEachRemaining(
        advancement -> {
          Map<String, Date> awardedCriteria = new HashMap<>();
          AdvancementProgress progress = player.getAdvancementTracker().getProgress(advancement);
          progress
              .getObtainedCriteria()
              .forEach(
                  key -> {
                    awardedCriteria.put(key, progress.getCriterionProgress(key).getObtainedDate());
                  });

          if (!awardedCriteria.isEmpty()) {
            advancements.add(new AdvancementData(advancement.getId().toString(), awardedCriteria));
          }
        });
    return advancements;
  }

  public static void setAdvancements(
      ServerPlayerEntity player, List<AdvancementData> advancements) {
    MinecraftServer server = SyncableFabric.getServer();
    final int exp = player.totalExperience;
    AtomicBoolean shouldRecoverExp = new AtomicBoolean(false);
    final Iterator<Advancement> advancementIterator =
        server.getAdvancementLoader().getAdvancements().iterator();
    advancementIterator.forEachRemaining(
        advancement -> {
          Optional<AdvancementData> optional =
              advancements.stream()
                  .filter(
                      data -> data.getNamespace().equalsIgnoreCase(advancement.getId().toString()))
                  .findFirst();
          if (optional.isPresent()) {
            AdvancementData advancementData = optional.get();
            final AdvancementProgress progress =
                player.getAdvancementTracker().getProgress(advancement);
            advancementData.getAwardedCriteria().keySet().stream()
                .filter(key -> !Lists.newArrayList(progress.getObtainedCriteria()).contains(key))
                .forEach(
                    criteria -> {
                      server.executeSync(
                          () -> {
                            player.getAdvancementTracker().grantCriterion(advancement, criteria);
                          });
                      shouldRecoverExp.set(true);
                    });
            Lists.newArrayList(progress.getObtainedCriteria()).stream()
                .filter(key -> !advancementData.getAwardedCriteria().containsKey(key))
                .forEach(
                    criteria ->
                        player.getAdvancementTracker().revokeCriterion(advancement, criteria));
          } else {
            AdvancementProgress progress = player.getAdvancementTracker().getProgress(advancement);
            progress
                .getObtainedCriteria()
                .forEach(
                    criteria ->
                        player.getAdvancementTracker().revokeCriterion(advancement, criteria));
          }
        });
    if (shouldRecoverExp.get()) {
      player.totalExperience = exp;
      server.getPlayerManager().sendPlayerStatus(player);
    }
  }
}
