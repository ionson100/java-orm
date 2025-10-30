ОРМ java 25
##### Что она может:
Из коробки работает с типами: int, Integer, double, Double, float,
Float, long, Long, short, Short, byte, Byte, BigDecimal, Date, LocalDateTime.\
Остальные типы конвертируются в byte[] и хранятся в базе как Blob поля. \
Массивы, списки словари, все сериализуется - как массив байтов по правилам интерфейса:
[Serializable](https://www.geeksforgeeks.org/java/serialization-and-deserialization-in-java/)
и
[Externalizable](https://www.geeksforgeeks.org/java/externalizable-interface-java/) \
Если пользователя не устраивает такое
хранение, можно задать свое хранение ```interface IAppenderWorker```\
Может работать с базами: Sqlite, MySql, Postgres, в работе может поддерживать одновременно много подключений к разным источникам.\
Все срединнее работают через пул, настройка пула через статические свойства ```ConfigureConnectionPool```\
Единицей работы, является ```ISession```, она держит одно соединение, фабрикой ISession является ```Configure```.
Сессия получается методом ```Configire.getSession``` или ```Configire.getSessionAuto```\
getSessionAuto получается на одну операцию, после действия сессия будет закрыта, а соединение будет отдано в пул.
При старте приложения фабрику (Configure) нужно инициализировать.
##### Быстрый старт
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
В целом орм имеет стандартный интерфейс построения запросов: единая функция
 или цепочка (Fluent), все примеры можно посмотреть в javaDoc или
в Source, он зашит в jar file.
Я остановлюсь на специфичных моментах:
#### Mapping annotations
```@MapTable``` задает название таблицы как название класса.

```@MapTableName("my_name")``` задает название таблицы как захотел пользователь.

```@MapTableReadOnly``` говорит, что объекты этого класса, вытащенные из базы, 
можно только смотреть, обновлять, удалять и вставлять - запрещено.

```@MapTableSessionKey("posgres1")``` соотносит этот класс к соединению, 
если вы работаете со множеством соединений, не дай бог одни объект из одной базы 
вы пытаетесь вставить в другую, пример покажу позже.

```@MapTableTypeMySql``` только если вы работаете с MySql, тут указывается тип таблицы 

```MapAppendCommandCreateTable``` атрибут поля класса, тут пользователь может написать свой скрипт, 
который будет исполняться, после создания таблицы. (ссылка поля на поле другой таблицы, сложные индексы)

example ```@MapTableTypeMySql("ENGINE=InnoDB DEFAULT CHARSET=utf8mb4")``` если вы его пропустите,
вставится по умолчанию: ```ENGINE=InnoDB```

```MapPrimaryKey``` помечает поле как первичный ключ, название поля в таблице будет как поле класса.

```MapPrimaryKeyName("_id")``` помечает поле как первичный ключ, название поля в таблице придумает пользователь.

```@MapColumn``` атрибут поля класса ассоциирует название колонки в таблице, название как у поля класса.

```@MapColumnName("my_name")``` атрибут поля класса ассоциирует название колонки в таблице, название придумает пользователь.

```@MapColumnIndex``` атрибут поля класса, при создании скрипта на создание таблице, орм попытается создать индекс на это поле.

```MapColumnJson``` атрибут поля класса, это поле будет сериализовано в json, и будет храниться в базе - как текст.

```@MapColumnType``` атрибут поля класса, тут пользовать может сам задать тип колонки в таблице и указать дефолтное значение.

```@MapColumnReadOnly``` атрибут поля класса, говорит что это поле не участвует в ставке и модификации, оно только для 
просмотра, как правило, такие поля заполняет сам база данных, типа временной метки или еще что.

```@MapColumnAppenderKey```  атрибут поля класса, что пользователь сам описал механизм хранения значения поля в базе данных.

##### Про первичные ключи
Он обязательный, он только один,
все поля numeric делаются инкрементными.\
Все поля строковые или UUID, пользователь заполняет сам, при создании объекта.\
При вставке, для numeric, возвращается значение которое присвоила база и эти значения заменяют значения которые получил объект при создании \
сам объект, после вставки, становится персистентным, с ним можно работать как с полученным из базы.\
Кроме ситуации когда вставка происходит через  ```session.insertBulk```

##### Про идентификацию объектов и class Persistent
Когда ваши объекты путешествуют по вашему коду, 
нужно иногда отследить, откуда они (вновь созданные или полученные из базы).\
От наследуйтесь от ```class Persistent``` вы всегда можете знать, откуда он ```boolean isPersistent()```\
мало того вы можете в этом случае использовать метод ```session.save(item)```, орм сама примет решение, вставлять или обновлять.

##### IEventOrm
Если вы реализуете это интерфейс в классе, то вы можете получать уведомления в объекте, какую стадию он проходит 
при вставке редактировании или удалении в базе, и если что-то пошло не так, можете кинуть исключение.\
Пример:
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
new Configure(TypeDataBase.SQLITE, "assHool.db",null,null);
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
#### Работа с множеством соединений

```java
public class BaseTable extends Persistent {
    @MapPrimaryKeyName("id")
    public UUID id=UUID.randomUUID();

    @MapColumnName("name")
    public String name;

    @MapColumnName("age")
    public int age;
}

//Эта таблица будет создана в базе данных Sqlite
// вызывать ее можно через сессию без параметров ключа
@MapTableName("my_table")
public class MyTableSqlite extends BaseTable {
}

//Эта таблица будет создана в базе данных MySql
// вызывать ее можно через сессию с ключем  "my"
@MapTableSessionKey("my")
@MapTableName("my_table")
public class MyTableMySql  extends BaseTable {
}

//Эта таблица будет создана в базе данных postgres
// вызывать ее можно через сессию с ключем "pg"
@MapTableSessionKey("pg")
@MapTableName("my_table")
public class MyTablePostgres extends BaseTable {
}
......
        
Configure.IsWriteConsole=true;//Выводить  в консоль все запросы к базе данных
Configure.addConfigure("my",TypeDataBase.MYSQL,"localhost:3306/test","root","12345");
// Я заряжаю в словарь соединение с базой данных mysql с ключом my,
// доставать сессии из него буду с этим ключом

Configure.addConfigure("pg",TypeDataBase.POSTGRESQL,"localhost:5432/test","postgres","postgres");
// Я заряжаю в словарь соединение с базой данных postgresql с ключом pg,
// доставать сессии из него буду с этим ключом

new Configure(TypeDataBase.SQLITE, "123.db",null,null);
// Я заряжаю в словарь соединение с базой данных sqlite оно дефолтное,
// у него ключ default, доставать сессии буду без указания ключа

// Заполняю таблицы данными в базе данных sqlite
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

// Заполняю таблицы данными в базе данных postgresql
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

// Заполняю таблицы данными в базе данных mysql
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
// Получаю список объектов из таблицы my_table в базе данных sqlite

List<MyTableMySql> myTableMySqlList = Configure.getSessionAuto("my").query(MyTableMySql.class).toList();
// Получаю список объектов из таблицы my_table в базе данных mysql

List<MyTablePostgres> myTablePostgres = Configure.getSessionAuto("pg").query(MyTablePostgres.class).toList();
// Получаю список объектов из таблицы my_table в базе данных postgresql
```
##### Сохранение данных в базе по своему сценарию.

На примере postgres, хранит данные в поле JSONB
```java
MapTableName("users")
public class MyTable extends Persistent {
    @MapPrimaryKey
    public int id;

    @MapColumn
    public String description;

    // Говорю, что это поле будет обрабатываться по моему сценарию, с ключем MyUser
    @MapColumnAppenderKey("MyUser")
    @MapColumn
    @MapColumnType("JSONB") // JSONB is a PostgreSQL type
    public MyUser myUser;
}

public class MyUser {
    public String name="one";
    public int age=1;
}
// Это класс для работы с базой данных, он реализует интерфейс IAppenderWorker
// и переопределяет методы toBase и fromBase
public class MyAppender implements IAppenderWorker {

    /**
     * Метод отрабатывает когда нужно вставить объект в базу данных
     *
     * @param o          Object field value
     * @param params     List of parameters for insertion into the database
     * @param connection jdbc connection
     * @return возвращает строку для вставки sql запроса
     */
    @Override
    public String toBase(Object o, List<Object> params, Connection connection) {
        MyUser myUser2 = (MyUser) o;
        PGobject jsonObject = new PGobject();// Создаем объект PGobject
        jsonObject.setType("json");
        try {
            jsonObject.setValue(new Gson().toJson(myUser2, MyUser.class));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        params.add(jsonObject);// Добавляем объект в список параметров
        return "?";// Возвращаем вопросик что встанет в sql запрос
    }

    /**
     * Метод отрабатывает когда нужно извлечь объект из ResultSet и передать его в поле myUser
     *
     * @param resultSet ResultSet
     * @param index     index of the field in the result set
     * @return возвращает объект MyUser из json строки, который получен из ResultSet, и должен встать в поле myUser
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


Configure.IsWriteConsole=true;//Выводить ли в консоль все запросы к базе данных
Configure.addAppender("MyUser", new MyAppender());//Добавляем кастомный appender с ключем "MyUser"
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
// Получить список всех возрастов у объекта MyUser



```
##### Асинхронные запросы.
В Fluent реализации у каждого конечного метода реализован асинхронный аналог.\
Это обертка ```CompletableFuture<?>```.
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
Что бы посмотреть как орм формирует запрос, вызовите метод ```toString()```
```java
 String SqlWhere=Configure.getSessionAuto().query(MyTable.class)
                .where("description IS NOT NULL")
                .orderByDesk("description")
                .toString();
```
##### Частичная типизированная выборка
```java
public class BaseUser extends Persistent {
    @MapPrimaryKey
    public UUID id=UUID.randomUUID();
}

@MapTableReadOnly // только смотреть
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
Configure.IsWriteConsole = true;//Выводить ли в консоль все запросы к базе данных
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
//Полная выборка
List<UserPartial> listUsersPartial = Configure.getSessionAuto().query(UserPartial.class).toList();
// Частичная выборка
List<?> emails = Configure.getSessionAuto().query(User.class).select("email");
//Выборка адресов

List<?> result = Configure.getSessionAuto().query(User.class).selectExpression("age+20");
//Выборка по выражению

// public class TempUser {
//     public UUID id;
//     public String email;
//}
//
String sqlSelect="select id, email from " + Configure.getSessionAuto().getTableName(User.class);
List<TempUser> tempUsers = Configure.getSessionAuto().query(TempUser.class).rawSqlSelect(sqlSelect).toList();
//Выбока по временному классу специально созданного для выборки
```
##### Helpers
В ```org.orm.UtilsHelper``` лежат некоторые помощники: работа с датами, методы сериализации