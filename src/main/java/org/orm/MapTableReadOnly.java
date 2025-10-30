package org.orm;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * A type marked with this interface can only retrieve data from a table.
 *
 * <pre>
 * {@code
 * @MapTable
 * @MapTableReadOnly
 * public class MyTable extends Persistent {
 *     @MapPrimaryKey
 *     public int id;
 *
 *     @MapColumn
 *     public Date dateCreate
 * }
 * }
 * </pre>
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface MapTableReadOnly {

}



