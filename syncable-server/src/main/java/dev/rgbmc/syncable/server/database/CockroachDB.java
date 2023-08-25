package dev.rgbmc.syncable.server.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.logger.Level;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.rgbmc.syncable.server.data.UserData;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class CockroachDB {
  public static Dao<UserData, UUID> users;
  private static CockroachDB instance;
  protected final ConnectionSource connectionSource;

  public CockroachDB(
      String host,
      String database,
      String user,
      String password,
      boolean ssl,
      int minimum_idle,
      int maximum_pool_size,
      int max_lifetime)
      throws SQLException {
    instance = this;
    connectionSource =
        getConnectionSource(
            host, database, user, password, ssl, minimum_idle, maximum_pool_size, max_lifetime);
    Logger.setGlobalLogLevel(Level.WARNING);
    users = DaoManager.createDao(connectionSource, UserData.class);
    users.setObjectCache(false);
    if (!users.isTableExists()) {
      TableUtils.createTable(this.connectionSource, UserData.class);
    }
  }

  public static CockroachDB getInstance() {
    return instance;
  }

  protected ConnectionSource getConnectionSource(
      String host,
      String database,
      String user,
      String password,
      boolean ssl,
      int minimum_idle,
      int maximum_pool_size,
      int max_lifetime)
      throws SQLException {
    HikariConfig config = new HikariConfig();
    config.setPoolName("Syncable-CockroachDB");
    String url =
        "jdbc:postgresql://"
            + host
            + "/"
            + database
            + "?useSSL="
            + ssl
            + "&serverTimezone=UTC&autoReconnect=true&allowPublicKeyRetrieval=true&characterEncoding=utf8";
    config.setJdbcUrl(url);
    config.setUsername(user);
    config.setPassword(password);
    config.setMinimumIdle(minimum_idle > 0 ? minimum_idle : 4);
    config.setMaximumPoolSize(maximum_pool_size);
    config.setMaxLifetime(max_lifetime);
    config.setDriverClassName("org.postgresql.Driver");
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    HikariDataSource dataSource = new HikariDataSource(config);

    return new HikariConnectionSource(dataSource, url);
  }

  public List<UserData> queryAll() throws SQLException {
    return users.queryForAll();
  }

  public boolean createOrUpdate(UserData dto) throws SQLException {
    return users.createOrUpdate(dto).isUpdated();
  }

  public UserData getUserData(UUID uuid) throws SQLException {
    return users.queryForId(uuid);
  }

  public boolean hasRegistered(UUID id) throws SQLException {
    return users.idExists(id);
  }

  public void remove(UUID id) throws SQLException {
    users.deleteById(id);
  }

  public void close() {
    connectionSource.closeQuietly();
  }

  public static class HikariConnectionSource extends DataSourceConnectionSource {
    private final HikariDataSource hikariDataSource;

    public HikariConnectionSource(HikariDataSource source, String url) throws SQLException {
      super(source, url);
      hikariDataSource = source;
    }

    @Override
    public void close() throws Exception {
      super.close();
      hikariDataSource.close();
    }
  }
}
