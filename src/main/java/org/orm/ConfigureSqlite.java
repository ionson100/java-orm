//package org.orm;
//
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * The type Configure sqlite.
// */
//public class ConfigureSqlite {
//    /**
//     * Instantiates a new Configure sqlite.
//     */
//    public ConfigureSqlite() {
//    }
//
//    private static int maximumPoolSize = 30;
//    private static int connectionTimeout = 300; // Тайм-аут ожидания соединения ConnectionTimeout(300); // Тайм-аут ожидания соединения
//    private static int IdleTimeout = 600; // Тайм-аут простоя
//    private static int maxLifetime = 1800000; // Максимальное время жизни соединения MaxLifetime(1800000); // Максимальное время жизни соединения   // Длина SQL-лимита
//
//    /**
//     * Gets connection timeout.
//     *
//     * @return the connection timeout
//     */
//    public static int getConnectionTimeout() {
//        if (connectionTimeout > 0) {
//            return connectionTimeout;
//        } else {
//            return 300;
//        }
//
//    }
//
//    /**
//     * Gets idle timeout.
//     *
//     * @return the idle timeout
//     */
//    public static int getIdleTimeout() {
//        if (IdleTimeout > 0) {
//            return IdleTimeout;
//        } else {
//            return 600;
//        }
//    }
//
//    /**
//     * Gets max lifetime.
//     *
//     * @return the max lifetime
//     */
//    public static int getMaxLifetime() {
//        if (maxLifetime > 0) {
//            return maxLifetime;
//        } else {
//            return 1800000;
//        }
//    }
//
//    /**
//     * Sets max lifetime.
//     *
//     * @param value the value
//     */
//    public static void setMaxLifetime(int value) {
//        if (value > 0) {
//            maxLifetime = value;
//        }
//    }
//
//    /**
//     * Sets connection timeout.
//     *
//     * @param value the value
//     */
//    public static void setConnectionTimeout(int value) {
//        if (value > 0) {
//            connectionTimeout = value;
//        }
//    }
//
//    /**
//     * Sets idle timeout.
//     *
//     * @param value the value
//     */
//    public static void setIdleTimeout(int value) {
//        if (value > 0) {
//            IdleTimeout = value;
//        }
//    }
//
//
//
//    /**
//     * Sets maximum pool size.
//     *
//     * @param value the value
//     */
//    public static void setMaximumPoolSize(int value) {
//        if (value > 0) {
//            maximumPoolSize = value;
//        }
//    }
//
//
//}







//package org.orm.apend;
//
//import java.sql.Array;
//import java.sql.SQLException;
//import java.util.Arrays;
//import java.util.List;
//
//public class AppenderArrayFactory {
//
//    public <T> AppenderValue getAppender(String typeName){
//        AppenderValue appenderValue = new AppenderValue();
//        appenderValue.fromBase = (resultSet, index) -> {
//
//            try {
//
//                Array sqlArray = resultSet.getArray(index);
//                if(sqlArray==null){
//                    return null;
//                }
//                T[] arrayObjects = (T[]) sqlArray.getArray();
//                return Arrays.stream(arrayObjects).toList();
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//
//        };
//        appenderValue.toBase = (o, params,connection) -> {
//            if (o == null) {
//                return "NULL";
//            }
//            try {
//                List<T> list=(List<T>)o;
//                params.add(connection.createArrayOf(typeName, list.toArray()));
//                return "?";
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//
//        };
//        return appenderValue;
//
//    }
//}
