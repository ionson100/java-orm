package org.orm;





import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Interface for working with the database
 */
public interface ISession extends AutoCloseable {

    /**
     * Use the ExecuteScalar method to retrieve a single value (for example, an aggregate value) from a database.Using parameters
     *
     * @param sql     SQL script in raw form, the ability to change values
     *                with the ? symbol, the observed values must be written to the object parameter, in the order in which they are written in the script.
     * @param parameters A collection of parameters that replace the `?` symbols in a script, the order of the parameters matches the order of the `?` symbols.
     * @return parameters of any type
     *
     * <pre>
     *{@code
     *
     * `@MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     `@MapPrimaryKey
     *     public long id;
     *     `@MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * session.insert(new SimpleTable("name1"));
     * var count =session.executeScalar("Select count(*) from 'SimpleTable' where name = ?","name1");
     *
     * }</pre>
     *
     */
    Object executeScalar(String sql, Object... parameters);

    /**
     * Executing a raw query to the database
     * @param sql     SQL script in raw form, the ability to change values
     *                with the ? symbol, the observed values must be written to the object parameter, in the order in which they are written in the script.
     * @param parameters A collection of parameters that replace the `?` symbols in a script, the order of the parameters matches the order of the `?` symbols.
     * <pre>
     * {@code
     * `@MapTable
     * ISession session=Configure.getSession();
     * session.execSQL("CREATE INDEX IF NOT EXISTS test_name ON 'test' ('name');");
     * }
     * </pre>
     */
    void execSQL(String sql, Object... parameters);

    /**
     * Open transaction
     * @throws SQLException if a database access error occurs
     */
    void beginTransaction() throws SQLException;

    /**
     * Close transaction
     * @throws SQLException if a database access error occurs
     */
    void commitTransaction() throws SQLException;

    /**
     * Rollback transaction
     * @throws SQLException if a database access error occurs
     */
    void rollbackTransaction() throws SQLException;

    /**
     * Getting the table name is usually needed to build a raw query.
     * @param <T>    The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @param aClass Instances of the  represent classes and interfaces in a running Java application.
     *               This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *               and a public field marked with the primary key annotation.
     * @return string as table name
     * <pre>
     * {@code
     * `@MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     `@MapPrimaryKey
     *     public long id;
     *     `@MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * Cursor cursor= execSQLRaw("select * from "+ session.getTableName(SimpleTable.class));
     * }
     * </pre>
     */
    <T> String getTableName(Class<T> aClass);

    /**
     * Checks if a table exists in the database.
     * @param <T>    The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @param aClass Instances of the  represent classes and interfaces in a running Java application.
     *               This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *               and a public field marked with the primary key annotation.
     * @return true - is existing, false - not exists
     * <pre>
     * {@code
     * `@MapTable
     *
     * ISession session=Configure.getSession();
     * try{
     *    if(session.tableExists(SimpleTable.class)==false){
     *       session.createTable(SimpleTable.class);
     *     }
     * }catch(Exception e){}
     * }
     * </pre>
     *
     */
    <T> boolean tableExists(Class<T> aClass);

    /**
     * Drops the table if it exists.
     * @param tableName table name
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * session.dropTableIfExists(session.getTableName(MyTable.class));
     * var res=session.tableExists(MyTable.class);
     * assertEquals(false,res);
     * }
     * </pre>
     */
    void dropTableIfExists(String tableName);

    /**
     * Drops the table if it exists.
     * @param aClass Instances of the  represent classes and interfaces in a running Java application.
     *               This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *               and a public field marked with the primary key annotation.
     * @param <T>    The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * session.dropTableIfExists(MyTable.class);
     * var res=session.tableExists(MyTable.class);
     * assertEquals(false,res);
     * }
     * </pre>
     */
    <T> void dropTableIfExists(Class<T> aClass);


    /**
     * Attempts to create a table; if unsuccessful, an exception is thrown.
     * @param aClass Instances of the  represent classes and interfaces in a running Java application.
     *               This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *               and a public field marked with the primary key annotation.
     * @param <T>    The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @throws Exception
     * <pre>
     * {@code
     * `@MapTable
     *
     * ISession session=Configure.getSession();
     * try{
     *    if(session.tableExists(SimpleTable.class)==false){
     *       session.createTable(SimpleTable.class);
     *     }
     * }catch(Exception e){}
     *
     * }
     * </pre>
     *
     * @exception Error creating table
     */
    <T> void  createTable(Class<T> aClass) throws Exception;

