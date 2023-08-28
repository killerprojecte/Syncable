package dev.rgbmc.syncable.objects;

import com.google.gson.annotations.SerializedName;
import net.minecraft.entity.effect.StatusEffectInstance;

public class PotionData {
  @SerializedName("amplifier")
  private int amplifier;

  @SerializedName("duration")
  private int duration;

  @SerializedName("ambient")
  private boolean ambient;

  @SerializedName("particles")
  private boolean particles;

  @SerializedName("icon")
  private boolean icon;

  public PotionData(StatusEffectInstance effect) {
    amplifier = effect.getAmplifier();
    duration = effect.getDuration();
    ambient = effect.isAmbient();
    particles = effect.shouldShowParticles();
    icon = effect.shouldShowIcon();
  }

  public int getAmplifier() {
    return amplifier;
  }

  public int getDuration() {
    return duration;
  }

  public boolean isAmbient() {
    return ambient;
  }

  public boolean hasParticles() {
    return particles;
  }

  public boolean hasIcon() {
    return icon;
  }
}
