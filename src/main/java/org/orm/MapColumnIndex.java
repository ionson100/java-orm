package org.orm;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A query to create an index will be created, it will be called after the query to create a database.
 *
 * <pre>
 * {@code
 * `@MapTableName("myTableSimple")
 * public class SimpleTable {
 *
 *     `@MapPrimaryKey
 *     public long id;
 *
 *     `MapColumnIndex
 *      public long myId;
 *
 *     `@MapColumnName("name")
 *     `@MapColumnType("TEXT UNIQUE")
 *     public String myName;
 * }
 * }
 * </pre>
 */

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface MapColumnIndex {
}