    /**
     *Creates a table if it does not exist.
     *
     * @param <T>    The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @param aClass Instances of the  represent classes and interfaces in a running Java application.
     *               This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *               and a public field marked with the primary key annotation.
     * @throws Exception Error creating table
     */
    <T> void createTableIfNotExists(Class<T> aClass) throws Exception;

    /**
     * Convenience method for inserting a row into the database.
     *
     * @param item An object, class type must be marked with annotation @{@link MapTable} or @{@link MapTableName}
     * @param <T>  The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * <pre>
     * {@code
     * `@MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     `@MapPrimaryKey
     *     public long id;
     *     `@MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * session.insert(new SimpleTable("name"));
     * var o=session.firstOrDefault(SimpleTable.class,null);
     * o.myName="newName";
     * var res=session.update(o);
     * }
     * </pre>
     */
    <T> void insert(T item);

    /**
     * Batch insert, please note that if objects contain incremental primary keys, these key fields are not updated after insertion.
     * @param tList The list of objects of class type must be marked with annotation @{@link MapTable} or @{@link MapTableName}
     * @param <T>   The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * <pre>
     * {@code
     * `@MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     `@MapPrimaryKey
     *     public long id;
     *     `@MapColumnName("name")
     *     public String myName;
     * }
     *  List<SimpleTable> list=new ArrayList<>();
     *  for (int i = 0; i < 400 ; i++) {
     *     list.add(new SimpleTable("name:"+i));
     *   }
     * ISession session=Configure.getSession();
     * session.insertBulk(list);
     * }
     * </pre>
     */
    <T> void insertBulk(List<T> tList);

    /**
     * Batch insert, please note that if objects contain incremental primary keys, these key fields are not updated after insertion.
     * @param object A collection of objects of class type must be marked with the annotation {@link MapTable} or {@link MapTableName}
     * @param <T>    The generic type must represent a class marked with the annotation {@link MapTable} or {@link MapTableName}
     * <pre>
     * {@code
     * `@MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     `@MapPrimaryKey
     *     public long id;
     *     `@MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * session.insertBulk(new SimpleTable("name:"+1),new SimpleTable("name:"+2));
     * }
     * </pre>
     */
    <T> void insertBulk( T... object);

    /**
     * Getting a list of rows from a database table based on a condition
     *
     * @param aClass  Instances of the  represent classes and interfaces in a running Java application.
     *                This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                and a public field marked with the primary key annotation.
     * @param where   A fragment of a SQL query script from a condition, where
     *                the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                and parameter values are entered into the object collection in the order they appear in the query.
     * @param parameters A collection of parameters that replace the `?` symbols in a script, the order of the parameters matches the order of the `?` symbols.
     * @param <T>     The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @return list parameters of type T
     * <pre>
     * {@code
     * `@MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     `@MapPrimaryKey
     *     public long id;
     *     `@MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * session.insert(new SimpleTable("name1"));
     * var list=session.getList(SimpleTable.class,"name = ?","name1");
     * }
     * </pre>
     */
    <T> List<T> getList(Class<T> aClass, String where, Object... parameters);

    /**
     * Convenience method for updating rows in the database.
     * The update occurs across all fields of the record, based on the match of the primary key.
     * @param item An object, class type must be marked with annotation @{@link MapTable} or @{@link MapTableName}
     * @param <T>  The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @return 1-success , 0 - not success
     * <pre>
     * {@code
     * `@MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     `@MapPrimaryKey
     *     public long id;
     *     `@MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * session.insert(new SimpleTable("name"));
     * var o=session.firstOrDefault(SimpleTable.class,null);
     * o.myName="newName";
     * var res=session.update(o);
     * }
     * </pre>
     */
    <T> int update(T item);

