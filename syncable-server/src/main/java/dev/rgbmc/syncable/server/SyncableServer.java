package dev.rgbmc.syncable.server;

import dev.rgbmc.syncable.server.database.CockroachDB;
import dev.rgbmc.syncable.server.websocket.SyncableProtocolServer;

import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.logging.Logger;

public class SyncableServer {
  private static SyncableServer instance;
  private final Logger logger;
  private CockroachDB database = null;
  private SyncableProtocolServer protocolServer;
  private int port = 55541;

  public SyncableServer() {
    instance = this;
    logger = Logger.getLogger("Syncable-Server");
  }

  public static SyncableServer getInstance() {
    return instance;
  }

  public void start(
      String host,
      String database,
      String user,
      String password,
      boolean ssl,
      int minimum_idle,
      int maximum_pool_size,
      int max_lifetime)
      throws SQLException {
    if (this.database != null) {
      throw new RuntimeException("Syncable-Server already started");
    }
    this.database =
        new CockroachDB(
            host, database, user, password, ssl, minimum_idle, maximum_pool_size, max_lifetime);
    new Thread(
            () -> {
              getLogger().info("Syncable protocol server starting....");
              protocolServer = new SyncableProtocolServer(new InetSocketAddress(port));
              protocolServer.setConnectionLostTimeout(0);
              protocolServer.setMaxPendingConnections(100000);
              protocolServer.start();
            })
        .start();
  }

  public void stop() {
    if (database == null) {
      throw new RuntimeException("Syncable-Server not running");
    }
    try {
      protocolServer.stop();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    getDatabase().close();
    database = null;
  }

  public CockroachDB getDatabase() {
    return database;
  }

  public Logger getLogger() {
    return logger;
  }

  public void setPort(int port) {
    this.port = port;
  }
}
