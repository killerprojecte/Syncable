package dev.rgbmc.syncable.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AdvancementsData {
    @SerializedName("advancements")
    private List<AdvancementData> advancements;

    public AdvancementsData(List<AdvancementData> advancements) {
        this.advancements = advancements;
    }

    public List<AdvancementData> getAdvancements() {
        return advancements;
    }
}
