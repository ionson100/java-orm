package org.orm;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Used when you want to save the object fields in the database yourself, in your format
 * Annotation for MapAppender
 *
 * <pre>
 * {@code
 * Configure.IsWriteConsole = true;
 * Configure.addAppender("MyUser", new MyAppender());
 * new Configure(TypeDataBase.POSTGRESQL, "localhost:5432/test", "postgres", "postgres");
 *
 * @MapTable
 * public class MyTable  extends Persistent  {
 *     @MapPrimaryKey
 *     public int id;
 *     @MapColumnAppenderKey("MyUser")
 *     @MapColumn
 *     @MapColumnType("JSONB")
 *     public MyUser json;
 * }
 * }
 * </pre>
 */
@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface MapColumnAppenderKey {
    /**
     * Key value (unique)
     * @return the key value
     */
    String value();
}

