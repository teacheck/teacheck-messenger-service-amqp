package io.teacheck.messenger.constants;

public final class Constants {
    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String BROKER_HOST = System.getenv("BROKER_HOST");
    public static final int BROKER_PORT = Integer.parseInt(System.getenv("BROKER_PORT"));
    public static final String BROKER_PASSWORD = System.getenv("BROKER_PASSWORD");
    public static final String BROKER_USERNAME = System.getenv("BROKER_USERNAME");
    public static final int BROKER_TIMEOUT = Integer.parseInt(System.getenv("BROKER_TIMEOUT"));
    public static final String BROKER_QUEUE_NAME = System.getenv("BROKER_QUEUE_NAME");

    public static final String DB_SERVICE_HOST = System.getenv("DB_SERVICE_HOST");
    public static final int DB_SERVICE_PORT = Integer.parseInt(System.getenv("DB_SERVICE_PORT"));
}