    /**
     *  Convenience method for updating rows in the database.
     *  The update occurs across all fields of the record, based on the match of the primary key and the additional condition.
     * @param item An object, class type must be marked with annotation @{@link MapTable} or @{@link MapTableName}
     * @param appendWhere Additional conditions
     * @param parameters Parameters of the additional condition
     * @param <T>  The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @return 1-success , 0 - not success
     * <pre>
     * {@code
     * `@MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *         this.last_update=new java.util.Date().getTime();
     *     }
     *     `@MapPrimaryKey
     *     public long id;
     *     `@MapColumnName("name")
     *     public String myName;
     *
     *     `@MapColumnName("last_update")
     *     public long lastUpdate;
     * }
     * ISession session=Configure.getSession();
     * session.insert(new SimpleTable("name"));
     * var o=session.firstOrDefault(SimpleTable.class,null);
     * o.myName="newName";
     * var res=session.update(o,"last_update = ?",o.last_update);
     * }
     * </pre>
     */
    <T> int update(T item, String appendWhere, Object... parameters);

    /**
     * Updating rows in a database based on a condition without loading rows on the client
     *
     * @param aClass       Instances of the  represent classes and interfaces in a running Java application.
     *                     This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                     and a public field marked with the primary key annotation.
     * @param columnValues parameters of type {@link PairColumnValue}
     * @param where        A fragment of a SQL query script from a condition, where
     *                     the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                     and parameter values are entered into the object collection in the order they appear in the query.
     * @param parameters      A collection of parameters that replace the `?` symbols in a script, the order of the parameters matches the order of the `?` symbols.
     * @param <T>          The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @return Number of affected records or 0
     * <pre>
     * {@code
     * @MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     @MapPrimaryKey
     *     public long id;
     *     @MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * session.insert(new SimpleTable("name"));
     * session.updateRows(SimpleTable.class,new PairColumnValue().put("myName","newName"),null);
     * }
     * </pre>
     */
    <T> int updateRows(Class<T> aClass,PairColumnValue columnValues, String where, Object... parameters);

    /**
     * Gets a single object based on a condition. If the condition is not met: the object is not found, or there is more than one, an exception is thrown.
     * @param aClass  Instances of the  represent classes and interfaces in a running Java application.
     *                This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                and a public field marked with the primary key annotation.
     * @param where   A fragment of a SQL query script from a condition, where
     *                the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                and parameter values are entered into the object collection in the order they appear in the query.
     * @param parameters A collection of parameters that replace the `?` symbols in a script, the order of the parameters matches the order of the `?` symbols.
     * @param <T>     The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @throws Exception <pre>
     * {@code
     * `@MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     `@MapPrimaryKey
     *     public long id;
     *     `@MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * try {
     *   SimpleTable  res=session.single(SimpleTable.class,"id=10");
     *   // or session.single(SimpleTable.class,"id=?",10);
     * } catch (Exception e) {}
     * }
     * </pre>
     * @return  parameters or Exception
     */
    <T> T single(Class<T> aClass, String where, Object... parameters) throws Exception;

    /**
     * Returns a single object; if there is none, or it is not the only one, null is returned.
     * @param aClass  Instances of the  represent classes and interfaces in a running Java application.
     *                This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                and a public field marked with the primary key annotation.
     * @param where   A fragment of a SQL query script from a condition, where
     *                the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                and parameter values are entered into the object collection in the order they appear in the query.
     * @param parameters A collection of parameters that replace the `?` symbols in a script, the order of the parameters matches the order of the `?` symbols.
     * @param <T>     The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * <pre>
     * {@code
     * `@MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     `@MapPrimaryKey
     *     public long id;
     *     `@MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     *   SimpleTable  res=session.singleOrDefault(SimpleTable.class,"id=10");
     *   // or session.singleOrDefault(SimpleTable.class,"id=?",10);
     * }
     * </pre>
     * @return The first object obtained by the condition, if the object does not exist, will return null
     */
    <T> T singleOrDefault(Class<T> aClass, String where, Object... parameters);

    /**
     * Convenience method for deleting rows in the database.
     *
     * @param item An object, class type must be marked with annotation @{@link MapTable} or @{@link MapTableName}
     * @param <T>  The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @return 1-success, 0 - not success
     * <pre>
     * {@code
     *
     * `@MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     `@MapPrimaryKey
     *     public long id;
     *     `@MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * session.insert(new SimpleTable("name"));
     * var o=session.firstOrDefault(SimpleTable.class,null);
     * var res = session.delete(o);
     *
     * }
     * </pre>
     */
    <T> int delete(T item);

