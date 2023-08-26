package dev.rgbmc.syncable.objects;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.Map;

public class AdvancementData {
  @SerializedName("namespace")
  private String namespace;

  @SerializedName("awarded_criteria")
  private Map<String, Date> awardedCriteria;

  public AdvancementData(String namespace, Map<String, Date> awardedCriteria) {
    this.namespace = namespace;
    this.awardedCriteria = awardedCriteria;
  }

  public Map<String, Date> getAwardedCriteria() {
    return awardedCriteria;
  }

  public String getNamespace() {
    return namespace;
  }
}
