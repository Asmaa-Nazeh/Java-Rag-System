package com.asmaa.rag;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;

import java.util.concurrent.TimeUnit;

public class MilvusConnection {

    private static MilvusConnection instance;
    private static final Object lock = new Object();

    // Default connection parameters
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 19530;
    private static final int DEFAULT_TIMEOUT = 10;

    private MilvusServiceClient client;
    private final String host;
    private final int port;
    private final int timeoutSeconds;

    // Private constructor to prevent direct instantiation
    private MilvusConnection(String host, int port, int timeoutSeconds) {
        this.host = host;
        this.port = port;
        this.timeoutSeconds = timeoutSeconds;
        this.client = createConnection();
    }

    /**
     * Get singleton instance with default connection parameters
     */
    public static MilvusConnection getInstance() {
        return getInstance(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_TIMEOUT);
    }

    /**
     * Get singleton instance with custom connection parameters
     */
    public static MilvusConnection getInstance(String host, int port, int timeoutSeconds) {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new MilvusConnection(host, port, timeoutSeconds);
                }
            }
        }
        return instance;
    }

    /**
     * Create a new Milvus client connection
     */
    private MilvusServiceClient createConnection() {
        try {
            ConnectParam connectParam = ConnectParam.newBuilder()
                    .withHost(host)
                    .withPort(port)
                    .withConnectTimeout(timeoutSeconds, TimeUnit.SECONDS)
                    .build();

            MilvusServiceClient newClient = new MilvusServiceClient(connectParam);
            System.out.println("Connected to Milvus at " + host + ":" + port);
            return newClient;

        } catch (Exception e) {
            System.err.println("Failed to connect to Milvus: " + e.getMessage());
            throw new RuntimeException("Could not establish connection to Milvus", e);
        }
    }

    /**
     * Get the current Milvus client, creating a new one if the current one is closed
     */
    public MilvusServiceClient getClient() {
        if (client == null) {
            synchronized (lock) {
                if (client == null) {
                    client = createConnection();
                }
            }
        }
        return client;
    }

    /**
     * Check if the connection is active
     */
    public boolean isConnected() {
        return client != null;
    }

    /**
     * Reconnect to Milvus (useful if connection is lost)
     */
    public void reconnect() {
        synchronized (lock) {
            if (client != null) {
                try {
                    client.close();
                } catch (Exception e) {
                    System.err.println("⚠️ Error closing existing connection: " + e.getMessage());
                }
            }
            client = createConnection();
        }
    }

    /**
     * Close the connection
     */
    public void close() {
        synchronized (lock) {
            if (client != null) {
                try {
                    client.close();
                    client = null;
                    System.out.println("🔒 Milvus connection closed.");
                } catch (Exception e) {
                    System.err.println("❌ Error closing Milvus connection: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Get connection info as string
     */
    public String getConnectionInfo() {
        return String.format("Milvus Connection [%s:%d, timeout=%ds, connected=%s]",
                           host, port, timeoutSeconds, isConnected());
    }

    /**
     * Factory method to create a new client (not singleton)
     * Use this when you need multiple independent connections
     */
    public static MilvusServiceClient createNewClient(String host, int port, int timeoutSeconds) {
        ConnectParam connectParam = ConnectParam.newBuilder()
                .withHost(host)
                .withPort(port)
                .withConnectTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .build();
        return new MilvusServiceClient(connectParam);
    }

    /**
     * Factory method with default parameters
     */
    public static MilvusServiceClient createNewClient() {
        return createNewClient(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_TIMEOUT);
    }
}