    /**
     * Deletes rows from a table based on a condition
     * @param aClass  Instances of the  represent classes and interfaces in a running Java application.
     *                This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                and a public field marked with the primary key annotation.
     * @param <T>    The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @param where   A fragment of a SQL query script from a condition, where
     *                the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                and parameter values are entered into the object collection in the order they appear in the query.
     * @param parameters A collection of parameters that replace the `?` symbols in a script, the order of the parameters matches the order of the `?` symbols.
     * @return Number of affected records or 0
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * for (int i = 0; i < 10; i++) {
     *    session.insert(new SimpleTable("name:"+i);
     * }
     * var res=session.deleteRows(SimpleTable.class,"name not null"));
     * // delete rows only where name not null
     * assertEquals(10,res);
     * var list=session.getList(SimpleTable.class);
     * assertEquals(0,list.size());
     * }
     * </pre>
     *

     */
    <T>int deleteRows( Class<T> aClass, String where, Object... parameters);

    /**
     * Get total number of records in the table
     * @param aClass Instances of the  represent classes and interfaces in a running Java application.
     *               This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *               and a public field marked with the primary key annotation.
     * @param <T>    The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @return total number of records in the table
     * <pre>
     * {@code
     * }
     * </pre>
     *
     */
    <T,D> D count(Class<T> aClass);

    /**
     * Gets the number of rows in a table, based on a condition
     * @param <T>    The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @param aClass  Instances of the  represent classes and interfaces in a running Java application.
     *                This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                and a public field marked with the primary key annotation.
     * @param where   A fragment of a SQL query script from a condition, where
     *                the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                and parameter values are entered into the object collection in the order they appear in the query.
     * @param parameters A collection of parameters that replace the `?` symbols in a script, the order of the parameters matches the order of the `?` symbols. A collection of parameters that replace the `?` symbols in a script, the order of the parameters matches the order of the `?` symbols.
     * @return number of records in a table with selection condition
     * <pre>
     * {@code
     * }
     * </pre>
     *
     */
    <T,D> D count(Class<T> aClass, String where, Object... parameters);

    /**
     * Checks if rows exist in a table based on a condition
     * @param aClass   Instances of the  represent classes and interfaces in a running Java application.
     *                 This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                 and a public field marked with the primary key annotation.
     * @param <T>    The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @return true - there are records, false - no entries
     *
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * session.deleteRows(SimpleTable.class);
     * for (int i = 0; i < 10; i++) {
     *     session.insert(new SimpleTable("name:"+i));
     * }
     * var res=session.any(SimpleTable.class);
     * assertEquals(true,res);
     * var res2=session.any(SimpleTable.class);
     * assertEquals(false,res2);
     * }
     * </pre>
     */
    <T>boolean  any(Class<T> aClass);

    /**
     * Checks if rows exist in a table based on a condition
     *
     * @param <T>    The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @param aClass  Instances of the  represent classes and interfaces in a running Java application.
     *                This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                and a public field marked with the primary key annotation.
     * @param where   A fragment of a SQL query script from a condition, where
     *                the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                and parameter values are entered into the object collection in the order they appear in the query.
     * @param parameters A collection of parameters that replace the `?` symbols in a script, the order of the parameters matches the order of the `?` symbols.
     * @return true - there are records, false - no entries
     * <pre>
     * {@code
     * ISession session=Configure.getSession();
     * session.deleteRows(SimpleTable.class);
     * for (int i = 0; i < 10; i++) {
     *     session.insert(new SimpleTable("name:"+i));
     * }
     * var res=session.any(SimpleTable.class);
     * assertEquals(true,res);
     * var res2=session.any(SimpleTable.class,"name=?","simple");
     * assertEquals(false,res2);
     * }
     * </pre>
     *
     */
    <T> boolean any(Class<T> aClass, String where, Object... parameters);

