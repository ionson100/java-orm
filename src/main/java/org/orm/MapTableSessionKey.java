package org.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used when the orm works with multiple connections, it marks which connection the table belongs to.
 * If you make a mistake in the table memberships, in the context of databases, an error will occur
 * <pre>
 * {@code
 * // pre start orm
 *  Configure.IsWriteConsole=true;
 *  Configure.addConfigure("pg",TypeDataBase.POSTGRESQL,"localhost:5432/test","postgres","postgres")
 *  Configure.addConfigure("my",TypeDataBase.MYSQL,"localhost:3306/test","root","12345");
 *
 *  //start orm, connection default to sqlite
 *   new Configure(TypeDataBase.SQLITE,"test",null,null);
 *
 *     @MapTableName("tFromSqlie")
 *     class MyTable1{
 *
 *         @MapPrimaryKey
 *         public int id;
 *
 *         @MapColumn
 *         public String name;
 *     }
 *
 *     @MapTableName("tFromPostgres")
 *     @MapTableSessionKey("pg")
 *     class MyTable2{
 *
 *         @MapPrimaryKey
 *         public int id;
 *
 *         @MapColumn
 *         public String name;
 *     }
 *
 *     @MapTableName("tFromMySql")
 *     @MapTableTypeMySql("ENGINE=InnoDB DEFAULT CHARSET=utf8mb4")
 *     @MapTableSessionKey("my")
 *     class MyTable3{
 *
 *         @MapPrimaryKey
 *         public int id;
 *
 *         @MapColumn
 *         public String name;
 *     }
 *     initBases();
 *     ISession session = Configure.getSession();
 *     Table<MyTable1> table1 = session.query(MyTable1.class).toList(); //from sqlite table
 *
 *     ISession session2 = Configure.getSession("pg");
 *     Table<MyTable2> table1 = session2.query(MyTable2.class).toList(); //from postgres table
 *
 *     ISession session3 = Configure.getSession("my");
 *     Table<MyTable3> table1 = session3.query(MyTable3.class).toList(); //from mysql table
 * }
 * </pre>
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface MapTableSessionKey {
    /**
     * Marks the session key name
     * @return the name of the session (unique)
     */
    String value();
}
