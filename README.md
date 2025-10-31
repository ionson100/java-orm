ORM Java 25
##### What it can do:
Out of the box, it works with the following types: int, Integer, double, Double, float,
Float, long, Long, short, Short, byte, Byte, BigDecimal, Date, LocalDateTime.
Other types are converted to byte[] and stored in the database as Blob fields. \
Arrays, lists, and dictionaries are all serialized as byte arrays according to the interface rules:
[Serializable](https://www.geeksforgeeks.org/java/serialization-and-deserialization-in-java/)
and
[Externalizable](https://www.geeksforgeeks.org/java/externalizable-interface-java/) \
If the user is not satisfied with this
storage, they can define their own ```interface IAppenderWorker```\
It can work with SQLite, MySql, and Postgres databases and can support multiple connections to different sources simultaneously. \
All applications work through a pool, which can be configured using the static properties ```ConfigureConnectionPool```\
The unit of work is the ```ISession```, which holds a single connection. The ISession factory is ```Configure```.
A session is obtained using the ```Configire.getSession``` or ```Configire.getSessionAuto``` method.
getSessionAuto is obtained for a single operation. After the operation, the session will be closed and the connection will be given to the pool.
When the application starts, the factory (Configure) must be initialized.
##### Quick Start
```java

@MapTableName("my_table")
public class MyTable extends Persistent {
    @MapPrimaryKeyName("id")
    public UUID id=UUID.randomUUID();

    @MapColumnName("name")
    public String name;

    @MapColumnName("age")
    public int age;
}
Configure.IsWriteConsole=true;
new Configure(TypeDataBase.SQLITE, "hole.db",null,null);
try (ISession session = Configure.getSession()) {
    session.dropTableIfExists(MyTable.class);
    session.createTableIfNotExists(MyTable.class);
    for (int i = 0; i < 10; i++) {
        MyTable myTable = new MyTable();
        myTable.age = i;
        myTable.name = "name" + i;
        session.save(myTable);
    }
   java.util.List<MyTable> list;
   list = session.query(MyTable.class).toList();
   for (MyTable myTable : list) {
       System.out.println(myTable.name+" "+myTable.age);
  }
}
```
In general, the ORM has a standard query building interface: a single function
or a chain (Fluent). All examples can be found in the javaDoc or
in the Source, which is embedded in the jar file.
I'll focus on the specific points:
#### Mapping annotations
```@MapTable``` specifies the table name as the class name.

```@MapTableName("my_name")``` specifies the table name as the user specifies.

```@MapTableReadOnly``` specifies that objects of this class retrieved from the database
can only be viewed, updated, deleted, and inserted—not permitted.

```@MapTableSessionKey("posgres1")``` associates this class with a connection.
If you're working with multiple connections, heaven forbid you try to insert an object from one database
into another. I'll show an example later.

```MapAppendCommandCreateTable``` is a class field attribute. Here, the user can write a custom script that will be executed after the table is created. (Referencing a field to a field in another table, complex indexes)

```@MapTableTypeMySql``` only if you are working with MySql; this specifies the table type.
example ```@MapTableTypeMySql("ENGINE=InnoDB DEFAULT CHARSET=utf8mb4")``` If you omit it,
the default value will be: ```ENGINE=InnoDB```

```MapPrimaryKey``` marks the field as the primary key; the field name in the table will be the class field.

```MapPrimaryKeyName("_id")``` marks the field as the primary key; the user will create a new field name in the table.

```@MapColumn``` class field attribute associates the column name in the table, the same name as the class field.

```@MapColumnName("my_name")``` class field attribute associates the column name in the table, a name the user can provide.

```@MapColumnIndex```  class field attribute. When creating a table creation script, the ORM will attempt to create an index on this field.

```MapColumnJson```  class field attribute. This field will be serialized as JSON and stored in the database as text.

```@MapColumnType```  class field attribute. Here, the user can specify the column type in the table and provide a default value.

```@MapColumnReadOnly``` class field attribute indicates that this field is not used for setting or modification; it is for viewing only. Typically, such fields are populated by the database itself, such as a timestamp or something similar.

```@MapColumnAppenderKey``` attribute of the class field that the user himself described the mechanism for storing the field value in the database.

##### About primary keys
It's mandatory, there's only one.
All numeric fields are incremental.
All fields are string or UUID; the user fills them in when creating an object.
When inserting a numeric field, the value assigned by the database is returned, and these values replace the values the object received when created.
After insertion, the object itself becomes persistent and can be accessed as if it were retrieved from the database.
Except when inserting via ```session.insertBulk```

##### About object identification and the Persistent class
When your objects travel throughout your code,
you sometimes need to track where they came from (newly created or retrieved from the database).
By inheriting from ```class Persistent```, you can always know where it came from using ```boolean isPersistent()```.
Furthermore, you can use the method in this case. ```session.save(item)```, the Orm will automatically decide whether to insert or update.

##### IEventOrm
If you implement this interface in a class, you can receive notifications in the object about what stage it is in
when inserting, editing, or deleting in the database, and if something goes wrong, you can throw an exception.

Example:
```java
@MapTableName("my_table")
public class MyTable extends Persistent implements IEventOrm {
    @MapPrimaryKeyName("id")
    public UUID id=UUID.randomUUID();

    @MapColumnName("name")
    public String name;

    @MapColumnName("age")
    public int age;
    
    @Override
    public void beforeUpdate() {
    }
    @Override
    public void afterUpdate() {
    }
    @Override
    public void beforeInsert() {
        System.out.println("beforeInsert"+this.name+" "+this.age);
    }
    @Override
    public void afterInsert() {
        System.out.println("afterInsert"+this.name+" "+this.age);
    }
    @Override
    public void beforeDelete() {
    }
    @Override
    public void afterDelete() {
    }
}
Configure.IsWriteConsole=true;
new Configure(TypeDataBase.SQLITE, "data.db",null,null);
try (ISession session = Configure.getSession()) {
    session.dropTableIfExists(MyTable.class);
    session.createTableIfNotExists(MyTable.class);
    for (int i = 0; i < 10; i++) {
         MyTable myTable = new MyTable();
         myTable.age = i;
         myTable.name = "name" + i;
         session.save(myTable);
    }
    java.util.List<MyTable> list;
    list = session.query(MyTable.class).toList();
    for (MyTable myTable : list) {
        System.out.println(myTable.name+" "+myTable.age);
    }
}
```
#### Working with Multiple Connections

```java
public class BaseTable extends Persistent {
@MapPrimaryKeyName("id")
public UUID id=UUID.randomUUID();

@MapColumnName("name")
public String name;

@MapColumnName("age")
public int age;
}

//This table will be created in a Sqlite database
// It can be accessed through a session without key parameters
@MapTableName("my_table")
public class MyTableSqlite extends BaseTable {
}

//This table will be created in a MySql database
// It can be accessed through a session with the key "my"
@MapTableSessionKey("my")
@MapTableName("my_table")
public class MyTableMySql extends BaseTable {
}

//This table will be created in a Postgres database
// It can be accessed through a session with the key "pg"
@MapTableSessionKey("pg")
@MapTableName("my_table")
public class MyTablePostgres extends BaseTable {
}
......

Configure.IsWriteConsole=true;//Write all database queries to the console
Configure.addConfigure("my",TypeDataBase.MYSQL,"localhost:3306/test","root","12345");
// I'm loading a connection to the MySQL database with the "my" key into the dictionary,
// I'll retrieve sessions from it with this key

Configure.addConfigure("pg",TypeDataBase.POSTGRESQL,"localhost:5432/test","postgres","postgres");
// I'm loading a connection to the PostgreSQL database with the "pg" key into the dictionary,
// I'll retrieve sessions from it with this key

new Configure(TypeDataBase.SQLITE, "123.db",null,null);
// I'm loading a default SQLite database connection into the dictionary.
// It has a default key, I'll retrieve sessions without specifying a key.

// I'm populating tables with data in the SQLite database.
try (ISession session = Configure.getSession()) {
session.dropTableIfExists(MyTableSqlite.class);
session.createTableIfNotExists(MyTableSqlite.class);
for (int i = 0; i < 10; i++) {
MyTableSqlite myTable = new MyTableSqlite();
myTable.age = i;
myTable.name = "name" + i;
session.save(myTable);
}
}

// Populate tables with data in the PostgreSQL database
try (ISession session = Configure.getSession("pg")) {
session.dropTableIfExists(MyTablePostgres.class);
session.createTableIfNotExists(MyTablePostgres.class);
for (int i = 0; i < 10; i++) {
MyTablePostgres myTable = new MyTablePostgres();
myTable.age = i;
myTable.name = "name" + i;
session.save(myTable);
}
}

// Populate tables with data in the MySQL database
try (ISession session = Configure.getSession("my")) {
session.dropTableIfExists(MyTableMySql.class);
session.createTableIfNotExists(MyTableMySql.class); 
for (int i = 0; i < 10; i++) { 
MyTableMySql myTable = new MyTableMySql(); 
myTable.age = i; 
myTable.name = "name" + i; 
session.save(myTable); 
}
}

List<MyTableSqlite> myTableSqliteList = Configure.getSessionAuto().query(MyTableSqlite.class).toList();
// Get a list of objects from the my_table table in the sqlite database

List<MyTableMySql> myTableMySqlList = Configure.getSessionAuto("my").query(MyTableMySql.class).toList();
// Get a list of objects from the my_table table in the MySQL database

List<MyTablePostgres> myTablePostgres = Configure.getSessionAuto("pg").query(MyTablePostgres.class).toList();
// Get a list of objects from the my_table table in the PostgreSQL database
```
##### Saving data to the database using your own scenario.

Using Postgres as an example, it stores data in a JSONB column.
```java
MapTableName("users")
public class MyTable extends Persistent {
@MapPrimaryKey
public int id;

@MapColumn
public String description;

// I'm saying that this field will be processed according to my scenario, with the key MyUser
@MapColumnAppenderKey("MyUser")
@MapColumn
@MapColumnType("JSONB") // JSONB is a PostgreSQL type
public MyUser myUser;
}

public class MyUser {
public String name="one";
public int age=1;
}
// This is a class for working with the database. It implements the IAppenderWorker interface
// and overrides the toBase and fromBase methods.
public class MyAppender implements IAppenderWorker {

/**
* This method is called when an object needs to be inserted into the database.
*
* @param o Object field value
* @param params List of parameters for insertion into the database
* @param connection jdbc connection
* @return returns a string for inserting the SQL query.
*/
@Override
public String toBase(Object o, List<Object> params, Connection connection) {
MyUser myUser2 = (MyUser) o;
PGobject jsonObject = new PGobject();// Create a PGobject object.
jsonObject.setType("json");
try {
jsonObject.setValue(new Gson().toJson(myUser2, MyUser.class));
} catch (SQLException e) {
throw new RuntimeException(e);
}
params.add(jsonObject);// Add an object to the parameter list
return "?";// Return a question mark that will be used in the SQL query
}

/**
* This method is triggered when an object needs to be extracted from the ResultSet and passed to the myUser field.
*
* @param resultSet ResultSet
* @param index index of the field in the result set
* @return returns the MyUser object from the JSON string obtained from the ResultSet and should be placed in the myUser field.
*/
@Override
public Object fromBase(ResultSet resultSet, int index) {

try {
String json = resultSet.getString(index);
MyUser myUser2 = new Gson().fromJson(json, MyUser.class);
return myUser2;
} catch (SQLException e) {
throw new RuntimeException(e);
}
}
}

Configure.IsWriteConsole=true;//Whether to output all database queries to the console
Configure.addAppender("MyUser", new MyAppender());//Add a custom appender with the "MyUser" key
new Configure(TypeDataBase.POSTGRESQL, "localhost:5432/test", "postgres", "postgres");

try (ISession session = Configure.getSession()) {
session.dropTableIfExists(MyTable.class);
session.createTable(MyTable.class);
List<MyTable> list=new ArrayList<>(10);
for (int i = 0; i < 10; i++) {
MyTable myTable = new MyTable();
myTable.description = "simple description:"+i;
myTable.myUser = new MyUser();
myTable.myUser.name = "name";
myTable.myUser.age = 12+i;
list.add(myTable);
}
session.insertBulk(list);
}
List<MyTable> myTables = Configure.getSessionAuto().query(MyTable.class).toList();

List<?> o=Configure.getSessionAuto().query(MyTable.class).selectExpression(" \"myUser\" -> 'age' ");
// Get a list of all ages for the MyUser object

```
##### Asynchronous queries.

In the Fluent implementation, each final method has an asynchronous counterpart.
This is a wrapper for ```CompletableFuture<?>```.

```java
Configure.getSessionAuto().query(MyTable.class)
     .where("description IS NOT NULL")
     .orderByDesk("description")
     .toListAsync().thenAccept(tables -> {
         for (MyTable myTable : tables) {
             System.out.println(myTable.description);
         }
     });
```
To see how the orm generates a request, call the ```toString()``` method.
```java
 String SqlWhere=Configure.getSessionAuto().query(MyTable.class)
                .where("description IS NOT NULL")
                .orderByDesk("description")
                .toString();
```
##### Partial Typed Selection

```java
public class BaseUser extends Persistent {
@MapPrimaryKey
public UUID id=UUID.randomUUID();
}

@MapTableReadOnly // read only
@MapTableName("users")
public class UserPartial extends BaseUser {
@MapColumn
public String name;

@MapColumn
public int age;
}

@MapTableName("users")
public class User extends UserPartial {
@MapColumn
public String email;
}
...
Configure.IsWriteConsole = true;//Whether to output all database queries to the console
new Configure(TypeDataBase.POSTGRESQL, "localhost:5432/test", "postgres", "postgres");

try (ISession session = Configure.getSession()) { 
session.dropTableIfExists(User.class); 
session.createTable(User.class); 

List<User> list = new ArrayList<>(10); 
for (int i = 0; i < 10; i++) { 
User myTable = new User(); 
myTable.name = "name" + i; 
myTable.age = 12 + i; 
myTable.email = "email" + i + "@mail.ru"; 
list.add(myTable); 
} 
session.insertBulk(list);
}

List<User> listUsers = Configure.getSessionAuto().query(User.class).toList();
//Full selection
List<UserPartial> listUsersPartial = Configure.getSessionAuto().query(UserPartial.class).toList();
// Partial selection
List<?> emails = Configure.getSessionAuto().query(User.class).select("email");
//Selecting addresses

List<?> result = Configure.getSessionAuto().query(User.class).selectExpression("age+20");
//Selecting by expression

// public class TempUser {
// public UUID id;
// public String email;
//}
//
String sqlSelect="select id, email from " + Configure.getSessionAuto().getTableName(User.class);
List<TempUser> tempUsers = Configure.getSessionAuto().query(TempUser.class).rawSqlSelect(sqlSelect).toList();
//Select by a temporary class specifically created for selection
```
##### Helpers
```org.orm.UtilsHelper``` contains some helpers: working with dates, serialization methods