    /**
     * Gets the first value based on a condition; if it doesn't exist, returns null.
     * @param <T>    The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @param aClass  Instances of the  represent classes and interfaces in a running Java application.
     *                This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                and a public field marked with the primary key annotation.
     * @param where   A fragment of a SQL query script from a condition, where
     *                the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                and parameter values are entered into the object collection in the order they appear in the query.
     * @param parameters A collection of parameters that replace the `?` symbols in a script, the order of the parameters matches the order of the `?` symbols.
     * @return object of type T or null
     * <pre>
     * {@code
     * `@MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     `@MapPrimaryKey
     *     public long id;
     *     `@MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * session.insert(new SimpleTable("name"));
     * var o=session.firstOrDefault(SimpleTable.class,"name not null");
     * o.myName="newName";
     * var res=session.update(o);
     * }
     * </pre>
     *
     */
    <T> T firstOrDefault(Class<T> aClass, String where, Object... parameters);

    /**
     * Get the first value based on the condition, if it does not exist, an exception is thrown
     * @param aClass  Instances of the  represent classes and interfaces in a running Java application.
     *                This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                and a public field marked with the primary key annotation.
     * @param where   A fragment of a SQL query script from a condition, where
     *                the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                and parameter values are entered into the object collection in the order they appear in the query.
     * @param parameters A collection of parameters that replace the `?` symbols in a script, the order of the parameters matches the order of the `?` symbols.
     * @param <T>     The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @return parameters of type T or {@link Exception}
     * @throws Exception <pre>
     * {@code
     * `@MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     `@MapPrimaryKey
     *     public long id;
     *     `@MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * try {
     *   SimpleTable  res=session.first(SimpleTable.class,"name=?","ion");
     * } catch (Exception e) {}
     * }
     * </pre>
     */
    <T> T first(Class<T> aClass, String where, Object... parameters) throws Exception;


    /**
     * Getting a list of values for one field of a database table
     * @param aClass     Instances of the  represent classes and interfaces in a running Java application.
     *                   This class
     * @param columnName The name of the field in the table by which the selection is made
     * @param where      A fragment of a SQL query script from a condition, where
     *                   the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                   and parameter values are entered into the object collection in the order they appear in the query.
     * @param parameters    A collection of parameters that replace the `?` symbols in a script, the order of the parameters matches the order of the `?` symbols.
     * @param <T>        The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @param <D>        any type
     * @return List parameters any types
     * <pre>
     * {@code
     * `@MapTable
     * public class SimpleTable {
     *     public SimpleTable(){}
     *     public SimpleTable(String name){
     *         this.myName = name;
     *     }
     *     `@MapPrimaryKey
     *     public long id;
     *     `@MapColumnName("name")
     *     public String myName;
     * }
     * ISession session=Configure.getSession();
     * List<String>  res=session.getListSelect(SimpleTable.class,"name","name not null");
     * }
     * </pre>
     */
    <T, D> List<D> getListSelect(Class<T> aClass,String columnName, String where, Object... parameters);

    /**
     *Obtaining a dictionary of parameters grouped by a database table column, with a selection condition
     * @param aClass   Instances of the  represent classes and interfaces in a running Java application.
     *                 This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                 and a public field marked with the primary key annotation.
     * @param columnName The column in the database table by which grouping occurs
     * @param where   A fragment of a SQL query script from a condition, where
     *                the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                and parameter values are entered into the object collection in the order they appear in the query.
     * @param parameters A collection of parameters that replace the `?` symbols in a script, the order of the parameters matches the order of the `?` symbols.
     * @param <T>     The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @return result map, key is the grouping value, value is the list of parameters in which this value occurs
     */
    <T> Map<Object,List<T>> groupBy(Class<T> aClass,String columnName, String where, Object... parameters);



    /**
     * Getting unique values for one table column
     * @param aClass   Instances of the  represent classes and interfaces in a running Java application.
     *                 This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                 and a public field marked with the primary key annotation.
     * @param columnName The column in a database table by which data is retrieved
     * @param where   A fragment of a SQL query script from a condition, where
     *                the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                and parameter values are entered into the object collection in the order they appear in the query.
     * @param parameters A collection of parameters that replace the `?` symbols in a script, the order of the parameters matches the order of the `?` symbols.
     * @param <T>     The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @return List of unique values
     */
    <T> List<Object> distinctBy(Class<T> aClass, String columnName, String where, Object... parameters);

    /**
     * Checks if the session is closed
     * @return false - session closed, true - session alive
     */
    boolean IsAlive();

