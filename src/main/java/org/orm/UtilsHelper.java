package org.orm;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * The type Utils helper.
 */
public class UtilsHelper {


    private UtilsHelper() {
    }

    /**
     * Bytes to HEX string
     *
     * @param bytes array byte
     * @return String
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length); // Initialize with estimated capacity

        for (byte b : bytes) {
            // Convert byte to int, ensuring it's treated as unsigned for hex conversion
            String hex = String.format("%02X", b);
            hexString.append(hex);
        }

        return hexString.toString();
    }


    /**
     * Date to string for sqlite format date.
     *
     * @param date the date
     * @return the string
     */
    public static String dateToStringForSQLite(Date date) {
        // Use ISO 8601 format for consistency with SQLite's date/time functions
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * String to date
     *
     * @param str the str
     * @return the date
     */
    public static Date stringToDate(String str) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            return formatter.parse(str);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Serialize to byte  [ ].
     *
     * @param obj the obj
     * @return the byte [ ]
     */
    static byte[] serializeByte(final Object obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(obj);
            out.flush();
            return bos.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Deserialize byte object.
     *
     * @param bytes the bytes
     * @return the object
     */
    static Object deserializeByte(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try (ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Serialize json string.
     *
     * @param obj the obj
     * @return the string
     */
    static String serializeJson(final Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    /**
     * Deserialize json object.
     *
     * @param str    the str
     * @param aClass the class
     * @return the object
     */
    static Object deserializeJson(String str, Class<?> aClass) {
        Gson gson = new Gson();
        return gson.fromJson(str, aClass);
    }

    static Array getSqlArrayFromList(Connection connection, List<?> list) {
        try {
            return connection.createArrayOf("integer", list.toArray());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the modified date
     * @param date java.util.Date
     * @param value int
     * @param addingDate org.orm.AddingDate
     * @return  java.util.Date
     */
    public static @NotNull Date addingDate(Date date, int value, AddingDate addingDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        switch (addingDate) {
            case Days:
                cal.add(Calendar.DATE, value);
                break;
            case Month:
                cal.add(Calendar.MONTH, value);
                break;
            case Year:
                cal.add(Calendar.YEAR, value);
                break;
            case Week:
                cal.add(Calendar.DATE, value * 7);
                break;
            case Hour:
                cal.add(Calendar.HOUR, value);
                break;
            case Minute:
                cal.add(Calendar.MINUTE, value);
                break;
            case Second:
                cal.add(Calendar.SECOND, value);
                break;
            default:
                throw new RuntimeException("Not found");
        }

        return cal.getTime();
    }

    /**
     * Get date at end of day
     * @param date java.util.Date
     * @return java.util.Date
     */
    public static @NotNull Date atEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * Get date at start of day
     * @param date java.util.Date
     * @return java.util.Date
     */
    public static @NotNull Date atStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

}
