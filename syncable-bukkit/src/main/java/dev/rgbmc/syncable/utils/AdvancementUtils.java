package dev.rgbmc.syncable.utils;

import com.google.gson.JsonObject;
import dev.rgbmc.syncable.SyncableBukkit;
import dev.rgbmc.syncable.objects.AdvancementData;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AdvancementUtils {
  public static List<AdvancementData> getAdvancements(Player player) {
    List<AdvancementData> advancements = new ArrayList<>();
    final Iterator<Advancement> advancementIterator = Bukkit.advancementIterator();
    advancementIterator.forEachRemaining(
        advancement -> {
          Map<String, Date> awardedCriteria = new HashMap<>();
          AdvancementProgress progress = player.getAdvancementProgress(advancement);
          progress
              .getAwardedCriteria()
              .forEach(
                  key -> {
                    awardedCriteria.put(key, progress.getDateAwarded(key));
                  });

          if (!awardedCriteria.isEmpty()) {
            advancements.add(new AdvancementData(advancement.getKey().toString(), awardedCriteria));
          }
        });
    return advancements;
  }

  public static void setAdvancements(Player player, List<AdvancementData> advancements) {
    final int exp = player.getTotalExperience();
    AtomicBoolean shouldRecoverExp = new AtomicBoolean(false);
    final Iterator<Advancement> advancementIterator = Bukkit.advancementIterator();
    advancementIterator.forEachRemaining(
        advancement -> {
          Optional<AdvancementData> optional =
              advancements.stream()
                  .filter(
                      data -> data.getNamespace().equalsIgnoreCase(advancement.getKey().toString()))
                  .findFirst();
          if (optional.isPresent()) {
            AdvancementData advancementData = optional.get();
            final AdvancementProgress progress = player.getAdvancementProgress(advancement);
            advancementData.getAwardedCriteria().keySet().stream()
                .filter(key -> !progress.getAwardedCriteria().contains(key))
                .forEach(
                    criteria -> {
                      SyncableBukkit.getScheduler()
                          .runTask(
                              () -> {
                                player.getAdvancementProgress(advancement).awardCriteria(criteria);
                              });
                      shouldRecoverExp.set(true);
                    });
            progress.getAwardedCriteria().stream()
                .filter(key -> !advancementData.getAwardedCriteria().containsKey(key))
                .forEach(
                    criteria ->
                        player.getAdvancementProgress(advancement).revokeCriteria(criteria));
          } else {
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            progress
                .getAwardedCriteria()
                .forEach(
                    criteria ->
                        player.getAdvancementProgress(advancement).revokeCriteria(criteria));
          }
        });
    if (shouldRecoverExp.get()) {
      player.setTotalExperience(exp);
    }
  }
}
