package org.orm;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A field marked with this annotation will be serialized to a string as json format
 * <pre>
 * {@code
 * @MapTable
 * public class MyTable extends Persistent {
 *     @MapPrimaryKey
 *     public int id;
 *
 *     @MapColumn
 *     @MapColumnJson
 *     public MyUser user;
 * }
 * }
 * </pre>
 */
@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface MapColumnJson {
}


