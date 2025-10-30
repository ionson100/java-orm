package org.orm;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A field marked with this annotation will not participate in insert and update queries.
 *
 * <pre>
 * {@code
 * @MapTable
 * public class MyTable extends Persistent {
 *     @MapPrimaryKey
 *     public int id;
 *
 *     @MapColumn
 *     @MapColumnReadOnly
 *     public Date dateCreate
 * }
 * }
 * </pre>
 */
@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface MapColumnReadOnly {

}

