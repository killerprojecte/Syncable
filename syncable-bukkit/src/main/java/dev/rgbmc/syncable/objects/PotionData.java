package dev.rgbmc.syncable.objects;

import com.google.gson.annotations.SerializedName;
import org.bukkit.potion.PotionEffect;

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

  public PotionData(PotionEffect effect) {
    amplifier = effect.getAmplifier();
    duration = effect.getDuration();
    ambient = effect.isAmbient();
    particles = effect.hasParticles();
    icon = effect.hasIcon();
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
