package dev.rgbmc.syncable.utils;

import com.google.common.collect.Lists;
import dev.rgbmc.syncable.SyncableForge;
import dev.rgbmc.syncable.objects.AdvancementData;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AdvancementUtils {
    public static List<AdvancementData> getAdvancements(ServerPlayer player) {
        MinecraftServer server = SyncableForge.getServer();
        List<AdvancementData> advancements = new ArrayList<>();
        final Iterator<Advancement> advancementIterator =
                server.getAdvancements().getAllAdvancements().iterator();
        advancementIterator.forEachRemaining(
                advancement -> {
                    Map<String, Date> awardedCriteria = new HashMap<>();
                    AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
                    progress
                            .getCompletedCriteria()
                            .forEach(
                                    key -> {
                                        awardedCriteria.put(key, progress.getCriterion(key).getObtained());
                                    });

                    if (!awardedCriteria.isEmpty()) {
                        advancements.add(new AdvancementData(advancement.getId().toString(), awardedCriteria));
                    }
                });
        return advancements;
    }

    public static void setAdvancements(
            ServerPlayer player, List<AdvancementData> advancements) {
        MinecraftServer server = SyncableForge.getServer();
        final int totalExperience = player.totalExperience;
        final int level = player.experienceLevel;
        final float exp = player.experienceProgress;
        AtomicBoolean shouldRecoverExp = new AtomicBoolean(false);
        final Iterator<Advancement> advancementIterator =
                server.getAdvancements().getAllAdvancements().iterator();
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
                                player.getAdvancements().getOrStartProgress(advancement);
                        advancementData.getAwardedCriteria().keySet().stream()
                                .filter(key -> !Lists.newArrayList(progress.getCompletedCriteria()).contains(key))
                                .forEach(
                                        criteria -> {
                                            server.executeBlocking(
                                                    () -> {
                                                        player.getAdvancements().award(advancement, criteria);
                                                    });
                                            shouldRecoverExp.set(true);
                                        });
                        Lists.newArrayList(progress.getCompletedCriteria()).stream()
                                .filter(key -> !advancementData.getAwardedCriteria().containsKey(key))
                                .forEach(
                                        criteria ->
                                                player.getAdvancements().revoke(advancement, criteria));
                    } else {
                        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
                        progress
                                .getCompletedCriteria()
                                .forEach(
                                        criteria ->
                                                player.getAdvancements().revoke(advancement, criteria));
                    }
                });
        if (shouldRecoverExp.get()) {
            player.totalExperience = totalExperience;
            player.experienceProgress = exp;
            player.setExperienceLevels(level);
        }
    }
}
