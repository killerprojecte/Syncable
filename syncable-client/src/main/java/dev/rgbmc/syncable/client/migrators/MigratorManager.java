package dev.rgbmc.syncable.client.migrators;

import java.util.HashMap;
import java.util.Map;

public class MigratorManager {
  private static final Map<String, Migrator> migrators = new HashMap<>();

  public static Map<String, Migrator> getMigrators() {
    return migrators;
  }

  public static void migrate(String name) {
    migrators.get(name).migrate();
  }

  public static boolean hasMigrator(String name) {
    return migrators.containsKey(name);
  }

  public static void registerMigrator(String name, Migrator migrator) {
    migrators.put(name, migrator);
  }
}
