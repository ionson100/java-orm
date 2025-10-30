package org.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For MySql tables, assigning a table type
 * <pre>
 * {@code
 * @MapTableName("t22")
 * @MapTableTypeMySql("ENGINE=InnoDB DEFAULT CHARSET=utf8mb4")
 * class MyTable {
 *  @MapPrimaryKey
 *  public UUID id = UUID.randomUUID();
 *
 *  @MapColumn
 *  @MapColumnIndex
 *  public String nane;
 *
 *  @MapColumn
 *  public int age
 *
 *  @MapColumnName("myDate")
 *  public LocalDateTime date = LocalDateTime.now();
 *  }
 *     }
 * </pre>
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface MapTableTypeMySql {

    /**
     * table type
     * @return table type
     */
    String value();
}
