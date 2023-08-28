package dev.rgbmc.syncable.server.data;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@DatabaseTable(tableName = "syncable")
public class UserData implements Serializable {
  @DatabaseField(id = true, dataType = DataType.UUID)
  private UUID id;

  @DatabaseField(dataType = DataType.BYTE_ARRAY)
  private byte[] data;

  public UserData() {}

  public UserData(UUID id, byte[] data) {
    this.id = id;
    this.data = data;
  }

  public UserData(UUID id) {
    this.id = id;
  }

  public UserData(UUID id, String decodedData) {
    this.id = id;
    setData(decodedData);
  }

  public byte[] getEncodedData() {
    return data;
  }

  public void setEncodedData(byte[] data) {
    this.data = data;
  }

  public String getData() {
    return new String(Base64.getDecoder().decode(this.data), StandardCharsets.UTF_8);
  }

  public void setData(String data) {
    this.data = Base64.getEncoder().encode(data.getBytes(StandardCharsets.UTF_8));
  }
}