    /**
     * This method inserts or updates objects that inherit the Persistent class, depending on the value of orm it decides whether to insert or update the object.
     * @param item An object, class type must be marked with annotation @{@link MapTable} or @{@link MapTableName}
     * @param <T>  The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @return 1-success , 0 - not success
     */
    <T> int save( T item);

    /**
     *  Get jdbc connection
     * @return jdbc connection to the database
     */
    Connection getConnection();

    /**
     * Get current session key
     * @return The session key
     */
    String getSessionKey();


    /**
     /** Entry point to Fluent Interface
     * @param aClass   Instances of the  represent classes and interfaces in a running Java application.
     *                 This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                 and a public field marked with the primary key annotation.
     * @param <T>  The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}
     * @return IQueryable
     */
    <T> IQueryable<T> query(Class<T> aClass);

    /**
     * Called on each iteration of the cursor, without creating a list
     * @param aClass   Instances of the  represent classes and interfaces in a running Java application.
     *                 This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                 and a public field marked with the primary key annotation.
     *
     * @param <T>  The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}.
     *            If you use the rawSqlSelect function, the class type can be arbitrary, without annotations.
     *
     * @param task Called on each cursor iteration
     * @param where      A fragment of a SQL query script from a condition, where
     *                   the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                   and parameter values are entered into the object collection in the order they appear in the query.
     * @param parameters    A array of parameters that replace the `?` symbols in a script, the order of the parameters matches the order of the `?` symbols.
     */
    <T> void iterator(Class<T> aClass, ITask<T> task, String where, Object... parameters);

    /**
     * Called on each iteration of the cursor, without creating a list
     * @param aClass   Instances of the  represent classes and interfaces in a running Java application.
     *                 This class must be annotated with the @{@link MapTable} or @{@link MapTableName} annotation, have a public parameterless constructor,
     *                 and a public field marked with the primary key annotation.
     *                  If you use the rawSqlSelect function, the class type can be arbitrary, without annotations.
     * @param <T>  The generic type must represent a class marked with the annotation @{@link MapTable} or @{@link MapTableName}.
     *            If you use the rawSqlSelect function, the class type can be arbitrary, without annotations.
     *
     * @param task Called on each cursor iteration
     * @param sql The full database query string
     * @param parameters    A array of parameters that replace the `?` symbols in a script, the order of the parameter
     */
    <T> void iteratorFree(Class<T> aClass, String sql, ITask<T> task, Object... parameters);



    /** Gets a typed list of parameters associated with a selection from a table.
     *  The type class must contain fields that match the names of the table columns. These fields must have the expected type.
     * @param aClass Any class of type, the class must have fields that match the table column names
     *               and the same type of fields that you expect to get from the database table.
     * @param sql Full SQL request, you can specify parameters
     * @param parameters A array of parameters that replace the `?` symbols in a script, the order of the parameters matches the order of the `?` symbols.
     * @param <T> Custom type, may not contain table annotations
     * @return List of parameters of type T
     */
    <T> List<T> getListFree( Class<T> aClass,String sql, Object... parameters);

    /**
     * Gets a typed list of parameters associated with a selection from a table.
     * Gets a typed list of parameters associated with a selection from a table.
     * The type class must contain fields that match the names of the table columns. These fields must have the expected type.
     * @param aClass Any class of type, the class must have fields that match the table column names
     *               and the same type of fields that you expect to get from the database table.
     * @param expression A fragment of a SQL query script from a condition, where`
     * @param where   A fragment of a SQL query script from a condition, where
     *                the condition, the word where, need not be written, string parameters are replaced with the parameter substitution character `?`,
     *                and parameter values are entered into the object collection in the order they appear in the query.
     * @param parameters A array of parameters that replace the `?` symbols in a script, the order of the parameters matches the order of the `?` symbols.
     * @param <T> Custom type, may not contain table annotations
     * @return List of parameters of type T
     */
    <T>List<Object> selectExpression(Class<T> aClass,String expression,String where, Object... parameters);

    /**
     * Retrieving an object from a database by primary key
     * @param aClass Any class of type, the class must have fields that match the table column names
     *               and the same type of fields that you expect to get from the database table.
     * @param primaryKey Primary key value
     * @param <T> Custom type, may not contain table annotations
     * @return object of type T or null
     */
    <T> T getByPrimaryKey(Class<T> aClass, Object primaryKey);
}
