package org.orm;

/**
 * The type Configure postgres am mySql.
 */
public class ConfigureConnectionPool {

    private ConfigureConnectionPool() {}
    private static boolean cachePrepStmts = true;        // Кеширование подготовленных запросов
    private static int prepStmtCacheSize = 250;       // Размер кеша
    private static int prepStmtCacheSqlLimit = 2048;
    private static int maximumPoolSize = 30;

    /**
     * Sets maximum pool size.
     *
     * @param value the value
     */
    public static void setMaximumPoolSize(int value) {
        maximumPoolSize = value;
    }

    /**
     * Gets cache prep stmts.
     *
     * @return the cache prep stmts
     */
    public static boolean getCachePrepStmts() {
        return cachePrepStmts;
    }

    /**
     * Gets prep stmt cache size.
     *
     * @return the prep stmt cache size
     */
    public static int getPrepStmtCacheSize() {
        return prepStmtCacheSize;
    }

    /**
     * Gets prep stmt cache sql limit.
     *
     * @return the prep stmt cache sql limit
     */
    public static int getPrepStmtCacheSqlLimit() {
        return prepStmtCacheSqlLimit;
    }

    /**
     * Sets prep stmt cache sql limit.
     *
     * @param value the value
     */
    public static void setPrepStmtCacheSqlLimit(int value) {

        ConfigureConnectionPool.prepStmtCacheSqlLimit = value;

    }

    /**
     * Sets prep stmt cache size.
     *
     * @param value the value
     */
    public static void setPrepStmtCacheSize(int value) {

        ConfigureConnectionPool.prepStmtCacheSize = value;

    }

    /**
     * Sets cache prep stmts.
     *
     * @param value the value
     */
    public static void setCachePrepStmts(boolean value) {

        ConfigureConnectionPool.cachePrepStmts = value;

    }

    /**
     * Gets maximum pool size.
     *
     * @return the maximum pool size
     */
    public static int getMaximumPoolSize() {
        return maximumPoolSize;
    }
}
